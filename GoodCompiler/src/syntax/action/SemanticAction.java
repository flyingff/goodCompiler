package syntax.action;

import syntax.SymbolTable;

public class SemanticAction {
	protected SymbolTable st;
	public final void setSymbolTable(SymbolTable st) {
	    this.st = st;
    }
}
