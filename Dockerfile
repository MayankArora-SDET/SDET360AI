# Build stage
FROM ubuntu:22.04 AS builder

# Prevent interactive prompts
ENV DEBIAN_FRONTEND=noninteractive

# Install build dependencies
RUN apt-get update && apt-get install -y \
    curl wget git build-essential software-properties-common \
    apt-transport-https ca-certificates gnupg tar \
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
    rm openjdk-23.tar.gz

# Set up Java environment
ENV JAVA_HOME=/opt/jdk-23
ENV PATH="$JAVA_HOME/bin:$PATH"

# Install Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Build Spring Boot application
WORKDIR /app
COPY springboot-service/pom.xml /app/springboot-service/
WORKDIR /app/springboot-service
RUN mvn dependency:go-offline -B

# Copy and build the application
COPY . /app/
RUN mvn clean package -DskipTests

# Runtime stage
FROM ubuntu:22.04

# Install runtime dependencies
RUN apt-get update && apt-get install -y \
    python3.11 python3-pip postgresql postgresql-contrib \
    && rm -rf /var/lib/apt/lists/*

# Copy Java runtime from builder
COPY --from=builder /opt/jdk-23 /opt/jdk-23
ENV JAVA_HOME=/opt/jdk-23
ENV PATH="$JAVA_HOME/bin:$PATH"

# Set up working directory
WORKDIR /app

# Copy built artifacts
COPY --from=builder /app/springboot-service/target/sdet360-0.0.1-SNAPSHOT.jar /app/

# Install Python dependencies
COPY ai-service/requirements.txt /app/ai-service/requirements.txt
RUN python3.11 -m pip install --no-cache-dir -r /app/ai-service/requirements.txt

# Copy application code and scripts
COPY . /app/

# Make scripts executable
RUN chmod +x /app/start-services.sh && \
    chmod +x /app/compile-proto.sh && \
    /app/compile-proto.sh

# Expose ports
EXPOSE 8080 8001 50051 5433

# Health check
HEALTHCHECK --interval=30s --timeout=30s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Start services
CMD ["/app/start-services.sh"]