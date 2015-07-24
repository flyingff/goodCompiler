package syntax;

import java.util.HashMap;
import java.util.Map;
/**
 * 符号类</br>
 * 每一个符号具有名字和自己的一些属性
 * @author lxm
 *
 */
public class V {
	public String name;																// 符号名	
	public boolean isFinal = true;													// 是否为终结符
	private final Map<String, Object> attr = new HashMap<String, Object>();			// 属性集合
	/**
	 * 新增一个属性
	 * @param name
	 * @param val
	 */
	public void attr(String name, Object val){ attr.put(name, val); }	
	/**
	 * 由属性名获得属性值
	 * @param name
	 * @return
	 */
	public Object attr(String name){ return attr.get(name);}						
}
