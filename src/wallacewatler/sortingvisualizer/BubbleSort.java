package wallacewatler.sortingvisualizer;

/**
 * Sorts an array by repeatedly swapping adjacent elements that are out of order.
 */
public class BubbleSort extends Algorithm {
    private int n;
    private int newN;
    private int i;

    public BubbleSort() {
        super("Bubble sort");
    }

    @Override
    protected void execute(int[] arr) {
        n = arr.length;
        while(n > 1) {
            newN = 0;
            i = 1;
            while(i < n) {
                try { awaitStep(); } catch(InterruptedException e) { return; }
                if(compare(arr, i - 1, i) > 0) {
                    swap(arr, i - 1, i);
                    newN = i;
                }
                i++;
            }
            n = newN;
        }
    }

    @Override
    public boolean isPointOfInterest(int index) {
        return index == i || index == newN || index == n;
    }
}
