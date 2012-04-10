package se.kth.ansjobmarcular;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FileActionsStorage implements ActionsStorage {

	private static final long MAX_INDEX = 3 * (Hand.MAX_INDEX + 1) * (ScoreCard.MAX_INDEX + 1);

	private RandomAccessFile fp;
	private Map<Long, Byte> buffer;

    private ExecutorService ioRunner = Executors.newSingleThreadExecutor();

    private class SaveTask implements Runnable {
        protected int value;
        private long index;

        public SaveTask(int value, long index) {
            this.value = value;
            this.index = index;
        }

        @Override
        public void run() {
            try {
                fp.seek(index);
                fp.writeByte(value);
            } catch (IOException ex) {
                System.err.printf("Error writing to file!");
                System.exit(-1);
            }
        }
    }

    private class FetchTask implements Callable<Integer> {
        private long index;

        public FetchTask(long index) {
            this.index = index;
        }

        @Override
        public Integer call() throws Exception {
            fp.seek(index);
            return fp.read();
        }

    }

	public FileActionsStorage() {
		File file = new File("/tmp/actions");
		try {
			fp = new RandomAccessFile(file, "rw");
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
		return idx;
	}



	private void putByte(long index, int b) {
		ioRunner.submit(new SaveTask(b, index));
	}

	private int getByte(long index)  {
        try {
            Callable<Integer> task = new FetchTask(index);
            return ioRunner.submit(task).get();
        } catch (Exception ex) {
            System.err.printf("Error reading from file!");
            System.exit(-1);
            return -1;
        }
	}

	public void close() {
		try {
            ioRunner.shutdown();
            ioRunner.awaitTermination(10, TimeUnit.MINUTES);
			fp.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
