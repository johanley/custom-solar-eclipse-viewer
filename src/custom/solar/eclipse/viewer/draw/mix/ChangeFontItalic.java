package custom.solar.eclipse.viewer.draw.mix;

import java.awt.Font;
import java.awt.Graphics2D;

/** Italicize the current font. */
public final class ChangeFontItalic extends ChangeFont {

  public static ChangeFontItalic chain(DrawingContext context, Graphics2D g) {
    return new ChangeFontItalic(context, g);
  }
  
  public ChangeFontItalic(Graphics2D g) {
    this(null, g);
  }
  
  public void change() {
    super.change();
    g.setFont(origFont.deriveFont(Font.ITALIC));
  }; 
  
  private ChangeFontItalic(DrawingContext wrappee, Graphics2D g) {
    super(wrappee, g);
  }

}
