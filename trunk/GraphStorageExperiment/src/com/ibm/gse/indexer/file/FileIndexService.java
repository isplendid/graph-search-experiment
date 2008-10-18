package com.ibm.gse.indexer.file;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import com.ibm.gse.index.file.FileIndexReader;
import com.ibm.gse.index.file.FileIndexWriter;
import com.ibm.gse.index.file.util.FileIndexSorter;
import com.ibm.gse.indexer.IDManager;
import com.ibm.gse.indexer.StorageReader;
import com.ibm.gse.pattern.PatternCodec;
import com.ibm.gse.pattern.PreorderPatternCodec;
import com.ibm.gse.query.MergeJoinPlan;
import com.ibm.gse.query.PatternPlan;
import com.ibm.gse.query.Plan;
import com.ibm.gse.query.Scan;
import com.ibm.gse.storage.file.FileRepositoryWriter;
import com.ibm.gse.storage.file.RID;
import com.ibm.gse.storage.file.RecordRange;
import com.ibm.gse.struct.QueryGraph;
import com.ibm.gse.struct.QueryGraphEdge;
import com.ibm.gse.struct.QueryGraphNode;
import com.ibm.gse.struct.QuerySchema;
import com.ibm.gse.system.GraphStorage;
import com.ibm.gse.temp.file.TempFileEntry;
import com.ibm.gse.temp.file.TempRepositoryFileWriter;
import com.ibm.gse.temp.file.util.TempRepositorySorter;
import com.ibm.gse.util.Heap;

public class FileIndexService {

	final static int coe = 10;

	IDManager idman;
	PatternCodec codec;

	public FileIndexService() {
		idman = new SleepyCatIDManager();
		codec = new PreorderPatternCodec();
	}

	public void indexNode(String filename) {
		TempRepositoryFileWriter wr = new TempRepositoryFileWriter(GraphStorage.config.getStringSetting("TempFolder", null) + "/raw" + 0, 1);
		InstanceKeywordRepository rs = new InstanceKeywordRepository(filename);
		int entryCounter = 0;
		int counter = 0;

		while(rs.next()) {
			if(counter % 1000 == 0)
				System.out.println("node " + counter);
			counter++;
			String uri = rs.getInstance();
			String[] terms = rs.getWords();
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

		System.out.println("ENTRY COUNT = " + entryCounter);
		TempRepositorySorter.sort(GraphStorage.config.getStringSetting("TempFolder", null) + "/raw" + 0, GraphStorage.config.getStringSetting("TempFolder", null) + "/sort" + 0, 1, GraphStorage.config.getStringSetting("TempFolder", null) + "/SortTmp");
		FileIndexer.index(GraphStorage.config.getStringSetting("TempFolder", null) + "/sort" + 0, GraphStorage.config.getStringSetting("DataFolder", null) + "/storage" + 0, GraphStorage.config.getStringSetting("DataFolder", null) + "/index" + 0, 1);
		//		TempRepositorySorter.deleteDirectory(new File())
	}

//	public List<String> getLabels(String uri) {
//		List<String> result = null;
//		ResultSet rs1 = dataReader.getSQLResultSet("SELECT text FROM texttable WHERE uri = '" + uri + "'");
//
//		try {
//			while (rs1.next()) {
//				String[] str = rs1.getString(1).split(" ");
//
//				if (str.length == 0) return null;
//				result = new ArrayList<String>();
//
//				for (int i = 0; i < str.length; i++)
//					result.add(str[i]);
//			}
//			rs1.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		dataReader.closeStat();
//
//		return result;
//	}

//	private void addEdge(TempRepositoryFileWriter wr, String pattern, int c1, int c2) {
//		int[] ins = new int[2];
//		ins[0] = c1;
//		ins[1] = c2;
//		wr.writeRecord(new TempFileEntry(pattern, ins));		
//	}
//
//	private String getAbbr(String uri) {
//		if (uri.lastIndexOf("/") > 0)
//			return uri.substring(uri.lastIndexOf("/") + 1);
//		else
//			return uri.substring(uri.indexOf("<") + 1);
//	}
//
//	public void indexEdge(String filename) {
//		TempRepositoryFileWriter wr = new TempRepositoryFileWriter(GraphStorage.config.getStringSetting("TempFolder", null) + "/raw" + 1, 2);
//		try {
//			BufferedReader rd = new BufferedReader(new FileReader(filename));
//			String temp;
//			int count = 0, all = 0;
//
//			while ((temp = rd.readLine()) != null) {
//				String[] str = temp.split(">");
//				String sub = str[0] + ">";
//				String pred = getAbbr(str[1].substring(1));
//				String obj = str[2].substring(1) + ">";
//
//
//				if (str[2].charAt(1) != '"') {
//
//					List<String> left = getLabels(sub);
//					List<String> right = getLabels(obj);
//
//
//
//					if (left == null || right == null) {
//						//   						System.out.println(sub + "\t" + obj);
//						if ((++ all) % 1000 == 0) System.out.println("NOT FOUND = " + all);
//						continue;
//					}
//
//					int ss = idman.getID(sub);
//					int os = idman.getID(obj);
//					if (ss == -1 || os == -1) continue;
//
//					if ((++count) % 1000 == 0) System.out.println(count);
//
//					for (String a : left)
//						for (String b : right) {
//							QueryGraph g = new QueryGraph();
//							QueryGraphNode ln = g.addNode(a);
//							QueryGraphNode rn = g.addNode(b);
//
//							g.addEdge(ln, rn, pred);
//							if (a.compareTo(b) < 0)
//								addEdge(wr, codec.encodePattern(g), ss, os);
//							else
//								addEdge(wr, codec.encodePattern(g), os, ss);
//						}
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		wr.close();
//		TempRepositorySorter.sort(GraphStorage.config.getStringSetting("TempFolder", null) + "/raw" + 1, GraphStorage.config.getStringSetting("TempFolder", null) + "/sort" + 1, 2, GraphStorage.config.getStringSetting("TempFolder", null) + "/SortTmp");
//		FileIndexer.index(GraphStorage.config.getStringSetting("TempFolder", null) + "/sort" + 1, GraphStorage.config.getStringSetting("DataFolder", null) + "/storage" + 1, GraphStorage.config.getStringSetting("DataFolder", null) + "/index" + 1, 2);
//	}
//
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
	 * 
	 */
//	public void close() {
//		dataReader.close();
//	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {		
		FileIndexService is = new FileIndexService();

		is.indexNode(args[0]);
		//				is.indexEdge("rawdata/triples.nt");
		//		is.indexTree();
//		is.close();
	}

//	class HeapContainer implements Comparable {
//		QueryGraph graph;
//		int insCnt;
//
//		HeapContainer(QueryGraph g, int c) {
//			this.graph = g;
//			this.insCnt = c;
//		}
//
//		@Override
//		public int compareTo(Object arg0) {
//			if (arg0 instanceof HeapContainer)
//				return insCnt - ((HeapContainer) arg0).insCnt;
//			else
//				return 0;
//		}
//
//	}
}
