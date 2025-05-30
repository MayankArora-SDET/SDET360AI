FROM python:3.11-slim

WORKDIR /app

# Install system dependencies
RUN apt-get update && apt-get install -y curl

# Copy requirements first for better caching
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy application code
COPY . .

# Create the generated directory if it doesn't exist
RUN mkdir -p /app/app/generated

# Set environment variables
ENV PYTHONUNBUFFERED=1

# Expose FastAPI and gRPC ports
EXPOSE 8000
EXPOSE 50051

# Start the application
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]
