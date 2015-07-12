package syntax;

import java.util.HashMap;
import java.util.Map;

public class V {
	public String name;
	public boolean isFinal = true;
	private final Map<String, Object> attr = new HashMap<String, Object>();
	public void attr(String name, Object val){ attr.put(name, val); }
	public Object attr(String name){ return attr.get(name);}
}
