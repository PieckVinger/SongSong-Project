package directory;

import common.DirectoryService;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class DirectoryServiceImpl extends UnicastRemoteObject implements DirectoryService {

    private Map<String, List<String>> directory;

    public DirectoryServiceImpl() throws RemoteException {
        directory = new HashMap<>();
    }

    private boolean isClientAlive(String client) {

        try {

            String[] parts = client.split(":");
            String host = parts[0];
            int port = Integer.parseInt(parts[1]);

            java.net.Socket socket = new java.net.Socket();

            socket.connect(new java.net.InetSocketAddress(host, port), 500);

            socket.close();

            return true;

        } catch (Exception e) {

            return false;

        }
    }

    public synchronized void registerFile(String filename, String clientAddress) throws RemoteException {

        directory.putIfAbsent(filename, new ArrayList<>());
        directory.get(filename).add(clientAddress);

        System.out.println("File registered: " + filename + " from " + clientAddress);
    }

    public synchronized List<String> getClients(String filename) throws RemoteException {

        List<String> clients = directory.getOrDefault(filename, new ArrayList<>());

        Iterator<String> iterator = clients.iterator();

        while (iterator.hasNext()) {

            String client = iterator.next();

            if (!isClientAlive(client)) {

                System.out.println("Removing disconnected client: " + client);

                iterator.remove();
            }
        }

        return new ArrayList<>(clients);
    }

    public synchronized void removeClient(String clientAddress) throws RemoteException {

        for (List<String> clients : directory.values()) {
            clients.remove(clientAddress);
        }

        System.out.println("Client removed: " + clientAddress);
    }
}