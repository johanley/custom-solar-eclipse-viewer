package custom.solar.eclipse.viewer.draw.mix;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

/** 
 Change to origin in order right-align text, instead of the default left-align.
 Applies a small delta to the origin. 
*/
public class ChangeCoordsRightAlignText extends ChangeCoords {
  
  public static ChangeCoordsRightAlignText chain(DrawingContext context, Graphics2D g, String text) {
    return new ChangeCoordsRightAlignText(context, g, text);
  }
  
  public ChangeCoordsRightAlignText(Graphics2D g, String text) {
    this(null, g, text);
  }
  
  public void change() {
    super.change();
    AffineTransform affTr = new AffineTransform();
    Point2D.Double tweaked = tweakedWhere(g);
    affTr.translate(tweaked.x, tweaked.y);
    g.transform(affTr);
  };
  
  private String text;
  
  private ChangeCoordsRightAlignText(DrawingContext context, Graphics2D g, String text) {
    super(context, g);
    this.text = text;
  }
  
  /** Render text centered on the given spot. */
  private Point2D.Double tweakedWhere(Graphics2D g) {
    return new Point2D.Double(- textWidth(text, g), 0);
  }
  
  /** Return the width of the given text, when rendered in the given context. */
  private int textWidth(String str, Graphics2D g) {
    return g.getFontMetrics().stringWidth(str);
  }
}
