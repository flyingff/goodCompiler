package test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import lexical.LexicalAnalyzer;
import lexical.LexicalAnalyzerImpl;
import syntax.SyntaxAnalyzer;
import syntax.SyntaxAnalyzerImpl;

public class TestSyntax {
	public static void main(String[] args) {
		LexicalAnalyzer la;
        try {
	        la = new LexicalAnalyzerImpl(new FileInputStream("e:\\mat1.automat"));
	        la.load(LexicalTest.class.getResourceAsStream("input1.txt"));
	        SyntaxAnalyzer sa = new SyntaxAnalyzerImpl(new FileInputStream("e:\\table1.atab"));
	        sa.setInput(la);
	        sa.analyse();
        } catch (FileNotFoundException e) {
	        e.printStackTrace();
        }
    }
}
