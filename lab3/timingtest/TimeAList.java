package timingtest;

import edu.princeton.cs.algs4.Stopwatch;
import java.math.BigDecimal;
import java.math.RoundingMode;
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

            BigDecimal bd = new BigDecimal(timePerOp);
            bd = bd.setScale(2, RoundingMode.DOWN);

            System.out.printf("%12d %12.2f %12d %12s\n", N, time, opCount, bd.toString());
        }
    }

    public static void main(String[] args) {
        timeAListConstruction();
    }

    public static void timeAListConstruction() {
        // TODO: YOUR CODE HERE
        int initialN = 1000;
        int LIMIT = 1024000;

        AList<Integer> Ns = new AList<>();
        AList<Double> times = new AList<>();
        AList<Integer> opCounts = new AList<>();

        int N = initialN;
        while (N <=  LIMIT){
            AList<Integer> a = new AList<>();
            Stopwatch sw = new Stopwatch();

            for(int i = 0 ; i < N; i++){
                a.addLast(i);
            }
            double timeInSeconds = sw.elapsedTime();
            Ns.addLast(N);
            opCounts.addLast(N);
            times.addLast(timeInSeconds);

            N *= 2;
        }

        printTimingTable(Ns, times, opCounts);
    }
}
