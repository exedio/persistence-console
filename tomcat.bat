@echo off
rem Guess CATALINA_HOME if not defined

set "CURRENT_DIR=%cd%"
if not "%CATALINA_HOME%" == "" goto CHECK_TOMCAT
set "CATALINA_HOME=%CURRENT_DIR%\tomcat"

:CHECK_TOMCAT
echo CATALINA_HOME set to "%CATALINA_HOME%"
if exist "%CATALINA_HOME%\bin\catalina.bat" goto RUN
echo Tomcat not built correctly! Run 'ant clean tomcat' first.
goto END

:RUN
rem Run catalina with different params e.g. jpda run
echo Tomcat is in "%CATALINA_HOME%"

if not "%1" == "" goto DO_RUN

echo Usage:  tomcat ( commands ... )
echo commands:
echo   jpda		Start Catalina under JPDA debugger
echo   run		Start Catalina in the current window
echo   jpda run	Start Catalina under JPDA debugger in the current window
echo   configtest	Run a basic syntax check on server.xml
echo   version	What version of tomcat are you running?
goto END

:DO_RUN
call "%CATALINA_HOME%\bin\catalina.bat" %1 %2
if errorlevel 1 goto END

:END
