package test;

import java.io.FileInputStream;
import java.io.IOException;

import syntax.V;
import lexical.LexicalAnalyzer;
import lexical.LexicalAnalyzerImpl;

public class LexicalTest {
	public static void main(String[] args) throws IOException {
		LexicalAnalyzer la = new LexicalAnalyzerImpl(new FileInputStream("e:\\mat1.automat"));
		la.load(LexicalTest.class.getResourceAsStream("input.txt"));
		V vx = la.next();
		while(vx != null) {
			System.out.println("[" + vx.name +": " + vx.attr("value") + "]");
			vx = la.next();
		}
	}
	
}
