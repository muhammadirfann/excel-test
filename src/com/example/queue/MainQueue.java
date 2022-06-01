package com.example.queue;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainQueue {

//    How is synchronization between the readers and the writer implemented
//
//    Whether multiple readers can wait for the next message without blocking each other access
//
//    If synchronization is used, how the time spent in a locked condition is minimized



    public static void main(String [] args) {
        Queue<String> s;

        SharedQueue<String> sq = new SharedQueue<>(String.class);

//        for (int i=1; i<=5; i++) {
//            if (i == 2) {
//                sq.remove();
//            }
//            System.out.println("Thread: " + Thread.currentThread().getId()
//                    + ", Item added: " + sq.add("El_" + i));
//        }

        List<Runnable> allTasks = new ArrayList<>();
        allTasks.add(addElementTask(sq));

        for(int i=0; i<5; i++) {
            allTasks.add(readElementTask(sq));
        }

        ExecutorService executorService = Executors.newFixedThreadPool(6);
        for(Runnable task: allTasks) {
            executorService.submit(task);
        }

        executorService.shutdown();
    }

    // Once kicked off, this thread will add 50 messages in the queue, at X intervals
    private static Runnable addElementTask(SharedQueue sharedQueue) {
        return () -> {

            for(int i=0; i<50; i++) {
                System.out.println("ThreadID: " + Thread.currentThread().getId() + ", Item Added? " + sharedQueue.add("El_" + i));
                try {
                    TimeUnit.MILLISECONDS.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    // Once kicked off, this thread will read 10 messages from the queue, at Y intervals
    private static Runnable readElementTask(SharedQueue sharedQueue) {
        return () -> {
            for(int i=0; i<10; i++) {
                System.out.println("ThreadID: " + Thread.currentThread().getId() + ", Item Read: " + sharedQueue.remove());
                try {
                    TimeUnit.MILLISECONDS.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
}
