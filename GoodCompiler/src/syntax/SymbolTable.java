package syntax;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class SymbolTable {
	private final Map<String, Symbol> syms = new HashMap<>(), funcs = new HashMap<>();
	private Set<Symbol> temp = new HashSet<>(), pool = new HashSet<>();
	public Symbol lookup(String name){
		return syms.get(name);
	}
	public Symbol add(String name){
		Symbol sx = new Symbol();
		sx.name = name;
		syms.put(name, sx);
		return sx;
	}
	public Symbol getTemp(){
		Symbol sx;
		if (pool.isEmpty()) {
			sx = new Symbol();
			sx.name = "T" + temp.size();
		} else {
			Iterator<Symbol> itx = pool.iterator();
			sx = itx.next();
			itx.remove();
		}
		temp.add(sx);
		return sx;
	}
	public void releaseTemp(Object s) {
		if (s instanceof Symbol) {
			if(temp.remove(s)){
				pool.add((Symbol)s);
			}
		}
	}
	public boolean isTemp(Symbol s) {
		if(pool.contains(s))
			throw new RuntimeException("Temporary Variable Leak!" + s);
		return temp.contains(s);
	}
	public Symbol addFunc(String name){
		Symbol sx = new Symbol();
		sx.name = name;
		funcs.put(name, sx);
		return sx;
	}
	public Symbol lookupFunc(String name){
		return funcs.get(name);
	}
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for(Symbol sx : syms.values()) {
			sb.append(sx).append("\n");
		}
		sb.append("Functions:\n");
		for(Symbol sx : funcs.values()) {
			sb.append(sx).append("\n");
		}
		return sb.toString();
	}
}