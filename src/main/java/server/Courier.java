package server;

import java.rmi.RemoteException;
import java.util.HashMap;

/** Handles groups of servers, sending messages between them */
public class Courier {

  HashMap<Integer, RemoteProposer> proposers;
  HashMap<Integer, RemoteAcceptor> acceptors;
  HashMap<Integer, RemoteLearner> learners;

  /** Initialize a courier with empty lists of proposers, acceptors, and learners */
  Courier() {
    this.proposers = new HashMap<>();
    this.acceptors = new HashMap<>();
    this.learners = new HashMap<>();
  }

  /**
   * Add a RemoteProposer with supplied uid to this couriers list of proposer addresses
   *
   * @param uid uid of proposer to add
   * @param p the proposer to add (an RMI stub)
   */
  public void addProposer(int uid, RemoteProposer p) {
    this.proposers.put(uid, p);
  }

  /**
   * Add a RemoteAcceptor with supplied uid to this couriers list of acceptors
   *
   * @param uid of acceptor
   * @param a the acceptor to add (an RMI stub)
   */
  public void addAcceptor(int uid, RemoteAcceptor a) {
    this.acceptors.put(uid, a);
  }

  /**
   * Add a RemoteLearner with supplied uid to this couriers list of learners
   *
   * @param uid of learner
   * @param l the learner to add (an RMI stub)
   */
  public void addLearner(int uid, RemoteLearner l) {
    this.learners.put(uid, l);
  }

  /**
   * Send a Prepare message to all known RemoteAcceptors
   *
   * @param fromUID uid this Prepare message is from
   * @param proposalID of this associated proposal
   */
  void sendPrepare(int fromUID, int proposalID) {
    this.acceptors.forEach(
        (i, a) -> {
          try {
            a.receivePrepare(fromUID, proposalID);
          } catch (RemoteException e) {
            e.printStackTrace();
          }
        });
  }

  /**
   * Send a Promise message to the given proposer
   *
   * @param toUID uid of proposer to send promise too
   * @param fromUID uid of acceptor sending the promise
   * @param acceptedProposalID the id of the acceptors accepted proposal
   * @param acceptedOp the operation associated with the acceptors accepted proposal
   */
  void sendPromise(int toUID, int fromUID, int acceptedProposalID, Operation acceptedOp) {
    RemoteProposer rp = this.proposers.get(toUID);
    try {
      rp.receivePromise(fromUID, acceptedProposalID, acceptedOp);
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }

  /**
   * Send an Accept message to each registered Acceptor
   *
   * @param proposalID id of proposal being accepted
   * @param op operation of proposal being accepted
   */
  void sendAccept(int proposalID, Operation op) {
    this.acceptors.forEach(
        (i, a) -> {
          try {
            a.receiveAcceptRequest(proposalID, op);
          } catch (RemoteException e) {
            e.printStackTrace();
          }
        });
  }

  /**
   * Send an accepted message to each registered learner
   *
   * @param fromUID the uid of the acceptor sending the message
   * @param proposalID the proposalID of the accepted proposal
   * @param op the accepted operation to apply
   */
  void sendAccepted(int fromUID, int proposalID, Operation op) {
    this.learners.forEach(
        (i, l) -> {
          try {
            l.receiveAccepted(fromUID, proposalID, op);
          } catch (RemoteException e) {
            e.printStackTrace();
          }
        });
  }

  /**
   * Update the global proposalID of the specified proposer (when higher ones are seen by acceptors)
   *
   * @param uidToUpdate uid of proposer to update
   * @param proposalID to set the proposalid of next proposal
   */
  void updateGlobalProposal(int uidToUpdate, int proposalID) {
    try {
      this.proposers.get(uidToUpdate).setProposalID(proposalID);
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }
}
