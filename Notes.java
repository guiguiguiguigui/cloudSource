import java.net.InetAddress;
import java.net.UnknownHostException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
 
public class Notes {
 

    private static String executeCommand(String command) {

        StringBuffer output = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader =
                            new BufferedReader(new InputStreamReader(p.getInputStream()));

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

    public static void main(String[] args) {
 
        InetAddress ip;
        String hostname;
        try {

            if (args[0].equals("send")){
                ip = InetAddress.getLocalHost();
                hostname = ip.getHostName();
                System.out.println("Your current IP address : " + ip);
                System.out.println("Your current Hostname : " + hostname);

                //At SEND,
                String send = executeCommand("./pathload_1.3.2/pathload_snd &");

                System.out.println(send);

            } else if (args[0].equals("receive") && args.length == 2 ){

                 //at RECEIVE
                String recieve = executeCommand("./pathload_1.3.2/pathload_rcv -s "+ args[1]);

                System.out.println(recieve);

            } else {
                System.out.println("please give valid input");
            }

        } catch (UnknownHostException e) {
 
            e.printStackTrace();
        }
    }
}