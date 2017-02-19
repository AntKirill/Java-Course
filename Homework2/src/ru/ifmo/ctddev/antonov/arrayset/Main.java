package ru.ifmo.ctddev.antonov.arrayset;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by kirill on 2/19/17.
 */
public class Main {


    public static void fill(Collection<Integer> col) {
        for (int i = 0; i < 10; i++) {
            col.add(i);
        }
    }

    public static <E> void dump(ArraySet<E> subset) {
        for (Iterator<E> it = subset.iterator(); it.hasNext();) {
            E v = it.next();
            System.out.print(v);
            System.out.print(" ");
        }
        System.out.println();
    }

    public static void test(ArraySet<Integer> arraySet) {
        System.out.println(arraySet.floor(-3));
    }

    static public Integer compare(Integer a, Integer b) {
        return 0;
    }

    public static void main(String [] args) {
        ArrayList<Integer> my = new ArrayList<>();
        fill(my);
        ArraySet<Integer> arraySet = new ArraySet<>(my, Main::compare);
        dump(arraySet);
        test(arraySet);
    }
}
