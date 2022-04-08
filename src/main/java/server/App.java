package server;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.exit;

public class App {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: <java -jar or java-cp> [port]");
            exit(1);
        }
        int port = Integer.parseInt(args[0]);
        Registry reg = null;
        try {
            reg = LocateRegistry.createRegistry(port);
        } catch (RemoteException e) {
            e.printStackTrace();
            exit(1);
        }
        List<Server> cluster = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            cluster.add(App.makeServer(reg, i));
        }
        for (int i = 0; i < 5; i++) {
            PaxosServerImpl s = (PaxosServerImpl) cluster.get(i);
            for (int j = 0; j < 5; j++) {
//                if (j == i) continue;
                String otherServer = "Server" + j;
                try {
                    RemoteProposer rp = (RemoteProposer) reg.lookup(otherServer);
                    RemoteLearner rl = (RemoteLearner) reg.lookup(otherServer);
                    RemoteAcceptor ra = (RemoteAcceptor) reg.lookup(otherServer);
                    s.courier.addProposer(j, rp);
                    s.courier.addLearner(j, rl);
                    s.courier.addAcceptor(j, ra);
                } catch (RemoteException | NotBoundException e) {
                    e.printStackTrace();
                    exit(1);
                }
            }
        }
    }

    private static Server makeServer(Registry reg, int id) {
        String name = "Server" + id;
        Server server = new PaxosServerImpl(id, new Courier());
        Server stub = null;
        try {
            stub = (Server) UnicastRemoteObject.exportObject(server, 0);
            reg.rebind(name, stub);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return server;
    }
}
