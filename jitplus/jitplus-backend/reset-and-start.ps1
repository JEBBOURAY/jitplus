# Arrêter les processus Java (Microservices)
Write-Host "Arrêt des microservices Java..." -ForegroundColor Yellow
Get-Process -Name "java" -ErrorAction SilentlyContinue | Stop-Process -Force

# Arrêter et nettoyer les conteneurs Docker et les volumes (Base de données)
Write-Host "Nettoyage des bases de données..." -ForegroundColor Yellow
cd c:\Users\ayoub\OneDrive\Desktop\Jit+\jitplus\jitplus-backend
docker-compose down -v
docker-compose up -d

# Attendre que les DB soient prêtes
Write-Host "Attente de l'initialisation des bases de données (10s)..." -ForegroundColor Cyan
Start-Sleep -Seconds 10

# Relancer les services
Write-Host "Redémarrage des services..." -ForegroundColor Green
.\start-all.ps1
