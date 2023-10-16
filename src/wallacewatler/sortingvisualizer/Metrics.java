package wallacewatler.sortingvisualizer;

/**
 * Metrics stores operational data for an algorithm. These can be used to compare algorithms in terms of number of
 * comparisons, swaps, and array accesses. While these metrics can give a rough estimate of algorithm speed, they
 * should not be relied upon because there are contributing factors outside what can be measured here.
 */
public final class Metrics {
    /**
     * Number of comparisons. A comparison is any operation that compares two elements of an array. Index comparisons
     * are not counted.
     */
    public int numComparisons;
    /**
     * Number of swaps. A swap is defined by two elements that switch places. Element removals, shifts, and insertions
     * are not counted.
     */
    public int numSwaps;
    /** Number of main array reads. */
    public int numMainArrayReads;
    /** Number of main array writes. */
    public int numMainArrayWrites;
    /** Number of auxiliary array reads. */
    public int numAuxArrayReads;
    /** Number of auxiliary array writes. */
    public int numAuxArrayWrites;

    /** Resets all metrics to zero. */
    public void clear() {
        numComparisons = 0;
        numSwaps = 0;
        numMainArrayReads = 0;
        numMainArrayWrites = 0;
        numAuxArrayReads = 0;
        numAuxArrayWrites = 0;
    }

    /**
     * Save the current metrics into an immutable record. Future changes to these metrics will not affect the returned
     * record.
     * @return An immutable record of the current metrics.
     */
    public Record record() {
        return new Record(
                numComparisons,
                numSwaps,
                numMainArrayReads,
                numMainArrayWrites,
                numAuxArrayReads,
                numAuxArrayWrites
        );
    }

    /**
     * An immutable record of Metrics.
     * @param numComparisons
     * @param numSwaps
     * @param numMainArrayReads
     * @param numMainArrayWrites
     * @param numAuxArrayReads
     * @param numAuxArrayWrites
     * @see Metrics
     */
    public record Record(
            int numComparisons,
            int numSwaps,
            int numMainArrayReads,
            int numMainArrayWrites,
            int numAuxArrayReads,
            int numAuxArrayWrites) {}
}
