# SDET360 Project Setup Guide for Windows

This comprehensive guide will walk you through setting up and running the SDET360 project on Windows, from cloning the repository to running all components.

## Table of Contents
1. [Clone the Repository](#clone-the-repository)
2. [gRPC Setup](#grpc-setup)
3. [AI Service Setup](#ai-service-setup)
4. [Spring Boot Service Setup](#spring-boot-service-setup)
5. [Angular Frontend Setup](#angular-frontend-setup)
6. [Database Setup](#database-setup)
7. [Troubleshooting](#troubleshooting)

## Clone the Repository

1. Install Git for Windows if not already installed:
   - Download from [git-scm.com](https://git-scm.com/download/win)
   - Follow the installation instructions

2. Clone the SDET360 repository:
   ```powershell
   git clone https://github.com/your-org/sdet360.git
   cd sdet360
   ```

## gRPC Setup

### Prerequisites

1. **PowerShell 7+**
   - Install from [github.com/PowerShell/PowerShell](https://github.com/PowerShell/PowerShell/releases)

2. **Protocol Buffer Compiler (`protoc`)**
   - Download pre-built binaries from [github.com/protocolbuffers/protobuf/releases](https://github.com/protocolbuffers/protobuf/releases)
   - Add `protoc.exe` to your system `PATH`

3. **PowerShell Execution Policy**
   ```powershell
   Set-ExecutionPolicy -Scope CurrentUser -ExecutionPolicy RemoteSigned
   ```

### Compile Protocol Buffers

1. Open PowerShell in the project root directory
2. Run the following command:
   ```powershell
   powershell -ExecutionPolicy Bypass -File .\compile-proto.ps1
   ```
3. This will generate:
   - Java stubs under `springboot-service\src\main\java`
   - Python stubs under `ai-service\app\generated`

> **Note:** Re-run this script after any changes to `proto/ai_service.proto`

## AI Service Setup

### Prerequisites

1. **Python 3.10+**
   - Download from [python.org/downloads/windows](https://www.python.org/downloads/windows/)
   - Ensure "Add Python to PATH" is checked during installation

### Setup Steps

1. Navigate to the AI service directory:
   ```powershell
   cd ai-service
   ```

2. Create and activate a virtual environment:
   ```powershell
   python -m venv venv
   .\venv\Scripts\activate
   ```

3. Install the requirements:
   ```powershell
   pip install -r requirements.txt
   ```

4. Run the AI service:
   ```powershell
   python -m app.main
   ```

## Spring Boot Service Setup

### Prerequisites

1. **Java 23**
   - Download from [oracle.com/java/technologies/downloads](https://www.oracle.com/java/technologies/downloads/)
   - Current version: Java 23 2024-09-17
     - Java(TM) SE Runtime Environment (build 23+37-2369)
     - Java HotSpot(TM) 64-Bit Server VM (build 23+37-2369, mixed mode, sharing)

2. **IntelliJ IDEA**
   - Download from [jetbrains.com/idea/download](https://www.jetbrains.com/idea/download/)
   - Community Edition is sufficient, but Ultimate provides better Spring Boot support

### Setup Steps

1. Open IntelliJ IDEA
2. Select "Open" and navigate to the `springboot-service` directory
3. Wait for the project to load and dependencies to be downloaded
4. Configure Java 23 in IntelliJ:
   - Go to File > Project Structure > Project
   - Set Project SDK to Java 23
   - Set Project language level to "23 - Records, patterns, etc."

5. Install Lombok Plugin (to resolve Lombok errors):
   - Go to File > Settings > Plugins
   - Search for "Lombok"
   - Install the plugin and restart IntelliJ
   - Enable annotation processing:
     - Go to File > Settings > Build, Execution, Deployment > Compiler > Annotation Processors
     - Check "Enable annotation processing"

6. Run the Spring Boot application:
   - Locate the main class `com.sdet.sdet360.Sdet360Application`
   - Right-click and select "Run"

## Angular Frontend Setup

### Prerequisites

1. **Node.js and npm**
   - Download from [nodejs.org](https://nodejs.org/)
   - Recommended version: Node.js 18.x or later

2. **Angular CLI**
   - Install globally:
     ```powershell
     npm install -g @angular/cli
     ```

### Setup Steps

1. Navigate to the Angular frontend directory:
   ```powershell
   cd SDETAIO
   ```

2. Install dependencies:
   ```powershell
   npm install
   ```

3. Run the development server:
   ```powershell
   ng serve
   ```

4. Access the application at `http://tenant1.localhost:4200/login`

## Database Setup

### Prerequisites

1. **PostgreSQL 17.4**
   - Download from [postgresql.org/download/windows](https://www.postgresql.org/download/windows/)
   - Current version: PostgreSQL 17.4 on x86_64-windows, compiled by msvc-19.42.34436, 64-bit

### Setup Steps

1. Install PostgreSQL with default options
2. During installation, set a password for the postgres user
3. After installation, create a new database:
   - Open pgAdmin (installed with PostgreSQL)
   - Connect to the server
   - Right-click on "Databases" and select "Create" > "Database"
   - Name the database "sdet360" (or as specified in the application properties)

4. Configure database connection in Spring Boot:
   - Open `springboot-service/src/main/resources/application.properties`
   - Ensure the database connection properties match your PostgreSQL installation

## Troubleshooting

### Lombok Issues
If you encounter Lombok-related errors even after installing the plugin:
1. Ensure you have enabled annotation processing in IntelliJ
2. Try invalidating caches and restarting:
   - Go to File > Invalidate Caches / Restart
   - Select "Invalidate and Restart"

### gRPC Compilation Errors
If you encounter errors during gRPC compilation:
1. Ensure all prerequisites are installed correctly
2. Check that `protoc` is in your PATH
3. Try running the script with administrator privileges

### Database Connection Issues
If the Spring Boot application cannot connect to the database:
1. Verify PostgreSQL is running
2. Check the connection details in `application.properties`
3. Ensure the database user has appropriate permissions

### Angular Build Errors
If you encounter errors during Angular build:
1. Ensure you have the correct Node.js version
2. Try clearing npm cache:
   ```powershell
   npm cache clean --force
   ```
3. Delete `node_modules` folder and reinstall dependencies:
   ```powershell
   rm -r node_modules
   npm install
   ```
