package sjtu.apex.gse.exp.edge;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.semanticweb.yars.nx.parser.NxParser;
import org.semanticweb.yars.nx.parser.ParseException;

import sjtu.apex.gse.filesystem.FilesystemUtility;
import sjtu.apex.gse.indexer.IDManager;
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
	
	private static void extractEdges(String src, String dest, String idDB) {
		EdgeFileWriter wr = new EdgeFileWriter(dest);
		IDManager idman = new SleepyCatIDManager(idDB);
		InputStream ins;
		NxParser nxp;
		int cnt = 0;
		org.semanticweb.yars.nx.Node[] stmt = null;
		
		try {
			ins = new FileInputStream(src);
			if (src.toLowerCase().endsWith(".gz")) ins = new GZIPInputStream(ins);
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
			
			if ((++cnt) % 5000 == 0)
				System.out.println(cnt);
			
			int sid = idman.addGetID(stmt[0].toN3());
			int oid = idman.addGetID(stmt[2].toN3());
			
			if (sid == -1 || oid == -1) continue;
			
			int pred = idman.addGetID(stmt[1].toN3());
			
			wr.append(sid, new Edge(pred, true));
			wr.append(oid, new Edge(pred, false));
		}
		wr.close();
	}
	
	private static void loadToEdgeDB(String src, String dest) {
		EdgeInfo ei = new EdgeInfo(dest);
		EdgeFileReader erd = new EdgeFileReader(src);
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
	}
	
	/**
	 * Load the edge
	 * @param src
	 * @param dest
	 * @param idDB
	 */
	public static void load(String src, String dest, String idDB) {
		extractEdges(src, dest + "/edgeTmp", idDB);
		sort(dest + "/edgeTmp", dest + "/tmp");
		loadToEdgeDB(dest + "/edgeTmp", dest);
		FilesystemUtility.deleteFile(dest + "/edgeTmp");
	}
	
	/**
	 * 
	 * @param args - 
	 */
	public static void main(String[] args) {
		EdgeLoad.load(args[0], args[1], args[2]);
	}
}
