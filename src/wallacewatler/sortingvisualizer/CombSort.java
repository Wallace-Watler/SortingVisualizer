package wallacewatler.sortingvisualizer;

/**
 * An improved version of bubble sort that starts with a large gap for element comparison and gradually reduces it.
 * @see BubbleSort
 */
public class CombSort extends Algorithm {
    public final double shrink;
    private int i;
    private int gap;

    public CombSort(double shrink) {
        super("Comb sort - shrink " + shrink);
        this.shrink = shrink;
    }

    @Override
    protected void execute(int[] arr) {
        gap = arr.length;
        boolean sorted = false;

        while(!sorted) {
            gap /= shrink;
            if(gap <= 1) {
                gap = 1;
                sorted = true;
            }

            i = 0;
            while(i + gap < arr.length) {
                try { awaitStep(); } catch(InterruptedException e) { return; }
                if(compare(arr, i, i + gap) > 0) {
                    swap(arr, i, i + gap);
                    sorted = false;
                }
                i++;
            }
        }
    }

    @Override
    public boolean isPointOfInterest(int index) {
        return index == i || index == i + gap;
    }
}
