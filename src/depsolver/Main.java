package depsolver;

// Java API
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// JSON API
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

public class Main {
	
	public static HashMap<String, Integer> solutions = new HashMap<>();
	public static List<String> commands = new ArrayList<>();
	
	static String readFile(String filename) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		StringBuilder sb = new StringBuilder();
		br.lines().forEach(line -> sb.append(line));
		br.close();
		return sb.toString();
	}
	
	// The main function
	public static void main(String[] args) throws IOException
	{
		TypeReference<List<Package>> repoType = new TypeReference<List<Package>>() {};
		List<Package> repo = JSON.parseObject(readFile(args[0]), repoType);
		TypeReference<List<String>> strListType = new TypeReference<List<String>>() {};
		List<String> initial = JSON.parseObject(readFile(args[1]), strListType);
		List<String> constraints = JSON.parseObject(readFile(args[2]), strListType);
		
		Search.search(repo, initial, constraints, initial);
		getBestSolution();
	}	
	
	/**
	 * Check if packages are valid.
	 * 
	 * @param packs The packages that need to be checked.
	 * @param repo The repository.
	 * @return A boolean.
	 */
	public static boolean isValid(List<String> packs, List<Package> repo)
	{
		boolean isDep;
		boolean isConflict;
		HashMap<String, Integer> conflictHash = new HashMap<>();
		for (Package p : repo)
		{
			if (packs.contains(p.getName() + "=" + p.getVersion()))
			{
				for (List<String> depends : p.getDepends())
				{
					isDep = false;
					for (String dependency : depends)
					{
						String[] splitDep = Package.splitPackage(dependency);
						String symbol = splitDep[2];
						if (!isDep)
						{
							for (String initp : packs)
							{
								String[] pSplit = Package.splitPackage(initp);
								if (splitDep[0].equals(pSplit[0]))
								{
									switch(symbol)
									{
										case "=":
											if (dependency.equals(initp))
											{
												isDep =  true;
											}
											break;
										case "<":
											if (!splitDep[1].equals(pSplit[1]))
											{
												if (compareVer(splitDep[1], pSplit[1]))
												{
													isDep =  true;
												}
											}
											break;
										case "<=":
											if (compareVer(splitDep[1], pSplit[1]))
											{
												isDep =  true;
											}
											break;
										case ">":
											if (!splitDep[1].equals(pSplit[1]))
											{
												if (compareVer(pSplit[1], splitDep[1]))
												{
													isDep =  true;
												}
											}
											break;
										case ">=":
											if (compareVer(pSplit[1], splitDep[1]))
											{
												isDep =  true;
											}
											break;
										default:
											isDep = true;
											break;
									}
								}
							}
						}
					}
					if (!isDep)
					{
						return false;
					}
				}
				isConflict = false;
				for (String conflicts : p.getConflicts())
				{
					String[] confSplit = Package.splitPackage(conflicts);
					String symbol = confSplit[2];
					if (!isConflict)
					{
						for (String temp : packs)
						{
							String[] tempSplit = Package.splitPackage(temp);
							if (confSplit[0].equals(tempSplit[0]))
							{
								switch(symbol)
								{
									case "=":
										if (conflicts.equals(temp))
										{
											isConflict = true;
											Integer value = conflictHash.get(temp);
											if (value != null)
											{
												conflictHash.put(temp, value++);
											}
											else
											{
												conflictHash.put(temp, 1);
											}
										}
										break;
									case "<":
										if (!confSplit[1].equals(tempSplit[1]))
										{
											if (compareVer(confSplit[1], tempSplit[1]))
											{
												isConflict =  true;
												Integer value = conflictHash.get(temp);
												if (value != null)
												{
													conflictHash.put(temp, value++);
												}
												else
												{
													conflictHash.put(temp, 1);
												}
											}
										}
										break;
									case "<=":
										if (compareVer(confSplit[1], tempSplit[1]))
										{
											isConflict =  true;
											Integer value = conflictHash.get(temp);
											if (value != null)
											{
												conflictHash.put(temp, value++);
											}
											else
											{
												conflictHash.put(temp, 1);
											}
										}
										break;
									case ">":
										if (!confSplit[1].equals(tempSplit[1]))
										{
											if (compareVer(tempSplit[1], confSplit[1]))
											{
												isConflict =  true;
												Integer value = conflictHash.get(temp);
												if (value != null)
												{
													conflictHash.put(temp, value++);
												}
												else
												{
													conflictHash.put(temp, 1);
												}
											}
										}
										break;
									case ">=":
										if (compareVer(tempSplit[1], confSplit[1]))
										{
											isConflict =  true;
											Integer value = conflictHash.get(temp);
											if (value != null)
											{
												conflictHash.put(temp, value++);
											}
											else
											{
												conflictHash.put(temp, 1);
											}
										}
										break;
									default:
										isConflict = true;
										Integer value = conflictHash.get(temp);
										if (value != null)
										{
											conflictHash.put(temp, value++);
										}
										else
										{
											conflictHash.put(temp, 1);
										}
										break;
								}
							}
						}
					}
				}
				if (isConflict)
				{
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Compare 2 versions of packages.
	 * 
	 * @param ver1 The first version
	 * @param ver2 The second version
	 * @return A boolean.
	 */
	private static boolean compareVer(String ver1, String ver2)
	{
		String ver1temp = ver1.replace("0", "");
		String ver2temp = ver2.replace("0", "");
		if (!ver1temp.isEmpty())
		{
			ver1 = ver1temp;
		}
		if (!ver2temp.isEmpty())
		{
			ver2 = ver2temp;
		}
		
		List<String> ver1List = new ArrayList<String>(Arrays.asList(ver1.split("\\.")));
		List<String> ver2List = new ArrayList<String>(Arrays.asList(ver2.split("\\.")));
		
		while(ver1List.size() != ver2List.size())
		{
			if (ver1List.size() > ver2List.size())
			{
				ver2List.add("0");
			}
			else
			{
				ver1List.add("0");
			}
		}
		
		for (int i = 0; i < ver1List.size(); i++)
		{
			if (Integer.parseInt(ver1List.get(i)) > Integer.parseInt(ver2List.get(i)))
			{
				return true;
			}
			else if (Integer.parseInt(ver2List.get(i)) > Integer.parseInt(ver1List.get(i)))
			{
				return false;
			}
		}
		return true;
	}
		
	/**
	 * Calculate the cost of a solution.
	 * 
	 * @param repo The repository.
	 * @return The cost of the solution.
	 */
	public static int solutionCost(List<Package> repo)
	{
		int cost = 0;
		for (String command : commands)
		{
			if (command.contains("-"))
			{
				cost += 1000000;
			}
			else
			{
				for (Package p : repo)
				{
					String pNameVer = p.getName() + "=" + p.getVersion();
					// Remove the install/uninstall symbol
					String comPack = command.substring(1);
					if (comPack.equals(pNameVer))
					{
						cost += p.getSize();
					}
				}
			}
		}
		return cost;
	}
	
	/**
	 * Print the best solution.
	 */
	private static void getBestSolution()
	{
		List<String> result = new ArrayList<>();
		int resultCost = 0;
		for (Map.Entry<String, Integer> entry : solutions.entrySet())
		{
			if (entry.getValue() < resultCost || result.size() == 0)
			{
				result = Arrays.asList(entry.getKey().split(","));
				resultCost = entry.getValue();
			}
		}
		System.out.println(JSON.toJSON(result));
	}
	
}
