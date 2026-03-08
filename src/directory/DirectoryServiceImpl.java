package directory;

import common.DirectoryService;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.*;

public class DirectoryServiceImpl extends UnicastRemoteObject implements DirectoryService {

    private Map<String, List<String>> directory;

    public DirectoryServiceImpl() throws RemoteException {
        directory = new HashMap<>();
    }

    public synchronized void registerFile(String filename, String clientAddress) throws RemoteException {

        directory.putIfAbsent(filename, new ArrayList<>());
        directory.get(filename).add(clientAddress);

        System.out.println("File registered: " + filename + " from " + clientAddress);
    }

    public synchronized List<String> getClients(String filename) throws RemoteException {

        return directory.getOrDefault(filename, new ArrayList<>());
    }

    public synchronized void removeClient(String clientAddress) throws RemoteException {

        for (List<String> clients : directory.values()) {
            clients.remove(clientAddress);
        }

        System.out.println("Client removed: " + clientAddress);
    }
}