# Deploying SDET360AI to RunPod

This guide explains how to deploy the SDET360AI application to RunPod using Docker and CI/CD.

## Prerequisites

- Docker account (for storing your Docker image)
- RunPod account with API key
- GitHub repository with your SDET360AI code

## Manual Deployment

1. **Build the Docker image locally**:

```bash
cd /path/to/SDET360AI
docker build -t yourusername/sdet360ai:latest .
```

2. **Push the image to Docker Hub**:

```bash
docker login
docker push yourusername/sdet360ai:latest
```

3. **Deploy to RunPod**:
   - Log in to your RunPod account at https://www.runpod.io/
   - Create a new pod
   - Select "Docker Hub" as the image source
   - Enter your image name: `yourusername/sdet360ai:latest`
   - Configure the following ports: 4201, 8081, 8001, 50051
   - Set environment variables:
     - `SPRING_PROFILES_ACTIVE=prod`
   - Deploy the pod

## CI/CD Deployment (GitHub Actions)

The repository includes a GitHub Actions workflow that automatically builds and deploys your application to RunPod whenever changes are pushed to the main branch.

### Setup GitHub Secrets

Add the following secrets to your GitHub repository:

1. `DOCKER_USERNAME` - Your Docker Hub username
2. `DOCKER_PASSWORD` - Your Docker Hub password or access token
3. `RUNPOD_API_KEY` - Your RunPod API key (found in your RunPod account settings)

### Workflow Details

The workflow performs the following steps:
1. Builds the Docker image
2. Pushes the image to Docker Hub
3. Deploys the image to RunPod using the RunPod API

### Customizing Deployment

You can customize the deployment by modifying the `.github/workflows/deploy-to-runpod.yml` file:

- Change the cloud region by modifying the `cloudType` parameter
- Adjust disk sizes by changing `containerDiskSize` and `volumeDiskSize`
- Add additional environment variables in the `env` section

## Application Access

Once deployed, your application will be accessible at:

- Frontend: `http://<your-pod-ip>:4201`
- Spring Boot API: `http://<your-pod-ip>:8081`
- AI Service API: `http://<your-pod-ip>:8001`
- gRPC Service: `<your-pod-ip>:50051`

## Troubleshooting

If you encounter issues with the deployment:

1. Check the pod logs in the RunPod dashboard
2. Verify that all required ports are properly exposed
3. Ensure your Docker image builds successfully locally before deploying
4. Check the GitHub Actions workflow logs for any CI/CD errors
