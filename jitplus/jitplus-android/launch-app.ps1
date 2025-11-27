# Script de lancement JitPlus Android
Write-Host ' Lancement de l''application JitPlus...' -ForegroundColor Cyan

# Vérifier si gradlew existe
if (Test-Path '.\gradlew.bat') {
    Write-Host ' Gradle wrapper trouvé' -ForegroundColor Green
    
    # Vérifier les émulateurs
    Write-Host ' Vérification des émulateurs...' -ForegroundColor Yellow
    $emulators = adb devices 2>$null
    
    if ($emulators -match 'emulator-') {
        Write-Host ' Émulateur détecté' -ForegroundColor Green
        Write-Host ' Installation de l''APK...' -ForegroundColor Cyan
        .\gradlew.bat assembleDebug installDebug
        
        if ($?) {
            Write-Host ' Application installée avec succès !' -ForegroundColor Green
            Write-Host ' Lancement de l''application...' -ForegroundColor Cyan
            adb shell am start -n com.jitplus.merchant/.ui.login.LoginActivity
        }
    } else {
        Write-Host ' Aucun émulateur détecté. Veuillez :' -ForegroundColor Yellow
        Write-Host '  1. Ouvrir Android Studio' -ForegroundColor White
        Write-Host '  2. Lancer un émulateur (Device Manager)' -ForegroundColor White
        Write-Host '  3. Re-exécuter ce script' -ForegroundColor White
    }
} else {
    Write-Host ' Gradle wrapper manquant' -ForegroundColor Yellow
    Write-Host ' Étapes requises :' -ForegroundColor Cyan
    Write-Host '  1. Ouvrir le projet dans Android Studio' -ForegroundColor White
    Write-Host '  2. Attendre la synchronisation Gradle (première fois)' -ForegroundColor White
    Write-Host '  3. Les fichiers gradlew seront générés automatiquement' -ForegroundColor White
    Write-Host '  4. Re-exécuter ce script' -ForegroundColor White
}
