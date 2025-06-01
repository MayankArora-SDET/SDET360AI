#!/usr/bin/env bash
set -e

# SDET360AI Production Test Script
# This script builds and runs the SDET360AI application in a production-like environment

# Display banner
echo "====================================================="
echo "      SDET360AI Production Environment Test          "
echo "====================================================="

# Set working directory to the script location
cd "$(dirname "$0")"
# Function to fix line endings
fix_line_endings() {
    echo "Fixing line endings for shell scripts..."
    find . -name "*.sh" -type f -exec sed -i 's/\r$//' {} \; 2>/dev/null || true
    echo "Line endings fixed."
}

# Fix line endings first thing
fix_line_endings

# Check for required tools and install if missing
echo "Checking required tools..."

# Check if Homebrew is installed
# if ! command -v brew &> /dev/null; then
#     echo "Homebrew is not installed. Installing Homebrew..."
#     /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
#     if [ $? -ne 0 ]; then
#         echo "Error: Failed to install Homebrew. Please install it manually."
#         exit 1
#     fi
# fi

# Check and install required tools
for cmd in docker java node npm python3; do
    if ! command -v $cmd &> /dev/null; then
        echo "$cmd is not installed. Installing via Homebrew..."
        case $cmd in
            docker)
                brew install --cask docker
                ;;
            java)
                brew install openjdk@23
                sudo ln -sfn $(brew --prefix)/opt/openjdk@23/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-23.jdk
                ;;
            node|npm)
                brew install node
                ;;
            python3)
                brew install python@3.11
                ;;
        esac
        if [ $? -ne 0 ]; then
            echo "Error: Failed to install $cmd. Please install it manually."
            exit 1
        fi
    fi
done

# Check for Maven - use Maven wrapper instead of requiring mvn
if ! command -v mvn &> /dev/null; then
    echo "Maven not found, but we'll use the Maven wrapper instead."
fi



# Function to compile proto files
compile_proto() {
    echo "Compiling Protocol Buffers..."
    chmod +x ./compile-proto.sh
    ./compile-proto.sh
    if [ $? -ne 0 ]; then   
        echo "Error: Failed to compile protocol buffers."
        exit 1
    fi
    echo "Protocol Buffers compiled successfully."
}

# Function to build Spring Boot service
build_spring_boot() {
    echo "Building Spring Boot service..."
    cd springboot-service
    ./mvnw clean package -DskipTests
    if [ $? -ne 0 ]; then
        echo "Error: Failed to build Spring Boot service."
        exit 1
    fi
    cd ..
    echo "Spring Boot service built successfully."
}

# Function to build Angular frontend
build_angular() {
    echo "Building Angular frontend..."
    cd SDETAIO
    npm install
    npm run build
    if [ $? -ne 0 ]; then
        echo "Error: Failed to build Angular frontend."
        exit 1
    fi
    cd ..
    echo "Angular frontend built successfully."
}

# Function to set up Python environment
setup_python() {
    echo "Setting up Python environment..."
    cd ai-service
    python3 -m pip install --upgrade pip
    if [ ! -f requirements.txt ]; then
        echo "Creating requirements.txt..."
        echo "fastapi>=0.100.0" > requirements.txt
        echo "uvicorn>=0.23.0" >> requirements.txt
        echo "grpcio>=1.58.0" >> requirements.txt
        echo "grpcio-tools>=1.58.0" >> requirements.txt
        echo "protobuf>=4.29.3" >> requirements.txt
    fi
    python3 -m pip install -r requirements.txt
    if [ $? -ne 0 ]; then
        echo "Error: Failed to install Python dependencies."
        exit 1
    fi
    cd ..
    echo "Python environment set up successfully."
}

# Function to start PostgreSQL
start_postgres() {
    echo "Starting PostgreSQL..."
    if docker ps -a | grep -q sdet360-postgres; then
        docker start sdet360-postgres
    else
        docker run --name sdet360-postgres -e POSTGRES_DB=mastersdet360 -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=12345 -p 5433:5432 -d postgres:16-alpine
    fi
    
    # Wait for PostgreSQL to start
    echo "Waiting for PostgreSQL to start..."
    sleep 5
    
    # Check if PostgreSQL is running
    if ! docker ps | grep -q sdet360-postgres; then
        echo "Error: Failed to start PostgreSQL."
        exit 1
    fi
    echo "PostgreSQL started successfully."
}

