package lexical;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import dfaautomat.DFAAutomat;
import dfaautomat.State;
import syntax.V;

public class LexicalAnalyzerImpl implements LexicalAnalyzer {
	private static final Set<Character> BLANKCH = new HashSet<Character>(Arrays.asList(' ','\t','\n','\r'));
	private BufferedReader br;
	private int buf = -1;
	private DFAAutomat dfa;
	private StringBuffer sb = new StringBuffer();
	public LexicalAnalyzerImpl(InputStream is) {
		dfa = DFAAutomat.load(is);
	}
	@Override
	public void load(InputStream is) {
		 br = new BufferedReader(new InputStreamReader(is));
	}

	@Override
	public V next() {
		if(br == null){	throw new RuntimeException("please load file first !");	}
		
		V v = new V();
		//boolean isOver = false;
		
		sb.setLength(0);
		dfa.reset();
		
		State curr = null;
		State pre = null;
		
		try {
			// find a non-blank char
			char r = getChar();
			while(BLANKCH.contains(r)) {
				r = getChar();
			}
			// enable circle
			do {
				pre = curr;
				curr = dfa.next(r);
				if(curr != null){
					sb.append(r);
					r = getChar();
				}
			} while(curr != null);
			buf = r;
			if(dfa.isFinal(pre)){
				v.name = pre.type;
				v.attr("value", sb.toString());
			} else {
				throw new RuntimeException("lexical error!" + sb.toString());
			}
		}catch (FileEndException e){
			if (sb.length() > 0) {
				if(dfa.isFinal(curr)){
					v.name = pre.type;
					v.attr("value", sb.toString());
				} else {
					throw new RuntimeException("lexical error!" + sb.toString());
				}
			} else 
				return null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return v;
	}
	private char getChar() throws IOException{
		char ch = 0;
		if(buf != -1){
			ch = (char) buf;
			buf = -1;
		} else {
			int read;
			if((read = br.read()) > -1){
				ch = (char) read;
			} else {
				throw new FileEndException();
			}
		}
		return ch;
	}
	public void close(){
		try {
			br.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}

class FileEndException extends RuntimeException {
	private static final long serialVersionUID = -4700426986853540136L;
}
