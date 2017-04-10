package ru.ifmo.ctddev.antonov.concurrent;

import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ParallelMapperImpl implements ParallelMapper {

    private ConcurrentQueue appliers;
    private List<Thread> threads;

    public ParallelMapperImpl(int threadsNumber) {
        appliers = new ConcurrentQueue();
        threads = new ArrayList<>();
        for (int i = 0; i < threadsNumber; i++) {
            Thread th = new Thread(new QueueKeeper(appliers));
            threads.add(th);
            th.start();
        }
    }

    @Override
    public <T, R> List<R> map(Function<? super T, ? extends R> f, List<? extends T> args) throws InterruptedException {
        List<Task<? super T, ? extends R>> tasks = new ArrayList<>();
        List<R> res = new ArrayList<>();
        for (T a : args) {
            Task<? super T, ? extends R> t = new Task<>(f, a);
            appliers.add(t);
            tasks.add(t);
        }
        for (Task<? super T, ? extends R> t : tasks) {
            res.add(t.getResult());
        }
        return res;
    }

    @Override
    public void close() throws InterruptedException {
        for (Thread th : threads) {
            th.interrupt();
        }
    }
}
