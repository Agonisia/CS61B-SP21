package flik;

import org.junit.Test;
import static org.junit.Assert.*;
public class FlikTest {
    @Test
    public void NumTest(){
        int i = 0;
        for (int j = 0; i < 500; ++i, ++j) {
            assertTrue("i should be equal to j", Flik.isSameNumber(i, j));
        }
    }
}
