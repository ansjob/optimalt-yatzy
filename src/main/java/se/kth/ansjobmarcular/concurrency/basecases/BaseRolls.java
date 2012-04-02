/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.ansjobmarcular.concurrency.basecases;

import java.util.Map;
import se.kth.ansjobmarcular.ActionsStorage;
import se.kth.ansjobmarcular.Category;
import se.kth.ansjobmarcular.Hand;
import se.kth.ansjobmarcular.RollingAction;
import se.kth.ansjobmarcular.ScoreCard;
import se.kth.ansjobmarcular.concurrency.ParallellAction;

/**
 *
 * @author ansjob
 */
public class BaseRolls extends ParallellAction {

    protected final int roll;
    protected final ActionsStorage db;
    protected int hand;
    protected ScoreCard sc;
    protected int cat;

    public BaseRolls(int roll, ActionsStorage db, ScoreCard sc, int hand, int cat, Map<ScoreCard, Double>[][] expectedScores, Map<ScoreCard, Double>[][] workingVals) {
        super(expectedScores, workingVals);
        this.roll = roll;
        this.db = db;
        this.hand = hand;
        this.sc = sc;
        this.cat = cat;
    }

    public Void call() {
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
                double prob = Hand.getHand(hand).probability(
                        destHand, mask);
                if (prob == 0) {
                    continue;
                }
                double expected = expectedScores[roll + 1][destHand.getIndex()].get(sc);
                score += prob * expected;
            }
            /*
             * If this score beats the maximum, remember which dice to hold.
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
         * Remember the expected score, and action for this combination of
         * holding.
         */
        expectedScores[roll][hand].put(sc, max);

        if (roll == 0) {
            return null;
        }

        /*
         * Save the optimal action.
         */
        //actions[roll - 1][hand][sc.getIndex()] = (byte) bestMask;
		db.addRollingAction(new RollingAction(bestMask), sc,
				Hand.getHand(hand), roll - 1);
		System.out
				.printf("Base case: %s \tUppertotal: %d \tHand %s \tMask: 0x%x \tRoll: %d \tExpected score %.2f\n",
						Category.values()[cat], sc.getUpper(),
						Hand.getHand(hand), bestMask, roll, max);
        return null;
    }
}
