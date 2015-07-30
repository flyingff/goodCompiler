package syntax;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Map.Entry;

public class DrawableV extends V {
	public static final Font FONT = new Font("YaHei Mono", Font.PLAIN, 14);
	private static final FontMetrics fm;
	public static final int Height, FHeight, FDescent; static {
		Graphics gx = new BufferedImage(1, 1, BufferedImage.TYPE_3BYTE_BGR).getGraphics();
		gx.setFont(FONT);
		fm = gx.getFontMetrics();
		FHeight = fm.getHeight();
		FDescent = fm.getDescent();
		Height = FHeight + 10;
	}
	public static final Stroke SLINE;
	public static final Color COLOR_V, COLOR_ATTR, COLOR_LINE; static {
		COLOR_LINE = new Color(0x990066);
		SLINE = new BasicStroke(2.0f);
		COLOR_V = new Color(0x006699);
		COLOR_ATTR = new Color(0xFFFF66);
	}
	private int width;
	private Point upper = new Point(), lower = new Point();
	private Rectangle rect = new Rectangle();
	private BufferedImage img;
	public int row, col;
	public DrawableV to;
	public boolean fromLeft = false, fromRight = false;
	public void setX(int x) {
		lower.x = upper.x = x + width / 2;
		rect.x = x;
	}
	public void setY(int y) {
		lower.y = (upper.y = y) + Height;
		rect.y = y;
	}
	public void moveX(int offset){
		setX(offset + rect.x);
	}
	public Point getUpperPt(){
		return upper;
	}
	public Point getLowerPt() {
		return lower;
	}
	public Rectangle getRect() {
		return rect;
	}
	public DrawableV(V v) {
		this.isFinal = v.isFinal;
		this.name = v.name;
		this.attr = v.attr;
		width = fm.stringWidth(name) + 14;
		img = new BufferedImage(width, Height, BufferedImage.TYPE_3BYTE_BGR);
		Graphics g = img.getGraphics();
		g.setColor(COLOR_V);
		g.fillRect(0, 0, width, Height);
		g.setColor(Color.black);
		g.drawRect(0, 0, width - 1, Height - 1);
		g.setColor(Color.white);
		g.setFont(FONT);
		g.drawString(name, 5, FHeight - FDescent + 5);
		rect.height = Height;
		rect.width = width;
	}
	public int getWidth() {
		return width;
	}
	public void drawImage(Graphics g) {
		g.drawImage(img, rect.x, rect.y, null);
	}
	public Line getLine(){
		if(to != null) {
			return new Line(lower.x, lower.y, to.upper.x, to.upper.y);
		}
		return null;
	}
	public BufferedImage getTip(){
		ArrayList<String> attrs = new ArrayList<String>();
		int maxwidth = 0;
		StringBuilder sb = new StringBuilder();
		int wx;
		for(Entry<String, Object> ex : attr.entrySet()) {
			sb.setLength(0);
			sb.append(ex.getKey()).append(" : ").append(ex.getValue());
			String sx = sb.toString();
			wx = fm.stringWidth(sx);
			if(wx > maxwidth) {
				maxwidth = wx;
			}
			attrs.add(sx);
		}
		int h = 8 + attrs.size() * (2 + FHeight), w = maxwidth + 14;
		BufferedImage tip = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
		Graphics g = tip.getGraphics();
		g.setColor(COLOR_ATTR);
		g.fillRect(0, 0, w, h);
		g.setColor(Color.black);
		g.drawRect(0, 0, w - 1, h - 1);
		int drawY = FHeight + 5 - FDescent;
		g.setFont(FONT);
		for(String sx : attrs) {
			g.drawString(sx, 7, drawY);
			drawY += FHeight + 2;
		}
		//JOptionPane.showMessageDialog(null, "", "", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(tip));
		return tip;
	}
}
