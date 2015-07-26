package syntax.action;

import java.util.List;

import syntax.Quad;
import syntax.SymbolTable;

/**
 * 所有语义动作类的父类
 * public void actionName(V left, V[] right) {
 * 		
 * }
 */
public class SemanticAction {
	protected SymbolTable st;															// 符号表
	private List<Quad> qlist;															// 四元式队列
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
	/**
	 * 根据四元式编号返回四元式
	 * @param num
	 * @return
	 */
	public final Quad getQuad(int num){
		return qlist.get(num - Quad.STARTNUM);
	}
	/**
	 * 获得下一条四元式的编号
	 * @return
	 */
	public final int nextQuad(){
		return qlist.size() + Quad.STARTNUM;
	}
}
