package test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import language.GrammarAnalyser;
import lexical.LexicalAnalyzer;
import lexical.LexicalAnalyzerImpl;
import lexical.TableReader;
import syntax.SyntaxAnalyzer;
import syntax.SyntaxAnalyzerImpl;

public class TestSyntax {
	public static void main(String[] args) throws Exception{
		// generate
		String automatPath = "e:\\syntaxtest.automat", atablePath = "e:\\syntaxtest.atable";
		TableReader ta = new TableReader(TestSyntax.class.getResourceAsStream("wordlist.properties"));
		ta.getMat().saveTo(new FileOutputStream(automatPath));
		GrammarAnalyser ga = new GrammarAnalyser(TestSyntax.class.getResourceAsStream("gramma.properties"));
		ga.getAnalyzeTable().save(new FileOutputStream(atablePath));
		
		// start analyze
		LexicalAnalyzer la;
        la = new LexicalAnalyzerImpl(new FileInputStream(automatPath));
        la.load(LexicalTest.class.getResourceAsStream("input1.txt"));
        SyntaxAnalyzer sa = new SyntaxAnalyzerImpl(new FileInputStream(atablePath));
        sa.setInput(la);
        sa.analyse();
    }
}
