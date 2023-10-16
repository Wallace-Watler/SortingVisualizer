package wallacewatler.sortingvisualizer;

import java.util.concurrent.Semaphore;

/**
 * An algorithm to be visualized. It operates on an array of integers.
 */
public abstract class Algorithm {
    /** The display name of this algorithm. */
    public final String name;
    /** The metrics of this algorithm. */
    public final Metrics metrics = new Metrics();
    private final Semaphore stepPermits = new Semaphore(0);

    /**
     * Define a new algorithm.
     * @param name the display name of this algorithm
     */
    public Algorithm(String name) {
        this.name = name;
    }

    final void reset() {
        metrics.clear();
        stepPermits.drainPermits();
    }

    final void step(int count) {
        stepPermits.release(count);
    }

    /**
     * The visualization calls this method to determine the color for the given array index.
     * @param index an array index
     * @return true if the given index should be highlighted in the visualization
     */
    public boolean isPointOfInterest(int index) {
        return false;
    }

    /**
     * The procedure that will be visualized. This is where the algorithm implementation should go. The visualization
     * will respond to changes in the input array. If the procedure is interrupted, it should stop executing and return.
     * The metrics can optionally be updated during execution.
     * @param arr the input array
     * @see Metrics
     */
    protected abstract void execute(int[] arr);

    /**
     * Wait for a step permit. If step permits are available, this will immediately return and consume one step permit.
     * If no step permits are available, this will block the current thread until either a step permit becomes available
     * or the thread is interrupted.
     * <p>
     * This is used to break up the algorithm execution in order to animate it. This should be called from within
     * {@code execute()}, typically right before a "step" of the algorithm (the exact meaning of a step is up to the
     * implementer).
     * @throws InterruptedException if the current thread is interrupted
     */
    protected final void awaitStep() throws InterruptedException {
        stepPermits.acquire();
    }

    /*
    TODO: These input arrays could either be the main array or an auxiliary array, but currently the main array metrics
          are incremented.
     */
    /**
     * Convenience method to swap two elements of an array. This will increase the number of swaps, main array reads,
     * and main array writes.
     * @param arr an array
     * @param i index of the first element
     * @param j index of the second element
     */
    protected final void swap(int[] arr, int i, int j) {
        final int temp = get(arr, i);
        set(arr, i, get(arr, j));
        set(arr, j, temp);
        metrics.numSwaps++;
    }

    /**
     * Convenience method to get an element of an array. This will increment the number of main array reads.
     * @param arr an array
     * @param i index of the element to get
     * @return the element at index {@code i}
     */
    protected final int get(int[] arr, int i) {
        metrics.numMainArrayReads++;
        return arr[i];
    }

    /**
     * Convenience method to set an element of an array. This will increment the number of main array writes.
     * @param arr an array
     * @param i index to place {@code value} in
     * @param value the element to put at index {@code i}
     */
    protected final void set(int[] arr, int i, int value) {
        metrics.numMainArrayWrites++;
        arr[i] = value;
    }

    /**
     * Convenience method to compare two elements of an array. This will increment the number of comparisons and main
     * array reads.
     * @param arr an array
     * @param i index of the first element
     * @param j index of the second element
     * @return a value less than zero, equal to zero, or greater than zero if element {@code i} is less than, equal to,
     *         or greater than element {@code j}, respectively.
     */
    protected final int compare(int[] arr, int i, int j) {
        metrics.numComparisons++;
        return get(arr, i) - get(arr, j);
    }
}
