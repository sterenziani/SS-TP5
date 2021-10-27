package back;
import java.util.LinkedList;
import java.util.List;

public class Cell {
	private List<Particle> particles;
	
	public Cell()
	{
		this.particles = new LinkedList<>();
	}

	public boolean addParticle(Particle p) {
		return particles.add(p);
	}
	
	public boolean removeParticle(Particle p) {
		return particles.remove(p);
	}
	
	public List<Particle> getParticles(){
		return particles;
	}
	
	public Particle getHead() {
		if(particles.isEmpty())
			return null;
		return particles.get(0);
	}
}