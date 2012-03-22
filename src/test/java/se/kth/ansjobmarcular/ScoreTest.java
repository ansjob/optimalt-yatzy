package se.kth.ansjobmarcular;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runners.Suite;
import se.kth.ansjobmarcular.Hand;
import se.kth.ansjobmarcular.Score;

public class ScoreTest {

	@Test
	public void testNumbers() {
		assertEquals(5, Score.value(new Hand(1, 1, 1, 1, 1), Score.Type.ACES));
		assertEquals(4, Score.value(new Hand(1, 1, 1, 1, 6), Score.Type.ACES));
		assertEquals(3, Score.value(new Hand(1, 4, 1, 2, 1), Score.Type.ACES));
		assertEquals(2, Score.value(new Hand(2, 1, 3, 1, 2), Score.Type.ACES));
		assertEquals(1, Score.value(new Hand(2, 6, 6, 1, 2), Score.Type.ACES));

		assertEquals(0, Score.value(new Hand(1, 1, 4, 1, 1), Score.Type.TWOS));
		assertEquals(2, Score.value(new Hand(1, 1, 1, 1, 2), Score.Type.TWOS));
		assertEquals(4, Score.value(new Hand(1, 2, 3, 2, 1), Score.Type.TWOS));
		assertEquals(6, Score.value(new Hand(2, 1, 2, 5, 2), Score.Type.TWOS));
		assertEquals(8, Score.value(new Hand(2, 2, 2, 1, 2), Score.Type.TWOS));

		assertEquals(0, Score.value(new Hand(1, 1, 4, 1, 1), Score.Type.THREES));
		assertEquals(9, Score.value(new Hand(1, 3, 3, 3, 2), Score.Type.THREES));
		assertEquals(18, Score.value(new Hand(6, 6, 3, 6, 1), Score.Type.SIXES));
		assertEquals(10, Score.value(new Hand(2, 5, 2, 5, 2), Score.Type.FIVES));
		assertEquals(12, Score.value(new Hand(2, 4, 4, 1, 4), Score.Type.FOURS));
	}

	@Test
	public void testPairs() {
		assertEquals(8, Score.value(new Hand(1, 4, 3, 4, 6), Score.Type.PAIR));
		assertEquals(12, Score.value(new Hand(4, 6, 4, 4, 6), Score.Type.PAIR));
		assertEquals(6, Score.value(new Hand(3, 2, 3, 1, 3), Score.Type.PAIR));
		assertEquals(0, Score.value(new Hand(1, 2, 3, 4, 5), Score.Type.PAIR));
	}

	@Test
	public void testKinds() {
		assertEquals(12, Score.value(new Hand(1, 4, 4, 4, 6), Score.Type.THREEOFAKIND));
		assertEquals(0, Score.value(new Hand(1, 2, 4, 4, 6), Score.Type.THREEOFAKIND));
		assertEquals(0, Score.value(new Hand(1, 2, 4, 4, 6), Score.Type.FOUROFAKIND));
		assertEquals(4, Score.value(new Hand(1, 1, 1, 1, 6), Score.Type.FOUROFAKIND));
		assertEquals(24, Score.value(new Hand(6, 6, 5, 6, 6), Score.Type.FOUROFAKIND));
	}

	@Test
	public void testStraights() {
		assertEquals(15, Score.value(new Hand(1, 2, 4, 3, 5), Score.Type.SMALLSTRAIGHT));
		assertEquals(0, Score.value(new Hand(1, 1, 1, 1, 1), Score.Type.SMALLSTRAIGHT));
		assertEquals(20, Score.value(new Hand(2, 3, 5, 4, 6), Score.Type.BIGSTRAIGHT));
		assertEquals(0, Score.value(new Hand(1, 1, 1, 1, 6), Score.Type.BIGSTRAIGHT));
		assertEquals(0, Score.value(new Hand(6, 6, 6, 6, 6), Score.Type.BIGSTRAIGHT));
	}

	@Test
	public void testHouses() {
		assertEquals(7, Score.value(new Hand(1, 1, 1, 2, 2), Score.Type.HOUSE));
		assertEquals(28, Score.value(new Hand(6, 6, 6, 5, 5), Score.Type.HOUSE));
		assertEquals(0, Score.value(new Hand(2, 3, 5, 4, 6), Score.Type.HOUSE));
		assertEquals(0, Score.value(new Hand(1, 1, 1, 5, 6), Score.Type.HOUSE));
		assertEquals(0, Score.value(new Hand(6, 6, 6, 6, 6), Score.Type.HOUSE));
	}

	@Test
	public void testYatzy() {
		assertEquals(50, Score.value(new Hand(1, 1, 1, 1, 1), Score.Type.YATZY));
		assertEquals(50, Score.value(new Hand(6, 6, 6, 6, 6), Score.Type.YATZY));
		assertEquals(50, Score.value(new Hand(2, 2, 2, 2, 2), Score.Type.YATZY));
		assertEquals(0, Score.value(new Hand(1, 1, 1, 1, 6), Score.Type.YATZY));
		assertEquals(0, Score.value(new Hand(6, 6, 4, 6, 6), Score.Type.YATZY));
	}

	 @Test
	 public void testChance() {
			assertEquals(5, Score.value(new Hand(1, 1, 1, 1, 1), Score.Type.CHANCE));
			assertEquals(30, Score.value(new Hand(6, 6, 6, 6, 6), Score.Type.CHANCE));
			assertEquals(10, Score.value(new Hand(2, 2, 2, 2, 2), Score.Type.CHANCE));
			assertEquals(16, Score.value(new Hand(1, 2, 3, 4, 6), Score.Type.CHANCE));
	 }

}
