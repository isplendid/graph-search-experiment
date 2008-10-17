package com.ibm.gse.indexer;

import java.sql.ResultSet;

public interface StorageReader {
	
	public ResultSet getSQLResultSet(String string);
	public void closeStat();
	public void close();
	
}