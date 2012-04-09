/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.ansjobmarcular.concurrency;

import java.util.Map;
import java.util.concurrent.Callable;
import se.kth.ansjobmarcular.ScoreCard;

/**
 *
 * @author ansjob
 */
public abstract class ParallellAction implements Callable<Void> {

    protected final Map<ScoreCard, Double>[][] workingVals;
    protected final Map<ScoreCard, Double> expectedScores;


    public ParallellAction(
            Map<ScoreCard, Double> expectedScores,
            Map<ScoreCard, Double>[][] workingVals) {
        this.expectedScores = expectedScores;
        this.workingVals = workingVals;
    }



}
