package sjtu.apex.gse.text;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.standard.StandardTokenizer;

import com.aliasi.tokenizer.Tokenizer;

public class NormalTokenizer extends Tokenizer {
	
	StandardTokenizer tk;
	
	public NormalTokenizer(Reader rd) {
		tk = new StandardTokenizer(rd);
	}

	@Override
	public String nextToken() {
		try {
			Token tkn;
			if ((tkn = tk.next()) == null)
				return null;
			else
				return tkn.termText();
		} catch (IOException e) {
			return null;
		}
	}

}
