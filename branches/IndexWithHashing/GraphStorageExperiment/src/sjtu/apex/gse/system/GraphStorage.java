package sjtu.apex.gse.system;

import sjtu.apex.gse.config.Configuration;
import sjtu.apex.gse.config.FileConfig;
import sjtu.apex.gse.hash.ModHash;
import sjtu.apex.gse.metadata.IndexManager;
import sjtu.apex.gse.metadata.PatternManager;
import sjtu.apex.gse.pattern.HashingPatternCodec;
import sjtu.apex.gse.planner.Planner;
import sjtu.apex.gse.planner.dp.DynamicProgrammingPlanner;
import sjtu.apex.gse.storage.map.ColumnNodeMap;
import sjtu.apex.gse.storage.map.HashLexicoColumnNodeMap;


public class GraphStorage {
	public static Configuration config = new FileConfig("sys-cfg");
//	public static PatternManager patternMan;
	public static PatternManager patternMan = new PatternManager(new HashingPatternCodec(new ModHash()));
	public static IndexManager indexMan = new IndexManager(config.getIntegerSetting("PatternLength", 0));
	public static ColumnNodeMap columnNodeMap = new HashLexicoColumnNodeMap();
	public static Planner queryPlanner = new DynamicProgrammingPlanner();
}
