package sjtu.apex.gse.metadata.test;

import java.util.List;

import org.junit.Test;

import sjtu.apex.gse.config.FileConfig;
import sjtu.apex.gse.pattern.PatternInfo;
import sjtu.apex.gse.query.FileQueryReader;
import sjtu.apex.gse.query.QueryReader;
import sjtu.apex.gse.struct.QuerySchema;
import sjtu.apex.gse.system.QuerySystem;


public class PatternManagerTest {
	
	@Test
	public void test() {
		QuerySystem qs = new QuerySystem(new FileConfig("cfg-web-1"));
		QueryReader rd = new FileQueryReader("dbg/q/q.0", qs.idManager());
		QuerySchema sch;
		
		while ((sch = rd.read()) != null) {
			List<PatternInfo> list = qs.patternManager().getSubPatterns(sch.getQueryGraph(), true);
			
			for (PatternInfo pi : list) {
				System.out.println(pi.getPatternString());
				System.out.print("\t");
				for (int src : pi.getSources())
					System.out.print(qs.sourceManager().getSource(src) + " ");
				System.out.println();
			}
		}
	}
}
