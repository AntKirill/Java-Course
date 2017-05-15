package ru.ifmo.ctddev.antonov.hello;


import info.kgeorgiy.java.advanced.hello.HelloClient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.charset.Charset;

/**
 * Simple implementation of UDP client.
 */
public class HelloUDPClient implements HelloClient {

    private static final Charset CHARSET = Charset.forName("UTF-8");
    private static final String HELLO = "Hello, ";

    /**
     * Starts simple UDP client. Sends in {@code threadNUm} threads {@code requestNum} requests per thread to the
     * {@code host} to the port with number {@code port}. Query is built like
     * {@code prefix + currentThread + "_" + currentQuery}.
     * @param host server host name or IP-address where to send
     * @param port server port where to send
     * @param prefix prefix for query
     * @param requestNum number of requests per thread
     * @param threadNum number of threads to use
     */
    @Override
    public void run(String host, int port, String prefix, int requestNum, int threadNum) {
        InetSocketAddress serverAddress = new InetSocketAddress(host, port);
        Thread pool[] = new Thread[threadNum];
        for (int i = 0; i < threadNum; ++i) {
            final int finalI = i;
            pool[i] = new Thread(() -> {
                try (DatagramSocket ds = new DatagramSocket(null)) {
                    ds.setSoTimeout(50);
                    for (int j = 0; j < requestNum && !Thread.interrupted(); j++) {
                        String query = prefix + finalI + "_" + j;
                        byte[] bQuery = query.getBytes(CHARSET);
                        byte[] bAnswer = new byte[ds.getReceiveBufferSize()];
                        DatagramPacket packetOut = new DatagramPacket(bQuery, bQuery.length, serverAddress);
                        DatagramPacket packetIn = new DatagramPacket(bAnswer, bAnswer.length);
                        while (true) {
                            try {
                                ds.send(packetOut);
                                ds.receive(packetIn);
                                String s = new String(packetIn.getData(), 0, packetIn.getLength());
                                if (s.equals(HELLO + query)) {
                                    System.out.println(s);
                                    break;
                                }
                            } catch (IOException ignored) {
                            }
                        }
                    }
                } catch (SocketException e) {
                    System.out.println("Couldn't create connection " + e.getMessage());
                }
            });
            pool[i].start();
        }
        try {
            for (Thread t : pool) {
                t.join();
            }
        } catch (InterruptedException e) {
            for (Thread t : pool) {
                t.interrupt();
            }
        }

    }

    /**
     * Calls {@link #run(String, int, String, int, int)}
     * @param args args for {@link #run(String, int, String, int, int)}
     */
    public static void main(String[] args) {
        if (args.length < 5) {
            System.out.println("Usage -- HelloUDPClient hostName portNum queryPrefix numberOfThreads numberOfQueries");
        }
        try {
            new HelloUDPClient().run(args[0], Integer.parseInt(args[1]), args[2],
                    Integer.parseInt(args[3]), Integer.parseInt(args[4]));
        } catch (NumberFormatException e) {
            System.out.println("Couldn't parse integer " + e.getLocalizedMessage());
        }
    }
}