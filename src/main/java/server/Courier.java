package server;

import java.rmi.RemoteException;
import java.util.HashMap;


/**
 * Handles groups of servers, sending messages between them
 */
public class Courier {

    HashMap<Integer, RemoteProposer> proposers;
    HashMap<Integer, RemoteAcceptor> acceptors;
    HashMap<Integer, RemoteLearner> learners;

    Courier() {
        this.proposers = new HashMap<>();
        this.acceptors = new HashMap<>();
        this.learners = new HashMap<>();
    }

    public void addProposer(int uid, RemoteProposer p) {
        this.proposers.put(uid, p);
    }

    public void addAcceptor(int uid, RemoteAcceptor a) {
        this.acceptors.put(uid, a);
    }

    public void addLearner(int uid, RemoteLearner l) {
        this.learners.put(uid, l);
    }

    void sendPrepare(int fromUID, int proposalID) {
        this.acceptors.forEach((i, a) -> {
            try {
                a.receivePrepare(fromUID, proposalID);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }

    void sendPromise(int toUID, int fromUID, int acceptedProposalID, Operation acceptedOp) {
        RemoteProposer rp = this.proposers.get(toUID);
        try {
            rp.receivePromise(fromUID, acceptedProposalID, acceptedOp);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }
    void sendAccept(int proposalID, Operation op) {
        this.acceptors.forEach((i, a) -> {
            try {
                a.receiveAcceptRequest(proposalID, op);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }

    void sendAccepted(int fromUID, int proposalID, Operation op) {
        this.learners.forEach((i, l) -> {
            try {
                l.receiveAccepted(fromUID, proposalID, op);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }

    void updateGlobalProposal(int uidToUpdate, int proposalID) {
        try {
            this.proposers.get(uidToUpdate).setProposalID(proposalID);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
