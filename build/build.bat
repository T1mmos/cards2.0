# ROOT resolves to e.g. G:\EclipseWorkspace\cards2.0
SET CURDIR="%cd%"
SET ROOT="%~dp0\.."

cd /d %ROOT%

cd cards_framework
call mvn clean compile
call mvn clean assembly:single
call mvn clean install

cd ..\cards_solitaire
call mvn clean compile
call mvn assembly:single

cd ..\cards_solitaireshowdown
call mvn clean compile
call mvn assembly:single

cd /d %CURDIR%