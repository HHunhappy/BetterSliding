#!/bin/sh

#
# Gradle wrapper startup script for POSIX
#
DIRNAME="$(dirname "$0")"
APP_HOME="$DIRNAME"
APP_BASE_NAME="$(basename "$0")"
CLASSPATH="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"

if [ -n "$JAVA_HOME" ]; then
    JAVA_EXE="$JAVA_HOME/bin/java"
else
    JAVA_EXE=java
    if ! command -v "$JAVA_EXE" >/dev/null 2>&1; then
        echo "ERROR: JAVA_HOME is not set and no java command found in PATH."
        exit 1
    fi
fi

exec "$JAVA_EXE" -Xmx64m "-Dorg.gradle.appname=$APP_BASE_NAME" -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
