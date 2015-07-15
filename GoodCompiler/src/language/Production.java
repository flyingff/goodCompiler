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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime * result + ((left == null) ? 0 : left.hashCode());
		result = prime * result + Arrays.hashCode(right);
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Production other = (Production) obj;
		if (action == null) {
			if (other.action != null)
				return false;
		} else if (!action.equals(other.action))
			return false;
		if (left == null) {
			if (other.left != null)
				return false;
		} else if (!left.equals(other.left))
			return false;
		if (!Arrays.equals(right, other.right))
			return false;
		return true;
	}
}
