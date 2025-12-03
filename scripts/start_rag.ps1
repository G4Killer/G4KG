param(
    [int]$Port = 8000
)

$root = Resolve-Path "$PSScriptRoot/.."
Set-Location "$root/G4RAG"

# 确保当前 PowerShell 会话能读取系统/用户环境变量中的 DASHSCOPE_API_KEY
if (-not $env:DASHSCOPE_API_KEY) {
    $env:DASHSCOPE_API_KEY = [System.Environment]::GetEnvironmentVariable("DASHSCOPE_API_KEY", "User")
    if (-not $env:DASHSCOPE_API_KEY) {
        $env:DASHSCOPE_API_KEY = [System.Environment]::GetEnvironmentVariable("DASHSCOPE_API_KEY", "Machine")
    }
}

if (-not (Test-Path ".venv")) {
    Write-Host "Creating Python venv..."
    python -m venv .venv
}

Write-Host "Activating venv..."
& ".\.venv\Scripts\Activate.ps1"

Write-Host "Installing RAG dependencies (requirements_websocket.txt)..."
pip install -r requirements_websocket.txt

Write-Host "Starting RAG WebSocket server on port $Port..."
python -m uvicorn src.websocket_server:app --host 0.0.0.0 --port $Port --log-level warning
