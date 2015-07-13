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
	protected SymbolTable st;
	protected List<Quad> qlist;
	public final void setSt(SymbolTable st) {
	    this.st = st;
    }
	public final void setQlist(List<Quad> qlist) {
	    this.qlist = qlist;
    }
	
}
