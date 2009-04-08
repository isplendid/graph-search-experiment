package sjtu.apex.gse.text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class FreqGenerator {
	
	int total = 0;
	HashMap<String, Integer> wordCnt = new HashMap<String, Integer>();
	
	public static boolean isLegal(String s) {
		if (s.length() == 0) return false;
		for (int i = 0; i < s.length(); i++)
			if (s.charAt(i) < 'a' || s.charAt(i) > 'z') return false;
		return true;
	}
	
	public void analyzeFreq(String[] words) {
		for (int i = 0; i < words.length; i++) {
			String w = words[i].toLowerCase();
			if (isLegal(w)) {
				total ++;
				Integer ti;
				if ((ti = wordCnt.get(w)) != null)
					wordCnt.put(w, ti + 1);
				else
					wordCnt.put(w, 1);
			}
		}
	}
	
	public void outputSortedFreq(Writer wr) throws IOException {
		List<Entry<String, Integer>> entries = new ArrayList<Entry<String, Integer>>();
		
		for (Entry<String, Integer> e : wordCnt.entrySet())
			entries.add(e);
		java.util.Collections.sort(entries, new EntryCompare());
		
		for (Entry<String, Integer> e : entries)
			wr.append(e.getKey()+ "\t" + e.getValue() + "\n");
		
		wr.append(total + "\n");
	}
	
	class EntryCompare implements Comparator {

		public int compare(Object arg0, Object arg1) {
			Entry<String, Integer> a = (Entry<String, Integer>)arg0;
			Entry<String, Integer> b = (Entry<String, Integer>)arg1;
			return a.getValue().compareTo(b.getValue());
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		FreqGenerator fg = new FreqGenerator();
		BufferedReader rd = new BufferedReader(new FileReader(args[0]));
		BufferedWriter wr = new BufferedWriter(new FileWriter(args[1]));
		
		String temp;
		
		while ((temp = rd.readLine()) != null)
			try {
				fg.analyzeFreq(temp.split("\t")[1].split(" "));
			} catch (Exception e) {
				System.out.println(temp);
			}
		
		fg.outputSortedFreq(wr);
		
		wr.close();
		rd.close();
	}
}
