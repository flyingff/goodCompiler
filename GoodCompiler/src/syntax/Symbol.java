package syntax;

import java.util.HashMap;
import java.util.Map;

public class Symbol {
	public String name;
	private final Map<String, Object> attr = new HashMap<String, Object>();
	public void attr(String name, Object val){ attr.put(name, val); }
	public Object attr(String name){ return attr.get(name);}
	@Override
	public String toString() {
		return "[" + name + ": " + attr.toString() + "]";
	}
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Symbol) {
			if (name!= null) {
				return name.equals(((Symbol) obj).name);
			} else {
				return ((Symbol) obj).name == null;
			}
		}
		return false;
	}
}
