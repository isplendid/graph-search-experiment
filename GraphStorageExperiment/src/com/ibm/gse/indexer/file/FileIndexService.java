package com.ibm.gse.indexer.file;

import java.io.File;
import java.util.Map;

import com.ibm.gse.index.file.FileIndexReader;
import com.ibm.gse.index.file.util.FileIndexMerger;
import com.ibm.gse.indexer.IDManager;
import com.ibm.gse.indexer.InstanceKeywordRepository;
import com.ibm.gse.indexer.LabelManager;
import com.ibm.gse.indexer.RelationRepository;
import com.ibm.gse.pattern.HashingPatternCodec;
import com.ibm.gse.pattern.ModHash;
import com.ibm.gse.pattern.PatternCodec;
import com.ibm.gse.storage.file.FileRepositoryReader;
import com.ibm.gse.storage.file.RecordRange;
import com.ibm.gse.struct.QueryGraph;
import com.ibm.gse.struct.QueryGraphEdge;
import com.ibm.gse.struct.QueryGraphNode;
import com.ibm.gse.system.GraphStorage;
import com.ibm.gse.temp.file.TempFileEntry;
import com.ibm.gse.temp.file.TempRepositoryFileWriter;
import com.ibm.gse.temp.file.util.TempRepositorySorter;
import com.ibm.gse.util.Heap;

public class FileIndexService {

	final static int coe = 10;

	PatternCodec codec;

	public FileIndexService() {
		codec = new HashingPatternCodec(new ModHash());
	}
	
	public void loadKeyword(String filename) {
		LabelManager lm = new SleepyCatLabelManager();
		InstanceKeywordRepository ikr = new InstanceKeywordRepository(filename);
		lm.load(ikr);
		ikr.close();
	}

	/**
	 * Index elementary patterns containing only one node
	 * @param filename The pattern file
	 */
	public void indexNode(String filename) {
		IDManager idman = new SleepyCatIDManager(); 
		TempRepositoryFileWriter wr = new TempRepositoryFileWriter(GraphStorage.config.getStringSetting("TempFolder", null) + "/raw" + 0, 1);
		InstanceKeywordRepository rs = new InstanceKeywordRepository(filename);
		int entryCounter = 0;
		int counter = 0;

		while(rs.next()) {
			if(counter % 1000 == 0)
				System.out.println("node " + counter);
			counter++;
			String uri = rs.getInstance();
			String[] terms = rs.getWordList();
			int id = idman.getID(uri);
			if(id == -1)
				id = idman.addURI(uri);
			for(int i = 0; i < terms.length; i++) {
				entryCounter ++;
				QueryGraph g = new QueryGraph();
				int[] ins = new int[1];
				g.addNode(terms[i]);
				ins[0] = id;
				TempFileEntry tfe = new TempFileEntry(codec.encodePattern(g), ins);
				wr.writeRecord(tfe);
			}
		}
		rs.close();
		wr.close();

		idman.close();

		System.out.println("ENTRY COUNT = " + entryCounter);
		TempRepositorySorter.sort(GraphStorage.config.getStringSetting("TempFolder", null) + "/raw" + 0, GraphStorage.config.getStringSetting("TempFolder", null) + "/sort" + 0, 1, GraphStorage.config.getStringSetting("TempFolder", null) + "/SortTmp");
		FileIndexer.index(GraphStorage.config.getStringSetting("TempFolder", null) + "/sort" + 0, GraphStorage.config.getStringSetting("DataFolder", null) + "/storage" + 0, GraphStorage.config.getStringSetting("DataFolder", null) + "/index" + 0, 1);
		deleteFile(GraphStorage.config.getStringSetting("TempFolder", null) + "/raw" + 0);
		deleteFile(GraphStorage.config.getStringSetting("TempFolder", null) + "/sort" + 0);
		//		TempRepositorySorter.deleteDirectory(new File())
	}

