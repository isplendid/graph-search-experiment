package sjtu.apex.gse.indexer.file;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import sjtu.apex.gse.index.file.util.FileIndexer;
import sjtu.apex.gse.indexer.IDManager;
import sjtu.apex.gse.indexer.InstanceKeywordRepository;
import sjtu.apex.gse.indexer.LabelManager;
import sjtu.apex.gse.indexer.RelationRepository;
import sjtu.apex.gse.pattern.PatternCodec;
import sjtu.apex.gse.struct.QueryGraph;
import sjtu.apex.gse.struct.QueryGraphNode;
import sjtu.apex.gse.system.GraphStorage;

public class IterativeIndexService {
	
	final static int maxSize = 3;
	FileIndexer fidx;
	IDManager idman;
	LabelManager lblman;
	PatternCodec codec;
	
	public IterativeIndexService() {
		fidx = new FileIndexer(maxSize);
		idman = new SleepyCatIDManager();
		lblman = new SleepyCatLabelManager();
		codec = GraphStorage.patternMan.getCodec();
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
	
	public void indexComplexPatterns() {
		
	}

	
	public void close() {
		fidx.close();
		idman.close();
		lblman.close();
	}
	
	private void addPattern(QueryGraph graph, Map<QueryGraphNode, Integer> ins) {
		Map<QueryGraphNode, Integer> map = GraphStorage.columnNodeMap.getMap(graph);
		int[] tmp = new int[graph.nodeCount()];
		
		for (Entry<QueryGraphNode, Integer> e : map.entrySet()) {
			tmp[map.get(e.getKey())] = e.getValue();
		}
		
		fidx.addEntry(codec.encodePattern(graph), graph.nodeCount(), tmp);
	}
	
	public static void main(String[] args) {
		IterativeIndexService idx = new IterativeIndexService();
		
		idx.indexAtomicPatterns(args[0], args[1]);
		idx.close();
	}
}
