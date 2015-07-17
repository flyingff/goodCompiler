package test;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import language.GrammarAnalyser;
import lexical.LexicalAnalyzer;
import lexical.LexicalAnalyzerImpl;
import lexical.TableReader;
import syntax.Quad;
import syntax.SyntaxAnalyzer;
import syntax.SyntaxAnalyzerImpl;

public class TestSyntax {
	public static void main(String[] args) throws Exception{
		boolean wordChanged = false;
		boolean grammaChanged = true;
		// generate
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
		// start analyze
		LexicalAnalyzer la;
        la = new LexicalAnalyzerImpl(new FileInputStream(automatPath));
        la.load(LexicalTest.class.getResourceAsStream("input1.txt"));
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
