package directory;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Directory {

    public static void main(String[] args) {

        try {

            DirectoryServiceImpl service = new DirectoryServiceImpl();

            Registry registry = LocateRegistry.createRegistry(1099);

            registry.rebind("DirectoryService", service);

            System.out.println("Directory RMI Server started");

        } catch (Exception e) {
            System.out.println("Error in file directory/Directory.java");
            e.printStackTrace();
        }

    }
}