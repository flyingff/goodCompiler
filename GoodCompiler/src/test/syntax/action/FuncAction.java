package test.syntax.action;

import java.util.ArrayList;
import java.util.List;

import syntax.Quad;
import syntax.Symbol;
import syntax.V;
import syntax.action.SemanticAction;

public class FuncAction extends SemanticAction {
	// 函数声明语句=函数头,{,局部语句组或空@test.syntax.action.FuncAction.f1
	public void f1(V left, V[] right) {
		Symbol s = (Symbol) right[0].attr("func");
		s.attr("localVar", st.exitFunc());
		backPatch((Integer)right[0].attr("skip"), nextQuad());
	}
	// 函数头=类型和函数名,(,形式参数列表,)@test.syntax.action.FuncAction.f2
	public void f2(V left, V[] right) {
		Symbol s = (Symbol) right[0].attr("func");
		s.attr("parlist", right[2].attr("parlist"));
		left.attr("func", s);
		left.attr("skip", nextQuad());
		newQuad().field("j");
		s.attr("addr", nextQuad());
	}
	// 类型和函数名=类型,id@test.syntax.action.FuncAction.f17
	public void f17(V left, V[] right) {
		String funcName = (String) right[1].attr("value");
		if (st.lookupFunc(funcName) != null) {
			throw new RuntimeException("Duplicate function declaration: " + funcName);
		}
		Symbol s = st.addFunc(funcName);
		s.attr("type", right[0].attr("type"));
		st.enterFunc();
		left.attr("func", s);
	}
	// 形式参数列表=形式参数@test.syntax.action.FuncAction.f3
	public void f3(V left, V[] right) {
		List<Symbol> parlist = new ArrayList<>();
		Symbol s = st.add((String)right[0].attr("name"));
		s.attr("type", right[0].attr("type"));
		parlist.add(s);
		left.attr("parlist", parlist);
	}
	// 形式参数列表=形式参数列表,com,形式参数@test.syntax.action.FuncAction.f4
	public void f4(V left, V[] right) {
		@SuppressWarnings("unchecked")
		List<Symbol> parlist = (List<Symbol>) right[0].attr("parlist");
		String name = (String) right[2].attr("name");
		if(st.lookupLocal(name) != null) {
			throw new RuntimeException("Duplicated local variable: " + name);
		}
		Symbol s = st.add(name);
		s.attr("type", right[2].attr("type"));
		parlist.add(s);
		left.attr("parlist", parlist);
	}
	// 形式参数=类型,id@test.syntax.action.FuncAction.f5
	public void f5(V left, V[] right) {
		left.attr("type", right[0].attr("type"));
		left.attr("name", right[1].attr("value"));
	}
	
	// 局部语句组或空=局部语句组,}@test.syntax.action.FuncAction.f6
	public void f6(V left, V[] right) {
		if((String)right[0].attr("ret") == null){
			newQuad().field("ret");
		}
		if(right[0].attr("nextq") != null)
			backPatch((Integer)right[0].attr("chain"), (Integer)right[0].attr("nextq"));
	}
	// 局部语句组=局部语句@test.syntax.action.FuncAction.f7
	public void f7(V left, V[] right) {
		left.attr("chain", right[0].attr("chain"));
		left.attr("nextq", right[0].attr("nextq"));
		String ret = (String) right[0].attr("ret");
		if(ret !=null){
			left.attr("ret", ret);
		}
	}
	
	// 局部语句组=局部语句组,局部语句@test.syntax.action.FuncAction.f8
	public void f8(V left, V[] right) {
		if(right[1].attr("exe") != null) {
			backPatch((Integer)right[0].attr("chain"), (Integer)right[0].attr("nextq"));
			left.attr("chain", right[1].attr("chain"));
		} else {
			left.attr("chain", right[0].attr("chain"));
		}
		String ret1 = (String) right[1].attr("ret");
		String ret0 = (String) right[0].attr("ret");
		if(ret0 != null || ret1 != null ){
			left.attr("ret", ret0 != null? ret0 : ret1);
		}
		//left.attr("ret", ret0 != null? ret0: ret1);
		left.attr("nextq", nextQuad());
	}
	// 局部语句=return语句@test.syntax.action.FuncAction.f19
	public void f19(V left, V[] right) {
		left.attr("exe", 1);
		left.attr("chain", right[0].attr("chain"));
		left.attr("nextq", nextQuad());
		left.attr("ret", right[0].attr("ret"));
	}
	// 局部语句=执行语句@test.syntax.action.FuncAction.f9
	public void f9(V left, V[] right) {
		left.attr("exe", 1);
		left.attr("chain", right[0].attr("chain"));
		left.attr("nextq", nextQuad());
	}
	// 局部变量定义语句=局部声明部分,semi@test.syntax.action.FuncAction.f10
	public void f10(V left, V[] right) {
		
	}
	// 局部声明部分=类型,局部声明元@test.syntax.action.FuncAction.f11|
	public void f11(V left, V[] right) {
		String type = (String) right[0].attr("type");
		String name = (String) right[1].attr("name");
		if(st.lookupLocal(name) != null){
			throw new RuntimeException("Duplicate local variable: " + name);
		}
		Symbol s = st.add(name);
		if(right[1].attr("dim") != null){
			s.attr("dim", right[1].attr("dim"));
		}
		s.attr("type", type);
		left.attr("type", type);
	}
	// 局部声明部分=局部声明部分,com,局部声明元@test.syntax.action.FuncAction.f12
	public void f12(V left, V[] right) {
		String type = (String) right[0].attr("type");
		String name = (String) right[2].attr("name");
		if(st.lookupLocal(name) != null) {
			throw new RuntimeException("Dupilicate local variable: " + name);
		}
		Symbol s = st.add(name);
		if(right[2].attr("dim") != null)
			s.attr("dim", right[2].attr("dim"));
		s.attr("type", type);
		left.attr("type", type);
	}
	// 局部声明元=id@test.syntax.action.FuncAction.f13
	public void f13(V left, V[] right) {
		left.attr("name", right[0].attr("value"));
	}
	// 局部声明元=局部数组参数表,]@test.syntax.action.FuncAction.f14
	public void f14(V left, V[] right) {
		left.attr("name", right[0].attr("name"));
		left.attr("dim", right[0].attr("dim"));
	}
	// 局部数组参数表=id,[,number@test.syntax.action.FuncAction.f15
	public void f15(V left, V[] right) {
		left.attr("name",right[0].attr("value"));
		List<Integer> dim = new ArrayList<>();
		String val = (String) right[2].attr("value");
		if(val.indexOf(".") != -1) {
			throw new RuntimeException("Array Dimension can only be integer: " + val);
		} 
		dim.add(Integer.parseInt(val));
		left.attr("dim", dim);
	}
	// 局部数组参数表=局部数组参数表,com,number@test.syntax.action.FuncAction.f16
	public void f16(V left, V[] right) {
		@SuppressWarnings("unchecked")
		List<Integer> dim = (List<Integer>) right[0].attr("dim");
		String val = (String) right[2].attr("value");
		if(val.indexOf(".") != -1) {
			throw new RuntimeException("Array Dimension can only be integer: " + val);
		} 
		dim.add(Integer.parseInt(val));
		left.attr("name", right[0].attr("name"));
		left.attr("dim", dim);
	}
	// return语句=return,值,semi@test.syntax.action.FuncAction.f18
	public void f18(V left, V[] right) {
		left.attr("ret", right[0].attr("value"));
		Object val = right[1].attr("value");
		newQuad().field("ret",null, null, val);
		st.releaseTemp(val);
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
