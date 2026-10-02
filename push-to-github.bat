@echo off
echo ===================================================
echo   Pushing MediCore HMS to GitHub Repository...
echo ===================================================
echo Target Repo: https://github.com/dj0707/Hospital_Managment_System_Full_Mern_Stack
echo.

cd /d "%~dp0"
git remote remove origin 2>nul
git remote add origin https://github.com/dj0707/Hospital_Managment_System_Full_Mern_Stack.git
git branch -M main
git push -u origin main --force

echo.
echo ===================================================
echo   Push complete! Check your GitHub repository.
echo ===================================================
pause
