# bestfs

A simple filesystem.

Team 11: Toan Tony Nguyen and Gordon Ngo

Class: CS149-03

Homework Assignment 04 due November 29, 2015

# Workload

### Tony Nguyen

###### The methods:

* currentd()
* maked(String name)
* createf(String file)
* extendf(String file, int size)
* truncf(String file, int size)
* formatd()
* chdir(String dir)

###### The data structure of the file system

* Block class
* File class
* Finding children and parent subdirectories and files
* Memory management in disk and files

### Gordon Ngo

###### The methods:

* deletefd(String name)
* listd(String directory)
* listf(String file)
* sizef(String file)
* movf(String file, String directory)
* listfb()
* dumpfs()

###### The data structure of the file system

* Softlinks and i-nodes

# Remarks

Originally developed on C.  Ran into too many problems with memory management and segmentations.

All work done on C was translated to Java and further developed on Java.

Original github with logs of who did what can be [FOUND HERE.](https://github.com/toantonyh/filesys)