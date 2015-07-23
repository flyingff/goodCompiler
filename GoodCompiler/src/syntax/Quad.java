package syntax;

/**
 * 四元式类
 * @author lxm
 *
 */
public class Quad {
	public static final int STARTNUM = 100;					// 四元式起始编号
	public int num;											// 相对编号
	public Object[] field = new Object[4];					// 四元式的四个域
	/**
	 * 产生新的四元式并对其赋值
	 * @param paras
	 */
	public void field(Object... paras) {
		int i = 0;
		for(; i < paras.length && i < 4; i++) {
			field[i] = paras[i];
		}
		for(; i < 4; i++) { 
			field[i] = null;
		}
	}
	@Override
	public String toString() {
		return num + " (" + cast(field[0]) + ", " + cast(field[1]) + ", " + cast(field[2]) + ", " + cast(field[3]) + ");";
	}
	private final String cast(Object ox) {
		return ox == null? "_" : ox.toString();
	}
}
