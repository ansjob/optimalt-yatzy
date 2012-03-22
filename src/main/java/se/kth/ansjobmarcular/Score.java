package se.kth.ansjobmarcular;

import java.util.Arrays;

public class Score {

	/**
	 * Calculate the score value for a given hand in the given score type.
	 * 
	 * @param hand
	 *            The hand to be evaluated.
	 * @param sp
	 *            The intended score card type to be filled in, Pairs, Twos,
	 *            etc.
	 * @return The numeric score of the hand, if filled in as the given type.
	 */
	public static int value(Hand hand, Category sp) {
		switch (sp) {
		case ONES:
		case TWOS:
		case THREES:
		case FOURS:
		case FIVES:
		case SIXES:
			return scoreNumbers(hand, sp);

		case PAIR:
			return scorePair(hand, 1);
		case TWOPAIR:
			return scorePair(hand, 2);

		case THREEOFAKIND:
			return scoreKind(hand, 3);
		case FOUROFAKIND:
			return scoreKind(hand, 4);

		case SMALLSTRAIGHT:
			return scoreStraight(hand, Category.SMALLSTRAIGHT);
		case LARGESTRAIGHT:
			return scoreStraight(hand, Category.LARGESTRAIGHT);

		case HOUSE:
			return scoreHouse(hand);

		case YATZY:
			return scoreYatzy(hand);

		case CHANCE:
			return scoreChance(hand);

		default:
			return 0;
		}
	}

	/**
	 * Calculate the numbers (specified) score for a hand.
	 * 
	 * @param hand
	 *            Hand to be evaluated.
	 * @param cat
	 *            Category used for the hand (ONES for example).
	 * @return Score.
	 */
	private static int scoreNumbers(Hand hand, Category cat) {
		switch (cat) {
		case ONES:
			return count(hand, 1);
		case TWOS:
			return count(hand, 2) * 2;
		case THREES:
			return count(hand, 3) * 3;
		case FOURS:
			return count(hand, 4) * 4;
		case FIVES:
			return count(hand, 5) * 5;
		case SIXES:
			return count(hand, 6) * 6;
		default:
			return 0;
		}
	}

	/**
	 * Calculate the chance score for a hand.
	 * 
	 * @param hand
	 *            Hand to be evaluated.
	 * @return Score.
	 */
	private static int scoreChance(Hand hand) {
		int sum = 0;
		for (int i : hand.getDice())
			sum += i;
		return sum;
	}

	/**
	 * Calculate the yatzy score for a hand.
	 * 
	 * @param hand
	 *            Hand to be evaluated.
	 * @return Score.
	 */
	private static int scoreYatzy(Hand hand) {
		int count = 1;
		int prev = 0;
		for (int i : hand.getDice()) {
			if (prev == i)
				count++;
			prev = i;
		}

		if (count == 5)
			return 50;
		else
			return 0;
	}

	/**
	 * Calculate the house score of a hand.
	 * 
	 * @param hand
	 *            Hand to be evaluated.
	 * @return Score.
	 */
	private static int scoreHouse(Hand hand) {
		int val = 0;
		int prev = 0;
		int count = 0;

		for (int i : hand.getDice()) {
			if (i == prev || prev == 0) {
				count++;
			} else {
				if (count != 2 && count != 3)
					return 0;
				val += count * prev;
				count = 1;
			}
			prev = i;
		}
		if (count != 2 && count != 3)
			return 0;
		val += count * prev;

		return val;
	}

	/**
	 * Calculate the straight score of a hand.
	 * 
	 * @param hand
	 *            Hand to be evaluated.
	 * @param type
	 *            The type of straight (big/small) to be calculated.
	 * @return Score.
	 */
	private static int scoreStraight(Hand hand, Category type) {
		if (type == Category.SMALLSTRAIGHT
				&& Arrays.equals(hand.getDice(), new int[] { 1, 2, 3, 4, 5 }))
			return 15;
		if (type == Category.LARGESTRAIGHT
				&& Arrays.equals(hand.getDice(), new int[] { 2, 3, 4, 5, 6 }))
			return 20;
		return 0;
	}

	/**
	 * Calculate the 3-, 4-of-a-kind score of a hand.
	 * 
	 * @param hand
	 *            Hand to be evaluated.
	 * @param no
	 *            Number of dice that should be the same (3 for THREEOFAKIND,
	 *            etc).
	 * @return Score.
	 */
	private static int scoreKind(Hand hand, int no) {
		int val = 0;
		int count = 1;
		int prev = 0;

		for (int i : hand.getDice()) {
			if (i == prev)
				count++;
			else
				count = 1;
			prev = i;
			if (count == no)
				val = no * prev;
		}

		return val;
	}

	/**
	 * Calculates the (two-)pair score of a hand.
	 * 
	 * @param hand
	 *            Hand to be evaluated.
	 * @param no
	 *            Number of pairs to calculate for.
	 * @return Score.
	 */
	private static int scorePair(Hand hand, int no) {
		int max[] = new int[no];
		int prev = 0;
		int idx = 0;

		for (int i : hand.getDice()) {
			if (prev == i && max[idx] < i) {
				max[idx++] = i;
				idx = (idx + 1) % no;
			}
			prev = i;
		}

		if (no == 2)
			return (max[0] * 2 + max[1] * 2);
		else
			return (max[0] * 2);
	}

	/**
	 * Count the occurrences of given number in given hand.
	 * 
	 * @param hand
	 *            The hand in which the dice will be counted.
	 * @param number
	 *            The (dice) number to be counted.
	 * @return Occurrences of <i>number</i> in <i>hand</i>.
	 */
	private static int count(Hand hand, int number) {
		int val = 0;
		for (int i : hand.getDice()) {
			if (i == number)
				val += 1;
		}
		return val;
	}
}
