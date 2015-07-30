package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;

import language.GrammarAnalyser;
import lexical.LexicalAnalyzer;
import lexical.LexicalAnalyzerImpl;
import lexical.TableReader;
import syntax.Quad;
import syntax.SyntaxAnalyzer;
import syntax.SyntaxAnalyzerImpl;

public class Entry {
	public static void main(String[] args) throws Exception{
		boolean wordChanged = false;
		boolean grammaChanged = true;
		// 自动机和分析表的输入路径
		String automatPath = "d:\\syntaxtest.automat", atablePath = "d:\\syntaxtest.atable";
		if (wordChanged) {
			TableReader ta = new TableReader(Entry.class.getResourceAsStream("wordlist.properties"));
			ta.getMat().saveTo(new FileOutputStream(automatPath));
		}
		if (grammaChanged) {
			GrammarAnalyser ga = new GrammarAnalyser(Entry.class.getResourceAsStream("grammar.properties"));
			ga.getAnalyzeTable().save(new FileOutputStream(atablePath));
			//System.out.println("BNF Grammar:");
			//System.out.print(ga.getBNFGrammar());
		}
		// 开始分析
		LexicalAnalyzer la;
		// 加载自动机
        la = new LexicalAnalyzerImpl(new FileInputStream(automatPath));
        // 加载输入文件
        la.load(Entry.class.getResourceAsStream("input6.txt"));
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
        File output = new File("d:\\output.txt");
        PrintWriter pw = new PrintWriter(output);
        System.out.println("\nQuad list:");
        for(Quad qx : sa.getQuad()) {
        	System.out.println(qx.toString());
        	pw.println(qx.toString());
        }
        System.out.println("\nSymbol Table:");
        pw.println(sa.getSymbolTable().toString());
        pw.close();
        System.out.println(sa.getSymbolTable());
    }
}
