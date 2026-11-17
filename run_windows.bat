@echo off
if not exist out mkdir out
javac -d out src\ParcelTrackingSystem.java
if errorlevel 1 pause & exit /b 1
java -cp out ParcelTrackingSystem
pause
