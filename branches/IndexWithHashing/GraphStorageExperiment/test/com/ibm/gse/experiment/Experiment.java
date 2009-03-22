package com.ibm.gse.experiment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

import com.ibm.gse.query.Plan;
import com.ibm.gse.query.Scan;
import com.ibm.gse.struct.QueryGraph;
import com.ibm.gse.struct.QueryGraphNode;
import com.ibm.gse.struct.QuerySchema;
import com.ibm.gse.system.GraphStorage;

/**
 * 
 * 
 * @author Tian Yuan
 */
public class Experiment {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		BufferedReader rd = new BufferedReader(new FileReader(args[0]));
		String temp;
		String[] ts;
		
		while (true) {
			QueryGraph graph = new QueryGraph();
			
			temp = rd.readLine();
			if (temp == null || temp.length() == 0) break;
			ts = temp.split(" ");
			int nodeCnt = Integer.parseInt(ts[0]);
			int edgeCnt = Integer.parseInt(ts[1]);
			
			QueryGraphNode[] nodes = new QueryGraphNode[nodeCnt];
			HashSet<QueryGraphNode> ns = new HashSet<QueryGraphNode>();
			for (int i = 0; i < nodeCnt; i++) {
				temp = rd.readLine();
				if (temp.length() == 0 || temp.equals("*"))
					nodes[i] = graph.addNode();
				else
					nodes[i] = graph.addNode(temp);
				
				ns.add(nodes[i]);
			}
			
			for (int i = 0; i < edgeCnt; i++) {
				temp = rd.readLine();
				ts = temp.split(" ");
				int a = Integer.parseInt(ts[0]), b = Integer.parseInt(ts[1]);
				
				graph.addEdge(nodes[a - 1], nodes[b - 1], ts[2]);
			}
			
			QuerySchema qs = new QuerySchema(graph, ns);
			Plan p = GraphStorage.queryPlanner.plan(qs);
			long time = System.currentTimeMillis();
			Scan scan = p.open();

			int count = 0;
			while (scan.next()) {
				/* DO SOMETHING */
				count++;
			}
			System.out.println("TIME = " + (System.currentTimeMillis() - time) + ", COUNT = " + count);
		}
	}

}
