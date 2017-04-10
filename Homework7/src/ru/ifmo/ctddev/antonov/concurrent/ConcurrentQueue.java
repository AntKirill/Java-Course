package ru.ifmo.ctddev.antonov.concurrent;

import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.Queue;

public class ConcurrentQueue {
    private final Queue<Task<?, ?>> taskQueue;

    public ConcurrentQueue() {
        this.taskQueue = new ArrayDeque<>();
    }

    public synchronized void add(Task<?, ?> ap) {
        taskQueue.add(ap);
        notifyAll();
    }

    public synchronized Task<?, ?> get() throws InterruptedException {
        while (taskQueue.isEmpty()) {
            wait();
        }
        return taskQueue.poll();
    }
}
