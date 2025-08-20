@echo off
REM Run this from the repo root. Optionally pass a commit message after the script.
REM Example: daily-update.bat "fix: correct total calculation"
powershell -ExecutionPolicy Bypass -File "%~dp0daily-update.ps1" %*
