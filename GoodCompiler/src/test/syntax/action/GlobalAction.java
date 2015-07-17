package test.syntax.action;

import syntax.Quad;
import syntax.V;
import syntax.action.SemanticAction;

public class GlobalAction extends SemanticAction {
	public void a1(V left, V[] right) {
		newQuad();
		System.out.println("A new 语句组;");
	}
	public void a2(V left, V[] right) {
		newQuad();
		System.out.println("A 语句 appended;");
	}
}
