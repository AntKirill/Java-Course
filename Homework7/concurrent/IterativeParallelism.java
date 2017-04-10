package ru.ifmo.ctddev.antonov.concurrent;

import info.kgeorgiy.java.advanced.concurrent.ListIP;
import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class IterativeParallelism implements ListIP {

    private ParallelMapper pm;

    private <T> List<List<? extends T>> makeGroups(int k, List<? extends T> list) {
        List<List<? extends T>> groups = new ArrayList<>();
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
            groups.add(list.subList(l, r));
            l = r;
            r += blockSize;
        }
        return groups;
    }

    private <T, R> R performInParallel(int amountOfThreads, List<? extends T> list, Function<List<? extends T>, R> func, Function<List<R>, R> afterAll) throws InterruptedException {
        List<List<? extends T>> groups = makeGroups(amountOfThreads, list);

        if (pm != null) {
            return afterAll.apply(pm.map(func, groups));
        }

        List<Thread> threads = new ArrayList<>();
        List<Applier<T, R>> appliers = new ArrayList<>();
        for (List<? extends T> el : groups) {
            Applier<T, R> ap = new Applier<>(func, el);
            appliers.add(ap);
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
        for (int i = 0; i < appliers.size(); i++) {
            results.add(appliers.get(i).getResult());
        }
        return afterAll.apply(results);
    }

    /**
     * Finds maximum at list
     * @param var1 amount of threads
     * @param var2 list of arguments
     * @param var3 comparator to compare arguments
     * @param <T> type of elements at list
     * @return maximim at list
     * @throws InterruptedException if peforming is
     */
    @Override
    public <T> T maximum(int var1, List<? extends T> var2, Comparator<? super T> var3) throws InterruptedException {
        return performInParallel(var1, var2, x -> Collections.max(x, var3), x -> Collections.max(x, var3));
    }

    /**
     * Finds minimum at list
     * @param var1 amount of threads
     * @param var2 list of arguments
     * @param var3 comparator to compare arguments
     * @param <T> type of elements at list
     * @return minimum at list
     * @throws InterruptedException if peforming is
     */
    @Override
    public <T> T minimum(int var1, List<? extends T> var2, Comparator<? super T> var3) throws InterruptedException {
        return performInParallel(var1, var2, x -> Collections.min(x, var3), x -> Collections.min(x, var3));
    }

    /**
     * Checkes if all elements at list accept predicate.
     * @param var1 amount of threads
     * @param var2 list of arguments
     * @param var3 predicate
     * @param <T> type of elements at list
     * @return return true if accepts and false otherwise
     * @throws InterruptedException if peforming is
     */
    @Override
    public <T> boolean all(int var1, List<? extends T> var2, Predicate<? super T> var3) throws InterruptedException {
        return performInParallel(var1, var2, x -> x.stream().allMatch(var3), x -> x.stream().allMatch(Predicate.isEqual(true)));
    }

    /**
     * Checkes if all elements at list accept predicate.
     * @param var1 amount of threads
     * @param var2 list of arguments
     * @param var3 predicate
     * @param <T> type of elements at list
     * @return return true if accepts and false otherwise
     * @throws InterruptedException if peforming is
     */
    @Override
    public <T> boolean any(int var1, List<? extends T> var2, Predicate<? super T> var3) throws InterruptedException {
        return performInParallel(var1, var2, x -> x.stream().anyMatch(var3), x -> x.stream().anyMatch(Predicate.isEqual(true)));
    }

    /**
     * Shows all elements as strings
     * @param var1 amount of threads
     * @param var2 list of arguments
     * @return String of all elements
     * @throws InterruptedException if peforming is interrupted
     */
    @Override
    public String join(int var1, List<?> var2) throws InterruptedException {
        List<String> representations = map(var1, var2, Object::toString);
        StringBuilder result = new StringBuilder();
        representations.forEach(result::append);
        return result.toString();
    }

    /**
     * Throws out all elements that dont accept the predicate.
     * @param var1 amount of threads
     * @param var2 list of arguments
     * @param var3 predicate
     * @param <T> type of elements at list
     * @return List of elements that accepts predicate
     * @throws InterruptedException if peforming is
     */
    @Override
    public <T> List<T> filter(int var1, List<? extends T> var2, Predicate<? super T> var3) throws InterruptedException {
        return performInParallel(var1, var2, x -> x.stream().filter(var3).collect(Collectors.toList()), x -> x.stream().reduce(new ArrayList<T>(), (a, b) -> {
            a.addAll(b);
            return a;
        }));
    }

    /**
     * Make other list that contains result of applying function to elements at list
     * @param var1 amount of threads
     * @param var2 list of arguments
     * @param var3 function
     * @param <T> type of elements at list
     * @return List of results
     * @throws InterruptedException if peforming is iterrupted
     */
    @Override
    public <T, U> List<U> map(int var1, List<? extends T> var2, Function<? super T, ? extends U> var3) throws InterruptedException {
        return performInParallel(var1, var2, x -> x.stream().map(var3).collect(Collectors.toList()), x -> x.stream().reduce(new ArrayList<U>(), (a, b) -> {
            a.addAll(b);
            return a;
        }));

    }

    /**
     * Creates new mapper
     * @param pm
     */
    public IterativeParallelism(ParallelMapper pm) {
        this.pm = pm;
    }

    /**
     * Creates new mapper
     */
    public IterativeParallelism() {
    }

    /**
     * Start performings
     * @throws InterruptedException
     */
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
