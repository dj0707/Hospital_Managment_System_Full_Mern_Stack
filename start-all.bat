@echo off
title MediCore HMS - Auto Launcher
color 0b
echo =======================================================================
echo          MediCore HMS - Smart Hospital & Pharmacy POS System
echo =======================================================================
echo.

echo [1/2] Checking and Starting Backend Spring Boot (Port 8080)...
start "MediCore-Backend" cmd /k "cd /d %~dp0backend && mvn spring-boot:run"

echo [2/2] Checking and Starting Frontend UI (Vite)...
start "MediCore-Frontend" cmd /k "cd /d %~dp0frontend && npm run dev"

echo.
echo =======================================================================
echo  Launch triggered!
echo  1. Please allow 10-15 seconds for the backend to start up.
echo  2. Open your browser at: http://localhost:5173
echo.
echo  Credentials:
echo  - Admin Portal:      admin / Admin@Medicore2026!
echo  - Doctor Portal:     doctor / Doctor@Medicore2026!
echo  - Pharmacy Portal:   pharmacist / Pharmacy@Medicore2026!
echo  - Patient Portal:    (Use self-registration)
echo =======================================================================
echo.
pause
