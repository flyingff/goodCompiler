package lexical;

import java.io.InputStream;

import syntax.V;

/**
 * �ӿ���
 * @author lxm
 *
 */
public interface LexicalAnalyzer {
	/**
	 * ����������,��ȡ�ַ���
	 * @param is
	 */
	void load(InputStream is);
	/**
	 * ʶ�𵥴�
	 * @return
	 */
	V next();
}
