package wallacewatler.sortingvisualizer;

/**
 * Sorts an array by repeatedly inserting elements into their correct position.
 */
public class InsertionSort extends Algorithm {
    private int i;
    private int j;

    public InsertionSort() {
        super("Insertion sort");
    }

    @Override
    protected void execute(int[] arr) {
        for(i = 1; i < arr.length; i++) {
            try { awaitStep(); } catch(InterruptedException e) { return; }
            for(j = i; j > 0 && compare(arr, j, j - 1) < 0; j--) {
                try { awaitStep(); } catch(InterruptedException e) { return; }
                swap(arr, j, j - 1);
            }
        }
    }

    @Override
    public boolean isPointOfInterest(int index) {
        return index == i || index == j;
    }
}
