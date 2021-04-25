javac Server.java
jar -cevf Server Server.jar Server*.class ServerThread.java ServerThread*.class
jar -tvf Server.jar
java -jar Server.jar
