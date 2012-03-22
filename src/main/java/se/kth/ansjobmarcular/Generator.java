package se.kth.ansjobmarcular;

public class Generator {
	int		a, b, c, d, e;


	public void generate() {
		for (a = 1; a <= 6; a++) {
			for (b = a; b <= 6; b++) {
				for (c = b; c <= 6; c++) {
					for (d = c; d <= 6; d++) {
						for (e = d; e <= 6; e++) {
							System.out.println(Score.value(new Hand(a, b, c, d, e), Category.ONES));
						}
					}
				}
			}
		}
	}
}
