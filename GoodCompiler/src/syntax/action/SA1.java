package syntax.action;

import syntax.Quad;
import syntax.V;

public class SA1 extends SemanticAction {
	private static int index = 100;
	public void a0(V left, V[] right){
		System.out.println("Result:" + right[0].attr("intval"));
	}
	public void a1(V left, V[] right){
		int v1 = (Integer)right[0].attr("intval");
		int v2 = (Integer)right[2].attr("intval");
		Quad q = new Quad();
		//q.num = qlist.size();
		//q.field = 
		//qlist.add(q);
		//System.out.println(" quad:" + index + "( + " + ", "   + right[0].name + ", " + right[2].name + ", " + (v1 + v2) + ")");
		left.attr("intval", v1 + v2);
		System.out.println(" quad:" + index + "( " + right[1].attr("intval") + ", "   + right[0].name + ", " + right[2].name + ", " + left.name + ")");
		index ++;
	}
	public void a2(V left, V[] right){
		left.attr("intval", right[0].attr("intval"));
	}
	public void a3(V left, V[] right){
		int v1 = (Integer)right[0].attr("intval");
		int v2 = (Integer)right[2].attr("intval");
		//System.out.println(" quad:" + index + "( * " + ", "   + v1 + ", " + v2 + ", " + (v1 * v2) + ")");
		left.attr("intval", v1 * v2);
		System.out.println(" quad:" + index + "( " + right[1].name + ", "   + right[0].name + ", " + right[2].name + ", " + left.name + ")");
		index++;
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
