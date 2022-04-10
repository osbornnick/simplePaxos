package server;

import logging.Logger;

import java.util.HashSet;

public class ProposerImpl implements RemoteProposer {
  private final int uid;
  int proposalID = 0;
  private final int quorumSize;
  HashSet<Integer> receivedPromises = new HashSet<>();
  Operation proposal;
  Courier courier;
  Logger logger;

  ProposerImpl(int uid, Logger logger, Courier courier, int quorumSize) {
    this.uid = uid;
    this.courier = courier;
    this.quorumSize = quorumSize;
    this.logger = logger;
  }

  @Override
  public void setProposal(Operation op) {
    this.proposal = op;
  }

  @Override
  public void setProposalID(int id) {
    this.proposalID = id;
  }

  @Override
  public void prepare() {
    receivedPromises.clear();
    logger.log("sending prepare for proposal %d", proposalID + 1);
    courier.sendPrepare(uid, ++proposalID);
  }

  @Override
  public void receivePromise(int fromUID, int acceptedProposalID, Operation acceptedOp) {
    if (receivedPromises.contains(fromUID)) return;
    if (acceptedProposalID > proposalID) {
      this.proposal = acceptedOp;
    }
    this.receivedPromises.add(fromUID);

    if (receivedPromises.size() == quorumSize) {
      logger.log("Quorum reached, sending accept for %d", proposalID);
      courier.sendAccept(this.proposalID, this.proposal);
    }
  }
}
