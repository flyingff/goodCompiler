package syntax;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.swing.JFrame;
import javax.swing.JPanel;

import language.Action;
import language.AnalyzeTable;
import language.GrammarAnalyser;
import language.Production;
import lexical.LexicalAnalyzer;
import syntax.action.SemanticAction;

/**
 * SyntaxAnalyzerImpl类,实现SyntaxAnalyzer接口
 * 实现语法分析的功能
 * @author lxm
 *
 */
public class SyntaxAnalyzerImpl implements SyntaxAnalyzer {
	private AnalyzeTable at;															// SLR(1)分析表
	private LexicalAnalyzer la;															// 词法分析器
	private SymbolTable st = new SymbolTable();											// 符号表
	private Stack<Integer> sstate = new Stack<Integer>();								// 状态栈
	private Stack<V> ssymbol = new Stack<>();											// 符号栈
	private List<Quad> quadlist = new ArrayList<>();									// 生成的四元式队列
	private Map<String, SemanticAction> saObjs = new HashMap<>();						// 语义动作映射
	private Map<String, Method> samethods = new HashMap<String, Method>();				// 动作方法映射
	private ShowPanel sp;
	
	public SyntaxAnalyzerImpl(InputStream analyzeTable) {
		// 加载分析表
		at = AnalyzeTable.load(analyzeTable);
		if (at == null) {
			throw new NullPointerException(" cannot be null");
		}
		sp = new ShowPanel();
		JFrame fr = new JFrame("Analyze Tree");
		fr.setLocationByPlatform(true);
		fr.setSize(1024, 768);
		fr.getContentPane().add(sp, BorderLayout.CENTER);
		fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fr.enableInputMethods(false);
		fr.setVisible(true);
    }
	public JPanel getShowPanel() {
		return sp;
	}
	
	@Override
	public void setInput(LexicalAnalyzer la) {
		if (la == null) {
			throw new NullPointerException("Lexical Analyzer cannot be null");
		}
		this.la = la;
	}
	public SymbolTable getSymbolTable() {
	    return st;
    }
	
