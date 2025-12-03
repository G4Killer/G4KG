param(
    [string]$Port = "8081"
)

$root = Resolve-Path "$PSScriptRoot/.."
Set-Location "$root/G4KG_BACKEND/g4kg-demo"

Write-Host "Starting Spring Boot backend on port $Port..."
& ".\gradlew.bat" bootRun "--args=--server.port=$Port"
