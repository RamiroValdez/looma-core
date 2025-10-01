@echo off
echo ========================================
echo Ejecutando test de ManageWorkController
echo ========================================
echo.

gradlew test --tests "com.amool.hexagonal.adapters.in.rest.controllers.ManageWorkControllerTest" --info

echo.
echo ========================================
echo Test completado!
echo ========================================
pause
