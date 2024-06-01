package custom.solar.eclipse.viewer.draw.mix;

import java.awt.Font;
import java.awt.Graphics2D;

/** Bold text. */
public final class ChangeFontBold extends ChangeFont {
  
  public static ChangeFontBold chain(DrawingContext context, Graphics2D g) {
    return new ChangeFontBold(context, g);
  }
  
  public ChangeFontBold(Graphics2D g) {
    this(null, g);
  }
  
  public void change() {
    super.change();
    origFont = g.getFont();
    g.setFont(origFont.deriveFont(Font.BOLD));
  }; 
  
  private ChangeFontBold(DrawingContext wrappee, Graphics2D g) {
    super(wrappee, g);
  }

}
