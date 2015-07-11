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
	private Map<String, Set<Production>> map = new HashMap<String, Set<Production>>();	//����ͬ�Ĳ���ʽ����
	private List<Set<Item>> itemfamily = new ArrayList<>();								//��Ŀ���淶��
	private Set<String> vset = new HashSet<>();											//���з��ŵļ���(�����ս���ͷ��ս��)
	private Set<Item> psset = new HashSet<Item>();										//������Ŀ�ļ���
	private Set<String> vnset = new HashSet<String>();									//���ս������
	private Map<String, Set<String>> firstSet = new HashMap<String, Set<String>>();		//���з��ս����FIRST����
	private Map<String, Set<String>> lastSet = new HashMap<String, Set<String>>();		//���з��ս����LAST����
	
	Map<Group, Integer> gotomap = new HashMap<Group, Integer>();						//GOTOת����
	public GrammaAnalyser(InputStream is) {
		p = new Properties();
		try {
			p.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for(Entry<Object, Object> x : p.entrySet()){									//�������Ա�
			Set<Production> xset = new HashSet<Production>();
			String left = ((String) x.getKey()).trim();									//�õ�����ʽ����
			vset.add(left);																//�����ķ����ż����ķ����ż���
			String[] ss = ((String) x.getValue()).split("\\|");							//��|��ȡÿһ�Ҳ�
			for(int i = 0;i < ss.length; i++){
				Production p = new Production();
				String[] str = ss[i].split("@");	
				String[] arr = str[0].trim().split(" *, *");							//��ȡ�Ҳ����е��ķ�����											//��ȡÿһ�Ҳ���ִ�ж����ķ�����·��
				if(arr.length == 1 && arr[0].equals("EPSLON")){
					p.setRight(new String[0]);
				} else {
					p.setRight(arr);
				}
				vset.addAll(Arrays.asList(p.getRight()));				//�����ķ����ż���
				p.setAction(str[1].trim());												//���ò���ʽ�Ķ���·��
				p.setLeft(left);														//���ò���ʽ����
				vnset.add(left);
				xset.add(p);								
			 }
			 map.put(left, xset);
			// vset.remove("EPSLON");
		}
		//System.out.println(map.toString());
		Set<Item> sItems = getItemSet(map);
		System.out.println(sItems.toString());
		System.out.println( "�ķ�����:\n" + vset.toString());
		System.out.println("���ս����:\n" + vnset.toString());
		Map<Group, Integer> mGotos = automat(sItems);
		System.out.println("GOTO��:\n" +  mGotos.toString());
		
		System.out.println(getFirstSet(map).toString());
		
	}
	public static void main(String[] args) {
		GrammaAnalyser ga = new GrammaAnalyser(GrammaAnalyser.class.getResourceAsStream("gramma.properties"));
	
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
				int len = px.getRight().length;											//�õ���ͬ�󲿵��Ҳ�����
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
					last = ps;															//������һ����Ŀ
				}
			}
		}
		//System.out.println(psset.toString());
		return psset;
	}
	
	/**
	 * ��÷��ս����First��
	 * @param map
	 * @return firstSet
	 */
	public Map<String, Set<String>> getFirstSet(Map<String, Set<Production>> map){
		Set<Map.Entry<String, Set<Production>>> entries = map.entrySet();
		for(Entry<String, Set<Production> > ex : entries){
			String vn = ex.getKey();
			firstSet.put(vn, new HashSet<String>());									//��ʼ��first��
		}
		boolean isChanged = true;
		while(isChanged){																//ѭ��ֱ�����е�first�����ٸı�
			isChanged = false;
			for(Entry<String, Set<Production> > ex : entries){							//�������еĲ���ʽ
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
	 * ����GOTOת������Զ���
	 * @param psset
	 * @return gotomap
	 */
	public Map<Group, Integer> automat(Set<Item> psset){
		
		Set<Item> begin = new HashSet<Item>();											//��ʼ��Ŀ����
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
			for(String str : vset){														//��ÿһ���ķ����Ž���GOTO����
				Set<Item> sx = getItemFamily(itemfamily.get(i), str);					//�õ���һ��״̬����Ŀ��
				if(sx.size() == 0) continue;											
					/*Set<Item> tmp = new HashSet<Item>();
					boolean isChanged = true;
					while(isChanged){
						if(!tmp.addAll(getClosure(sx))){
							isChanged = false;
						}
						sx.addAll(tmp);
					}*/
				getClosure(sx);															//�����Ŀ���ıհ�
				int to = getKey(itemfamily, sx);
				if(to <= -1){															//����Ŀ���������򴴽�������GOTOת��
					gotomap.put(new Group(i, str), itemfamily.size());
					itemfamily.add(sx);
				} else {																//����ֻ����GOTOת��������
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
					&& (ix.getPd().getRight()[ix.getDotPos()].equals(prefix))){			//��Ŀδ����ĩβ��ƥ���ǰ׺
				set.add(ix.getNext());													//����ָ�����һ����Ŀ���뼯��
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
		Set<Item> tmp = new HashSet<Item>(j);											//��ʱ����
		while(size < j.size()) {
			size = j.size();
			for(Item ix : j){
				String prefix = null;
				if(ix.getDotPos() < ix.getPd().getRight().length){			
					prefix = ix.getPd().getRight()[ix.getDotPos()];						//�õ���ǰ׺
				}
				for(Item xx : psset){
					if(xx.getPd().getLeft().equals(prefix) && xx.getDotPos() == 0){		//ƥ���󲿵�
						tmp.add(xx);
					}
				}
			}
			Set<Item> change = j;														//����j��tmp
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
		int key = -1;																	//�������򷵻�-1
		for( int i = 0; i < itemfamily.size(); i++){									//ɨ����Ŀ���б�
			if(itemfamily.get(i).equals(si)){
				key = i;
			}
		}
		return key;
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
 
/**
 * Production��
 * ��¼����ʽ��������Ϣ
 * ��������ʽ��left,����ʽ�Ҳ��ķ������б�right��ִ�ж����ķ�����·��action
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
