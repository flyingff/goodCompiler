package syntax;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
				/* 
				 * 出错建议机制
				 * 如果Action为NULL, 说明我们遇到了一个不能顺利进行分析的符号;
				 * 为了帮助用户解决问题，我们可以采取以下策略：在该符号前插入
				 * 所有可能输入的终结符中的一个，尝试继续分析过程，如果能够成功，
				 * 并且能将原来不能顺利分析的符号移进掉，则认为该输入是有效的，
				 * 并将其作为建议之一。
				 * 例如：假设有输入串 a := 3 2;
				 * 在扫描到第二个数字时发现无法规约，这时遍历所有的终结符，在其中选
				 * 一个插入2之前，如果插入的终结符使得该式能够成功移近（“吃”）掉2（比如+和*
				 * 都可以规约掉2，分别成为加值和乘积值）,则认为该终结符可以被建议。
				 */
				// 取得不能被规约符号的符号名
				String nextVt = (String) v.name;
				Set<String> proposal = new HashSet<>();
				// 遍历所有可能的终结符
outer:			for(String vtx : at.getVT()) {
					// 模拟
					Action ax; 
					// 复制状态栈，无需复制符号栈。
					Stack<Integer> sstack2 = new Stack<Integer>();
					sstack2.addAll(sstate);
					// 进行模拟规约，直到插入的终结符被规约掉。
					int currstate2;
inner:				while(true) {
						currstate2 = sstack2.peek();
						ax = at.query(currstate2, vtx);
						// 如果走不通，直接看下一个终结符
						if(ax == null) continue outer;
						switch(ax.getType()) {
							// 如果是归约而非移进，继续
							case Action.REDUCTION:
								int poplen = ax.getP().getRight().length;
								for(int i = 0; i < poplen; i++)
									sstack2.pop();
								ax = at.query(sstack2.peek(), ax.getP().getLeft());
								if(ax == null || ax.getType() != Action.GOTO) continue outer;
								sstack2.push(ax.getState());
								break;
							case Action.GOTO:
								// this should not occur.
								continue outer;
							case Action.ACC:
								// 如果能接受ACC，则认为该符号可用
								proposal.add(vtx);
								continue outer;
							case Action.STEPINTO:
								// 移进掉了插入的符号
								sstack2.push(ax.getState());
								break inner;
						}
					}
					// 规约原有的符号
					currstate2 = sstack2.peek();
					ax = at.query(currstate2, nextVt);
					// 如果仍然不能被规约，说明该插入终结符无效
					if(ax == null) continue;
					proposal.add(vtx);
				}
				// 生成格式化的错误信息
				StringBuffer err = new StringBuffer();
				err.append("SYNTAX ERROR - Unexpected symbol: ").append(v.attr("value"));
				err.append(", expect these symbols:").append(proposal);
				throw new RuntimeException(err.toString());
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
				st.allocateAddr();
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
	@Override
	public List<Quad> getQuad() {
		return quadlist;
	}
}
