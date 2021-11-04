package back;

public class Particle {
    private int id;
    private double radius;
    private double mass;
    private double x;
    private double y;
    private double vx;
    private double vy;
    private double ax;
    private double ay;
    private double prevAx;
    private double prevAy;
    private double desiredV;

    public Particle(int id, double x, double y, double vx, double vy, double ax, double ay, double mass, double radius, double desiredV)
    {
	    this.id = id;
	    this.x = x;
	    this.y = y;
	    this.radius = radius;
	    this.mass = mass;
	    this.vx = vx;
	    this.vy = vy;
	    this.ax = ax;
	    this.ay = ay;
	    this.setDesiredV(desiredV);
	}
    
    public int getId() {
		return id;
	}

    public double getRadius() {
		return radius;
	}

    public double getMass() {
		return mass;
	}

    public double getX() {
		return x;
	}

    public void setX(double x) {
		this.x = x;
	}

    public double getY() {
		return y;
	}

    public void setY(double y) {
		this.y = y;
	}

    public double getVx() {
		return vx;
	}

    public void setVx(double vx) {
		this.vx = vx;
	}

    public double getVy() {
		return vy;
	}

    public void setVy(double vy) {
		this.vy = vy;
	}

    public double getAx() {
		return ax;
	}

    public void setAx(double ax) {
		this.ax = ax;
	}

    public double getAy() {
		return ay;
	}

    public void setAy(double ay) {
		this.ay = ay;
	}

    public double getPrevAx() {
		return prevAx;
	}

    public void setPrevAx(double prevAx) {
		this.prevAx = prevAx;
	}

    public double getPrevAy() {
		return prevAy;
	}

    public void setPrevAy(double prevAy) {
		this.prevAy = prevAy;
	}

    public double getV() {
    	double v2 = Math.pow(vx,2) + Math.pow(vy, 2);
    	return Math.sqrt(v2);
    }

    public double getCenterDistance(Particle p) {
    	double diffX = p.getX() - x;
    	double diffY = p.getY() - y;
    	double diff2 = Math.pow(diffX, 2) + Math.pow(diffY, 2);
    	return Math.sqrt(diff2);
    }
    
    public double getEdgeDistance(Particle p) {
        return getCenterDistance(p) - p.getRadius() - radius;
    }
	
    public double getOverlap(Particle p)
    {
        if(p.getId() == id)
            return -1;
        return -getEdgeDistance(p);
    }

    public double relativeV(Particle p, double enx, double eny) {
        return (vx - p.getVx())*(-eny) + (vy - p.getVy())*enx;
    }

    public double overlapWall(Walls wall, double width, double height, double gapSize)
    {
        switch(wall)
        {
            case LEFT:
                return radius - Math.abs(0 - x);
            case RIGHT:
                return radius - Math.abs(width - x);
            case UP:
            	return radius - Math.abs(height - y);
            case DOWN:
                if(x > (width - gapSize)/2 && x < (width + gapSize)/2) 
                    return -1;
                else
                    return radius - Math.abs(0 - y);
        }
        return -1;
    }

	public double getDesiredV() {
		return desiredV;
	}

	public void setDesiredV(double desiredV) {
		this.desiredV = desiredV;
	}
	
	public boolean isNeighbor(Particle p2, double rc)
	{
		return Double.compare(Math.abs(getEdgeDistance(p2)), rc) <= 0;
	}
}
