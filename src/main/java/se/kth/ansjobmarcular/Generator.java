package se.kth.ansjobmarcular;

import java.text.DateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import se.kth.ansjobmarcular.concurrency.basecases.BaseCase;
import se.kth.ansjobmarcular.concurrency.recursion.RollCase;

public class Generator {

	/*
	 * The array containing the optimal strategy.
	 */
	private Map<ScoreCard, Double>[][] workingVals;
	private Map<ScoreCard, Double> expectedScores;
	private ActionsStorage db = new MemoryActionsStorage();
	private ExecutorService runner = Executors.newFixedThreadPool(16);

	@SuppressWarnings("unchecked")
	public Generator() {
		workingVals = (Map<ScoreCard, Double>[][]) new Map<?, ?>[3][Hand.MAX_INDEX + 1];
		expectedScores = new HashMap<ScoreCard, Double>();

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j <= Hand.MAX_INDEX; j++) {
				workingVals[i][j] = Collections
						.synchronizedMap(new HashMap<ScoreCard, Double>(6435));

			}
		}
	}

	public void generateBaseCases() throws InterruptedException {

		/*
		 * For every last (unfilled) category.
		 */
		List<BaseCase> tasks = new LinkedList<BaseCase>();
		for (Category cat : Category.values()) {
			/*
			 * The upper total only matters for the first six categories
			 */
			if (cat == Category.ONES || cat == Category.TWOS
					|| cat == Category.THREES || cat == Category.FOURS
					|| cat == Category.FIVES || cat == Category.SIXES) {
				for (int upperTotal = 1; upperTotal <= 63; upperTotal++) {
					tasks.add(new BaseCase(expectedScores, workingVals,
							upperTotal, cat, runner, db));
				}
			}
			/*
			 * For the other ones, we can generate it as if it was 0, and copy
			 * the results to the other situations. All we need to remember is
			 * that if we reach the final round, and have something other than
			 * 1-6 left, we just pretend upperTotal is 0
			 */
			tasks.add(new BaseCase(expectedScores, workingVals, 0, cat, runner,
					db));
		}
		runner.invokeAll(tasks);
		copyResults();
	}

	@SuppressWarnings("unchecked")
	public void generate() throws InterruptedException,
			CloneNotSupportedException {
		boolean[][] ways;
		ScoreCard sc;

		/*
		 * For every round in the game (backwards).
		 */
		for (byte filled = 13; filled >= 0; filled--) {

			/*
			 * Initialize workingVals.
			 */
			workingVals = (Map<ScoreCard, Double>[][]) new Map<?, ?>[4][Hand.MAX_INDEX + 1];
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j <= Hand.MAX_INDEX; j++) {
					workingVals[i][j] = Collections
							.synchronizedMap(new HashMap<ScoreCard, Double>());
				}
			}

			/*
			 * For every way the scorecard may be filled when we get here
			 */
			List<RollCase> tasks = new LinkedList<RollCase>();
			ways = Utils.allWaysToPut(filled, 15);
			for (boolean[] way : ways) {
				/*
				 * Fill out the scorecard in this new way
				 */
				sc = new ScoreCard();
				boolean hasUpperFree = false;
				for (byte i = 0; i < way.length; i++) {
					if (way[i]) {
						sc.fillScore(Category.values()[i]);
					} else if (i < 6) {
						hasUpperFree = true;
					}
				}

				/*
				 * For every possible upperTotal score.
				 */
				for (byte upperTotal = 0; upperTotal < 64; upperTotal++, sc
						.addScore(1)) {
					ScoreCard tmpsc = sc.getCopy();
					tmpsc.addScore(upperTotal);
					tasks.add(new RollCase(expectedScores, workingVals, tmpsc,
							db));
				}
			}
			runner.invokeAll(tasks);
			/*
			 * Forget the last round's expected scores, we don't need them
			 * anymore. Also, make the (now complete) workingVals the
			 * expectedScores for the next round.
			 */
			for (int i = 0; i < workingVals.length; i++) {
				for (int j = 0; j < workingVals[i].length; j++) {
					workingVals[i][j] = null;
				}
			}

			DateFormat df = DateFormat.getTimeInstance();
			System.out.printf("[%s] Done with recursion step %d\n", df
					.format(new Date()), 14 - filled);
		}

		/* Print the expected score for a Yatzy game. */
		System.out.printf("Expected total score: %.2f\n", expectedScores.get(new ScoreCard()));

		/* Close the storage. */
		db.close();
	}

	/**
	 * Copies the results from upperTotal=0 to all other upperTotal values in
	 * the database, to facilitate lookups later.
	 */
	private void copyResults() {
		long startTime = System.currentTimeMillis();
		Utils.debug("Starting to copy results for PAIR -> YATZY\n\n");
		Category[] values = Category.values();
		for (int cat = 6; cat < values.length; cat++) {
			Category c = values[cat];
			/*
			 * Now let's generate the scorecard
			 */
			ScoreCard sc = new ScoreCard();
			for (Category other : values) {
				if (!other.equals(c)) {
					sc.fillScore(other);
				}
			}
			Utils.debug("Copying values for %s\n", c);
			for (short hand = 1; hand <= Hand.MAX_INDEX; hand++) {
				Hand h = Hand.getHand(hand);
				for (byte roll = 0; roll <= 3; roll++) {
					int action;
					if (roll == 3) {
						action = db.suggestMarking(h, sc);
					} else {
						action = db.suggestRoll(h, sc, roll);
					}
					for (byte upperTotal = 1; upperTotal <= 63; upperTotal++, sc
							.addScore(1)) {
						if (roll == 3)
							db.addMarkingAction((byte) action, sc, h);
						else
							db.addRollingAction((byte) action, sc, h, roll);
					}
				}
				Utils.debug("Copied everything for %s with hand %s\n", c, h
						.toString());
			}
		}
		long elapsed = System.currentTimeMillis() - startTime;
		Utils.debug("Copied base cases in %d ms\n", elapsed);
	}

	public void copyResults(ScoreCard sc) {

		Utils.debug("Copying values for %s\n", sc);
		for (int hand = 1; hand <= Hand.MAX_INDEX; hand++) {
			Hand h = Hand.getHand(hand);
			for (int roll = 0; roll <= 3; roll++) {
				int action;
				if (roll == 3) {
					action = db.suggestMarking(h, sc);
				} else {
					action = db.suggestRoll(h, sc, roll);
				}
				for (int upperTotal = 1; upperTotal <= 63; upperTotal++, sc
						.addScore(1)) {
					if (roll == 3)
						db.addMarkingAction((byte) action, sc, h);
					else
						db.addRollingAction((byte) action, sc, h, roll);
				}
			}
			Utils.debug("Copied everything for %s with hand %s\n", sc, h
					.toString());
		}
	}

	public void close() {
		runner.shutdownNow();
	}

}
