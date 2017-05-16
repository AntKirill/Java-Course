package ru.ifmo.ctddev.antonov.hello;

import info.kgeorgiy.java.advanced.hello.HelloClient;

import java.io.IOException;
import java.net.*;
import java.nio.charset.Charset;

public class HelloUDPClient implements HelloClient {
    private final Integer timeOut = 200;

    /**
     * main method to start client.
     * @param args - arguments of client.
     */
    public static void main(String args[]) {
        if (args.length != 5 ) {
            System.out.println("There must be 5 arguments");
            return;
        }
        new HelloUDPClient().run(args[0], Integer.parseInt(args[1]), args[2], Integer.parseInt(args[3]), Integer.parseInt(args[4]));
    }

    /**
     * Invokes client who sending some request and waiting for answer that contains this request with prefix "Hello, ".
     * @param host - servers host
     * @param port - servers port
     * @param prefix - prefix for each request
     * @param requests - at each thread client will send {@code requests} number of requests
     * @param threads - amount of working threads for client
     */
    @Override
    public void run(String host, int port, String prefix, int requests, int threads) {
        Charset charset = Charset.forName("UTF-8");

        final InetAddress address;
        try {
            address = InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            System.out.print("Unknown host exception faced while getting server address\n");
            e.printStackTrace();
            return;
        }

        Thread threadPools[] = new Thread[threads];

        for (Integer i = 0; i < threads; i++) {
            String queryText = prefix.concat(i.toString()).concat("_");
            threadPools[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    byte[] forRec;
                    DatagramSocket socket;
                    try{
                        socket = new DatagramSocket();
                        socket.setSoTimeout(timeOut);
                        forRec = new byte[socket.getReceiveBufferSize()];
                    } catch (SocketException e) {
                        System.out.println("Can not create socket!");
                        e.printStackTrace();
                        return;
                    }

                    for (Integer j = 0; j < requests; j++) {
                        String curQueryText = queryText.concat(j.toString());
                        byte[] forSnd = curQueryText.getBytes(charset);
                        try {
                            socket.send(new DatagramPacket(forSnd, forSnd.length, address, port));
                        } catch (IOException e) {
                            System.out.println("Data can not be sent via this socket");
                            return;
                        }
                        DatagramPacket serversAnswerInPacket = new DatagramPacket(forRec, forRec.length);
                        try {
                            socket.receive(serversAnswerInPacket);
                        } catch (IOException e) {
                            //System.out.println("rofl");
                            --j;
                            continue;
                        }

                        String answer = new String(serversAnswerInPacket.getData(), 0, serversAnswerInPacket.getLength());
                        System.out.println(answer);
                        if (!answer.contains(curQueryText)) {
                            --j;
                        }
                    }
                }
            });
            threadPools[i].start();
        }

        try {
            for (int i = 0; i< threads; i++) {
                threadPools[i].join();
            }
        } catch (InterruptedException e) {
            for (int i = 0; i < threads; i++) {
                threadPools[i].interrupt();
            }
        }
    }
}