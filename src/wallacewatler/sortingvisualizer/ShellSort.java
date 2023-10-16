package wallacewatler.sortingvisualizer;

/**
 * An improved version of insertion sort that starts with a large gap for element insertion and gradually reduces it.
 * @see InsertionSort
 */
public class ShellSort extends Algorithm {
    public final double shrink;
    private int i;
    private int j;

    public ShellSort(double shrink) {
        super("Shell sort - shrink " + shrink);
        this.shrink = shrink;
    }

    @Override
    protected void execute(int[] arr) {
        int gap = arr.length;
        do {
            gap /= shrink;
            if(gap <= 1)
                gap = 1;

            for(i = gap; i < arr.length; i++) {
                try { awaitStep(); } catch(InterruptedException e) { return; }
                final int temp = get(arr, i);
                metrics.numComparisons++;
                for(j = i; j >= gap && get(arr, j - gap) > temp; j -= gap) {
                    try { awaitStep(); } catch(InterruptedException e) { return; }
                    set(arr, j, get(arr, j - gap));
                    metrics.numComparisons++;
                }
                set(arr, j, temp);
            }
        } while(gap > 1);
    }

    @Override
    public boolean isPointOfInterest(int index) {
        return index == i || index == j;
    }
}
