/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.ansjobmarcular;

/**
 *
 * @author ansjob
 */
public class MemoryActionsStorage implements ActionsStorage {

    private byte[][][] storage = new byte[4][Hand.MAX_INDEX+1][ScoreCard.MAX_INDEX+1];


    @Override
    public byte suggestRoll(Hand currentHand, ScoreCard currentScore, int roll) {
        return storage[roll][currentHand.getIndex()][currentScore.getIndex()];
    }

    @Override
    public byte suggestMarking(Hand currentHand, ScoreCard currentScore) {
        return storage[3][currentHand.getIndex()][currentScore.getIndex()];
    }

    @Override
    public void addMarkingAction(byte action, ScoreCard currentScore, Hand hand) {
        storage[3][hand.getIndex()][currentScore.getIndex()] = (byte) action;
    }

    @Override
    public void addRollingAction(byte action, ScoreCard currentScore, Hand hand, int roll) {
        storage[roll][hand.getIndex()][currentScore.getIndex()] = (byte) action;
    }

    @Override
    public void putExpectedScore(double expected, ScoreCard currentScore, Hand hand, int roll) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getExpectedScore(ScoreCard currentScore, Hand hand, int roll) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
