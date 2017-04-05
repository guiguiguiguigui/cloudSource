/* CloudSource
 * Sophia Sun & Hanae Yaskawa
 * VM.java
 */

        
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

// for startSender
import java.io.*;


//for methond getMyIP
import java.net.InetAddress;
import java.net.UnknownHostException;
        
//for methond executeCommand
import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.util.*;


public class VM implements VMInterface {
        
    public VM() {}


    /*------------------------------------------
      tells @param senderIP to start a pathload sender.
      then start a pathload receiver on this machine
      access pathload.log, extract useful information, return.

      MAYBE GOOD?
    -----------------------------------------*/
    public String getCapacityWith( String senderIP ){

        String data = "no data";

        try {
            Registry registry = LocateRegistry.getRegistry(senderIP);
            VMInterface sender = (VMInterface) registry.lookup("CloudSource"); //get sender RMI object
            boolean response = sender.startSender();
            
            if (response){ //Sender started is true

                //make sure there is no previous data in log
                Process p1 = Runtime.getRuntime().exec("rm pathload_1.3.2/pathload.log");
                p1.waitFor();
                System.out.println("deleting old Pathload Logs");

                // start reciever
                Process p = Runtime.getRuntime().exec("./pathload_1.3.2/pathload_rcv -s "+ senderIP);
                System.out.println("Started Pathload reciever");
                p.waitFor();

                // parse log file
                Scanner reader = new Scanner(new File("pathload_1.3.2/pathload.log"));
                System.out.println("Parcing pathload log");

                while (reader.hasNext()) {
                    String line = reader.nextLine();
                    if (line.toLowerCase().contains("result")) {
                        data = reader.nextLine().split(": ")[1]; //get the useful line.
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Receiver exception: " + e.toString());
            e.printStackTrace();
        }

        return data;
    }


    /*------------------------------------------
      Starts a Pathload sender on this machine.
      GOOD
    -----------------------------------------*/
    public boolean startSender() {

        String send = executeCommand("./pathload_1.3.2/pathload_snd");

        System.out.println("Started pathload sender.");
        try{
            PrintWriter log = new PrintWriter(new File("pathload_sender_log.txt"));
            log.println(send);
        } catch (Exception e){
            System.out.println(e);

        }

        return true;
    }



    /*------------------------------------------
      uses java Runtime to execute a shell command,
      waits for the process to finish
      returms the logs after process finishes.
    -----------------------------------------*/
    private static String executeCommandWait(String command) {

        StringBuffer output = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            System.out.println("started excecuting: " + command);
            p.waitFor();

            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine())!= null) {
                System.out.println(line);
                output.append(line + "\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return output.toString();
    }

/*------------------------------------------
      gets the ip of this machine.
    -----------------------------------------*/
    private static String getMyIP(){
        String ip="";
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
            System.out.println("Your current IP address : " + ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return ip;
    }

    /*------------------------------------------
      uses java Runtime to execute a shell command,
      returms the logs after process finishes.

      TODO: do we keep //p.waitFor(); ?
    -----------------------------------------*/
    private static String executeCommand(String command) {

        StringBuffer output = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            System.out.println("started excecuting: " + command);
            BufferedReader reader =new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            while ((line = reader.readLine())!= null) {
                System.out.println(line);
                output.append(line + "\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return output.toString();
    }
        
    public static void main(String args[]) {

        try {
            VM obj = new VM();
            VMInterface stub = (VMInterface) UnicastRemoteObject.exportObject(obj, 0);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.getRegistry();

            String myIP = getMyIP();
            
            System.out.println("setting rmi server hostname to "+ myIP);
            
            // tried to solve connection problem, not sure if it works
            System.setProperty("java.rmi.server.hostname",myIP);


            //diferentiate machines by local registry (ip) (identified at lookup), not by name
            //because its the same application
            registry.bind("CloudSource", stub);

            System.err.println("Server ready");

        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
