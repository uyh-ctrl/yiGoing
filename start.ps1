[CmdletBinding()]
param([string]$Profile = 'dev')

$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSCommandPath
$frontend = Join-Path $root 'mall-admin-web'
$logDir = Join-Path $root 'logs'

function Test-ListeningPort([int]$Port) {
    return $null -ne (Get-NetTCPConnection -State Listen -LocalPort $Port -ErrorAction SilentlyContinue | Select-Object -First 1)
}
function Wait-ListeningPort([int]$Port, [int]$Seconds) {
    $deadline = (Get-Date).AddSeconds($Seconds)
    while (-not (Test-ListeningPort $Port) -and (Get-Date) -lt $deadline) { Start-Sleep -Milliseconds 500 }
    return Test-ListeningPort $Port
}

if (-not (Get-Command java -ErrorAction SilentlyContinue)) { throw 'Java 21 is required.' }
if (-not (Get-Command npm.cmd -ErrorAction SilentlyContinue)) { throw 'Node.js and npm are required.' }
if (-not (Test-Path $frontend)) { throw "Frontend directory is missing: $frontend" }
New-Item -ItemType Directory -Force -Path $logDir | Out-Null

$jar = Join-Path $root 'mall-admin\target\mall-admin-1.0-SNAPSHOT.jar'
if (-not (Test-ListeningPort 8080)) {
    if (-not (Test-Path $jar)) {
        Write-Host 'Building backend...' -ForegroundColor Cyan
        Push-Location (Join-Path $root 'mall-portal')
        try { & .\mvnw.cmd '-f' '..\pom.xml' '-pl' 'mall-admin' '-am' '-DskipTests' 'package' } finally { Pop-Location }
        if ($LASTEXITCODE -ne 0) { throw 'Backend build failed.' }
    }
    Start-Process -FilePath java -ArgumentList @('-jar', $jar, "--spring.profiles.active=$Profile") -WorkingDirectory $root -RedirectStandardOutput (Join-Path $logDir 'backend.out.log') -RedirectStandardError (Join-Path $logDir 'backend.err.log') -WindowStyle Hidden
    if (-not (Wait-ListeningPort 8080 30)) { throw "Backend did not start. Check $logDir\backend.err.log" }
}

if (-not (Test-Path (Join-Path $frontend 'node_modules'))) {
    Push-Location $frontend
    try { & npm.cmd ci; if ($LASTEXITCODE -ne 0) { throw 'Frontend dependency installation failed.' } } finally { Pop-Location }
}
if (-not (Test-ListeningPort 5173)) {
    Start-Process -FilePath npm.cmd -ArgumentList @('run', 'dev', '--', '--host', '127.0.0.1') -WorkingDirectory $frontend -RedirectStandardOutput (Join-Path $logDir 'frontend.out.log') -RedirectStandardError (Join-Path $logDir 'frontend.err.log') -WindowStyle Hidden
    if (-not (Wait-ListeningPort 5173 30)) { throw "Frontend did not start. Check $logDir\frontend.err.log" }
}
Write-Host 'Open http://127.0.0.1:5173' -ForegroundColor Green
