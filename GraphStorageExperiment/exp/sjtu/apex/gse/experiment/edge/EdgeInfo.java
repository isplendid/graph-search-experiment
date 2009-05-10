package sjtu.apex.gse.experiment.edge;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.sleepycat.bind.tuple.IntegerBinding;
import com.sleepycat.bind.tuple.StringBinding;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;

public class EdgeInfo {
	Database edge;
	Environment env;
	
	public EdgeInfo(String path) {
		EnvironmentConfig ec = new EnvironmentConfig();
		ec.setAllowCreate(true);
		ec.setTransactional(false);
		try {
			env = new Environment(new File(path), ec);
		} catch (Exception e) {
			e.printStackTrace();
			env = null;
		}
		
		DatabaseConfig dc = new DatabaseConfig();
		dc.setAllowCreate(true);
		dc.setTransactional(false);
		
		try {
			edge = env.openDatabase(null, "EDGE", dc);
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
	}
	
	public void addEdge(int id, String edgeLab, boolean dir) {
		String temp;
		DatabaseEntry idEnt = new DatabaseEntry();
		DatabaseEntry edgeEnt = new DatabaseEntry();
		IntegerBinding.intToEntry(id, idEnt);
		OperationStatus os;
		
		try {
			os = edge.get(null, idEnt, edgeEnt, LockMode.DEFAULT);
			if (os == OperationStatus.NOTFOUND)
				temp = (dir ? "+" : "-") + edgeLab;
			else
				temp = StringBinding.entryToString(edgeEnt) + "\t" + (dir ? "+" : "-") + edgeLab;
			
			StringBinding.stringToEntry(temp, edgeEnt);
			edge.put(null, idEnt, edgeEnt);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<Edge> getEdges(int id) {
		DatabaseEntry idEnt = new DatabaseEntry();
		DatabaseEntry edgeEnt = new DatabaseEntry();
		IntegerBinding.intToEntry(id, edgeEnt);
		OperationStatus os;
		
		try {
			os = edge.get(null, idEnt, edgeEnt, LockMode.DEFAULT);
			if (os == OperationStatus.NOTFOUND)
				return null;
			else {
				String[] ts = StringBinding.entryToString(edgeEnt).split("\t");
				List<Edge> res = new ArrayList<Edge>();
				
				for (String s : ts)
					res.add(new Edge(s.substring(1), s.startsWith("+")));
					
				return res;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void close() {
		try {
			edge.close();
			env.close();
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
	}
}
