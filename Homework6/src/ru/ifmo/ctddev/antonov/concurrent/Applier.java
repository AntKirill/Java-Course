package ru.ifmo.ctddev.antonov.concurrent;

import java.util.List;
import java.util.function.Function;

/**
 * Created by kirill on 3/20/17.
 */
public class Applier <T, R> implements Runnable {

    private Function<List<? extends T>, R> func;
    private List<? extends T> l;
    private R result;

    public Applier(Function<List<? extends T>, R> func, List<? extends T> l) {
        this.func = func;
        this.l = l;
    }

    @Override
    public void run() {
        result = func.apply(l);
    }

    public R getResult() {
        return result;
    }
}
