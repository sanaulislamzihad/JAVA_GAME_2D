@echo off
cd /d "%~dp0"
echo Running game...
java -cp "out\production\My2dGame" Main.DriverClass
if %ERRORLEVEL% neq 0 (
  echo.
  echo Run failed. Prothome build.bat chalaye compile koren.
  pause
)
