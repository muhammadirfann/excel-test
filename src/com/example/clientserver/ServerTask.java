package com.example.clientserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerTask implements Runnable {

    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public ServerTask(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    private void setupConnection() throws IOException {
        this.out = new PrintWriter(this.clientSocket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
    }

    @Override
    public void run() {
        try {
            this.setupConnection();
            String message;

            // in.readLine() is blocking until server receives a message
            while ((message = in.readLine()) != null) {
                boolean quit = processMessage(message);
                if (quit) {
                    break;
                }
            }

            this.teardownConnection();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    private boolean processMessage(String message) {
        System.out.println("ServerThreadID: " + Thread.currentThread().getId() + ", Message from Client: " + message);
        boolean quit = false;
        if ("ping".equalsIgnoreCase(message)) {
            out.println("PONG");
        }
        else if("!".equals(message)) {
            out.println("Abort signal received. Ending session!");
            quit = true;
        }
        else {
            out.println("Unrecognized command!");
        }

        return quit;
    }

    public void teardownConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }
}
