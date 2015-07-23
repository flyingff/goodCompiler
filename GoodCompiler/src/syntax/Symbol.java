package syntax;

import java.util.HashMap;
import java.util.Map;

/**
 * 符号表类，存放符号
 * @author lxm
 *
 */
public class Symbol {
	public String name;														// 符号名
	private final Map<String, Object> attr = new HashMap<String, Object>();	// 属性集合
	/**
	 * 添加属性
	 * @param name
	 * @param val
	 */
	public void attr(String name, Object val){ attr.put(name, val); }
	/**
	 * 返回属性名相对应的值
	 * @param name
	 * @return
	 */
	public Object attr(String name){ return attr.get(name);}
	@Override
	public String toString() {
		return name;
	}
	public String details(){
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
