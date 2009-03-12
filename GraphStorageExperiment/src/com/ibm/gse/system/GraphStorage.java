package com.ibm.gse.system;

import com.ibm.gse.config.Configuration;
import com.ibm.gse.config.FileConfig;
import com.ibm.gse.hash.ModHash;
import com.ibm.gse.metadata.IndexManager;
import com.ibm.gse.metadata.PatternManager;
import com.ibm.gse.pattern.HashingPatternCodec;
import com.ibm.gse.storage.map.ColumnNodeMap;
import com.ibm.gse.storage.map.LexicoColumnNodeMap;

public class GraphStorage {
	public static Configuration config = new FileConfig("config");
//	public static PatternManager patternMan;
	public static PatternManager patternMan = new PatternManager(new HashingPatternCodec(new ModHash()));
	public static IndexManager indexMan = new IndexManager(config.getIntegerSetting("PatternLength", 0));
	public static ColumnNodeMap columnNodeMap = new LexicoColumnNodeMap();
}
