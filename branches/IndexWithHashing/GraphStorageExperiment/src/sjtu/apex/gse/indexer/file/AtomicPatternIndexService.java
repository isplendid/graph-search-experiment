package sjtu.apex.gse.indexer.file;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import sjtu.apex.gse.config.Configuration;
import sjtu.apex.gse.config.FileConfig;
import sjtu.apex.gse.hash.HashFunction;
import sjtu.apex.gse.hash.ModHash;
import sjtu.apex.gse.index.file.util.FileIndexer;
import sjtu.apex.gse.indexer.IDManager;
import sjtu.apex.gse.indexer.InstanceKeywordRepository;
import sjtu.apex.gse.indexer.LabelManager;
import sjtu.apex.gse.indexer.RelationRepository;
import sjtu.apex.gse.pattern.HashingPatternCodec;
import sjtu.apex.gse.pattern.PatternCodec;
import sjtu.apex.gse.storage.map.ColumnNodeMap;
import sjtu.apex.gse.storage.map.HashLexicoColumnNodeMap;
import sjtu.apex.gse.struct.QueryGraph;
import sjtu.apex.gse.struct.QueryGraphNode;

public class AtomicPatternIndexService {
	
	private int maxSize;
	private FileIndexer fidx;
	private IDManager idman;
	private LabelManager lblman;
	private PatternCodec codec;
	private ColumnNodeMap cnm;
//	private Configuration config;
	private HashFunction hash;
	
	public AtomicPatternIndexService(Configuration config) {
		maxSize = config.getIntegerSetting("PatternLength", 3);
		fidx = new FileIndexer(maxSize, config);
		idman = new SleepyCatIDManager(config);
		lblman = new SleepyCatLabelManager(config);
		hash = new ModHash(config);
		codec = new HashingPatternCodec(hash);
		cnm = new HashLexicoColumnNodeMap(hash);
//		this.config = config;
	}
	
	private void indexLabel(String filename) {
		InstanceKeywordRepository rd = new InstanceKeywordRepository(filename);
		int cnt = 0;
		
		while(rd.next()) {
			cnt++;
			if(cnt % 1000 == 0)
				System.out.println("node " + cnt);
			
			String uri = rd.getInstance();
			String[] terms = rd.getWordList();
			
			int id = idman.getID(uri);
			if(id == -1)
				id = idman.addURI(uri);
			
			for(int i = 0; i < terms.length; i++) {
				int[] ins = new int[1];
				ins[0] = id;
				fidx.addEntry(terms[i], 1, ins);
			}
		}
		rd.close();
	}
	
	private void indexEdge(String filename) {
		RelationRepository rd = new RelationRepository(filename);
		int count = 0;
		
		while (rd.next()) {
			String sub = rd.getSubject();
			String pred = rd.getPredicate();
			String obj = rd.getObject();
			

			int ss = idman.getID(sub);
			int os = idman.getID(obj);
			
			if (ss == -1 || os == -1) continue;

			if ((++count) % 1000 == 0) System.out.println(count);

			QueryGraph g = new QueryGraph();
			QueryGraphNode snode = g.addNode();
			QueryGraphNode onode = g.addNode();

			g.addEdge(snode, onode, pred);
			
			Map<QueryGraphNode, Integer> map = new HashMap<QueryGraphNode, Integer>();
			map.put(snode, ss);
			map.put(onode, os);

			addPattern(g, map);
			
		}
		rd.close();
	}
	
	private void loadKeyword(String filename) {
        InstanceKeywordRepository rd = new InstanceKeywordRepository(filename);
        lblman.load(rd);
        rd.close();
	}
	
	public void indexAtomicPatterns(String labelFile, String relationFile) {
		indexLabel(labelFile);
		loadKeyword(labelFile);
		indexEdge(relationFile);
	}

	
	public void close() {
		fidx.close();
		idman.close();
		lblman.close();
	}
	
	private void addPattern(QueryGraph graph, Map<QueryGraphNode, Integer> ins) {
		Map<QueryGraphNode, Integer> map = cnm.getMap(graph);
		int[] tmp = new int[graph.nodeCount()];
		
		for (Entry<QueryGraphNode, Integer> e : map.entrySet()) {
			tmp[map.get(e.getKey())] = e.getValue();
		}
		
		fidx.addEntry(codec.encodePattern(graph), graph.nodeCount(), tmp);
	}
	
	public static void main(String[] args) {
		AtomicPatternIndexService idx = new AtomicPatternIndexService(new FileConfig(args[0]));
		
		idx.indexAtomicPatterns(args[1], args[2]);
		idx.close();
	}

}
