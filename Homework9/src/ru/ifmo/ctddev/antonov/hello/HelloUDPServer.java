package ru.ifmo.ctddev.antonov.hello;

import info.kgeorgiy.java.advanced.hello.HelloServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HelloUDPServer implements HelloServer {

    /**
     * Main method to run server.
     * @param args - arguments of server.
     */
    public static void main(String args[]) {
        if (args.length != 2) {
            System.out.print("Run with 2 arguments");
            return;
        }
        new HelloUDPServer().start(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
    }

    private static final Charset charset = Charset.forName("UTF-8");
    private DatagramSocket socket;
    private ExecutorService threadPool;
    private volatile boolean isRunnig = true;

    /**
     * Invokes Server.
     * @param port - port to take queries.
     * @param threads - max amount of working threads for server.
     */
    @Override
    public void start(int port, int threads) {
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            //System.out.print("Can not create socket!\n");
            //System.out.println(e.getMessage());
        }
        threadPool = Executors.newFixedThreadPool(threads);

        for (int i = 0; i < threads; i++) {
            threadPool.submit(() -> {
                while (isRunnig) {
                    DatagramPacket query = null;
                    try {
                        query = new DatagramPacket((new byte[socket.getReceiveBufferSize()]), socket.getReceiveBufferSize());
                    } catch (SocketException e) {
                        //System.out.println("Unable to get query!");
                        //System.out.println(e.getMessage());
                    }

                    try {
                        socket.receive(query);
                        byte[] resp = ("Hello, ".concat(new String(query.getData(), 0, query.getLength(), charset))).getBytes(charset);
                        DatagramPacket response = new DatagramPacket(resp, resp.length, query.getAddress(), query.getPort());
                        socket.send(response);
                    } catch (IOException e) {
                        //e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * Stop server.
     * This method interrupts the work of the server.
     */
    @Override
    public void close() {
        synchronized (this) {
            isRunnig = false;
            socket.close();
            threadPool.shutdown();
        }
    }
}