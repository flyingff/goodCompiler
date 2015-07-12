package main;

import java.io.FileInputStream;
import java.io.PrintStream;
import java.util.List;

import lexical.LexicalAnalyzer;
import lexical.LexicalAnalyzerImpl;
import syntax.Quad;
import syntax.SyntaxAnalyzer;
import syntax.SyntaxAnalyzerImpl;

public class Entry {
	public static void main(String[] args) throws Exception{
		// construct lexical analyzer from an existing Auto-mat
		LexicalAnalyzer la = new LexicalAnalyzerImpl(new FileInputStream(""));
		
		// load input file
		la.load(Entry.class.getResourceAsStream("input.txt"));
		
		// construct syntax analyzer from an existing SLR(1) analyze table 
		SyntaxAnalyzer sa = new SyntaxAnalyzerImpl(new FileInputStream(""));
		
		// associate lexical analyzer into syntax analyzer
		sa.setInput(la);
		
		// use syntax analyzer to produce a list of quad
		List<Quad> list = sa.analyse();
		
		// print quadruples to both screen and a file
		PrintStream ps = new PrintStream("d:\\output.txt");
		for(Quad qx : list) {
			System.out.println(qx);
			ps.println(qx);
		}
		ps.close();
	}
}
