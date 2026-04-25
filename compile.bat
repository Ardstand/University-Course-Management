@echo off
echo Compiling University Course Management System...

if not exist out mkdir out

dir /s /b src\*.java > sources.txt

javac -d out -sourcepath src @sources.txt

if %ERRORLEVEL% == 0 (
    echo.
    echo Compile successful!
    echo.
    echo Run with:
    echo java -cp out com.university.Main
) else (
    echo.
    echo Compile failed. Check errors above.
)
