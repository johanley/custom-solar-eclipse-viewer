package custom.solar.eclipse.viewer.draw.mix;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;

/** 
 Alter the current font in some way.
 
 Implementations need to change the existing font using Font.deriveFont(). 
*/
public abstract class ChangeFont extends DrawingContextWrapper {
  
  public ChangeFont(DrawingContext wrappee, Graphics2D g) {
    super(wrappee);
    this.g = g;
    this.origFont = g.getFont();
    this.origStroke = g.getStroke();
    this.origColor = g.getColor();
  }
  
  @Override public void reverse() {
    g.setFont(origFont);
    g.setStroke(origStroke);
    g.setColor(origColor);
    super.reverse();
  }
  
  protected Font origFont;
  protected Stroke origStroke;
  protected Color origColor;
  protected Graphics2D g;

}
