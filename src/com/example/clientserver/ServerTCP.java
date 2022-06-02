package com.example.clientserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    /**
     * ThreadPoolExecutor to handle 10 clients at a time
     */
    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    public void startServer(int port) {
        try {
            this.serverSocket = new ServerSocket(port);

            while(true) {  // keep the server up endlessly so it can listen to requests
                // blocking call until a client opens a connection to this Server
                Socket clientSocket = this.serverSocket.accept();

                // Once the connection is made, submit it to a new thread for processing
                executorService.submit(new ServerTask(clientSocket));
            }

//            this.stop();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    private void stop() throws IOException {
        this.executorService.shutdown();
        this.serverSocket.close();
    }

    public static void main(String [] args) {
        ServerTCP server = new ServerTCP();
        server.startServer(4567);
    }

}
