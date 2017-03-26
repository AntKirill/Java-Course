package ru.ifmo.ctddev.antonov.concurrent;

import info.kgeorgiy.java.advanced.concurrent.ListIP;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class IterativeParallelism implements ListIP {

    private <T, R> List<Applier<T, R>> makeGroups(int k, List<? extends T> list, Function<List<? extends T>, R> func) {
        List<Applier<T, R>> appliers = new ArrayList<>();
        int blockSize = list.size() / k;
        int amountOfBlocks = k;
        int rest = list.size() % k;
        if (k > list.size()) {
            blockSize = 1;
            amountOfBlocks = list.size();
            rest = 0;
        }
        int cntBigger = 0;
        int r = blockSize;
        int l = 0;
        for (int i = 0; i < amountOfBlocks; i++) {
            if (cntBigger < rest) {
                ++r;
                ++cntBigger;
            }
            appliers.add(new Applier<>(func, list.subList(l, r)));
            l = r;
            r += blockSize;
        }
        return appliers;
    }

    private <T, R> R performInParallel(int amountOfThreads, List<? extends T> list, Function<List<? extends T>, R> func, Function<List<R>, R> afterAll) throws InterruptedException {
        List<Applier<T, R>> appliers = makeGroups(amountOfThreads, list, func);

        List<Thread> threads = new ArrayList<>();
        for (Applier<T, R> ap : appliers) {
            Thread t = new Thread(ap);
            threads.add(t);
            t.start();
        }
        for (Thread t : threads) {
            t.join();
        }
        List<R> results = new ArrayList<>();
        for (Applier<T, R> ap : appliers) {
            results.add(ap.getResult());
        }
        return afterAll.apply(results);
    }

    @Override
    public <T> T maximum(int var1, List<? extends T> var2, Comparator<? super T> var3) throws InterruptedException {
        return performInParallel(var1, var2, x -> Collections.max(x, var3), x -> Collections.max(x, var3));
    }

    @Override
    public <T> T minimum(int var1, List<? extends T> var2, Comparator<? super T> var3) throws InterruptedException {
        return performInParallel(var1, var2, x -> Collections.min(x, var3), x -> Collections.min(x, var3));
    }

    @Override
    public <T> boolean all(int var1, List<? extends T> var2, Predicate<? super T> var3) throws InterruptedException {
        return performInParallel(var1, var2, x -> x.stream().allMatch(var3), x -> x.stream().allMatch(Predicate.isEqual(true)));
    }

    @Override
    public <T> boolean any(int var1, List<? extends T> var2, Predicate<? super T> var3) throws InterruptedException {
        return performInParallel(var1, var2, x -> x.stream().anyMatch(var3), x -> x.stream().anyMatch(Predicate.isEqual(true)));
    }

    @Override
    public String join(int var1, List<?> var2) throws InterruptedException {
        List<String> representations = map(var1, var2, Object::toString);
        StringBuilder result = new StringBuilder();
        representations.forEach(result::append);
        return result.toString();
    }

    @Override
    public <T> List<T> filter(int var1, List<? extends T> var2, Predicate<? super T> var3) throws InterruptedException {
        return performInParallel(var1, var2, x -> x.stream().filter(var3).collect(Collectors.toList()), x -> x.stream().reduce(new ArrayList<T>(), (a, b) -> {
                    a.addAll(b);
                    return a;
                }));
    }

    @Override
    public <T, U> List<U> map(int var1, List<? extends T> var2, Function<? super T, ? extends U> var3) throws InterruptedException {
        return performInParallel(var1, var2, x -> x.stream().map(var3).collect(Collectors.toList()), x -> x.stream().reduce(new ArrayList<U>(), (a, b) -> {
                    a.addAll(b);
                    return a;
                }));

    }

    public void run() throws InterruptedException {
        List<Integer> l = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            l.add(i);
        }
        System.out.println(maximum(4, l, null));
    }

    public static void main(String[] args) throws InterruptedException {
        new IterativeParallelism().run();
    }
}
