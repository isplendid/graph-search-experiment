package sjtu.apex.gse.index.file.test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sjtu.apex.gse.config.Configuration;
import sjtu.apex.gse.index.file.FileIndexReader;


public class FileIndexReaderTest {
	
	final int size = 2;
	FileIndexReader rd;

	@Before
	public void setUp() throws Exception {
		Configuration config = null;
		rd = new FileIndexReader(config.getStringSetting("DataFolder", null) + "/index" + (size - 1), size, 128);
	}

	@Test
	public void testFileIndexReader() throws IOException {
		int counter = 0;
		BufferedWriter wr = new BufferedWriter(new FileWriter("result.txt"));
		
		while (rd.next()) {
			counter ++;
			if (rd.getInstanceCount() > 10)
				wr.append(rd.getPatternString() + "\t" + rd.getInstanceCount() + "\n");
//			if (counter % 1000 == 0) break;
		}
		wr.close();
	}
	
	@After
	public void tearDown() {
		rd.close();
	}

}
