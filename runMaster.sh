cd cloudSource/experiment
export CLASSPATH=./class
javac VM.java VMInterface.java Master.java Client.java -d ./class
java -classpath ./class -Djava.rmi.server.codebase=file:./class Master \
"34.210.54.174" "54.186.87.4" "54.190.194.52" "54.200.139.39"
