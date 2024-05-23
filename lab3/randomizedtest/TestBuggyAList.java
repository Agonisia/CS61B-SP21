package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove(){
        BuggyAList<Integer> ba = new BuggyAList<>();
        AListNoResizing<Integer> anr = new AListNoResizing<>();

        for (int i =4; i < 7; i++){
            ba.addLast(i);
            anr.addLast(i);
        }

        for (int i =0; i <3; i++){
            assertEquals(ba.removeLast(), anr.removeLast());
        }
    }

    @Test
    public void randomizedTest(){
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> B = new BuggyAList<>();

        int N = 500;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                B.addLast(randVal);

            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                int size_b = B.size();

                assertEquals(size, size_b);
            }else if (L.size() > 0 && operationNumber == 2) {
                int res = L.getLast();
                int res_b = B.getLast();

                assertEquals(res, res_b);
                // 3: removeLast
            } else if (L.size() > 0 && operationNumber == 3) {
                int res = L.removeLast();
                int res_b = B.removeLast();

                assertEquals(res, res_b);
            }
        }
    }
}
