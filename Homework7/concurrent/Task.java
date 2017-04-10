package ru.ifmo.ctddev.antonov.concurrent;

import java.util.List;
import java.util.function.Function;

/**
 * Next task to perform.
 * @param <T> type of element that we want to apply function to.
 * @param <R> type of result.
 */
public class Task <T, R>  {

    private Function<? super T, ? extends R> func;
    private T l;
    private R result = null;

    /**
     * Create another task.
     * @param func function that we want to apply to argument
     * @param l argument
     */
    public  Task(Function<? super T,? extends R> func, T l) {
        this.func = func;
        this.l = l;
    }

    /**
     * Start performing of next task.
     */
    public synchronized void go() {
        result = func.apply(l);
        notifyAll();
    }

    /**
     * Take result of applying function to argument
     * @return result
     * @throws InterruptedException
     */
    public synchronized R getResult() throws InterruptedException {
        while (result == null) {
            wait();
        }
        return result;
    }
}