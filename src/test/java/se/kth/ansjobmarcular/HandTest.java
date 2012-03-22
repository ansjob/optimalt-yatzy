package se.kth.ansjobmarcular;
import static org.junit.Assert.*;

import org.junit.Test;

import se.kth.ansjobmarcular.Hand;


public class HandTest {

	@Test
	public void test() {
		byte a, b, c, d, e;
		int i = 1;
		for (a = 1; a <= 6; a++) {
			for (b = a; b <= 6; b++) {
				for (c = b; c <= 6; c++) {
					for (d = c; d <= 6; d++) {
						for (e = d; e <= 6; e++) {
							assertEquals(i++, new Hand(a, b, c, d, e).getIndex());
						}
					}
				}
			}
		}
	}

}
