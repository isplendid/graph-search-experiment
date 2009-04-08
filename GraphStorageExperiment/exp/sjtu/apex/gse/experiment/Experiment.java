package sjtu.apex.gse.experiment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

import sjtu.apex.gse.query.Plan;
import sjtu.apex.gse.query.Scan;
import sjtu.apex.gse.struct.QueryGraph;
import sjtu.apex.gse.struct.QueryGraphNode;
import sjtu.apex.gse.struct.QuerySchema;
import sjtu.apex.gse.system.GraphStorage;


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
		BufferedWriter wr = new BufferedWriter(new FileWriter(args[1]));
		String temp;
		String[] ts;
		int cnt = 0;
		
		while (true) {
			System.out.println(++cnt);
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
			wr.append((System.currentTimeMillis() - time) + "\t " + count + "\n");
		}
		wr.close();
	}

}
