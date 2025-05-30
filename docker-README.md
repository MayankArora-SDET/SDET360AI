# SDET360 Docker Setup

This document provides instructions for setting up and running the SDET360 application using Docker.

## Prerequisites

- Docker Desktop installed and running
- Java 23 (for local development)
- Protoc 30.2 (for local development)
- PowerShell (for Windows)

## Project Structure

The SDET360 application consists of:

1. **Spring Boot Backend** (`springboot-service`): Java backend service
2. **AI Service** (`ai-service`): Python FastAPI service with gRPC
3. **Angular Frontend** (`SDETAIO`): Web interface
4. **Proto Files** (`proto`): Protocol buffer definitions for gRPC communication
5. **PostgreSQL Database**: Persistent storage

## Docker Setup

The Docker configuration includes:

- **docker-compose.yml**: Defines all services and their relationships
- **Dockerfiles**: Individual container configurations for each service
- **docker-setup.ps1**: PowerShell script to simplify the setup process

## Running with Docker

1. Open PowerShell and navigate to the project root directory:
   ```
   cd c:\Users\neha.pal\Desktop\12-05-2025\sdet360
   ```

2. Run the setup script:
   ```
   .\docker-setup.ps1
   ```

3. Access the services:
   - PostgreSQL: `localhost:5432`
   - Spring Boot API: `http://localhost:8080`
   - AI Service API: `http://localhost:8000`
   - Angular Frontend: `http://localhost:4200`

## Manual Docker Commands

If you prefer to run commands manually:

```powershell
# Build and start all services
docker-compose up --build -d

# View logs for a specific service
docker-compose logs -f springboot-service

# Stop all services
docker-compose down

# Restart all services
docker-compose restart
```

## Database Configuration

The PostgreSQL database is configured with:
- Database name: `mastersdet360`
- Username: `postgres`
- Password: `12345`

## Troubleshooting

1. **Proto compilation issues**: The proto-compiler service handles compilation of proto files. Check its logs:
   ```
   docker-compose logs proto-compiler
   ```

2. **Service connectivity**: Ensure all services are running:
   ```
   docker-compose ps
   ```

3. **Database issues**: Check PostgreSQL logs:
   ```
   docker-compose logs postgres
   ```

4. **Container rebuild**: If you need to rebuild a specific service:
   ```
   docker-compose build <service-name>
   docker-compose up -d <service-name>
   ```
