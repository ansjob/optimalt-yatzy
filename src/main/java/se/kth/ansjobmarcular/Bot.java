package se.kth.ansjobmarcular;

public class Bot {

    /**
     * The database interface
     */
    protected ActionsStorage db = new FileActionsStorage();
    /**
     * The ScoreCard used in the game
     */
    protected ScoreCard sc = new ScoreCard();
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
        if (args.length != 1) {
            System.err.printf("Send an integer as command parameter!\n");
            System.exit(-1);
        }
        try {
            int numGames = Integer.parseInt(args[0]);

            Bot bot = new Bot();
            for (int i = 0; i < numGames; i++) {
                bot.playGame();
            }
        } catch (NumberFormatException e) {
            System.err.printf("Send an integer as command parameter! ");
            System.exit(-1);
        }
    }

    private void playGame() {
        int totalScore = 0;
        for (int round = 0; round < 15; round++) {
            /*
             * Start by rolling all dice
             */
            randomize(dice, 0x1f);
            for (int roll = 1; roll <= 2; roll++) {
                int holdMask = db.suggestRoll(new Hand(dice[0], dice[1], dice[2], dice[3], dice[4]), sc, roll);
                randomize(dice, holdMask);
            }
            /* Now we need to to the marking */
            Hand h = new Hand(dice[0], dice[1], dice[2], dice[3], dice[4]);
            int cat = db.suggestMarking(h, sc);
            Category c = Category.fromInt(cat);
            totalScore += sc.value(h, c);
            sc.fillScore(c);
        }
        System.out.printf("Finished game with total score %d\n", totalScore);
    }

    private static void randomize(byte[] dice, int mask) {
        for (int i = 0; i < dice.length; i++) {
            if (1 << (dice.length - i) == 0) {
                dice[i] = (byte) (1 + (Math.random() * 6));
            }
        }
    }
}
