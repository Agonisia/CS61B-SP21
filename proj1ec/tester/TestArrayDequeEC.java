package tester;

import static org.junit.Assert.*;
import org.junit.Test;
import student.StudentArrayDeque;
import edu.princeton.cs.introcs.StdRandom;
import java.util.ArrayDeque;

public class TestArrayDequeEC {

    @Test
    public void testAddFirst() {
        StudentArrayDeque<Integer> deque = new StudentArrayDeque<>();
        deque.addFirst(3);
        deque.addFirst(2);
        assertEquals("addFirst(3)\naddFirst(2)\n", 2, (int) deque.get(0));
        assertEquals("addFirst(3)\naddFirst(2)\n", 3, (int) deque.get(1));
    }

    @Test
    public void testAddLast() {
        StudentArrayDeque<Integer> deque = new StudentArrayDeque<>();
        deque.addLast(3);
        deque.addLast(4);
        assertEquals("addLast(3)\naddLast(4)\n", 3, (int) deque.get(0));
        assertEquals("addLast(3)\naddLast(4)\n", 4, (int) deque.get(1));
    }

    @Test
    public void testRemoveFirst() {
        StudentArrayDeque<Integer> deque = new StudentArrayDeque<>();
        deque.addLast(3);
        deque.addLast(4);
        assertEquals("addLast(3)\naddLast(4)\nremoveFirst()\n", 3, (int) deque.removeFirst());
        assertEquals("addLast(3)\naddLast(4)\nremoveFirst()\n", 1, deque.size());
        assertEquals("addLast(3)\naddLast(4)\nremoveFirst()\n", 4, (int) deque.get(0));
    }

    @Test
    public void testRemoveLast() {
        StudentArrayDeque<Integer> deque = new StudentArrayDeque<>();
        deque.addLast(3);
        deque.addLast(4);
        assertEquals("addLast(3)\naddLast(4)\nremoveLast()\n", 4, (int) deque.removeLast());
        assertEquals("addLast(3)\naddLast(4)\nremoveLast()\n", 1, deque.size());
        assertEquals("addLast(3)\naddLast(4)\nremoveLast()\n", 3, (int) deque.get(0));
    }

    @Test
    public void testSize() {
        StudentArrayDeque<Integer> deque = new StudentArrayDeque<>();
        assertEquals("initial size()\n", 0, deque.size());
        deque.addLast(3);
        deque.addLast(4);
        assertEquals("addLast(3)\naddLast(4)\n", 2, deque.size());
        deque.removeFirst();
        assertEquals("addLast(3)\naddLast(4)\nremoveFirst()\n", 1, deque.size());
    }

    @Test
    public void testIsEmpty() {
        StudentArrayDeque<Integer> deque = new StudentArrayDeque<>();
        assertTrue("initial isEmpty()\n", deque.isEmpty());
        deque.addLast(3);
        assertFalse("addLast(3)\n", deque.isEmpty());
        deque.removeFirst();
        assertTrue("addLast(3)\nremoveFirst()\n", deque.isEmpty());
    }

    private String printFailureSequence(ArrayDeque<String> sequence) {
        StringBuilder sequenceString = new StringBuilder();
        for (String operation : sequence) {
            sequenceString.append(operation).append("\n");
        }
        return sequenceString.toString();
    }

    @Test
    public void RandomizedTest() {
        int N = 100;
        ArrayDequeSolution<Integer> solution = new ArrayDequeSolution<>();
        ArrayDeque<String> failureSequence = new ArrayDeque<>();
        StudentArrayDeque<Integer> buggy = new StudentArrayDeque<>();

        for (int i = 0; i < N; i++) {
            int randomOperation = StdRandom.uniform(0, 4);

            if (randomOperation == 0) {
                solution.addFirst(i);
                buggy.addFirst(i);
                failureSequence.addLast("addFirst(" + i + ")");
            } else if (randomOperation == 1) {
                solution.addLast(i);
                buggy.addLast(i);
                failureSequence.addLast("addLast(" + i + ")");
            }

            if (solution.size() > 0) {
                if (randomOperation == 2) {
                    Integer removeFromSolution = solution.removeFirst();
                    Integer removeFromBuggy = buggy.removeFirst();
                    failureSequence.addLast("removeFirst()");
                    assertEquals(printFailureSequence(failureSequence), removeFromSolution, removeFromBuggy);
                } else if (randomOperation == 3) {
                    Integer removeFromSolution = solution.removeLast();
                    Integer removeFromBuggy = buggy.removeLast();
                    failureSequence.addLast("removeLast()");
                    assertEquals(printFailureSequence(failureSequence), removeFromSolution, removeFromBuggy);
                }
            }
        }
    }
}
