export CLASSPATH=./cloudSource/experiment/class
rmi=( $(ps | grep rmiregistry) )
kill "${rmi[0]}"
rmiregistry &
javac cloudSource/experiment/VM.java cloudSource/experiment/VMInterface.java cloudSource/experiment/Master.java cloudSource/experiment/Client.java -d ./cloudSource/experiment/class
java -classpath ./cloudSource/experiment/class -Djava.rmi.server.codebase=file:./cloudSource/experiment/class VM &