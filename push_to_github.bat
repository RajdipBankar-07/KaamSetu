@echo off
setlocal enabledelayedexpansion
title KaamSetu - Push to GitHub

echo ================================================================
echo    🌾 KAAMSETU V1 - GITHUB REPOSITORY AUTO-PUSH SCRIPT
echo ================================================================
echo.

:: 1. Locate Git executable
set "GIT_CMD=git"
where git >nul 2>nul
if %errorlevel% neq 0 (
    if exist "C:\Program Files\Git\bin\git.exe" (
        set "GIT_CMD=C:\Program Files\Git\bin\git.exe"
    ) else if exist "E:\Program Files\Git\cmd\git.exe" (
        set "GIT_CMD=E:\Program Files\Git\cmd\git.exe"
    ) else if exist "E:\Program Files\Git\bin\git.exe" (
        set "GIT_CMD=E:\Program Files\Git\bin\git.exe"
    ) else if exist "C:\Program Files (x86)\Git\bin\git.exe" (
        set "GIT_CMD=C:\Program Files (x86)\Git\bin\git.exe"
    ) else if exist "%LOCALAPPDATA%\Programs\Git\bin\git.exe" (
        set "GIT_CMD=%LOCALAPPDATA%\Programs\Git\bin\git.exe"
    ) else (
        echo [ERROR] Git was not found in standard installation paths.
        echo Please install Git from: https://git-scm.com/downloads
        echo.
        pause
        exit /b 1
    )
)

echo [1/5] Using Git executable: "%GIT_CMD%"
echo.

:: 2. Initialize Git repo if needed
if not exist ".git" (
    echo [2/5] Initializing local Git repository...
    "%GIT_CMD%" init
    "%GIT_CMD%" branch -M main
) else (
    echo [2/5] Local Git repository detected.
)
echo.

:: 3. Configure Remote URL
set "REPO_URL=https://github.com/RajdipBankar-07/KaamSetu.git"
echo [3/5] Setting remote origin to: %REPO_URL%
"%GIT_CMD%" remote remove origin >nul 2>nul
"%GIT_CMD%" remote add origin %REPO_URL%
echo.

:: 4. Stage and Commit Changes
echo [4/5] Staging files for commit...
"%GIT_CMD%" add -A

set /p COMMIT_MSG="Enter commit message (Press Enter for default: 'KaamSetu V1: Full Enterprise Platform Release'): "
if "!COMMIT_MSG!"=="" (
    set "COMMIT_MSG=KaamSetu V1: Full Enterprise Platform Release"
)

"%GIT_CMD%" commit -m "!COMMIT_MSG!"
echo.

:: 5. Push to Remote GitHub Repository
echo [5/5] Pushing branch 'main' to GitHub: %REPO_URL%
echo.
"%GIT_CMD%" push -u origin main

if !errorlevel! neq 0 (
    echo.
    echo [NOTICE] If remote repository contains existing files, attempting force push...
    "%GIT_CMD%" push -f -u origin main
)

echo.
echo ================================================================
if !errorlevel! equ 0 (
    echo    ✅ SUCCESS: KaamSetu code pushed to GitHub successfully!
    echo    🔗 URL: https://github.com/RajdipBankar-07/KaamSetu
) else (
    echo    ❌ PUSH FAILED: Please check your internet connection or GitHub credentials.
)
echo ================================================================
echo.
pause
