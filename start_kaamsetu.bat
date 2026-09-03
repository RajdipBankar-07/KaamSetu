@echo off
setlocal enabledelayedexpansion
title KaamSetu Launcher 2>nul

echo ================================================================
echo    KAAMSETU [KaamSetu] - ONE-CLICK FULL-STACK LAUNCHER
echo    Rural and Village Local Jobs Two-Sided Marketplace SaaS
echo ================================================================
echo.

cd /d "%~dp0" 2>nul

:: ------------------------------------------------------------------
:: 1. LOCATE JAVA 21+ RUNTIME
:: ------------------------------------------------------------------
set "JAVA_EXE="

:: A. Check JAVA_HOME
if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\java.exe" (
        set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
        goto FOUND_JAVA
    )
)

:: B. Check standard Program Files paths
if exist "C:\Program Files\Java\jdk-21.0.11\bin\java.exe" (
    set "JAVA_EXE=C:\Program Files\Java\jdk-21.0.11\bin\java.exe"
    goto FOUND_JAVA
)
if exist "C:\Program Files\Java\jdk-21\bin\java.exe" (
    set "JAVA_EXE=C:\Program Files\Java\jdk-21\bin\java.exe"
    goto FOUND_JAVA
)
if exist "E:\Program Files\Java\jdk-21.0.11\bin\java.exe" (
    set "JAVA_EXE=E:\Program Files\Java\jdk-21.0.11\bin\java.exe"
    goto FOUND_JAVA
)
if exist "E:\Program Files\Java\jdk-21\bin\java.exe" (
    set "JAVA_EXE=E:\Program Files\Java\jdk-21\bin\java.exe"
    goto FOUND_JAVA
)
if exist "C:\Program Files\Java\" (
    for /d %%i in ("C:\Program Files\Java\jdk*") do (
        if exist "%%i\bin\java.exe" (
            set "JAVA_EXE=%%i\bin\java.exe"
            goto FOUND_JAVA
        )
    )
)
if exist "E:\Program Files\Java\" (
    for /d %%i in ("E:\Program Files\Java\jdk*") do (
        if exist "%%i\bin\java.exe" (
            set "JAVA_EXE=%%i\bin\java.exe"
            goto FOUND_JAVA
        )
    )
)
if exist "C:\Program Files\Eclipse Adoptium\" (
    for /d %%i in ("C:\Program Files\Eclipse Adoptium\jdk*") do (
        if exist "%%i\bin\java.exe" (
            set "JAVA_EXE=%%i\bin\java.exe"
            goto FOUND_JAVA
        )
    )
)

:: C. Check User's .jdks
if exist "%USERPROFILE%\.jdks\" (
    for /d %%i in ("%USERPROFILE%\.jdks\jdk*") do (
        if exist "%%i\bin\java.exe" (
            set "JAVA_EXE=%%i\bin\java.exe"
            goto FOUND_JAVA
        )
    )
)

:: D. Fallback to system PATH
where.exe java >nul 2>nul
if !errorlevel! equ 0 (
    set "JAVA_EXE=java"
    goto FOUND_JAVA
)

echo [ERROR] Java JDK 21 was not found on your system!
echo Please ensure JDK 21 is installed in "C:\Program Files\Java\jdk-21.0.11"
echo.
pause
exit /b 1

:FOUND_JAVA
echo [1/3] Java runtime located: "!JAVA_EXE!"
echo.

:: ------------------------------------------------------------------
:: 1.5 VERIFY RUNTIME & CLASSPATH ARTIFACTS
:: ------------------------------------------------------------------
if not exist "%~dp0backend\target\run_app_clean_args.txt" (
    echo [INFO] Preparing runtime environment and generating classpath...
    powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0backend\prepare_runtime.ps1"
    echo.
) else if not exist "%~dp0backend\target\classes\com\kaamsetu\KaamSetuApplication.class" (
    echo [INFO] Classes missing. Compiling and preparing runtime...
    powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0backend\prepare_runtime.ps1"
    echo.
)

:: ------------------------------------------------------------------
:: 2. START SPRING BOOT BACKEND [PORT 8090]
:: ------------------------------------------------------------------
:START_BACKEND
curl.exe -s -m 2 http://localhost:8090/api/v1/health >nul 2>nul
if !errorlevel! equ 0 (
    echo [2/3] Spring Boot Backend is ALREADY running on port 8090.
) else (
    netstat -ano 2>nul | findstr ":8090" | findstr "LISTENING" >nul 2>nul
    if !errorlevel! equ 0 (
        echo [2/3] Spring Boot Backend is ALREADY listening on port 8090.
    ) else (
        echo [2/3] Starting KaamSetu Spring Boot Backend Server on Port 8090...
        start "KaamSetu Backend Server [Port 8090]" /D "%~dp0" "!JAVA_EXE!" "-Dfile.encoding=UTF-8" "@backend\target\run_app_clean_args.txt"
        
        echo     Waiting for backend initialization and database readiness...
        set "BACKEND_READY=0"
        for /l %%i in (1,1,25) do (
            if !BACKEND_READY!==0 (
                ping 127.0.0.1 -n 2 >nul
                curl.exe -s -m 2 http://localhost:8090/api/v1/health >nul 2>nul
                if !errorlevel! equ 0 (
                    set "BACKEND_READY=1"
                    echo     Backend is live and database is fully initialized!
                ) else (
                    netstat -ano 2>nul | findstr ":8090" | findstr "LISTENING" >nul 2>nul
                    if !errorlevel! equ 0 (
                        if %%i GEQ 6 (
                            set "BACKEND_READY=1"
                            echo     Backend is live and listening on port 8090!
                        )
                    )
                )
            )
        )
        if !BACKEND_READY!==0 (
            echo     Backend is starting up in background...
        )
    )
)
echo.

