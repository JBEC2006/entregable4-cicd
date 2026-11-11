@echo off
setlocal

set JAR_NUEVO=%~1
set DESTINO=C:\ambientes\prod
set PUERTO=8082
if "%RUTA_HUMO%"=="" set RUTA_HUMO=/

set JENKINS_NODE_COOKIE=dontKillMe
set BUILD_ID=dontKillMe

if not exist "%JAR_NUEVO%" (
    echo No se encontro el jar %JAR_NUEVO%
    exit /b 1
)

if not exist "%DESTINO%\data" mkdir "%DESTINO%\data"

if exist "%DESTINO%\app.jar" (
    echo Guardando respaldo de la version actual
    copy /Y "%DESTINO%\app.jar" "%DESTINO%\app-anterior.jar" > nul
)

call :frenar
netstat -ano | findstr ":%PUERTO% " | findstr "LISTENING" > nul && (
    echo No se pudo frenar la version anterior
    exit /b 1
)
echo Copiando la version nueva
copy /Y "%JAR_NUEVO%" "%DESTINO%\app.jar" > nul || (
    echo No se pudo copiar el jar nuevo
    exit /b 1
)
call :levantar
call :probar

if "%CODIGO%"=="200" (
    echo Prueba de humo correcta, despliegue terminado
    exit /b 0
)

echo La prueba de humo fallo, codigo de respuesta %CODIGO%
if not exist "%DESTINO%\app-anterior.jar" (
    echo No hay version anterior para hacer rollback
    exit /b 1
)

echo Haciendo rollback a la version anterior
call :frenar
copy /Y "%DESTINO%\app-anterior.jar" "%DESTINO%\app.jar" > nul
set RUTA_HUMO=/
call :levantar
call :probar
echo Rollback terminado, produccion responde %CODIGO%
exit /b 1

:frenar
echo Frenando la version que corre en el puerto %PUERTO%
for /f "tokens=5" %%p in ('netstat -ano ^| findstr ":%PUERTO% " ^| findstr "LISTENING"') do taskkill /F /PID %%p > nul 2>&1
ping -n 4 127.0.0.1 > nul
exit /b 0

:levantar
echo Levantando la aplicacion con el perfil prod
powershell -NoProfile -Command "Start-Process java -ArgumentList '-jar','app.jar','--spring.profiles.active=prod' -WorkingDirectory '%DESTINO%' -WindowStyle Hidden -RedirectStandardOutput '%DESTINO%\app.log' -RedirectStandardError '%DESTINO%\error.log'"
exit /b 0

:probar
echo Prueba de humo contra http://localhost:%PUERTO%%RUTA_HUMO%
set CODIGO=000
set INTENTOS=0
:esperar
set /a INTENTOS+=1
ping -n 3 127.0.0.1 > nul
for /f %%c in ('curl -s -o NUL -w "%%{http_code}" http://localhost:%PUERTO%%RUTA_HUMO%') do set CODIGO=%%c
if not "%CODIGO%"=="000" exit /b 0
if %INTENTOS% lss 30 goto esperar
exit /b 0
