package daemon;

import common.DirectoryService;

import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Daemon {

    public static void main(String[] args) {

        int PORT = Integer.parseInt(args[0]);
        String SHARED_FOLDER = args[1];

        try {

            // connect to Directory RMI
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            DirectoryService directory = (DirectoryService) registry.lookup("DirectoryService");

            String clientAddress = java.net.InetAddress.getLocalHost().getHostAddress();

            // scan shared folder
            File folder = new File(SHARED_FOLDER);

            if (!folder.exists()) {
                folder.mkdir();
            }

            File[] files = folder.listFiles();

            if (files != null) {
                for (File file : files) {

                    if (file.isFile()) {

                        directory.registerFile(file.getName(), clientAddress + ":" + PORT);

                        System.out.println("Registered file: " + file.getName());
                    }
                }
            }

            // start TCP server
            ServerSocket serverSocket = new ServerSocket(PORT);

            System.out.println("Daemon running on port " + PORT);

            while (true) {

                Socket socket = serverSocket.accept();

                new ClientHandler(socket, SHARED_FOLDER).start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}