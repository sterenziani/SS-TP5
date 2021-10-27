package back;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CellIndexFinder
{
	
	public static Map<Integer, List<Particle>> findNeighbors(List<Particle> particles, double L, double rc)
	{
		double maxR = 0;
		for(Particle p : particles)
		{
			if(p.getRadius() > maxR)
				maxR = p.getRadius();
		}
		int M = (int) (L / (rc + 2*maxR));
		
        // Put particles inside the matrix as Particles
        Cell[][] matrix = new Cell[M][M];
        double l = L/M;
        for(int i=0; i < M; i++)
        	for(int j=0; j < M; j++)
        		matrix[i][j] = new Cell();
        
        Map<Integer, List<Particle>> map = new HashMap<>();
        for(Particle p : particles)
        {
        	int row = (int) ((p.getY()+10) / l);
        	int col = (int) (p.getX() / l);
        	matrix[row][col].addParticle(p);
        	map.put(p.getId(), new ArrayList<>());
        }
        // Find neighbors
        for(int i=0; i < M; i++)
        {
        	for(int j=0; j < M; j++)
        	{
    			for(Particle p1 : matrix[i][j].getParticles())
    			{
    				// Get index of each cell to visit
    				int upperRow = i-1;
    				int lowerRow = i+1;
    				int rightCol = j+1;
    				
    				// Look for neighbors in this cell
    				for(Particle p2 : matrix[i][j].getParticles())
    				{
    					if(p1.getId() < p2.getId() && p1.isNeighbor(p2, rc))
    					{
    						map.get(p1.getId()).add(p2);
    						map.get(p2.getId()).add(p1);
    					}
    				}
    				// Check neighbors in Upper Cell, if reachable
    				if(upperRow >= 0)
    				{
        				for(Particle p2 : matrix[upperRow][j].getParticles())
        				{
        					if(!p1.equals(p2) && p1.isNeighbor(p2, rc))
        					{
        						map.get(p1.getId()).add(p2);
        						map.get(p2.getId()).add(p1);
        					}
        				}
    				}

    				// Check neighbors in Upper-Right Cell, if reachable
    				if(upperRow >= 0 && rightCol < M)
    				{
        				for(Particle p2 : matrix[upperRow][rightCol].getParticles())
        				{
        					if(!p1.equals(p2) && p1.isNeighbor(p2, rc))
        					{
        						map.get(p1.getId()).add(p2);
        						map.get(p2.getId()).add(p1);
        					}
        				}
    				}
    				
    				// Check neighbors in Right Cell, if reachable
    				if(rightCol < M)
    				{
        				for(Particle p2 : matrix[i][rightCol].getParticles())
        				{
        					if(!p1.equals(p2) && p1.isNeighbor(p2, rc))
        					{
        						map.get(p1.getId()).add(p2);
        						map.get(p2.getId()).add(p1);
        					}
        				}
    				}
    				
    				// Check neighbors in Lower-Right cell, if reachable
    				if(rightCol < M && lowerRow < M)
    				{
        				for(Particle p2 : matrix[lowerRow][rightCol].getParticles())
        				{
        					if(!p1.equals(p2) && p1.isNeighbor(p2, rc))
        					{
        						map.get(p1.getId()).add(p2);
        						map.get(p2.getId()).add(p1);
        					}
        				}
    				}
    			}
        	}
        }
        return map;
	}
}