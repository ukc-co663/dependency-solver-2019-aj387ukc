package depsolver;

import java.util.HashSet;
import java.util.List;

public class Search {
	
	private static HashSet<List<String>> visited = new HashSet<>();
	
	/**
	 * Check if a series of packages to be installed/uninstalled are
	 * all present.
	 * 
	 * @param pSet The list of packages.
	 * @param constraints The constraints.json
	 * @return A boolean.
	 */
	public static boolean allPacks(List<String> constraints, List<String> pList)
	{
		boolean constraintMet;
		for (String cons : constraints)
		{
			// + or -
			String initSymbol = Character.toString(cons.charAt(0));
			cons = cons.substring(1);
			String[] consNV = Package.splitPackage(cons); 
			String consName = consNV[0];
			String consVer = consNV[1];
			String consSymbol = consNV[2];
			
			constraintMet = false;
			if (initSymbol.equals("+"))
			{
				for (String p : pList)
				{
					String[] packSplit = Package.splitPackage(p);
					if (consName.equals(packSplit[0]))
					{
						if (consSymbol.equals("="))
						{
							if (consVer.equals(packSplit[1]))
							{
								constraintMet = true;
								break;
							}
						}
						else if (consSymbol.equals(""))
						{
							constraintMet = true;
							break;
						}
					}
				}
			}
			if (!constraintMet)
			{
				return false;
			}
			if (initSymbol.equals("-"))
			{
				int i = 0;
				for (String p : pList)
				{
					String[] packSplit = Package.splitPackage(p);
					if (consSymbol.equals("="))
					{
						if (consName.equals(packSplit[0]) && consVer.equals(packSplit[1]))
						{
							++i;
						}
					}
				}
				if (i > 0)
				{
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Search for the packages that satisfy the constraints.
	 * 
	 * @param tempSet The list that will be used to store stuff.
	 * @param repo The repository.
	 * @param initial The initial states.
	 * @param constraints The constraints.
	 */
	public static void search(List<Package> repo, List<String> initial, List<String> constraints, List<String> tempSet)
	{
		// If the package isn't valid
		if (!Main.isValid(tempSet, repo))
		{ 
			return; 
		}
		// If the package was visited already
		if (visited.contains(tempSet))
		{
			return;
		}
		if (allPacks(constraints, tempSet))
		{
			int costOfSolution = Main.solutionCost(repo);
			String temp = "";
			for (String str : Main.commands)
			{
				temp += str+",";
			}
			Main.solutions.put(temp, costOfSolution);
			return;	
		}
		visited.add(tempSet);
		for (Package p : repo)
		{
			if (!tempSet.contains(p.getName() + "=" + p.getVersion()) && !Main.commands.contains("-" + p.getName() + "=" + p.getVersion()))
			{
				tempSet.add(p.getName() + "=" + p.getVersion());
				Main.commands.add("+" + p.getName() + "=" + p.getVersion());				
				search(repo, initial, constraints, tempSet);
				Main.commands.remove("+" + p.getName() + "=" + p.getVersion());
				tempSet.remove(p.getName() + "=" + p.getVersion());
			}
			else if (initial.contains(p.getName() + "=" + p.getVersion()))
			{
				tempSet.remove(p.getName() + "=" + p.getVersion());
				Main.commands.add("-" + p.getName() + "=" + p.getVersion());
				search(repo, initial, constraints, tempSet);
				Main.commands.remove("-" + p.getName() + "=" + p.getVersion());
				tempSet.add(p.getName() + "=" + p.getVersion());
			}
		}
	}
}
