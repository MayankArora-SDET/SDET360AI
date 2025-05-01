# Windows Setup for gRPC Code Generation

This guide covers prerequisites and steps to run `compile-proto.ps1` on Windows.

## Prerequisites

1. **PowerShell 7+**
   - Ensure PowerShell Core is installed.

2. **Java JDK 17+**
   - `javac` must be on `PATH`.

3. **Protocol Buffer Compiler (`protoc`)**
   - Download pre-built binaries from https://github.com/protocolbuffers/protobuf/releases.
   - Add `protoc.exe` to `PATH`.

4. **Python 3.10+**
   - Install from https://www.python.org/downloads/windows/.
   - Ensure `python` and `pip` are on `PATH`.

5. **PowerShell Execution Policy**
   ```powershell
   Set-ExecutionPolicy -Scope CurrentUser -ExecutionPolicy RemoteSigned
   ```

## Windows script dependencies

- Uses `$grpcVersion` and `$protoBufVersion` variables.
- Downloads `protoc-gen-grpc-java` plugin if missing.
- Installs Python gRPC packages: `grpcio`, `grpcio-tools`, `protobuf`.

## Running the script

1. Open PowerShell in the project root.
2. If needed, allow script execution:
   ```powershell
   Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
   ```
3. Execute:
   ```powershell
   .\compile-proto.ps1
   ```
4. Generated Java stubs appear under `springboot-service\src\main\java`.
5. Python stubs appear under `ai-service\app\generated`.

---

Ensure you re-run this script after any changes to `proto/ai_service.proto`.
