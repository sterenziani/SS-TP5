package front;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import back.Particle;

public class Parser
{	
	private static final double MASS = 80;
	private static final double DESIRED_V = 2;
	
	@SuppressWarnings("resource")
	public static Input ParseInputFile(String filename) throws FileNotFoundException
    {
		FileInputStream file = new FileInputStream(filename);  
        Scanner scanner = new Scanner(file);
        double width = scanner.nextDouble();
        double height = scanner.nextDouble();
        double gapSize = scanner.nextDouble();
        int N = scanner.nextInt();
        List<Particle> particles = new LinkedList<>();
        for (int i=0; i < N; i++)
        {
        	double x = scanner.nextDouble();
        	double y = scanner.nextDouble();
        	double r = scanner.nextDouble();
        	Particle p = new Particle(i, x, y, 0, 0, 0, 0, MASS, r, DESIRED_V);
        	particles.add(p);
        }
		return new Input(width, height, gapSize, particles);  
    }
}
