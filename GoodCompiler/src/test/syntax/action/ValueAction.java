package test.syntax.action;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

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
	// 值 = 左值
	public void a5(V left, V[] right){
		Symbol s = (Symbol)right[0].attr("value");
		Object offset = right[0].attr("offset");
		if (offset != null) {
			Symbol tmp = st.getTemp();
			newQuad().field("=[]", s.name + "[" + offset + "]", null, tmp);
			st.releaseTemp(offset);
			left.attr("value", tmp);
		} else {
			left.attr("value", s);
		}
		left.attr("type", s.attr("type"));
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
		Object val = right[2].attr("value");
		Symbol leftv = (Symbol) right[0].attr("value");
		Object offset = leftv.attr("offset");
		qx.field(offset == null? ":=": "[]=", val, null, offset == null?leftv : leftv.name + "[" + offset + "]");
		st.releaseTemp(offset);
		left.attr("value", val);
		left.attr("type", ((Symbol)leftv).attr("type"));
	}
	// 求值语句=值,semi
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
	// 左值 = 数组访问参数表,]
	public void a13(V left, V[] right){
		Symbol s = (Symbol)right[0].attr("value");
		int currdim =(Integer)right[0].attr("currdim");
		if(currdim != ((List<?>)s.attr("dim")).size()) {
			throw new RuntimeException("Array dimension mismatch with " + s.name + ": " + currdim);
		}
		left.attr("value", s);
		left.attr("offset", right[0].attr("offset"));
		left.attr("type", s.attr("type"));
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
		int currdim = (Integer)right[0].attr("currdim") + 1;
		Object offset = right[0].attr("offset"), dimx = right[2].attr("value");
		Symbol s = (Symbol) right[0].attr("value");
		List<Integer> dim = ((List<Integer>)s.attr("dim"));
		if(dim.size() < currdim) {
			throw new RuntimeException("Array Dimension out of range at " + s + ": " + currdim);
		}
		if (REAL.equals(right[2].attr("type"))) throw new RuntimeException("Array dimension cannot be a real number:" + dimx);
		Symbol tmp = st.getTemp(), tmp2 = st.getTemp();
		newQuad().field("*", dim.get(currdim - 2), offset, tmp2);
		newQuad().field("+", tmp2, dimx, tmp);
		st.releaseTemp(dimx);
		st.releaseTemp(tmp2);
		st.releaseTemp(offset);
		left.attr("offset", tmp);
		left.attr("currdim", currdim);
		left.attr("value", s);
		left.attr("type", s.attr("type"));
	}
	//函数调用=id,(,实参列表,)@test.syntax.action.ValueAction.a16
	public void a16(V left, V[] right){
		String name = (String)right[0].attr("value");
		Symbol func = st.lookupFunc(name);
		if(func == null) {
			throw new RuntimeException("Undefined function: " + name);
		}
		List<Object> paras = (List<Object>) right[2].attr("paras");
		if(paras != null){
			for(Object ox : paras){
				Quad qx = newQuad();
				qx.field("par", null, null, ox);
				if(ox instanceof Symbol && st.isTemp((Symbol)ox)){
					st.releaseTemp((Symbol)ox);
				}
			}
		}
		newQuad().field("call", null, null, func);
	}
	// 实参列表=实参@test.syntax.action.ValueAction.a17
	public void a17(V left, V[] right){
		List<Object> paras = new ArrayList<>();
		left.attr("paras", paras);
		paras.add(right[0].attr("value"));
	}
	// 实参列表=实参列表,com,实参@test.syntax.action.ValueAction.a18
	public void a18(V left, V[] right) {
		List<Object> paras = (List<Object>)right[0].attr("paras");
		if(paras == null) {
			throw new RuntimeException("Lack of Parameter before ','.");
		}
		paras.add(right[2].attr("value"));
		left.attr("paras", paras);
	}
	// 实参=值@test.syntax.action.ValueAction.a20
	public void a20(V left, V[] right){
		left.attr("value", right[0].attr("value"));
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
}