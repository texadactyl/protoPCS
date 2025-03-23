set -e

cd producer
go install -v ./...

cd ../consumer
javac *.java

