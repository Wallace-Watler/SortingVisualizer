import wallacewatler.sortingvisualizer.*;

import java.awt.*;

public final class Test {
    public static void main(String[] args) throws InterruptedException {
        final SortingVisualizer visualizer = new SortingVisualizer(400, 3, 675);
        visualizer.start();
        visualizer.waitForExit();
    }
}
