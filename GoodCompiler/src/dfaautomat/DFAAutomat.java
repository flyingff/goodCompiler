package dfaautomat;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * DFAAutomat��,
 * DFA�Զ���,ʵ��Serializable�ӿ�,�����浽�ļ���
 * @author lxm
 *
 */
public class DFAAutomat implements Serializable{
	public static final char EPSLON = '\0';												//EPSLON��ʾ���ַ�
	private static final long serialVersionUID = -4274687974256578446L;					//���к�ID
	private State startState;															//��ʼ״̬
	private Map<Group, State> converts;													//״̬ת����
	private Set<State> finalStates;														//��̬��
	private transient State currState;													//��¼��ǰ״̬(��̬,�����ļ��в�����)
	
	private DFAAutomat(State startState,
			Map<Group, State> converts, Set<State> finalStates) {
		this.startState = startState;
		this.converts = converts;
		this.finalStates = finalStates;
		reset();
	}
	
	/**
	 * �����Զ���
	 */
	public void reset(){
		currState = startState;
	}
	
	/**
	 * ����ת��������һ״̬
	 * @param input
	 * @return currState
	 */
	public State next(char input){
		return currState = converts.get(Group.getComparer(currState, input));
	}
	public State next(String input){
		for (char ch: input.toCharArray()) {
			next(ch);
		}
		return currState;
	}
	
	/**
	 * �ж��Ƿ񵽴���̬
	 * @param s
	 * @return
	 */
	public boolean isFinal(State s){
		return finalStates.contains(s);
	}
	
	public boolean isFinal(){
		return finalStates.contains(currState);
	}
	
	/**
	 * �������д���ļ�
	 * @param os
	 */
	public void saveTo(OutputStream os){
		try {
			ObjectOutputStream oos = new ObjectOutputStream(os);
			oos.writeObject(this);														//���ļ�д�����
			oos.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ���ļ��м��������
	 * @param is
	 * @return ret
	 */
	public static DFAAutomat load(InputStream is) {
		DFAAutomat ret = null;
		try {
			ObjectInputStream ois = new ObjectInputStream(is);
			ret = (DFAAutomat)ois.readObject();											//��ȡ����
			ret.reset();
			ois.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	/**
	 * �����ڲ���,�����Զ���
	 * @return
	 */
	public static DFAConstructor constructor(){
		return new DFAConstructor();
	}
	/**
	 * �ڲ���
	 * �����Զ���
	 * @author lxm
	 *
	 */
	public static class DFAConstructor {
		private Map<Group, State> map = new HashMap<Group, State>();					//ת����
		private Set<State> finals = new HashSet<State>();								//��̬��
		private State begin;															//��ʼ״̬
	
		private DFAConstructor() {}
		
		/**
		 * ����һ����
		 * @param from
		 * @param via
		 * @param to
		 */
		public void edge(State from, char via, State to){
			map.put(new Group(from, via), to);
		}
		public void finals(State... s) {
			finals.addAll(Arrays.asList(s));
		}
		public void begin(State s) {
			begin = s;
		}
		public DFAAutomat finish(){
			return new DFAAutomat(begin, map, finals);
		}
	}
	
	/**
	 * �����ڲ���NFAConstructor,����NFA
	 * @return
	 */
	public static NFAConstructor constructorN(){
		return new NFAConstructor();
	}
	/**
	 * �ڲ���
	 * NFA�Զ���
	 * @author lxm
	 *
	 */
	public static class NFAConstructor {
		private Map<Group, Set<State>> map = new HashMap<Group, Set<State>>();			//ת����
		private Set<State> finals = new HashSet<State>();								//��̬��
		private Set<State> begin = new HashSet<State>();								//��ʼ״̬��
		private Set<Character> alpha = new HashSet<Character>();						//��ĸ��

		private NFAConstructor() {}
		
		/**
		 * ����һ����
		 * @param from
		 * @param via
		 * @param to
		 */
		public void edge(State from, char via, State... to){
			if(via != EPSLON){
				alpha.add(via);
			}
			Set<State> mSet = map.get(Group.getComparer(from, via));
			if(mSet == null){
				mSet = new HashSet<State>();
				map.put(new Group(from, via), mSet);
			}
			mSet.addAll(Arrays.asList(to));
		}
		public void finals(State... s) {
			finals.addAll(Arrays.asList(s));
		}
		public void begin(State... s) {
			begin.addAll(Arrays.asList(s));
		}
		
		/**
		 * ��NFAת��ΪDFA
		 * @return
		 */
		public DFAAutomat finish(){
			State begin0 = null;
			Map<Group, State> map0 = new HashMap<Group, State>();
			Set<State> finals0 = new HashSet<State>();
			Map<Set<State>,Integer> clMap = new HashMap<Set<State>,Integer>();
			Set<State> s = new HashSet<State>();
			s.addAll(begin);
			List<State> sl = new ArrayList<State>();
			Set<State> bg = closure(s);
			clMap.put(bg, 0);
			sl.add(new State());
			List<Set<State>> il = new ArrayList<Set<State>>();
			il.add(bg);
			for(int i = 0; i < sl.size(); i ++){
				for(char chx : alpha){
					Set<State> tmp;
					if((tmp = closure(il.get(i), chx)).size() != 0){
						Integer to = clMap.get(tmp);
						if(to == null){
							to = sl.size();
							clMap.put(tmp, to);
							sl.add(new State());
							il.add(tmp);
						}
						map0.put(new Group(sl.get(i) , chx),sl.get(to));
					}
				}
			}
			for(int i = 0; i < sl.size(); i++){
				boolean isFinal = false;
				State x = sl.get(i);
				for(State sx : il.get(i)){
					isFinal = isFinal || finals.contains(sx);
					if (x.priority < sx.priority) {	
						x.priority = sx.priority;
						x.type = sx.type;
					}
				}
				if (isFinal)
					finals0.add(x);
			}
			begin0 = sl.get(0);
			return new DFAAutomat(begin0, map0, finals0);
		}
		/**
		 * ��EPSLON�հ�
		 * @param set
		 * @return
		 */
		private Set<State> closure(Set<State> set){
			return closure(set, EPSLON);
		}
		private Set<State> closure(Set<State> set, char ch) {
			Set<State> ret = new HashSet<State>();
			Set<State> tmp = new HashSet<State>();
			for(State sx : set) {
				Set<State> snext;
				if ((snext = map.get(Group.getComparer(sx, ch))) != null) {
					ret.addAll(snext);
				}
			}
			// calculate epslon closure
			boolean change = true;
			while(change) {
				change = false;
				tmp.clear();
				tmp.addAll(ret);
				for(State sx : tmp) {
					Set<State> snext;
					if ((snext = map.get(Group.getComparer(sx, EPSLON))) != null) {
						change = ret.addAll(snext) || change;
					}
				}
			}
			return ret;
		}
	}
}

/**
 * Group��
 * ��¼ǰһ״̬�������ַ�
 * @author lxm
 *
 */
class Group implements Serializable{
	private static final long serialVersionUID = -6497235445783214316L;
	private static Group BUF = new Group(null, (char)0);
	public static Group getComparer(State s, char ch) { 
		BUF.state = s;
		BUF.ch = ch;
		return BUF;
	}
	private State state;
	private char ch;
	
	public Group(State state, char ch) {
		this.state = state;
		this.ch = ch;
	}
	@Override
	public boolean equals(Object o) {
		if (o instanceof Group){
			return ((Group) o).state == state && ch == ((Group) o).ch;
		}
		return false;
	}
	@Override
	public int hashCode() {
		return (state == null? 0 : state.hashCode() << 8) + ch;
	}
}