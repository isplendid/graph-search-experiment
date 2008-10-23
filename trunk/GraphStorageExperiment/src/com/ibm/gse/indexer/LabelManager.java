package com.ibm.gse.indexer;


public interface LabelManager {
	
	public String[] getLabel(String uri);
	public void load(InstanceKeywordRepository repos);
	public void close();
	
}
