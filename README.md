# protoPCS

There are 3 source folders:
* producer - File producer for the Consumer, written in Go.
* Consumer - File consumer, written in Java.
* swinger - Richard's first Java Swing screen in 15 years.

## Consumer logic

* Load all the records into a TreeMap.
* For several randomly selected TreeMap items, print the contents of the leaf.
* Print the first and last record in the B-tree.

To-date, supporting only record types begin frame, end frame, change an int64 primitive, and change a float64 primitive.

## Quick Start on MacOS and Linux
```
git clone https://github.com/texadactyl/protoPCS
bash compile.sh
bash run.sh
```
## Quick Start on Windows
```
git clone https://github.com/texadactyl/protoPCS
compile.bat
run.bat
```
## Swinger Launch on MacOS, Linux, or Windows

Assuming that you have done one of the quick starts:

```java -cp swinger swinger```

-or-
```
cd swinger
java swinger
```
