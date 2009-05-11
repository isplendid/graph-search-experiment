package sjtu.apex.gse.experiment.edge;

import java.util.ArrayList;
import java.util.List;

import sjtu.apex.gse.filesystem.FilesystemUtility;
import sjtu.apex.gse.indexer.IDManager;
import sjtu.apex.gse.indexer.RelationRepository;
import sjtu.apex.gse.indexer.file.SleepyCatIDManager;
import sjtu.apex.gse.util.Heap;

public class EdgeLoad {
	
	static final int maxIns = 5000000;
	
	private static void sort(String src, String tmpDir) {
		List<EdgeContainer> tarr = new ArrayList<EdgeContainer>();
		EdgeFileReader rd = new EdgeFileReader(src);
		int cnt = 0;
		
		FilesystemUtility.createDir(tmpDir);
		
		//Split the source file into multiple threads
		while (rd.next()) {
			tarr.add(new EdgeContainer(rd.getID(), rd.getEdge()));
			
			if (tarr.size() >= maxIns) {
				EdgeFileWriter wr = new EdgeFileWriter(tmpDir + "/tmp" + (cnt++));
				java.util.Collections.sort(tarr);
				
				for (EdgeContainer ec : tarr)
					wr.append(ec.id, ec.edge);
				
				wr.close();
				tarr = new ArrayList<EdgeContainer>();
			}
		}
		rd.close();
		
		EdgeFileWriter wr = new EdgeFileWriter(tmpDir + "/tmp" + (cnt++));
		java.util.Collections.sort(tarr);
		
		for (EdgeContainer ec : tarr)
			wr.append(ec.id, ec.edge);
		
		wr.close();
		
		FilesystemUtility.deleteFile(src);
		
		//Merge threads into target file
		wr = new EdgeFileWriter(src);
		Heap hp = new Heap();
		
		for (int i = 0; i < cnt; i++) {
			rd = new EdgeFileReader(tmpDir + "/tmp" + i);
			
			if (rd.next()) 
				hp.insert(rd);
			else
				rd.close();
		}
		
		while (hp.size() > 0) {
			rd = (EdgeFileReader)hp.remove();
			wr.append(rd.getID(), rd.getEdge());
			
			if (rd.next())
				hp.insert(rd);
			else
				rd.close();
		}
		
		wr.close();
		
		FilesystemUtility.deleteDir(tmpDir);
		
	}
	
	public static void load(String src, String dest, String idDB) {
		EdgeFileWriter wr = new EdgeFileWriter(dest + "/edgeTmp");
		
		IDManager lbman = new SleepyCatIDManager(idDB);
		RelationRepository rd = new RelationRepository(src);
		int cnt = 0;
		
		while (rd.next()) {
			if ((++cnt) % 5000 == 0)
				System.out.println(cnt);
			
			int sid = lbman.getID(rd.getSubject());
			int oid = lbman.getID(rd.getObject());
			
			if (sid == -1 || oid == -1) continue;
			
			String pred = rd.getPredicate();
			
			wr.append(sid, new Edge(pred, true));
			wr.append(oid, new Edge(pred, false));
		}
		wr.close();
		rd.close();
		
		sort(dest + "/edgeTmp", dest + "/tmp");
		
		EdgeInfo ei = new EdgeInfo(dest);
		EdgeFileReader erd = new EdgeFileReader(dest + "/edgeTmp");
		Integer prev = null;
		List<Edge> tlist = new ArrayList<Edge>();
		Edge ep = null;
		
		while (erd.next()) {
			int tid = erd.getID();
			Edge tedge = erd.getEdge();
			
			if (prev == null)
				tlist.add(tedge);
			else if (tid != prev) {
				ei.addEdge(prev, tlist);
				tlist = new ArrayList<Edge>();
				tlist.add(tedge);
			} else if (!tedge.equals(ep))
				tlist.add(tedge);
			prev = tid;
			ep = tedge;
		}
		erd.close();
		ei.addEdge(prev, tlist);
		ei.close();
		
		FilesystemUtility.deleteFile(dest + "/edgeTmp");
	}
	
	public static void main(String[] args) {
		EdgeLoad.load(args[0], args[1], args[2]);
	}
}
