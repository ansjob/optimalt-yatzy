package se.kth.ansjobmarcular.concurrency.basecases;

import java.util.Map;
import java.util.concurrent.ExecutorService;

import se.kth.ansjobmarcular.ActionsStorage;
import se.kth.ansjobmarcular.Category;
import se.kth.ansjobmarcular.Hand;
import se.kth.ansjobmarcular.RollingAction;
import se.kth.ansjobmarcular.ScoreCard;
import se.kth.ansjobmarcular.concurrency.ParallellAction;

public class BaseCase extends ParallellAction {

	protected int upperTotal;
	protected Category cat;
	protected ExecutorService runner;
	protected ActionsStorage db;

	public BaseCase(Map<ScoreCard, Double>[][] expectedScores,
			Map<ScoreCard, Double>[][] workingVals, int upperTotal,
			Category cat, ExecutorService runner, ActionsStorage db) {
		super(expectedScores, workingVals);
		this.upperTotal = upperTotal;
		this.cat = cat;
		this.runner = runner;
		this.db = db;
	}

	public Void call() throws Exception {
		ScoreCard sc = new ScoreCard();
		sc.addScore(upperTotal);
		for (Category c : Category.values()) {
			if (c != cat)
				sc.fillScore(c);
		}

		/* For every roll during this round. */
		for (int roll = 3; roll >= 0; roll--) {
			/* If last roll. */
			if (roll == 3) {
				/* For every possible hand */
				for (int hand = 1; hand <= Hand.MAX_INDEX; hand++) {
					double expected = sc.value(Hand.getHand(hand), cat);
					expectedScores[3][hand].put(sc, expected);
				}
				continue;
			}

			/* If roll 0-2 */
			for (int hand = 1; hand <= Hand.MAX_INDEX; hand++) {
				double max = 0;
				int bestMask = 0;
				Hand h = Hand.getHand(hand);
				/*
				 * For every possible combination of holding the dice.
				 */
				for (int mask = 0; mask <= 0x1F; mask++) {
					double score = 0;
					/*
					 * For every possible outcome hand.
					 */
					for (Hand destHand : h.getPossibleOutcomes(mask)) {
						double prob = Hand.getHand(hand).probability(destHand,
								mask);
						if (prob == 0) {
							continue;
						}
						double expected = expectedScores[roll + 1][destHand
								.getIndex()].get(sc);
						score += prob * expected;
					}
					/*
					 * If this score beats the maximum, remember which dice to
					 * hold.
					 */
					if (score >= max) {
						max = score;
						bestMask = mask;
					}
					/*
					 * You can't hold/save dice you never rolled.
					 */
					if (roll == 0) {
						break;
					}
				}

				/*
				 * Remember the expected score, and action for this combination
				 * of holding.
				 */
				expectedScores[roll][hand].put(sc, max);

				if (roll == 0) {
					break;
				}

				/*
				 * Save the optimal action.
				 */
				// actions[roll - 1][hand][sc.getIndex()] = (byte) bestMask;
				db.addRollingAction(new RollingAction(bestMask), sc, Hand
						.getHand(hand), roll - 1);
				System.out
						.printf(
								"Base case: %s \tUppertotal: %d \tHand %s \tMask: 0x%x \tRoll: %d \tExpected score %.2f\n",
								cat, sc.getUpper(), Hand.getHand(hand),
								bestMask, roll, max);
			}
		}
		System.out.printf(
				"Generated all base cases for %s with upperTotal %d\n", cat,
				upperTotal);
		return null;
	}
}
