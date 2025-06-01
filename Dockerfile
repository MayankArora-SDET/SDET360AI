FROM ubuntu:22.04 AS base
 
 
# Prevent interactive prompts during package installation
ENV DEBIAN_FRONTEND=noninteractive
 
# Install common dependencies
RUN apt-get update && apt-get install -y \
    curl \
    wget \
    git \
    build-essential \
    software-properties-common \
    apt-transport-https \
    ca-certificates \
    gnupg \
    tar \
    && rm -rf /var/lib/apt/lists/*
 
# Install OpenJDK 23
RUN ARCH=$(uname -m) && \
    if [ "$ARCH" = "x86_64" ]; then \
        JDK_URL="https://download.java.net/java/GA/jdk23/3c5b90190c68498b986a97f276efd28a/37/GPL/openjdk-23_linux-x64_bin.tar.gz"; \
    elif [ "$ARCH" = "aarch64" ]; then \
        JDK_URL="https://download.java.net/java/GA/jdk23/3c5b90190c68498b986a97f276efd28a/37/GPL/openjdk-23_linux-aarch64_bin.tar.gz"; \
    else \
        echo "Unsupported architecture: $ARCH" && exit 1; \
    fi && \
    wget $JDK_URL -O openjdk-23.tar.gz && \
    tar -xzf openjdk-23.tar.gz -C /opt/ && \
    rm openjdk-23.tar.gz && \
    ls -la /opt/
 
# Find and set JAVA_HOME dynamically
RUN JAVA_PATH=$(find /opt -name "jdk-*" -type d | head -1) && \
    echo "Found Java at: $JAVA_PATH" && \
    echo "export JAVA_HOME=$JAVA_PATH" >> /etc/environment && \
    echo "export PATH=\$JAVA_HOME/bin:\$PATH" >> /etc/environment
 
# Set environment variables for this build
ENV JAVA_HOME=/opt/jdk-23
ENV PATH="$JAVA_HOME/bin:$PATH"
 
# Verify Java installation and fix JAVA_HOME if needed
RUN if [ ! -d "$JAVA_HOME" ]; then \
        ACTUAL_JAVA_HOME=$(find /opt -name "jdk-*" -type d | head -1); \
        echo "Adjusting JAVA_HOME from $JAVA_HOME to $ACTUAL_JAVA_HOME"; \
        export JAVA_HOME=$ACTUAL_JAVA_HOME; \
        export PATH="$JAVA_HOME/bin:$PATH"; \
    fi && \
    echo "Final JAVA_HOME: $JAVA_HOME" && \
    ls -la $JAVA_HOME && \
    java -version && javac -version
 
# Install Python 3.11
RUN add-apt-repository ppa:deadsnakes/ppa -y && \
    apt-get update && \
    apt-get install -y python3.11 python3.11-dev python3.11-venv python3-pip && \
    rm -rf /var/lib/apt/lists/* && \
    update-alternatives --install /usr/bin/python3 python3 /usr/bin/python3.11 1 && \
    update-alternatives --install /usr/bin/python python /usr/bin/python3.11 1
 
# Install Node.js 20 and npm
RUN curl -fsSL https://deb.nodesource.com/setup_20.x | bash - && \
    apt-get update && \
    apt-get install -y nodejs && \
    rm -rf /var/lib/apt/lists/*
 
# Install PostgreSQL
RUN apt-get update && apt-get install -y postgresql postgresql-contrib && rm -rf /var/lib/apt/lists/*
 
# Install Protocol Buffers compiler
RUN apt-get update && apt-get install -y protobuf-compiler && rm -rf /var/lib/apt/lists/*
 
# Install Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*
 
# Verify Maven can find Java
RUN export JAVA_HOME=$(find /opt -name "jdk-*" -type d | head -1) && \
    export PATH="$JAVA_HOME/bin:$PATH" && \
    echo "Maven using JAVA_HOME: $JAVA_HOME" && \
    mvn -version
 
# Set up working directory
WORKDIR /app
 
RUN curl -fsSL https://ollama.com/install.sh | sh
# RUN ollama install llama3 70b
 
# Copy dependency files first to leverage Docker cache
COPY SDETAIO/package*.json /app/SDETAIO/
COPY springboot-service/pom.xml /app/springboot-service/
COPY ai-service/requirements.txt /app/ai-service/
 
# Install frontend dependencies
WORKDIR /app/SDETAIO
RUN npm install
 
# Install backend dependencies
WORKDIR /app/springboot-service
RUN export JAVA_HOME=$(find /opt -name "jdk-*" -type d | head -1) && \
    export PATH="$JAVA_HOME/bin:$PATH" && \
    mvn dependency:go-offline -B
 
# Install Python dependencies
WORKDIR /app/ai-service
RUN python -m pip install --upgrade pip && \
    python -m pip install -r requirements.txt
 
# Copy the rest of the application code
WORKDIR /app
COPY . .
 
# Set up PostgreSQL
USER postgres
RUN /etc/init.d/postgresql start && \
    psql --command "CREATE DATABASE mastersdet360;" && \
    psql --command "ALTER USER postgres WITH PASSWORD '12345';" && \
    psql --command "GRANT ALL PRIVILEGES ON DATABASE mastersdet360 TO postgres;" && \
    psql --command "ALTER USER postgres WITH SUPERUSER;"
USER root
 
# Compile Protocol Buffers
RUN chmod +x /app/compile-proto.sh && /app/compile-proto.sh
 
# Build Spring Boot service
WORKDIR /app/springboot-service
RUN export JAVA_HOME=$(find /opt -name "jdk-*" -type d | head -1) && \
    export PATH="$JAVA_HOME/bin:$PATH" && \
    mvn clean package -DskipTests
 
# Build Angular frontend
WORKDIR /app/SDETAIO
RUN npm run build
 
# Expose ports
EXPOSE 4201 8080 8001 50051 5433
 
# Create startup script
WORKDIR /app
RUN echo '#!/bin/bash\n\
service postgresql start\n\
cd /app/ai-service && python main.py &\n\
cd /app/springboot-service && java -jar target/sdet360-0.0.1-SNAPSHOT.jar &\n\
cd /app/SDETAIO && npx serve -s dist/ai/browser -l 4201\n\
tail -f /dev/null' > /app/start.sh && \
chmod +x /app/start.sh
 
# Set entry point
ENTRYPOINT ["/app/start.sh"]