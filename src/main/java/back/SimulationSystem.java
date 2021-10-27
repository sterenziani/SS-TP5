package back;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import front.Input;

public class SimulationSystem
{
	private List<Particle> particles;
	private double width;
	private double height;
	private double gapSize;
	private double deltaT;
	private static final int A = 2000;
	private static final double B = 0.08;
	private static final double kn = 1.2 * Math.pow(10,5);
	private static final double kt = 2.4 * Math.pow(10,5);
	private static final double T = 0.5;
	private static final double GOAL_WIDTH = 3;
	private static final double GOAL_Y = -10;
	
	private Map<Integer, Double> FmapX;
	private Map<Integer, Double> FmapY;
	
	public SimulationSystem(Input input, double deltaT)
	{
		this.particles = input.getParticles();
		this.width = input.getWidth();
		this.height = input.getHeight();
		this.gapSize = input.getGapSize();
		this.deltaT = deltaT;
		FmapX = new HashMap<>();
		FmapY = new HashMap<>();
	}

	public void updateValues()
	{
		for(Particle p : particles)
		{
			p.setPrevAx(p.getAx());
			p.setPrevAy(p.getAy());
			
			updateForces(p);
			double Fx = FmapX.get(p.getId());
			double Fy = FmapY.get(p.getId());
			double m = p.getMass();
			p.setAx(Fx/m);
			p.setAy(Fy/m);
			
			p.setVx(p.getVx() + deltaT*p.getAx());
			p.setVy(p.getVy() + deltaT*p.getAy());
			p.setX(p.getX() + deltaT*(p.getVx() + deltaT*p.getAx()));
			p.setY(p.getY() + deltaT*(p.getVy() + deltaT*p.getAy()));
		}
	}

	public void updateParticles()
	{
		List<Particle> removed = new ArrayList<>();
		for(Particle p : particles)
		{
			double ax = p.getAx();
			double ay = p.getAy();
			updateForces(p);
			double Fx = FmapX.get(p.getId());
			double Fy = FmapY.get(p.getId());
			double m = p.getMass();
			p.setAx(Fx/m);
			p.setAy(Fy/m);
			if(beemanEvolve(p, ax, ay))
				removed.add(p);
		}
		particles.removeAll(removed);
	}
	
	public void updateForces(Particle p)
	{
		FmapX.put(p.getId(), 0.0);
		FmapY.put(p.getId(), 0.0);
		addGranularForce(p);
		addSocialForce(p);
		addDesireForce(p);
	}

	private boolean beemanEvolve(Particle p, double ax, double ay)
	{
		double nextAx = p.getAx();
		double nextAy = p.getAy();
		double prevAx = p.getPrevAx();
		double prevAy = p.getPrevAy();
		double nextX = p.getX() + p.getVx()*deltaT + (2.0/3.0)*p.getAx()*Math.pow(deltaT, 2) - (1.0/6.0)*p.getPrevAx()*Math.pow(deltaT, 2);
		double nextY = p.getY() + p.getVy()*deltaT + (2.0/3.0)*p.getAy()*Math.pow(deltaT, 2) - (1.0/6.0)*p.getPrevAy()*Math.pow(deltaT, 2);
		if(nextY < GOAL_Y)
			return true;
		
		p.setX(nextX);
		p.setY(nextY);
		double nextVx = p.getVx() + deltaT*(2*nextAx + 5*ax - prevAx)/6;
		double nextVy = p.getVy() + deltaT*(2*nextAy + 5*ay - prevAy)/6;
		p.setVx(nextVx);
		p.setVy(nextVy);
		p.setPrevAx(ax);
		p.setPrevAy(ay);
		return false;
	}

