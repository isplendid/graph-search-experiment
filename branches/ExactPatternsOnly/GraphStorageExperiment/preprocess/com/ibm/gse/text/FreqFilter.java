package com.ibm.gse.text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * This class filters out desired amount of words
 * @author Tian Yuan
 * 
 */
public class FreqFilter {
	
	HashMap<String, Integer> set = new HashMap<String, Integer>();
	
	public void loadFreq(String filename, int lo, int hi) throws Exception {
		BufferedReader rd = new BufferedReader(new FileReader(filename));
		String temp;
		
		while ((temp = rd.readLine()) != null) {
			String word = temp.split("\t")[0];
			
			int freq = Integer.parseInt(temp.split("\t")[1]);
			
			if (word.length() < 3 || freq > hi || freq < lo) continue;
			set.put(word, freq);
		}
		
		rd.close();
	}
	
	public void filter(String infn, String outfn, int maxWordCnt) throws Exception {
		BufferedReader rd = new BufferedReader(new FileReader(infn));
		BufferedWriter wr = new BufferedWriter(new FileWriter(outfn));
		String temp;
		
		while ((temp = rd.readLine()) != null) 
			try {
				String[] strs = temp.split("\t");
				String[] words = strs[1].split(" ");
		
				
				List<WordInfo> strArr = new ArrayList<WordInfo>();
				
				for (int i = 0; i < words.length; i++) {
					String w = words[i].toLowerCase();
					if (set.containsKey(w))
						strArr.add(new WordInfo(w, set.get(w)));
				}
				
				if (strArr.size() != 0) {
					wr.append(strs[0] + "\t");
					Collections.sort(strArr);
					for (int i = 0; i < Math.min(maxWordCnt, strArr.size()); i++)
						wr.append(strArr.get(i).word + " "); 
					wr.append("\n");
				}
			}
			catch (Exception e) {
				wr.append(temp + "\n");
				System.out.println(temp);
				continue;
			}
		wr.close();
		rd.close();
	}
	
	/**
	 * 
	 * @param args The arguments are respectively, frequency statistic filename, keyword filename,
	 * output filename, lowest frequency allowed, highest frequency allowed and maximal word count allowed
	 * 
	 */
	public static void main(String[] args) throws Exception {
		FreqFilter ff = new FreqFilter();
		ff.loadFreq(args[0], Integer.parseInt(args[3]), Integer.parseInt(args[4]));
		ff.filter(args[1], args[2], Integer.parseInt(args[4]));
		
	}
	
	class WordInfo implements Comparable {
		String word;
		int freq;
		
		public WordInfo(String word, int freq) {
			this.word = word;
			this.freq = freq;
		}

		@Override
		public int compareTo(Object o) {
			if (o instanceof WordInfo)
				return ((WordInfo) o).freq - freq;
			else
				return 0;
		}
	}
}
