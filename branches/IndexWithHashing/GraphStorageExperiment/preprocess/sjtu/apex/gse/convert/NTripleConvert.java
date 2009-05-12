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

import sjtu.apex.gse.experiment.Experiment;

public class NTripleConvert {
	
	private static String url = "http://sjtu.edu.cn";
	
	public static void convert(String inFn, String outFn) throws Exception {
		convert(inFn, outFn, url);
	}
	
	public static void convert(String inFn, String outFn, String url) throws Exception
	{
		Parser parser = new RdfXmlParser();
		InputStream inp = new FileInputStream(inFn);
		OutputStream oup = new FileOutputStream(outFn);
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
	}
	
	public static void main(String[] args) throws Exception {
		File f = new File(args[0]);
		File[] queries = f.listFiles();

		for (File q : queries)
			NTripleConvert.convert(q.getAbsolutePath(), args[1] + q.getName());
		
	}


}