	private void addGranularForce(Particle i)
	{
		double totalFGx = 0;
		double totalFGy = 0;
		for(Particle j: particles)
		{
			double overlap = i.getOverlap(j);
			if(overlap > 0 && j.getId() != i.getId())
			{
				double dx = j.getX() - i.getX();
				double dy = j.getY() - i.getY();
				double dist = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));

				double enx = dx/dist;
				double eny = dy/dist;

				double fn = -kn * overlap;
				double ft = -kt * overlap * i.relativeV(j, enx, eny);

				totalFGx += fn*enx - ft*eny;
				totalFGy += fn*eny + ft*enx;
			}
		}

		for(Walls w : Walls.values())
		{
			double overlap = i.overlapWall(w, width, height, gapSize);
			if(overlap > 0)
			{
				double enx = 0;
				double eny = 0;
				switch(w)
				{
            		case UP:
            			enx = 0.0;
            			eny = 1.0;
            			break;
            		case LEFT:
            			enx = -1.0;
            			eny = 0.0;
            			break;
            		case RIGHT:
            			enx = 1.0;
            			eny = 0.0;
            			break;
            		case DOWN:
            			enx = 0.0;
            			eny = -1.0;
            			break;
            	}
				double fn = -kn * overlap;
				double ft = -kt * overlap * (i.getVx()*(-eny) + i.getVy()*enx);
				totalFGx += fn*enx - ft*eny;
				totalFGy += fn*eny + ft*enx;
			}
		}
		double currentFx = FmapX.getOrDefault(i.getId(), 0.0);
		double currentFy = FmapY.getOrDefault(i.getId(), 0.0);
		currentFx += totalFGx;
		currentFy += totalFGy;
		FmapX.put(i.getId(), currentFx);
		FmapY.put(i.getId(), currentFy);
	}

	private void addSocialForce(Particle i)
	{
		double totalFSx = 0;
		double totalFSy = 0;
		for(Particle j: particles)
		{
			if(j.getId() != i.getId())
			{
				double dx = j.getX() - i.getX();
				double dy = j.getY() - i.getY();
				double dist = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
				double enx = dx/dist;
				double eny = dy/dist;

				double fs = -A * Math.exp(-i.getEdgeDistance(j) / B);
				totalFSx += fs*enx;
				totalFSy += fs*eny;
			}
		}
		double currentFx = FmapX.get(i.getId());
		double currentFy = FmapY.get(i.getId());
		currentFx += totalFSx;
		currentFy += totalFSy;
		FmapX.put(i.getId(), currentFx);
		FmapY.put(i.getId(), currentFy);
	}
	
	private void addDesireForce(Particle i)
	{
		double FDx = 0;
		double FDy = 0;
		
		double dx = getTargetX(i) - i.getX();
		double dy = getTargetY(i) - i.getY();
		double dist = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));

		double enx = dx/dist;
		double eny = dy/dist;
		
		FDx = i.getMass() * (i.getDesiredV() * enx - i.getVx()) / T;
		FDy = i.getMass() * (i.getDesiredV() * eny - i.getVy()) / T;
		
		double currentFx = FmapX.get(i.getId());
		double currentFy = FmapY.get(i.getId());
		currentFx += FDx;
		currentFy += FDy;
		FmapX.put(i.getId(), currentFx);
		FmapY.put(i.getId(), currentFy);
	}
	
	private double getTargetX(Particle p)
	{
		if(p.getY() > 0)
		{
			if(p.getX() > width/2)
				return Math.min(p.getX(), (width+gapSize-0.2)/2);
			else
				return Math.max(p.getX(), (width-gapSize+0.2)/2);
		}
		else
		{
			if(p.getX() > width/2)
				return Math.min(p.getX(), (width+GOAL_WIDTH)/2);
			else
				return Math.max(p.getX(), (width-GOAL_WIDTH)/2);
		}
	}
	
	private double getTargetY(Particle p)
	{
		if(p.getY() < 0)
			return GOAL_Y;
		else
			return 0;
	}
	
	public List<Particle> getStatus()
	{
		List<Particle> ret = new ArrayList<>();
		for(Particle p : particles)
			ret.add(new Particle(p.getId(), p.getX(), p.getY(), p.getVx(), p.getVy(),
					p.getAx(), p.getAy(), p.getMass(), p.getRadius(), p.getDesiredV()));
		return ret;
	}
	
	public int getRemainingParticles()
	{
		return particles.size();
	}
}