package sjtu.apex.gse.indexer.file;

import java.io.File;

import sjtu.apex.gse.config.Configuration;
import sjtu.apex.gse.indexer.InstanceKeywordRepository;
import sjtu.apex.gse.indexer.LabelManager;

import com.sleepycat.bind.tuple.StringBinding;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;

public class SleepyCatLabelManager implements LabelManager {
	
	Environment env;
	Database uri2kw;
	
	public SleepyCatLabelManager(Configuration config) {
		this(config.getStringSetting("LabelRepository", null));
	}
	
	public SleepyCatLabelManager(String dir) {
		EnvironmentConfig ec = new EnvironmentConfig();
		ec.setAllowCreate(true);
		ec.setTransactional(false);
		
		try {
			env = new Environment(new File(dir), ec);
		} catch (Exception e) {
			env = null;
			e.printStackTrace();
		}
		
		DatabaseConfig dc = new DatabaseConfig();
		dc.setAllowCreate(true);
		dc.setTransactional(false);
		
		try {
			uri2kw = env.openDatabase(null, dir, dc);
		} catch (DatabaseException e) {
			uri2kw = null;
			e.printStackTrace();
		}
	}

	@Override
	public String[] getLabel(String uri) {
		DatabaseEntry ude = new DatabaseEntry();
		DatabaseEntry kde = new DatabaseEntry();
		
		StringBinding.stringToEntry(uri, ude);
		try {
			OperationStatus os = uri2kw.get(null, ude, kde, LockMode.DEFAULT);
			if (os == OperationStatus.SUCCESS)
				return StringBinding.entryToString(kde).split(" ");
			else
				return null;
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public void load(InstanceKeywordRepository repos) {
		while (repos.next()) {
			String uri = repos.getInstance();
			String ws = repos.getWordStr();
	
			DatabaseEntry ude = new DatabaseEntry();
			DatabaseEntry wde = new DatabaseEntry();
	
			StringBinding.stringToEntry(uri, ude);
			StringBinding.stringToEntry(ws, wde);
			
			try {
				uri2kw.put(null, ude, wde);
			} catch (DatabaseException e) {
				e.printStackTrace();
			}
		}
	}

	public void close() {
		try {
			uri2kw.close();
			env.close();
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
	}
}
