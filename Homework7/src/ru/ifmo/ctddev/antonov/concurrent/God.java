package ru.ifmo.ctddev.antonov.concurrent;

/**
 * Created by kirill on 3/27/17.
 */
public class God implements Runnable {

    private final AppliersQueue queue;

    public God(AppliersQueue appliers) {
        queue = appliers;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                queue.get().run();
            }
        } catch (InterruptedException e) {
            //
        }

    }
}
