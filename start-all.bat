@echo off
echo ===================================================
echo   Starting MediCore HMS Full System...
echo ===================================================

echo [1/2] Launching Spring Boot Backend (Port 8080)...
start "MediCore Backend (Spring Boot)" cmd /k "cd /d %~dp0backend && mvn spring-boot:run"

echo [2/2] Launching React Frontend (Vite)...
start "MediCore Frontend (React)" cmd /k "cd /d %~dp0frontend && npm run dev"

echo ===================================================
echo   System launched successfully!
echo   - Backend API:   http://localhost:8080
echo   - Swagger Docs:  http://localhost:8080/swagger-ui.html
echo   - Frontend App:  http://localhost:5173
echo   - Login:         admin / Admin@Medicore2026!
echo ===================================================
pause
