@echo off
if "%1"=="" (
	echo Usage: usedb dbms [mysql, oracle, postgresql, sqlite, sqlserver]
) else (
	copy /Y src\test\resources\lightsleep-%1.properties src\test\resources\lightsleep.properties
	copy /Y src\test\resources\lightsleep-%1.properties bin\lightsleep.properties
)
echo --------
type bin\lightsleep.properties
echo --------
