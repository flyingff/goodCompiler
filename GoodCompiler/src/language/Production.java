package language;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Production类
 * 记录产生式的所有信息
 * 包括产生式左部left,产生式右部文法符号列表right和执行动作的方法的路径action
 * @author lxm
 *
 */
public class Production implements Serializable{
    private static final long serialVersionUID = -4371732071940483600L;
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
