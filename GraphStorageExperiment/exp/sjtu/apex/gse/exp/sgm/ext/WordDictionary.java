package sjtu.apex.gse.exp.sgm.ext;

import java.util.HashMap;
import java.util.Map;

public class WordDictionary {
	
	Map<String, Integer> wd2id;
	Map<Integer, String> id2wd;
	
	public WordDictionary() {
		wd2id = new HashMap<String, Integer>();
		id2wd = new HashMap<Integer, String>();
	}
	
	public int getID(String word) {
		Integer t;
		
		if ((t = wd2id.get(word)) == null) {
			t = id2wd.size();
			wd2id.put(word, t);
			id2wd.put(t, word);
		}
		
		return t;
	}
	
	public String getWord(int id) {
		return id2wd.get(id);
	}
}
