package com.ibm.gse.text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashSet;

public class FreqFilter {
	
	HashSet<String> set = new HashSet<String>();
	
	public void loadFreq(String filename, int lo, int hi) throws Exception {
		BufferedReader rd = new BufferedReader(new FileReader(filename));
		String temp;
		
		while ((temp = rd.readLine()) != null) {
			String word = temp.split("\t")[0];
			
			int freq = Integer.parseInt(temp.split("\t")[1]);
			
			if (word.length() < 3 || freq > hi || freq < lo) continue;
			set.add(word);
		}
		
		rd.close();
	}
	
	public void filter(String infn, String outfn) throws Exception {
		BufferedReader rd = new BufferedReader(new FileReader(infn));
		BufferedWriter wr = new BufferedWriter(new FileWriter(outfn));
		String temp;
		
		while ((temp = rd.readLine()) != null) 
			try {
				String[] strs = temp.split("\t");
				String[] words = strs[1].split(" ");
		
				wr.append(strs[0] + "\t");
				for (int i = 0; i < words.length; i++) {
					String w = words[i].toLowerCase();
					if (set.contains(w))
						wr.append(w + " "); 
				}
				
				wr.append("\n");
			}
			catch (Exception e) {
				wr.append(temp + "\n");
				System.out.println(temp);
				continue;
			}
		wr.close();
		rd.close();
	}
	
	public static void main(String[] args) throws Exception {
		FreqFilter ff = new FreqFilter();
		ff.loadFreq(args[0], Integer.parseInt(args[3]), Integer.parseInt(args[4]));
		ff.filter(args[1], args[2]);
		
	}
}
