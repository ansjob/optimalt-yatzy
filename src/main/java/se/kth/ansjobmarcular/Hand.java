package se.kth.ansjobmarcular;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * The hand class represents the 5 dices in a set state.
 * @author Marcus
 *
 */
public class Hand {
	public final int SIZE = 5;
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

    @Override
    public boolean equals(Object o) {
        if (o instanceof Hand){
            return Arrays.equals(dice, ((Hand)o).dice);
        }
        return false;
    }

    public double probability(Hand other) {
        int misMatches = 0;
        int[] this_counts = new int[7];
        int[] other_counts = new int[7];
        int[] needed = new int[7];
        for (int i = 0; i < SIZE; i++) {
            this_counts[this.dice[i]]++;
            other_counts[other.dice[i]]++;
        }
        for (int i = 1 ; i <= 6; i++) {
            needed[i] = other_counts[i] <= this_counts[i] ? 0: other_counts[i] - this_counts[i];
            misMatches += needed[i];
        }
        return factorial[needed[1]]
                * factorial[needed[2]]
                * factorial[needed[3]]
                * factorial[needed[4]]
                * factorial[needed[5]]
                * factorial[needed[6]] / Math.pow(6.0, misMatches);
    }



    private static final Map<Integer, Hand> getHands;
    private static final Map<Hand, Integer> getIndexes;
    private static final int[] factorial = {1,1,2, 6, 24, 120, 720};

    static {
        getHands = new HashMap<Integer, Hand>();
        getIndexes = new HashMap<Hand, Integer>();
        generate();
    }

    private static void generate() {
        int a,b,c,d,e,i;
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
