package test.syntax.action;

import java.util.ArrayList;
import java.util.List;

import syntax.Symbol;
import syntax.V;
import syntax.action.SemanticAction;

public class Vstmt extends SemanticAction {
	
	public void a1(V left, V[] right){
		String type = (String) right[0].attr("type");
		left.attr("type", type);
		Symbol s = (Symbol) right[1].attr("symbol");
		s.attr("type", type);
	}
	public void a2(V left, V[] right){
		String type = (String) right[0].attr("type");
		left.attr("type", type);
		Symbol s = (Symbol) right[2].attr("symbol");
		s.attr("type", type);
	}
	public void a3(V left, V[] right){
		left.attr("type", right[0].attr("value"));
	}
	/**
	 * 声明语句=类型,声明元@test.syntax.action.Vstmt.a1|声明语句,com,声明元@test.syntax.action.Vstmt.a2
类型=int@test.syntax.action.Vstmt.a3|bool@test.syntax.action.Vstmt.a3|char@test.syntax.action.Vstmt.a3|real@test.syntax.action.Vstmt.a3
声明元=id@test.syntax.action.Vstmt.a4|数组参数表,]@test.syntax.action.Vstmt.a5
数组参数表=id,[,值@test.syntax.action.Vstmt.a6|数组参数表,com,值@test.syntax.action.Vstmt.a7
值=number@test.syntax.action.Vstmt.a8
	 */
	public void a4(V left, V[] right){
		String name = (String) right[0].attr("value");
		if (st.lookup(name) != null) {
			throw new RuntimeException("Duplicate variable: " + name);
		}
		Symbol s = st.add(name);
		left.attr("symbol", s);
	}
	public void a5(V left, V[] right){
		left.attr("symbol", right[0].attr("symbol"));
	}
	public void a6(V left, V[] right){
		String name = (String) right[0].attr("value");
		if (st.lookup(name) != null) {
			throw new RuntimeException("Duplicate variable: " + name);
		}
		Symbol s = st.add(name);
		left.attr("symbol", s);
		List<Integer> dim;
		s.attr("dim", dim = new ArrayList<Integer>());
		String x = (String)right[2].attr("value");
		if (x.indexOf('.')!= -1) {
			throw new RuntimeException("Array dimension can only be integer: " + x);
		}
		dim.add(Integer.parseInt(x));
	}
	
	@SuppressWarnings("unchecked")
	public void a7(V left, V[] right){
		List<Integer> dim = (List<Integer>)(((Symbol)(right[0].attr("symbol"))).attr("dim"));
		String x = (String)right[2].attr("value");
		if (x.indexOf('.')!= -1) {
			throw new RuntimeException("Array dimension can only be integer: " + x);
		}
		dim.add(Integer.parseInt(x));
		left.attr("symbol", right[0].attr("symbol"));
	}
}
