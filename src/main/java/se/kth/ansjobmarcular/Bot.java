package se.kth.ansjobmarcular;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Bot {

    /**
     * The database interface
     */
    protected ActionsStorage db = new FileActionsStorage();
    
    protected static List<Integer> results = new LinkedList<Integer>();
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

        int numGames = 200;

        Bot bot = new Bot();
        for (int i = 0; i < numGames; i++) {
            bot.playGame_julio();
        }
        double avg = 0;
        for (Integer res : results) {
        	avg += res;
        }
        avg /= results.size();
        System.out.printf("Played %d games, at average score %.2f\n", numGames, avg);
    }
    
    //Only Two round game example to show the bot behavior when 1st hand is LARGESTRAIGHT and 2nd is SHORTSTRAIGHT
    private void playGame_julio() {
        ScoreCard sc = new ScoreCard();
        int totalScore = 0;
        
        /* 
         *  ********** FIRST ROUND **************************************************** 
         */
        dice[0] = (byte) 2;
        dice[1] = (byte) 3;
        dice[2] = (byte) 4;
        dice[3] = (byte) 5;
        dice[4] = (byte) 6;
        Hand initialHand = new Hand (dice[0], dice[1], dice[2], dice[3], dice[4]);
        for (int roll = 1; roll <= 2; roll++) {
            int holdMask = db.suggestRoll(initialHand, sc, roll);
            randomize(dice, holdMask);
        }
        
        /*
         * Now we need to to the marking
         */
        Hand h = new Hand(dice[0], dice[1], dice[2], dice[3], dice[4]);

        int cat = db.suggestMarking(h, sc);
        Category c = Category.fromInt(cat);
        totalScore += sc.value(h, c);
        sc.fillScore(c);
        if (c.isUpper()) {
            int value = ScoreCard.count(h, cat+1) * (cat+1);
            sc.addScore(value);
        }
        
        
        /* 
         *  ********** SECOND ROUND **************************************************** 
         */
        dice[0] = (byte) 1;
        dice[1] = (byte) 1;
        dice[2] = (byte) 2;
        dice[3] = (byte) 3;
        dice[4] = (byte) 4;
        Hand initialHand2 = new Hand ((byte) 1, (byte) 1, (byte) 2, (byte) 3, (byte) 4);
        for (int roll = 1; roll <= 2; roll++) {
            int holdMask = db.suggestRoll(initialHand2, sc, roll);
            // holdMask should be 31 as we already hit LARGESTRAIGHT the previous round 
            //     and in this 2nd round we have just hit SMALLSTRAIGHT
            randomize(dice, holdMask);
        }
        
        /*
         * Now we need to to the marking
         */
        h = new Hand(dice[0], dice[1], dice[2], dice[3], dice[4]);

        cat = db.suggestMarking(h, sc);
        c = Category.fromInt(cat);
        totalScore += sc.value(h, c);
        sc.fillScore(c);
        if (c.isUpper()) {
            int value = ScoreCard.count(h, cat+1) * (cat+1);
            sc.addScore(value);
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
        results.add(totalScore);
        System.out.printf("%d\n", totalScore);
    }

    static Random r = new Random();
    private static void randomize(byte[] dice, int mask) {
        for (int i = 0; i < dice.length; i++) {
            if (((1 << (dice.length - (i + 1))) & mask) == 0) {
                dice[i] = (byte) (1 + r.nextInt(6));
            }
        }
        Arrays.sort(dice);
    }
}
