package custom.solar.eclipse.viewer.draw.mix;

import java.awt.BasicStroke;
import java.awt.Graphics2D;

/** Set the stroke width to a new value. */
public class ChangeFontStrokeWidth extends ChangeFont {
  
  public static ChangeFontStrokeWidth chain(DrawingContext context, Graphics2D g, float width) {
    return new ChangeFontStrokeWidth(context, g, width);
  }
  
  public ChangeFontStrokeWidth(Graphics2D g, float width) {
    this(null, g, width);
  }
  
  public void change() {
    super.change();
    g.setStroke(new BasicStroke(width));
  }; 
  
  private Float width;
  
  private ChangeFontStrokeWidth(DrawingContext wrappee, Graphics2D g, float factor) {
    super(wrappee, g);
    this.width = factor;
  }
}
