package wallacewatler.sortingvisualizer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

/**
 * A simple graphical tool to visualize sorting algorithms. An application should create an instance of
 * {@code SortingVisualizer} and optionally add custom algorithms, then call {@code start()}.
 */
public class SortingVisualizer extends Canvas {
    private static final double TARGET_FPS = 30;
    private final JFrame frame = new JFrame("Sorting Visualizer");
    private final int[] array;
    private final int horizontalScale;
    private final int canvasHeight;
    private final Thread renderThread = new Thread(this::renderLoop, "renderLoop");
    private final Thread simulationThread = new Thread(this::simulationLoop, "simulationLoop");
    private final List<Algorithm> algorithms = new ArrayList<>();
    private int currentAlg = 1;
    private Thread algorithmThread;
    private Color backgroundColor = Color.DARK_GRAY;
    private Color dataColor = Color.LIGHT_GRAY;
    private Color pointOfInterestColor = Color.RED;
    private Color metricsColor = Color.GREEN;
    private Color instructionsColor = Color.BLACK;
    private int stepSpeedExponent = 8;
    private boolean paused = true;
    private boolean running = true;

    /**
     * Create a new visualizer with some basic algorithms included. The window width will be
     * {@code n * horizontalScale}.
     * @param n the size of the array
     * @param horizontalScale the width of an element in pixels
     * @param canvasHeight the window height
     */
    public SortingVisualizer(int n, int horizontalScale, int canvasHeight) {
        algorithms.add(new BinaryQuickSort());
        algorithms.add(new BubbleSort());
        algorithms.add(new CombSort(1.3));
        algorithms.add(new InsertionSort());
        algorithms.add(new QuickInsertSort(10));
        algorithms.add(new QuickShellSort(100, 2.25));
        algorithms.add(new QuickSort());
        algorithms.add(new SelectionSort());
        algorithms.add(new ShellSort(2.25));

        array = IntStream.range(0, n).toArray();
        this.horizontalScale = horizontalScale;
        this.canvasHeight = canvasHeight;
        algorithmThread = new Thread(() -> algorithms.get(currentAlg).execute(array), "algorithm");

        final Dimension d = new Dimension(n * horizontalScale, canvasHeight);
        setSize(d);
        setPreferredSize(d);
        setMinimumSize(d);
        setMaximumSize(d);
        frame.setSize(d);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setFocusable(true);
        frame.requestFocus();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.add(this);
        frame.pack();

        frame.addWindowListener(new WindowListener() {
            public void windowOpened(WindowEvent e) {}
            public void windowClosing(WindowEvent e) { stop(); }
            public void windowClosed(WindowEvent e) { frame.dispose(); }
            public void windowIconified(WindowEvent e) {}
            public void windowDeiconified(WindowEvent e) {}
            public void windowActivated(WindowEvent e) {}
            public void windowDeactivated(WindowEvent e) {}
        });

        addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {}

            public void keyPressed(KeyEvent e) {
                switch(e.getKeyCode()) {
                    case KeyEvent.VK_LEFT -> previousAlgorithm();
                    case KeyEvent.VK_RIGHT -> nextAlgorithm();
                    case KeyEvent.VK_COMMA -> stepSpeedExponent = stepSpeedExponent <= 0 ? 0 : (stepSpeedExponent - 1);
                    case KeyEvent.VK_PERIOD -> stepSpeedExponent++;
                    case KeyEvent.VK_SPACE -> paused = !paused;
                }
            }

            public void keyReleased(KeyEvent e) {
                final Runnable action = switch(e.getKeyCode()) {
                    case KeyEvent.VK_S -> () -> Arrays.sort(array);
                    case KeyEvent.VK_R -> () -> Util.shuffle(array);
                    case KeyEvent.VK_M -> () -> {
                        Arrays.sort(array);
                        Util.misplace(array, array.length, array.length / 100);
                    };
                    case KeyEvent.VK_N -> () -> {
                        Arrays.sort(array);
                        Util.misplace(array, Math.max(1, array.length / 300), 10 * array.length);
                    };
                    default -> null;
                };

                if(action == null)
                    return;

                algorithmThread.interrupt();
                try {
                    algorithmThread.join();
                } catch(InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                algorithms.get(currentAlg).reset();
                action.run();
                algorithmThread = new Thread(() -> algorithms.get(currentAlg).execute(array), "algorithm");
                algorithmThread.start();
            }
        });

