package syntax.action;

import java.util.List;

import syntax.Quad;
import syntax.SymbolTable;

/**
 * public void action(V left, V[] right) {
 * 		
 * }
 */
public class SemanticAction {
	protected SymbolTable st;															//符号表
	protected List<Quad> qlist;															//四元式队列
	public final void setSt(SymbolTable st) {
	    this.st = st;
    }
	public final void setQlist(List<Quad> qlist) {
	    this.qlist = qlist;
    }
	
}
