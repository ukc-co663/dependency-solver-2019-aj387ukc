package depsolver;

import java.util.ArrayList;
import java.util.List;

public class Package {
	
		private String name;
		private String version;
		private Integer size;
		private List<List<String>> depends = new ArrayList<>();
		private List<String> conflicts = new ArrayList<>();

		public String getName()
		{
			return name;
		}
		public String getVersion()
		{
			return version;
		}
		public Integer getSize()
		{
			return size;
		}
		public List<List<String>> getDepends()
		{
			return depends;
		}
		public List<String> getConflicts()
		{
			return conflicts;
		}
		public void setName(String name)
		{
			this.name = name;
		}
		public void setVersion(String version)
		{
			this.version = version;
		}
		public void setSize(Integer size)
		{
			this.size = size;
		}
		public void setDepends(List<List<String>> depends)
		{
			this.depends = depends;
		}
		public void setConflicts(List<String> conflicts)
		{
			this.conflicts = conflicts;
		}
		
		/**
		 * Split a package into name, symbol and version.
		 * 
		 * @param string The package that needs to be split.
		 * @return A string with the package split into 3 parts mentioned.
		 */
		public static String[] splitPackage(String string)
		{
			String pName = "";
			String pVer = "";
			String symbol = "";
			String[] pNV = new String[2]; 
			
			// If the package has <=
			if (string.contains("<="))
			{
				pNV = string.split("<=");
				pName = pNV[0];
				pVer = pNV[1];
				symbol = "<=";
			}
			// If the package has <
			else if (string.contains("<"))
			{
				pNV = string.split("<");
				pName = pNV[0];
				pVer = pNV[1];
				symbol = "<";
			}
			// If the package has >=
			else if (string.contains(">="))
			{
				pNV = string.split("=>");
				pName = pNV[0];
				pVer = pNV[1];
				symbol = ">=";
			}
			// If the package has >
			else if (string.contains(">"))
			{
				pNV = string.split(">");
				pName = pNV[0];
				pVer = pNV[1];
				symbol = ">";
			}
			// If the package has =
			else if (string.contains("="))
			{
				pNV = string.split("=");
				pName = pNV[0];
				pVer = pNV[1];
				symbol = "=";
			}
			// Else if the package is just a letter
			else
			{
				pName = string;
			}
			String[] result = {
				pName, pVer, symbol
			};
			return result;
		}
}
