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

	@Test
	public void testScoreCardGeneration() {
		ScoreCard sc;
		boolean[][] variations;
		boolean[] usedIndexes = new boolean[ScoreCard.MAX_INDEX];

		for (int filled = 14; filled >= 0; filled--) {
			variations = Utils.allWaysToPut(filled, 15);

			for (boolean[] way : variations) {
				sc = new ScoreCard();
				int i = 0;
				for (Category cat : Category.values()) {
					if (way[i++])
						sc.fillScore(cat);
				}
				assertFalse(usedIndexes[sc.getIndex()]);
				usedIndexes[sc.getIndex()] = true;
			}
		}
	}

}
