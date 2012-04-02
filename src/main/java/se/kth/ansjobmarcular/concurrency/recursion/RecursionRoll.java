/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.ansjobmarcular.concurrency.recursion;

import java.util.Map;
import se.kth.ansjobmarcular.ActionsStorage;
import se.kth.ansjobmarcular.Hand;
import se.kth.ansjobmarcular.RollingAction;
import se.kth.ansjobmarcular.ScoreCard;
import se.kth.ansjobmarcular.concurrency.ParallellAction;

/**
 *
 * @author ansjob
 */
public class RecursionRoll extends ParallellAction {

    protected int roll, hand;
    protected ScoreCard sc;
    protected ActionsStorage db;

    public RecursionRoll(
            int roll,
            int hand,
            ScoreCard sc,
            ActionsStorage db,
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
        for (int mask = 0; mask <= 0x1ff; mask++) {
            double score = 0;
            /*
             * For every possible outcome hand.
             */
            for (Hand destHand : Hand.getHand(hand).getPossibleOutcomes(mask)) {
                double expected = workingVals[roll + 1][destHand.getIndex()].get(sc);
                score += Hand.getHand(hand).probability(
                        destHand, mask)
                        * expected;
            }
            if (score > max) {
                max = score;
                bestMask = mask;
            }
            if (roll == 0) {
                break;
            }
        }

        /*
         * Save the optimal score for this state.
         */
        workingVals[roll][hand].put(sc, max);

        // System.out.printf("%x: %s roll: %d, action: %x => %.2f\n",
        // sc.getIndex(), Hand.getHand(hand), roll,
        // bestMask, workingVals[roll][hand].get(sc));
        if (roll == 0)
            return null;

        /*
         * Save the optimal action for the state.
         */
        // actions[roll - 1][hand][sc.getIndex()] = (byte)
        // bestMask;
        db.addRollingAction(new RollingAction(bestMask),
                sc, Hand.getHand(hand), roll - 1);
        return null;
    }
}
