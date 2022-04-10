package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

/** Performs the Proposer role of the PAXOS algorithm */
public interface RemoteProposer extends Remote {

  /**
   * Set the current proposal operation
   *
   * @param op to set the current proposal too
   * @throws RemoteException rmi
   */
  void setProposal(Operation op) throws RemoteException;

  /**
   * Send a prepare message to all known Acceptors, initiating PAXOS.
   *
   * @throws RemoteException rmi
   */
  void prepare() throws RemoteException;

  /**
   * Receive a promise message from the given Acceptor
   *
   * @param uid of sending Acceptor
   * @param acceptedProposalID id of last accepted proposal
   * @param acceptedOp operation of last accepted proposal
   * @throws RemoteException rmi
   */
  void receivePromise(int uid, int acceptedProposalID, Operation acceptedOp) throws RemoteException;

  /**
   * set the proposal id of the next proposal
   *
   * @param proposalID to set
   * @throws RemoteException rmi
   */
  void setProposalID(int proposalID) throws RemoteException;
}
