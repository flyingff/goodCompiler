package syntax.action;

import syntax.V;

public class A1 extends SemanticAction {
	public void a0(V left, V[] right){
		left.attr("v", right[0].attr("v"));
		
	}
			stmt=type,id,semi@syntax.action.A1.a1|type,arglist,],semi@syntax.action.A1.a2
			type=int@syntax.action.A1.a3|bool@syntax.action.A1.a4|real@syntax.action.A1.a5|char@syntax.action.A1.a6
			arglist=arglist,com,E@syntax.action.A1.a7|id,[,E@syntax.action.A1.a8
			E=number@syntax.action.A1.a9
	public void a1(V left, V[] right){
		right[1].attr("type", right[0].attr(right[0].name));
		 st.add(right[1].name);
	}
	public void a2(V left, V[] right){
		
	}
	public void a3(V left, V[] right){
	
	}
	public void a4(V left, V[] right){
	
	}
	public void a5(V left, V[] right){
	
	}
	public void a6(V left, V[] right){
	
	}
	public void a7(V left, V[] right){
		
	}
	public void a8(V left, V[] right){
	
	}
	public void a9(V left, V[] right){
		
	}
	

}
