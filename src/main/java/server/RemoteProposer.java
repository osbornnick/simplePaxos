package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Performs the Proposer role of the PAXOS algorithm
 */
public interface RemoteProposer extends Remote {

    public void setProposal(Operation op) throws RemoteException;

    public void prepare() throws RemoteException;

    public void receivePromise(int uid, int acceptedProposalID,  Operation acceptedOp) throws RemoteException;

    void setProposalID(int proposalID) throws RemoteException;
}
