# protoPCS

There are 3 source folders:
* producer - File producer for the Consumer, written in Go.
* Consumer - File consumer, written in Java.
* swinger - Richard's first Swing screen in 15 years.

## Consumer logic

* Load all the records into a B-tree.
* For several randomly selected B-tree items, print the contents of the leaf.
* Print the first and last record in the B-tree.

To-date, supporting only record types begin frame, end frame, change an int64 primitive, and change a float64 primitive.

## Quick Start on MacOS and Linux

* bash compile.sh
* bash run.sh
