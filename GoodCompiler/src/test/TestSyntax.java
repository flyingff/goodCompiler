package test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.Thread.UncaughtExceptionHandler;

import language.GrammarAnalyser;
import lexical.LexicalAnalyzer;
import lexical.LexicalAnalyzerImpl;
import lexical.TableReader;
import syntax.Quad;
import syntax.SyntaxAnalyzer;
import syntax.SyntaxAnalyzerImpl;
/**
 * 语法分析测试程序
 * @author lxm
 *
 */
public class TestSyntax {
	public static void main(String[] args) throws Exception{
		boolean wordChanged = false;
		boolean grammaChanged = true;
		// 自动机和分析表的输入路径
		String automatPath = "d:\\syntaxtest.automat", atablePath = "d:\\syntaxtest.atable";
		if (wordChanged) {
			TableReader ta = new TableReader(TestSyntax.class.getResourceAsStream("wordlist.properties"));
			ta.getMat().saveTo(new FileOutputStream(automatPath));
		}
		if (grammaChanged) {
			GrammarAnalyser ga = new GrammarAnalyser(TestSyntax.class.getResourceAsStream("grammar.properties"));
			ga.getAnalyzeTable().save(new FileOutputStream(atablePath));
			//System.out.println("BNF Grammar:");
			//System.out.print(ga.getBNFGrammar());
		}
		// 开始分析
		LexicalAnalyzer la;
		// 加载自动机
        la = new LexicalAnalyzerImpl(new FileInputStream(automatPath));
        // 加载输入文件
        la.load(LexicalTest.class.getResourceAsStream("input7.txt"));
        // 提示出错位置等信息
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
        	@Override
        	public void uncaughtException(Thread arg0, Throwable e) {
        		Throwable t = e;
        		while(t.getCause() != null) t = t.getCause();
        		
        		if (t instanceof RuntimeException) {
        			System.err.println("Error at line " + la.getLine() + ", col " + (la.getCol() - 1));
        			System.err.println(t.getMessage());
        		}
        		t.printStackTrace();
        	}
        });
        SyntaxAnalyzer sa = new SyntaxAnalyzerImpl(new FileInputStream(atablePath));
        sa.setInput(la);
        sa.analyse();
        System.out.println("\nQuad list:");
        for(Quad qx : sa.getQuad()) {
        	System.out.println(qx);
        }
        System.out.println("\nSymbol Table:");
        System.out.println(sa.getSymbolTable());
    }
}
