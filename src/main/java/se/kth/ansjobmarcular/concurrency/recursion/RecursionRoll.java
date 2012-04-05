/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.ansjobmarcular.concurrency.recursion;

import java.util.Map;

import se.kth.ansjobmarcular.ActionsStorage;
import se.kth.ansjobmarcular.Hand;
import se.kth.ansjobmarcular.Keeper;
import se.kth.ansjobmarcular.ScoreCard;
import se.kth.ansjobmarcular.Utils;
import se.kth.ansjobmarcular.concurrency.ParallellAction;

/**
 * 
 * @author ansjob
 */
public class RecursionRoll extends ParallellAction {

	protected int roll, hand;
	protected ScoreCard sc;
	protected ActionsStorage db;

	public RecursionRoll(int roll, int hand, ScoreCard sc, ActionsStorage db,
			Map<ScoreCard, Double>[][] expectedScores,
			Map<ScoreCard, Double>[][] workingVals) {
		super(expectedScores, workingVals);
		this.roll = roll;
		this.hand = hand;
		this.sc = sc;
		this.db = db;
	}

	public Void call() throws Exception {
		double max = 0;
		int bestMask = 0;
		/*
		 * For every hold mask.
		 */
		for (int mask = 0; mask <= Hand.MAX_MASK; mask++) {
			double score = 0;
			/*
			 * For every possible outcome hand.
			 */
			for (Hand destHand : Hand.getHand(hand).getPossibleOutcomes(mask)) {
				double expected = workingVals[roll + 1][destHand.getIndex()]
						.get(sc);
				double probability = Hand.getHand(hand).probability(destHand,
						mask);
				score += probability * expected;
			}
			if (score > max) {
				max = score;
				bestMask = mask;
			}
			if (roll == 0)
				break;
		}

		/*
		 * Save the optimal score for this state.
		 */
		workingVals[roll][hand].put(sc, max);

		/* There's no action before rolling the first hand. */
		if (roll == 0)
			return null;

		/*
		 * Save the optimal action for the state.
		 */
		db.addRollingAction((byte) bestMask, sc, Hand.getHand(hand), roll - 1);
		return null;
	}

	public void blah() {
		double[] K = new double[Keeper.MAX_INDEX];

		for (int hand = 1; hand <= Hand.MAX_INDEX; hand++) {
			Hand h = Hand.getHand(hand);
			K[new Keeper(h, 0x1f).getIndex()] = workingVals[roll + 1][hand].get(sc);
			
			for (int held = 4; held >= 0; held--) {
				for (boolean way[] : Utils.allWaysToPut(held, 5)) {
					int mask = Utils.fromBooleanArray(way);
					Keeper k = new Keeper(h, mask);
					
					double sum = 0;
					for (int d = 1; d <= 6; d++) {
						Keeper otherK = k.add(d);
						sum += K[otherK.getIndex()];
					}
					K[k.getIndex()] = sum;
				}
			}
		}
	}
}
