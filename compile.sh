set -e

cd producer
go get
go install -v ./...

cd ../consumer
javac *.java

