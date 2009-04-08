package sjtu.apex.gse.index.test;


/**
 * 
 * @author Tian Yuan
 * 
 */
public class TestIndexerService {
	
//	IndexStorage indexStorage;
//	FileDataStorageReader dataReader;
//	PatternCodec codec;
//	
//    public TestIndexerService(IndexStorage indexstorage, FileDataStorageReader dsr) {
//        try {
//            this.indexStorage = indexstorage;
//            this.dataReader = dsr;
//            codec = new PreorderPatternCodec();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//	
//	public void indexNode() {
//        try {
//            ResultSet rs = dataReader.getSQLResultSet("SELECT * FROM TEXTTABLE");
//            int counter = 0;
//            while(rs.next()) {
//                if(counter % 1000 == 0)
//                    System.out.println("node " + counter);
//                counter++;
//                String uri = rs.getString(1);
//                String label = rs.getString(2);
//                String[] terms = label.split(" ");
//                int id = indexStorage.getID(uri);
//                if(id == -1)
//                    id = indexStorage.addURI(uri);
//                for(int i = 0; i < terms.length; i++) {
//                    QueryGraph g = new QueryGraph();
//                    g.addNode(terms[i]);
//                    indexStorage.addNode(codec.encodePattern(g), id);
//                }
//            }
//            rs.close();
//            dataReader.closeStat();
//            
//            ResultSet keys = indexStorage.getPatterns(1);
//            while(keys.next()) {
//                String key = keys.getString(1);
//                int count = indexStorage.getPatternCount(key, 1);
//                indexStorage.addStat(key, count);
//            }
//            keys.close();
//            indexStorage.closeStat();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//	
//	public List<String> getLabels(String uri) {
//		List<String> result = null;
//		ResultSet rs1 = dataReader.getSQLResultSet("SELECT text FROM texttable WHERE uri = '" + uri + "'");
//		
//		try {
//			while (rs1.next()) {
//				String[] str = rs1.getString(1).split(" ");
//				
//				if (str.length == 0) return null;
//				result = new ArrayList<String>();
//				
//				for (int i = 0; i < str.length; i++)
//					result.add(str[i]);
//			}
//			rs1.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		dataReader.closeStat();
//		
//		return result;
//	}
//	
//	public void indexEdge(String filename) {
//       	try {
//   			BufferedReader rd = new BufferedReader(new FileReader(filename));
//   			String temp;
//   			int count = 0, all = 0;
//    			
//   			while ((temp = rd.readLine()) != null) {
//   				String[] str = temp.split(">");
//   				String sub = str[0] + ">";
//   				String pred = str[1].substring(1) + ">";
//   				String obj = str[2].substring(1) + ">";
//   				
//   				
//   				if (str[2].charAt(1) != '"') {
//   					
//   					List<String> left = getLabels(sub);
//   					List<String> right = getLabels(obj);
//   					
//   					
//   					
//   					if (left == null || right == null) {
////   						System.out.println(sub + "\t" + obj);
//   						if ((++ all) % 1000 == 0) System.out.println("NOT FOUND = " + all);
//   						continue;
//   					}
//   					
//   					int ss = indexStorage.getID(sub);
//   					int os = indexStorage.getID(obj);
//   					if (ss == -1 || os == -1) continue;
//   					
//   					if ((++count) % 1 == 0) System.out.println(count);
//   					
//   					for (String a : left)
//   						for (String b : right) {
//   							QueryGraph g = new QueryGraph();
//   							QueryGraphNode ln = g.addNode(a);
//   							QueryGraphNode rn = g.addNode(b);
//   							
//   							g.addEdge(ln, rn, pred);
//   							indexStorage.addEdge(codec.encodePattern(g), ss, os);
//   						}
//   				}
//   			}
//       	} catch (Exception e) {
//       		e.printStackTrace();
//       	}
//    }
//	
//	public void close() {
//		dataReader.close();
//		indexStorage.close();
//	}
//	
//	public static void mainInsertNode() {
//		IndexStorage stg = new IndexStorage();
//		stg.uninstall();
//		stg.install();
//		TestIndexerService is = new TestIndexerService(stg, new FileDataStorageReader());
//		
//		is.indexNode();
//		is.close();
//	}
//	
//	public static void mainInsertEdge(String filename) {
//		TestIndexerService is = new TestIndexerService(new IndexStorage(), new FileDataStorageReader());
//		is.indexEdge(filename);
//		is.close();
//	}
//	
//	public static void main(String[] args) {
//		mainInsertEdge(args[0]);
//	}
	
}
