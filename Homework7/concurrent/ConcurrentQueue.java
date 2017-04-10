package ru.ifmo.ctddev.antonov.concurrent;

import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Class that contains queue for cuncurrent operations.
 */
public class ConcurrentQueue {

    private final Queue<Task<?, ?>> taskQueue;

    /**
     * Constructs queue for keeping elements.
     */
    public ConcurrentQueue() {
        this.taskQueue = new ArrayDeque<>();
    }

    /**
     * Add task to queue. Queue is locked in the process of adding
     * @param ap - another task
     */
    public 
    synchronized void add(Task<?, ?> ap) {
        taskQueue.add(ap);
        notifyAll();
    }

    /**
     * Get task to perform.
     * @return task to perform
     * @throws InterruptedException
     */
    public synchronized Task<?, ?> get() throws InterruptedException {
        while (taskQueue.isEmpty()) {
            wait();
        }
        return taskQueue.poll();
    }
}
