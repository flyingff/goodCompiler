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
	private Map<String, Set<Production>> map = new HashMap<String, Set<Production>>();	//左部相同的产生式集合
	private List<Set<Item>> itemfamily = new ArrayList<>();								//项目集规范族
	private Set<String> vset = new HashSet<>();											//所有符号的集合(包括终结符和非终结符)
	private Set<Item> psset = new HashSet<Item>();										//所有项目的集合
	private Set<String> vnset = new HashSet<String>();									//非终结符集合
	private Map<String, Set<String>> firstSet = new HashMap<String, Set<String>>();		//所有非终结符的FIRST集合
	private Map<String, Set<String>> lastSet = new HashMap<String, Set<String>>();		//所有非终结符的LAST集合
	
	Map<Group, Integer> gotomap = new HashMap<Group, Integer>();						//GOTO转换表
	public GrammaAnalyser(InputStream is) {
		p = new Properties();
		try {
			p.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for(Entry<Object, Object> x : p.entrySet()){									//遍历属性表
			Set<Production> xset = new HashSet<Production>();
			String left = ((String) x.getKey()).trim();									//得到产生式的左部
			vset.add(left);																//将左部文法符号加入文法符号集合
			String[] ss = ((String) x.getValue()).split("\\|");							//用|提取每一右部
			for(int i = 0;i < ss.length; i++){
				Production p = new Production();
				String[] str = ss[i].split("@");	
				String[] arr = str[0].trim().split(" *, *");							//提取右部所有的文法符号											//提取每一右部的执行动作的方法的路径
				if(arr.length == 1 && arr[0].equals("EPSLON")){
					p.setRight(new String[0]);
				} else {
					p.setRight(arr);
				}
				vset.addAll(Arrays.asList(p.getRight()));				//加入文法符号集合
				p.setAction(str[1].trim());												//设置产生式的动作路径
				p.setLeft(left);														//设置产生式的左部
				vnset.add(left);
				xset.add(p);								
			 }
			 map.put(left, xset);
			// vset.remove("EPSLON");
		}
		//System.out.println(map.toString());
		Set<Item> sItems = getItemSet(map);
		System.out.println(sItems.toString());
		System.out.println( "文法符号:\n" + vset.toString());
		System.out.println("非终结符集:\n" + vnset.toString());
		Map<Group, Integer> mGotos = automat(sItems);
		System.out.println("GOTO表:\n" +  mGotos.toString());
		
		System.out.println(getFirstSet(map).toString());
		
	}
	public static void main(String[] args) {
		GrammaAnalyser ga = new GrammaAnalyser(GrammaAnalyser.class.getResourceAsStream("gramma.properties"));
	
	}
	/**
	 * 返回项目集
	 * @param map
	 * @return psset
	 */
	public Set<Item> getItemSet(Map<String, Set<Production>> map){
		for(Entry<String, Set<Production>> ex : map.entrySet()){
			for(Production px : ex.getValue()){
				int pos = 0;
				int len = px.getRight().length;											//得到相同左部的右部总数
				Item last = null;														
				while(pos < len + 1){
					Item ps = new Item();
					ps.setDotPos(pos);
					ps.setPd(px);
					psset.add(ps);
					pos++;
					if (last != null) {
						last.setNext(ps);												
					}
					last = ps;															//保存上一个项目
				}
			}
		}
		//System.out.println(psset.toString());
		return psset;
	}
	
	/**
	 * 获得非终结符的First集
	 * @param map
	 * @return firstSet
	 */
	public Map<String, Set<String>> getFirstSet(Map<String, Set<Production>> map){
		Set<Map.Entry<String, Set<Production>>> entries = map.entrySet();
		for(Entry<String, Set<Production> > ex : entries){
			String vn = ex.getKey();
			firstSet.put(vn, new HashSet<String>());									//初始化first集
		}
		boolean isChanged = true;
		while(isChanged){																//循环直到所有的first集不再改变
			isChanged = false;
			for(Entry<String, Set<Production> > ex : entries){							//遍历所有的产生式
				for(Production p : ex.getValue()){
					String left = ex.getKey();
					if(p.getRight().length != 0){
						String tmp = p.getRight()[0];
						if(vnset.contains(tmp)){
							if(firstSet.get(tmp).contains("EPSLON")){
								firstSet.get(left).addAll(firstSet.get(tmp));
								int len = p.getRight().length;
								int i = 1;
								while( i < len &&
										(firstSet.get(p.getRight()[i - 1]).contains("EPSLON"))){
									if( vnset.contains(p.getRight()[i])){
										isChanged = firstSet.get(left).addAll(firstSet.get(p.getRight()[i])) || isChanged;
										i++;
									} else {
										isChanged = firstSet.get(left).add(p.getRight()[i]) || isChanged;
										break;
									}
								}
								if(i == len - 1 && (firstSet.get(p.getRight()[i]).contains("EPSLON"))){
									isChanged = firstSet.get(left).add("EPSLON") || isChanged;
								}
							} else {
								isChanged = firstSet.get(left).addAll(firstSet.get(tmp))|| isChanged;
							}
						} else {
							isChanged =  firstSet.get(left).add(tmp) || isChanged;
						}
					} else {
						isChanged = firstSet.get(left).add("EPSLON") || isChanged;
					}
				}
			}
		}
		
		return firstSet;
	}
	
	public Map<String, Set<String>> getLastSet(Map<String, Set<Production>> map){
		Set<Map.Entry<String, Set<Production>>> entries = map.entrySet();
		return lastSet;
	}

	/**
	 * 构造GOTO转换表的自动机
	 * @param psset
	 * @return gotomap
	 */
	public Map<Group, Integer> automat(Set<Item> psset){
		
		Set<Item> begin = new HashSet<Item>();											//初始项目集合
		for(Item psx : psset){
			if(psx.getPd().getLeft().equals("start") && psx.getDotPos() == 0){
				//Set<Item> addItems = new HashSet<Item>();
				//addItems.add(psx);
				//while(begin.addAll(addItems)){
				//	addItems.addAll(getClosure(psset, begin));
				//}
				begin.add(psx);
			}
		}
		getClosure(begin);
		itemfamily.add(begin);
		//System.out.println("begin: " +begin);
		//for(int i = 0 ; i < itemfamily.size(); i++){
		//	int from = i;
		//}
		for(int i = 0; i < itemfamily.size(); i++){
			//System.out.println( " index :" + i + ", itemset: " + itemfamily.get(i).toString());
			for(String str : vset){														//对每一个文法符号进行GOTO动作
				Set<Item> sx = getItemFamily(itemfamily.get(i), str);					//得到下一个状态的项目集
				if(sx.size() == 0) continue;											
					/*Set<Item> tmp = new HashSet<Item>();
					boolean isChanged = true;
					while(isChanged){
						if(!tmp.addAll(getClosure(sx))){
							isChanged = false;
						}
						sx.addAll(tmp);
					}*/
				getClosure(sx);															//求该项目集的闭包
				int to = getKey(itemfamily, sx);
				if(to <= -1){															//该项目集不存在则创建并产生GOTO转换
					gotomap.put(new Group(i, str), itemfamily.size());
					itemfamily.add(sx);
				} else {																//否则只产生GOTO转换不创建
					gotomap.put(new Group(i, str), to);
				}
			}
		}
		System.out.println("项目集规范族集合: \n" + itemfamily.toString());
		return gotomap;
	}
	
	/**
	 * 返回项目集
	 * @param j
	 * @param prefix
	 * @return set
	 */
	public Set<Item> getItemFamily(Set<Item> j, String prefix){
		Set<Item> set = new HashSet<Item>();
		for(Item ix : j){
			if((ix.getDotPos() < (ix.getPd().getRight().length))
					&& (ix.getPd().getRight()[ix.getDotPos()].equals(prefix))){			//项目未到达末尾且匹配活前缀
				set.add(ix.getNext());													//将其指向的下一个项目加入集合
			}
		}
		return set;
	} 
	
	/**
	 * 返回项目集j的CLOSURE闭包
	 * @param j
	 * @return j
	 */
	public Set<Item> getClosure(Set<Item> j){
		int size = 0;
		Set<Item> tmp = new HashSet<Item>(j);											//临时变量
		while(size < j.size()) {
			size = j.size();
			for(Item ix : j){
				String prefix = null;
				if(ix.getDotPos() < ix.getPd().getRight().length){			
					prefix = ix.getPd().getRight()[ix.getDotPos()];						//得到活前缀
				}
				for(Item xx : psset){
					if(xx.getPd().getLeft().equals(prefix) && xx.getDotPos() == 0){		//匹配左部等
						tmp.add(xx);
					}
				}
			}
			Set<Item> change = j;														//交换j和tmp
			j = tmp;
			tmp = change;
		}
		return j;
	}
	
	/**
	 * 由项目集获得相应编号
	 * @param itemfamily
	 * @param si
	 * @return key
	 */
	private int getKey(List<Set<Item> > itemfamily, Set<Item> si){
		int key = -1;																	//不存在则返回-1
		for( int i = 0; i < itemfamily.size(); i++){									//扫描项目集列表
			if(itemfamily.get(i).equals(si)){
				key = i;
			}
		}
		return key;
	}
}

/**
 * group类
 * 记录上一个状态和经由的文法符号
 * from表示状态编号
 * via表示文法符号
 * @author lxm
 *
 */
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

/**
 * 项目类
 * 记录产生式pd,圆点位置dotPos和下一个项目next
 * @author lxm
 *
 */
class  Item{
	private Production pd;
	private int dotPos;
	private Item next = null;
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
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		sb.append(pd.getLeft()).append("-->");
		for(int i = 0; i < pd.getRight().length; i++) {
			if (i != 0)
				sb.append(" ");
			if (dotPos == i)
				sb.append("·");
			sb.append(pd.getRight()[i]);
		}
		if (dotPos == pd.getRight().length){
			sb.append("·");
		}
		return sb.toString();
	}
	@Override
	public int hashCode() {
		return pd.hashCode() << 4 + dotPos;
	}
}
 
/**
 * Production类
 * 记录产生式的所有信息
 * 包括产生式左部left,产生式右部文法符号列表right和执行动作的方法的路径action
 * @author lxm
 *
 */
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
		return "\n" + left + "-->" + Arrays.toString(right)
				+ "@" + action;
	}
	public String getLeft() {
		return left;
	}
	public void setLeft(String left) {
		this.left = left;
	}
}
