package syntax;

import java.util.List;

import lexical.LexicalAnalyzer;

public interface SyntaxAnalyzer {
	/**
	 * 设置输入的词法分析器
	 * @param la
	 */
	void setInput(LexicalAnalyzer la);
	
	/**
	 * 语法分析程序,生成四元式
	 * @return
	 */
	List<Quad> analyse();
	
	/**
	 * 获得符号表
	 * @return
	 */
	SymbolTable getSymbolTable();
	
	/**
	 * 得到四元式列表
	 * @return
	 */
	List<Quad> getQuad();
}
