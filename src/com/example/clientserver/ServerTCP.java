package com.example.clientserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * HTTP Servers are generally more complex as the HTTP protocol contain instructions on
 * how to process the data once it's received
 *
 * TCP is connection-oriented (hence reliable) light-weight protocol for implementing
 * simple server programs
 *
 *
 */
public class ServerTCP {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void startServer(int port) {
        try {
            this.serverSocket = new ServerSocket(port);

            // blocking call until a client opens a connection to this Server
            this.clientSocket = this.serverSocket.accept();
            System.out.println("Client connected");

            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String message;

            // in.readLine() is blocking until server receives a message
            while ((message = in.readLine()) != null) {
                boolean quit = processMessage(message);
                if (quit) {
                    break;
                }
            }

            this.stop();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private boolean processMessage(String message) {
        System.out.println("Message received: " + message);
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

    public void stop() {
        try {
            in.close();
            out.close();
            clientSocket.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static void main(String [] args) {
        ServerTCP server = new ServerTCP();
        server.startServer(4567);
    }

}
