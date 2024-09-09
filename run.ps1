$REQUIRED_COMMANDS = @("mvn", "python")
$commandMissing = $false
$missingCommands = @()
foreach ($cmd in $REQUIRED_COMMANDS)
{
    if (-not (Get-Command $cmd -ErrorAction SilentlyContinue))
    {
        $commandMissing = $true
        $missingCommands += $cmd
    }
}
if ($commandMissing -gt 0)
{
    Write-Host "Les commandes suivantes sont manquantes : $( $missingCommands -join ', ' )" -ForegroundColor Red
    Write-Host "Veuillez installer les commandes manquantes ou les ajouter au PATH, puis relancer le script." -ForegroundColor Red
    exit 1
}



Write-Host "Vérification de l'existence du dossier de sauvegarde..."
if (-not (Test-Path -Path "backup"))
{
    Write-Host "Le dossier de sauvegarde n'existe pas. Création en cours..."
    New-Item -ItemType Directory -Path "backup"
    Write-Host "Dossier de sauvegarde créé avec succès."
}
else
{
    Write-Host "Le dossier de sauvegarde existe déjà."
}
Write-Host "======================================================================================================"

Write-Host "Définition des chemins source et de sauvegarde..."
$sourcePath = "target/site/allure-maven-plugin/history"
$backupPath = "backup/public/history"
Write-Host "Chemin source: $sourcePath"
Write-Host "Chemin de sauvegarde: $backupPath"
Write-Host "======================================================================================================"

Write-Host "Vérification de l'existence du dossier de sauvegarde spécifique..."
if (-not (Test-Path -Path $backupPath))
{
    Write-Host "Création du dossier de sauvegarde spécifique..."
    New-Item -ItemType Directory -Path $backupPath -Force
    Write-Host "Dossier de sauvegarde spécifique créé avec succès."
}
else
{
    Write-Host "Le dossier de sauvegarde spécifique existe déjà."
}
Write-Host "======================================================================================================"

Write-Host "Copie des fichiers du dossier source vers le dossier de sauvegarde..."
Copy-Item -Path "$sourcePath\*" -Destination $backupPath -Recurse -Force
Write-Host "Copie terminée."
Write-Host "======================================================================================================"

Write-Host "Nettoyage du projet Maven..."
mvn clean
Write-Host "Nettoyage terminé."
Write-Host "======================================================================================================"

Write-Host "Exécution des tests Maven..."
# choix de l'
#   application à tester   -> target.suite.xml
#   environnement à tester -> api.url
mvn test "-Dapi.url=https://jsonplaceholder.typicode.com"  "-Dtarget.suite.xml.file=src/test/resources/testng/testng-jsonapi.xml"
Write-Host "Tests terminés."
Write-Host "======================================================================================================"

Write-Host "Copie du contenu de: backup/public/history:"
Get-ChildItem -Path "backup/public/history" -File
Write-Host "======================================================================================================"

Write-Host "Création du dossier cible pour les résultats Allure..."
New-Item -ItemType Directory -Path "target/allure-results/history" -Force
if (Test-Path -Path $backupPath)
{
    Write-Host "Copie des fichiers $backupPath dans le dossier output de allure: target/allure-results/history"
    Copy-Item -Path "$backupPath\*" -Destination "target/allure-results/history" -Recurse -Force -Verbose
}
else
{
    Write-Host "Aucun backup trouvé $backupPath"
}
Write-Host "======================================================================================================"

Write-Host "Génération du rapport Allure..."
mvn allure:report
Write-Host "======================================================================================================"

Write-Host "Démarrage du serveur HTTP pour afficher le rapport Allure..."
python.exe -m http.server 8080 -d .\target\site\allure-maven-plugin
