package lexical;

import java.io.InputStream;

import syntax.V;

/**
 * 接口类
 * @author lxm
 *
 */
public interface LexicalAnalyzer {
	/**
	 * 加载输入流,读取字符流
	 * @param is
	 */
	void load(InputStream is);
	/**
	 * 识别单词
	 * @return
	 */
	V next();
}
