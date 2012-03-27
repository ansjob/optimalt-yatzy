package se.kth.ansjobmarcular;

import java.io.Serializable;

public class ScoreCard implements Serializable {
	/* 64 << 15 + 2^15 = 2129919 */
	public static final int	MAX_INDEX = 2129919;
	private int	upperTotal;
	private int	filled;
	
	public ScoreCard() {
		upperTotal = 0;
		filled = 0;
	}
	
	public ScoreCard(int filled, int upperTotal) {
		this.filled = filled;
		this.upperTotal = upperTotal;
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new ScoreCard(filled, upperTotal);
	}
	
	public boolean isFilled(Category cat) {
		switch (cat) {
		case ONES:
			return (filled & (1 << 14)) > 0;
		case TWOS:
			return (filled & (1 << 13)) > 0;
		case THREES:
			return (filled & (1 << 12)) > 0;
		case FOURS:
			return (filled & (1 << 11)) > 0;
		case FIVES:
			return (filled & (1 << 10)) > 0;
		case SIXES:
			return (filled & (1 << 9)) > 0;
		case PAIR:
			return (filled & (1 << 8)) > 0;
		case TWOPAIR:
			return (filled & (1 << 7)) > 0;
		case THREEOFAKIND:
			return (filled & (1 << 6)) > 0;
		case FOUROFAKIND:
			return (filled & (1 << 5)) > 0;
		case SMALLSTRAIGHT:
			return (filled & (1 << 4)) > 0;
		case LARGESTRAIGHT:
			return (filled & (1 << 3)) > 0;
		case HOUSE:
			return (filled & (1 << 2)) > 0;
		case CHANCE:
			return (filled & (1 << 1)) > 0;
		case YATZY:
			return (filled & (1 << 0)) > 0;
		default:
			return false;
		}
	}
	
	public int getUpper() {
		return upperTotal;
	}

	public void addScore(int score) {
		upperTotal += score;
		if (upperTotal > 64)
			upperTotal = 64;
	}
	
	public void fillScore(Category cat) {
		fillScore(cat, 0);
	}
	
	public void fillScore(Category cat, int score) {
		switch (cat) {
		case ONES:
			filled |= (1 << 14);
			addScore(score);
			break;
		case TWOS:
			filled |= (1 << 13);
			addScore(score);
			break;
		case THREES:
			filled |= (1 << 12);
			addScore(score);
			break;
		case FOURS:
			filled |= (1 << 11);
			addScore(score);
			break;
		case FIVES:
			filled |= (1 << 10);
			addScore(score);
			break;
		case SIXES:
			filled |= (1 << 9);
			addScore(score);
			break;
		case PAIR:
			filled |= (1 << 8);
			break;
		case TWOPAIR:
			filled |= (1 << 7);
			break;
		case THREEOFAKIND:
			filled |= (1 << 6);
			break;
		case FOUROFAKIND:
			filled |= (1 << 5);
			break;
		case SMALLSTRAIGHT:
			filled |= (1 << 4);
			break;
		case LARGESTRAIGHT:
			filled |= (1 << 3);
			break;
		case HOUSE:
			filled |= (1 << 2);
			break;
		case CHANCE:
			filled |= (1 << 1);
			break;
		case YATZY:
			filled |= 1;
			break;
		}
	}
	
	public int getIndex() {
		return ((byte)upperTotal << 15) + filled;
	}
	
	@Override
	public String toString() {
		return "" + getIndex();
	}
	
	public static final ScoreCard[] scoreCards;
	static {
		scoreCards = new ScoreCard[ScoreCard.MAX_INDEX + 1];
		//generate();
	}
	
	private static void generate() {
		ScoreCard sc;
		boolean[][] ways;
		int idx = 0;
		
		for (int i = 0; i <= 15; i++) {
			for (int j = 0; j <= 64; j++) {
				ways = Utils.allWaysToPut(i, 15);
				for (boolean[] way : ways) {
					sc = new ScoreCard();
					sc.addScore(j);
					for (int k = 0; k < way.length; k++) {
						if (way[k])
							sc.fillScore(Category.values()[k]);
					}
					if (scoreCards[idx] != null)
						throw new RuntimeException();
					scoreCards[idx++] = sc;
				}
			}
		}
	}
	
	@Override
	public int hashCode() {
		return getIndex();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ScoreCard) {
			return ((ScoreCard)obj).getIndex() == this.getIndex();
		}
		return false;
	}
}