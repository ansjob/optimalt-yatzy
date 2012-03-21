package se.kth.ansjobmarcular;

import java.util.Arrays;

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
}
