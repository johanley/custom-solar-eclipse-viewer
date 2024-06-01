package custom.solar.eclipse.viewer.draw.mix;

import java.awt.Graphics2D;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import custom.solar.eclipse.viewer.math.Maths;

/** Center some text at the current origin, by applying a small delta to center text at the current origin. */
public class ChangeCoordsCenterText extends ChangeCoords {
  
  public static ChangeCoordsCenterText chain(DrawingContext context, Graphics2D g, String text) {
    return new ChangeCoordsCenterText(context, g, text);
  }
  
  public ChangeCoordsCenterText(Graphics2D g, String text) {
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
  
  private ChangeCoordsCenterText(DrawingContext context, Graphics2D g, String text) {
    super(context, g);
    this.text = text;
  }
  
  /** Render text centered on the given spot. */
  private Point2D.Double tweakedWhere(Graphics2D g) {
    return new Point2D.Double(- textWidth(text, g)/2, textHeight(text, g)/2);
  }
  
  /** Return the height of the given text, when rendered in the given context. */
  private int textHeight(String str, Graphics2D g) {
    LineMetrics lm = g.getFont().getLineMetrics(str, g.getFontRenderContext());
    float ascent = lm.getAscent();
    float descent = lm.getDescent();
    float height = ascent + descent; //don't include the leading!
    float FUDGE_FACTOR_FOR_AESTHETIC_PURPOSES = 0.60F; //without this the centering is off - too low
    return Maths.round(height*FUDGE_FACTOR_FOR_AESTHETIC_PURPOSES); 
  }

  /** Return the width of the given text, when rendered in the given context. */
  private int textWidth(String str, Graphics2D g) {
    return g.getFontMetrics().stringWidth(str);
  }
}
