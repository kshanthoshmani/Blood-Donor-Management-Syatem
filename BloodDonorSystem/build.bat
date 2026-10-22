@echo off
REM ============================================================
REM  Blood Donor Information System – Windows Build Script
REM  Requirements: JDK 17+, sqlite-jdbc.jar in lib\
REM ============================================================

setlocal

set SRC_DIR=src
set OUT_DIR=out
set LIB_DIR=lib
set JAR_NAME=BloodDonorSystem.jar
set MAIN_CLASS=gui.LoginFrame
set JDBC_JAR=sqlite-jdbc.jar

REM ── Step 1: Clean output directory ───────────────────────────────────────────
echo Cleaning output directory...
if exist "%OUT_DIR%" rd /s /q "%OUT_DIR%"
mkdir "%OUT_DIR%"

REM ── Step 2: Collect all source files ─────────────────────────────────────────
echo Collecting source files...
dir /s /b "%SRC_DIR%\*.java" > sources.txt

REM ── Step 3: Compile ──────────────────────────────────────────────────────────
echo Compiling sources...
javac -encoding UTF-8 ^
      -cp "%LIB_DIR%\%JDBC_JAR%" ^
      -d "%OUT_DIR%" ^
      @sources.txt

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Compilation failed. Check errors above.
    del sources.txt
    pause
    exit /b 1
)

del sources.txt
echo Compilation successful.

REM ── Step 4: Copy db.properties into output (classpath resource) ──────────────
echo Copying db.properties...
copy /Y "database\db.properties" "%OUT_DIR%\db.properties" >nul

REM ── Step 5: Create manifest ───────────────────────────────────────────────────
echo Creating manifest...
echo Main-Class: %MAIN_CLASS%> manifest.txt
echo Class-Path: lib/%JDBC_JAR%>> manifest.txt
echo.>> manifest.txt

REM ── Step 6: Package JAR ───────────────────────────────────────────────────────
echo Creating JAR...
jar cfm "%JAR_NAME%" manifest.txt -C "%OUT_DIR%" .
del manifest.txt

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] JAR creation failed.
    pause
    exit /b 1
)

echo.
echo ============================================================
echo  Build successful: %JAR_NAME%
echo ============================================================
echo.
echo To run the application:
echo   java -cp "%JAR_NAME%;lib\%JDBC_JAR%" gui.LoginFrame
echo.
echo Or double-click run.bat
echo.
pause
endlocal
