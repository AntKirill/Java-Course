package ru.ifmo.ctddev.antonov.concurrent;

/**
 * Keeps queue that able to perform concurrent operations.
 */
public class QueueKeeper implements Runnable {


    private final ConcurrentQueue queue;

    /**
     * Creates new QueueKeeper
     * @param appliers
     */
    public QueueKeeper(ConcurrentQueue appliers) {
        queue = appliers;
    }

    /**
     * Start performing tasks.
     */
    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                queue.get().go();
            }
        } catch (InterruptedException e) {
            //
        }

    }
}
