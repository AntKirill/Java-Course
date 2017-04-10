package ru.ifmo.ctddev.antonov.concurrent;

import java.util.List;
import java.util.function.Function;

public class Applier <T, R> implements Runnable {

    private Function<List<? extends T>, ? extends R> func;
    private List<? extends T> l;
    private R result = null;

    public  Applier(Function<List<? extends T>, R> func, List<? extends T> l) {
        this.func = func;
        this.l = l;
    }

    @Override
    public void run() {
        result = func.apply(l);
    }

    public R getResult() throws InterruptedException {
        return result;
    }
}
