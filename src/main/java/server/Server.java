package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Server extends Remote {
  /**
   * Get and return the value associated with the given key, null if not present
   *
   * @param key to find value for
   * @return value for key, null if it doesn't exist
   * @throws RemoteException for any number of issues
   */
  String get(String key) throws RemoteException;

  /**
   * Put a key value pair in the store
   *
   * @param key to add
   * @param value to add
   * @return true if committed, false otherwise
   * @throws RemoteException
   */
  boolean put(String key, String value) throws RemoteException;

  /**
   * Delete a key value pair
   *
   * @param key to delete from store
   * @return true if committed, false otherwise
   * @throws RemoteException
   */
  boolean delete(String key) throws RemoteException;
}
