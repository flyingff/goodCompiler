package syntax;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import language.Action;
import language.AnalyzeTable;
import language.GrammaAnalyser;
import language.Production;
import lexical.LexicalAnalyzer;
import syntax.action.SemanticAction;

public class SyntaxAnalyzerImpl implements SyntaxAnalyzer {
	private AnalyzeTable at;
	private LexicalAnalyzer la;
	private SymbolTable st = new SymbolTable();
	private Stack<Integer> sstate = new Stack<Integer>();
	private Stack<V> ssymbol = new Stack<>();
	private List<Quad> quadlist = new ArrayList<>();
	private Map<String, SemanticAction> saObjs = new HashMap<>();
	
	public SyntaxAnalyzerImpl(InputStream analyzeTable) {
		at = AnalyzeTable.load(analyzeTable);
		if (at == null) {
			throw new NullPointerException(" cannot be null");
		}
    }
	@Override
	public void setInput(LexicalAnalyzer la) {
		if (la == null) {
			throw new NullPointerException("Lexical Analyzer cannot be null");
		}
		this.la = la;
	}
	public SymbolTable getSymbolTable() {
	    return st;
    }
	@Override
	public List<Quad> analyse() {
		quadlist.clear();
		int currState = 0;
		Action action;
		sstate.push(0);
		V term = new V();
		term.name = GrammaAnalyser.TERMINATOR;
		ssymbol.push(term);
		V v = la.next();
		V[] param;
		Production p;
		while(true){
			v = (v == null) ? term : v; 
			currState = sstate.peek();
			String input = v.name;
			action = at.query(currState, input);
			if (action == null){
				throw new RuntimeException("Syntax error at: " + v.attr("value"));
			}
			switch (action.getType()) {
			case Action.ACC:
				p = action.getP();
				param = new V[p.getRight().length];
				for(int i = 0; i < param.length; i++){
					param[param.length - 1 - i] = ssymbol.pop();
					sstate.pop();
				}
				V vnx = new V();
				vnx.name = p.getLeft();
				vnx.isFinal = false;
				SemanticAction(p, vnx, param);
				System.out.println("Analyze complete!");
				break;
			case Action.GOTO:
				throw new RuntimeException("Unexpected 'Goto' action: " + action);
			case Action.REDUCTION:
				p = action.getP();
				param = new V[p.getRight().length];
				for(int i = 0; i < param.length; i++){
					param[param.length - 1 - i] = ssymbol.pop();
					sstate.pop();
				}
				String tmp = p.getLeft();
				V vn = new V();
				vn.name = tmp;
				vn.isFinal = false;
				SemanticAction(p, vn, param);
				action = at.query(sstate.peek(), tmp);
				if(action != null && action.getType() == Action.GOTO && action.getState() != -1){
					ssymbol.push(vn);
					sstate.push(action.getState());
				} else {
					throw new RuntimeException("Reduction error at: " +  v.attr("value"));
				}
				break;
			case Action.STEPINTO:
				sstate.push(action.getState());
				ssymbol.push(v);
				v = la.next();
				break;
			default:
				throw new RuntimeException("Stepinto error at: " + v.attr("value"));
			}
			if(action.getType() == Action.ACC){
				break;
			}
		}
		return quadlist;
	}
	
	private void SemanticAction(Production p, V left, V[] param){
		String a = p.getAction();
		if (a == null) return;
		int pos;
		String clazz = a.substring(0, pos = a.lastIndexOf('.'));
		String method = a.substring(pos + 1);
		SemanticAction obj = saObjs.get(clazz);
		if (obj == null) {
			try {
				Class<?> cx = Class.forName(clazz);
				obj = (SemanticAction) cx.newInstance();
				obj.setQlist(quadlist);
				obj.setSt(st);
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
		try {
	        Method mx = obj.getClass().getMethod(method, V.class, V[].class);
	        mx.invoke(obj, left, param);
        } catch (Exception e) {
        	throw new RuntimeException(e);
        }
	}
}
