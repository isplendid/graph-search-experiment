package sjtu.apex.gse.experiment.edge;

import sjtu.apex.gse.indexer.IDManager;
import sjtu.apex.gse.indexer.RelationRepository;
import sjtu.apex.gse.indexer.file.SleepyCatIDManager;

public class EdgeLoad {
	public static void load(String src, String dest, String idDB) {
		EdgeInfo ei = new EdgeInfo(dest);
		IDManager lbman = new SleepyCatIDManager(idDB);
		RelationRepository rd = new RelationRepository(src);
		
		while (rd.next()) {
			int sid = lbman.getID(rd.getSubject());
			int oid = lbman.getID(rd.getObject());
			String pred = rd.getPredicate();
			
			ei.addEdge(sid, pred, true);
			ei.addEdge(oid, pred, false);
		}
		
		ei.close();
	}
	
	public static void main(String[] args) {
		EdgeLoad.load(args[0], args[1], args[2]);
	}
}
