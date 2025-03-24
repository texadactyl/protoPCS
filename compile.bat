cd producer
go get
go install -v .\...

cd ..\consumer
javac *.java

cd ..\swinger
javac *.java

