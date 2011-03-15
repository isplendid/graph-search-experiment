package sjtu.apex.gse.operator.web.test;

import static org.junit.Assert.assertTrue;
import ie.deri.urq.lidaq.log.LODQ2_LogHandling;
import ie.deri.urq.lidaq.repos.WebRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import sjtu.apex.gse.indexer.IDManager;
import sjtu.apex.gse.indexer.SourceManager;
import sjtu.apex.gse.indexer.file.SleepyCatIDManager;
import sjtu.apex.gse.indexer.file.SleepyCatSourceManager;
import sjtu.apex.gse.operator.web.WebPatternScan;
import sjtu.apex.gse.struct.QueryGraph;
import sjtu.apex.gse.struct.QueryGraphNode;
import sjtu.apex.gse.struct.QuerySchema;

public class WebRepositoryTest {
	
	private String tmpIdFldr = "tmp/id";
	private String tmpSrcFldr = "tmp/src";
	
	private List<QuerySchema> list = new ArrayList<QuerySchema>();
	private List<String> bind = new ArrayList<String>();
	private IDManager idman;
	private SourceManager srcman;
	
	@Before
	public void init() {
		idman = new SleepyCatIDManager(tmpIdFldr);
		srcman = new SleepyCatSourceManager(tmpSrcFldr);
		
		//Test case 1
		QueryGraph graph = new QueryGraph();
		QueryGraphNode from = graph.addNode();
		QueryGraphNode to = graph.addNode();
		graph.addEdge(from, to, idman.addGetID("http://xmlns.com/foaf/0.1/title"));
		List<QueryGraphNode> seln = new ArrayList<QueryGraphNode>();
		seln.add(to);
		
		QuerySchema qs = new QuerySchema(graph, seln);
		list.add(qs);
		bind.add("http://www.informatik.uni-leipzig.de/~auer/foaf.rdf#me");
	}
	
	@Test
	public void test() {
		LODQ2_LogHandling.setDefaultLogging();
		System.out.println("Test started");
		for (int i = 0; i < list.size(); i++) {
			QuerySchema qs = list.get(i);
			String uri = bind.get(i);
			
			WebPatternScan webrepos = new WebPatternScan(qs, idman, srcman, new WebRepository(null, null));
			
			webrepos.addKey(idman.addGetID(uri), -1, new HashSet<Integer>());
			Set<QueryGraphNode> ns = qs.getSelectedNodeSet();
			
			webrepos.keyEnded();
			
			System.out.println("Iteration begins");
			while (webrepos.next()) {
				System.out.print("Output : ");
				for (QueryGraphNode n : ns) {
					System.out.println(idman.getURI(webrepos.getID(n)) + " ");
				}
				System.out.println();
			}
		}
		assertTrue(true);
	}
	
}
