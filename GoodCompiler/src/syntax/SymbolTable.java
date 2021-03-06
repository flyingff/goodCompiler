package syntax;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 符号表类,存放符号的信息
 * @author lxm
 *
 */
public class SymbolTable {
	private final Map<String, Symbol> syms = new HashMap<>(), funcs = new HashMap<>();		// 符号的集合
	private Map<String, Symbol> localSyms = null;											// 局部符号
	private Set<Symbol> temp = new HashSet<>(), pool = new HashSet<>();						// 临时变量池
	private static int startAddr = 10000;													// 符号表起始地址
	private static final int INTSIZE = 4, CHARSIZE = 2, REALZISE = 8, BOOLSIZE = 1;			// 定义不同类型的分配空间
	/**
	 * 在全局符号表和当前局部符号表中查看符号是否存在</br>
	 * 并返回符号信息
	 * @param name
	 * @return
	 */
	public Symbol lookup(String name){
		if(localSyms != null) {
			Symbol sx = localSyms.get(name);
			if (sx != null) return sx;
		}
		return syms.get(name);
	}
	
	/**
	 * 仅在当前局部符号表中查看符号是否存在</br>
	 * 返回符号的信息
	 * @param name
	 * @return
	 */
	public Symbol lookupLocal(String name) {
		if (localSyms != null) {
			return localSyms.get(name);
		}
		return null;
	}
	/**
	 * 增加一个符号
	 * @param name
	 * @return
	 */
	public Symbol add(String name) {
		Symbol sx = new Symbol();
		sx.name = name;
		if (localSyms == null) { 
			syms.put(name, sx);
		} else {
			localSyms.put(name, sx);
		}
		return sx;
	}
	/**
	 * 获取一个临时变量
	 * @return
	 */
	public Symbol getTemp(){
		Symbol sx;
		if (pool.isEmpty()) {
			sx = new TempSymbol();
			sx.name = "T" + temp.size();
		} else {
			Iterator<Symbol> itx = pool.iterator();
			sx = itx.next();
			itx.remove();
		}
		temp.add(sx);
		return sx;
	}
	/**
	 * 释放临时变量
	 * @param s
	 */
	public void releaseTemp(Object s) {
		if (s instanceof TempSymbol) {
			if(temp.remove(s)){
				pool.add((Symbol)s);
			}
		}
	}
	/**
	 * 判断是否为临时变量
	 * @param s
	 * @return
	 */
	public boolean isTemp(Symbol s) {
		if(pool.contains(s))
			throw new RuntimeException("Temporary Variable Leak!" + s);
		return temp.contains(s);
	}
	/**
	 * 增加一个函数,将函数作为符号存放
	 * @param name
	 * @return
	 */
	public Symbol addFunc(String name){
		Symbol sx = new Symbol();
		sx.name = name;
		funcs.put(name, sx);
		return sx;
	}
	public Symbol lookupFunc(String name){
		return funcs.get(name);
	}
	/**
	 *为函数中的局部变量开辟空间
	 */
	public void enterFunc() {
		if(localSyms == null) {
			localSyms = new HashMap<>();
		} else {
			throw new RuntimeException("Cannot define function within a function.");
		}
	}
	/**
	 * 退出函数时,回收存放数据的空间
	 * @return
	 */
	public Map<String, Symbol> exitFunc(){
		Map<String, Symbol> ret = localSyms;
		localSyms = null;
		return ret;
	}
	@SuppressWarnings("unchecked")
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Global variables: \n");
		for(Symbol sx : syms.values()) {
			sb.append("\t").append(sx.details()).append("\n");
		}
		sb.append("Temporary variables:\n");
		for(Symbol sx : pool){
			sb.append("\t").append(sx.details()).append("\n");
		}
		sb.append("Functions:\n");
		for(Symbol sx : funcs.values()) {
			sb.append('\t').append(sx.name).append(": RetType = ").append(sx.attr("type")).
				append(",BaseAddr = ").append(sx.attr("addr")).append(", [Parameters: ");
			List<Symbol> parlist = (List<Symbol>)sx.attr("parlist");
			if(parlist != null) {
				for(Symbol syx : parlist) {
					sb.append(syx.details()).append(", ");
				}
				sb.setLength(sb.length() - 2);
			} else {
				sb.append("No parameter");
			}
			sb.append("], [LocalVariables: ");
			boolean hasVariable = false;
			for(Entry<String, Symbol> ex: ((Map<String, Symbol>)sx.attr("localVar")).entrySet()) {
				sb.append(ex.getValue().details()).append(", ");
				hasVariable = true;
			}
			if(hasVariable)
				sb.setLength(sb.length() - 2);
			sb.append("]\n");
			//.append(sx.attr("parlist")).append("\n");
		}
		return sb.toString();
	}
	public void allocateAddr() {
		int allocPointer = startAddr;
		// 为临时变量分空间
	    for(Symbol sx : pool){
	    	sx.attr("addr", allocPointer);
	    	sx.attr("size", 8);
	    	allocPointer += 8;
	    }
	    // 全局变量分配空间
	    for(Entry<String, Symbol> sx : syms.entrySet()){
	    	Symbol s = sx.getValue();
	    	s.attr("addr", allocPointer);
			int num = 1;
			@SuppressWarnings("unchecked")
            List<Integer> dim = (List<Integer>)s.attr("dim");
			if(dim != null){
				for(Integer ix : dim){
					num *= ix;
				}
			}
			switch((String)s.attr("type")) {
				case "int":
					s.attr("size", INTSIZE * num);
					allocPointer += INTSIZE * num; 
				break;
				case "char":
					s.attr("size", CHARSIZE * num);
					allocPointer += CHARSIZE * num; 
				break;
				case "bool":
					s.attr("size", BOOLSIZE * num);
					allocPointer += BOOLSIZE * num; 
				break;
				case "real":
					s.attr("size", REALZISE * num);
					allocPointer += REALZISE * num; 
				break;
				default:
					throw new RuntimeException("wrong type of symbol: " + s.name + " type :" + s.attr("type"));
			}
	    }
    }
}

class TempSymbol extends Symbol{
	@Override
	public String toString() {
		return "$" + name;
	}
}