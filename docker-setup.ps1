# Error handling
$ErrorActionPreference = 'Stop'

# Helper functions
function Write-Info {
    param([string]$Message)
    Write-Host "[INFO] $Message" -ForegroundColor Cyan
}

function Write-Success {
    param([string]$Message)
    Write-Host "[SUCCESS] $Message" -ForegroundColor Green
}

function Write-Error {
    param([string]$Message)
    Write-Host "[ERROR] $Message" -ForegroundColor Red
}

try {
    Write-Info "Setting up SDET360 Docker Environment"

    # Check if Docker CLI is working
    try {
        $dockerVersion = docker --version 2>&1
        if ($LASTEXITCODE -ne 0) {
            throw "Docker CLI not working properly"
        }
        Write-Info "Docker version: $dockerVersion"
        
        # Verify Docker daemon is responding
        $dockerInfo = docker info 2>&1
        if ($LASTEXITCODE -ne 0) {
            throw "Docker daemon is not responding"
        }
    } catch {
        Write-Error "Docker is not properly initialized. Please ensure Docker Desktop is fully started."
        Write-Info "1. Look for the Docker icon in the system tray (bottom-right)"
        Write-Info "2. If the icon is red or missing, open Docker Desktop from Start menu"
        Write-Info "3. Wait for the Docker icon to turn white (not animating)"
        Write-Info "4. If prompted, sign in to Docker Hub"
        Write-Info "5. Right-click the Docker icon and ensure 'Switch to Windows containers' is selected"
        exit 1
    }

    # Create a network for the containers if it doesn't exist
    Write-Info "Creating Docker network 'sdet360-network'..."
    $networkExists = docker network ls --format '{{.Name}}' | Select-String -Pattern '^sdet360-network$' -Quiet
    if (-not $networkExists) {
        docker network create sdet360-network
        Write-Success "Docker network created successfully"
    } else {
        Write-Info "Docker network already exists, skipping creation"
    }

    # Build and start the containers with progress
    Write-Info "Building and starting Docker containers (this may take a few minutes)..."
    $startTime = Get-Date
    
    # Pull images first to show progress
    Write-Info "Pulling required Docker images..."
    docker-compose pull --ignore-pull-failures

    # Build and start containers
    docker-compose up --build -d

    # Wait for services to start
    $maxRetries = 30
    $retryInterval = 5
    $servicesReady = $false
    
    Write-Info "Waiting for services to start (this may take a few minutes)..."
    
    for ($i = 1; $i -le $maxRetries; $i++) {
        $containers = docker ps --format '{{.Names}} {{.Status}}' 2>$null
        
        $postgresRunning = $containers -match 'sdet360-postgres' -and $containers -match 'healthy'
        $springbootRunning = $containers -match 'sdet360-springboot'
        $aiServiceRunning = $containers -match 'sdet360-ai'
        $frontendRunning = $containers -match 'sdet360-frontend'
        
        $allRunning = $postgresRunning -and $springbootRunning -and $aiServiceRunning -and $frontendRunning
        
        if ($allRunning) {
            $servicesReady = $true
            break
        }
        
        Write-Info "Waiting for all services to start... (Attempt $i/$maxRetries)"
        Start-Sleep -Seconds $retryInterval
    }
    
    # Display service status
    $endTime = Get-Date
    $duration = $endTime - $startTime
    
    Write-Host "`n=== Service Status ===" -ForegroundColor Cyan
    Write-Host ("{0,-20} {1,-15} {2,-10}" -f "SERVICE", "STATUS", "HEALTH") -ForegroundColor Yellow
    Write-Host ("{0,-20} {1,-15} {2,-10}" -f "--------", "------", "------") -ForegroundColor Yellow
    
    function Get-ContainerStatus {
        param($containerName)
        $status = docker ps --filter "name=$containerName" --format '{{.Status}}' 2>$null
        if (-not $status) { return @('Not Running', 'N/A') }
        
        $health = if ($status -match '\(health: ([^)]+)\)') { $matches[1] } else { 'N/A' }
        $status = $status -replace '\s*\(.*\)', ''
        return @($status, $health)
    }
    
    $services = @(
        @{ Name = 'PostgreSQL'; Container = 'sdet360-postgres' },
        @{ Name = 'Spring Boot API'; Container = 'sdet360-springboot' },
        @{ Name = 'AI Service API'; Container = 'sdet360-ai' },
        @{ Name = 'Angular Frontend'; Container = 'sdet360-frontend' }
    )
    
    foreach ($service in $services) {
        $status = Get-ContainerStatus $service.Container
        $statusColor = if ($status[0] -eq 'Not Running') { 'Red' } else { 'Green' }
        $healthColor = if ($status[1] -eq 'healthy') { 'Green' } elseif ($status[1] -eq 'N/A') { 'Gray' } else { 'Yellow' }
        
        Write-Host ("{0,-20} " -f $service.Name) -NoNewline -ForegroundColor $statusColor
        Write-Host ("{0,-15} " -f $status[0]) -NoNewline -ForegroundColor $statusColor
        Write-Host $status[1] -ForegroundColor $healthColor
    }
    
    Write-Host "`n=== Access Information ===" -ForegroundColor Cyan
    $accessInfo = @{
        'PostgreSQL'      = 'localhost:5433'  # Updated port
        'Spring Boot API' = 'http://localhost:8081'  # Updated port
        'AI Service API'  = 'http://localhost:8001'  # Updated port
        'Angular App'     = 'http://localhost:4201'  # Updated port
    }
    
    $accessInfo.GetEnumerator() | Sort-Object Name | ForEach-Object {
        Write-Host ("{0,-20} {1}" -f ($_.Name + ":"), $_.Value) -ForegroundColor Yellow
    }
    
    Write-Host "`n=== Useful Commands ===" -ForegroundColor Cyan
    @(
        'View logs: docker-compose logs -f [service_name]',
        'Stop services: docker-compose down',
        'Restart services: docker-compose restart',
        'View running containers: docker ps',
        'View all containers: docker ps -a',
        'View container logs: docker logs [container_name]',
        'Open shell in container: docker exec -it [container_name] /bin/sh'
    ) | ForEach-Object { Write-Host "- $_" -ForegroundColor Yellow }
    
    if (-not $servicesReady) {
        Write-Warning "Some services failed to start within the expected time. Please check the logs with: docker-compose logs"
    } else {
        Write-Success ("`nAll services started successfully in {0} minutes and {1} seconds" -f 
            [math]::Floor($duration.TotalMinutes), $duration.Seconds)
    }
    
    Write-Host "`nSetup completed at $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')" -ForegroundColor Green
}
catch {
    Write-Error "An error occurred during setup: $_"
    Write-Info "Check the logs with: docker-compose logs"
    exit 1
}
