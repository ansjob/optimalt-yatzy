package se.kth.ansjobmarcular;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.RuntimeErrorException;

@SuppressWarnings("unchecked")
public class Keeper {
	public static final int MAX_INDEX = 462;
	private static Keeper[] keepers = new Keeper[MAX_INDEX];
	
	private static List<Keeper>[] keepersM = (List<Keeper>[]) new List<?>[6];
	
	private static Map<Keeper, Integer> indexes = new HashMap<Keeper, Integer>();
	private int count = 0;
	private int[] dice = new int[7];

	private Keeper() {

	}

	static {
		for (int j = 0; j <keepersM.length; j++) {
			keepersM[j] = new ArrayList<Keeper>(Hand.MAX_INDEX);
		}
		int i = 0;
		for (int a = 0; a <= 6; a++) {
			for (int b = a; b <= 6; b++) {
				for (int c = b; c <= 6; c++) {
					for (int d = c; d <= 6; d++) {
						for (int e = d; e <= 6; e++) {
							Keeper k = new Keeper(a, b, c, d, e);
							keepers[i] = k;
							//Utils.debug("Generated keeper[%d]: %s\n", i, keepers[i]);
							indexes.put(keepers[i], i);
							keepersM[k.count].add(k);
							i++;
						}
					}
				}
			}
		}
	}
	
	public static List<Keeper> getKeepers(int cardinality) {
		return keepersM[cardinality];
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + count;
		result = prime * result + Arrays.hashCode(dice);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Keeper))
			return false;
		Keeper other = (Keeper) obj;
		if (count != other.count)
			return false;
		if (!Arrays.equals(dice, other.dice))
			return false;
		return true;
	}

	public int getMask(Hand hand) {
		int mask = 0;
		int[] h = hand.getDice();
		int local[] = Arrays.copyOf(dice, dice.length);
		
		int i = 0;
		int c = count;
		int d = 1;
		while (c > 0) {
			/* Find the first die needed. */
			while (local[d] == 0)
				d++;
			/* d is now the die needed. */
			
			if (h[i] == d) {
				mask |= (1 << (4-i));
				c--;
				local[d]--;
			}
			i++;
			
			if (i == 5 && c > 0)
				return -1;
		}
		return mask;
	}

	public Keeper add(int d) {
		Keeper other = new Keeper();
		other.count = this.count + 1;
		other.dice = Arrays.copyOf(this.dice, this.dice.length);
		other.dice[d]++;
		return other;
	}

	public int getCount() {
		return count;
	}

	public int[] getDice() {
		return dice;
	}

	public int getIndex() {
		return indexes.get(this);
	}

	public Keeper getKeeper(int index) {
		return keepers[index];
	}

	public Keeper(Hand hand, int mask) {
		int[] tmp = hand.getDice();
		for (int i = 0; i < 5; i++) {
			if ((mask & (1 << (4 - i))) != 0) {
				dice[tmp[i]]++;
				count++;
			}
		}
	}

	public Keeper(int a, int b, int c, int d, int e) {
		if (a != 0) {
			count++;
			dice[a]++;
		}
		if (b != 0) {
			count++;
			dice[b]++;
		}
		if (c != 0) {
			count++;
			dice[c]++;
		}
		if (d != 0) {
			count++;
			dice[d]++;
		}
		if (e != 0) {
			count++;
			dice[e]++;
		}
	}
	
	public String toString() {
		return Arrays.toString(dice);
	}
}
