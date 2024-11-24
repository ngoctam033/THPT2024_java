@echo off
setlocal enabledelayedexpansion

for /d %%D in (*) do (
    if exist "%%D\pom.xml" (
        echo Building project: %%D
        pushd %%D

        mvn clean install -DskipTests
        if ERRORLEVEL 1 (
            echo Build failed for project: %%D
        ) else (
            echo Build succeeded for project: %%D
        )

        popd
        echo ----------------------------------------
    )
)

endlocal