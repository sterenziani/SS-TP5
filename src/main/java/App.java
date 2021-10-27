import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import back.Particle;
import back.SimulationSystem;
import front.Input;
import front.Output;
import front.Parser;

public class App
{
	private static final int DT2 = 500;	// Cada cuanto se imprime
	
	public static void main(String[] args) throws IOException
	{
		Input input = Parser.ParseInputFile("input.txt");
		Output.resetFolder("output/");
		Output.createRoom(input);
		double deltaT = 0.0001;
		double t = 0.0;
		
		List<Particle> particles = input.getParticles();
		int N = input.getN();
		Map<Double, Integer> unloadMap = new HashMap<>();
		SimulationSystem system = new SimulationSystem(input, deltaT);
		system.updateValues();
		while(t <= 5*60 && system.getRemainingParticles() > 0)
		{
			unloadMap.put(t, N-system.getRemainingParticles());
			if( (int)(t/deltaT) % DT2 == 0 )
			{
				Output.outputAnimationFile(particles, t, (int)(t/(deltaT*DT2)));
				System.out.println(t);
			}
			system.updateParticles();
			System.out.println(t +"\t" +unloadMap.get(t));
			t += deltaT;
		}
		if(system.getRemainingParticles() == 0)
			System.out.println("EVERYBODY ESCAPED! t = " +t);
		Output.outputMap(unloadMap);
	}
}
