package syntax;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
	private final Map<String, Symbol> syms = new HashMap<String, Symbol>();
	public Symbol find(String name){
		return syms.get(name);
	}
	public Symbol add(String name){
		Symbol sx = new Symbol();
		sx.name = name;
		syms.put(name, sx);
		return sx;
	}
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for(Symbol sx : syms.values()) {
			sb.append(sx).append("\n");
		}
		return sb.toString();
	}
}