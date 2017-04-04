/* CloudSource
 * Sophia Sun & Hanae Yaskawa
 * hello.java
 */


import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VMInterface extends Remote{
  
  String getCapacityWith( String ip ) throws  RemoteException;
  boolean startSender() throws RemoteException;

  
  
}
