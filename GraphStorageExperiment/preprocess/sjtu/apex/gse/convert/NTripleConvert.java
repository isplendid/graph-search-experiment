package sjtu.apex.gse.convert;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.rio.Parser;
import org.openrdf.rio.StatementHandler;
import org.openrdf.rio.StatementHandlerException;
import org.openrdf.rio.ntriples.NTriplesWriter;
import org.openrdf.rio.rdfxml.RdfXmlParser;

public class NTripleConvert {
	
	private static String url = "http://sjtu.edu.cn";
	
	public static void convert(String inFn, String outFn) throws Exception {
		convert(inFn, outFn, url);
	}
	
	public static void convert(String inPath, String outFn, String url) throws Exception
	{
		File f = new File(inPath);
		File[] queries = f.listFiles();

		OutputStream oup = new FileOutputStream(outFn);
		for (File q : queries)
			if (q.isFile() && q.getAbsolutePath().endsWith(".owl")) {
				
				Parser parser = new RdfXmlParser();
				InputStream inp = new FileInputStream(q.getAbsoluteFile());
				final NTriplesWriter writer = new NTriplesWriter(oup);
				writer.startDocument();
				parser.setStatementHandler(new StatementHandler()
				{
					public void handleStatement(Resource s, URI p, Value o) throws StatementHandlerException {
						try
						{
							writer.writeStatement(s, p, o);
						}

						catch (IOException e)
						{
							throw new StatementHandlerException(e);
						}
					}
				});
				
				parser.parse(inp, url);
				writer.endDocument();
				inp.close();
			}

		oup.close();
	}
	
	public static void main(String[] args) throws Exception {
		NTripleConvert.convert(args[0], args[1]);
		
	}


}
