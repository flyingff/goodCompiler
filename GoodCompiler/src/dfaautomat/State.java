package dfaautomat;

import java.io.Serializable;

/**
 * State��
 * ��¼״̬������type�����ȼ�priority
 * @author lxm
 *
 */
public class State implements Serializable{
	private static int x = 0;
	private static final long serialVersionUID = 7360128750927748735L;					//���к�ID
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
