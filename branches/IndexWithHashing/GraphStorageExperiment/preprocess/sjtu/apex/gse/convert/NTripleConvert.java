package sjtu.apex.gse.convert;

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
	public static void main(String[] args) throws Exception
	{
		Parser parser = new RdfXmlParser();
		InputStream inp = new FileInputStream(args[0]);
		OutputStream oup = new FileOutputStream(args[1]);
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
		
		parser.parse(inp, args[2]);
		writer.endDocument();
	}


}
