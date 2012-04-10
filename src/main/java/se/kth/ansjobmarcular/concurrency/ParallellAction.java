/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.ansjobmarcular.concurrency;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 *
 * @author ansjob
 */
public abstract class ParallellAction implements Callable<Void> {

    protected final Map<Integer, Double>[][] workingVals;
    protected final Map<Integer, Double> expectedScores;


    public ParallellAction(
            Map<Integer, Double> expectedScores,
            Map<Integer, Double>[][] workingVals) {
        this.expectedScores = expectedScores;
        this.workingVals = workingVals;
    }



}
