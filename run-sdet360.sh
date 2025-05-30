#!/usr/bin/env bash
set -e

# SDET360AI Application Runner Script
# This script sets up and runs the SDET360AI application

# Display banner
echo "====================================================="
echo "           SDET360AI Application Runner              "
echo "====================================================="

# Set working directory to the script location
cd "$(dirname "$0")"

# Check for Docker and Docker Compose
if ! command -v docker &> /dev/null; then
    echo "Error: Docker is not installed. Please install Docker first."
    exit 1
fi

if ! docker compose version &> /dev/null; then
    echo "Error: Docker Compose is not installed. Please install Docker Compose first."
    exit 1
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

# Function to build and start the application
start_application() {
    echo "Building and starting SDET360AI application..."
    docker compose build
    if [ $? -ne 0 ]; then
        echo "Error: Docker Compose build failed."
        exit 1
    fi
    
    docker compose up -d
    if [ $? -ne 0 ]; then
        echo "Error: Docker Compose up failed."
        exit 1
    fi
    
    echo "SDET360AI application started successfully!"
    echo "Services:"
    echo "- Frontend: http://localhost:4201"
    echo "- Spring Boot API: http://localhost:8081"
    echo "- AI Service API: http://localhost:8001"
    echo "- PostgreSQL: localhost:5433"
}

# Function to stop the application
stop_application() {
    echo "Stopping SDET360AI application..."
    docker compose down
    echo "SDET360AI application stopped."
}

# Function to show logs
show_logs() {
    echo "Showing logs for SDET360AI application..."
    docker compose logs -f
}

# Function to show help
show_help() {
    echo "Usage: ./run-sdet360.sh [COMMAND]"
    echo ""
    echo "Commands:"
    echo "  start       Compile proto files and start the application"
    echo "  stop        Stop the application"
    echo "  restart     Restart the application"
    echo "  logs        Show application logs"
    echo "  proto       Compile protocol buffers only"
    echo "  help        Show this help message"
    echo ""
    echo "If no command is provided, the application will be started."
}

# Parse command line arguments
case "$1" in
    start)
        compile_proto
        start_application
        ;;
    stop)
        stop_application
        ;;
    restart)
        stop_application
        compile_proto
        start_application
        ;;
    logs)
        show_logs
        ;;
    proto)
        compile_proto
        ;;
    help)
        show_help
        ;;
    *)
        # Default action if no arguments provided
        show_help
        compile_proto
        start_application
        ;;
esac

exit 0
