package syntax.action;

import syntax.V;

public class SA1 extends SemanticAction {
	public void a0(V left, V[] right){
		System.out.println("Result:" + right[0].attr("intval"));
	}
	public void a1(V left, V[] right){
		int v1 = (Integer)right[0].attr("intval");
		int v2 = (Integer)right[2].attr("intval");
		left.attr("intval", v1 + v2);
	}
	public void a2(V left, V[] right){
		left.attr("intval", right[0].attr("intval"));
	}
	public void a3(V left, V[] right){
		int v1 = (Integer)right[0].attr("intval");
		int v2 = (Integer)right[2].attr("intval");
		left.attr("intval", v1 * v2);
	}
	public void a4(V left, V[] right){
		left.attr("intval", right[0].attr("intval"));
	}
	public void a5(V left, V[] right){
		left.attr("intval", right[1].attr("intval"));
	}
	public void a6(V left, V[] right){
		left.attr("intval", Integer.parseInt((String) right[0].attr("value")));
	}
}
