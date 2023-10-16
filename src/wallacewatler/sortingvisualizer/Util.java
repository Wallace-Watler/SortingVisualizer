package wallacewatler.sortingvisualizer;

import java.util.Random;

final class Util {
    public static void reverse(int[] arr) {
        for(int i = 0; i < arr.length / 2; i++) {
            swap(arr, i, arr.length - 1 - i);
        }
    }

    public static void swap(int[] arr, int i, int j) {
        final int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    public static void move(int[] arr, int takeFrom, int insertInto) {
        if(takeFrom == insertInto)
            return;

        final int taken = arr[takeFrom];
        if(takeFrom < insertInto) {
            for(int i = takeFrom + 1; i <= insertInto; i++)
                arr[i - 1] = arr[i];
        } else {
            for(int i = takeFrom - 1; i >= insertInto; i--)
                arr[i + 1] = arr[i];
        }
        arr[insertInto] = taken;
    }

    public static void shuffle(int[] arr, Random rand) {
        for(int i = arr.length - 1; i > 0; i--)
            swap(arr, i, rand.nextInt(i + 1));
    }

    public static void shuffle(int[] arr) {
        shuffle(arr, new Random());
    }

    public static void misplace(int[] arr, int maxDisp, int reps, Random rand) {
        for(int rep = 0; rep < reps; rep++) {
            final int takeFrom = rand.nextInt(arr.length);
            final int insertInto = rand.nextInt(Math.max(takeFrom - maxDisp, 0), Math.min(takeFrom + maxDisp + 1, arr.length));
            move(arr, takeFrom, insertInto);
        }
    }

    public static void misplace(int[] arr, int maxDisp, int reps) {
        misplace(arr, maxDisp, reps, new Random());
    }
}
