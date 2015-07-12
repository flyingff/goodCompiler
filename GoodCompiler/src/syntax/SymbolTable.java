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
}

class Symbol {
	public String name;
	private final Map<String, Object> attr = new HashMap<String, Object>();
	public void attr(String name, Object val){ attr.put(name, val); }
	public Object attr(String name){ return attr.get(name);}
}