	@Override
	public List<Quad> analyse() {
		quadlist.clear();																// 清空四元式队列
		int currState = 0;
		Action action;
		sstate.push(0);																	// 初始状态压栈
		V term = new V();
		term.name = GrammarAnalyser.TERMINATOR;
		term = new DrawableV(term);
		ssymbol.push(term);																// '#'压栈
		V v = la.next();
		if(v != null) {
			v = new DrawableV(v);
		} else {
			v = term;
		}
		sp.addSymbol((DrawableV)v);
		V[] param;
		Production p;
		while(true){
			v = (v == null) ? term : v; 
			currState = sstate.peek();													// 查看栈顶的状态
			String input = v.name;														// 当前输入
			action = at.query(currState, input);										// 获得对于的动作
			if (action == null){ 
				/* 
				 * 出错建议机制
				 * 如果Action为NULL, 说明我们遇到了一个不能顺利进行分析的符号;
				 * 为了帮助用户解决问题，我们可以采取以下策略：在该符号前插入
				 * 所有可能输入的终结符中的一个，尝试继续分析过程，如果能够成功，
				 * 并且能将原来不能顺利分析的符号移进掉，则认为该输入是有效的，
				 * 并将其作为建议之一。
				 * 例如：假设有输入串 a := 3 2;
				 * 在扫描到第二个数字时发现无法规约，这时遍历所有的终结符，在其中选
				 * 一个插入2之前，如果插入的终结符使得该式能够成功移近（“吃”）掉2（比如+和*
				 * 都可以规约掉2，分别成为加值和乘积值）,则认为该终结符可以被建议。
				 */
				// 取得不能被规约符号的符号名
				String nextVt = (String) v.name;
				Set<String> proposal = new HashSet<>();
				// 遍历所有可能的终结符
outer:			for(String vtx : at.getVT()) {
					// 模拟
					Action ax; 
					// 复制状态栈，无需复制符号栈。
					Stack<Integer> sstack2 = new Stack<Integer>();
					sstack2.addAll(sstate);
					// 进行模拟规约，直到插入的终结符被规约掉。
					int currstate2;
inner:				while(true) {
						currstate2 = sstack2.peek();
						ax = at.query(currstate2, vtx);
						// 如果走不通，直接看下一个终结符
						if(ax == null) continue outer;
						switch(ax.getType()) {
							// 如果是归约而非移进，继续
							case Action.REDUCTION:
								int poplen = ax.getP().getRight().length;
								for(int i = 0; i < poplen; i++)
									sstack2.pop();
								ax = at.query(sstack2.peek(), ax.getP().getLeft());
								if(ax == null || ax.getType() != Action.GOTO) continue outer;
								sstack2.push(ax.getState());
								break;
							case Action.GOTO:
								// this should not occur.
								continue outer;
							case Action.ACC:
								// 如果能接受ACC，则认为该符号可用
								proposal.add(vtx);
								continue outer;
							case Action.STEPINTO:
								// 移进掉了插入的符号
								sstack2.push(ax.getState());
								break inner;
						}
					}
					// 规约原有的符号
					currstate2 = sstack2.peek();
					ax = at.query(currstate2, nextVt);
					// 如果仍然不能被规约，说明该插入终结符无效
					if(ax == null) continue;
					proposal.add(vtx);
				}
				// 生成格式化的错误信息
				StringBuffer err = new StringBuffer();
				err.append("SYNTAX ERROR - Unexpected symbol: ").append(v.attr("value"));
				err.append(", expect these symbols:").append(proposal);
				throw new RuntimeException(err.toString());
			}
			// 根据不同类型执行不同的动作
			switch (action.getType()) {
			// 接受
			case Action.ACC:
				p = action.getP();
				param = new V[p.getRight().length];
				for(int i = 0; i < param.length; i++){
					param[param.length - 1 - i] = ssymbol.pop();
					sstate.pop();
				}
				V vnx = new V();
				vnx.name = p.getLeft();
				vnx.isFinal = false;
				vnx = new DrawableV(vnx);
				SemanticAction(p, vnx, param);
				sp.reduction(vnx, param);
				System.out.println("Analyze complete!");
				st.allocateAddr();
				break;
				// 跳转
			case Action.GOTO:
				throw new RuntimeException("Unexpected 'Goto' action: " + action);
				// 规约
			case Action.REDUCTION:
				p = action.getP();
				param = new V[p.getRight().length];
				for(int i = 0; i < param.length; i++){
					param[param.length - 1 - i] = ssymbol.pop();
					sstate.pop();
				}
				String tmp = p.getLeft();
				V vn = new V();
				vn.name = tmp;
				vn.isFinal = false;
				vn = new DrawableV(vn);
				SemanticAction(p, vn, param);
				sp.reduction(vn, param);
				action = at.query(sstate.peek(), tmp);
				if(action != null && action.getType() == Action.GOTO && action.getState() != -1){
					ssymbol.push(vn);
					sstate.push(action.getState());
				} else {
					throw new RuntimeException("Reduction error at: " +  v.attr("value"));
				}
				break;
				// 移进
			case Action.STEPINTO:
				sstate.push(action.getState());
				ssymbol.push(v);
				v = la.next();
				if(v != null) {
					v = new DrawableV(v);
				} else {
					v = term;
				}
				sp.addSymbol((DrawableV)v);
				break;
				// 出错
			default:
				throw new RuntimeException("Stepinto error at: " + v.attr("value"));
			}
			if(action.getType() == Action.ACC){
				break;
			}
		}
		return quadlist;
	}
	
