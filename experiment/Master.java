
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Master {

    private Master() {}

    public static void main(String[] args) {

    // do master things.


    // but idk, man

        
    //following is client code from example

        String host = (args.length < 1) ? null : args[0];
        try {
            Registry registry = LocateRegistry.getRegistry(host);
            VMInterface stub = (VMInterface) registry.lookup("CloudSource");
            String response = stub.sayHello();
            System.out.println("response: " + response);
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
