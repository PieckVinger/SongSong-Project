package download;

import java.io.*;
import java.net.Socket;

public class DownloadWorker extends Thread {

    private String host;
    private int port;
    private String filename;
    private long start;
    private long end;
    private RandomAccessFile output;

    public boolean failed = false;

    public DownloadWorker(String host, int port, String filename,
                          long start, long end, RandomAccessFile output) {

        this.host = host;
        this.port = port;
        this.filename = filename;
        this.start = start;
        this.end = end;
        this.output = output;
    }

    public void run() {

        try {

            Socket socket = new Socket(host, port);

            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());

            out.writeUTF("GET");
            out.writeUTF(filename);
            out.writeLong(start);
            out.writeLong(end);

            byte[] buffer = new byte[4096];

            long bytesToRead = end - start;
            long position = start;

            while (bytesToRead > 0) {

                int read = in.read(buffer, 0, (int)Math.min(buffer.length, bytesToRead));

                if (read == -1)
                    break;

                synchronized (output) {

                    output.seek(position);
                    output.write(buffer, 0, read);
                }

                position += read;
                bytesToRead -= read;
            }

            socket.close();

            System.out.println("Fragment " + start + " - " + end + " downloaded from " + host + ":" + port);

        } catch (Exception e) {
            System.out.println("Error in file download/DownloadWorker.java");

            failed = true;

            System.out.println("Source failed: " + host + ":" + port);
            System.out.println("Fragment " + start + " - " + end + " not downloaded");

        }
    }
}