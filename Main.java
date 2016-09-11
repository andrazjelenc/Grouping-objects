import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class Main {

	public static void main(String[] args) 
	{
		Scanner sc = new Scanner(System.in);
		
		int n = sc.nextInt(); //number of objects
		int m = sc.nextInt(); //number of compare dimensions
		
		int k = sc.nextInt(); //number of groups
		
		int iterations = 100; //how many times should i try it
		
		if(k >= n)
		{
			System.out.println("More groups than objects!");
			System.exit(0);
		}
		
		double data[][] = new double[n][m];
		
		for(int i = 0; i < n; i++)
		{
			for(int j = 0; j < m; j++)
			{
				data[i][j] = sc.nextInt();
			}
		}
		sc.close();
		
		List<HashSet<Integer>> groups = hevristic(data, n, m, k, iterations);
		if(groups == null)
		{
			System.out.println("Something went wrong!");
		}
		
		for(int i = 0; i < k; i++)
		{
			//System.out.println("Center: " + objToString(centers[i]));
			System.out.println("Group #:");
			for(Integer e : groups.get(i))
			{
				System.out.println("--> " + objToString(data[e]));
			}
		}
		
		
	}
	
	private static List<HashSet<Integer>> hevristic(double[][] data, int n, int m, int k, int iterations) 
	{
		double minDistance = Double.MAX_VALUE;
		//double minCenters[][] = new double[k][m];
		List<HashSet<Integer>> minGroups = null;// = new ArrayList<HashSet<Integer>>();
		
		int[] rowIndexes = new int[n];
		for(int i = 0; i < n; i++)
		{
			rowIndexes[i] = i;
		}
		
		
		for(int it = 0; it < iterations; it++)
		{
			double centers[][] = new double[k][m];
			List<HashSet<Integer>> groups = new ArrayList<HashSet<Integer>>();
			
			double localDistance = 0;
			
			shuffleArray(rowIndexes);
			for(int i = 0; i < k; i++)
			{
				centers[i] = data[rowIndexes[i]].clone();
				groups.add(new HashSet<Integer>());
			}
				
			while(true)
			{
				//clear sets
				for(HashSet<Integer> set : groups)
				{
					set.clear();
				}

				//go trough all object and add it to best suitable group
				for(int i = 0; i < n; i++)
				{
					double distance = Double.MAX_VALUE;
					int group = -1;
						
					for(int j = 0; j < k; j++)
					{
						double currDistance = diff(data[i], centers[j]);
						if(currDistance < distance)
						{
							distance = currDistance;
							group = j;
						}
					}
					localDistance += Math.sqrt(distance);
					groups.get(group).add(i);
				}
					
				boolean changes = false;
				//calculate average of each center
				for(int i = 0; i < k; i++)
				{
					double newCenter[] = new double[m];
					for(int e : groups.get(i))
					{
						for(int l = 0; l < m; l++)
						{
							newCenter[l] += data[e][l];
						}
					}
					for(int j = 0; j < m; j++)
					{
						newCenter[j] /= groups.get(i).size();
					}
					if(!Arrays.equals(newCenter, centers[i]))
					{
						changes = true;
					}
					centers[i] = newCenter;//.clone();
				}
					
				if(!changes)
				{
					//System.out.println("DONE");
					break;
				}
				//wait for user input
				//sc.nextLine();
			}
			
			//check if we find better solutions
			if(localDistance < minDistance)
			{
				minGroups = groups;
				minDistance = localDistance;
			}
		}
				
				
		return minGroups;
	}

	private static double diff(double o1[], double o2[])
	{
		double d = 0;
		for(int i = 0; i < o1.length; i++)
		{
			d += Math.abs(o1[i] - o2[i]);
		}
		return d;
	}

	private static String objToString(double o[])
	{
		String output = "(";
		for(int i = 0; i < o.length; i++)
		{
			output += o[i] +", ";
		}
		output = output.substring(0, output.length()-2);
		output += ")";
		return output;
	}
	
	static void shuffleArray(int[] ar)
	{
		Random rnd = ThreadLocalRandom.current();
	    	for (int i = ar.length - 1; i > 0; i--)
	    	{
	    		int index = rnd.nextInt(i + 1);
	    		// Simple swap
	    		int a = ar[index];
	    		ar[index] = ar[i];
	    		ar[i] = a;
    		}
  	}
}
