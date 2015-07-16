package syntax.action;

import java.util.List;

import syntax.Quad;
import syntax.SymbolTable;

/**
 * public void actionName(V left, V[] right) {
 * 		
 * }
 */
public class SemanticAction {
	protected SymbolTable st;															//符号表
	private List<Quad> qlist;															//四元式队列
	protected final Quad newQuad() {
		Quad q = new Quad();
		q.num = Quad.STARTNUM + qlist.size();
		qlist.add(q);
		return q;
	}
	public final void setSt(SymbolTable st) {
	    this.st = st;
    }
	public final void setQlist(List<Quad> qlist) {
	    this.qlist = qlist;
    }
	
}
