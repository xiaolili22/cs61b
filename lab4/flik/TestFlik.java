package flik;

import org.junit.Test;

import static flik.Flik.isSameNumber;
import static org.junit.Assert.*;

public class TestFlik {
    @Test
    public void testSmallNumber() {
        assertTrue("2 and 2 is same number, return true", isSameNumber(2, 2));
        assertTrue("0 and 0 is same number, return true", isSameNumber(0, 0));
        assertTrue("-100 and -100 is same number, return true", Flik.isSameNumber(-100, -100));
        assertFalse("2 and 0 are not same number, return false", isSameNumber(2, 0));

    }
    @Test
    public void testBigNumber() {
        assertTrue("127 and 127 is same number, return true", isSameNumber(127, 127));
        assertTrue("128 and 128 is same number, return true", isSameNumber(128, 128));
        assertTrue("256 and 256 is same number, return true", isSameNumber(256, 256));
    }
}
