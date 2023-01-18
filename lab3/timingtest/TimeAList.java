package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeAList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
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
        timeAListConstruction();
    }

    public static void timeAListConstruction() {
        /** Instantiated array lists to track the numbers in the testing. */
        AList<Integer> Ns = new AList<>();
        AList<Double> times = new AList<>();
        AList<Integer> opCounts = new AList<>();

        for (int i = 0; i < 8; i += 1) {
            int lSize= (int) Math.pow(2, i) * 1000;
            AList<Integer> testedL = new AList<>();
            int j = 0;
            Stopwatch sw = new Stopwatch();
            while (j < lSize) {
                testedL.addLast(j);
                j += 1;
            }
            double timeInSeconds = sw.elapsedTime();
            Ns.addLast(lSize);
            times.addLast(timeInSeconds);
            opCounts.addLast(lSize);
        }
        printTimingTable(Ns, times, opCounts);
    }
}
