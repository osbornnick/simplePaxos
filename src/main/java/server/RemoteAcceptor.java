package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Perform the Acceptor role of PAXOS
 */
public interface RemoteAcceptor extends Remote {

    void receivePrepare(int fromUID, int proposal) throws RemoteException;

    void receiveAcceptRequest(int proposal, Operation op) throws RemoteException;

}
