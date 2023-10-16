package wallacewatler.sortingvisualizer;

/**
 * Sorts an array by repeatedly selecting the next lowest value.
 */
public class SelectionSort extends Algorithm {
    private int i;
    private int j;
    private int jMin;

    public SelectionSort() {
        super("Selection sort");
    }

    @Override
    protected void execute(int[] arr) {
        for(i = 0; i < arr.length - 1; i++) {
            try { awaitStep(); } catch(InterruptedException e) { return; }
            jMin = i;
            for(j = i + 1; j < arr.length; j++) {
                try { awaitStep(); } catch(InterruptedException e) { return; }
                if(compare(arr, j, jMin) < 0)
                    jMin = j;
            }
            if(jMin != i)
                swap(arr, i, jMin);
        }
    }

    @Override
    public boolean isPointOfInterest(int index) {
        return index == i || index == j || index == jMin;
    }
}
