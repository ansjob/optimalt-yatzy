package se.kth.ansjobmarcular;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math.util.MathUtils;

/**
 * The hand class represents the 5 dices in a set state.
 * 
 * @author Marcus
 * 
 */
public class Hand {
	public static final int MAX_INDEX = 252;
	public static final int MAX_MASK = 0x1ff;
	public static final int SIZE = 5;
	private final int dice[];

	public Hand(int a, int b, int c, int d, int e) {
		dice = new int[SIZE];
		this.dice[0] = a;
		this.dice[1] = b;
		this.dice[2] = c;
		this.dice[3] = d;
		this.dice[4] = e;
		Arrays.sort(this.dice);
	}

	public Hand[] getPossibleOutcomes(int holdMask) {
		int rolled = 0;
		int outcomes, outs;
		int[] counts = new int[6];
		int[] tmp = new int[5];
		Hand[] res;

		/* Count the held, and the number of rolled dice. */
		for (int i = 0; i < 5; i++) {
			if ((holdMask & (16 >> i)) > 0)
				counts[this.dice[i] - 1]++;
			else
				rolled++;
		}

		/* If all dice are held, there's only one outcome (the current hand). */
		if (rolled == 0)
			return new Hand[] { this };

		/* Calculate how many different outcomes there are. */
		outcomes = (int) MathUtils.binomialCoefficient(6, rolled);
		res = new Hand[outcomes];

		/* Prepare the buffer that will be used to fill the outcome array with. */
		int idx = 0;
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < counts[i]; j++) {
				tmp[idx++] = i + 1;
			}
		}
		
		outs = 0;
		for (int sum = rolled; sum <= rolled * 6; sum++) {
			for (int die = 0; die < rolled; die++) {
				
			}
			res[outs++] = new Hand(tmp[0], tmp[1], tmp[2], tmp[3], tmp[4]);
		}

		return res;
	}

	public int[] getDice() {
		return Arrays.copyOf(dice, SIZE);
	}

	public static int indexOf(Hand h) {
		return getIndexes.get(h);
	}

	public static Hand getHand(int index) {
		return getHands.get(index);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(dice);
	}

	public int getIndex() {
		return getIndexes.get(this);
	}

	public double probability(Hand other, int holdMask) {
		int[] desired = new int[7];
		int[] held = new int[7];
		int[] needed = new int[7];
		int rolled = 0;
		int s = 1;
		int d;

		/* Count the number of dice of each type that are held & desired. */
		for (int i = 0; i < SIZE; i++) {
			/* See if the holdMask tells us to hold/keep this dice. */
			if ((holdMask & (16 >> i)) > 0)
				held[this.dice[i]]++;
			else
				/* If it's not held, increase the number of dice rolled. */
				rolled++;
			desired[other.getDice()[i]]++;
		}

		/* Calculate the number of dice we need to roll. */
		for (int i = 1; i <= 6; i++) {
			if (held[i] > desired[i]) {
				return 0;
			} else if (held[i] < desired[i]) {
				needed[i] = desired[i] - held[i];
			}
		}

		/* Calculate the probability of rolling those dice. */
		d = rolled;
		for (int i = 1; i <= 6; i++) {
			s *= nCr(d, needed[i]);
			d -= needed[i];
		}

		return s / Math.pow(6.0, rolled);
	}

	public static int nCr(int a, int b) {
		if (b == 0)
			return 1;
		if (a == 0)
			return 0;
		if (b > a)
			return 0;
		return factorial[a] / (factorial[b] * factorial[(a - b)]);
	}

	private static final int[] factorial = { 1, 1, 2, 6, 24, 120, 720 };

	private static final Map<Integer, Hand> getHands;
	private static final Map<Hand, Integer> getIndexes;
	static {
		getHands = new HashMap<Integer, Hand>();
		getIndexes = new HashMap<Hand, Integer>();
		generate();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Hand) {
			return Arrays.equals(dice, ((Hand) o).dice);
		}
		return false;
	}

	@Override
	public String toString() {
		return Arrays.toString(dice);
	}

	private static void generate() {
		int a, b, c, d, e, i;
		i = 0;
		for (a = 1; a <= 6; a++) {
			for (b = a; b <= 6; b++) {
				for (c = b; c <= 6; c++) {
					for (d = c; d <= 6; d++) {
						for (e = d; e <= 6; e++) {
							i++;
							Hand h = new Hand(a, b, c, d, e);
							getHands.put(i, h);
							getIndexes.put(h, i);
						}
					}
				}
			}
		}
	}
}
