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
	int dice[];

	public Hand() {
		dice = new int[SIZE];
	}

	public Hand(int a, int b, int c, int d, int e) {
		dice = new int[SIZE];
		this.dice[0] = a;
		this.dice[1] = b;
		this.dice[2] = c;
		this.dice[3] = d;
		this.dice[4] = e;
		Arrays.sort(this.dice);
	}

	public void setDice(int a, int b, int c, int d, int e) {
		this.dice[0] = a;
		this.dice[1] = b;
		this.dice[2] = c;
		this.dice[3] = d;
		this.dice[4] = e;
		Arrays.sort(this.dice);
	}

	public int[] getDice() {
		return dice;
	}
	
	public int getIndex() {
		return getIndexes.get(this);
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


    private static final Map<Integer, Hand> getHands;
    private static final Map<Hand, Integer> getIndexes;

    static {
        getHands = new HashMap<Integer, Hand>();
        getIndexes = new HashMap<Hand, Integer>();
        generate();
        System.out.println("Genererade " + getHands.values().size() + " händer ");
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
