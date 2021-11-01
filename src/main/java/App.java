import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
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
		List<Particle> particles = input.getParticles();
		double deltaT = 0.001; //0.1 * Math.sqrt(particles.get(0).getMass() / SimulationSystem.kn);
		System.out.println("dt = " +deltaT);
		double t = 0.0;
		
		double desiredV = particles.get(0).getDesiredV();
		int N = input.getN();
		int evacuated = 0;
		int prevEvacuated = 0;
		Map<Integer, Double> unloadMap = new HashMap<>();
		SimulationSystem system = new SimulationSystem(input, deltaT);
		system.updateValues();
		
		unloadMap.put(0, 0.0);
		Instant startTime = Instant.now();
		while(t <= 500 && system.getRemainingParticles() > 0)
		{
			if(N-system.getRemainingParticles() > evacuated)
			{
				evacuated = N-system.getRemainingParticles();
				for(int i=prevEvacuated+1; i <= evacuated; i++)
					unloadMap.put(i, t);
				prevEvacuated = evacuated;
			}
			if( (int)(t/deltaT) % DT2 == 0 )
			{
				Output.outputAnimationFile(particles, t, (int)(t/(deltaT*DT2)));
				System.out.println(t);
			}
			system.updateParticles();
			t += deltaT;
		}
		System.out.println("Finished in " +Duration.between(startTime, Instant.now()).toMinutes() +" mins");
		unloadMap.put(N-system.getRemainingParticles(), t);
		
		if(system.getRemainingParticles() == 0)
			System.out.println("EVERYBODY ESCAPED! t = " +t);
		Output.outputMap(unloadMap, desiredV, N, input.getGapSize());
	}
}
