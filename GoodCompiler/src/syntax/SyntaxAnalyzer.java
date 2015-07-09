package syntax;

import java.util.List;

import lexical.LexicalAnalyzer;

public interface SyntaxAnalyzer {
	void setInput(LexicalAnalyzer la);
	List<Quad> analyse();
}
