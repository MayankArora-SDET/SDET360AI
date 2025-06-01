FROM python:3.11-slim

WORKDIR /app

# Install system dependencies
RUN apt-get update && apt-get install -y curl

# Create the generated directory first to ensure it exists
RUN mkdir -p /app/app/generated

# Copy requirements first for better caching
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy Python application code (excluding the generated directory)
COPY . .

# The generated protobuf files will be mounted from the host at runtime

# Set environment variables
ENV PYTHONUNBUFFERED=1

# Expose FastAPI and gRPC ports
EXPOSE 8000
EXPOSE 50051

# Start the application
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]
