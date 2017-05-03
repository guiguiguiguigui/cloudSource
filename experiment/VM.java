/* CloudSource
 * Sophia Sun & Hanae Yaskawa
 * VM.java
 */

import javax.swing.Timer;        
import java.awt.*;
import java.awt.event.*;
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
    -----------------------------------------*/
    public String getCapacityWith( String senderIP ){

        String data = "no data";

        try {
            Registry registry = LocateRegistry.getRegistry(senderIP);
            VMInterface sender = (VMInterface) registry.lookup("CloudSource"); //get sender RMI object
            boolean response = sender.startSender();


            
            if (response){ //Sender started is true

                    System.out.println("sleeping for reciever.");
                Thread.sleep(1000);

                System.out.println("Sender booted successfully. Starting reciever process.");
                //make sure there is no previous data in log
                Process p1 = Runtime.getRuntime().exec("rm pathload.log");
                p1.waitFor();
                System.out.println("deleting old Pathload Logs");

                // start reciever
                Process p = Runtime.getRuntime().exec("../pathload_1.3.2/pathload_rcv -s "+ senderIP);
                InputStream inputStream = p.getInputStream();
                //print out the output stream
                System.out.println("Started Pathload reciever");
                int status = p.waitFor();
                if (status != 0){
                    System.out.println("process terminately abnormally: ");

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream), 1);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        System.out.println(line);
                    }
                    inputStream.close();
                    bufferedReader.close();
                }

                // parse log file
                Scanner reader = new Scanner(new File("pathload.log"));
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
    -----------------------------------------*/
    public boolean startSender() {

        try{
            //Sender sender1 = new Sender();
            // Starts a new thread
            //sender1.start();
            

            Timer timer;
            timer = new Timer(100, new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    //...Update the progress bar...
                    //timer.stop();
                    System.out.println("waiting for sender.");
                    String send  = executeCommand("../pathload_1.3.2/pathload_snd");
                }    
            });
            timer.start();
            System.out.println("Started pathload sender.");
            return true;

        } catch (Exception e){
            return false;
        }   
    }


    /*------------------------------------------
      Thread class for sender
    -----------------------------------------*/
    public class Sender extends Thread {

        @Override
        public void run() {
                String send  = executeCommand("../pathload_1.3.2/pathload_snd");
                /*
                PrintWriter log = new PrintWriter(new File("pathload_sender_log.txt"));
                log.println(send);
                */
        }
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
            registry.rebind("CloudSource", stub);

            System.out.println("Server ready");

        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
