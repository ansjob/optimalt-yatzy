package se.kth.ansjobmarcular;

/**
 * Hello world!
 *
 */
public class App
{
    public static void main( String[] args ) throws ClassNotFoundException
    {
    	Class.forName("se.kth.ansjobmarcular.Hand");
        //Class.forName("se.kth.ansjobmarcular.ScoreCard");

        
        Generator gen = new Generator();
        gen.generateBaseCases();
        gen.generate();
    }
}
