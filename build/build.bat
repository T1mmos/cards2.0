SET ROOT="G:\EclipseWorkspace\cards_2.0"
cd /d %ROOT%

cd cards_framework
call mvn clean compile
call mvn clean assembly:single

cd ..\cards_solitaire
call mvn clean compile
call mvn assembly:single

cd ..\cards_solitaireshowdown
call mvn clean compile
call mvn assembly:single

cd /d %ROOT%