/* CloudSource
 * Sophia Sun & Hanae Yaskawa
 * hello.java
 */


import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Experiment extends Remote{
  
  String experientOneRound( String ip ) throws  RemoteException;
  boolean startSender() throws RemoteException;

  
  
}
