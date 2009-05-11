package sjtu.apex.gse.experiment.querygen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sjtu.apex.gse.config.Configuration;
import sjtu.apex.gse.config.FileConfig;
import sjtu.apex.gse.experiment.edge.Edge;
import sjtu.apex.gse.experiment.edge.EdgeInfo;
import sjtu.apex.gse.indexer.IDManager;
import sjtu.apex.gse.indexer.LabelManager;
import sjtu.apex.gse.indexer.file.SleepyCatIDManager;
import sjtu.apex.gse.indexer.file.SleepyCatLabelManager;
import sjtu.apex.gse.operator.Scan;
import sjtu.apex.gse.query.FileQueryReader;
import sjtu.apex.gse.query.FileQueryWriter;
import sjtu.apex.gse.query.QueryReader;
import sjtu.apex.gse.query.QueryWriter;
import sjtu.apex.gse.struct.GraphUtility;
import sjtu.apex.gse.struct.QueryGraph;
import sjtu.apex.gse.struct.QueryGraphNode;
import sjtu.apex.gse.struct.QuerySchema;
import sjtu.apex.gse.system.QuerySystem;

/**
 * ComplexQueryGenerator generates complex queries with more edges and nodes
 * 
 * @author Tian Yuan
 *
 */
public class ComplexQueryGenerator {
	final double pec = 0.3;
	final double pee = 0.7;
	final double pce = 0.05;
	final int scanmax = 100;
	final boolean logging = true;
	
	String elfn;
	String initfn;
	String outfn;
	LabelManager lm;
	IDManager im;
	EdgeInfo einfo;
	QuerySystem sys;
	
	static int errSerial = 0;
	

	public ComplexQueryGenerator(Configuration config, String elfn, String initfn, String outfn, String eip) {
		this.elfn = elfn;
		this.initfn = initfn;
		this.outfn = outfn;
		sys = new QuerySystem(config);
		lm = new SleepyCatLabelManager(config);
		im = new SleepyCatIDManager(config);
		einfo = new EdgeInfo(eip);
	}
	
	private QuerySchema getFullSchema(QueryGraph g) {
		Set<QueryGraphNode> nset = new HashSet<QueryGraphNode>();
		for (int j = g.nodeCount() - 1; j >= 0; j--)
			nset.add(g.getNode(j));
		return new QuerySchema(g, nset);
	}
	
	private void constraintExtend(QuerySchema qs, List<QuerySchema> qarr) {
		QueryGraph qg = qs.getQueryGraph();
		
		for (int i = qg.nodeCount() - 1; i >= 0; i--)
			if (qg.getNode(i).isGeneral() && Math.random() < pec) {
				QueryGraphNode extNode = qg.getNode(i);
				List<String> avl = new ArrayList<String>();
				Scan s = sys.queryPlanner().plan(qs).open();
				
				while (s.next()) {
					int ni = s.getID(extNode);
					String[] sl = lm.getLabel(im.getURI(ni));
					for (int j = sl.length - 1; j >= 0; j--) {
						
						avl.add(sl[j]);
					}
				}
				
				QueryGraph ng = GraphUtility.extendConstraint(qg, i, avl.get((int)Math.round(avl.size() * Math.random())));
				
				qarr.add(getFullSchema(ng));
			}
	}
	
	private void edgeExtend(QuerySchema qs, List<QuerySchema> qarr, List<String> elabels) {
		if (Math.random() > pee) return; 
		Set<String> existEdgeLabel = new HashSet<String>();
		QueryGraph qg = qs.getQueryGraph();
		int nodeCount = qg.nodeCount();
		int cnt = 0;
		
		for (int i = qg.edgeCount() - 1; i >= 0; i--)
			existEdgeLabel.add(qg.getEdge(i).getLabel());
		
		
		List<Set<Edge>> list = new ArrayList<Set<Edge>>();
		
		for (int i = 0; i < nodeCount; i++)
			list.add(new HashSet<Edge>());
		
		Scan s = sys.queryPlanner().plan(qs).open();
		
		while (s.next() && cnt < scanmax) {
			cnt ++;
			for (int i = 0; i < nodeCount; i++) {
				QueryGraphNode qn = qg.getNode(i);
				int nid = s.getID(qn);
				List<Edge> edges = einfo.getEdges(nid); 
				Set<Edge> set = list.get(i);
				
				for (Edge e : edges)
					if (!set.contains(e)) set.add(e);
			}
		}
		s.close();
		
		System.out.println("\tRandomly generating queries ...");
		for (int i = 0; i < nodeCount; i++) {
			
			for (Edge e : list.get(i))
				if (!existEdgeLabel.contains(e.getLabel()) && Math.random() < pce) {
					System.out.println("\t" + e.getLabel() + " added.");
					QuerySchema nqs = getFullSchema(GraphUtility.extendEdge(qg, i, e.getLabel(), e.getDir()));
					
					qarr.add(nqs);
				}
			
		}
	}

	public void generate(int threshold) {
		try {
			BufferedReader rd = new BufferedReader(new FileReader(elfn));
			QueryWriter wr = new FileQueryWriter(outfn);
			QueryReader qp = new FileQueryReader(initfn);
			
			ArrayList<String> elabels = new ArrayList<String>();
			ArrayList<QuerySchema> qarr = new ArrayList<QuerySchema>();
			String temp;
			
			while ((temp = rd.readLine()) != null)
				elabels.add(temp.split("\t")[0]);
			
			QuerySchema qs;
			
			while ((qs = qp.read()) != null)
				qarr.add(qs);
			
			int head = 0, orgsize = qarr.size();
			
			while (head < orgsize && head < qarr.size() && (qarr.size() - orgsize < threshold)) {
				qs = qarr.get(head);
				
				int pp = qarr.size();
				System.out.println("CHECKING nodeid = " + head);
				edgeExtend(qs, qarr, elabels);
				constraintExtend(qs, qarr);
				System.out.println((qarr.size() - orgsize) + "ADDED");
				
				if (qarr.size() > pp)
					for (int i = pp; i < qarr.size(); i++)
						wr.write(qarr.get(i));
				
				head ++;
			}
			
			File f = new File("crash.log");
			f.delete();
			
			qp.close();
			wr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void close() {
		lm.close();
		im.close();
		einfo.close();
	}

	/**
	 * 
	 * @param args Configuration, Edge label filename, Initial queries, Output, threshold
	 */
	public static void main(String[] args) {
		Configuration cf = new FileConfig(args[0]); 
		String edgelabel = args[1];
		int t = 500;
		
		File f = new File(args[2]);
		File[] queries = f.listFiles();

		for (File q : queries) {
			String in = q.getAbsolutePath();
			String out = args[3] + "/" + q.getName();
			ComplexQueryGenerator qg = new ComplexQueryGenerator(cf, edgelabel, in, out, args[4]);
			qg.generate(t);
			qg.close();
		}
		
	}

}