package se.kth.ansjobmarcular;

import static org.junit.Assert.*;

import org.junit.Test;

import se.kth.ansjobmarcular.ScoreCard;

public class ScoreCardTest {

	@Test
	public void testScoreCard() {
		ScoreCard sc = new ScoreCard();
		assertEquals(0, sc.getIndex());	
		
		sc.fillScore(Category.HOUSE);
		assertEquals(4, sc.getIndex());
		
		sc.fillScore(Category.YATZY);
		assertEquals(5, sc.getIndex());

		sc.fillScore(Category.ONES, 500);
		assertEquals(2113541, sc.getIndex());
	}

}
