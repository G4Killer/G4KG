$root = Resolve-Path "$PSScriptRoot/.."
$backendScript = Join-Path $root "scripts/start_backend.ps1"
$frontendScript = Join-Path $root "scripts/start_frontend.ps1"
$ragScript = Join-Path $root "scripts/start_rag.ps1"

Write-Host "Launching backend, frontend, and RAG in separate PowerShell windows..."

Start-Process -FilePath "powershell" -ArgumentList "-NoExit", "-Command", "& '$backendScript'"
Start-Process -FilePath "powershell" -ArgumentList "-NoExit", "-Command", "& '$frontendScript'"
Start-Process -FilePath "powershell" -ArgumentList "-NoExit", "-Command", "& '$ragScript'"

Write-Host "All services starting. Check each window for logs."
