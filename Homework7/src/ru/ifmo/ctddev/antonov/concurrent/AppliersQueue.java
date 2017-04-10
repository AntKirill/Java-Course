package ru.ifmo.ctddev.antonov.concurrent;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Created by kirill on 3/27/17.
 */
public class AppliersQueue {
    private final Queue<Applier<?, ?>> taskQueue;

    public AppliersQueue() {
        this.taskQueue = new ArrayDeque<>();
    }

    public synchronized void add(Applier<?, ?> ap) {
        taskQueue.add(ap);
    }

    public synchronized Applier<?, ?> get() throws InterruptedException {
        while (taskQueue.isEmpty()) {
            wait();
        }
        return taskQueue.poll();
    }
}
