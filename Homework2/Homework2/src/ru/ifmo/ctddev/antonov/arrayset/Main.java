package ru.ifmo.ctddev.antonov.arrayset;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by kirill on 2/19/17.
 */
public class Main {


    public static void fill(Collection<Integer> col) {
        col.add(30);
        col.add(20);
        col.add(20);
        col.add(10);
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
        ArraySet<Integer> arraySet = new ArraySet<>(my);
        dump(arraySet);
        //test(arraySet);
        ArraySet<Integer> desSet = arraySet.descendingSet();
        //SortedSet<Integer> desSet = arraySet.subSet(10, 11);
        dump((ArraySet<Integer>) desSet);
        desSet.floor(5);
    }
}