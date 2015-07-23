package language;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * SLR(1)分析表,记录当前状态,输入符号和执行动作
 * @author lxm
 *
 */
public class AnalyzeTable implements Serializable{
    private static final long serialVersionUID = 8106377333742804836L;
	private Map<Group, Action> analyzeTable = new HashMap<Group, Action>();				// 分析表map
	private Group gQuery = new Group(0, null);
	private Set<String> vtset = null;									// 
	AnalyzeTable(Map<Group, Action> analyzeTable) {
	    this.analyzeTable = analyzeTable;
    }
	public void setVT(Set<String> vt) {
		this.vtset = vt;
	}
	public Set<String> getVT(){
		return vtset;
	}
	
	public Action query(int state, String input) {
		gQuery.setFrom(state);
		gQuery.setVia(input);
		return analyzeTable.get(gQuery);
	}
	public static AnalyzeTable load(InputStream is){
		AnalyzeTable table = null;
		try {
			ObjectInputStream ois = new ObjectInputStream(is);
			table = (AnalyzeTable) ois.readObject();
			ois.close();
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		return table;
	}
	public void save(OutputStream os){
		try {
			ObjectOutputStream oos = new ObjectOutputStream(os);
			oos.writeObject(this);
			oos.close();
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	public void show() {
		for(Entry<Group, Action> ex : analyzeTable.entrySet()){
			System.out.println("from: " + ex.getKey().getFrom() + 
					" via: " + ex.getKey().getVia() + " Action: " + ex.getValue());
		}
	}

}