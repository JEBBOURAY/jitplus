$services = @(
    "discovery-service",
    "gateway-service",
    "auth-service",
    "customer-service",
    "loyalty-service"
)

Write-Host "Starting Databases..." -ForegroundColor Green
docker-compose up -d auth-db customer-db loyalty-db

foreach ($service in $services) {
    Write-Host "Starting $service in a new window..." -ForegroundColor Cyan
    Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd $service; mvn spring-boot:run"
    Start-Sleep -Seconds 5
}

Write-Host "All services are starting. Please wait for them to initialize." -ForegroundColor Green
