package sjtu.apex.gse.indexer.file;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import sjtu.apex.gse.config.Configuration;
import sjtu.apex.gse.config.FileConfig;
import sjtu.apex.gse.hash.HashFunction;
import sjtu.apex.gse.hash.ModHash;
import sjtu.apex.gse.index.file.util.FileIndexer;
import sjtu.apex.gse.indexer.IDManager;
import sjtu.apex.gse.operator.Plan;
import sjtu.apex.gse.operator.Scan;
import sjtu.apex.gse.operator.join.ArrayHashKey;
import sjtu.apex.gse.operator.join.Tuple;
import sjtu.apex.gse.pattern.DirectPatternCodec;
import sjtu.apex.gse.pattern.HashingPatternCodec;
import sjtu.apex.gse.pattern.PatternCodec;
import sjtu.apex.gse.query.FileQueryReader;
import sjtu.apex.gse.query.QueryReader;
import sjtu.apex.gse.storage.map.ColumnNodeMap;
import sjtu.apex.gse.storage.map.HashLexicoColumnNodeMap;
import sjtu.apex.gse.storage.map.LexicoColumnNodeMap;
import sjtu.apex.gse.struct.QueryGraphNode;
import sjtu.apex.gse.struct.QuerySchema;
import sjtu.apex.gse.system.QuerySystem;
import sjtu.apex.gse.system.QuerySystem.QuerySystemMode;

import com.sun.net.ssl.internal.ssl.Debug;

public class ComplexPatternIndexService {
	private int maxSize;
	private FileIndexer fidx;
	private IDManager idman;
	private PatternCodec codec;
	private ColumnNodeMap cnm;
	private QuerySystem qs;
	
	public ComplexPatternIndexService(Configuration source, Configuration target) {
		qs = new QuerySystem(source, QuerySystemMode.FILE_ONLY);
		maxSize = target.getIntegerSetting("PatternLength", 3);
		fidx = new FileIndexer(maxSize, target);
		idman = new SleepyCatIDManager(target);
		
		String pt = target.getStringSetting("PatternType", "Hash").toLowerCase();
		
		if (pt.equals("hash")) {
			HashFunction hash = new ModHash(target);
			codec = new HashingPatternCodec(hash);
			cnm = new HashLexicoColumnNodeMap(hash);
		} else if (pt.equals("direct")) {
			codec = new DirectPatternCodec(); 
			cnm = new LexicoColumnNodeMap();
		}
	}
	
	public void indexComplexPatterns(String patternFile) {
		QueryReader qr = new FileQueryReader(patternFile, idman);
		QuerySchema q;
		
		while ((q = qr.read()) != null) {
			Map<ArrayHashKey, Tuple> maps = new HashMap<ArrayHashKey, Tuple>();
			Plan p = qs.queryPlanner().plan(q);
			Debug.println("DEBUG" , p.toString());
			Scan s = p.open();
			String patternStr = codec.encodePattern(q.getQueryGraph());
			System.out.println("Indexing pattern " + patternStr);
			
			while (s.next()) {
				Map<QueryGraphNode, Integer> nodeId = cnm.getMap(q.getQueryGraph());
				int[] bindings = new int[q.getQueryGraph().nodeCount()];
				
				for (Entry<QueryGraphNode, Integer> e : nodeId.entrySet())
					bindings[e.getValue()] = s.getID(e.getKey());
				
				ArrayHashKey arrKey = new ArrayHashKey(bindings);
				Tuple t;
				
				if (!maps.containsKey(arrKey)) {
					t = new Tuple(bindings, new HashSet<Integer>());
					maps.put(arrKey, t);
				}
				else
					t = maps.get(arrKey);
				
				t.getSources().addAll(s.getSourceSet());
			}
			
			for (Tuple t : maps.values())
				fidx.addEntry(patternStr, q.getQueryGraph().nodeCount(), t.getBindings(), t.getSources());
		}
	}
	
	public void close() {
		fidx.close();
		idman.close();
		qs.close();
	}
	
	public static void main(String[] args) {
		ComplexPatternIndexService cpi = new ComplexPatternIndexService(new FileConfig(args[0]), new FileConfig(args[1]));
		
		cpi.indexComplexPatterns(args[2]);
		cpi.close();
	}
}
