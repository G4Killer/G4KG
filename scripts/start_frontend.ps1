$root = Resolve-Path "$PSScriptRoot/.."
Set-Location "$root/G4KG_FRONT"

if (-not (Test-Path "node_modules")) {
    Write-Host "Installing frontend dependencies..."
    npm install
}

Write-Host "Starting Vue dev server..."
npm run serve
