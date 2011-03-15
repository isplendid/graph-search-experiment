package sjtu.apex.gse.operator.join.test;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sjtu.apex.gse.operator.join.ResultMerger;
import sjtu.apex.gse.operator.join.Tuple;
import sjtu.apex.gse.struct.QueryGraph;
import sjtu.apex.gse.struct.QueryGraphNode;
import sjtu.apex.gse.struct.QuerySchema;

public class ResultMergerTest {
	
	private BlockingQueue<Tuple> bq;
	private ResultMerger l;
	private ResultMerger r;
	private Random rand;

	@Before
	public void setUp() throws Exception {
		rand = new Random();
		bq = new LinkedBlockingQueue<Tuple>();
		QueryGraph g = new QueryGraph();
		QueryGraphNode na = g.addNode();
		QueryGraphNode nb = g.addNode();
		QueryGraphNode nc = g.addNode();
		g.addEdge(na, nb, 0);
		g.addEdge(nb, nc, 0);
		List<QueryGraphNode> selectedNode = new ArrayList<QueryGraphNode>();
		selectedNode.add(na);
		selectedNode.add(nb);
		selectedNode.add(nc);
		QuerySchema qs = new QuerySchema(g, selectedNode);
		
		Map<QueryGraphNode, Integer> mapL = new HashMap<QueryGraphNode, Integer>();
		Map<QueryGraphNode, Integer> mapR = new HashMap<QueryGraphNode, Integer>();
		
		mapL.put(nb, 0);
		mapL.put(na, 1);
		
		mapR.put(nb, 0);
		mapR.put(nc, 1);
		
		l = new ResultMerger(bq, qs, mapL);
		r = new ResultMerger(bq, qs, mapR);
		
		l.setJoiningMerger(r);
		r.setJoiningMerger(l);
	}
	
	@Test
	public void test() throws Exception {
		new TupleAddThread(l).start();
		new TupleAddThread(r).start();
			
		new OutputThread(bq).start();
		
		Thread.sleep(10000);
	}

	@After
	public void tearDown() throws Exception {
		
	}

	private class TupleAddThread extends Thread {
		ResultMerger merger;
		
		public TupleAddThread(ResultMerger merger) {
			this.merger = merger;
		}
		
		@Override
		public void run() {
			while (true) {
				int a = rand.nextInt(100);
				int b = rand.nextInt();
				
				int[] values = new int[2];
				int[] jv = new int[1];
				
				jv[0] = a;
				values[0] = a;
				values[1] = b;
				
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				merger.addTuple(jv, new Tuple(values, new HashSet<Integer>()));
			}
		}
	}
	
	private class OutputThread extends Thread {
		private BlockingQueue<Tuple> bq;
		
		public OutputThread(BlockingQueue<Tuple> bq) {
			this.bq = bq;
		}
		
		@Override
		public void run() {
			Tuple t = null;
			
			while (true) {
				try {
					t = bq.take();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				System.out.println(t.getValue(0) + " , " + t.getValue(1) + " , " + t.getValue(2));
			}
			
		}
	}
}