# Function to start services
start_services() {
    echo "Starting services..."
    
    # Start AI service
    echo "Starting AI service..."
    cd ai-service
    python3 main.py &
    AI_PID=$!
    cd ..
    
    # Start Spring Boot service
    echo "Starting Spring Boot service..."
    cd springboot-service
    # Use Java 23 if available
    if [ -d "/Library/Java/JavaVirtualMachines/openjdk-23.jdk" ]; then
        JAVA_HOME="/Library/Java/JavaVirtualMachines/openjdk-23.jdk/Contents/Home"
        "$JAVA_HOME/bin/java" -jar target/sdet360-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod --spring.datasource.url=jdbc:postgresql://localhost:5433/mastersdet360 --spring.datasource.username=postgres --spring.datasource.password=12345 --fastapi.grpc.host=localhost --fastapi.grpc.port=50051 &
    else
        java -jar target/sdet360-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod --spring.datasource.url=jdbc:postgresql://localhost:5433/mastersdet360 --spring.datasource.username=postgres --spring.datasource.password=12345 --fastapi.grpc.host=localhost --fastapi.grpc.port=50051 &
    fi
    SPRING_PID=$!
    cd ..
    
    # Start Angular frontend
    echo "Starting Angular frontend..."
    cd SDETAIO
    if ! command -v serve &> /dev/null; then
        npm install -g serve
    fi
    serve -s dist/ai/browser -l 4201 &
    ANGULAR_PID=$!
    cd ..
    
    # Save PIDs to file for later cleanup
    echo "$AI_PID $SPRING_PID $ANGULAR_PID" > .prod_test_pids
    
    echo "All services started successfully!"
    echo "Frontend: http://localhost:4201"
    echo "Spring Boot API: http://localhost:8081"
    echo "AI Service API: http://localhost:8001"
}

# Function to stop services
stop_services() {
    echo "Stopping services..."
    
    # Kill processes if PID file exists
    if [ -f .prod_test_pids ]; then
        read -r AI_PID SPRING_PID ANGULAR_PID < .prod_test_pids
        kill $AI_PID $SPRING_PID $ANGULAR_PID 2>/dev/null || true
        rm .prod_test_pids
    fi
    
    # Stop PostgreSQL
    docker stop sdet360-postgres || true
    
    echo "All services stopped."
}

# Function to build and run Docker image
build_and_run_docker() {
    echo "Building Docker image..."
    docker build -t sdet360ai .
    if [ $? -ne 0 ]; then
        echo "Error: Failed to build Docker image."
        exit 1
    fi
    
    echo "Running Docker container..."
    docker run -d --name sdet360ai -p 4201:4201 -p 8081:8080 -p 8001:8001 -p 50051:50051 -p 5433:5432 sdet360ai:test
    
    echo "Docker container started!"
    echo "Frontend: http://localhost:4201"
    echo "Spring Boot API: http://localhost:8081"
    echo "AI Service API: http://localhost:8001"
}

# Function to stop Docker container
stop_docker() {
    echo "Stopping Docker container..."
    docker stop sdet360ai-test || true
    docker rm sdet360ai-test || true
    echo "Docker container stopped and removed."
}

# Function to show help
show_help() {
    echo "Usage: ./run-prod-test.sh [COMMAND]"
    echo ""
    echo "Commands:"
    echo "  start       Build and start all services locally"
    echo "  stop        Stop all services"
    echo "  docker      Build and run using Docker"
    echo "  stop-docker Stop and remove Docker container"
    echo "  help        Show this help message"
    echo ""
    echo "If no command is provided, the help message will be displayed."
}

# Handle cleanup on script exit
trap stop_services EXIT

# Parse command line arguments
case "$1" in
    start)
        compile_proto
        build_spring_boot
        build_angular
        setup_python
        start_postgres
        start_services
        # Disable trap to keep services running
        trap - EXIT
        ;;
    stop)
        stop_services
        ;;
    docker)
        build_and_run_docker
        # Disable trap as it's not needed for Docker
        trap - EXIT
        ;;
    stop-docker)
        stop_docker
        ;;
    help|*)
        show_help
        ;;
esac

exit 0