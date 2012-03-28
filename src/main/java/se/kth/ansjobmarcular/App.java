package se.kth.ansjobmarcular;

/**
 * 
 * 
 */
public class App {
	public static void main(String[] args) throws ClassNotFoundException {
		Class.forName("se.kth.ansjobmarcular.Hand");

		Generator gen = new Generator();
		System.out.println("Generating base cases..");
		gen.generateBaseCases();
		System.out.println("Generating other cases..");
		gen.generate();
	}
}