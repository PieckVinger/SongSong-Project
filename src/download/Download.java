package download;

import common.DirectoryService;
import java.io.RandomAccessFile;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class Download {

    public static void main(String[] args) {

        try {

            if (args.length < 1) {
                System.out.println("Usage: java Download <filename>");
                return;
            }

            String filename = args[0];

            // connect to directory
            Registry registry = LocateRegistry.getRegistry("10.0.6.16", 1099);
            DirectoryService directory =
                    (DirectoryService) registry.lookup("DirectoryService");

            List<String> clients = directory.getClients(filename);

            if (clients.isEmpty()) {
                System.out.println("File not found in directory");
                return;
            }

            System.out.println("Sources: " + clients);

            String[] first = clients.get(0).split(":");

            String host = first[0];
            int port = Integer.parseInt(first[1]);

            java.net.Socket socket = new java.net.Socket(host, port);

            java.io.DataOutputStream out = new java.io.DataOutputStream(socket.getOutputStream());
            java.io.DataInputStream in = new java.io.DataInputStream(socket.getInputStream());

            out.writeUTF("SIZE");
            out.writeUTF(filename);

            long fileSize = in.readLong();

            socket.close();

            System.out.println("File size: " + fileSize);

            int numSources = clients.size();

            long fragmentSize = fileSize / numSources;

            RandomAccessFile output = new RandomAccessFile("download_" + filename, "rw");
            output.setLength(fileSize);

            DownloadWorker[] workers = new DownloadWorker[numSources];

            for (int i = 0; i < numSources; i++) {

                String[] parts = clients.get(i).split(":");

                host = parts[0];
                port = Integer.parseInt(parts[1]);

                long start = i * fragmentSize;

                long end = (i == numSources - 1)
                        ? fileSize
                        : start + fragmentSize;

                workers[i] = new DownloadWorker(host, port, filename, start, end, output);

                workers[i].start();
            }

            for (DownloadWorker w : workers) {
                w.join();
            }

            output.close();

            System.out.println("Download completed");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}