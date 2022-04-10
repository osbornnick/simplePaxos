package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

/** Performs the Learner role of the PAXOS algorithm */
public interface RemoteLearner extends Remote {

  /**
   * receive an Accepted proposal update from the given Acceptor
   *
   * @param fromUID uid of sending Acceptor
   * @param proposalID id of accepted proposal
   * @param op accepted operation to apply
   * @throws RemoteException rmi
   */
  void receiveAccepted(int fromUID, int proposalID, Operation op) throws RemoteException;
}
