@echo off
setlocal
set PROJECT_DIR=%~dp0

:MENU
cls
echo.
echo ========================================
echo  Toycell Backend - Service Launcher
echo ========================================
echo.
echo  1. Start service-auth (Port 8081)
echo  2. Start service-account (Port 8082)
echo  3. Start api-gateway (Port 8080)
echo  4. Start ALL services
echo  5. Stop all services
echo  6. Check port status
echo  0. Exit
echo.
echo ========================================
set /p choice="Enter your choice: "

if "%choice%"=="1" goto AUTH
if "%choice%"=="2" goto ACCOUNT
if "%choice%"=="3" goto GATEWAY
if "%choice%"=="4" goto ALL
if "%choice%"=="5" goto STOP
if "%choice%"=="6" goto STATUS
if "%choice%"=="0" exit
goto MENU

:AUTH
echo.
echo Starting service-auth on port 8081...
start "service-auth (8081)" cmd /k "pushd %PROJECT_DIR% && gradlew.bat :service-auth:bootRun"
timeout /t 2 /nobreak >nul
goto MENU

:ACCOUNT
echo.
echo Starting service-account on port 8082...
start "service-account (8082)" cmd /k "pushd %PROJECT_DIR% && gradlew.bat :service-account:bootRun"
timeout /t 2 /nobreak >nul
goto MENU

:GATEWAY
echo.
echo Starting api-gateway on port 8080...
start "api-gateway (8080)" cmd /k "pushd %PROJECT_DIR% && gradlew.bat :api-gateway:bootRun"
timeout /t 2 /nobreak >nul
goto MENU

:ALL
echo.
echo Stopping old processes...
taskkill /F /IM java.exe >nul 2>&1
timeout /t 3 /nobreak >nul
echo.
echo Starting service-auth (8081)...
start "service-auth (8081)" cmd /k "pushd %PROJECT_DIR% && gradlew.bat :service-auth:bootRun"
timeout /t 8 /nobreak >nul
echo Starting service-account (8082)...
start "service-account (8082)" cmd /k "pushd %PROJECT_DIR% && gradlew.bat :service-account:bootRun"
timeout /t 8 /nobreak >nul
echo Starting api-gateway (8080)...
start "api-gateway (8080)" cmd /k "pushd %PROJECT_DIR% && gradlew.bat :api-gateway:bootRun"
echo.
echo All services starting! Wait 60 seconds then test.
timeout /t 3 /nobreak >nul
goto MENU

:STOP
echo.
echo Stopping all Java processes...
taskkill /F /IM java.exe >nul 2>&1
timeout /t 2 /nobreak >nul
echo Services stopped!
pause
goto MENU

:STATUS
echo.
echo Checking ports...
echo.
echo Port 8080 (API Gateway):
netstat -ano | findstr :8080 | findstr LISTENING
echo.
echo Port 8081 (Auth Service):
netstat -ano | findstr :8081 | findstr LISTENING
echo.
echo Port 8082 (Account Service):
netstat -ano | findstr :8082 | findstr LISTENING
echo.
pause
goto MENU
