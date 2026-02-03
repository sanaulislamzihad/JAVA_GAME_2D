@echo off
REM My2dGame compile check - double-click kore run koren
echo Compiling...
set JAVAC=
if defined JAVA_HOME set JAVAC=%JAVA_HOME%\bin\javac.exe
if not exist "%JAVAC%" set JAVAC=javac

"%JAVAC%" -encoding UTF-8 -d out\production\My2dGame -cp "out\production\My2dGame" ^
  src\Main\*.java src\entity\*.java src\object\*.java src\Sound\*.java src\tiles\*.java 2>&1

if %ERRORLEVEL% neq 0 (
  echo.
  echo *** Compile FAIL *** - error upore dekhen.
  pause
  exit /b 1
)

REM resources (enemy, player, objects, tiles, sound, maps) copy korte hobe - naile enemy.png paabe na
if exist resources (
  xcopy /E /I /Y resources\* out\production\My2dGame\ >nul
  echo Resources copied (enemy, player, objects, etc.)
)

echo.
echo *** Compile OK ***  - ekhon run.bat diye game chalaben. Enemy image dekha jabe.
pause