        createBufferStrategy(3);
    }

    /**
     * @return The currently running algorithm.
     */
    public Algorithm currentAlgorithm() {
        return algorithms.get(currentAlg);
    }

    /**
     * Adds an algorithm to this visualizer, which can then be displayed. Algorithms can be added either before or after
     * calling {@code start()}.
     * @param algorithm an algorithm to add
     * @see SortingVisualizer#start()
     */
    public void addAlgorithm(Algorithm algorithm) {
        algorithms.add(algorithm);
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Color getDataColor() {
        return dataColor;
    }

    public void setDataColor(Color dataColor) {
        this.dataColor = dataColor;
    }

    public Color getPointOfInterestColor() {
        return pointOfInterestColor;
    }

    public void setPointOfInterestColor(Color pointOfInterestColor) {
        this.pointOfInterestColor = pointOfInterestColor;
    }

    public Color getMetricsColor() {
        return metricsColor;
    }

    public void setMetricsColor(Color metricsColor) {
        this.metricsColor = metricsColor;
    }

    public Color getInstructionsColor() {
        return instructionsColor;
    }

    public void setInstructionsColor(Color instructionsColor) {
        this.instructionsColor = instructionsColor;
    }

    /**
     * Start the visualizer. Typically, you should call {@code waitForExit()} after this.
     * @see SortingVisualizer#waitForExit()
     */
    public void start() {
        algorithmThread.start();
        renderThread.start();
        simulationThread.start();
    }

    /**
     * Notify the visualizer that it should stop running. Typically, you should call {@code waitForExit()} after this.
     * @see SortingVisualizer#waitForExit()
     */
    public void stop() {
        running = false;
        algorithmThread.interrupt();
        renderThread.interrupt();
        simulationThread.interrupt();
    }

    /**
     * Blocks the current thread until the visualization has fully exited.
     * @throws InterruptedException if the current thread is interrupted while waiting
     */
    public void waitForExit() throws InterruptedException {
        algorithmThread.join();
        simulationThread.join();
        renderThread.join();
        frame.dispose();
    }

    private void nextAlgorithm() {
        algorithmThread.interrupt();
        try {
            algorithmThread.join();
        } catch(InterruptedException ex) {
            throw new RuntimeException(ex);
        }
        algorithms.get(currentAlg).reset();
        currentAlg = (currentAlg + 1) % algorithms.size();
        algorithmThread = new Thread(() -> algorithms.get(currentAlg).execute(array), "algorithm");
        algorithmThread.start();
    }

    private void previousAlgorithm() {
        algorithmThread.interrupt();
        try {
            algorithmThread.join();
        } catch(InterruptedException ex) {
            throw new RuntimeException(ex);
        }
        algorithms.get(currentAlg).reset();
        currentAlg = currentAlg == 0 ? algorithms.size() - 1 : currentAlg - 1;
        algorithmThread = new Thread(() -> algorithms.get(currentAlg).execute(array), "algorithm");
        algorithmThread.start();
    }

    private void simulationLoop() {
        long now, lastTime = System.nanoTime();
        double dt = 0;
        while(!Thread.interrupted()) {
            now = System.nanoTime();
            dt += (now - lastTime) / 1_000_000_000.0;
            lastTime = now;

            if(paused || !algorithmThread.isAlive()) {
                dt = 0;
            } else {
                final double stepInterval = 1.0 / (1L << stepSpeedExponent);
                final int numSteps = (int) (dt / stepInterval);
                currentAlgorithm().step(numSteps);
                dt -= numSteps * stepInterval;
            }
        }
    }

    private void renderLoop() {
        while(running) {
            render();
            long nanos = (long) (1_000_000_000L / TARGET_FPS);
            long millis = nanos / 1_000_000;
            nanos -= millis * 1_000_000;
            try {
                Thread.sleep(millis, (int) nanos);
            } catch(InterruptedException ignored) {}
        }
    }

    private void render() {
        final BufferStrategy bs = getBufferStrategy();
        final Graphics g = bs.getDrawGraphics();
        g.setColor(backgroundColor);
        g.fillRect(0, 0, frame.getWidth(), frame.getHeight());

        final Algorithm algorithm = algorithms.get(currentAlg);

        for(int i = 0; i < array.length; i++) {
            final int height = canvasHeight * array[i] / (array.length - 1);

            if(algorithmThread.isAlive() && algorithm.isPointOfInterest(i))
                g.setColor(pointOfInterestColor);
            else
                g.setColor(dataColor);

            g.fillRect(i * horizontalScale, canvasHeight - height, horizontalScale, height);
        }

        final int metricsLeftEdge = 10;
        final int metricsMarginY = 10;
        g.setColor(metricsColor);
        g.setFont(new Font("Consolas", Font.PLAIN, 16));
        g.drawString(algorithm.name, metricsLeftEdge, 10 + metricsMarginY);
        g.drawString(formatSpeedExp() + (paused ? ", paused" : ""), metricsLeftEdge, 25 + metricsMarginY);
        g.drawString("Comparisons: " + formatCount(algorithm.metrics.numComparisons), metricsLeftEdge, 40 + metricsMarginY);
        g.drawString("Swaps: " + formatCount(algorithm.metrics.numSwaps), metricsLeftEdge, 55 + metricsMarginY);
        g.drawString("Main array reads: " + formatCount(algorithm.metrics.numMainArrayReads), metricsLeftEdge, 70 + metricsMarginY);
        g.drawString("Main array writes: " + formatCount(algorithm.metrics.numMainArrayWrites), metricsLeftEdge, 85 + metricsMarginY);
        g.drawString("Auxiliary array reads: " + formatCount(algorithm.metrics.numAuxArrayReads), metricsLeftEdge, 100 + metricsMarginY);
        g.drawString("Auxiliary array writes: " + formatCount(algorithm.metrics.numAuxArrayWrites), metricsLeftEdge, 115 + metricsMarginY);

        g.setColor(instructionsColor);
        g.drawString("Space: pause    R: random    N: nearly sorted    M: misplaced    S: sorted    Left: previous algorithm    Right: next algorithm    Comma: slower    Period: faster", array.length * horizontalScale - 1500, canvasHeight - 10);

        g.dispose();
        bs.show();
    }

    private String formatSpeedExp() {
        if(stepSpeedExponent == 0)
            return (1L << stepSpeedExponent) + " step per sec";

        if(stepSpeedExponent > 0)
            return (1L << stepSpeedExponent) + " steps per sec";

        return (1L << -stepSpeedExponent) + " seconds per step";
    }

    private static String formatCount(int count) {
        if(count < 10_000)
            return Integer.toString(count);

        if(count < 100_000)
            return new DecimalFormat("#.# K").format(count / 1000.0);

        if(count < 1_000_000)
            return (count / 1000) + " K";

        if(count < 10_000_000)
            return new DecimalFormat("#.## M").format(count / 1_000_000.0);

        if(count < 100_000_000)
            return new DecimalFormat("#.# M").format(count / 1_000_000.0);

        if(count < 1_000_000_000)
            return (count / 1_000_000) + " M";

        return new DecimalFormat("#.## B").format(count / 1_000_000_000.0);
    }
}
