package se.kth.ansjobmarcular;

import static org.junit.Assert.*;

import org.junit.Test;

public class SolutionTest {
	ActionsStorage db;
	ScoreCard sc;
	byte roll;
	Hand h;

	public SolutionTest() {
		db = new FileActionsStorage();
	}

	@Test
	public void testFirstRound() {
		/* Trivial (Yatzy) keeper. */
		sc = new ScoreCard();
		h = new Hand(6, 6, 6, 6, 6);
		roll = 1;
		assertEquals(0x1f, db.suggestRoll(h, sc, roll));
		assertEquals(Category.toInt(Category.YATZY), db.suggestMarking(h, sc));
		
		/* Trivial (Yatzy) keeper mid game. */
		sc = new ScoreCard();
		sc.fillScore(Category.ONES);
		sc.fillScore(Category.SMALLSTRAIGHT);
		sc.fillScore(Category.HOUSE);
		sc.fillScore(Category.TWOS);
		sc.fillScore(Category.FOURS);
		sc.fillScore(Category.TWOPAIR);
		sc.fillScore(Category.PAIR);
		h = new Hand(6, 6, 6, 6, 6);
		roll = 1;
		assertEquals(0x1f, db.suggestRoll(h, sc, roll));
		assertEquals(Category.toInt(Category.YATZY), db.suggestMarking(h, sc));
		
		/* Test with one to keep. */
		sc = new ScoreCard();
		h = new Hand(5, 6, 6, 6, 6);
		roll = 1;
		assertEquals(0xf, db.suggestRoll(h, sc, roll));
		assertEquals(Category.toInt(Category.SIXES), db.suggestMarking(h, sc));
		
		/* Test with two to keep. */
		sc = new ScoreCard();
		h = new Hand(3, 4, 6, 6, 6);
		roll = 1;
		assertEquals(0x7, db.suggestRoll(h, sc, roll));
		assertEquals(Category.toInt(Category.THREEOFAKIND), db.suggestMarking(h, sc)); // Stämmer detta?
		
		/* Test with small straight. */
		sc = new ScoreCard();
		h = new Hand(1, 2, 3, 4, 5);
		roll = 1;
		assertEquals(0x1f, db.suggestRoll(h, sc, roll));
		assertEquals(Category.toInt(Category.SMALLSTRAIGHT), db.suggestMarking(h, sc));
		
		/* Test with large straight. */
		sc = new ScoreCard();
		h = new Hand(2, 3, 4, 5, 6);
		roll = 1;
		assertEquals(0x1f, db.suggestRoll(h, sc, roll));
		assertEquals(Category.toInt(Category.LARGESTRAIGHT), db.suggestMarking(h, sc));
	}
	


	@Test
	public void testSecondRound() {
		/* Trivial (Sixes) keeper. */
		sc = new ScoreCard();
		sc.fillScore(Category.YATZY);
		h = new Hand(6, 6, 6, 6, 6);
		roll = 1;
		assertEquals(0x1f, db.suggestRoll(h, sc, roll));
		assertEquals(Category.toInt(Category.SIXES), db.suggestMarking(h, sc));
		
		/* Test with one to keep. */
		sc = new ScoreCard();
		sc.fillScore(Category.YATZY);
		h = new Hand(5, 6, 6, 6, 6);
		roll = 1;
		assertEquals(0xf, db.suggestRoll(h, sc, roll));
		assertEquals(Category.toInt(Category.SIXES), db.suggestMarking(h, sc));
	}
}
