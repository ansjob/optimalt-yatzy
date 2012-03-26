package se.kth.ansjobmarcular;

public class Generator {
	double expected[][][];

	public Generator() {
		expected = new double[6435][4][Hand.MAX_INDEX + 1];
	}

	public void generateBaseCases() {
		double max, score;
		int bestMask, action;

		for (int cat = 0; cat < 15; cat++) {
			for (int roll = 3; roll >= 0; roll--) {
				/* If last roll. */
				if (roll == 3) {
					for (int hand = 1; hand <= Hand.MAX_INDEX; hand++) {
						expected[cat][3][hand] = Score.value(
								Hand.getHand(hand), Category.values()[cat]);
						System.out.printf("%s: %s => %.2f\n",
								Category.values()[cat], Hand.getHand(hand)
										.toString(), expected[cat][3][hand]);
					}
					continue;
				}

				/* If roll 0-2 */
				for (int hand = 1; hand <= Hand.MAX_INDEX; hand++) {
					max = 0;
					bestMask = 0;
					for (int mask = 0; mask <= 0x1F; mask++) {
						score = 0;
						for (int destHand = 1; destHand <= Hand.MAX_INDEX; destHand++) {
							score += Hand.getHand(hand).probability(
									Hand.getHand(destHand), mask)
									* expected[cat][roll + 1][destHand];
						}
						if (score >= max) {
							max = score;
							bestMask = mask;
						}
						if (roll == 0)
							break;
					}
					expected[cat][roll][hand] = max;
					action = bestMask;
					System.out.printf("%s: %s roll: %d, action: %x => %.2f\n",
							Category.values()[cat], Hand.getHand(hand), roll,
							action, max);
				}
			}
		}
	}
}
