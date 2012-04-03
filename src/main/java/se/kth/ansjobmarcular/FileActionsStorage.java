package se.kth.ansjobmarcular;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FileActionsStorage implements ActionsStorage {

	private static final long MAX_INDEX = 3 * (Hand.MAX_INDEX + 1) * (ScoreCard.MAX_INDEX + 1);

	private RandomAccessFile fp;

	public FileActionsStorage() {
		File file = new File("/tmp/actions");
		try {
			fp = new RandomAccessFile(file, "rw");
//			System.out.printf("Max index: %d\n", MAX_INDEX);
			fp.setLength(MAX_INDEX);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public byte suggestRoll(Hand currentHand, ScoreCard currentScore,
			int roll) {
		long index = getIndex(currentScore, currentHand, roll);
		int b = getByte(index);
		return (byte) b;
	}

	public byte suggestMarking(Hand currentHand, ScoreCard currentScore) {
		long index = getIndex(currentScore, currentHand, 3);
		return (byte) getByte(index);
	}

	public void addMarkingAction(byte action, ScoreCard currentScore,
			Hand hand) {
		long index = getIndex(currentScore, hand, 3);
		putByte(index, action);
	}

	public void addRollingAction(byte action, ScoreCard currentScore,
			Hand hand, int roll){
		long index = getIndex(currentScore, hand, roll);
		putByte(index, action);
	}

	public void putExpectedScore(double expected, ScoreCard currentScore,
			Hand hand, int roll) {
		throw new UnsupportedOperationException();
	}

	public double getExpectedScore(ScoreCard currentScore, Hand hand, int roll) {
		throw new UnsupportedOperationException();
	}


	long cardSize = 1;
	long handSize = cardSize * (ScoreCard.MAX_INDEX + 1);
	long rollSize = handSize * (Hand.MAX_INDEX + 1);
	private long getIndex(ScoreCard sc, Hand hand, int roll) {
		long idx = (rollSize * roll) + (handSize * hand.getIndex()) + (cardSize * sc.getIndex());
		//System.out.printf("Index returned %d\n", idx);
		return idx;
	}

	private void putByte(long index, int b) {
		try {
			fp.seek(index);
			fp.writeByte(b);
		} catch (IOException e){
			e.printStackTrace();
		}
	}

	private int getByte(long index) {
		try {
			fp.seek(index);
			return fp.read();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
			return -1;
		}
	}

	public void close() {
		try {
			fp.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
