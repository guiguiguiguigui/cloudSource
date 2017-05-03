export CLASSPATH=./class
rmiregistry &
javac VM.java VMInterface.java Master.java Client.java -d ./class
java -classpath ./class -Djava.rmi.server.codebase=file:./class VM