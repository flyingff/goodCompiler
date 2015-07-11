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

/**
 * LexicalAnalyzer接口实现类
 * 将输入程序识别为单词符号串
 * @author lxm
 *
 */
public class LexicalAnalyzerImpl implements LexicalAnalyzer {
	private static final Set<Character> BLANKCH = new HashSet<Character>(Arrays.asList(' ','\t','\n','\r'));//空白字符集
	private BufferedReader br;															//从输入流读取字符的缓冲区
	private int buf = -1;																//缓冲区
	private DFAAutomat dfa;																//DFA自动机对象
	private StringBuffer sb = new StringBuffer();										//保存未识别完的输入字符串
	
	/**
	 * 获得DFA对象
	 * @param is
	 */
	public LexicalAnalyzerImpl(InputStream is) {
		dfa = DFAAutomat.load(is);
	}
	@Override
	public void load(InputStream is) {
		 br = new BufferedReader(new InputStreamReader(is));
	}

	@Override
	public V next() {
		if(br == null){	throw new RuntimeException("please load file first !");	}		//读取文件失败
		
		V v = new V();
		//boolean isOver = false;
		
		sb.setLength(0);																//清空字符串缓冲区
		dfa.reset();																	//重置DFA
		
		State curr = null;																//记录当前状态
		State pre = null;																//记录前一状态
		
		try {
			// find a non-blank char
			char r = getChar();
			while(BLANKCH.contains(r)) {												//找到非空字符
				r = getChar();
			}
			// enable circle	
			do {																		//开始循环
				pre = curr;
				curr = dfa.next(r);
				if(curr != null){
					sb.append(r);
					r = getChar();
				}
			} while(curr != null);
			buf = r;																	//保存多读的字符
			if(dfa.isFinal(pre)){
				v.name = pre.type;
				v.attr("value", sb.toString());
			} else {
				throw new RuntimeException("lexical error!" + sb.toString());
			}
		}catch (FileEndException e){													//读到文件末尾
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
	/**
	 * 读取一个字符
	 * @return ch
	 * @throws IOException
	 */
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
