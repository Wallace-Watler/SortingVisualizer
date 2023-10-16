package wallacewatler.sortingvisualizer;

import java.util.Deque;
import java.util.LinkedList;

/**
 * A variant of quicksort that switches to insertion sort when a partition has few enough elements.
 * @see QuickSort
 * @see InsertionSort
 */
public class QuickInsertSort extends Algorithm {
    public final int maxElementsForInsertion;
    private int i;
    private int j;

    public QuickInsertSort(int maxElementsForInsertion) {
        super("Hybrid of Quicksort and Insertion sort");
        this.maxElementsForInsertion = maxElementsForInsertion;
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

            // If few enough elements, use insertion sort
            if(high - low <= maxElementsForInsertion) {
                for(i = low + 1; i <= high; i++) {
                    try { awaitStep(); } catch(InterruptedException e) { return; }
                    for(j = i; j > low && compare(arr, j, j - 1) < 0; j--) {
                        try { awaitStep(); } catch(InterruptedException e) { return; }
                        swap(arr, j, j - 1);
                    }
                }
                continue;
            }

            // Otherwise, partition further
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
