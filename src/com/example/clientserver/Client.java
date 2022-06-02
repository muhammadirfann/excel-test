package com.example.clientserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class Client implements Runnable {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void setupConnection(String host, int port) {
        try {
            this.clientSocket = new Socket(host, port);
            this.out = new PrintWriter(this.clientSocket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void sendMessageToServer(String message) {

        long threadId = Thread.currentThread().getId();

        System.out.println("ClientThreadId: " + threadId + ", Message to Server: " + message);
        this.out.println(message);

        try {
            System.out.println("ClientThreadId: " + threadId + ", Server response: " + in.readLine());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void tearDownClient() {
        System.out.println("Tearing down the client - threaId: " + Thread.currentThread().getId());
        try {
            this.in.close();
            this.out.close();
            this.clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        this.setupConnection("localhost", 4567);

        try {
            // send 5 pings
            for(int i=0; i<5; i++) {
                this.sendMessageToServer("ping");
                TimeUnit.MILLISECONDS.sleep(1000);
            }

            // send abort signal
            this.sendMessageToServer("!");
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        this.tearDownClient();
    }
}
