package ru.ifmo.ctddev.antonov.concurrent;

public class QueueKeeper implements Runnable {

    private final ConcurrentQueue queue;

    public QueueKeeper(ConcurrentQueue appliers) {
        queue = appliers;
    }

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
