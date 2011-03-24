package sjtu.apex.gse.query;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.openrdf.query.algebra.TupleExpr;
import org.openrdf.query.parser.QueryParser;
import org.openrdf.query.parser.sparql.SPARQLParserFactory;

import sjtu.apex.gse.indexer.IDManager;
import sjtu.apex.gse.struct.QuerySchema;

public class SPARQLQueryReader implements QueryReader {
	QueryParser parser;
	boolean read = false;
	QuerySchema sch;
	
	public SPARQLQueryReader(String filename, IDManager idman) {
		parser = new SPARQLParserFactory().getParser();
		SPARQLConvert convert = new SPARQLConvert(idman);
		
		try {
			TupleExpr exp = parser.parseQuery(readStringFromFile(filename), "http://deri.ie").getTupleExpr();
			
			exp.visit(convert);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		sch = convert.getQuerySchema();
	}
	
	private String readStringFromFile(String filename) {
		StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader;
        
		try {
			reader = new BufferedReader(new FileReader(filename));
	        char[] buf = new char[1024];
	        int numRead=0;
	        while((numRead=reader.read(buf)) != -1){
	            fileData.append(buf, 0, numRead);
	        }
	        reader.close();
	        
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        return fileData.toString();
	}

	@Override
	public QuerySchema read() {
		QuerySchema ret = null;
		
		if (!read) {
			read = true;
			ret = sch;
		}

		return ret;
	}

	@Override
	public void close() {

	}

}
