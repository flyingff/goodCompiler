package syntax;

public class Quad {
	public static final int STARTNUM = 100;
	public int num;
	public Object[] field = new Object[4];
	@Override
	public String toString() {
		return num + " (" + field[0] + ", " + field[1] + ", " + field[2] + ", " + field[3] + ");";
	}
}
