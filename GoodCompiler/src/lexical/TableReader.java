package lexical;

import static dfaautomat.DFAAutomat.EPSLON;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.EmptyStackException;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Stack;

import dfaautomat.DFAAutomat;
import dfaautomat.DFAAutomat.NFAConstructor;
import dfaautomat.State;

public class TableReader {
	private DFAAutomat mat;
	private Properties p;
	private int priority(char ch){
		switch(ch) {
		case ')':
			return 1;
		case '.':
			return 2;
		case '|':
			return 3;
		case '*':
		case '+':
			return 4;
		case '(':
			return 5;
		}
		return -1;
	}
	
	public TableReader(InputStream is) {
		p = new Properties();
		try {
			p.load(is);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		Stack<Character> sop = new Stack<Character>();
		Stack<SubMat> snum = new Stack<SubMat>();
		NFAConstructor c = DFAAutomat.constructorN();
		State s = new State();
		c.begin(s);
		for(Entry<Object, Object> ex : p.entrySet()) {
			String key = (String) ex.getKey();
			int len = key.length();
			for(int i = 0; i < len; i++) {
				char chx = key.charAt(i);
				switch (chx) {
				case '(':
				case ')':
				case '*':
				case '+':
				case '|':
				case '.':
					while(!sop.isEmpty() && priority(sop.peek()) > priority(chx)){
						// pop and calculate 
						if (!calc(sop, snum, c)) break;
					}
					if(chx != ')')
						sop.push(chx);
					else if (sop.pop() != '(') {
						throw new RuntimeException("Too many right brackets.");
					}
					break;
				case '\\':
					chx = key.charAt(++i);
				default:
					SubMat tmp = new SubMat();
					tmp.start = new State();
					tmp.end = new State();
					c.edge(tmp.start, chx, tmp.end);
					snum.push(tmp);
					break;
				}
			}
			
			while(!sop.isEmpty()) {
				try {
					if (!calc(sop, snum, c))
						throw new RuntimeException("Too maly left bracket at " + key);
				}catch (EmptyStackException e) {
					throw new RuntimeException("Lack of Operand at " + key);
				}
			}
			if (snum.size() != 1) {
				throw new RuntimeException("Lack of Operator at " + key);
			}
			SubMat sm = snum.pop();
			c.edge(s, EPSLON, sm.start);
			c.finals(sm.end);
			String[] val = ((String) ex.getValue()).split(",");
			sm.end.priority = Integer.parseInt(val[0].trim());
			sm.end.type = val[1].trim();
		}
		mat = c.finish();
	}
	public DFAAutomat getMat() {
		return mat;
	}
	private boolean calc(Stack<Character> sop, Stack<SubMat> snum, NFAConstructor c) {
		char opx = sop.pop();
		SubMat p1, p2, tmp;
		State s1, s2;
		switch (opx) {
		case '(':
			sop.push('(');
			return false;
		case '*': 
			p1 = snum.pop();
			s1 = new State();
			s2 = new State();
			c.edge(s1, EPSLON, s2);
			c.edge(s2, EPSLON, s1);
			c.edge(s1, EPSLON, p1.start);
			c.edge(p1.end, EPSLON, s2);
			tmp = new SubMat();
			tmp.start = s1;
			tmp.end = s2;
			snum.push(tmp);
			break;
		case '+':
			p1 = snum.pop();
			s1 = new State();
			s2 = new State();
			c.edge(s2, EPSLON, s1);
			c.edge(s1, EPSLON, p1.start);
			c.edge(p1.end, EPSLON, s2);
			tmp = new SubMat();
			tmp.start = s1;
			tmp.end = s2;
			snum.push(tmp);
			break;
		case '|':
			p1 = snum.pop();
			p2 = snum.pop();
			s1 = new State();
			s2 = new State();
			c.edge(s1, EPSLON, p1.start);
			c.edge(s1, EPSLON, p2.start);
			c.edge(p1.end, EPSLON, s2);
			c.edge(p2.end, EPSLON, s2);
			tmp = new SubMat();
			tmp.start = s1;
			tmp.end = s2;
			snum.push(tmp);
			break;
		case '.':
			p1 = snum.pop();
			p2 = snum.pop();
			c.edge(p2.end, EPSLON, p1.start);
			tmp = new SubMat();
			tmp.start = p2.start;
			tmp.end = p1.end;
			snum.push(tmp);
			break;
		default:
			throw new RuntimeException("" + opx);
		}
		return true;
	}
	
	public static void main(String[] args) throws IOException{
		TableReader tr = new TableReader(TableReader.class.getResourceAsStream("wordlist.properties"));
		DFAAutomat df = tr.getMat();
		//df.next("++");
		//System.out.println(df.isFinal());
		df.saveTo(new FileOutputStream("e:\\mat1.automat"));
	}
}

class SubMat {
	public State start, end;
	@Override
	public String toString() {
		return "[" + start.hashCode() + ", " + end.hashCode() + "]";
	}
}
