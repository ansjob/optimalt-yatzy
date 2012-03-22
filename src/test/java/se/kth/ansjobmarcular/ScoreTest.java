package se.kth.ansjobmarcular;

import static org.junit.Assert.*;

import org.junit.Test;
import se.kth.ansjobmarcular.Hand;
import se.kth.ansjobmarcular.Score;

public class ScoreTest {

	@Test
	public void testNumbers() {
		assertEquals(5, Score.value(new Hand(1, 1, 1, 1, 1), Category.ONES));
		assertEquals(4, Score.value(new Hand(1, 1, 1, 1, 6), Category.ONES));
		assertEquals(3, Score.value(new Hand(1, 4, 1, 2, 1), Category.ONES));
		assertEquals(2, Score.value(new Hand(2, 1, 3, 1, 2), Category.ONES));
		assertEquals(1, Score.value(new Hand(2, 6, 6, 1, 2), Category.ONES));

		assertEquals(0, Score.value(new Hand(1, 1, 4, 1, 1), Category.TWOS));
		assertEquals(2, Score.value(new Hand(1, 1, 1, 1, 2), Category.TWOS));
		assertEquals(4, Score.value(new Hand(1, 2, 3, 2, 1), Category.TWOS));
		assertEquals(6, Score.value(new Hand(2, 1, 2, 5, 2), Category.TWOS));
		assertEquals(8, Score.value(new Hand(2, 2, 2, 1, 2), Category.TWOS));

		assertEquals(0, Score.value(new Hand(1, 1, 4, 1, 1), Category.THREES));
		assertEquals(9, Score.value(new Hand(1, 3, 3, 3, 2), Category.THREES));
		assertEquals(18, Score.value(new Hand(6, 6, 3, 6, 1), Category.SIXES));
		assertEquals(10, Score.value(new Hand(2, 5, 2, 5, 2), Category.FIVES));
		assertEquals(12, Score.value(new Hand(2, 4, 4, 1, 4), Category.FOURS));
	}

	@Test
	public void testPairs() {
		assertEquals(8, Score.value(new Hand(1, 4, 3, 4, 6), Category.PAIR));
		assertEquals(12, Score.value(new Hand(4, 6, 4, 4, 6), Category.PAIR));
		assertEquals(6, Score.value(new Hand(3, 2, 3, 1, 3), Category.PAIR));
		assertEquals(0, Score.value(new Hand(1, 2, 3, 4, 5), Category.PAIR));
	}

	@Test
	public void testKinds() {
		assertEquals(12, Score.value(new Hand(1, 4, 4, 4, 6), Category.THREEOFAKIND));
		assertEquals(0, Score.value(new Hand(1, 2, 4, 4, 6), Category.THREEOFAKIND));
		assertEquals(0, Score.value(new Hand(1, 2, 4, 4, 6), Category.FOUROFAKIND));
		assertEquals(4, Score.value(new Hand(1, 1, 1, 1, 6), Category.FOUROFAKIND));
		assertEquals(24, Score.value(new Hand(6, 6, 5, 6, 6), Category.FOUROFAKIND));
	}

	@Test
	public void testStraights() {
		assertEquals(15, Score.value(new Hand(1, 2, 4, 3, 5), Category.SMALLSTRAIGHT));
		assertEquals(0, Score.value(new Hand(1, 1, 1, 1, 1), Category.SMALLSTRAIGHT));
		assertEquals(20, Score.value(new Hand(2, 3, 5, 4, 6), Category.LARGESTRAIGHT));
		assertEquals(0, Score.value(new Hand(1, 1, 1, 1, 6), Category.LARGESTRAIGHT));
		assertEquals(0, Score.value(new Hand(6, 6, 6, 6, 6), Category.LARGESTRAIGHT));
	}

	@Test
	public void testHouses() {
		assertEquals(7, Score.value(new Hand(1, 1, 1, 2, 2), Category.HOUSE));
		assertEquals(28, Score.value(new Hand(6, 6, 6, 5, 5), Category.HOUSE));
		assertEquals(0, Score.value(new Hand(2, 3, 5, 4, 6), Category.HOUSE));
		assertEquals(0, Score.value(new Hand(1, 1, 1, 5, 6), Category.HOUSE));
		assertEquals(0, Score.value(new Hand(6, 6, 6, 6, 6), Category.HOUSE));
	}

	@Test
	public void testYatzy() {
		assertEquals(50, Score.value(new Hand(1, 1, 1, 1, 1), Category.YATZY));
		assertEquals(50, Score.value(new Hand(6, 6, 6, 6, 6), Category.YATZY));
		assertEquals(50, Score.value(new Hand(2, 2, 2, 2, 2), Category.YATZY));
		assertEquals(0, Score.value(new Hand(1, 1, 1, 1, 6), Category.YATZY));
		assertEquals(0, Score.value(new Hand(6, 6, 4, 6, 6), Category.YATZY));
	}

	 @Test
	 public void testChance() {
			assertEquals(5, Score.value(new Hand(1, 1, 1, 1, 1), Category.CHANCE));
			assertEquals(30, Score.value(new Hand(6, 6, 6, 6, 6), Category.CHANCE));
			assertEquals(10, Score.value(new Hand(2, 2, 2, 2, 2), Category.CHANCE));
			assertEquals(16, Score.value(new Hand(1, 2, 3, 4, 6), Category.CHANCE));
	 }

}
