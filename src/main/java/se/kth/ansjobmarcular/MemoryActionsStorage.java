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
    public RollingAction suggestRoll(Hand currentHand, ScoreCard currentScore, int roll) {
        return new RollingAction(storage[roll][currentHand.getIndex()][currentScore.getIndex()]);
    }

    @Override
    public MarkingAction suggestMarking(Hand currentHand, ScoreCard currentScore) {
        return new MarkingAction(storage[3][currentHand.getIndex()][currentScore.getIndex()]);
    }

    @Override
    public void addMarkingAction(MarkingAction action, ScoreCard currentScore, Hand hand) {
        storage[3][hand.getIndex()][currentScore.getIndex()] = (byte) action.getIndex();
    }

    @Override
    public void addRollingAction(RollingAction action, ScoreCard currentScore, Hand hand, int roll) {
        storage[roll][hand.getIndex()][currentScore.getIndex()] = (byte) action.getIndex();
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
