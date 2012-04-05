package se.kth.ansjobmarcular;

import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import se.kth.ansjobmarcular.concurrency.basecases.BaseCase;
import se.kth.ansjobmarcular.concurrency.recursion.RecursionFinalHand;

public class Generator {

	/*
	 * The array containing the optimal strategy.
	 */
	private Map<ScoreCard, Double>[][] expectedScores, workingVals;
	private ActionsStorage db = new MemoryActionsStorage();
	private ExecutorService runner = Executors.newFixedThreadPool(32);

	@SuppressWarnings("unchecked")
	public Generator() {
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
		long time = System.currentTimeMillis() - startTime;
		Utils.debug("Generated base cases in %d ms\n", time);
	}

	@SuppressWarnings("unchecked")
	public void generate() throws InterruptedException {
		boolean[][] ways;
		ScoreCard sc;

		/*
		 * For every round in the game (backwards).
		 */
		for (int filled = 13; filled >= 0; filled--) {

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
			ways = Utils.allWaysToPut(filled, 15);
			for (boolean[] way : ways) {
				/*
				 * Fill out the scorecard in this new way
				 */
				sc = new ScoreCard();
				boolean hasUpperFree = false;
				for (int i = 0; i < way.length; i++) {
					if (way[i]) {
						sc.fillScore(Category.values()[i]);
					} else if (i < 6) {
						hasUpperFree = true;
					}
				}

				/*
				 * For every possible upperTotal score.
				 */
				for (int upperTotal = 0; upperTotal < 64; upperTotal++, sc
						.addScore(1)) {
					/*
					 * For every roll from 3 to 0 for this scorecard.
					 */
					for (int roll = 3; roll >= 0; roll--) {
						/*
						 * If last roll.
						 */
						if (roll == 3) {
							/*
							 * For every possible hand.
							 */
							List<RecursionFinalHand> tasks = new LinkedList<RecursionFinalHand>();
							for (int hand = 1; hand <= Hand.MAX_INDEX; hand++) {
								tasks.add(new RecursionFinalHand(hand, sc,
										hand, db, expectedScores, workingVals));
							}
							runner.invokeAll(tasks);
							continue;
						}

						/*
						 * If roll 0-2, for every hand:
						 */
						double[] K = new double[Keeper.MAX_INDEX];
						for (int hand = 1; hand <= Hand.MAX_INDEX; hand++) {
							K[new Keeper(Hand.getHand(hand), 0x1f).getIndex()] = workingVals[roll + 1][hand]
									.get(sc);
						}

						for (int held = 4; held >= 0; held--) {
							for (int hand = 1; hand <= Hand.MAX_INDEX; hand++) {
								Hand h = Hand.getHand(hand);
								int bestMask = 0;
								double maxK = 0;
								for (Keeper k : Keeper.getKeepers(held)) {
									if (k.getCount() != held)
										throw new PanicException(k.getCount()
												+ " != " + held);

									double sum = 0;
									for (int d = 1; d <= 6; d++) {
										Keeper otherK = k.add(d);
										sum += K[otherK.getIndex()];
									}
									sum /= 6.0;
									K[k.getIndex()] = sum;
									if (sum > maxK && k.getMask(h) != -1) {
										maxK = sum;
										bestMask = k.getMask(h);
									}
								}								
								/*
								 * Now let's save the expected score and optimal
								 * action for this roll
								 */
								workingVals[roll][hand].put(sc, maxK);
								db.addRollingAction((byte) bestMask, sc, h, roll);
								
								Utils.debug("RollingAction: %s \n %s roll: %d -> %x E = %.2f\n\n",
										sc.getUnFilled(), h.toString(),
										roll, bestMask, maxK);
							}
						}
					}
					if (!hasUpperFree) {
						copyResults(sc);
						break;
					}
				}
			}
			/*
			 * Forget the last round's expected scores, we don't need them
			 * anymore. Also, make the (now complete) workingVals the
			 * expectedScores for the next round.
			 */
			expectedScores = workingVals;

			DateFormat df = DateFormat.getDateInstance();
			System.out.printf("[%s] Done with recursion step %d\n ", df
					.format(new Date()), 14 - filled);
		}

		/* Print the expected score for a Yatzy game. */
		System.out.printf("Expected total score: %.2f\n", expectedScores[0][1]
				.get(new ScoreCard()));

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

}
