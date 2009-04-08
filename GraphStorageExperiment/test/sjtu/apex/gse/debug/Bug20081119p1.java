package sjtu.apex.gse.debug;

import java.util.ArrayList;

import sjtu.apex.gse.query.UpdateScan;
import sjtu.apex.gse.storage.RAMArrayRepository;
import sjtu.apex.gse.struct.QueryGraph;
import sjtu.apex.gse.struct.QueryGraphNode;
import sjtu.apex.gse.struct.QuerySchema;


/**
 * 
 * Bug20081119p1
 * @author Tian Yuan
 */
public class Bug20081119p1 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		QueryGraph qg = new QueryGraph();
		QueryGraphNode na = qg.addNode();
		QueryGraphNode nb = qg.addNode();
		ArrayList<QueryGraphNode> al = new ArrayList<QueryGraphNode>();
		al.add(na);
		al.add(nb);
		QuerySchema qs = new QuerySchema(qg, al);
		UpdateScan s = new RAMArrayRepository(qs);
		
		for (int i = 0; i < 10000; i++) {
			s.insert();
			s.setID(0, i);
			s.setID(1, i*2);
		}
		
		s.beforeFirst();
		while (s.next()) {
			System.out.println(s.getID(0) + " " + s.getID(1));
		}

	}

}
