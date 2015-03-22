#!/bin/bash
if [ ! -d compiled ]; then
  mkdir compiled
fi
javac -d compiled -cp tests:tests/jar/hamcrest-core-1.3.jar:tests/jar/junit-4.12.jar:tests/jar/mockito-core-1.10.19.jar:tests/jar/javassist-3.19.0-GA.jar:tests/jar/objenesis-2.1.jar:tests/jar/powermock-mockito-1.6.2-full.jar:jar/lwjgl.jar:jar/jinput.jar:jar/slick-util.jar:src tests/unit/Tests.java
java -cp .:tests/jar/hamcrest-core-1.3.jar:tests/jar/junit-4.12.jar:tests/jar/mockito-core-1.10.19.jar:tests/jar/javassist-3.19.0-GA.jar:tests/jar/objenesis-2.1.jar:tests/jar/powermock-mockito-1.6.2-full.jar:jar/lwjgl.jar:jar/jinput.jar:jar/slick-util.jar:assets:compiled org.junit.runner.JUnitCore unit.Tests
