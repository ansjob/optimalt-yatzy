package se.kth.ansjobmarcular;

import java.util.logging.Logger;

public class Generator {
	
	private ActionsStorage db;
	
	public Generator() {
		db = new SQLiteActionsStorage();
		db.clearDb();
	}

	public void generateBaseCases() {
		double max, score;
		int bestMask, action;

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
						double expected = Score.value(
								Hand.getHand(hand), Category.values()[cat]);
						db.putExpectedScore(expected, sc, Hand.getHand(hand), roll);
						System.out.printf("%s: %s => %.2f\n",
								Category.values()[cat], Hand.getHand(hand)
										.toString(), expected);
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
							double expected = db.getExpectedScore(sc, Hand.getHand(destHand), roll+1);
							score += Hand.getHand(hand).probability(Hand.getHand(destHand), mask)
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
					db.putExpectedScore(max, sc, Hand.getHand(hand), roll);
					action = bestMask;
					System.out.printf("%s: %s roll: %d, action: %x => %.2f\n",
							Category.values()[cat], Hand.getHand(hand), roll,
							action, max);
				}
			}
		}
	}

	public void generate() {
		double max, score;
		int bestMask, action;
		boolean[][] ways;
		ScoreCard sc, tmpSc;
		Category category, otherCat;

		for (int filled = 15; filled >= 0; filled--) {
			ways = Utils.allWaysToPut(filled, 15);
			for (boolean[] way : ways) {
				sc = new ScoreCard();
				for (int i = 0; i < way.length; i++) {
					if (way[i])
						sc.fillScore(Category.values()[i]);
				}
				for (int roll = 3; roll >= 0; roll--) {
					/* If last roll. */
					if (roll == 3) {
						/* For every possible hand. */
						for (int hand = 1; hand <= Hand.MAX_INDEX; hand++) {
							/* For every unfilled category. */
							for (int cat = 0; cat < 15; cat++) {
								category = Category.values()[cat];
								if (sc.isFilled(category))
									continue;

								/* Pretend to fill the category. */
								try {
									tmpSc = (ScoreCard) sc.clone();
								} catch (CloneNotSupportedException e) {
									e.printStackTrace();
									return;
								}

								/*
								 * Calculate the expected score if filling
								 * current category with the hand.
								 */
								score = Score.value(Hand.getHand(hand),
										category);
								int i = cat + 1;
								for (int unfilled = 1; unfilled <= 14 - filled; unfilled++) {
									while (way[i])
										i++;

									/*
									 * Find the first unfilled category except
									 * the one we just "filled".
									 */
									otherCat = Category.values()[i];

									/*
									 * Calculate the expected score for this
									 * category the next round.
									 */
									score += db.getExpectedScore(tmpSc, Hand.getHand(hand), roll);

									/*
									 * Start looking for next unfilled category
									 * on next category.
									 */
									i++;
								}
								db.putExpectedScore(score, tmpSc, Hand.getHand(hand), 3);
							}
						}
						continue;
					}

					/* If roll 0-2 */
					for (int hand = 1; hand <= Hand.MAX_INDEX; hand++) {
						// TODO
					}
				}
			}
			for (int curCat = 1; curCat <= 15; curCat++) {

			}
		}
	}
}
