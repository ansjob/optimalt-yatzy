package se.kth.ansjobmarcular;

import java.util.Arrays;

public class Bot {

    /**
     * The database interface
     */
    protected ActionsStorage db = new FileActionsStorage();
    /**
     * The dice currently at hand
     */
    protected byte[] dice = new byte[5];

    /**
     * Runs a bot that plays args[0] number of games, and prints the results to
     * stdout.
     *
     * @param args
     */
    public static void main(String[] args) {

        int numGames = 10;

        Bot bot = new Bot();
        for (int i = 0; i < numGames; i++) {
            bot.playGame();
        }

    }

    private void playGame() {
        ScoreCard sc = new ScoreCard();
        int totalScore = 0;
        for (int round = 0; round < 15; round++) {
            /*
             * Start by rolling all dice
             */
            randomize(dice, 0x0);
            for (int roll = 1; roll <= 2; roll++) {
                int holdMask = db.suggestRoll(new Hand(dice[0], dice[1], dice[2], dice[3], dice[4]), sc, roll);
                randomize(dice, holdMask);
            }
            /*
             * Now we need to to the marking
             */
            Hand h = new Hand(dice[0], dice[1], dice[2], dice[3], dice[4]);
            if (round < 14) {
                int cat = db.suggestMarking(h, sc);
                Category c = Category.fromInt(cat);
                totalScore += sc.value(h, c);
                sc.fillScore(c);
                if (c.isUpper()) {
                    int value = ScoreCard.count(h, cat+1) * (cat+1);
                    sc.addScore(value);
                }
            } else {
                for (Category cat : Category.values) {
                    if (!sc.isFilled(cat)) {
                        totalScore += sc.value(h, cat);
                    }
                }
            }
        }
        System.out.printf("Finished game with total score %d\n", totalScore);
    }

    private static void randomize(byte[] dice, int mask) {
        for (int i = 0; i < dice.length; i++) {
            if (((1 << (dice.length - (i + 1))) & mask) == 0) {
                dice[i] = (byte) (1 + (Math.random() * 6));
            }
        }
        Arrays.sort(dice);
    }
}
