package deque;
import edu.princeton.cs.algs4.Stopwatch;

public class TimeArrayDeque {
    private static void printTimingTable(ArrayDeque<Integer> Ns, ArrayDeque<Double> times, ArrayDeque<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeArrayDequeConstruction();
    }

    public static void timeArrayDequeConstruction() {
        /** Instantiated array lists to track the numbers in the testing. */
        ArrayDeque<Integer> Ns = new ArrayDeque<>();
        ArrayDeque<Double> times = new ArrayDeque<>();
        ArrayDeque<Integer> opCounts = new ArrayDeque<>();

        for (int i = 0; i < 16; i += 1) {
            int lSize= (int) Math.pow(2, i) * 1000;
            ArrayDeque<Integer> ad1 = new ArrayDeque<>();
            int j = 0;
            /** Track the time used to add and remove. */
            Stopwatch sw = new Stopwatch();
            while (j < lSize / 2) {
                ad1.addFirst(j);
                j += 1;
            }
            while (j < lSize) {
                ad1.addLast(j);
                j += 1;
            }
            while ( j > lSize / 2) {
                ad1.removeFirst();
                j -= 1;
            }
            double timeInSeconds = sw.elapsedTime();
            Ns.addLast(lSize);
            times.addLast(timeInSeconds);
            opCounts.addLast(lSize);
        }
        printTimingTable(Ns, times, opCounts);
    }
}