# compile-proto.ps1 - Windows PowerShell version
$ErrorActionPreference = "Stop"

# Versions
$grpcVersion = "1.58.0"

# Directories
$protoDir = "proto"
$javaOutDir = "springboot-service\src\main\java"
$pyOutDir = "ai-service\app\generated"

# Locate or download Java plugin
try {
    $cmd = Get-Command protoc-gen-grpc-java -ErrorAction Stop
    $protocGenGrpcJava = $cmd.Source
} catch {
    $plugin = "protoc-gen-grpc-java-$grpcVersion-windows-x86_64.exe"
    if (-not (Test-Path $plugin)) {
        Write-Host "Downloading protoc-gen-grpc-java plugin..."
        Invoke-WebRequest -Uri "https://repo1.maven.org/maven2/io/grpc/protoc-gen-grpc-java/$grpcVersion/$plugin" -OutFile $plugin
    }
    $protocGenGrpcJava = Join-Path (Get-Location) $plugin
}

# Python interpreter selection (use venv if present)
$venvPython = Join-Path (Get-Location) 'ai-service\.venv\Scripts\python.exe'
if (Test-Path $venvPython) { $pythonExe = $venvPython } else { $pythonExe = 'python' }

Write-Host "Generating Java gRPC code..."
New-Item -Path "$javaOutDir\com\sdet\sdet360\grpc\generated" -ItemType Directory -Force | Out-Null
& protoc --plugin="protoc-gen-grpc-java=$protocGenGrpcJava" `
         --java_out="$javaOutDir" `
         --grpc-java_out="$javaOutDir" `
         -I="$protoDir" "$protoDir\ai_service.proto"

Write-Host "Installing Python dependencies from requirements.txt..."
& $pythonExe -m pip install -r ai-service/requirements.txt

Write-Host "Generating Python gRPC code..."
New-Item -Path $pyOutDir -ItemType Directory -Force | Out-Null
& $pythonExe -m grpc_tools.protoc `
         -I="$protoDir" `
         --python_out="$pyOutDir" `
         --grpc_python_out="$pyOutDir" `
         "$protoDir\ai_service.proto"

# Create package init
New-Item -Path "$pyOutDir\__init__.py" -ItemType File -Force | Out-Null

Write-Host "Proto compilation completed!"
