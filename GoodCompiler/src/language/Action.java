package language;

/**
 * Action��
 * ������Ҫִ�еĶ���
 * @author lxm
 *
 */
public class Action {
	public static final int REDUCTION = 1, STEPINTO = 2, GOTO = 3, ACC = 4;
	private static final String[] TYPENAME = new String[]{"ERR-0", "Reduction", "StepInto", "Goto", "Accept"};
	private	int type;																	//��������:�ƽ�S,��ԼR�ͽ���acc��
	private int state = -1;																	//��һ��״̬
	private Production p = null;														//���˲���ʽ��Լ
	public Action() {
		type = ACC;
	}
	public Action(Production p) {
	    this.p = p;
	    type = REDUCTION;
    }
	public Action(int type, int state) {
	    this.type = type;
	    this.state = state;
	    p = null;
    }
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public Production getP() {
		return p;
	}
	public void setP(Production p) {
		this.p = p;
	}
	@Override
	public String toString() {
		switch(type) {
		case REDUCTION:
			return "[Reduction p=" + p.toString() + "]" ;
		case ACC:
			return "[Acc]";
		default:
			return "["+TYPENAME[type] + " target=" + state + "]";
		} 
	}
}
