package lexical;

import java.io.InputStream;

import syntax.V;

/**
 * ���ܣ��ѳ���ת����Vt����
 * @author FlyingFlameR
 *
 */
public interface LexicalAnalyzer {
	void load(InputStream is);
	V next();
}
