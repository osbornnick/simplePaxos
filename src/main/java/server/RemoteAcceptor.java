package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

/** Perform the Acceptor role of PAXOS */
public interface RemoteAcceptor extends Remote {

  /**
   * Receive a prepare message from the given Proposer
   *
   * @param fromUID uid of sending proposer
   * @param proposal id of prepared proposal
   * @throws RemoteException rmi
   */
  void receivePrepare(int fromUID, int proposal) throws RemoteException;

  /**
   * Receive an accept request message from a Proposer
   *
   * @param proposal id of accepted proposal
   * @param op operation of accepted proposal
   * @throws RemoteException rmi
   */
  void receiveAcceptRequest(int proposal, Operation op) throws RemoteException;
}
