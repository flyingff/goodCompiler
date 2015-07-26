package main.syntax.action;

import syntax.Quad;
import syntax.Symbol;
import syntax.V;
import syntax.action.SemanticAction;
/**
 * 表达式的语义动作
 * @author lxm
 *
 */
public class ExpAction extends SemanticAction{
		
	// 布尔表达式=与或值@mainsyntax.action.ExpAction.e1
	public void e1(V left, V[] right){
		left.attr("TC", right[0].attr("TC"));
		left.attr("FC", right[0].attr("FC"));
	}
	// 与或值=与前缀,非值@mainsyntax.action.ExpAction.e2
	public void e2(V left, V[] right){
		 left.attr("FC", merge((Integer)right[0].attr("FC"), (Integer)right[1].attr("FC")));
		 left.attr("TC", right[1].attr("TC"));
	}
	// 与或值 =或前缀,非值@mainsyntax.action.ExpAction.e3 
	public void e3(V left, V[] right){
		 left.attr("TC", merge((Integer)right[0].attr("TC"), (Integer)right[1].attr("TC")));
		 left.attr("FC", right[1].attr("FC"));
	}
	// 与前缀=与或值,and@mainsyntax.action.ExpAction.e11
	public void e11(V left, V[] right){
		left.attr("FC", right[0].attr("FC"));
		backPatch((Integer)right[0].attr("TC"), nextQuad());
	}
	// 或前缀=与或值,or@mainsyntax.action.ExpAction.e12
	public void e12(V left, V[] right){
		left.attr("TC", right[0].attr("TC"));
		backPatch((Integer)right[0].attr("FC"), nextQuad());
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
	// 与或值=非值@mainsyntax.action.ExpAction.e4
	public void e4(V left, V[] right){
		left.attr("TC", right[0].attr("TC"));
		left.attr("FC", right[0].attr("FC"));
	}
	// 非值=not,元布尔值@mainsyntax.action.ExpAction.e5
	public void e5(V left, V[] right){
		left.attr("TC", right[1].attr("FC"));
		left.attr("FC", right[1].attr("TC"));
	}
	// 非值=元布尔值@mainsyntax.action.ExpAction.e6
	public void e6(V left, V[] right){
		left.attr("TC", right[0].attr("TC"));
		left.attr("FC", right[0].attr("FC"));
	}
	// 元布尔值=false@mainsyntax.action.ExpAction.e7|true@mainsyntax.action.ExpAction.e7
	public void e7(V left, V[] right){
		Quad q = newQuad();
		q.field("j");
		left.attr("true".equals(right[0].attr("value"))?"TC":"FC", q.num);
	}
	// 元布尔值=值,关系运算符,值@mainsyntax.action.ExpAction.e8
	public void e8(V left, V[] right){
		Object o1 = right[0].attr("value"), o2 = right[2].attr("value");
		Quad qx = newQuad();
		qx.field("j" + right[1].attr("value"),o1, o2);
		left.attr("TC", qx.num);
		qx = newQuad();
		qx.field("j");
		left.attr("FC", qx.num);
	}
	// 元布尔值=(,布尔表达式,)@mainsyntax.action.ExpAction.e9
	public void e9(V left, V[] right){
		left.attr("TC", right[1].attr("TC"));
		left.attr("FC", right[1].attr("FC"));
	}
	// 关系运算符=<@mainsyntax.action.ExpAction.e10
	// 关系运算符=>@mainsyntax.action.ExpAction.e10
	// 关系运算符=<>@mainsyntax.action.ExpAction.e10
	// 关系运算符=\=\=@mainsyntax.action.ExpAction.e10
	// 关系运算符=>\=@mainsyntax.action.ExpAction.e10
	// 关系运算符=<\=@mainsyntax.action.ExpAction.e10
	public void e10(V left, V[] right){
		left.attr("value", right[0].attr("value"));
	}	
	/**
	 * 回填
	 * @param head
	 * @param nextQuad
	 */
	// 元布尔值=id@mainsyntax.action.ExpAction.e11
	public void e13(V left, V[] right){
		String name = (String) right[0].attr("value");
		Symbol s = st.lookup(name);
		if(s == null){
			throw new RuntimeException("Symbol undefined: " + name);
		} else {
			if(s.attr("type").equals(ValueAction.BOOL)){
				Quad q = newQuad();
				q.field("jnz",s);
				left.attr("TC", q.num);
				q = newQuad();
				q.field("j");
				left.attr("FC", q.num);
			} else {
				throw new RuntimeException("Symbol is not a boolean: " + name);
			}
		}
	}
	private void backPatch(Integer head, int nextQuad) {
		while(head != null && head >= Quad.STARTNUM){
			Quad q = getQuad(head);
			head = (Integer)q.field[3];
			q.field[3] = nextQuad;
		}
	}
}