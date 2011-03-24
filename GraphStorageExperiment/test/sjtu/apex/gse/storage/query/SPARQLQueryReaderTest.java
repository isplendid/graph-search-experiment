package sjtu.apex.gse.storage.query;

import org.junit.Test;

import sjtu.apex.gse.indexer.file.SleepyCatIDManager;
import sjtu.apex.gse.query.QueryReader;
import sjtu.apex.gse.query.SPARQLQueryReader;
import sjtu.apex.gse.struct.QuerySchema;


public class SPARQLQueryReaderTest {
	@Test
	public void test() {
		QueryReader r = new SPARQLQueryReader("test.sparql", new SleepyCatIDManager("dbg/id"));
		QuerySchema sch;
		
		while ((sch = r.read()) != null) {
			System.out.println(sch);
		}
	}
}
