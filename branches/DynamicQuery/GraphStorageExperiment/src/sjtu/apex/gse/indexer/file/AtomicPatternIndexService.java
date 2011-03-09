package sjtu.apex.gse.indexer.file;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import org.semanticweb.yars.nx.parser.NxParser;
import org.semanticweb.yars.nx.parser.ParseException;

import sjtu.apex.gse.config.Configuration;
import sjtu.apex.gse.config.FileConfig;
import sjtu.apex.gse.hash.HashFunction;
import sjtu.apex.gse.hash.ModHash;
import sjtu.apex.gse.index.file.util.FileIndexer;
import sjtu.apex.gse.indexer.IDManager;
import sjtu.apex.gse.indexer.SourceManager;
import sjtu.apex.gse.pattern.DirectPatternCodec;
import sjtu.apex.gse.pattern.HashingPatternCodec;
import sjtu.apex.gse.pattern.PatternCodec;
import sjtu.apex.gse.storage.map.ColumnNodeMap;
import sjtu.apex.gse.storage.map.HashLexicoColumnNodeMap;
import sjtu.apex.gse.storage.map.LexicoColumnNodeMap;
import sjtu.apex.gse.struct.QueryGraph;
import sjtu.apex.gse.struct.QueryGraphNode;

public class AtomicPatternIndexService {
	
	private int maxSize;
	private FileIndexer fidx;
	private IDManager idman;
	private SourceManager srcman;
	private PatternCodec codec;
	private ColumnNodeMap cnm;
	
	public AtomicPatternIndexService(Configuration config) {
		maxSize = config.getIntegerSetting("PatternLength", 3);
		fidx = new FileIndexer(maxSize, config);
		idman = new SleepyCatIDManager(config);
		srcman = new SleepyCatSourceManager(config);
		
		String pt = config.getStringSetting("PatternType", "Hash").toLowerCase();
		
		if (pt.equals("hash")) {
			HashFunction hash = new ModHash(config);
			codec = new HashingPatternCodec(hash);
			cnm = new HashLexicoColumnNodeMap(hash);
		} else if (pt.equals("direct")) {
			codec = new DirectPatternCodec(); 
			cnm = new LexicoColumnNodeMap();
		}
	}
	
	public void indexAtomicPatterns(String nqFile) {
		InputStream ins;
		NxParser nxp;
		org.semanticweb.yars.nx.Node[] stmt = null;
		
		try {
			ins = new FileInputStream(nqFile);
			if (nqFile.toLowerCase().endsWith(".gz")) ins = new GZIPInputStream(ins);
			nxp = new NxParser(ins);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		} catch (ParseException e) {
			e.printStackTrace();
			return;
		}
		
		while (nxp.hasNext()) {
			stmt = nxp.next();
			
			processStatement(stmt);
		}
	}
	
	private void processStatement(org.semanticweb.yars.nx.Node[] stmt) {
		Set<Integer> src = new HashSet<Integer>();
		Map<QueryGraphNode, Integer> ins; 
		QueryGraph graph;
		QueryGraphNode sub;
		QueryGraphNode obj;
		
		int subInt = idman.addGetID(stmt[0].toString());
		int objInt = idman.addGetID(stmt[2].toString());
		int predInt = idman.addGetID(stmt[1].toString());
		
		src.add(srcman.addGetID(stmt[3].toString()));
		
		//Add graph of size 2
		graph = new QueryGraph();
		ins = new HashMap<QueryGraphNode, Integer>();
		
		sub = graph.addNode();
		obj = graph.addNode();
		graph.addEdge(sub, obj, predInt);
		
		ins.put(sub, subInt);
		ins.put(obj, objInt);
		
		addPattern(graph, ins, src);
		
		//Add graph of size 1 A
		graph = new QueryGraph();
		ins = new HashMap<QueryGraphNode, Integer>();
		
		sub = graph.addNode(subInt);
		obj = graph.addNode();
		graph.addEdge(sub, obj, predInt);
		
		ins.put(sub, subInt);
		ins.put(obj, objInt);
		
		addPattern(graph, ins, src);
		
		//Add graph of size 1 B
		graph = new QueryGraph();
		ins = new HashMap<QueryGraphNode, Integer>();
		
		sub = graph.addNode();
		obj = graph.addNode(objInt);
		
		graph.addEdge(sub, obj, predInt);
		
		ins.put(sub, subInt);
		ins.put(obj, objInt);
		
		addPattern(graph, ins, src);
	}
	
	private void addPattern(QueryGraph graph, Map<QueryGraphNode, Integer> ins, Set<Integer> src) {
        Map<QueryGraphNode, Integer> map = cnm.getMap(graph);
        int[] tmp = new int[graph.nodeCount()];
        
        for (Entry<QueryGraphNode, Integer> e : ins.entrySet()) {
                tmp[map.get(e.getKey())] = e.getValue();
        }
        
        fidx.addEntry(codec.encodePattern(graph), graph.nodeCount(), tmp, src);
	}

	
	public void close() {
		fidx.close();
		idman.close();
	}
	
	public static void main(String[] args) {
		AtomicPatternIndexService idx = new AtomicPatternIndexService(new FileConfig(args[0]));
		
		idx.indexAtomicPatterns(args[1]);
		idx.close();
	}

}
