@echo off
echo ========================================
echo     Eventified Backend Launcher
echo ========================================
echo.
echo Choose your mode:
echo 1. Development Mode (No Security) - For backend development
echo 2. Frontend Mode (JWT Security) - For frontend team testing  
echo 3. Production Mode (Full Security) - For production
echo 4. Exit
echo.
set /p choice="Enter your choice (1-4): "

if "%choice%"=="1" (
    echo.
    echo Starting in DEVELOPMENT mode...
    echo Security: DISABLED
    echo Profile: dev
    echo.
    set SPRING_PROFILES_ACTIVE=dev
    mvn spring-boot:run
) else if "%choice%"=="2" (
    echo.
    echo Starting in FRONTEND mode...
    echo Security: ENABLED with JWT
    echo Profile: frontend
    echo.
    set SPRING_PROFILES_ACTIVE=frontend
    mvn spring-boot:run
) else if "%choice%"=="3" (
    echo.
    echo Starting in PRODUCTION mode...
    echo Security: ENABLED with JWT
    echo Profile: prod
    echo.
    set SPRING_PROFILES_ACTIVE=prod
    mvn spring-boot:run
) else if "%choice%"=="4" (
    echo Goodbye!
    exit /b 0
) else (
    echo Invalid choice. Please try again.
    pause
    goto :eof
)

pause