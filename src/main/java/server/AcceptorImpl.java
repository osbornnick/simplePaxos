package server;

import logging.Logger;

public class AcceptorImpl implements RemoteAcceptor {

  private final int uid;
  Courier courier;
  int promisedID = -1;
  int acceptedID = -1;
  Operation acceptedOp = null;
  private Logger logger;

  AcceptorImpl(int uid, Logger logger, Courier courier) {
    this.uid = uid;
    this.courier = courier;
    this.logger = logger;
  }

  @Override
  public void receivePrepare(int fromUID, int proposalID) {
    logger.log("Received prepare from %d with proposal id %d", fromUID, proposalID);
    logger.log("Comparing proposalID %d to promised ID %d", proposalID, promisedID);
    if (proposalID <= promisedID) return;
    promisedID = proposalID;
    courier.updateGlobalProposal(uid, promisedID);
    logger.log("Promising proposal %d to %d", promisedID, fromUID);
    courier.sendPromise(fromUID, uid, acceptedID, acceptedOp);
  }

  @Override
  public void receiveAcceptRequest(int proposalID, Operation op) {
    if (proposalID >= promisedID) {
      promisedID = proposalID;
      acceptedID = proposalID;
      acceptedOp = op;
      logger.log("Accepting proposal %d", acceptedID);
      courier.updateGlobalProposal(uid, acceptedID);
      courier.sendAccepted(uid, acceptedID, acceptedOp);
    }
  }
}
