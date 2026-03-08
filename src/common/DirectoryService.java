package common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface DirectoryService extends Remote {

    void registerFile(String filename, String clientAddress) throws RemoteException;

    List<String> getClients(String filename) throws RemoteException;

    void removeClient(String clientAddress) throws RemoteException;
}