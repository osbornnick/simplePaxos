package server;


import logging.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public class LearnerImpl implements RemoteLearner{

    class Proposal {
        Operation op;
        int acceptCount;
        HashSet<Integer> acceptedBy;

        Proposal(Operation op) {
            this.op = op;
            acceptCount = 0;
            acceptedBy = new HashSet<>();
        }
    }

    private final int uid;
    private final int quorumSize;
    private HashMap<Integer, Proposal> proposals = new HashMap<>();
    private ConcurrentHashMap<String, String> dataStore;
    Logger logger;

    LearnerImpl(int id, Logger logger, int quorumSize, ConcurrentHashMap<String, String> dataStore) {
        this.uid = id;
        this.quorumSize = quorumSize;
        this.dataStore = dataStore;
        this.logger = logger;
    }

    @Override
    public void receiveAccepted(int fromUID, int proposalID, Operation op) {
        Proposal proposal;
        if (proposals.containsKey(proposalID)) proposal = proposals.get(proposalID);
        else {
            proposal = new Proposal(op);
            proposals.put(proposalID, proposal);
        };

        if (!proposal.acceptedBy.contains(fromUID)) {
            proposal.acceptCount++;
            proposal.acceptedBy.add(fromUID);
        }

        if (proposal.acceptCount == quorumSize) {
            logger.log("Learning proposal %d", proposalID);
            op.run(this.dataStore);
            proposals.clear(); // is this necessary?
        };
    }

}
