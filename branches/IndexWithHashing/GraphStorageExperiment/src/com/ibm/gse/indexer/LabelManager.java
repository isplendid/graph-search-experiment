package com.ibm.gse.indexer;

import java.util.ArrayList;


public interface LabelManager {
	
	public String[] getLabel(String uri);
	public void load(InstanceKeywordRepository repos);
	public void close();
	
}
