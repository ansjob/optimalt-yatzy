package se.kth.ansjobmarcular;
import static org.junit.Assert.*;

import org.junit.Test;

import se.kth.ansjobmarcular.Hand;
import se.kth.ansjobmarcular.Keeper;



public class KeeperTest {
	
	@Test
	public void testGetMask() {
		Keeper k = new Keeper(1, 2, 3, 4, 0);
		Hand h = new Hand(1, 1, 2, 3, 4);
		assertEquals(23, k.getMask(h));
		
		k = new Keeper(6,6,0,0,0);
		h = new Hand(1,2,6,6,6);
		assertEquals(6, k.getMask(h));
		
		k = new Keeper(1,2,5,0,0);
		h = new Hand(1,1,2,4,5);
		assertEquals(21, k.getMask(h));
		h = new Hand(1,1,2,5,6);
		assertEquals(22, k.getMask(h));
		
		try {
			k = new Keeper(1,2,5,0,0);
			h = new Hand(2,2,2,4,5);
			k.getMask(h);
			fail();
		} catch (RuntimeException e) {
		}
	}
}
