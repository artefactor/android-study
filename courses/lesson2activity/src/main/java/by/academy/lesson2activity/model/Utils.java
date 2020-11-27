package by.academy.lesson2activity.model;

import java.util.Random;
import java.util.TreeSet;

public class Utils {
    public static int[] uniqueRandomIntArray() {
        Random e = new Random();
        int random = (int) (Math.random() * 20);

        TreeSet<Integer> set = new TreeSet<>();
        while (set.size() < random) {
            set.add(e.nextInt());
        }
        Integer[] integers = set.toArray(new Integer[]{});
        int[] numbers = new int[integers.length];
        for (int i = 0; i < integers.length; i++) {
            numbers[i] = integers[i];
        }
        return numbers;
    }
}
