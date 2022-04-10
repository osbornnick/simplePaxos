package server;


import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentHashMap;
import logging.Logger;

public class PaxosServerImpl implements Server, RemoteProposer, RemoteAcceptor, RemoteLearner {

    int serverID;
    Courier courier;
    ConcurrentHashMap<String, String> map;
    RemoteProposer proposer;
    RemoteAcceptor acceptor;
    RemoteLearner learner;
    private final Logger logger;

    PaxosServerImpl(int id, Courier courier) {
        this.serverID = id;
        this.courier = courier;
        this.map = new ConcurrentHashMap<>();
        logger = new Logger(PaxosServerImpl.class.getName() + id);
        this.proposer = new ProposerImpl(id, logger, courier, 3);
        this.acceptor = new AcceptorImpl(id, logger, courier);
        this.learner = new LearnerImpl(id, logger, 3, map);
    }

    @Override
    public String get(String key) throws RemoteException {
        logger.log("REQUEST: GET %s", key);
        return map.get(key);
    }

    @Override
    synchronized public boolean put(String key, String value) throws RemoteException {
        logger.log("REQUEST: PUT %s %s", key, value);
        Operation op = (Operation & Serializable)(map) -> {
            map.put(key, value);
            return true;
        };
        this.setProposal(op);
        this.prepare();
        return true;
    }

    @Override
    synchronized public boolean delete(String key) throws RemoteException {
        logger.log("REQUEST: DELETE %s", key);
        Operation op = (Operation & Serializable)(map) -> {
            map.remove(key);
            return true;
        };
        this.setProposal(op);
        this.prepare();
        return true;
    }

    @Override
    public void setProposal(Operation op) throws RemoteException {
        this.proposer.setProposal(op);
    }

    @Override
    public void prepare() throws RemoteException {
        this.proposer.prepare();
    }

    @Override
    public void receivePromise(int uid, int acceptedProposalID,  Operation acceptedOp) throws RemoteException {
        logger.log("received promise from %d with acceptedProposalID %d", uid, acceptedProposalID);
        this.proposer.receivePromise(uid, acceptedProposalID, acceptedOp);
    }

    @Override
    public void setProposalID(int proposalID) throws RemoteException {
        this.proposer.setProposalID(proposalID);
    }

    @Override
    public void receivePrepare(int fromUID, int proposal) throws RemoteException {
        this.failAcceptor();
        this.acceptor.receivePrepare(fromUID, proposal);
    }

    @Override
    public void receiveAcceptRequest(int proposal, Operation op) throws RemoteException {
        logger.log("Acceptor receiving for proposal %d", proposal);
        this.failAcceptor();
        this.acceptor.receiveAcceptRequest(proposal, op);
    }

    @Override
    public void receiveAccepted(int uid, int proposal, Operation op) throws RemoteException {
        logger.log("Learner receiving from %s for proposal %d", uid, proposal);
        this.learner.receiveAccepted(uid, proposal, op);
    }

    /**
     * with a 1/10 chance, restart this servers acceptor
     */
    private void failAcceptor() {
        if (Math.random() < 0.1) {
            logger.log("Pseudo-failing Acceptor");
            this.acceptor = new AcceptorImpl(this.serverID, logger, courier);
        }
    }
}
