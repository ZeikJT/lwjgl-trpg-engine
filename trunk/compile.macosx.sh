#!/bin/bash
javac -d compiled -cp jar/lwjgl.jar:jar/jinput.jar:jar/slick-util.jar:src src/TRPG/TRPG.java
java -cp .:jar/lwjgl.jar:jar/jinput.jar:jar/slick-util.jar:assets:compiled -Djava.library.path=native/macosx TRPG.TRPG