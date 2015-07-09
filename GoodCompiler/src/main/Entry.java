package main;

import java.io.PrintStream;
import java.util.List;

import lexical.LexicalAnalyzer;
import syntax.Quad;
import syntax.SyntaxAnalyzer;

public class Entry {
	public static void main(String[] args) throws Exception{
		LexicalAnalyzer la = null;
		la.load(Entry.class.getResourceAsStream("input.txt"));
		SyntaxAnalyzer sa = null;
		sa.setInput(la);
		List<Quad> list = sa.analyse();
		PrintStream ps = new PrintStream("d:\\output.txt");
		for(Quad qx : list) {
			System.out.println(qx);
			ps.println(qx);
		}
		ps.close();
	}
}
