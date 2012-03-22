/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.ansjobmarcular;

import org.junit.Assert;
import org.junit.Test;
import org.junit.Assert.*;

/**
 *
 * @author ansjob
 */
public class ProbabilityTests {

    @Test
    public void sameProbabilityOne() {
        Hand h = new Hand(1,1,1,1,1);
        Hand other = new Hand(1,1,1,1,1);
        Assert.assertEquals(1.0, h.probability(other), 0.00001);
    }

    @Test
    public void allDiffProbability() {
        Hand h = new Hand(1,1,1,1,1);
        Hand other = new Hand(2,2,2,2,2);
        Assert.assertEquals(1.0/7776.0, h.probability(other), 0.00001);
    }

    @Test
    public void fullHouseTest() {
        Hand h = new Hand(1,2,3,4,5);
        Hand other = new Hand(1,1,1,2,2);
        Assert.assertEquals(1/36.0, h.probability(other), 0.00001);
    }

}
