package com.example.queue;

import java.lang.reflect.Array;

public class SharedQueue <T> {

    private static int DEFAULT_QUEUE_SIZE = 3;

    private int front = -1;
    private int back = -1;
    private int size = 0;
    private final T [] data;

    public SharedQueue(Class<T> clazz) {
        this.data = (T[]) Array.newInstance(clazz, DEFAULT_QUEUE_SIZE);
    }

    public synchronized boolean add(T element) {
        if (this.size >= DEFAULT_QUEUE_SIZE) {
            return false;
        }

        incrementBack();
        data[back] = element;
        this.size++;

        return true;
    }

    public synchronized T remove() {
        if(size <= 0) {
            return null;
        }

        incrementFront();
        T element = data[this.front];
        this.size--;
        return element;
    }

    private void incrementFront() {
        if(shouldResetPointer(this.front)) {
            this.front = 0;  // circle back to 0 if reached end of array
        }
        else {
            this.front++;
        }
    }

    private void incrementBack() {
        if(shouldResetPointer(this.back)) {
            this.back = 0;  // circle back to 0 if reached end of array
        }
        else {
            this.back++;
        }
    }

    private static boolean shouldResetPointer(int pointerPosition) {
        return pointerPosition >= DEFAULT_QUEUE_SIZE-1;
    }
}
