/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.ansjobmarcular;

/**
 *
 * @author ansjob
 */
public class Utils {

    public static int fromBooleanArray(Boolean[] bools) {
        if (bools.length > 32) {
            throw new IllegalArgumentException();
        }
        int res = 0;
        for (int i = 0; i < bools.length; i++) {
            if (bools[i]) {
                res |= 1 << (bools.length - i -1);
            }
        }
        return res;
    }

    public static Boolean[] fromInt(int x, int size) {
        Boolean[] res = new Boolean[size];

        for (int i = 0; i < size; i++) {
            res[i] = (x & (1 << size - i -1)) != 0;
        }

        return res;
    }

}
