import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {

    private Client() {}

    public static void main(String[] args) {

    // assuming takes in a list of IPs


        String[] vmIPs = (args.length < 1) ? null : args;

        try {

            String vmAIP = vmIPs[0];
            Registry vmARegistry = LocateRegistry.getRegistry(vmAIP);
            VMInterface vmAStub = (VMInterface) vmARegistry.lookup("CloudSource");

            String response = vmAStub.getCapacityWith(vmIPs[1]);

            System.out.println("response: " + response);

        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
