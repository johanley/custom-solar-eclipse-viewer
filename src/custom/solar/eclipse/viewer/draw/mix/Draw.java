package custom.solar.eclipse.viewer.draw.mix;

import java.awt.Graphics2D;

/** Draw something on the current graphics context. */
public interface Draw {

  void draw(Graphics2D g);
  
  /**
   Change the graphics context before drawing, then reverse out all the changes.
   The caller uses this method when chaining together temporary changes to the drawing context.
  */
  default void drawIn(DrawingContext context, Graphics2D g) {
    context.change();
    draw(g);
    context.reverse();
  }
  
}
