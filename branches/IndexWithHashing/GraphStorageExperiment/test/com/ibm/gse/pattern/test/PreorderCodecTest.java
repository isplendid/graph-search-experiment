package com.ibm.gse.pattern.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.ibm.gse.pattern.HashingPatternCodec;
import com.ibm.gse.struct.QueryGraph;
import com.ibm.gse.struct.QueryGraphNode;

/**
 * @author Tian Yuan
 *
 */
public class PreorderCodecTest {
	
	List<String> graphs = new ArrayList<String>();
	
	List<QueryGraph> qg = new ArrayList<QueryGraph>();
	HashingPatternCodec pc = new HashingPatternCodec();

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		graphs.add("a");
		graphs.add("a[-b::c]");
		graphs.add("a[+b::c,-d::e]");
		graphs.add("a[+b::c[+d::f],-d::e]");
		graphs.add("http[+related::nodoc]");
		
		QueryGraph q;
		q = new QueryGraph();
		QueryGraphNode na = q.addNode();
		QueryGraphNode nb = q.addNode();
		q.addEdge(na, nb, "test");
		qg.add(q);
	}

	/**
	 * Test method for {@link com.ibm.gse.pattern.HashingPatternCodec#encodePattern(com.ibm.gse.struct.QueryGraph)}.
	 */
	@Test
	public void testEncodePattern() {
		
	}

	/**
	 * Test method for {@link com.ibm.gse.pattern.HashingPatternCodec#decodePattern(java.lang.String)}.
	 */
	@Test
	public void testDecodePattern() {
		for (String s : graphs)
			assertEquals(s, pc.encodePattern(pc.decodePattern(s)));
		
		for (QueryGraph g : qg)
			System.out.println(pc.encodePattern(g));
	}

}
