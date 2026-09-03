$ErrorActionPreference = "Stop"

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$rootDir = (Resolve-Path (Join-Path $scriptDir "..")).Path
Set-Location -LiteralPath $rootDir

# 1. Run runtime prep
powershell -ExecutionPolicy Bypass -File "backend\prepare_runtime.ps1"

# 2. Locate Java and Javac
$javaExe = "C:\Program Files\Java\jdk-21.0.11\bin\java.exe"
$javacExe = "C:\Program Files\Java\jdk-21.0.11\bin\javac.exe"
if (-not (Test-Path $javaExe)) {
    $whereJava = Get-Command java.exe -ErrorAction SilentlyContinue
    if ($whereJava) { $javaExe = $whereJava.Source }
}
if (-not (Test-Path $javacExe)) {
    $whereJavac = Get-Command javac.exe -ErrorAction SilentlyContinue
    if ($whereJavac) { $javacExe = $whereJavac.Source }
}

Write-Host "`n1. Collecting all Java sources (main + test)..."
$javaFiles = Get-ChildItem -Path "backend\src\main\java", "backend\src\test\java" -Recurse -Filter "*.java" | Select-Object -ExpandProperty FullName
$sourceArgs = $javaFiles | ForEach-Object { '"' + $_.Replace('\', '/') + '"' }

Write-Host "2. Reading classpath..."
$cleanCp = (Get-Content "backend\target\clean_classpath.txt" -Raw).Split("`r`n", [System.StringSplitOptions]::RemoveEmptyEntries)[1].Trim().Trim('"')
$classesDir = (Resolve-Path "backend\target\classes").Path.Replace('\', '/')

$compileArgs = @("-encoding", "UTF-8", "-cp", "`"$cleanCp`"", "-d", "`"$classesDir`"") + $sourceArgs
[System.IO.File]::WriteAllLines("backend\target\compile_all_args.txt", $compileArgs)

Write-Host "3. Compiling all sources with javac..."
& "$javacExe" "@backend\target\compile_all_args.txt"

if ($LASTEXITCODE -ne 0) {
    Write-Error "Java compilation failed with exit code $LASTEXITCODE"
    exit 1
}

Write-Host "4. Compilation successful! Running SERVER-001..025 Test Suite..."
$runCp = "$classesDir;$cleanCp"
$runArgs = @("-cp", "`"$runCp`"", "com.kaamsetu.ServerAvailabilityIntegrationTest")
[System.IO.File]::WriteAllLines("backend\target\run_test_args.txt", $runArgs)

& "$javaExe" "@backend\target\run_test_args.txt"

Write-Host "`n5. Running JOB-001..047 Work Lifecycle Integration Test Suite..."
$runJobArgs = @("-cp", "`"$runCp`"", "com.kaamsetu.WorkLifecycleIntegrationTest")
[System.IO.File]::WriteAllLines("backend\target\run_job_test_args.txt", $runJobArgs)

& "$javaExe" "@backend\target\run_job_test_args.txt"

Write-Host "`n6. Running MSG-001..020 Persistent Messaging Integration Test Suite..."
$runMsgArgs = @("-cp", "`"$runCp`"", "com.kaamsetu.MessageIntegrationTest")
[System.IO.File]::WriteAllLines("backend\target\run_msg_test_args.txt", $runMsgArgs)

& "$javaExe" "@backend\target\run_msg_test_args.txt"

Write-Host "`nCompleted full test execution."

