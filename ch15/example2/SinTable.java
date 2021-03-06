package javathreads.examples.ch15.example2;

public class SinTable implements Runnable {

    private final float lookupValues[];
    private final Thread lookupThreads[];
    private final int startLoop;
    private final int endLoop;
    private int curLoop;
    private final int numThreads;

    public SinTable() {
        lookupValues = new float[360 * 100];
        lookupThreads = new Thread[12];
        startLoop = 0;
        curLoop = 0;
        endLoop = (360 * 100);
        numThreads = 12;
    }

    public static void main(String args[]) {
        System.out.println("Starting Example 2 (Threaded Example)");

        SinTable st = new SinTable();
        float results[] = st.getValues();

        System.out.println("Results: " + results[0] + ", "
                + results[1] + ", " + results[2] + ", " + "...");
        System.out.println("Done");
    }

    @Override
    public void run() {
        SinTableRange str;
        while ((str = loopGetRange()) != null) {
            loopDoRange(str.start, str.end);
        }
    }

    public float[] getValues() {
        for (int i = 0; i < numThreads; i++) {
            lookupThreads[i] = new Thread(this);
            lookupThreads[i].start();
        }
        for (int i = 0; i < numThreads; i++) {
            try {
                lookupThreads[i].join();
            } catch (InterruptedException iex) {
            }
        }
        return lookupValues;
    }

    private synchronized SinTableRange loopGetRange() {
        if (curLoop >= endLoop) {
            return null;
        }
        SinTableRange ret = new SinTableRange();
        ret.start = curLoop;
        curLoop += (endLoop - startLoop) / numThreads + 1;
        ret.end = (curLoop < endLoop) ? curLoop : endLoop;
        return ret;
    }

    private void loopDoRange(int start, int end) {
        for (int i = start; i < end; i += 1) {
            float sinValue = (float) Math.sin((i % 360) * Math.PI / 180.0);
            lookupValues[i] = sinValue * i / 180.0f;
        }
    }

    private class SinTableRange {

        public int start, end;
    }
}
