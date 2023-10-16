package wallacewatler.sortingvisualizer;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;

/**
 * Sorts an array by recursively partitioning it in two. The pivot is chosen as the average of the minimum and maximum
 * values.
 */
public class BinaryQuickSort extends Algorithm {
    private int i;
    private int j;

    public BinaryQuickSort() {
        super("Binary Quicksort - LR pointers");
    }

    @Override
    protected void execute(int[] arr) {
        final Deque<Ply> plies = new LinkedList<>();
        plies.push(new Ply(0, arr.length - 1, Arrays.stream(arr).min().orElse(0), Arrays.stream(arr).max().orElse(0)));

        while(!plies.isEmpty()) {
            final Ply ply = plies.pop();
            final int low = ply.low;
            final int high = ply.high;
            final int min = ply.min;
            final int max = ply.max;
            if(low < 0 || high < 0 || low >= high)
                continue;

            final int pivot = min + (max - min) / 2;
            i = low - 1;
            j = high + 1;
            while(true) {
                do {
                    try { awaitStep(); } catch(InterruptedException e) { return; }
                    i++;
                    metrics.numComparisons++;
                } while(get(arr, i) < pivot);

                do {
                    try { awaitStep(); } catch(InterruptedException e) { return; }
                    j--;
                    metrics.numComparisons++;
                } while(get(arr, j) > pivot);

                if(i >= j)
                    break;

                try { awaitStep(); } catch(InterruptedException e) { return; }
                swap(arr, i, j);
            }
            plies.push(new Ply(j + 1, high, pivot, max));
            plies.push(new Ply(low, j, min, pivot));
        }
    }

    @Override
    public boolean isPointOfInterest(int index) {
        return index == i || index == j;
    }

    private record Ply(int low, int high, int min, int max) {}
}
