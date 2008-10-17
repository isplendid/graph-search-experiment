package com.ibm.gse.text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import com.aliasi.tokenizer.EnglishStopListFilterTokenizer;
import com.aliasi.tokenizer.PorterStemmerFilterTokenizer;
import com.aliasi.tokenizer.Tokenizer;

public class LabelGenerator {
	
	public static List<String> generate(String ins) {
		Tokenizer tk = new EnglishStopListFilterTokenizer(new PorterStemmerFilterTokenizer(new NormalTokenizer(new StringReader(ins))));
		
		List<String> res = new ArrayList<String>();
		String temp;
		
		while ((temp = tk.nextToken()) != null)
			res.add(temp);
		
		return res;
	}
	
	public static void main(String[] args) throws Exception {
		BufferedReader rd = new BufferedReader(new FileReader(args[0]));
		BufferedWriter wr = new BufferedWriter(new FileWriter(args[1]));
		String temp;
		int cnt = 0;
		
		while ((temp = rd.readLine()) != null) {
//			System.out.println(temp);
			if ((++cnt) % 1000 == 0) System.out.println(cnt);
			String[] tmp = temp.split(">");
			
			if (tmp[2].charAt(1) == '"') {
				wr.append(tmp[0] + ">\t");
			
				for (String s : generate(tmp[2]))
					wr.append(s + " ");
				wr.append("\n");
			}
		}
		
		wr.close();
		rd.close();
			
	}
	
}
