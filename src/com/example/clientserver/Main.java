package com.example.clientserver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String [] args) {
        List<Runnable> clientThreads = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            clientThreads.add(new Client());
        }

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        clientThreads.forEach(client -> executorService.submit(client));
    }
}
