package syntax;

import java.io.InputStream;
import java.util.List;

import language.AnalyzeTable;
import lexical.LexicalAnalyzer;

public class SyntaxAnalyzerImpl implements SyntaxAnalyzer {
	private AnalyzeTable at;
	private LexicalAnalyzer la;
	private SymbolTable st = new SymbolTable();
	
	public SyntaxAnalyzerImpl(InputStream analyzeTable) {
		at = AnalyzeTable.load(analyzeTable);
		if (at == null) {
			throw new NullPointerException(" cannot be null");
		}
    }
	@Override
	public void setInput(LexicalAnalyzer la) {
		if (la == null) {
			throw new NullPointerException("Lexical Analyzer cannot be null");
		}
	}

	@Override
	public List<Quad> analyse() {
		return null;
	}

}