	private void addEdge(TempRepositoryFileWriter wr, String pattern, int c1, int c2) {
		int[] ins = new int[2];
		ins[0] = c1;
		ins[1] = c2;
		wr.writeRecord(new TempFileEntry(pattern, ins));		
	}

	private String getAbbr(String uri) {
		if (uri.lastIndexOf("/") > 0)
			return uri.substring(uri.lastIndexOf("/") + 1, uri.length() - 1);
		else
			return uri;
	}

	private void deleteFile(String filename) {
		File f = new File(filename);
		f.delete();
	}
	
	private void renameFile(String src, String dest) {
		File f = new File(src);
		f.renameTo(new File(dest));
	}
	
	/**
	 * Index elementary patterns containing one edge
	 * @param filename
	 * @param maxEntryCnt
	 * @param maxThreadCnt
	 */
	public void indexEdge(String filename, int maxEntryCnt, int maxThreadCnt) {
		indexEdge(filename, maxEntryCnt, maxThreadCnt, null);
	}

	/**
	 * Index elementary pattern containing one edge from the given triple
	 * @param filename
	 * @param maxEntryCnt
	 * @param maxThreadCnt
	 * @param startTriple
	 */
	public void indexEdge(String filename, int maxEntryCnt, int maxThreadCnt, String[] startTriple) {
		String tempFolder = GraphStorage.config.getStringSetting("TempFolder", null);
		String dataFolder = GraphStorage.config.getStringSetting("DataFolder", null);
		int count = 0, entryCnt = 0, threadCnt;
		boolean skip = false;

		IDManager idman = new SleepyCatIDManager();
		RelationRepository rd = new RelationRepository(filename);
		
		if (startTriple == null)
			threadCnt = 0;
		else {
			skip = true;
			threadCnt = 1;
		}
		

		TempRepositoryFileWriter wr = new TempRepositoryFileWriter(tempFolder + "/raw" + 1, 2);

		while (rd.next()) {
			String sub = rd.getSubject();
			String pred = getAbbr(rd.getPredicate());
			String obj = rd.getObject();
			
//			if (pred.length() > 50) continue;

			int ss = idman.getID(sub);
			int os = idman.getID(obj);
			
			if (ss == -1 || os == -1) continue;

			if ((++count) % 1000 == 0) System.out.println(count);
			
			if (skip) {
				if (sub.equals(startTriple[0]) && pred.equals(startTriple[1]) && obj.equals(startTriple[2]))
					skip = false;
				else
					continue;
			}

			entryCnt ++;
			QueryGraph g = new QueryGraph();
			QueryGraphNode ln = g.addNode();
			QueryGraphNode rn = g.addNode();

			g.addEdge(ln, rn, pred);

			addEdge(wr, codec.encodePattern(g), ss, os);
			
			if (entryCnt > maxEntryCnt) {
				wr.close();
				TempRepositorySorter.sort(tempFolder + "/raw" + 1, tempFolder + "/sort" + 1, 2, tempFolder + "/SortTmp");
				deleteFile(tempFolder + "/raw" + 1);
				FileIndexer.index(tempFolder + "/sort" + 1, dataFolder + "/storage" + 1 + ".t" + threadCnt, dataFolder + "/index" + 1 + ".t" + threadCnt, 2);
				deleteFile(tempFolder + "/sort" + 1);
				threadCnt ++;
				
				if (threadCnt >= maxThreadCnt) {
					System.out.println("Merging files ... ");
					FileIndexMerger.merge(dataFolder, dataFolder, 2, threadCnt);
					for (int i = 0; i < threadCnt; i++) {
						deleteFile(dataFolder + "/index1.t" + i);
						deleteFile(dataFolder + "/storage1.t" + i);
					}
					renameFile(dataFolder + "/index1", dataFolder + "/index1.t0");
					renameFile(dataFolder + "/storage1", dataFolder + "/storage1.t0");
					threadCnt = 1;
				}
				
				
				wr = new TempRepositoryFileWriter(tempFolder + "/raw" + 1, 2);
				entryCnt = 0;
			}
		}
		wr.close();
		
		TempRepositorySorter.sort(tempFolder + "/raw" + 1, tempFolder + "/sort" + 1, 2, tempFolder + "/SortTmp");
		deleteFile(tempFolder + "/raw" + 1);
		FileIndexer.index(tempFolder + "/sort" + 1, dataFolder + "/storage" + 1 + ".t" + threadCnt, dataFolder + "/index" + 1 + ".t" + threadCnt, 2);
		deleteFile(tempFolder + "/sort" + 1);

		threadCnt ++;
		FileIndexMerger.merge(dataFolder, dataFolder, 2, threadCnt);
		for (int i = 0; i < threadCnt; i++) {
			deleteFile(dataFolder + "/index1.t" + i);
			deleteFile(dataFolder + "/storage1.t" + i);
		}
			
	}
	
