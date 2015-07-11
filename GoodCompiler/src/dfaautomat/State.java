package dfaautomat;

import java.io.Serializable;

/**
 * State类
 * 记录状态的类型type和优先级priority
 * @author lxm
 *
 */
public class State implements Serializable{
	private static int x = 0;
	private static final long serialVersionUID = 7360128750927748735L;					//序列号ID
	public int priority = -1;
	public String type = null;
	private int id;
	public State() {
		id = x++;
	}
	@Override
	public int hashCode() {
		return id;
	}
}
