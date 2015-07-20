package test.syntax.action;

import syntax.Quad;
import syntax.V;
import syntax.action.SemanticAction;

public class ControlAction extends SemanticAction {
	
	public void pass(V left, V[] right) {
		left.attr("chain", right[0].attr("chain"));
	}
	// 执行语句组 = 执行语句组,执行语句
	public void c1(V left, V[] right){
		backPatch((Integer)right[0].attr("chain"), (Integer)right[0].attr("nextq"));
		left.attr("nextq", nextQuad());
		left.attr("chain", right[1].attr("chain"));
	}
	// 执行语句组=执行语句@test.syntax.action.ControlAction.c11
	public void c11(V left, V[] right){
		left.attr("nextq", nextQuad());
		left.attr("chain", right[0].attr("chain"));
	}
	
	//	while语句头=while字符串,(,布尔表达式,)@test.syntax.action.ControlAction.c2
	public void c2(V left, V[] right){
		Integer tc = (Integer)right[2].attr("TC"), fc = (Integer)right[2].attr("FC");
		backPatch(tc, nextQuad());
		left.attr("chain", fc);
		left.attr("quad", right[0].attr("quad"));
	}
	//	while语句=while语句头,{,执行语句组或空@test.syntax.action.ControlAction.c3
	public void c3(V left, V[] right){
		left.attr("chain", right[0].attr("chain"));
		backPatch((Integer)right[2].attr("chain"), (Integer)right[0].attr("quad"));
		newQuad().field("j", null, null, right[0].attr("quad"));
	}
	//	else子句=else字符串,{,执行语句组或空@test.syntax.action.ControlAction.c4
	public void c4(V left, V[] right){
		left.attr("elseq", right[0].attr("elseq"));
		left.attr("elsechain", right[0].attr("elsechain"));
		left.attr("chain", right[2].attr("chain"));
	}
	//	if语句头=if,(,布尔表达式,)@test.syntax.action.ControlAction.c5
	public void c5(V left, V[] right){
		Integer tc = (Integer)right[2].attr("TC"), fc = (Integer)right[2].attr("FC");
		backPatch(tc, nextQuad());
		left.attr("chain", fc);
	}	
	//没有else的if语句=if语句头,{,执行语句组或空@test.syntax.action.ControlAction.c6
	public void c6(V left, V[] right){
		left.attr("chain", right[0].attr("chain"));
		left.attr("chain2", right[2].attr("chain"));
	}
	//if语句=没有else的if语句,else子句@test.syntax.action.ControlAction.c7
	public void c7(V left, V[] right){
		left.attr("chain", merge((Integer)right[1].attr("elsechain"), (Integer)right[0].attr("chain2")));
		backPatch((Integer)right[0].attr("chain"), (Integer)right[1].attr("elseq"));
	}
	//while字符串=while@test.syntax.action.ControlAction.c8
	public void c8(V left, V[] right){
		left.attr("quad", nextQuad());
	}
	//if语句=没有else的if语句@test.syntax.action.ControlAction.c9
	public void c9(V left, V[] right){
		left.attr("chain", merge((Integer)right[0].attr("chain"), (Integer)right[0].attr("chain2")));
	}	
	//else字符串=else@test.syntax.action.ControlAction.c10
	public void c10(V left, V[] right){
		left.attr("elsechain", nextQuad());
		newQuad().field("j");
		left.attr("elseq", nextQuad());
	}


	private Integer merge(Integer head1, Integer head2){
		Integer newhead = 0;
		if (head1 == null){
			newhead = head2;
		} else if (head2 == null){
			newhead = head1;
		} else {
			Quad qx = getQuad(head2);
			while(qx.field[3] != null){
				qx = getQuad((Integer)qx.field[3]);
			}
			qx.field[3] = head1;
			newhead = head2;
		}
		return newhead;
	}
	private void backPatch(Integer head, int nextQuad) {
		while(head != null && head >= Quad.STARTNUM){
			Quad q = getQuad(head);
			head = (Integer)q.field[3];
			q.field[3] = nextQuad;
		}
	}
}
