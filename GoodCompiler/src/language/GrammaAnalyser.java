package language;

import java.awt.event.ItemEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

public class GrammaAnalyser {
	private Properties p;
	private Map<String, Set<Production>> map = new HashMap<String, Set<Production>>();
	public GrammaAnalyser(InputStream is) {
		p = new Properties();
		try {
			p.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for(Entry<Object, Object> x : p.entrySet()){
			Set<Production> xset = new HashSet<Production>();
			String left = ((String) x.getKey()).trim();
			 String[] ss = ((String) x.getValue()).split("\\|");
			 for(int i = 0;i < ss.length; i++){
				 Production p = new Production();
				 String[] str = ss[i].split("@");
				 p.setRight(str[0].trim().split(" *, *"));
				 p.setAction(str[1].trim());
				 p.setLeft(left);
				 xset.add(p);
				 
			 }
			 map.put(left, xset);
		}
		//System.out.println(map.toString());
		ProSetFamily(map);
	}
	public static void main(String[] args) {
		GrammaAnalyser ga = new GrammaAnalyser(GrammaAnalyser.class.getResourceAsStream("gramma.properties"));
	}
	public void ProSetFamily(Map<String, Set<Production>> map){
		Set<Item> psset = new HashSet<Item>();
		for(Entry<String, Set<Production>> ex : map.entrySet()){
			String left = ex.getKey();
			for(Production px : ex.getValue()){
				int pos = 0;
				int len = px.getRight().length;
				while(pos < len + 1){
					Item ps = new Item();
					ps.setDotPos(pos);
					ps.setPd(px);
					psset.add(ps);
					pos++;
				}
			}
		}
		System.out.println(psset.toString());
	}
	public void automat(Set<Item> psset){
		Map<Integer, Set<Item> > itemfamily = new HashMap<Integer, Set<Item>>();
		Set<Goto> gotoset = new HashSet<Goto>();
		int index = 0;
		
		for(Item psx : psset){
			if(psx.getPd().getLeft() == "start" && psx.getDotPos() == 0){
				Set<Item> begin = new HashSet<Item>();
				begin.add(psx);
				String prefix = psx.getPd().getRight()[0];
				begin = getItemFamily(psset, prefix);
				itemfamily.put(index++, begin);
				}
			}
		for(Entry<Integer, Set<Item>> x : itemfamily.entrySet()){
			
		}
		
		}
	public  Set<Item> getItemFamily(Set<Item> psset, String prefix){
		Set<Item> set = new HashSet<Item>();
		for(Item ps : psset){
			if(ps.getPd().getLeft().equals(prefix) && ps.getDotPos() == 0){
				set.add(ps);
			}
			if(((ps.getDotPos() < ps.getPd().getRight().length) && ps.getDotPos() > 0) &&
					ps.getPd().getRight()[ps.getDotPos()].equals(prefix)){
				set.add(ps);
			}
		}
		return set;
	}
}

class Goto{
	private int from;
	private String via;
	private int to;
}

class  Item{
	private Production pd;
	private int dotPos;
	
	public int getDotPos() {
		return dotPos;
	}
	public void setDotPos(int dotPos) {
		this.dotPos = dotPos;
	}
	public Production getPd() {
		return pd;
	}
	public void setPd(Production pd) {
		this.pd = pd;
	}
	@Override
	public String toString() {
		return "\nProjectSet [pd=" + pd + ", dotPos=" + dotPos + "]";
	}
	@Override
	public int hashCode() {
		return pd.hashCode() << 4 + dotPos;
	}
}
 
class Production{
	private String left;
	private String[] right;
	private String action;
	public String[] getRight() {
		return right;
	}
	public void setRight(String[] right) {
		this.right = right;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	@Override 
	public String toString() {
		return "Production [left=" + left + ", right=" + Arrays.toString(right)
				+ ", action=" + action + "]";
	}
	public String getLeft() {
		return left;
	}
	public void setLeft(String left) {
		this.left = left;
	}
}
