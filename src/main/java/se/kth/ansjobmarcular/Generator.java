package se.kth.ansjobmarcular;

import java.util.HashMap;

public class Generator {
	
	/* The array containing the optimal strategy. */
	private byte[][][] actions;

	private HashMap<ScoreCard, Double>[][] expectedScores, workingVals;

	@SuppressWarnings("unchecked")
	public Generator() {
		actions = new byte[3][Hand.MAX_INDEX+1][ScoreCard.MAX_INDEX+1];
		workingVals = (HashMap<ScoreCard, Double>[][]) new HashMap<?, ?>[4][253];
		expectedScores = (HashMap<ScoreCard, Double>[][]) new HashMap<?, ?>[4][253];

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 253; j++) {
				workingVals[i][j] = new HashMap<ScoreCard, Double>();
				expectedScores[i][j] = new HashMap<ScoreCard, Double>();
			}
		}
	}

	public void generateBaseCases() {
		double max, score;
		int bestMask;

		/* For every last (unfilled) category. */
		for (int cat = 0; cat < 15; cat++) {
			ScoreCard sc = new ScoreCard();
			for (Category c : Category.values()) {
				if (c != Category.values()[cat])
					sc.fillScore(c);
			}
			/* For every roll during this round. */
			for (int roll = 3; roll >= 0; roll--) {
				/* If last roll. */
				if (roll == 3) {
					/* For every possible hand. */
					for (int hand = 1; hand <= Hand.MAX_INDEX; hand++) {
						double expected = Score.value(Hand.getHand(hand),
								Category.values()[cat]);
						expectedScores[3][hand].put(sc, expected);
						// System.out.printf("%s: %s => %.2f\n",
						// Category.values()[cat], Hand.getHand(hand)
						// .toString(), expectedScores[3][hand].get(sc));
					}
					continue;
				}

				/* If roll 0-2 */
				for (int hand = 1; hand <= Hand.MAX_INDEX; hand++) {
					max = 0;
					bestMask = 0;
					/* For every possible combination of holding the dice. */
					for (int mask = 0; mask <= 0x1F; mask++) {
						score = 0;
						/* For every possible hand. */
						for (int destHand = 1; destHand <= Hand.MAX_INDEX; destHand++) {
							double expected = expectedScores[roll + 1][destHand]
									.get(sc);
							score += Hand.getHand(hand).probability(
									Hand.getHand(destHand), mask)
									* expected;
						}
						/*
						 * If this score beats the maximum, remember which dice
						 * to hold.
						 */
						if (score >= max) {
							max = score;
							bestMask = mask;
						}
						/* You can't hold/save dice you never rolled. */
						if (roll == 0)
							break;
					}
					
					/*
					 * Remember the expected score, and action for this
					 * combination of holding.
					 */
					expectedScores[roll][hand].put(sc, max);
					// System.out.printf("%s: %s roll: %d, action: %x => %.2f\n",
					// Category.values()[cat], Hand.getHand(hand), roll,
					// action, expectedScores[roll][hand].get(sc));
					
					if (roll == 0)
						break;
					
					/* Save the optimal action. */
					actions[roll-1][hand][sc.getIndex()] = (byte)bestMask;
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void generate() {
		double max, score;
		int bestMask;
		boolean[][] ways;
		ScoreCard sc, tmpSc;
		Category category;

		/* For every round in the game (backwards). */
		for (int filled = 13; filled >= 0; filled--) {

			/* Initialize workingVals. */
			workingVals = (HashMap<ScoreCard, Double>[][]) new HashMap<?, ?>[4][Hand.MAX_INDEX+1];
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j <= Hand.MAX_INDEX; j++) {
					workingVals[i][j] = new HashMap<ScoreCard, Double>();
				}
			}

			/* For every way the scorecard may be filled when we get here */
			ways = Utils.allWaysToPut(filled, 15);
			for (boolean[] way : ways) {
				/* Fill out the scorecard in this new way */
				sc = new ScoreCard();
				for (int i = 0; i < way.length; i++) {
					if (way[i])
						sc.fillScore(Category.values()[i]);
				}
				/* For every roll from 3 to 0 for this scorecard. */
				for (int roll = 3; roll >= 0; roll--) {
					/* If last roll. */
					if (roll == 3) {
						/* For every possible hand. */
						for (int hand = 1; hand <= Hand.MAX_INDEX; hand++) {
							max = 0;
							byte bestCat = 0;
							/* For every unfilled category. */
							for (int cat = 1; cat <= 15; cat++) {
								category = Category.values()[cat - 1];
								if (sc.isFilled(category))
									continue;

								/* Pretend to fill the category. */
								try {
									tmpSc = (ScoreCard) sc.clone();
								} catch (CloneNotSupportedException e) {
									e.printStackTrace();
									return;
								}
								tmpSc.fillScore(Category.values()[cat - 1]);

								/*
								 * Calculate the expected score if filling
								 * current category with the hand.
								 */
								score = Score.value(Hand.getHand(hand),
										category)
										+ expectedScores[0][1].get(tmpSc);

								if (score >= max) {
									max = score;
									bestCat = (byte)cat;
								}
							}
							/* Save the optimal expected score for this state. */
							workingVals[3][hand].put(sc, max);
							//System.out.printf("%x: filling %s: %s => %.2f\n",
							//		sc.getIndex(), bestCategory,
							//		Hand.getHand(hand), max);
							
							/* Save the optimal category to put the hand in (the optimal action). */
							actions[roll-1][hand][sc.getIndex()] = bestCat;
						}
						continue;
					}

					/* If roll 0-2 (TODO) */
					/* For every hand. */
					for (int hand = 1; hand <= Hand.MAX_INDEX; hand++) {
						max = 0;
						bestMask = 0;
						/* For every hold mask. */
						for (int mask = 0; mask <= 0x1ff; mask++) {
							score = 0;
							/* For every possible outcome hand. */
							for (int destHand = 1; destHand <= Hand.MAX_INDEX; destHand++) {
								double expected = workingVals[roll + 1][destHand]
										.get(sc);
								score += Hand.getHand(hand).probability(
										Hand.getHand(destHand), mask)
										* expected;
							}
							if (score > max) {
								max = score;
								bestMask = mask;
							}
							if (roll == 0)
								break;
						}
						
						/* Save the optimal score for this state. */
						workingVals[roll][hand].put(sc, max);
						
						//System.out.printf("%x: %s roll: %d, action: %x => %.2f\n",
						//sc.getIndex(), Hand.getHand(hand), roll,
						//bestMask, workingVals[roll][hand].get(sc));
						
						if (roll == 0)
							break;
						
						/* Save the optimal action for the state. */
						actions[roll-1][hand][sc.getIndex()] = (byte)bestMask;
					}
				}
			}

			expectedScores = workingVals;
		}
	}
}
