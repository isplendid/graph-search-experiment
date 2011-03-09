package sjtu.apex.gse.indexer.file;

import java.io.File;

import com.sleepycat.bind.tuple.IntegerBinding;
import com.sleepycat.bind.tuple.StringBinding;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;

import sjtu.apex.gse.config.Configuration;
import sjtu.apex.gse.indexer.SourceManager;

public class SleepyCatSourceManager implements SourceManager {
	
	long counter = 1;
	
	Database uri2id;
	Database id2uri;
	Environment env;
	
	public SleepyCatSourceManager(Configuration config) {
		this(config.getStringSetting("SourceMappingFolder", null));
	}
	
	public SleepyCatSourceManager(String srcDir) {
		EnvironmentConfig ec = new EnvironmentConfig();
		ec.setAllowCreate(true);
		ec.setTransactional(false);
		try {
			env = new Environment(new File(srcDir), ec);
		} catch (Exception e) {
			e.printStackTrace();
			env = null;
		}
		
		DatabaseConfig dc = new DatabaseConfig();
		dc.setAllowCreate(true);
		dc.setTransactional(false);
		
		try {
			uri2id = env.openDatabase(null, "URI2ID", dc);
			id2uri = env.openDatabase(null, "ID2URI", dc);
			counter = uri2id.count();
		} catch (DatabaseException e) {
			e.printStackTrace();
			uri2id = null;
		}
	}

	@Override
	public void install() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getSource(int id) {
		DatabaseEntry uride = new DatabaseEntry();
		DatabaseEntry idde = new DatabaseEntry();
		
		IntegerBinding.intToEntry(id, idde);
		try {
			OperationStatus os = id2uri.get(null, idde, uride, LockMode.DEFAULT);
			if (os == OperationStatus.SUCCESS)
				return StringBinding.entryToString(uride);
			else
				return null;
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public int getID(String source) {
		DatabaseEntry uride = new DatabaseEntry();
		DatabaseEntry idde = new DatabaseEntry();
		
		StringBinding.stringToEntry(source, uride);
		try {
			OperationStatus os = uri2id.get(null, uride, idde, LockMode.DEFAULT);
			if (os == OperationStatus.SUCCESS)
				return IntegerBinding.entryToInt(idde);
			else
				return -1;
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		return -1;
	}

	@Override
	public int addSource(String uri) {
		DatabaseEntry uride = new DatabaseEntry();
		DatabaseEntry idde = new DatabaseEntry();
		StringBinding.stringToEntry(uri, uride);
		IntegerBinding.intToEntry((int)(counter++), idde);
		
		try {
			uri2id.put(null, uride, idde);
			id2uri.put(null, idde, uride);
		} catch (DatabaseException e) {
			e.printStackTrace();
			return -1;
		}
		return (int)(counter - 1);
	}

	@Override
	public void close() {
		try {
			uri2id.close();
			id2uri.close();
			env.close();
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int addGetID(String source) {
		int ret = -1;
		if ((ret = getID(source)) < 0)
			ret = addSource(source);
		
		return ret;
	}

}
