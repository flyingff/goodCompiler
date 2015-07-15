package syntax.action;

import syntax.Symbol;
import syntax.V;

public class A1 extends SemanticAction {
	private static final String ARRPREFIX = "[";
	public void a0(V left, V[] right){
		left.attr("v", right[0].attr("v"));
	}
			//stmt=type,id,semi@syntax.action.A1.a1|type,arglist,],semi@syntax.action.A1.a2
			//type=int@syntax.action.A1.a3|bool@syntax.action.A1.a4|real@syntax.action.A1.a5|char@syntax.action.A1.a6
	public void a1(V left, V[] right){
		Symbol s = st.add((String)right[1].attr("value"));
		s.attr("type", right[0].attr("type"));
		System.out.println(s.toString());
	}
	public void a2(V left, V[] right){
		Symbol s = st.add(right[1].name);
		s.attr("type", ARRPREFIX + right[0].attr("type"));
		int dim = (int)right[1].attr("dim");
		s.attr("dim", dim);
		for(int i = 0; i < dim; i++){
			s.attr("d" + i, right[1].attr("d" + i));
		}
		System.out.println(s.toString());
	}
	public void a3(V left, V[] right){
		left.attr("type", right[0].attr("value"));
		System.out.println(left.toString());
	}
	public void a4(V left, V[] right){
		left.attr("type", right[0].attr("value"));
		System.out.println(left.toString());
	}
	public void a5(V left, V[] right){
		left.attr("type", right[0].attr("value"));
		System.out.println(left.toString());
	}
	public void a6(V left, V[] right){
		left.attr("type", right[0].attr("value"));
		System.out.println(left.toString());
	}
	
	//arglist=arglist,com,E@syntax.action.A1.a7|id,[,E@syntax.action.A1.a8
	//E=number@syntax.action.A1.a9
	public void a7(V left, V[] right){
		left.name = right[0].name;
		left.attr("type", right[0].attr("type"));
		int dim = (int)right[0].attr("dim");
		left.attr("dim", dim + 1);
		for(int i = 0; i < dim; i++){
			left.attr("d" + i, right[0].attr("d" + i));
		}
		left.attr("d" + dim, right[2].attr("value"));
	}
	public void a8(V left, V[] right){
		left.name = right[0].name;
		left.attr("dim", 1);
		left.attr("d" + 0, right[2].attr("value"));
	}
	public void a9(V left, V[] right){
		left.attr("value", right[0].attr("value"));
	}
	

}
