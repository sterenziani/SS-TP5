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
	public static final double kn = 1.2 * Math.pow(10,5);
	private static final double kt = 2.4 * Math.pow(10,5);
	private static final double T = 0.5;
	private static final double DOOR_MARGIN = 0.2;
	private static final double GOAL_WIDTH = 3;
	private static final double GOAL_Y = -10;
	
	private static final double rc = 2;
	
	private Map<Integer, Double> FmapX;
	private Map<Integer, Double> FmapY;
	
	private Map<Integer, Double> unloadMapIds = new HashMap<>();
	private Map<Integer, Double> unloadMapAmounts = new HashMap<>();
	
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

	public void updateParticles(double t)
	{
		List<Particle> removed = new ArrayList<>();
		Map<Integer, List<Particle>> neighbors = CellIndexFinder.findNeighbors(particles, height-GOAL_Y, rc);
		for(Particle p : particles)
			updateForces(p, neighbors);
		for(Particle p : particles)
		{
			if(beemanEvolve(p))
				removed.add(p);
			if(p.getY() < 0 && !unloadMapIds.containsKey(p.getId()))
			{
				unloadMapIds.put(p.getId(), t);
				unloadMapAmounts.put(unloadMapIds.size(), t);
			}
		}
		particles.removeAll(removed);
	}
	
	public Map<Integer, Double> getUnloadMap()
	{
		return unloadMapAmounts;
	}
	
	public void updateForces(Particle p, Map<Integer, List<Particle>> neighbors)
	{
		FmapX.put(p.getId(), 0.0);
		FmapY.put(p.getId(), 0.0);
		addGranularForce(p, neighbors.get(p.getId()));
		addSocialForce(p, neighbors.get(p.getId()));
		addDesireForce(p);
	}

	private boolean beemanEvolve(Particle p)
	{
		double Fx = FmapX.get(p.getId());
		double Fy = FmapY.get(p.getId());
		double nextAx = Fx/p.getMass();
		double nextAy = Fy/p.getMass();
		double ax = p.getAx();
		double ay = p.getAy();
		double prevAx = p.getPrevAx();
		double prevAy = p.getPrevAy();
		p.setX(p.getX() + p.getVx()*deltaT + (2.0/3.0)*ax*Math.pow(deltaT, 2) - (1.0/6.0)*prevAx*Math.pow(deltaT, 2));
		p.setY(p.getY() + p.getVy()*deltaT + (2.0/3.0)*ay*Math.pow(deltaT, 2) - (1.0/6.0)*prevAy*Math.pow(deltaT, 2));
		p.setVx(p.getVx() + deltaT*(2*nextAx + 5*ax - prevAx)/6);
		p.setVy(p.getVy() + deltaT*(2*nextAy + 5*ay - prevAy)/6);
		p.setPrevAx(ax);
		p.setPrevAy(ay);
		p.setAx(nextAx);
		p.setAy(nextAy);
		if(p.getY() < GOAL_Y)
			return true;
		return false;
	}

	private void addGranularForce(Particle i, List<Particle> neighbors)
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
            			if(i.getY() > 0)
                			eny = -1.0;
            			else
                			eny = 1.0;
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

	private void addSocialForce(Particle i, List<Particle> neighbors)
	{
		double totalFSx = 0;
		double totalFSy = 0;
		for(Particle j: neighbors)
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
				return Math.min(p.getX(), (width+gapSize-DOOR_MARGIN)/2);
			else
				return Math.max(p.getX(), (width-gapSize+DOOR_MARGIN)/2);
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
