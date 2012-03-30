/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.ansjobmarcular.concurrency.basecases;

import java.util.Map;
import se.kth.ansjobmarcular.Category;
import se.kth.ansjobmarcular.Hand;
import se.kth.ansjobmarcular.ScoreCard;
import se.kth.ansjobmarcular.concurrency.ParallellAction;

/**
 *
 * @author ansjob
 */
public class BaseFinalHand extends ParallellAction {

    /* Parameters needed for this basic action */
    protected ScoreCard sc;
    protected int hand;
    protected int cat;

    public BaseFinalHand(
            ScoreCard sc,
            int hand,
            int cat,
            Map<ScoreCard, Double>[][] expectedScores,
            Map<ScoreCard, Double>[][] workingVals) {

        super(expectedScores, workingVals);
        this.sc = sc;
        this.hand = hand;
        this.cat = cat;
    }

    public Void call() throws Exception {
        double expected = sc.value(Hand.getHand(hand),
                Category.values()[cat]);
        expectedScores[3][hand].put(sc, expected);
//        System.out.printf("%s:%s %s => %.2f\n",
//        Category.values()[cat], sc.getUpper(), Hand.getHand(hand)
//        .toString(), expectedScores[3][hand].get(sc));
        return null;
    }
}
