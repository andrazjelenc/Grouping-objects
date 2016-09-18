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
		//read input parameters
		Scanner sc = new Scanner(System.in);
		
		String[] params = sc.nextLine().split(" ");
		int n = Integer.parseInt(params[0]); //number of objects
		int m = Integer.parseInt(params[1]); //number of compare dimensions
		int k =Integer.parseInt(params[2]); //number of groups
		int iterations = Integer.parseInt(params[3]); //max number of tries
		
		if(k >= n)
		{
			System.out.println("More groups than objects!");
			System.exit(0);
		}
		
		//read data
		double data[][] = new double[n][m];
		String names[] = new String[n];
		
		for(int i = 0; i < n; i++)
		{
			//name x1 x2 x3 .. xm
			String line = sc.nextLine();
			String razbito[] = line.split(" ");
			names[i] = razbito[0].trim();
			for(int j = 0; j < m; j++)
			{
				data[i][j] = Double.parseDouble(razbito[1+j].trim());
			}
		}
		sc.close();

		System.out.println("Number of objects: " + n);
		System.out.println("Dimensions: " + m);
		System.out.println("Number of requested groups: " + k);
		System.out.println("Max number of iterations: " + iterations);
		
		//calculate
		List<HashSet<Integer>> groups = calculate(data, n, m, k, iterations);
		if(groups == null)
		{
			System.out.println("Something went wrong!");
			System.exit(0);
		}
		
		//print output
		for(int i = 0; i < k; i++)
		{
			System.out.println("Group #:");
			for(Integer e : groups.get(i))
			{
				System.out.println("--> " + names[e] + ": " +  objToString(data[e]));
			}
		}
	}
	
	private static List<HashSet<Integer>> calculate(double[][] data, int n, int m, int k, int iterations) 
	{
		double minDistance = Double.MAX_VALUE;
		
		List<HashSet<Integer>> minGroups = null;
		List<HashSet<Integer>> currentGroups = null;
		
		double centers[][] = new double[k][m];
		
		int options = nCr(n,k); //combinations
		
		System.out.println("All possible compinations: " + options);
		if(options <= iterations && options > 0) //we can check everything
		{			
			System.out.println("Checking all combinations...");
			
			int sub[] = new int[k];
			for(int i = 0; i < k; i++) //init
			{
				sub[i] = i;
			}
			
			for(int i = 0; i < options; i++)
			{
				for(int j = 0; j < k; j++)
				{
					centers[j] = data[sub[j]].clone();
				}
				currentGroups = useCenters(data, centers, n, m, k);
				if(currentDistance < minDistance)
				{
					minDistance = currentDistance;
					minGroups = currentGroups;
				}
				
				//moving on the next combination
				for(int p = k-1; p >= 0; p--)
				{
					if(p == k-1)
					{
						if(sub[p] < n-1)
						{
							sub[p]++;
							break;
						}
					}
					else
					{
						if(sub[p]+1 < sub[p+1])
						{
							sub[p]++;
							for(int l = p+1; l < k; l++)
							{
								sub[l] = sub[l-1]+1;
							}
							break;
						}
					}
				}
			}
			
		}
		else
		{
			//randomized
			System.out.println("Using random start centers...");
			int[] rowIndexes = new int[n];
			for(int i = 0; i < n; i++)
			{
				rowIndexes[i] = i;
			}
			
			for(int it = 0; it < iterations; it++)
			{
				shuffleArray(rowIndexes);
				for(int i = 0; i < k; i++)
				{
					centers[i] = data[rowIndexes[i]].clone();
				}
				
				//work with that center
				currentGroups = useCenters(data, centers, n, m, k);
				if(currentDistance < minDistance)
				{
					minDistance = currentDistance;
					minGroups = currentGroups;
				}
			}
		}
		return minGroups;
		
	}
	
	private static double currentDistance;
	private static List<HashSet<Integer>> useCenters(double[][] data, double[][] centers, int n, int m, int k)
	{
		currentDistance = 0;
		
		List<HashSet<Integer>> groups = new ArrayList<HashSet<Integer>>();
		for(int i = 0; i < k; i++)
		{
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
				currentDistance += Math.pow(distance,2);
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
				
			if(!changes) //local optimum
			{
				break;
			}
		}
		return groups;
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
	
	private static void shuffleArray(int[] ar)
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
	
	private static int nCr(int n, int r)
	{
		int result = 1;		
		for(int dump = 0; dump < r; dump++)
		{
			result = result * (n-dump)/(dump+1);
			if(result < 0) return -1; //in case of overflow
		}
		return result;
	}
}
