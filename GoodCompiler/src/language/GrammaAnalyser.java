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
	private static final String EPSLON = "_", TERMINATOR = "#", START = "S";
	private Properties p;
	private Map<String, Set<Production>> map = new HashMap<String, Set<Production>>();	// ����ͬ�Ĳ���ʽ����
	private List<Set<Item>> itemfamily = new ArrayList<>();								// ��Ŀ���淶��
	private Set<String> vset = new HashSet<>();											// ���з��ŵļ���(�����ս���ͷ��ս��)
	private Set<Item> psset = new HashSet<Item>();										// ������Ŀ�ļ���
	private Set<String> vnset = new HashSet<String>();									// ���ս������
	private Map<String, Set<String>> firstSet = new HashMap<String, Set<String>>();		// ���з��ս����FIRST����
	private Map<String, Set<String>> followSet = new HashMap<String, Set<String>>();		// ���з��ս����FOLLOW����
	private Map<Group, Integer> gotomap = new HashMap<Group, Integer>();				// GOTOת����
	
	public GrammaAnalyser(InputStream is) {
		p = new Properties();
		try {
			p.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for(Entry<Object, Object> x : p.entrySet()){									// �������Ա�
			Set<Production> xset = new HashSet<Production>();
			String left = ((String) x.getKey()).trim();									// �õ�����ʽ����
			vset.add(left);																// �����ķ����ż����ķ����ż���
			String[] ss = ((String) x.getValue()).split("\\|");							// ��|��ȡÿһ�Ҳ�
			for(int i = 0;i < ss.length; i++){
				Production p = new Production();
				String[] str = ss[i].split("@");	
				String[] arr = str[0].trim().split(" *, *");							// ��ȡ�Ҳ����е��ķ�����											//  ��ȡÿһ�Ҳ���ִ�ж����ķ�����·��
				if(arr.length == 1 && arr[0].equals(EPSLON)){
					p.setRight(new String[0]);
				} else {
					p.setRight(arr);
				}
				vset.addAll(Arrays.asList(p.getRight()));								// �����ķ����ż���
				p.setAction(str[1].trim());												// ���ò���ʽ�Ķ���·��
				p.setLeft(left);														// ���ò���ʽ����
				vnset.add(left);
				xset.add(p);								
			 }
			 map.put(left, xset);
			//   vset.remove(EPSLON);
		}
		//  System.out.println(map.toString());
		Set<Item> sItems = getItemSet(map);
		System.out.println(sItems.toString());
		System.out.println( "�ķ�����:\n" + vset.toString());
		System.out.println("���ս����:\n" + vnset.toString());
		Map<Group, Integer> mGotos = automat(sItems);
		System.out.println("GOTO��:\n" +  mGotos.toString());
		
		System.out.println("FIRST SET:" + getFirstSet(map).toString());
		System.out.println("FOLLOW SET:" + getFollowSet(map).toString());
		AnalyzeTable at = constructor();
		at.show();
		
	}
	public static void main(String[] args) {
		new GrammaAnalyser(GrammaAnalyser.class.getResourceAsStream("gramma.properties"));
	}
	/**
	 * ������Ŀ��
	 * @param map
	 * @return psset
	 */
	public Set<Item> getItemSet(Map<String, Set<Production>> map){
		for(Entry<String, Set<Production>> ex : map.entrySet()){
			for(Production px : ex.getValue()){
				int pos = 0;
				int len = px.getRight().length;											// �õ���ͬ�󲿵��Ҳ�����
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
					last = ps;															// ������һ����Ŀ
				}
			}
		}
		//  System.out.println(psset.toString());
		return psset;
	}
	
	/**
	 * ��÷��ս����First��
	 * @param pros
	 * @return firstSet
	 */
	public Map<String, Set<String>> getFirstSet(Map<String, Set<Production>> pros){
		Set<Map.Entry<String, Set<Production>>> entries = pros.entrySet();
		for(Entry<String, Set<Production> > ex : entries){
			String vn = ex.getKey();
			firstSet.put(vn, new HashSet<String>());										// ��ʼ��first��
		}
		boolean isChanged = true;
		while(isChanged){																	// ѭ��ֱ�����е�first�����ٸı�
			isChanged = false;																// ���first���Ƿ�仯
			for(Entry<String, Set<Production> > ex : entries){								// �������еĲ���ʽ
				String left = ex.getKey();
				Set<String> currFirstSet = firstSet.get(left);
				int beginSize = currFirstSet.size();
				for(Production p : ex.getValue()){
					if(p.getRight().length != 0){
						String tmp = p.getRight()[0];
						if(vnset.contains(tmp)){											// ����ʽ�Ҳ���һ�������Ƿ��ս��
							currFirstSet.addAll(firstSet.get(tmp));							// ���˷��ս����first�����뵱ǰ��first��
							if(firstSet.get(tmp).contains(EPSLON)){							// ���ս����first������EPSLON
								int len = p.getRight().length;
								int i = 1;
								//   �ж����ķ����Ƿ�Ϊ���ս������first���Ƿ���EPSLON
								while(i < len &&
										firstSet.get(p.getRight()[i - 1]).contains(EPSLON)){
									if(vnset.contains(p.getRight()[i])){					// �Ƿ��ս��
										currFirstSet.addAll(firstSet.get(p.getRight()[i]));	// ����first������
										i++;
									} else {
										currFirstSet.add(p.getRight()[i]);					// ���ս������
										break;
									}
								}
								if(i == len													// ��֮ǰ���з��ž�Ϊ���ս��
									&& (firstSet.get(p.getRight()[i - 1]).contains(EPSLON))){// ���һ��������first������EPSLON
									currFirstSet.add(EPSLON);								// ��EPSLON���뵱ǰfirst��
								} else {
									currFirstSet.remove(EPSLON);							// �����first����ȥ��EPSLON
								}
							}
						} else {
							currFirstSet.add(tmp);
						}
					} else {
						currFirstSet.add(EPSLON);
					}
				}
				isChanged = isChanged || currFirstSet.size() > beginSize;
			}
		}
		return firstSet;
	}
	
	/**
	 * ��÷��ս����follow��
	 * @param map
	 * @return followSet
	 */
	public Map<String, Set<String>> getFollowSet(Map<String, Set<Production>> map){
		Set<Map.Entry<String, Set<Production>>> entries = map.entrySet();
		for(Entry<String, Set<Production>> ex : entries){
			String vn = ex.getKey();
			followSet.put(vn, new HashSet<String>());												// ��ʼ��follow��
			if (vn.equals(START)){
				followSet.get(vn).add(TERMINATOR);
			}
		}
		boolean isChanged = true;
		while(isChanged){
			isChanged = false;																	//���follow���Ƿ�仯
			for(Entry<String, Set<Production>> ex : entries){
				String left = ex.getKey();
				for(Production p : ex.getValue()){
					String[] right = p.getRight();
					for(int i = 0; i < right.length; i++){
						String tmp = right[i];													// ȡ������ʽ�Ҳ���i������
						if(vnset.contains(tmp)){	
							Set<String> follow = followSet.get(tmp);
							int followsize = follow.size();
							if(i < right.length - 1){											// ��Ϊ���ս���Ҳ����Ҳ����һ������
								if(!vnset.contains(right[i + 1])){								// ����һ���������ս��
									follow.add(right[i + 1]);										// ���������ս����follow��
								} else {
									follow.addAll(firstSet.get(right[i + 1]));					// ���������һ�����ս����first��
									follow.remove(EPSLON);										// ��ȥ��epslon
									int x = i + 1; 
									while(x < right.length - 1){								// �ж�֮���Ƿ��з��ս��
										if(vnset.contains(right[x]) 							
											&& firstSet.get(right[x]).contains(EPSLON)){		// ������first������EPSLON
											if(vnset.contains(right[x + 1])){					// ����first�����뵱ǰ��follow��
												follow.addAll(firstSet.get(right[x + 1]));
												x ++;
											} else {
												follow.add(right[x]);								// �����ս�����뵱ǰ��follow��
												follow.remove(EPSLON);							// ��ȥ��epslon
												break;
											}
										}
									}
									if(x == right.length - 1 && vnset.contains(right[x])){
										follow.addAll(followSet.get(left));
										follow.remove(EPSLON);
									}
								}
							}
							if(vnset.contains(tmp) && i == right.length - 1){
								follow.addAll(followSet.get(left));
							}
							isChanged = isChanged || follow.size() > followsize;
						}
					}
				}
			}
		}
			return followSet;
	}

	/**
	 * ����GOTO״̬ת�����Զ���
	 * @param psset
	 * @return gotomap
	 */
	public Map<Group, Integer> automat(Set<Item> psset){
		
		Set<Item> begin = new HashSet<Item>();											// ��ʼ��Ŀ����
		for(Item psx : psset){
			if(psx.getPd().getLeft().equals(START) && psx.getDotPos() == 0){
				begin.add(psx);
			}
		}
		getClosure(begin);
		itemfamily.add(begin);
		for(int i = 0; i < itemfamily.size(); i++){
			//  System.out.println( " index :" + i + ", itemset: " + itemfamily.get(i).toString());
			for(String str : vset){														// ��ÿһ���ķ����Ž���GOTO����
				Set<Item> sx = getItemFamily(itemfamily.get(i), str);					// �õ���һ��״̬����Ŀ��
				if(sx.size() == 0) continue;											
				getClosure(sx);															// �����Ŀ���ıհ�
				int to = getKey(itemfamily, sx);
				if(to <= -1){															// ����Ŀ���������򴴽�������GOTOת��
					gotomap.put(new Group(i, str), itemfamily.size());
					itemfamily.add(sx);
				} else {																// ����ֻ����GOTOת��������
					gotomap.put(new Group(i, str), to);
				}
			}
		}
		System.out.println("��Ŀ���淶�弯��: \n" + itemfamily.toString());
		return gotomap;
	}
	
	/**
	 * ������Ŀ��
	 * @param j
	 * @param prefix
	 * @return set
	 */
	public Set<Item> getItemFamily(Set<Item> j, String prefix){
		Set<Item> set = new HashSet<Item>();
		for(Item ix : j){
			if((ix.getDotPos() < (ix.getPd().getRight().length))
					&& (ix.getPd().getRight()[ix.getDotPos()].equals(prefix))){			// ��Ŀδ����ĩβ��ƥ���ǰ׺
				set.add(ix.getNext());													// ����ָ�����һ����Ŀ���뼯��
			}
		}
		return set;
	} 
	
	/**
	 * ������Ŀ��j��CLOSURE�հ�
	 * @param j
	 * @return j
	 */
	public Set<Item> getClosure(Set<Item> j){
		int size = 0;
		Set<Item> tmp = new HashSet<Item>(j);											// ��ʱ����
		while(size < j.size()) {
			size = j.size();
			for(Item ix : j){
				String prefix = null;
				if(ix.getDotPos() < ix.getPd().getRight().length){			
					prefix = ix.getPd().getRight()[ix.getDotPos()];						// �õ���ǰ׺
				}
				for(Item xx : psset){
					if(xx.getPd().getLeft().equals(prefix) && xx.getDotPos() == 0){		// ƥ���󲿵�
						tmp.add(xx);
					}
				}
			}
			Set<Item> change = j;														// ����j��tmp
			j = tmp;
			tmp = change;
		}
		return j;
	}
	
	/**
	 * ����Ŀ�������Ӧ���
	 * @param itemfamily
	 * @param si
	 * @return key
	 */
	private int getKey(List<Set<Item> > itemfamily, Set<Item> si){
		int key = -1;																	// �������򷵻�-1
		for( int i = 0; i < itemfamily.size(); i++){									// ɨ����Ŀ���б�
			if(itemfamily.get(i).equals(si)){
				key = i;
			}
		}
		return key;
	}

	//
	private AnalyzeTable constructor(){
		Map<Group, Action> table = new HashMap<>();
		for(Entry<Group, Integer> ex : gotomap.entrySet()){
			Group g = ex.getKey();
			int currState = g.getFrom();
			String via = g.getVia();
			int nextState = ex.getValue(); 
			Set<Item> from = itemfamily.get(currState);
			for(Item ix : from){
				int dotpos = ix.getDotPos();
				int len = ix.getPd().getRight().length;
				if(dotpos < len){
					if(vnset.contains(ix.getPd().getRight()[dotpos])){
						Action a = new Action(Action.GOTO, nextState);
						table.put(g, a);
					} else {
						Action a = new Action(Action.STEPINTO, nextState);
						table.put(g, a);
					}
				} else {
					if(ix.getPd().getLeft().equals(START)){
						Action a = new Action();
						table.put(g, a);
					}else {
						Set<String> follow = followSet.get(ix.getPd().getLeft());
						for(String x : follow){
							Group gx = new Group(currState, x);
							Action a = new Action(ix.getPd());
							table.put(gx, a);
						}
					}
				}
			}
		}
		return new AnalyzeTable(table);
	}
}





/**
 * group��
 * ��¼��һ��״̬�;��ɵ��ķ�����
 * from��ʾ״̬���
 * via��ʾ�ķ�����
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
 * ��Ŀ��
 * ��¼����ʽpd,Բ��λ��dotPos����һ����Ŀnext
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
				sb.append("��");
			sb.append(pd.getRight()[i]);
		}
		if (dotPos == pd.getRight().length){
			sb.append("��");
		}
		return sb.toString();
	}
	@Override
	public int hashCode() {
		return pd.hashCode() << 4 + dotPos;
	}
}
