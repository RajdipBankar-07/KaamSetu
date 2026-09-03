# -------------------------------------------------------------------
# KaamSetu - Runtime & Environment Preparation Script
# Automatically verifies JDK, dependencies, compiles classes, & creates argfiles
# -------------------------------------------------------------------
$ErrorActionPreference = "Stop"

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$rootDir = (Resolve-Path (Join-Path $scriptDir "..")).Path
Set-Location -LiteralPath $rootDir

Write-Host "KaamSetu Setup: Checking environment and preparing runtime..." -ForegroundColor Cyan

# 1. Locate Java & Javac
$javaPath = $null
$javacPath = $null

$searchPaths = @(
    $env:JAVA_HOME,
    "C:\Program Files\Java\jdk-21.0.11",
    "C:\Program Files\Java\jdk-21",
    "C:\Program Files\Java",
    "C:\Program Files (x86)\Java",
    "C:\Program Files\Eclipse Adoptium",
    "C:\Program Files\Microsoft",
    "C:\Program Files\Amazon Corretto",
    "C:\Program Files\Zulu",
    "E:\Program Files\Java",
    "E:\Program Files\Eclipse Adoptium",
    "$env:USERPROFILE\.jdks"
)

foreach ($p in $searchPaths) {
    if (-not [string]::IsNullOrWhiteSpace($p) -and (Test-Path -LiteralPath $p)) {
        $candidateJava = Join-Path $p "bin\java.exe"
        if (Test-Path -LiteralPath $candidateJava) {
            $javaPath = $candidateJava
            $candidateJavac = Join-Path $p "bin\javac.exe"
            if (Test-Path -LiteralPath $candidateJavac) {
                $javacPath = $candidateJavac
            }
            break
        }
        $subDirs = Get-ChildItem -LiteralPath $p -Directory -Filter "jdk*" -ErrorAction SilentlyContinue
        foreach ($sd in $subDirs) {
            $candidateJava2 = Join-Path $sd.FullName "bin\java.exe"
            if (Test-Path -LiteralPath $candidateJava2) {
                $javaPath = $candidateJava2
                $candidateJavac2 = Join-Path $sd.FullName "bin\javac.exe"
                if (Test-Path -LiteralPath $candidateJavac2) {
                    $javacPath = $candidateJavac2
                }
                break 2
            }
        }
    }
}

if (-not $javaPath) {
    $whereJava = Get-Command java.exe -ErrorAction SilentlyContinue
    if ($whereJava) { $javaPath = $whereJava.Source }
}

if (-not $javacPath) {
    $whereJavac = Get-Command javac.exe -ErrorAction SilentlyContinue
    if ($whereJavac) { $javacPath = $whereJavac.Source }
}

if (-not $javaPath) {
    Write-Error "Java runtime (java.exe) could not be found. Please ensure Java 21 is installed."
    exit 1
}

Write-Host "   [OK] Found Java: $javaPath" -ForegroundColor Green

# 2. Version comparison helper
function Compare-VersionStrings($v1, $v2) {
    $p1 = $v1.Split('.-') | ForEach-Object { $val = 0; if ([int]::TryParse($_, [ref]$val)) { $val } else { 0 } }
    $p2 = $v2.Split('.-') | ForEach-Object { $val = 0; if ([int]::TryParse($_, [ref]$val)) { $val } else { 0 } }
    $maxLen = [Math]::Max($p1.Length, $p2.Length)
    for ($i = 0; $i -lt $maxLen; $i++) {
        $n1 = if ($i -lt $p1.Length) { $p1[$i] } else { 0 }
        $n2 = if ($i -lt $p2.Length) { $p2[$i] } else { 0 }
        if ($n1 -lt $n2) { return -1 }
        if ($n1 -gt $n2) { return 1 }
    }
    return 0
}

# 3. Create target directories
$targetClasses = Join-Path $rootDir "backend\target\classes"
$targetTestClasses = Join-Path $rootDir "backend\target\test-classes"
if (-not (Test-Path -LiteralPath $targetClasses)) {
    New-Item -ItemType Directory -Force -Path $targetClasses | Out-Null
}
if (-not (Test-Path -LiteralPath $targetTestClasses)) {
    New-Item -ItemType Directory -Force -Path $targetTestClasses | Out-Null
}

