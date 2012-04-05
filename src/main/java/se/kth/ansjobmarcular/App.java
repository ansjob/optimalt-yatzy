package se.kth.ansjobmarcular;

/**
 *
 *
 */
public class App {


	public static void main(String[] args) throws Throwable {
		long time;
		Class.forName("se.kth.ansjobmarcular.Hand");
		Generator gen = new Generator();
		
		/* Generate the base cases. */
		System.out.println("Generating base cases..");
		time = System.currentTimeMillis();
		gen.generateBaseCases();
		System.out.printf("Generated base cases in %dms\n", System.currentTimeMillis() - time);
		
		/* Generate other cases. */
		System.out.println("Generating other cases..");
		time = System.currentTimeMillis();
		gen.generate();
		System.out.printf("Generated other cases in %dms\n", System.currentTimeMillis() - time);
		gen.close();
	}
}