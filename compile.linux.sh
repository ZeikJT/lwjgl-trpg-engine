#!/bin/bash
if [ ! -d compiled ]; then
  mkdir compiled
fi
javac -d compiled -cp jar/lwjgl.jar:jar/jinput.jar:jar/slick-util.jar:src src/TRPG/Main.java
java -cp .:jar/lwjgl.jar:jar/jinput.jar:jar/slick-util.jar:assets:compiled -Djava.library.path=native/linux TRPG.Main
