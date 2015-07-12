package language;

import java.util.Arrays;

/**
 * Production��
 * ��¼����ʽ��������Ϣ
 * ��������ʽ��left,����ʽ�Ҳ��ķ������б�right��ִ�ж����ķ�����·��action
 * @author lxm
 *
 */
public class Production{
	private String left;
	private String[] right;
	private String action;
	public String[] getRight() {
		return right;
	}
	public void setRight(String[] right) {
		this.right = right;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	@Override 
	public String toString() {
		return "" + left + "-->" + Arrays.toString(right)
				+ "@" + action;
	}
	public String getLeft() {
		return left;
	}
	public void setLeft(String left) {
		this.left = left;
	}
}
