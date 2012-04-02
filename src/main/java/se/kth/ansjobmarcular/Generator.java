package se.kth.ansjobmarcular;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import se.kth.ansjobmarcular.concurrency.basecases.BaseCase;
import se.kth.ansjobmarcular.concurrency.recursion.RecursionFinalHand;
import se.kth.ansjobmarcular.concurrency.recursion.RecursionRoll;

public class Generator {

	/* The array containing the optimal strategy. */
	// private byte[][][] actions;

	private Map<ScoreCard, Double>[][] expectedScores, workingVals;

	private ActionsStorage db = new FileActionsStorage();

	private ExecutorService runner = Executors.newCachedThreadPool();

	@SuppressWarnings("unchecked")
	public Generator() {
		// actions = new byte[3][Hand.MAX_INDEX + 1][ScoreCard.MAX_INDEX + 1];
		workingVals = (Map<ScoreCard, Double>[][]) new Map<?, ?>[4][253];
		expectedScores = (Map<ScoreCard, Double>[][]) new Map<?, ?>[4][253];

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 253; j++) {
				workingVals[i][j] = Collections
						.synchronizedMap(new HashMap<ScoreCard, Double>());
				expectedScores[i][j] = Collections
						.synchronizedMap(new HashMap<ScoreCard, Double>());
			}
		}
	}

	public void generateBaseCases() throws InterruptedException {
		long startTime = System.currentTimeMillis();
		double max, score;
		int bestMask;

		System.out.printf("┌");
		for (int i = 0; i < 15; i++)
			System.out.printf("─");
		System.out.printf("┐\n ");
		/* For every last (unfilled) category. */
		List<BaseCase> tasks = new LinkedList<BaseCase>();
		for (int cat = 0; cat < 15; cat++) {
			/* The upper total only matters for the first six categories */
			if (cat < 6) {
				for (int upperTotal = 1; upperTotal <= 63; upperTotal++) {
					tasks.add(new BaseCase(expectedScores, workingVals, upperTotal, cat, runner, db));
				}
			}
			/*
			 * For the other ones, we can generate it as if it was 0, and copy
			 * the results to the other situations. All we need to remember is
			 * that if we reach the final round, and have something other than
			 * 1-6 left, we just pretend upperTotal is 0
			 */
			tasks.add(new BaseCase(expectedScores, workingVals, 0, cat, runner, db));
			System.out.printf("#");
		}
		runner.invokeAll(tasks);
		System.out.println();
		long time = System.currentTimeMillis() - startTime;
		System.out.printf("Generated base cases in %d ms\n", time);
	}

	@SuppressWarnings("unchecked")
	public void generate() throws InterruptedException {
		double max, score;
		int bestMask;
		boolean[][] ways;
		ScoreCard sc, tmpSc;
		Category category;

		/* For every round in the game (backwards). */
		for (int filled = 13; filled >= 0; filled--) {

			/* Initialize workingVals. */
			workingVals = (Map<ScoreCard, Double>[][]) new Map<?, ?>[4][Hand.MAX_INDEX + 1];
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j <= Hand.MAX_INDEX; j++) {
					workingVals[i][j] = Collections
							.synchronizedMap(new HashMap<ScoreCard, Double>());
				}
			}

			/* For every way the scorecard may be filled when we get here */
			ways = Utils.allWaysToPut(filled, 15);
			for (boolean[] way : ways) {
				/* Fill out the scorecard in this new way */
				sc = new ScoreCard();
				for (int i = 0; i < way.length; i++) {
					if (way[i])
						sc.fillScore(Category.values()[i]);
				}

				/* For every possible upperTotal score. */
				for (int upperTotal = 0; upperTotal < 64; upperTotal++) {
					sc.addScore(1);
					/* For every roll from 3 to 0 for this scorecard. */
					for (int roll = 3; roll >= 0; roll--) {
						/* If last roll. */
						if (roll == 3) {
							/* For every possible hand. */
							List<RecursionFinalHand> tasks = new LinkedList<RecursionFinalHand>();
							for (int hand = 1; hand <= Hand.MAX_INDEX; hand++) {
								tasks.add(new RecursionFinalHand(hand, sc,
										hand, db, expectedScores, workingVals));
							}
							runner.invokeAll(tasks);
							continue;
						}

						/* If roll 0-2 */
						/* For every hand. */
						List<RecursionRoll> tasks = new LinkedList<RecursionRoll>();
						for (int hand = 1; hand <= Hand.MAX_INDEX; hand++) {
							tasks.add(new RecursionRoll(roll, hand, sc, db,
									expectedScores, workingVals));
						}
						runner.invokeAll(tasks);
					}
				}
			}
			/*
			 * Forget the last round's expected scores, we don't need them
			 * anymore. Also, make the (now complete) workingVals the
			 * expectedScores for the next round.
			 */
			expectedScores = workingVals;
		}
	}
}
