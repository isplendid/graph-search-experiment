package sjtu.apex.gse.experiment.querygen;

import java.io.File;
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
import sjtu.apex.gse.operator.Plan;
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
	double pec = 0.01;
	double pee1 = 0.5;
	double pee2 = 0.03;
	int edgeScanMax = 100;
	int conScanMax = 3000;
	final boolean logging = true;
	
//	String elfn;
	String initfn;
	String outfn;
	LabelManager lm;
	IDManager im;
	EdgeInfo einfo;
	QuerySystem sys;
	
	static int errSerial = 0;
	

	public ComplexQueryGenerator(Configuration config, /*String elfn,*/ String initfn, String outfn, String eip, Configuration prob) {
//		this.elfn = elfn;
		pec = prob.getDoubleSetting("ItrGenConExt", 0.01);
		pee1 = prob.getDoubleSetting("ItrGenEdgeExt1", 0.5);
		pee2 = prob.getDoubleSetting("ItrGenEdgeExt2", 0.03);
		edgeScanMax = prob.getIntegerSetting("ItrGenConScan", 100);
		conScanMax = prob.getIntegerSetting("ItrGenEdgeScan", 3000);
		
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
		int cnt = 0;
		
		Plan p = sys.queryPlanner().plan(qs);
		Scan s = p.open();
		while (s.next() && cnt < conScanMax) {
			cnt ++;
			for (int i = qg.nodeCount() - 1; i >= 0; i--)
				if (qg.getNode(i).isGeneral() && Math.random() < pec) {
					QueryGraphNode extNode = qg.getNode(i);
					int ni = s.getID(extNode);
					String[] sl = lm.getLabel(im.getURI(ni));


					if (sl.length != 0) {
						QueryGraph ng = GraphUtility.extendConstraint(qg, i, sl[(int)(sl.length * Math.random())]);

						qarr.add(getFullSchema(ng));
					}
				}
		}
		s.close();
	}
	
	private void edgeExtend(QuerySchema qs, List<QuerySchema> qarr) {//, List<String> elabels) {
		if (Math.random() > pee1) return; 
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
		
		while (s.next() && cnt < edgeScanMax) {
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
				if (!existEdgeLabel.contains(e.getLabel()) && Math.random() < pee2) {
					System.out.println("\t" + e.toString() + " added to node " + qg.getNode(i) + ".");
					QuerySchema nqs = getFullSchema(GraphUtility.extendEdge(qg, i, e.getLabel(), e.getDir()));
					
					qarr.add(nqs);
				}
			
		}
	}

	public void generate(int threshold, int mod) {
		try {
//			BufferedReader rd = new BufferedReader(new FileReader(elfn));
			QueryWriter wr = new FileQueryWriter(outfn);
			QueryReader qp = new FileQueryReader(initfn);
			
//			ArrayList<String> elabels = new ArrayList<String>();
			ArrayList<QuerySchema> qarr = new ArrayList<QuerySchema>();
//			String temp;
			
//			while ((temp = rd.readLine()) != null)
//				elabels.add(temp.split("\t")[0]);
			
			QuerySchema qs;
			
			while ((qs = qp.read()) != null)
				qarr.add(qs);
			
			int head = 0, orgsize = qarr.size();
			
			while (head < orgsize && head < qarr.size() && (qarr.size() - orgsize < threshold)) {
				qs = qarr.get(head);
				
				int pp = qarr.size();
				System.out.println("CHECKING nodeid = " + head);
				
				if ((mod & 0x00000001) != 0) edgeExtend(qs, qarr);//, elabels);
				if ((mod & 0x00000002) != 0) constraintExtend(qs, qarr);
				
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
//		String edgelabel = args[1];
//		int t = 1000;
		
		File f = new File(args[1]);
		File[] queries = f.listFiles();

		for (File q : queries) {
			String in = q.getAbsolutePath();
			String out = args[2] + "/" + q.getName();
			ComplexQueryGenerator qg = new ComplexQueryGenerator(cf, in, out, args[3], new FileConfig(args[5]));
			qg.generate(Integer.parseInt(args[6]), Integer.parseInt(args[4]));
			qg.close();
		}
		
	}

}
