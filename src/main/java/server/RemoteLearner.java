package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Performs the Learner role of the PAXOS algorithm
 */
public interface RemoteLearner extends Remote {

    void receiveAccepted(int fromUID, int proposalID, Operation op) throws RemoteException;

}
