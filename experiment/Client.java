import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {

    private Client() {}

    public static void main(String[] args) {

    // assuming takes in a list of IPs


        String[] vmIPs = (args.length < 1) ? null : args;

        try {

            System.out.println("Running Master!");

            String vmAIP = vmIPs[0];

            System.out.println("VM A IP: " + vmAIP);

            System.out.println("VM B IP: " + vmIPs[1]);
            
            Registry vmARegistry = LocateRegistry.getRegistry(vmAIP);
            VMInterface vmAStub = (VMInterface) vmARegistry.lookup("CloudSource");


            System.out.println("successfully looked up stub, running the experiment.");
             
            String response = vmAStub.getCapacityWith(vmIPs[1]);

            System.out.println("response: " + response);

        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
