# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy only the POM file first to leverage Docker cache
COPY springboot-service/pom.xml .

# Download dependencies in a separate layer
RUN mvn dependency:go-offline -B

# Copy source code (including pre-generated protobuf classes)
COPY springboot-service/src ./src

# Build the application with parallel builds and skip tests
RUN mvn clean package -DskipTests -T 1C

# Runtime stage
FROM eclipse-temurin:23-jre

WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=docker
ENV JAVA_OPTS="-Xms512m -Xmx2g -XX:MaxRAMPercentage=75.0"

# Expose the application port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=30s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
