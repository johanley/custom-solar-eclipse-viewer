package custom.solar.eclipse.viewer.draw.mix;

import java.awt.Font;
import java.awt.Graphics2D;

/** Change the current font size by a given factor. */
public class ChangeFontSize extends ChangeFont {
  
  public static ChangeFontSize chain(DrawingContext context, Graphics2D g, float factor) {
    return new ChangeFontSize(context, g, factor);
  }
  
  public ChangeFontSize(Graphics2D g, float factor) {
    this(null, g, factor);
  }
  
  public void change() {
    super.change();
    Font differentSizedFont = origFont.deriveFont(origFont.getSize() * factor);
    g.setFont(differentSizedFont);
  }; 
  
  private Float factor;
  
  private ChangeFontSize(DrawingContext wrappee, Graphics2D g, float factor) {
    super(wrappee, g);
    this.factor = factor;
  }
}
