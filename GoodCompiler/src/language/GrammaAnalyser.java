package language;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
		Set<Item> sItems = itemSetFamily(map);
		//System.out.println(sItems.toString());
		Map<Group, Integer> mGotos = automat(sItems);
		//System.out.println(mGotos.toString());
		
	}
	public static void main(String[] args) {
		GrammaAnalyser ga = new GrammaAnalyser(GrammaAnalyser.class.getResourceAsStream("gramma.properties"));
	}
	public Set<Item> itemSetFamily(Map<String, Set<Production>> map){
		Set<Item> psset = new HashSet<Item>();
		for(Entry<String, Set<Production>> ex : map.entrySet()){
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
		//System.out.println(psset.toString());
		return psset;
	}
	//得到GOTO转换表
	private List<Set<Item>> itemfamily = new ArrayList<>();
	private Set<String> vnset = new HashSet<>();
	public Map<Group, Integer> automat(Set<Item> psset){
		
		Map<Group, Integer> gotomap =new HashMap<Group, Integer>();
		
		Set<Item> begin = new HashSet<Item>();
		for(Item psx : psset){
			if(psx.getPd().getLeft().equals( "start") && psx.getDotPos() == 0){
				Set<Item> addItems =new HashSet<Item>();
				addItems.add(psx);
				while(begin.addAll(addItems)){
					for(Item x : begin){
						
						String left = x.getPd().getLeft();
						if(x.getDotPos()<x.getPd().getRight().length)
							{String string = x.getPd().getRight()[x.getDotPos()];
							System.out.println("pre:" + string);
							System.out.println(getItemFamily(psset,left, string));
							addItems.addAll(getItemFamily(psset,left, string));
						}
					}
				}
			}
		}
		itemfamily.add(begin);
		System.out.println("begin: " +begin);
		for(int i =0 ; i <itemfamily.size(); i++){
			int from = i;
			for(Item ix : itemfamily.get(i)){
				if(ix.getDotPos() != ix.getPd().getRight().length) {
					String left = ix.getPd().getLeft();
					String prefix = ix.getPd().getRight()[ix.getDotPos()];
					Set<Item> to = getItemFamily(psset,left, prefix);
					if(!itemfamily.contains(to)){
						itemfamily.add(to);
						gotomap.put(new Group(from, prefix), itemfamily.size());
					} else {
						int ito = getKey(itemfamily, to);
							gotomap.put(new Group(from, prefix), ito);
						}
					}
					
				}
			}
		for(int i = 0; i < itemfamily.size(); i++){
			//System.out.println( " index :" + i + ", itemset: " + itemfamily.get(i).toString());
		}
		return gotomap;
		}
	
	//得到项目集
	public  Set<Item> getItemFamily(Set<Item> psset, String left, String prefix){
		Set<Item> set = new HashSet<Item>();
		for(Item ps : psset){
			if(ps.getPd().getLeft().equals(prefix) && ps.getDotPos() == 0){
				set.add(ps);
			}
			if(((ps.getDotPos() <= ps.getPd().getRight().length) && ps.getDotPos() > 0) &&
				ps.getPd().getLeft().equals(left) && ps.getPd().getRight()[ps.getDotPos() - 1].equals(prefix)){
				set.add(ps);
			}
		}
		return set;
	} 
	//由项目集获得编号
	private int getKey(List<Set<Item> > itemfamily, Set<Item> si){
		int key = 0;
		for( int i = 0; i < itemfamily.size(); i++){
			if(itemfamily.get(i).equals(si)){
				key = i;
			}
		}
		return key;
	}
}

//GOTO转换表
class Group{
	private int from;
	private String via;
	public int getFrom() {
		return from;
	}
	public void setFrom(int from) {
		this.from = from;
	}
	public String getVia() {
		return via;
	}
	public void setVia(String via) {
		this.via = via;
	}
	public Group(int from, String via) {
	    super();
	    this.from = from;
	    this.via = via;
    }
	@Override
    public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + from;
	    result = prime * result + ((via == null) ? 0 : via.hashCode());
	    return result;
    }
	@Override
    public boolean equals(Object obj) {
		if(obj instanceof Group){
			if(((Group) obj).from == this.from  && ((Group) obj).via == this.via){
				return true;
			}
		}
	    return false;
    }
	@Override
    public String toString() {
	    return "\nGroup [from=" + from + ", via=" + via + "]";
    }
}

class  Item{
	private Production pd;
	private int dotPos;
	private Item next;
	public void setNext(Item next) {
	    this.next = next;
    }
	public Item getNext() {
	    return next;
    }
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
