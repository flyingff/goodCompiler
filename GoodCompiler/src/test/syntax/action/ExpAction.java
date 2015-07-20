package test.syntax.action;

import syntax.Quad;
import syntax.Symbol;
import syntax.V;
import syntax.action.SemanticAction;

public class ExpAction extends SemanticAction{
	private static final String GT = ">", GE =">=", LT = "<", LE = "<=", NE = "<>", TRUEV = "true", FALSEV = "false";
		
	// 布尔表达式=与或值@test.syntax.action.ExpAction.e1
	public void e1(V left, V[] right){
		left.attr("value", right[0].attr("value"));
		
	}
	// 与或值=与或值,and,非值@test.syntax.action.ExpAction.e2
	public void e2(V left, V[] right){
		if(right[0].attr("value").equals(TRUEV) && right[2].attr("value").equals(TRUEV)){
			left.attr("value", TRUEV);
		} else {
			left.attr("value", FALSEV);
		}
	}
	// 布尔表达式=与或值,or,非值@test.syntax.action.ExpAction.e3 
	public void e3(V left, V[] right){
		if(right[0].attr("value").equals(TRUEV) || right[2].attr("value").equals(TRUEV)){
			left.attr("value", TRUEV);
		} else {
			left.attr("value", FALSEV);
		}
	}
	// 布尔表达式=非值@test.syntax.action.ExpAction.e4
	public void e4(V left, V[] right){
			left.attr("value", right[0].attr("value"));
	}
	// 非值=not,元布尔值@test.syntax.action.ExpAction.e5
	public void e5(V left, V[] right){
		if(right[1].attr("value").equals(TRUEV)){
			left.attr("value", FALSEV);
		} else {
			left.attr("value", TRUEV);
		}
	}
	// 非值=元布尔值@test.syntax.action.ExpAction.e6
	public void e6(V left, V[] right){
		left.attr("value", right[0].attr("value"));
	}
	// 元布尔值=false@test.syntax.action.ExpAction.e7|true@test.syntax.action.ExpAction.e7
	public void e7(V left, V[] right){
		left.attr("value", right[0].attr("value"));
	}
	// 元布尔值=值,关系运算符,值@test.syntax.action.ExpAction.e8
	public void e8(V left, V[] right){
		Object o1 = right[0].attr("value"), o2 = right[2].attr("value");
		Symbol tmp = st.getTemp();
		Quad qx = newQuad();
		qx.field(right[1].attr("value"),o1, o2, tmp);
		tmp.attr("value", getResult(o1, right[1].attr("value"), o2));
		left.attr("value", tmp);
	}
	private String getResult(Object o1, Object attr, Object o2) {
		if(attr.equals(GT)){
			return Integer.parseInt((String)o1) > Integer.parseInt((String)o2)? TRUEV : FALSEV;
		}
		if(attr.equals(LT)){
			return Integer.parseInt((String)o1) < Integer.parseInt((String)o2)? TRUEV : FALSEV;
		}
		if(attr.equals(GE)){
			return Integer.parseInt((String)o1) >= Integer.parseInt((String)o2)? TRUEV : FALSEV;
		}
		if(attr.equals(LE)){
			return Integer.parseInt((String)o1) <= Integer.parseInt((String)o2)? TRUEV : FALSEV;
		}
		if(attr.equals(NE)){
			return Integer.parseInt((String)o1) == Integer.parseInt((String)o2)? FALSEV : TRUEV;
		}
		return FALSEV;
    }
	// 元布尔值=(,布尔表达式,)@test.syntax.action.ExpAction.e9
	public void e9(V left, V[] right){
		left.attr("value", right[1].attr("value"));
	}
	// 关系运算符=<@test.syntax.action.ExpAction.e10
	// 关系运算符=>@test.syntax.action.ExpAction.e10
	// 关系运算符=<>@test.syntax.action.ExpAction.e10
	// 关系运算符=\=\=@test.syntax.action.ExpAction.e10
	// 关系运算符=>\=@test.syntax.action.ExpAction.e10
	// 关系运算符=<\=@test.syntax.action.ExpAction.e10
	public void e10(V left, V[] right){
		left.attr("value", right[0].attr("value"));
	}
	
}
