/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.ansjobmarcular;

import org.apache.commons.math.util.MathUtils;

/**
 *
 * @author ansjob
 */
public class Utils {

    public static int fromBooleanArray(boolean[] bools) {
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

    public static boolean[] fromInt(int x, int size) {
        boolean[] res = new boolean[size];

        for (int i = 0; i < size; i++) {
            res[i] = (x & (1 << size - i -1)) != 0;
        }

        return res;
    }
    
    public static boolean[][] allWaysToPut(int trues, int size) {
    	int ways = (int) MathUtils.binomialCoefficient(size, trues);
    	boolean[][] res = new boolean[ways][size];

    	/* Base case: set the rightmost SIZE values to true */
    	for (int i = 0; i < trues; i++) {
    		res[0][size - i -1] = true;
    	}
    	
    	for (int i = 1; i < ways; i++) {
    		/* Först kopierar vi förra steget */
    		for (int j = 0; j < size; j++) {
    			res[i][j] = res[i-1][j];
    		}
    		
    		int j;
    		/* Sedan letar vi efter den vänstraste som är true*/
    		for (j = 0; j < size; j++) {
    			if (res[i][j]){
    				break;
    			}
    		}
    		
    		if (j > 0) {
    			res[i][j-1] = true;
    			res[i][j] = false;
    		}
    		else {
    			/* Find the next [false, true] sequence and count ones inbetween */
    			int count = 1;
    			int k;
    			for (k = 1; k < size-1; k++) {
    				if (res[i][k]) count++;
    				if (res[i][k] == false && res[i][k+1]) {
    					break;
    				}
    			}
    			/* Set the false values first*/
    			for (int q = 0; q < k - count; q++) {
    				res[i][q] = false;
    			}
    			/* Then the trues */
    			for (int q = k-count; q <= k; q++) {
    				res[i][q] = true;
    			}
    			/* Then we'll make the last switch */
    			res[i][k+1] = false;
    		}
    	}
    	
    	return res;
    }

}