	public void indexComplex(int minJoinInsCnt, int mergeThreshold, int maxThreadCnt, int totalThreshold) {
		String dataFolder = GraphStorage.config.getStringSetting("DataFolder", null);
		String tempFolder = GraphStorage.config.getStringSetting("TempFolder", null);
		String idx2 = dataFolder + "/index" + 1;
		Heap h = new Heap();
		IDManager idman = new SleepyCatIDManager();
		LabelManager labman = new SleepyCatLabelManager();
		
		FileIndexReader fir = new FileIndexReader(idx2, 2);
		TempRepositoryFileWriter tw = new TempRepositoryFileWriter(tempFolder + "/raw" + 1, 2);
		int entryCnt = 0, totalCnt = 0, threadCnt = 0;
		
		while (fir.next()) {
			int insCnt = fir.getInstanceCount();
			
			if (insCnt > minJoinInsCnt) {
				String ps = fir.getPatternString();
			
				h.insert(new HeapContainer(ps, codec.decodePattern(ps), insCnt, fir.getRange()));
			}
		}
		
		fir.close();
		
		HeapContainer hc;
		
		while ((hc = (HeapContainer)h.remove()) != null && totalCnt < totalThreshold) {
			QueryGraph g = hc.graph;
			String ps = hc.patternStr;
			
			if (g.edgeCount() == 1) {
				QueryGraphEdge edge = g.getEdge(0);
				System.out.println(edge.getLabel());
//				FileRepository fr = new FileRepository(ps, 2);
				FileRepositoryReader fr = new FileRepositoryReader(dataFolder + "/storage1", 2, hc.recRange);
				Map<QueryGraphNode, Integer> map = GraphStorage.columnNodeMap.getMap(g);
				int ent[];
				int rdCnt = 0;
				
				while ((ent = fr.readEntry()) != null) {
					if ((++rdCnt) % 5000 == 0) System.out.println(rdCnt + "," + totalCnt);
					
					int ss = ent[map.get(edge.getNodeFrom())];
					int os = ent[map.get(edge.getNodeTo())];
					String sub = idman.getURI(ss);
					String pred = edge.getLabel();
					String obj = idman.getURI(os);
					
					String[] left = labman.getLabel(sub);
					String[] right = labman.getLabel(obj);


					if (left == null || right == null) {
						continue;
					}
					
					for (String l : left) {
						String r = "*";
						
						entryCnt ++;
						totalCnt ++;
						QueryGraph ng = new QueryGraph();
							
						QueryGraphNode na = ng.addNode(l);
						QueryGraphNode nb = ng.addNode();
						ng.addEdge(na, nb, pred);
						
						if (l.compareTo(r) < 0)
							addEdge(tw, codec.encodePattern(ng), ss, os);
						else
							addEdge(tw, codec.encodePattern(ng), os, ss);
					}
					
					for (String r : right) {
						String l = "*";
						
						entryCnt ++;
						totalCnt ++;
						QueryGraph ng = new QueryGraph();
						
						QueryGraphNode na = ng.addNode();
						QueryGraphNode nb = ng.addNode(r);
						ng.addEdge(na, nb, pred);
					
						if (l.compareTo(r) < 0)
							addEdge(tw, codec.encodePattern(ng), ss, os);
						else
							addEdge(tw, codec.encodePattern(ng), os, ss);
					}
					
					if (entryCnt > mergeThreshold) {
	                    tw.close();
	                    TempRepositorySorter.sort(tempFolder + "/raw" + 1, tempFolder + "/sort" + 1, 2, tempFolder + "/SortTmp");
	                    deleteFile(tempFolder + "/raw" + 1);
	                    FileIndexer.index(tempFolder + "/sort" + 1, dataFolder + "/storage" + 1 + ".t" + threadCnt, dataFolder + "/index" + 1 + ".t" + threadCnt, 2);
	                    deleteFile(tempFolder + "/sort" + 1);
	                    threadCnt ++;
	                    
	                    if (threadCnt >= maxThreadCnt) {
	                            System.out.println("Merging files ... ");
	                            FileIndexMerger.merge(dataFolder, dataFolder, "index1.t", "storage1.t", 2, threadCnt);
	                            for (int i = 0; i < threadCnt; i++) {
	                                    deleteFile(dataFolder + "/index1.t" + i);
	                                    deleteFile(dataFolder + "/storage1.t" + i);
	                            }
	                            renameFile(dataFolder + "/index1.t", dataFolder + "/index1.t0");
	                            renameFile(dataFolder + "/storage1.t", dataFolder + "/storage1.t0");
	                            threadCnt = 1;
	                    }
	                    
	                    
	                    tw = new TempRepositoryFileWriter(tempFolder + "/raw" + 1, 2);
	                    entryCnt = 0;
					}
					
				}
				
				fr.close();

				
			} else {
				System.err.println("ERROR ON PATTERN " + ps);
			}
		}
		
		tw.close();
		
		TempRepositorySorter.sort(tempFolder + "/raw" + 1, tempFolder + "/sort" + 1, 2, tempFolder + "/SortTmp");
        deleteFile(tempFolder + "/raw" + 1);
        FileIndexer.index(tempFolder + "/sort" + 1, dataFolder + "/storage" + 1 + ".t" + threadCnt, dataFolder + "/index" + 1 + ".t" + threadCnt, 2);
        deleteFile(tempFolder + "/sort" + 1);
        
        threadCnt++;
        
        FileIndexMerger.merge(dataFolder, dataFolder, "index1.t", "storage1.t", 2, threadCnt);
        for (int i = 0; i < threadCnt; i++) {
                deleteFile(dataFolder + "/index1.t" + i);
                deleteFile(dataFolder + "/storage1.t" + i);
        }
        renameFile(dataFolder + "/index1.t", dataFolder + "/index1.t0");
        renameFile(dataFolder + "/storage1.t", dataFolder + "/storage1.t0");
        renameFile(dataFolder + "/index1", dataFolder + "/index1.t1");
        renameFile(dataFolder + "/storage1", dataFolder + "/storage1.t1");
        
//        FileIndexMerger.merge(dataFolder, dataFolder, 2, 2);
        FileIndexMerger.merge(dataFolder, dataFolder, "index1", "storage1", 2, 2);
        
        for (int i = 0; i < 2; i++) {
            deleteFile(dataFolder + "/index1.t" + i);
            deleteFile(dataFolder + "/storage1.t" + i);
        }
        
	}

