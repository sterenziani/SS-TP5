package front;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import back.Particle;

public class Output {
	public static String OUTPUT_DIR = "output/";
	public static String RESULTS_DIR = "results/";
	
	private static File createFile(String outputFileName, String header)
	{
    	File file = new File(outputFileName);
		try
		{
			if(!file.createNewFile())
				file.delete();
			FileWriter writer = new FileWriter(outputFileName, true);
			writer.write(header+"\n");
			writer.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
		return file;
	}
    
    public static void resetFolder(String folderName)
    {
    	File folder = new File(folderName);
        File[] files = folder.listFiles();
        if(files!=null)
        {
            for(File f: files)
                f.delete();
        }
        folder.delete();
        folder.mkdir();        
    }
    
    public static void createFolder(String folderName)
    {
    	File folder = new File(folderName);
        if(!folder.exists())
        	folder.mkdir();        
    }

    public static void createRoom(Input input) throws IOException
    {
    	String filename = OUTPUT_DIR + "room-" + input.getGapSize() +".xyz";
    	StringBuilder s = new StringBuilder();
    	File file = new File(filename);
        file.delete();
        file.createNewFile();
        try (FileWriter writer = new FileWriter(filename, true))
        {
        	int lines = 0;
        	double radius = 0.05;
        	DecimalFormat df = new DecimalFormat("#.#");
        	df.setMaximumFractionDigits(5);
        	
			// Vertical walls
        	for(double y=0; y < input.getHeight(); y += radius)
        	{
        		s.append("0.00" +"\t" +df.format(y) +"\t"  +df.format(radius) +"\n");
        		s.append(df.format(input.getWidth()) +"\t" +df.format(y) +"\t"  +df.format(radius) +"\n");
        		lines += 2;
        	}
        	
        	// Horizontal walls
        	for(double x=0; x < input.getWidth(); x += radius)
        	{
        		if(x < (input.getWidth()-input.getGapSize())/2 || x > (input.getWidth()+input.getGapSize())/2)
        		{
        			s.append(df.format(x) +"\t" +"0.00" +"\t" +df.format(radius) +"\n");
        			lines++;
        		}
        		s.append(df.format(x) +"\t" +df.format(input.getHeight()) +"\t" +df.format(radius) +"\n");
        		lines++;
        	}
        	writer.write(lines +"\n\n");
        	writer.write(s.toString());
        	writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public static void outputAnimationFile(List<Particle> particles, double time, int index)
    {
		String outputFileName = OUTPUT_DIR + "output" +index +".xyz";
    	File file = createFile(outputFileName, particles.size() +"\n");
        try (FileWriter writer = new FileWriter(file, true))
        {
        	for(Particle p : particles)
        	{
        		writer.write(String.valueOf(p.getX()) +"\t" +String.valueOf(p.getY()) +"\t" +String.valueOf(p.getRadius()) +"\n");
        	}
        	writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public static void outputMap(Map<Double, Integer> map, double desiredV, int N, double d)
    {
    	createFolder(RESULTS_DIR);
    	DecimalFormat df = new DecimalFormat("#.#");
    	df.setMaximumFractionDigits(2);
		String outputFileName = RESULTS_DIR + "timestamps-" +df.format(desiredV) +"-" +N +"-" +df.format(d) +".csv";
    	File file = createFile(outputFileName, "t;n");
    	List<Entry<Double, Integer>> entries = new ArrayList<Map.Entry<Double, Integer>>(map.entrySet());
    	Collections.sort(entries, new Comparator<Map.Entry<Double, Integer>>()
		    	{
    		        public int compare(Entry<Double, Integer> a, Entry<Double, Integer> b)
    		        {	return Double.compare(a.getKey(), b.getKey());	}
		    	});
    	
        try (FileWriter writer = new FileWriter(file, true))
        {
        	for(Entry<Double, Integer> e : entries)
    			writer.write(e.getKey() +";" +e.getValue() +"\n");
        	writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
