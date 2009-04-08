package sjtu.apex.gse.storage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import sjtu.apex.gse.query.Scan;
import sjtu.apex.gse.struct.QueryGraphNode;
import sjtu.apex.gse.system.ConnectionFactory;


public class DBRepository implements Scan{
	
	static Connection conn = ConnectionFactory.createConnection();
	
	Statement stm;
	ResultSet rs;
	Map<QueryGraphNode, Integer> nid;
	
	public DBRepository(String sqlStm, Map<QueryGraphNode, Integer> nid) {
		try {
			stm = conn.createStatement();
			rs = stm.executeQuery(sqlStm);
			this.nid = nid;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void beforeFirst() {
		try {
			rs.beforeFirst();
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}

	@Override
	public void close() {
		try {
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getID(QueryGraphNode n) {
		try {
			return rs.getInt("NODE" + nid.get(n));
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public int getID(int nodeID) {
		try {
			return rs.getInt(nodeID);
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public boolean hasNode(QueryGraphNode n) {
		return nid.containsKey(n);
	}

	@Override
	public boolean next() {
		try {
			return rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
}
