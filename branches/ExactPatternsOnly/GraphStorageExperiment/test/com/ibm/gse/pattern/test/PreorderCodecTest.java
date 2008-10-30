package com.ibm.gse.pattern.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.ibm.gse.pattern.PreorderPatternCodec;

/**
 * @author Tian Yuan
 *
 */
public class PreorderCodecTest {
	
	List<String> graphs = new ArrayList<String>();
	PreorderPatternCodec pc = new PreorderPatternCodec();

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
		
	}

	/**
	 * Test method for {@link com.ibm.gse.pattern.PreorderPatternCodec#encodePattern(com.ibm.gse.struct.QueryGraph)}.
	 */
	@Test
	public void testEncodePattern() {
		
	}

	/**
	 * Test method for {@link com.ibm.gse.pattern.PreorderPatternCodec#decodePattern(java.lang.String)}.
	 */
	@Test
	public void testDecodePattern() {
		for (String s : graphs)
			assertEquals(s, pc.encodePattern(pc.decodePattern(s)));
	}

}
