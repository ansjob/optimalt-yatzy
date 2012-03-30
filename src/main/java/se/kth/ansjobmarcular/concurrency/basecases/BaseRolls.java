/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.ansjobmarcular.concurrency.basecases;

import java.util.Map;
import se.kth.ansjobmarcular.ActionsStorage;
import se.kth.ansjobmarcular.Hand;
import se.kth.ansjobmarcular.RollingAction;
import se.kth.ansjobmarcular.ScoreCard;

/**
 *
 * @author ansjob
 */
public class BaseRolls extends BaseFinalHand {

    protected final int roll;
    protected final ActionsStorage db;

    public BaseRolls(int roll, ActionsStorage db, ScoreCard sc, int hand, int cat, Map<ScoreCard, Double>[][] expectedScores, Map<ScoreCard, Double>[][] workingVals) {
        super(sc, hand, cat, expectedScores, workingVals);
        this.roll = roll;
        this.db = db;
    }

    public Void call() {
        double max = 0;
        int bestMask = 0;
        /*
         * For every possible combination of holding the dice.
         */
        for (int mask = 0; mask <= 0x1F; mask++) {
            double score = 0;
            /*
             * For every possible hand.
             */
            for (int destHand = 1; destHand <= Hand.MAX_INDEX; destHand++) {
                double prob = Hand.getHand(hand).probability(
                        Hand.getHand(destHand), mask);
                if (prob == 0) {
                    continue;
                }
                double expected = expectedScores[roll + 1][destHand].get(sc);
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
                return null;
            }
        }

        /*
         * Remember the expected score, and action for this combination of
         * holding.
         */
        expectedScores[roll][hand].put(sc, max);

        // System.out.printf("%s: %s roll: %d, action: %x => %.2f\n",
        // Category.values()[cat], Hand.getHand(hand), roll,
        // action, expectedScores[roll][hand].get(sc));

        if (roll == 0) {
            return null;
        }

        /*
         * Save the optimal action.
         */
        //actions[roll - 1][hand][sc.getIndex()] = (byte) bestMask;
        db.addRollingAction(new RollingAction(bestMask), sc, Hand.getHand(hand), roll - 1);

        return null;
    }
}
