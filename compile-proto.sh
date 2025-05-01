#!/usr/bin/env bash
set -e

# Versions
GRPC_VERSION=1.58.0
PROTOBUF_VERSION=4.29.3


# Directories
PROTO_DIR=proto
JAVA_OUT_DIR=springboot-service/src/main/java
PY_OUT_DIR=ai-service/app/generated

# Check and install Java plugin
if ! command -v protoc-gen-grpc-java &> /dev/null; then
  PLUGIN="protoc-gen-grpc-java-${GRPC_VERSION}-osx-x86_64.exe"
  if [ ! -f "$PLUGIN" ]; then
    echo "Downloading protoc-gen-grpc-java plugin..."
    curl -L "https://repo1.maven.org/maven2/io/grpc/protoc-gen-grpc-java/${GRPC_VERSION}/${PLUGIN}" -o "$PLUGIN"
    chmod +x "$PLUGIN"
  fi
  PROTOC_GEN_GRPC_JAVA="$(pwd)/$PLUGIN"
else
  PROTOC_GEN_GRPC_JAVA="$(command -v protoc-gen-grpc-java)"
fi

echo "Generating Java gRPC code..."
mkdir -p "${JAVA_OUT_DIR}/com/sdet/sdet360/grpc/generated"
protoc --plugin=protoc-gen-grpc-java="${PROTOC_GEN_GRPC_JAVA}" \
       --java_out="${JAVA_OUT_DIR}" \
       --grpc-java_out="${JAVA_OUT_DIR}" \
       -I="${PROTO_DIR}" "${PROTO_DIR}/ai_service.proto"

# Detect suitable Python binary (prefer python3.11/python3.10)
if command -v python3.11 >/dev/null; then
  PYTHON_BIN=python3.11
elif command -v python3.10 >/dev/null; then
  PYTHON_BIN=python3.10
else
  PYTHON_BIN=python
fi
echo "Using $PYTHON_BIN for Python codegen."

echo "Installing Python gRPC tools..."
$PYTHON_BIN -m pip install --upgrade grpcio==${GRPC_VERSION} grpcio-tools==${GRPC_VERSION} protobuf

echo "Generating Python gRPC code..."
mkdir -p "${PY_OUT_DIR}"
$PYTHON_BIN -m grpc_tools.protoc \
       -I="${PROTO_DIR}" \
       --python_out="${PY_OUT_DIR}" \
       --grpc_python_out="${PY_OUT_DIR}" \
       "${PROTO_DIR}/ai_service.proto"

touch "${PY_OUT_DIR}/__init__.py"

echo "Proto compilation completed!"