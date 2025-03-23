package main

import (
	"fmt"
	"os"
	"strconv"
)

func showHelp() {
	fmt.Println("Usage:\tgo  run  main.go  <count>  <data file>")
	os.Exit(1)
}

func main() {

	if len(os.Args) != 3 {
		showHelp()
	}

	// Get the record count between begin and end frame.
	count, err := strconv.ParseInt(os.Args[1], 10, 64)
	if err != nil {
		showHelp()
	}

	// Get the output file path.
	pathData := os.Args[2]

	// Capture data.
	err = producer(count, pathData)
	if err != nil {
		os.Exit(1)
	}
}
