package se.kth.ansjobmarcular.concurrency.recursion;

import java.util.Map;
import se.kth.ansjobmarcular.ActionsStorage;
import se.kth.ansjobmarcular.Category;
import se.kth.ansjobmarcular.Hand;
import se.kth.ansjobmarcular.MarkingAction;
import se.kth.ansjobmarcular.ScoreCard;
import se.kth.ansjobmarcular.concurrency.ParallellAction;

public class RecursionFinalHand extends ParallellAction {

	protected int cat;
	protected ScoreCard sc;
	protected int hand;
	protected ActionsStorage db;

	public RecursionFinalHand(int cat, ScoreCard sc, int hand,
			ActionsStorage db, Map<ScoreCard, Double>[][] expectedScores,
			Map<ScoreCard, Double>[][] workingVals) {
		super(expectedScores, workingVals);
		this.cat = cat;
		this.sc = sc;
		this.hand = hand;
		this.db = db;
	}

	public Void call() throws Exception {
		double max = 0;
		byte bestCat = 0;
		ScoreCard tmpSc;
		/*
		 * For every unfilled category.
		 */
		for (Category cat : Category.values()) {
			if (sc.isFilled(cat)) {
				continue;
			}

			/*
			 * Pretend to fill the category.
			 */
			try {
				tmpSc = (ScoreCard) sc.clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
				return null;
			}

			/*
			 * Calculate the expected score if filling current category with the
			 * hand.
			 */
			double score = tmpSc.value(Hand.getHand(hand), cat);

			/*
			 * If ONES-SIXES, take bonus into consideration.
			 */
			if (cat == Category.ONES || cat == Category.TWOS
					|| cat == Category.THREES || cat == Category.FOURS
					|| cat == Category.FIVES || cat == Category.SIXES) {
				tmpSc.fillScore(cat, tmpSc.value(Hand.getHand(hand),
						cat));
			} else {
				tmpSc.fillScore(cat);
			}

			/*
			 * Check that this was set (assume 0 otherwise)
			 */
			if (expectedScores[0][1].containsKey(tmpSc))
			/*
			 * Add the expected score of the next state.
			 */
			{
				score += expectedScores[0][1].get(tmpSc);
			}

			/*
			 * If this is the best score so far, remember what category was the
			 * optimal.
			 */
			if (score > max) {
				max = score;
				bestCat = (byte) Category.toInt(cat);
			}
		}

		/*
		 * Save the optimal expected score for this state.
		 */
		workingVals[3][hand].put(sc, max);

		// System.out.printf("%x: filling %s: %s => %.2f\n",
		// sc.getIndex(), bestCategory,
		// Hand.getHand(hand), max);

		/*
		 * Save the optimal category to put the hand in (the optimal action).
		 */
		// actions[roll - 1][hand][sc.getIndex()] =
		// bestCat;
		db.addMarkingAction(new MarkingAction(bestCat), sc, Hand.getHand(hand));
		System.out.printf("Scorecard: %s\n Hand: %s -> %s\n\n", sc.toString(),
				Hand.getHand(hand).toString(), Category.values()[bestCat]);
		return null;
	}
}
