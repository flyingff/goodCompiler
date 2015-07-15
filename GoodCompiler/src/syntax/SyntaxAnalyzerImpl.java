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
import language.GrammarAnalyser;
import language.Production;
import lexical.LexicalAnalyzer;
import syntax.action.SemanticAction;

/**
 * SyntaxAnalyzerImpl类,实现SyntaxAnalyzer接口
 * 实现语法分析的功能
 * @author lxm
 *
 */
public class SyntaxAnalyzerImpl implements SyntaxAnalyzer {
	private AnalyzeTable at;															// 语法分析表
	private LexicalAnalyzer la;															// 词法分析器
	private SymbolTable st = new SymbolTable();											// 符号表
	private Stack<Integer> sstate = new Stack<Integer>();								// 栈,存放状态
	private Stack<V> ssymbol = new Stack<>();											// 符号栈
	private List<Quad> quadlist = new ArrayList<>();									// 生成的四元式队列
	private Map<String, SemanticAction> saObjs = new HashMap<>();						// 语义动作映射
	private Map<String, Method> samethods = new HashMap<String, Method>();				// 动作方法映射
	public SyntaxAnalyzerImpl(InputStream analyzeTable) {
		// 加载分析表
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
		quadlist.clear();																// 清空四元式队列
		int currState = 0;
		Action action;
		sstate.push(0);																	// 初始状态压栈
		V term = new V();
		term.name = GrammarAnalyser.TERMINATOR;
		ssymbol.push(term);																// '#'压栈
		V v = la.next();
		V[] param;
		Production p;
		while(true){
			v = (v == null) ? term : v; 
			currState = sstate.peek();													// 查看栈顶的状态
			String input = v.name;														// 当前输入
			action = at.query(currState, input);										// 获得对于的动作
			if (action == null){ 
				throw new RuntimeException("Syntax error at: " + v.attr("value"));
			}
			// 根据不同类型执行不同的动作
			switch (action.getType()) {
			// 接受
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
				// 跳转
			case Action.GOTO:
				throw new RuntimeException("Unexpected 'Goto' action: " + action);
				// 规约
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
				// 移进
			case Action.STEPINTO:
				sstate.push(action.getState());
				ssymbol.push(v);
				v = la.next();
				break;
				// 出错
			default:
				throw new RuntimeException("Stepinto error at: " + v.attr("value"));
			}
			if(action.getType() == Action.ACC){
				break;
			}
		}
		return quadlist;
	}
	
	/**
	 * 根据要规约的产生式,执行相应的语义动作
	 * @param p
	 * @param left
	 * @param param
	 */
	private void SemanticAction(Production p, V left, V[] param){
		String a = p.getAction();														// 获得动作执行路径
		if (a == null) return;
		int pos;
		String clazz = a.substring(0, pos = a.lastIndexOf('.'));						
		String method = a.substring(pos + 1);
		SemanticAction obj = saObjs.get(clazz);
		if (obj == null) {
			try {
				// 获得类对象的实例
				Class<?> cx = Class.forName(clazz);
				obj = (SemanticAction) cx.newInstance();
				obj.setQlist(quadlist);
				obj.setSt(st);
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
		Method mx = samethods.get(method);
		if(mx == null){
			try {
				// 获得对象方法,调用方法
				mx = obj.getClass().getMethod(method, V.class, V[].class);
				samethods.put(a, mx);
		        mx.invoke(obj, left, param);
	        } catch (Exception e) {
	        	throw new RuntimeException(e);
        }
		}
	}
}
