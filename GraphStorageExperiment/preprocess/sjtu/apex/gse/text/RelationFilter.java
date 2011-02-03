package sjtu.apex.gse.text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class RelationFilter {
	
	static final String[] res = {"<http://", "<http://dbpedia.org/resource/"};
	static final String[] prp = {"<http://dbpedia.org/property/", "<http://www.w3.org/1999/02/22-rdf-syntax-ns#", "<http://sjtu.edu.cn#"};
	
	private static boolean isResource(String s) {
		for (int i = 0; i < res.length; i++)
			if (s.startsWith(res[i]))
				return true;
		return false;
	}
	
	private static String getRelLabel(String s) {
		for (int i = 0; i < prp.length; i++)
			if (s.startsWith(prp[i]))
				return s.substring(prp[i].length(), s.length() - 1).toLowerCase();
		
		return null;
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		BufferedReader rd = new BufferedReader(new FileReader(args[0]));
		BufferedWriter wr = new BufferedWriter(new FileWriter(args[1]));
		String tmp;
		
//		int plen = prp.length();
		
		while ((tmp = rd.readLine()) != null) {
			String[] s = tmp.split("\t");
			String t;
			
			if (isResource(s[0]) && (t = getRelLabel(s[1])) != null 
					&& isResource(s[2])) {
				String sub = s[0];
				String obj = s[2];
				
				String pred = t;
				
				if (pred.length() > 50)
					continue;
				
				wr.append(sub + "\t" + pred + "\t" + obj + "\n");
			}
		}
		
		rd.close();
		wr.close();
	}

}
