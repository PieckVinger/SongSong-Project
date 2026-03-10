package daemon;

import java.io.*;
import java.net.Socket;

public class ClientHandler extends Thread {

    private Socket socket;
    private String folder;

    public ClientHandler(Socket socket, String folder) {
        this.socket = socket;
        this.folder = folder;
    }

    public void run() {

        try {

            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            String command = in.readUTF();

            if (command.equals("SIZE")) {

                String filename = in.readUTF();

                File file = new File(folder + File.separator + filename);

                out.writeLong(file.length());

            } else if (command.equals("GET")) {

                String filename = in.readUTF();
                long start = in.readLong();
                long end = in.readLong();

                File file = new File(folder + File.separator + filename);

                RandomAccessFile raf = new RandomAccessFile(file, "r");

                raf.seek(start);

                byte[] buffer = new byte[4096];
                long bytesToSend = end - start;

                while (bytesToSend > 0) {

                    int read = raf.read(buffer, 0, (int)Math.min(buffer.length, bytesToSend));

                    if (read == -1)
                        break;

                    out.write(buffer, 0, read);

                    bytesToSend -= read;
                }

                raf.close();
            }

            socket.close();

        } catch (Exception e) {
            System.out.println("Error in file daemon/ClientHandler.java");
            e.printStackTrace();
        }
    }
}