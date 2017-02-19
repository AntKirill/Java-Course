package ru.ifmo.ctddev.antonov.arrayset;

public class Main {

    static <T extends Comparable<T>> int f (T a, T b) {
        return a.compareTo(b);
    }

    public static void main(String args[]) {
        int a = 5, b = 6;
        System.out.println(f(5, 6));
        System.out.println(f(a, a));
        Integer a1 = 10, b1 = 1;
        System.out.println(f(a1, b1));
    }
}
