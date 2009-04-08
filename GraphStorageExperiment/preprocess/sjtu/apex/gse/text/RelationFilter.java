package sjtu.apex.gse.text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class RelationFilter {
	
	static final String res = "<http://dbpedia.org/resource/";
	static final String prp = "<http://dbpedia.org/property/";

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		BufferedReader rd = new BufferedReader(new FileReader(args[0]));
		BufferedWriter wr = new BufferedWriter(new FileWriter(args[1]));
		String tmp;
		
		int plen = prp.length();
		
		while ((tmp = rd.readLine()) != null) {
			String[] s = tmp.split("\t");
			
			if (s[0].startsWith(res) && s[2].startsWith(res) 
					&& s[1].startsWith(prp)) {
				String sub = s[0];
				String obj = s[2];
				
				String pred = s[1].substring(plen, s[1].length() - 1).toLowerCase();
				
				if (pred.length() > 50)
					continue;
				
				wr.append(sub + "\t" + pred + "\t" + obj + "\n");
			}
		}
	}

}
