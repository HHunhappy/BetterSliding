@rem Gradle wrapper startup script for Windows
@if "%DEBUG%"=="" @echo off
@setlocal enabledelayedexpansion
@set DIRNAME=%~dp0
@if "%DIRNAME%"=="" set DIRNAME=.
@set APP_BASE_NAME=%~n0
@set APP_HOME=%DIRNAME%
@set CLASSPATH=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar
@set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if %ERRORLEVEL% neq 0 (
    echo ERROR: No java command found in PATH.
    exit /b 1
)
"%JAVA_EXE%" -Xmx64m -Dorg.gradle.appname="%APP_BASE_NAME%" -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*
if %ERRORLEVEL% neq 0 exit /b %ERRORLEVEL%
@endlocal