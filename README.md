# Simple PAXOS
Project 4 | CS6650 Building Scalable Distributed Systems | Spring 2022 | Northeastern University

_By: Nick Osborn_

This software package implements a replicated fault-tolerant key-value store, achieved through the use of the PAXOS algorithm and Java RMI.

## Quick start

Run the server:
> java -jar ./out/server.jar port

Run the client in a separate terminal:
> java -jar ./out/server.jar hostname port

N.B. If testing this software on a single machine, use `localhost` as the hostname and any later port, I usually use port `32000`.

Once the client completes, end the server process with a keyboard interrupt - `Ctrl-c`.

### Output

Inspect the files generated by each server and the client during the test run: `./(Server/Client)[#].log`.

In the client log, you can view the requests the client made, and to which server those requests were made. In the server log, you can view the requests received by that server from the client, and the requests made between servers during the PAXOS process.


### Periodic Failures

The Acceptors of each server have a one in ten chance of failure every time they receive a request. Acceptor failure is simulated by replacing a servers Acceptor with a new one, with the (clean) default state. As can be seen by a close inspection of the output logs, all state updates requested by the client are replicated across all five PAXOS servers, despite acceptor failure. 