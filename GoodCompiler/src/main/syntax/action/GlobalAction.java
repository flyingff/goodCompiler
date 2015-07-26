package main.syntax.action;

import syntax.Quad;
import syntax.V;
import syntax.action.SemanticAction;
/**
 * 定义全局语句执行的语义动作
 * @author lxm
 *
 */
public class GlobalAction extends SemanticAction {
	// S=语句组@test.syntax.action.GlobalAction.a1
	public void a1(V left, V[] right) {
		backPatch((Integer)right[0].attr("chain"), nextQuad());
		newQuad().field("end");
	}
	// 语句组=语句@test.syntax.action.GlobalAction.a2
	public void a2(V left, V[] right) {
		left.attr("nextq", nextQuad());
		left.attr("chain", right[0].attr("chain"));
	}
	// 语句组=语句组,语句@test.syntax.action.GlobalAction.a3
	public void a3(V left, V[] right) {
		if(right[1].attr("exe") != null) {
			backPatch((Integer)right[0].attr("chain"), (Integer)right[0].attr("nextq"));
			left.attr("chain", right[1].attr("chain"));
		} else {
			left.attr("chain", right[0].attr("chain"));
		}
		left.attr("nextq", nextQuad());
	}
	// 语句=执行语句@test.syntax.action.GlobalAction.a4
	public void a4(V left, V[] right) {
		left.attr("chain", right[0].attr("chain"));
		left.attr("exe", 1);
	}
	/**
	 * 回填
	 * @param head
	 * @param nextQuad
	 */
	private void backPatch(Integer head, int nextQuad) {
		while(head != null && head >= Quad.STARTNUM){
			Quad q = getQuad(head);
			head = (Integer)q.field[3];
			q.field[3] = nextQuad;
		}
	}
}
