package front;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import back.Particle;

public class Parser
{	
	@SuppressWarnings("resource")
	public static Input ParseInputFile(String filename) throws FileNotFoundException
    {
		FileInputStream file = new FileInputStream(filename);  
        Scanner scanner = new Scanner(file);
        double width = scanner.nextDouble();
        double height = scanner.nextDouble();
        double gapSize = scanner.nextDouble();
        double desiredV = scanner.nextDouble();
        int N = scanner.nextInt();
        List<Particle> particles = new LinkedList<>();
        for (int i=0; i < N; i++)
        {
        	double x = scanner.nextDouble();
        	double y = scanner.nextDouble();
        	double r = scanner.nextDouble();
        	double m = scanner.nextDouble();
        	Particle p = new Particle(i, x, y, 0, 0, 0, 0, m, r, desiredV);
        	particles.add(p);
        }
		return new Input(width, height, gapSize, particles);  
    }
}
