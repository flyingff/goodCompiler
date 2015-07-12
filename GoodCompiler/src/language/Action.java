package language;

/**
 * Action类
 * 分析表将要执行的动作
 * @author lxm
 *
 */
public class Action {
	public static final int REDUCTION = 1, STEPINTO = 2, GOTO = 3, ACC = 4;
	private static final String[] TYPENAME = new String[]{"ERR-0", "Reduction", "StepInto", "Goto", "Accept"};
	private	int type;																	//动作类型:移进S,规约R和接受acc等
	private int state = -1;																	//下一个状态
	private Production p = null;														//按此产生式规约
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
