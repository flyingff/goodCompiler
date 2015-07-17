package test.syntax.action;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;

import syntax.Quad;
import syntax.Symbol;
import syntax.V;
import syntax.action.SemanticAction;

/**
 * 值的属性为: value - Integer, Double或Symbol, 代表当前符号的值<br>
 * type - String,可能为INT, REAL, CHAR, BOOL
 * @author FlyingFlameR
 *
 */
public class ValueAction extends SemanticAction{
	public static final String INT = "int", REAL = "real", CHAR = "char", BOOL="bool";
	private static final String[] PREC = new String[]{BOOL, CHAR, INT, REAL};
	public void a1(V left, V[] right){
		String val = (String) right[0].attr("value");
		if (val.indexOf('.') != -1) {
			// int
			left.attr("value",Integer.parseInt(val));
			left.attr("type", INT);
		} else{
			// real
			left.attr("value", Double.parseDouble(val));
			left.attr("type", REAL);
		}
	}
	// pass function
	public void pass(V left, V[] right){
		left.attr("value", right[1].attr("value"));
		left.attr("type", right[1].attr("type"));
	}
	// 自增值=++,左值
	public void a6(V left, V[] right){
		// TODO consider array
		Symbol sx = (Symbol)right[1].attr("value"), temp = st.getTemp();
		Quad qx = newQuad();
		qx.field("+", sx, 1, temp);
		qx = newQuad();
		qx.field(":=", temp, null, sx);
		st.releaseTemp(temp);
		
		left.attr("type", sx.attr("type"));
		left.attr("value", sx);
	}
	
	// 自增值=左值,++
	public void a7(V left, V[] right){
		// TODO consider array
		Symbol sx = (Symbol)right[1].attr("value"), temp = st.getTemp(), temp2 = st.getTemp();
		Quad qx = newQuad();
		qx.field(":=", sx, null, temp2);
		qx = newQuad();
		qx.field("+", sx, 1, temp);
		qx = newQuad();
		qx.field(":=", temp, null, sx);
		st.releaseTemp(temp);
		
		left.attr("type", sx.attr("type"));
		left.attr("value", temp2);
	}
	//乘积值 = 乘积值,*,自增值
	public void a8(V left, V[] right){
		Object p1 = right[0].attr("value"), p2 = right[2].attr("value");
		Symbol temp = st.getTemp();
		Quad qx = newQuad();
		qx.field("*", p1, p2, temp);
		if (p1 instanceof Symbol && st.isTemp((Symbol)p1)) {
			st.releaseTemp((Symbol)p1);
		}
		left.attr("value", temp);
		left.attr("type", getResultType((String)right[0].attr("type"), (String)right[2].attr("type")));
	}
	//加值 = 加值,+,乘积值
	public void a9(V left, V[] right){
		Object p1 = right[0].attr("value"), p2 = right[2].attr("value");
		Symbol temp = st.getTemp();
		Quad qx = newQuad();
		qx.field("+", p1, p2, temp);
		if (p1 instanceof Symbol && st.isTemp((Symbol)p1)) {
			st.releaseTemp((Symbol)p1);
		}
		left.attr("value", temp);
		left.attr("type", getResultType((String)right[0].attr("type"), (String)right[2].attr("type")));
	}
	// = 左值,ass,加值
	public void a10(V left, V[] right){
		Quad qx = newQuad();
		Object val, leftv;
		qx.field(":=", val = right[2].attr("value"), null, leftv = right[0].attr("value"));
		if (val instanceof Symbol && st.isTemp((Symbol)val)) {
			st.releaseTemp((Symbol)val);
		}
		// TODO consider array
		left.attr("value", leftv);
		left.attr("type", ((Symbol)leftv).attr("type"));
	}
	// = 求值语句=值,semi
	public void a11(V left, V[] right){
		Object val = right[0].attr("value");
		if (val instanceof Symbol && st.isTemp((Symbol)val)) {
			st.releaseTemp((Symbol)val);
		}
	}
	// 左值 = id
	public void a12(V left, V[] right){
		Symbol sx = st.lookup((String) right[0].attr("value"));
		if (sx == null) {
			throw new RuntimeException("Symbol Undefined:" + (String) right[0].attr("value"));
		}
		left.attr("type", sx.attr("type"));
		left.attr("value", sx);
	}
	// 数组访问参数表,]
	public void a13(V left, V[] right){
		
	}
	// 数组访问参数表 = id,[,数组访问参数
	public void a14(V left, V[] right){
		Symbol sx = st.lookup((String) right[0].attr("value"));
		if (sx == null) {
			throw new RuntimeException("Symbol Undefined: " + (String) right[0].attr("value"));
		}
		if (sx.attr("dim") == null){
			throw new RuntimeException("Symbol is not an array: " + (String) right[0].attr("value"));
		}
		left.attr("value", sx);
		left.attr("currdim", 1);
		String type = (String) right[2].attr("type");
		if (type.equals(REAL)) throw new RuntimeException("Array dimension cannot be a real number:" + right[2].attr("value"));
		left.attr("offset", right[2].attr("value"));
	}
	// 数组访问参数表 = 数组访问参数表,com,数组访问参数
	public void a15(V left, V[] right){
		int currdim = (Integer)right[0].attr("currdim");
		Object offset = (Integer)right[0].attr("offset");
		
	}
	public void a16(V left, V[] right){
		
	}
	public void a17(V left, V[] right){
		
	}
	public void a18(V left, V[] right){
		
	}
	public String getResultType(String t1, String t2){
		int p1 = -1, p2 = -1;
		for(int i = 0; i < 10; i++) {
			if (PREC[i].equals(t1)) {
				p1 = i;
			}
			if (PREC[i].equals(t2)) {
				p2 = i;
			}
		}
		if (p1 == -1 || p2 == -1) throw new RuntimeException("Type error:" + t1 + ", " + t2);
		return p1 > p2? PREC[p1] : PREC[p2];
	}
	public static void main(String[] args) {
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread arg0, Throwable arg1) {
				System.err.println(arg1.getMessage());
			}
		});
		
		Integer.parseInt("129812698123512347");
	}
}
