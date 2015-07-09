package lexical;

import java.io.InputStream;

import syntax.V;

/**
 * 功能：把程序转换成Vt序列
 * @author FlyingFlameR
 *
 */
public interface LexicalAnalyzer {
	void load(InputStream is);
	V next();
}
