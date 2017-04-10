package ru.ifmo.ctddev.antonov.concurrent;

import java.util.List;
import java.util.function.Function;

public class Task <T, R>  {

    private Function<? super T, ? extends R> func;
    private T l;
    private R result = null;

    public  Task(Function<? super T,? extends R> func, T l) {
        this.func = func;
        this.l = l;
    }

    public synchronized void go() {
        result = func.apply(l);
        notifyAll();
    }

    public synchronized R getResult() throws InterruptedException {
        while (result == null) {
            wait();
        }
        return result;
    }
}