package syntax;

public class Quad {
	public static final int STARTNUM = 100;
	public int num;
	public Object[] field = new Object[4];
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
