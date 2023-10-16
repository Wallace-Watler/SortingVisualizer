package wallacewatler.sortingvisualizer;

import java.util.Deque;
import java.util.LinkedList;

/**
 * A variant of quicksort that switches to shell sort when a partition has few enough elements.
 * @see QuickSort
 * @see ShellSort
 */
public class QuickShellSort extends Algorithm {
    public final int maxElementsForShell;
    public final double shrink;
    private int i;
    private int j;

    public QuickShellSort(int maxElementsForShell, double shrink) {
        super("Hybrid of Quicksort and Shell sort");
        this.maxElementsForShell = maxElementsForShell;
        this.shrink = shrink;
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

            // If few enough elements, use shell sort
            final int n = high - low + 1;
            if(n <= maxElementsForShell) {
                int gap = n;
                do {
                    gap /= shrink;
                    if(gap <= 1)
                        gap = 1;

                    for(i = gap + low; i < high + 1; i++) {
                        try { awaitStep(); } catch(InterruptedException e) { return; }
                        final int arrI = get(arr, i);
                        metrics.numComparisons++;
                        for(j = i; j >= gap + low && get(arr, j - gap) > arrI; j -= gap) {
                            try { awaitStep(); } catch(InterruptedException e) { return; }
                            set(arr, j, get(arr, j - gap));
                            metrics.numComparisons++;
                        }
                        set(arr, j, arrI);
                    }
                } while(gap > 1);
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
