javac Client.java
jar -cevf Client Client.jar Client*.class imgs info.txt server.txt
jar -tvf Client.jar
java -jar Client.jar