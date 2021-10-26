package front;
import java.util.List;
import back.Particle;

public class Input
{
    double width;
    double height;
    double gapSize;
    List<Particle> particles;
    
	public Input(double width, double height, double gapSize, List<Particle> particles)
	{
		this.width = width;
		this.height = height;
		this.gapSize = gapSize;
		this.particles = particles;
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

	public double getGapSize() {
		return gapSize;
	}

	public List<Particle> getParticles() {
		return particles;
	}
	
	public int getN() {
		return particles.size();
	}
}
