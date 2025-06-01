#!/bin/bash
set -e

# Start PostgreSQL service
echo "Starting PostgreSQL..."
service postgresql start

# Wait for PostgreSQL to be ready
until pg_isready -U postgres; do
  echo "Waiting for PostgreSQL to start..."
  sleep 2
done

# Initialize database if it doesn't exist
if ! psql -U postgres -lqt | cut -d \| -f 1 | grep -qw mastersdet360; then
    echo "Initializing database..."
    psql -U postgres -c "CREATE DATABASE mastersdet360;"
    psql -U postgres -c "CREATE USER postgres WITH PASSWORD '12345' SUPERUSER;"
    psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE mastersdet360 TO postgres;"
    echo "Database initialized successfully"
else
    echo "Database already exists, skipping initialization"
fi

# Start AI service in background
echo "Starting AI service..."
cd /app/ai-service
python3.11 main.py &

# Start Spring Boot application
echo "Starting Spring Boot application..."
cd /app
java -jar /app/springboot-service/target/sdet360-0.0.1-SNAPSHOT.jar

# Keep container running if anything fails
tail -f /dev/null
