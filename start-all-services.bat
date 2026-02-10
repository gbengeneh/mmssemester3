@echo off
echo ========================================
echo  Microservices Startup Script
echo ========================================
echo.

echo [1/5] Starting Eureka Server (Port 8761)...
start "Eureka Server" cmd /k "cd eureka_server && mvnw spring-boot:run"
echo Waiting 30 seconds for Eureka Server to start...
timeout /t 30 /nobreak
echo.

echo [2/5] Starting Config Server (Port 8888)...
start "Config Server" cmd /k "cd config_server && mvnw spring-boot:run"
echo Waiting 20 seconds for Config Server to start...
timeout /t 20 /nobreak
echo.

echo [3/5] Starting User Service (Port 8081)...
start "User Service" cmd /k "cd user_service && mvnw spring-boot:run"
echo Waiting 15 seconds for User Service to start...
timeout /t 15 /nobreak
echo.

echo [4/5] Starting Employee Service (Port 8082)...
start "Employee Service" cmd /k "cd employee_service && mvnw spring-boot:run"
echo Waiting 15 seconds for Employee Service to start...
timeout /t 15 /nobreak
echo.

echo [5/5] Starting API Gateway...
start "API Gateway" cmd /k "cd api-gateway && mvnw spring-boot:run"
echo.

echo ========================================
echo  All services are starting!
echo ========================================
echo.
echo Check Eureka Dashboard: http://localhost:8761
echo.
echo Press any key to exit this window...
pause >nul