# 4. Resolve dependencies from .m2 repository
$m2Repo = "$env:USERPROFILE\.m2\repository"
if (-not (Test-Path -LiteralPath $m2Repo)) {
    Write-Error ".m2 repository not found at $m2Repo"
    exit 1
}

$allJars = Get-ChildItem -LiteralPath $m2Repo -Recurse -Filter "*.jar" | 
    Where-Object { 
        $_.Name -notlike "*-sources.jar" -and 
        $_.Name -notlike "*-javadoc.jar" -and 
        $_.Name -notlike "*-tests.jar" -and
        $_.FullName -notmatch "plugins"
    }

$deduped = @{}
foreach ($j in $allJars) {
    $versionDir = $j.Directory
    $artifactDir = $versionDir.Parent
    $artifactKey = $artifactDir.FullName
    $version = $versionDir.Name

    if (-not $deduped.ContainsKey($artifactKey) -or (Compare-VersionStrings $version $deduped[$artifactKey].Version) -gt 0) {
        $deduped[$artifactKey] = @{ Path = $j.FullName; Version = $version }
    }
}

$selectedJars = $deduped.Values | ForEach-Object { $_.Path.Replace('\', '/') }
$cleanCp = ($selectedJars -join ";")
$classesDir = $targetClasses.Replace('\', '/')
$fullCp = $classesDir + ";" + $cleanCp

# Write clean_classpath.txt
$cleanCpFile = Join-Path $rootDir "backend\target\clean_classpath.txt"
[System.IO.File]::WriteAllLines($cleanCpFile, @("-cp", "`"$cleanCp`""))

# Write run_app_clean_args.txt
$runAppFile = Join-Path $rootDir "backend\target\run_app_clean_args.txt"
$appArgs = @(
    "-cp",
    "`"$fullCp`"",
    "com.kaamsetu.KaamSetuApplication"
)
[System.IO.File]::WriteAllLines($runAppFile, $appArgs)
Write-Host "   [OK] Built classpath and argument files ($($selectedJars.Count) dependency JARs)" -ForegroundColor Green

# 5. Copy resources
$resDir = Join-Path $rootDir "backend\src\main\resources"
if (Test-Path -LiteralPath $resDir) {
    Copy-Item -Path "$resDir\*" -Destination $targetClasses -Recurse -Force
    Write-Host "   [OK] Copied application resources and JSON datasets" -ForegroundColor Green
}

# 6. Check if compilation is required
$mainClassFile = Join-Path $targetClasses "com\kaamsetu\KaamSetuApplication.class"
$needsCompile = $true
if (Test-Path -LiteralPath $mainClassFile) {
    $mainClassTime = (Get-Item -LiteralPath $mainClassFile).LastWriteTime
    $srcDir = Join-Path $rootDir "backend\src\main\java"
    $latestSource = Get-ChildItem -LiteralPath $srcDir -Recurse -Filter "*.java" | Sort-Object LastWriteTime -Descending | Select-Object -First 1
    if ($latestSource -and $latestSource.LastWriteTime -le $mainClassTime) {
        $needsCompile = $false
    }
}

if ($needsCompile) {
    if (-not $javacPath) {
        Write-Warning "javac.exe not found to recompile classes, using existing compiled classes."
    } else {
        Write-Host "   Compiling Java sources with javac..." -ForegroundColor Yellow
        $srcDir = Join-Path $rootDir "backend\src\main\java"
        $javaFiles = Get-ChildItem -LiteralPath $srcDir -Recurse -Filter "*.java" | Select-Object -ExpandProperty FullName
        $sourceArgs = $javaFiles | ForEach-Object { '"' + $_.Replace('\', '/') + '"' }
        $compileArgs = @("-encoding", "UTF-8", "-cp", "`"$cleanCp`"", "-d", "`"$classesDir`"") + $sourceArgs
        $compileFile = Join-Path $rootDir "backend\target\compile_main_args.txt"
        [System.IO.File]::WriteAllLines($compileFile, $compileArgs)

        & "$javacPath" "@$compileFile"
        if ($LASTEXITCODE -ne 0) {
            Write-Error "Compilation failed with exit code $LASTEXITCODE"
            exit 1
        }
        Write-Host "   [OK] Java sources compiled successfully" -ForegroundColor Green
    }
} else {
    Write-Host "   [OK] Compiled classes are up to date" -ForegroundColor Green
}

Write-Host "[SUCCESS] KaamSetu runtime is ready!" -ForegroundColor Green
