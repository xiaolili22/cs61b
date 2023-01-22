package deque;
import edu.princeton.cs.algs4.Stopwatch;

public class TimeLinkedListDeque {
    private static void printTimingTable(ArrayDeque<Integer> ns,
                                         ArrayDeque<Double> times,
                                         ArrayDeque<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < ns.size(); i += 1) {
            int N = ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeLinkedListDequeConstruction();
    }

    public static void timeLinkedListDequeConstruction() {
        /** Instantiated array lists to track the numbers in the testing. */
        ArrayDeque<Integer> ns = new ArrayDeque<>();
        ArrayDeque<Double> times = new ArrayDeque<>();
        ArrayDeque<Integer> opCounts = new ArrayDeque<>();

        for (int i = 0; i < 16; i += 1) {
            int lSize = (int) Math.pow(2, i) * 1000;
            LinkedListDeque<Integer> lld1 = new LinkedListDeque<>();
            int j = 0;
            /** Track the time used to add and remove. */
            Stopwatch sw = new Stopwatch();
            while (j < lSize / 2) {
                lld1.addFirst(j);
                j += 1;
            }
            while (j < lSize) {
                lld1.addLast(j);
                j += 1;
            }
            while (j > lSize / 2) {
                lld1.removeFirst();
                j -= 1;
            }
            double timeInSeconds = sw.elapsedTime();
            ns.addLast(lSize);
            times.addLast(timeInSeconds);
            opCounts.addLast(lSize);
        }
        printTimingTable(ns, times, opCounts);
    }
}