	/**
	 * 根据要规约的产生式,执行相应的语义动作
	 * @param p
	 * @param left
	 * @param param
	 */
	private void SemanticAction(Production p, V left, V[] param){
		String a = p.getAction();														// 获得动作执行路径
		if (a == null) return;
		int pos;
		String clazz = a.substring(0, pos = a.lastIndexOf('.'));						
		String method = a.substring(pos + 1);
		SemanticAction obj = saObjs.get(clazz);
		if (obj == null) {
			try {
				// 获得类对象的实例
				Class<?> cx = Class.forName(clazz);
				obj = (SemanticAction) cx.newInstance();
				obj.setQlist(quadlist);
				obj.setSt(st);
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
		Method mx = samethods.get(method);
		if(mx == null){
			try {
				// 获得对象方法,调用方法
				mx = obj.getClass().getMethod(method, V.class, V[].class);
				samethods.put(a, mx);
		        mx.invoke(obj, left, param);
	        } catch (Exception e) {
	        	throw new RuntimeException(e);
        }
		}
	}
	@Override
	public List<Quad> getQuad() {
		return quadlist;
	}
}

class ShowPanel extends JPanel {
	private static final long serialVersionUID = 2469534508763564176L;
	private static final Color COLOR_BG; static {
		COLOR_BG = new Color(0xDDEEFF);
	}
	
	private int offsetX = 0, offsetY = 0;
	private int clickX = Integer.MIN_VALUE, clickY = Integer.MIN_VALUE;
	private boolean clicked = false;
	private List<VCol> cols = new ArrayList<>();
	private BufferedImage buf, small;
	private Graphics gbuf, gsmall;
	
	public void addSymbol(DrawableV vx){
		vx.col = cols.size();
		VCol vrx = new VCol(vx.col);
		vrx.add(vx);
		synchronized (cols) {
			cols.add(vrx);
		}
		repaint();
	}
	public void reduction(V left_, V[] right_){
		DrawableV left = (DrawableV) left_;
		if(right_.length == 0) {
			// null production
			left.col = cols.size();
			VCol vrx = new VCol(left.col);
			vrx.add(left, 1);
			cols.add(vrx);
			repaint();
			return;
		}
		int colSum = 0, maxRow = -1;
		int leftCol = Integer.MAX_VALUE, rightCol = Integer.MIN_VALUE;
		for(V vx_ : right_) {
			DrawableV vx = (DrawableV) vx_;
			colSum += vx.col;
			vx.to = left;
			if(vx.row > maxRow) {
				maxRow = vx.row;
			}
			if(vx.col > rightCol) {
				rightCol = vx.col;
			}
			if(vx.col < leftCol) {
				leftCol = vx.col;
			}
		}
		int col = (int) Math.ceil((double)colSum / right_.length);
		left.fromLeft = leftCol < col;
		left.fromRight = rightCol > col;
		VCol rx = cols.get(col);
		left.col = col;
		rx.add(left, maxRow + 1);
		repaint();
	}
	public ShowPanel() {
		MouseAdapter ma = new MouseAdapter() {
			private int lastX, lastY;
			@Override
			public void mousePressed(MouseEvent e) {
				lastX = e.getX();
				lastY = e.getY();
			}
			@Override
			public void mouseDragged(MouseEvent e) {
				offsetX += e.getX() - lastX;
				offsetY += e.getY() - lastY;
				lastX = e.getX();
				lastY = e.getY();
				clicked = false;
				repaint();
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				clickX = e.getX();
				clickY = e.getY();
				clicked = true;
				repaint();
			}
		};
		addMouseMotionListener(ma);
		addMouseListener(ma);
		addComponentListener(new ComponentAdapter() {
			@Override public void componentShown(ComponentEvent e) {
				componentResized(e);
			}
			@Override public void componentResized(ComponentEvent e) {
				buf = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_3BYTE_BGR);
				gbuf = buf.getGraphics();
				((Graphics2D)gbuf).setStroke(DrawableV.SLINE);
				((Graphics2D)gbuf).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				repaint();
			}
		});
		small = new BufferedImage(200, 160, BufferedImage.TYPE_3BYTE_BGR);
		//gsmall = small.getGraphics();
	}
	public void update(Graphics g) { paint(g); }
	public void paint(Graphics gscr) {
		Graphics g = gbuf.create();
		int cX = clickX - offsetX, cY = clickY - offsetY;
		BufferedImage tips = null;
		g.setColor(COLOR_BG);
		g.fillRect(0, 0, getWidth(), getHeight());
		gsmall = small.createGraphics();
		gsmall.setColor(COLOR_BG);
		gsmall.fillRect(0, 0, small.getWidth(), small.getHeight());
		gsmall.setColor(Color.black);
		gsmall.drawRect(0, 0, small.getWidth() - 1, small.getHeight() - 1);
		double rate = (double)small.getHeight() / maxColHegiht, rate2 = (double)small.getWidth() / maxColWidth;
		rate = rate < rate2? rate: rate2;
		((Graphics2D)gsmall).transform(AffineTransform.getScaleInstance(rate, rate));
		
		g.translate(offsetX, offsetY);
		int w = getWidth(), h = getHeight();
		Rectangle viewport = new Rectangle(-offsetX, -offsetY, w, h);
		gsmall.drawRect(viewport.x, viewport.y, viewport.width, viewport.height);
		g.setColor(DrawableV.COLOR_LINE);
		VCol[] arr;
		synchronized (cols) {
			arr = cols.toArray(new VCol[0]);
		}
		for(VCol vcx : arr) {
			for(DrawableV dvx : vcx.getArr()) {
				Rectangle rx = dvx.getRect();
				dvx.drawImage(gsmall);
				if(viewport.intersects(rx)) {
					dvx.drawImage(g);
					if(clicked && rx.contains(cX, cY)) {
						tips = dvx.getTip();
					}
				}
				Line lx = dvx.getLine();
				if(lx != null && lx.intersect(viewport)) {
					g.drawLine(lx.pt1.x, lx.pt1.y, lx.pt2.x, lx.pt2.y);
				}
			}
		}
		if(tips != null) {
			g.drawImage(tips, cX, cY, null);
		}
		gscr.drawImage(buf, 0, 0, null);
		gscr.drawImage(small, buf.getWidth() - small.getWidth() - 20, 20, null);
	}
	private int maxColWidth = 0, maxColHegiht = 0;
	class VCol{
		public int width = 10, height = 10;
		private int colNum, x;
		private List<DrawableV> list = new ArrayList<DrawableV>();
		private DrawableV[] arr;
		private int maxRowNum = 0;
		public VCol(int colnum) {
			this.colNum = colnum;
			x = 5;
			for(VCol vcx : cols) {
				x += vcx.width + 5;
			}
		}
		public void add(DrawableV v) {
			add(v, list.size());
		}
		public void add(DrawableV v, int row){
			v.row = row;
			int offset = 0;
			if(width < v.getWidth()) {
				offset += v.getWidth() - width;
				width = v.getWidth();
			}
			if(maxRowNum < row) {
				maxRowNum = row;
				height = maxRowNum * (DrawableV.Height + 15) + 15;
				if(height > maxColHegiht) {
					maxColHegiht = height;
				}
			}
			if(offset > 0) {
				moveRight(offset / 2);
				for(int i = colNum + 1; i < cols.size(); i++) {
					cols.get(i).moveRight(offset);
				}
			}
			v.setX(x + (width - v.getWidth()) / 2);
			v.setY(row * (DrawableV.Height + 15));
			list.add(v);
			arr = list.toArray(new DrawableV[0]);
		}
		private void moveRight(int offset){
			for(DrawableV  vx : list){
				vx.moveX(offset);
			}
			x += offset;
			if(x + width > maxColWidth) {
				maxColWidth = x + width;
			}
		}	
		public DrawableV[] getArr() {
			return arr;
		}
	}
}

class Line {
	public Point pt1, pt2;
	public Line(int x1, int y1, int x2, int y2) {
		pt1 = new Point();
		pt2 = new Point();
		pt1.x = x1;
		pt2.x = x2;
		pt1.y = y1;
		pt2.y = y2;
	}
	private final int ptlr(Point p1, Point px, Point p2) {
		return (p1.x - p2.x) * (px.y - p2.y) - (p1.y - p2.y) * (px.x - p2.x);
	}
	// 判断两条线段是否相交
	private boolean lineInter(Point p11, Point p12, Point p21, Point p22) {
		return (ptlr(p21, p22, p11) * ptlr(p21, p22, p12) <= 0 ||
			ptlr(p11, p12, p21) * ptlr(p11, p12, p22) <= 0);
	}
	private Point tmp1 = new Point(), tmp2 = new Point();
	private Point tmppt1(int x, int y) {
		tmp1.x = x; tmp1.y = y; return tmp1;
	}
	private Point tmppt2(int x, int y) {
		tmp2.x = x; tmp2.y = y; return tmp2;
	}
	public boolean intersect(Rectangle r) {
		// Two point both are in rect
		if (r.contains(pt1) || r.contains(pt2))
			 return true;
		// Two point both aren't in rect
		if (lineInter(pt1, pt2, tmppt1(r.x, r.x), tmppt2(r.x + r.width, r.y + r.height)) ||
			lineInter(pt1, pt2, tmppt1(r.x + r.width, r.x), tmppt2(r.x, r.y + r.height)))
			return true;
		return false;
	}
}