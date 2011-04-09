#!/bin/bash
javac -cp jar\lwjgl.jar:jar\jinput.jar:jar\slick-util.jar TRPG.java
java -cp .:assets:jar\lwjgl.jar:jar\jinput.jar:jar\slick-util.jar -Djava.library.path=native\linux TRPG