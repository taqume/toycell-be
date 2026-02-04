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
echo  3. Start service-balance (Port 8083)
echo  4. Start service-fee (Port 8084)
echo  5. Start service-transaction (Port 8086)
echo  6. Start service-transfer (Port 8087)
echo  7. Start api-gateway (Port 8080)
echo  8. Start ALL services
echo  9. Stop all services
echo  A. Check port status
echo  0. Exit
echo.
echo ========================================
set /p choice="Enter your choice: "

if "%choice%"=="1" goto AUTH
if "%choice%"=="2" goto ACCOUNT
if "%choice%"=="3" goto BALANCE
if "%choice%"=="4" goto FEE
if "%choice%"=="5" goto TRANSACTION
if "%choice%"=="6" goto TRANSFER
if "%choice%"=="7" goto GATEWAY
if "%choice%"=="8" goto ALL
if "%choice%"=="9" goto STOP
if /i "%choice%"=="A" goto STATUS
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

:BALANCE
echo.
echo Starting service-balance on port 8083...
start "service-balance (8083)" cmd /k "pushd %PROJECT_DIR% && gradlew.bat :service-balance:bootRun"
timeout /t 2 /nobreak >nul
goto MENU

:FEE
echo.
echo Starting service-fee on port 8084...
start "service-fee (8084)" cmd /k "pushd %PROJECT_DIR% && gradlew.bat :service-fee:bootRun"
timeout /t 2 /nobreak >nul
goto MENU

:TRANSACTION
echo.
echo Starting service-transaction on port 8086...
start "service-transaction (8086)" cmd /k "pushd %PROJECT_DIR% && gradlew.bat :service-transaction:bootRun"
timeout /t 2 /nobreak >nul
goto MENU

:TRANSFER
echo.
echo Starting service-transfer on port 8087...
start "service-transfer (8087)" cmd /k "pushd %PROJECT_DIR% && gradlew.bat :service-transfer:bootRun"
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
echo Starting service-balance (8083)...
start "service-balance (8083)" cmd /k "pushd %PROJECT_DIR% && gradlew.bat :service-balance:bootRun"
timeout /t 8 /nobreak >nul
echo Starting service-fee (8084)...
start "service-fee (8084)" cmd /k "pushd %PROJECT_DIR% && gradlew.bat :service-fee:bootRun"
timeout /t 8 /nobreak >nul
echo Starting service-transaction (8086)...
start "service-transaction (8086)" cmd /k "pushd %PROJECT_DIR% && gradlew.bat :service-transaction:bootRun"
timeout /t 8 /nobreak >nul
echo Starting service-transfer (8087)...
start "service-transfer (8087)" cmd /k "pushd %PROJECT_DIR% && gradlew.bat :service-transfer:bootRun"
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
echo Port 8083 (Balance Service):
netstat -ano | findstr :8083 | findstr LISTENING
echo.
echo Port 8084 (Fee Service):
netstat -ano | findstr :8084 | findstr LISTENING
echo.
echo Port 8086 (Transaction Service):
netstat -ano | findstr :8086 | findstr LISTENING
echo.
echo Port 8087 (Transfer Service):
netstat -ano | findstr :8087 | findstr LISTENING
echo.
pause
goto MENU
