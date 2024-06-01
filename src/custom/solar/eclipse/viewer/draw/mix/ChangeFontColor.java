package custom.solar.eclipse.viewer.draw.mix;

import java.awt.Color;
import java.awt.Graphics2D;

/** Change the color using in drawing and writing text. */
public class ChangeFontColor extends ChangeFont {
  
  public static ChangeFontColor chain(DrawingContext context, Graphics2D g, Color color) {
    return new ChangeFontColor(context, g, color);
  }
  
  public ChangeFontColor(Graphics2D g, Color color) {
    this(null, g, color);
  }
  
  public void change() {
    super.change();
    g.setColor(color);
  }; 
  
  private Color color;
  
  private ChangeFontColor(DrawingContext wrappee, Graphics2D g, Color color) {
    super(wrappee, g);
    this.color = color;
  }
}
