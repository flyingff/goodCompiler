package language;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * SLR(1)分析表,记录当前状态,输入符号和执行动作
 * @author lxm
 *
 */
public class AnalyzeTable {
	private Map<Group, Action> analyzeTable = new HashMap<Group, Action>();				//分析表map
	AnalyzeTable(Map<Group, Action> analyzeTable) {
	    this.analyzeTable = analyzeTable;
    }
	public static AnalyzeTable load(InputStream is){
		return null;
	}
	public void save(OutputStream os){
		
	}
	public void show() {
		for(Entry<Group, Action> ex : analyzeTable.entrySet()){
			System.out.println("from :" + ex.getKey().getFrom() + " via: " + ex.getKey().getVia() + 
				" Action: " + ex.getValue());
		}
	}

}