	//	/**
	//	 * 
	//	 */
	//	public void indexTree() {
	//		HashMap<String, Heap> arr = new HashMap<String, Heap>();
	//		FileIndexReader fir = new FileIndexReader(GraphStorage.config.getStringSetting("DataFolder", null) + "/index" + 1, 2);
	//		FileIndexWriter fiw = new FileIndexWriter(GraphStorage.config.getStringSetting("TempFolder", null) + "/indexTmp" + 2, 3);
	//		FileRepositoryWriter frw = new FileRepositoryWriter(GraphStorage.config.getStringSetting("DataFolder", null) + "/storage" + 2, 3); 
	//
	//		//Load initial patterns
	//		while (fir.next()) {
	//			int insCnt = fir.getInstanceCount();
	//
	//			if (insCnt > 10) {
	//				String ps = fir.getPatternString();
	//
	//				QueryGraph g = GraphStorage.patternMan.getCodec().decodePattern(ps);
	//				for (int i = 0; i < g.nodeCount(); i++) {
	//					String label = g.getNode(i).getLabel();
	//					Heap h = arr.get(label);
	//					if (h == null) {
	//						h = new Heap();
	//						arr.put(label, h);
	//					}
	//					h.insert(new HeapContainer(g, insCnt));
	//				}
	//			}
	//		}
	//
	//		fir.close();
	//
	//		//		return;
	//
	//		int progress = 0;
	//
	//		//Find the most promising patterns
	//		for (Entry<String, Heap> e : arr.entrySet()) {
	//			String label = e.getKey();
	//			Heap h = e.getValue();
	//			List<QueryGraph> hcl = new ArrayList<QueryGraph>();
	//
	//			progress++;
	//			System.out.println(progress);
	//
	//			HeapContainer hc;
	//			int cnt = 0;
	//
	//			while (cnt < 10 && ((hc = (HeapContainer)h.remove()) != null)) {
	//				boolean n0e = hc.graph.getNode(0).getLabel().equals(label);
	//				boolean n1e = hc.graph.getNode(1).getLabel().equals(label);
	//				if (n0e && n1e) continue;
	//
	//				hcl.add(hc.graph);
	//				cnt++;
	//			}
	//
	//			for (int i = 0; i < hcl.size(); i++)
	//				for (int j = i + 1; j < hcl.size(); j++)
	//				{
	//					QueryGraphEdge e0 = hcl.get(i).getEdge(0);
	//					QueryGraphEdge e1 = hcl.get(j).getEdge(0);
	//					QueryGraph g = new QueryGraph();
	//					QueryGraphNode jn = g.addNode(label), n0, n1;
	//
	//					if (e0.getNodeFrom().getLabel().equals(label)) {
	//						n0 = g.addNode(e0.getNodeTo().getLabel());
	//						g.addEdge(jn, n0, e0.getLabel());
	//					} else {
	//						n0 = g.addNode(e0.getNodeFrom().getLabel());
	//						g.addEdge(n0, jn, e0.getLabel());
	//					}
	//
	//					if (e1.getNodeFrom().getLabel().equals(label)) {
	//						n1 = g.addNode(e1.getNodeTo().getLabel());
	//						g.addEdge(jn, n1, e1.getLabel());
	//					} else {
	//						n1 = g.addNode(e1.getNodeFrom().getLabel());
	//						g.addEdge(n1, jn, e1.getLabel());
	//					}
	//
	//					List<QueryGraphNode> qsl0, qsl1, qslr;
	//					qsl0 = new ArrayList<QueryGraphNode>();
	//					qsl0.add(jn);
	//					qsl0.add(n0);
	//
	//					qsl1 = new ArrayList<QueryGraphNode>();
	//					qsl1.add(jn);
	//					qsl1.add(n1);
	//
	//					qslr = new ArrayList<QueryGraphNode>();
	//					qslr.add(jn);
	//					qslr.add(n0);
	//					qslr.add(n1);
	//
	//					QueryGraph g0 = g.getNodeInducedSubgraph(new HashSet<QueryGraphNode>(qsl0));
	//					QuerySchema qs0 = new QuerySchema(g0, qsl0); 
	//					Plan pl = new PatternPlan(qs0);
	//
	//					QueryGraph g1 = g.getNodeInducedSubgraph(new HashSet<QueryGraphNode>(qsl1));
	//					QuerySchema qs1 = new QuerySchema(g1, qsl1); 
	//					Plan pr = new PatternPlan(qs1);
	//
	//					QuerySchema qsr = new QuerySchema(g, qslr);
	//					List<QueryGraphNode> jnl = new ArrayList<QueryGraphNode>();
	//					jnl.add(jn);
	//					Plan pm = new MergeJoinPlan(pl, pr, jnl, qsr);
	//
	//					RID first = frw.getRID();
	//					RID last = null;
	//					List<int[]> results = new ArrayList<int[]>();
	//
	//					int totalSumSize = GraphStorage.patternMan.getPatternInstanceCount(g0) + GraphStorage.patternMan.getPatternInstanceCount(g1);
	//
	//					//					System.out.println("BEGIN FETCH ...");
	//					Scan s = pm.open();
	//
	//					while (results.size() < coe * totalSumSize && s.next()) {
	//						results.add(getInstanceList(s, qsr));
	//					}
	//					s.close();
	//					//					System.out.println("END FETCH ...");
	//
	//					if (results.size() > 0 && results.size() < coe * totalSumSize) {
	//						System.out.println(GraphStorage.patternMan.getCodec().encodePattern(g));
	//
	//						for (int k = 0; k < results.size(); k++) {
	//							last = frw.getRID();
	//							frw.writeEntry(results.get(k));
	//						}
	//						fiw.writeEntry(GraphStorage.patternMan.getCodec().encodePattern(g), new RecordRange(first, last));
	//					}
	//
	//
	//					//						
	//					//						fiw.close();
	//					//						frw.close();
	//					//						return;
	//				}
	//		}
	//
	//		fiw.close();
	//		frw.close();
	//		FileIndexSorter.sort(GraphStorage.config.getStringSetting("TempFolder", null) + "/indexTmp" + 2, GraphStorage.config.getStringSetting("DataFolder", null) + "/index" + 2, 3, GraphStorage.config.getStringSetting("TempFolder", null) + "/sortTmp");
	//	}
	//
	//	private int[] getInstanceList(Scan s, QuerySchema qs) {
	//		int size = qs.getSelectedNodeCount();
	//		int[] result = new int[size];
	//		String[] labels = new String[size];
	//
	//		for (int i = 0; i < size; i++) {
	//			result[i] = s.getID(qs.getSelectedNode(i));
	//			labels[i] = qs.getSelectedNode(i).getLabel();
	//		}
	//
	//		for (int i = 0; i < size - 1; i++) {
	//			int p = i;
	//			for (int j = i + 1; j < size; j++)
	//				if (labels[j].compareTo(labels[p]) < 0) p = j;
	//
	//			String ts = labels[p];
	//			int ti = result[p];
	//			labels[p] = labels[i];
	//			result[p] = result[i];
	//			labels[i] = ts;
	//			result[i] = ti;
	//		}
	//
	//		return result;
	//	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {		
		FileIndexService is = new FileIndexService();
//
//		is.indexNode(args[0]);
//		is.indexEdge(args[1], 5000000, 3);
//		is.loadKeyword(args[0]);
		is.indexComplex(1, 10000000, 3, 30000000);

	}

	class HeapContainer implements Comparable {
		String patternStr;
		QueryGraph graph;
		int insCnt;
		RecordRange recRange;

		HeapContainer(String patternStr, QueryGraph g, int c, RecordRange recRange) {
			this.patternStr = patternStr;
			this.graph = g;
			this.insCnt = c;
			this.recRange = recRange;
		}

		@Override
		public int compareTo(Object arg0) {
			if (arg0 instanceof HeapContainer)
				return insCnt - ((HeapContainer) arg0).insCnt;
			else
				return 0;
		}

	}
}
