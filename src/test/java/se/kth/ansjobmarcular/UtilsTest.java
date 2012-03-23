/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.ansjobmarcular;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ansjob
 */
public class UtilsTest {

    @Test
    public void TestOne() {
        Boolean[] bools = {true};
        assertEquals(1, Utils.fromBooleanArray(bools));
    }

    @Test
    public void TestTwo() {
        Boolean[] bools = {true, false};
        assertEquals(2, Utils.fromBooleanArray(bools));
    }

    @Test
    public void Test42() {
        Boolean[] bools = {true, false, true, false, true, false};
        assertEquals(42, Utils.fromBooleanArray(bools));
    }

    @Test
    public void OneToBoolean() {
        int x = 1;
        int size = 4;
        Boolean[] expected = {false, false, false, true};
        assertArrayEquals(expected, Utils.fromInt(x, size));
    }

    @Test
    public void FiveToBoolean() {
        int x = 5;
        int size = 4;
        Boolean[] expected = {false, true, false, true};
        assertArrayEquals(expected, Utils.fromInt(x, size));
    }

    @Test
    public void bigTest() {
        for (int i = 0; i < 1024; i++) {
            assertEquals(i, Utils.fromBooleanArray(Utils.fromInt(i, 32)));
        }
    }

}

