
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.io.*;

public class Master {

    private Master() {}

    public static void write2DArrayToFile(String[][] data){

    }

    public static void main(String[] args) {

  // assuming takes in a list of IPs

        String[] vmIPs = (args.length < 1) ? null : args;
        int numVMs = vmIPs.length;

        String[][] data = new String[numVMs][numVMs];
        //matrix m[b][a] recording capacity from VM b to VM a

        System.out.println("Running Master!");

        // nested for loop: only getting capacity between two machines once

        for (int a = 0; a < numVMs; a++) {

            String vmAIP = vmIPs[a];
            System.out.println("VM A IP: " + vmAIP);

            for (int b = a+1; b < numVMs; b++){

                String vmBIP = vmIPs[b];
                System.out.println("VM B IP: " + vmBIP);
                
                try {

                    Registry vmARegistry = LocateRegistry.getRegistry(vmAIP);
                    
                    VMInterface vmAStub = (VMInterface) vmARegistry.lookup("CloudSource");

                    System.out.println("successfully looked up stub, running the experiment.");
                     
                    String capacity = vmAStub.getCapacityWith(vmBIP);

                    System.out.println("Capacity from VMB (" + vmBIP+ ") to VMA (" + vmAIP +"): "
                                         + capacity);

                    data[b][a] = capacity;

                } catch (Exception e) {
                    System.err.println("Client exception: " + e.toString());
                    e.printStackTrace();
                }
            }
        }

        //write data to file
        try{
            PrintWriter newDataFile = new PrintWriter(new File("data.txt"));
            for (int a = 0; a< numVMs; a++){
                for (int b = a+1; b< numVMs; b++){
                    newDataFile.write("capacity from "+ vmIDs[b] + " to " + vmIDs[a] + " is: " + data[b][a]);
                 }
            }
            newDataFile.close();
        } catch( Exception e){
            System.err.println("file writing exception: " + e.toString());
        }

    }
}
