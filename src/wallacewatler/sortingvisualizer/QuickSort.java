package wallacewatler.sortingvisualizer;

import java.util.Deque;
import java.util.LinkedList;

/**
 * Sorts an array by recursively partitioning it in two. The pivot is chosen as the middle element.
 */
public class QuickSort extends Algorithm {
    private int i;
    private int j;

    public QuickSort() {
        super("Quicksort - LR pointers");
    }

    @Override
    protected void execute(int[] arr) {
        final Deque<Ply> plies = new LinkedList<>();
        plies.push(new Ply(0, arr.length - 1));

        while(!plies.isEmpty()) {
            final Ply ply = plies.pop();
            final int low = ply.low;
            final int high = ply.high;
            if(low < 0 || high < 0 || low >= high)
                continue;

            final int pivot = get(arr, low + (high - low) / 2);
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
            plies.push(new Ply(j + 1, high));
            plies.push(new Ply(low, j));
        }
    }

    @Override
    public boolean isPointOfInterest(int index) {
        return index == i || index == j;
    }

    private record Ply(int low, int high) {}
}
