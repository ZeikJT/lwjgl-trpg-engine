if not exist compiled mkdir compiled
javac -d compiled -cp jar/lwjgl.jar;jar/jinput.jar;jar/slick-util.jar;src src/TRPG/Main.java
java -cp .;jar/lwjgl.jar;jar/jinput.jar;jar/slick-util.jar;assets;compiled -Djava.library.path=native/windows TRPG.Main
