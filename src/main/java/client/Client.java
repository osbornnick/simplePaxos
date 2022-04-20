package client;

import logging.Logger;
import server.Server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;

public class Client {
    private static Logger logger;

    public Client() {
        logger = new Logger(Client.class.getName());
    }

    /**
     * run the client application
     * @param args command line args. expects one - the server hostname
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -cp ./out client.Client <hostname> <port>");
            System.exit(1);
        }
        logger = new Logger(Client.class.getName());
        ArrayList<Server> servers = new ArrayList<Server>();
        int port = Integer.parseInt(args[1]);
        try {
            Registry reg = LocateRegistry.getRegistry(args[0], port);
            for(int i = 0; i < 5; i++) {
                String name = "Server" + i;
                Server server = (Server) reg.lookup(name);
                servers.add(server);
            }
        } catch (Exception e) {
            System.out.printf("Could not connect to servers, are they running? error: %s%n", e.getMessage());
            System.exit(1);
        }

        prepopulate(servers);
        test(servers);
        System.out.println("Pre-population & test operations run, entering interactive mode...");
        try {
            interactive(servers);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private static int randomInt() {
        return (int) Math.round(Math.random() * 4);
    }

    /**
     * perform 15 GET/PUT/DELETE operations in the given order
     * @param servers to execute the operations on.
     */
    public static void test(ArrayList<Server> servers) {
        Iterator intIterator = new Iterator<Integer>() {
            int count = 0;
            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public Integer next() {
                int cur = count;
                count = (count + 1) % 5;
                return cur;
            }
        };
        String[][] requests = {
                {"DELETE", "Actor"},
                {"DELETE", "Airport"},
                {"PUT", "Airport", "Horse"},
                {"PUT", "Australia", "Honey"},
                {"PUT", "Banana", "Hospital"},
                {"GET", "Banana"},
                {"PUT", "Banana", "House"},
                {"GET", "Banana"},
                {"GET", "Animal"},
                {"DELETE", "Ambulance"},
                {"PUT", "NOT PRESENT", "Hydrogen"},
                {"GET", "NOT PRESENT"},
                {"DELETE", "NOT PRESENT"},
                {"GET", "NOT PRESENT"},
                {"DELETE", "Ambulance"},
                {"GET", "Australia"},
        };
        Arrays.stream(requests)
                .forEach(
                        req -> {
                            try {
                                int serverId = (int) intIterator.next();
                                switch (req[0]) {
                                    case "DELETE":
                                        logger.log("REQUESTING SERVER %d DELETE %s", serverId, req[1]);
                                        logger.log("Response %s", servers.get(serverId).delete(req[1]));
                                        break;
                                    case "PUT":
                                        logger.log("REQUESTING SERVER %d PUT %s %s", serverId, req[1], req[2]);
                                        logger.log("Response %s", servers.get(serverId).put(req[1], req[2]));
                                        break;
                                    case "GET":
                                        logger.log("REQUESTING SERVER %d GET %s", serverId, req[1]);
                                        logger.log("Response %s", servers.get(serverId).get(req[1]));
                                        break;
                                }
                            } catch (RemoteException e) {
                                logger.log(e.getMessage());
                            }
                        });
    }

    /**
     * prepopulate the server with some key-value paris
     * @param servers to populate
     */
    private static void prepopulate(ArrayList<Server> servers) {
        String[][] data = {
                {"Actor", "Gold"},
                {"Advertisement", "Grass"},
                {"Afternoon", "Greece"},
                {"Airport", "Guitar"},
                {"Ambulance", "Hair"},
                {"Animal", "Hamburger"},
                {"Answer", "Helicopter"},
                {"Apple", "Helmet"},
                {"Army", "Holiday"},
        };
        Arrays.stream(data)
                .forEach(
                        (pair) -> {
                            int serverId = Client.randomInt();
                            try {
                                logger.log("REQUESTING SERVER %d PUT %s %s", serverId, pair[0], pair[1]);
                                logger.log("Response: %s", servers.get(serverId).put(pair[0], pair[1]));
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        });
    }

    private static void interactive(ArrayList<Server> servers) throws RemoteException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("--Interactive mode--");
        System.out.printf("Instructions: %nEnter one of the below commands. A server will be chosen randomly to respond to your request. All key/values are case-sensitive%n");
        System.out.printf("Command options:%n");
        printCommand("GET [key]", "get the value for an associated key from the replicated store");
        printCommand("PUT [key] [value]", "put a key value pair into the replicated store");
        printCommand("DELETE [key]", "delete a key value pair from the replicated store");
        System.out.println("--");
        System.out.print("> ");
        String cmd = "";
        int choice;
        while (!Objects.equals(cmd = scanner.nextLine(), "STOP")) {
            String[] cmdParts = cmd.split(" ");
            if (cmdParts.length < 1) {
                System.out.println("Please enter a command");
            } else {
                switch (cmdParts[0].toUpperCase()) {
                    case "STOP":
                        return;
                    case "GET":
                        if (cmdParts.length != 2) System.out.println("incorrect number of arguments, correct syntax is GET [key]");
                        else {
                            choice = new Random().nextInt(servers.size());
                            Server s = servers.get(choice);
                            System.out.printf("Issuing request %s to server %d%n", Arrays.toString(cmdParts), choice);
                            String response = s.get(cmdParts[1]);
                            System.out.printf("Response: %s%n", response);
                        }
                        break;
                    case "PUT":
                        if (cmdParts.length != 3) System.out.println("incorrect number of arguments, correct syntax is PUT [key] [value]");
                        else {
                            choice = new Random().nextInt(servers.size());
                            Server s = servers.get(choice);
                            System.out.printf("Issuing request %s to server %d%n", Arrays.toString(cmdParts), choice);
                            System.out.printf("Response: %s%n", s.put(cmdParts[1], cmdParts[2]));
                        }
                        break;
                    case "DELETE":
                        if (cmdParts.length != 2) System.out.println("incorrect number of arguments, correct syntax is DELETE [key]");
                        else {
                            choice = new Random().nextInt(servers.size());
                            Server s = servers.get(choice);
                            System.out.printf("Issuing request %s to server %d%n", Arrays.toString(cmdParts), choice);
                            System.out.printf("Response: %s%n", s.delete(cmdParts[1]));
                        }
                        break;
                    default:
                        System.out.printf("Unrecognized command '%s', recognized commands are STOP PUT GET DELETE%n", cmdParts[0]);
                }
            }
            System.out.print("> ");
        }
    }

    private static void printCommand(String cmd, String details) {
        System.out.printf("%-20s %s%n", cmd, details);
    }
}