:: ------------------------------------------------------------------
:: 3. LAUNCH FRONTEND IN DEFAULT BROWSER
:: ------------------------------------------------------------------
echo [3/3] Opening KaamSetu Web Application in your default browser...
start "" "%~dp0index.html"
echo.

:: ------------------------------------------------------------------
:: 4. STATUS DASHBOARD AND MANAGEMENT MENU
:: ------------------------------------------------------------------
:DASHBOARD
echo ================================================================
echo    KAAMSETU IS RUNNING - BACKEND AND FRONTEND ACTIVE
echo ================================================================
echo.
echo  - Frontend UI App    : %~dp0index.html
echo  - Backend REST API   : http://localhost:8090/api/v1
echo  - Swagger API Docs   : http://localhost:8090/api/v1/swagger-ui.html
echo  - H2 Database Console: http://localhost:8090/api/v1/h2-console
echo.
echo  ----------------------------------------------------------------
echo  Pre-configured Test Accounts:
echo  ----------------------------------------------------------------
echo   - Worker Account   : Username = Suresh   ^| Password = Suresh@123
echo   - Provider Account : Username = Mahesh   ^| Password = Mahesh@123
echo   - Super Admin      : Username = Admin    ^| Password = Admin@123
echo  ----------------------------------------------------------------
echo.
echo  ================================================================
echo  Server Control Options:
echo  ----------------------------------------------------------------
echo   [S] Stop Backend Server [Port 8090]
echo   [R] Restart Backend Server
echo   [O] Re-open Frontend in Browser
echo   [Q] Quit Launcher [Leave Backend Running]
echo  ================================================================
echo.

set "USER_CHOICE="
set /p USER_CHOICE="Enter option S/R/O/Q or press Enter to keep running: "

if /i "!USER_CHOICE!"=="S" goto STOP_SERVER
if /i "!USER_CHOICE!"=="R" goto RESTART_SERVER
if /i "!USER_CHOICE!"=="O" goto REOPEN_FRONTEND
if /i "!USER_CHOICE!"=="Q" goto EXIT_SCRIPT

goto EXIT_SCRIPT

:STOP_SERVER
echo.
echo [INFO] Stopping KaamSetu Backend process on port 8090...
for /f "tokens=5" %%a in ('netstat -ano 2^>nul ^| findstr ":8090" ^| findstr "LISTENING"') do (
    set "TPID=%%a"
    if defined TPID (
        if not "!TPID!"=="0" (
            taskkill /F /T /PID !TPID! >nul 2>nul
            echo Backend process PID !TPID! terminated.
        )
    )
)
powershell -NoProfile -Command "Get-NetTCPConnection -LocalPort 8090 -ErrorAction SilentlyContinue | ForEach-Object { Stop-Process -Id .OwningProcess -Force -ErrorAction SilentlyContinue }" >nul 2>nul
echo Backend Server Stopped Successfully.
echo.
pause
exit /b 0

:RESTART_SERVER
echo.
echo [INFO] Stopping current backend instance...
for /f "tokens=5" %%a in ('netstat -ano 2^>nul ^| findstr ":8090" ^| findstr "LISTENING"') do (
    set "TPID=%%a"
    if defined TPID (
        if not "!TPID!"=="0" (
            taskkill /F /T /PID !TPID! >nul 2>nul
        )
    )
)
powershell -NoProfile -Command "Get-NetTCPConnection -LocalPort 8090 -ErrorAction SilentlyContinue | ForEach-Object { Stop-Process -Id .OwningProcess -Force -ErrorAction SilentlyContinue }" >nul 2>nul
timeout /t 2 /nobreak >nul
goto START_BACKEND

:REOPEN_FRONTEND
echo [INFO] Opening frontend in browser...
start "" "%~dp0index.html"
goto DASHBOARD

:EXIT_SCRIPT
echo.
echo ================================================================
echo  KaamSetu is active. To stop the backend anytime, close its window
echo  or run start_kaamsetu.bat again and select [S].
echo ================================================================
echo.
exit /b 0