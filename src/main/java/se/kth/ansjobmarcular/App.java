package se.kth.ansjobmarcular;

/**
 * Hello world!
 *
 */
public class App
{
    public static void main( String[] args ) throws ClassNotFoundException
    {
        System.out.println( "Hello World!" );
        Class.forName("se.kth.ansjobmarcular.Hand");
        Class.forName("se.kth.ansjobmarcular.ScoreCard");
        
        Generator gen = new Generator();
        gen.generateBaseCases();
    }
}
