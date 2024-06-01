package custom.solar.eclipse.viewer.draw.mix;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

/** Change the coordinate system using by drawing operations. */
public class ChangeCoords extends DrawingContextWrapper {
  
  public ChangeCoords(DrawingContext context, Graphics2D g) {
    super(context);
    this.g = g;
    this.orig = g.getTransform();
  }
  
  @Override public void reverse() {
    g.setTransform(orig);
    super.reverse();
  }
  
  protected static final Point2D.Double ORIGIN = new Point2D.Double(0.0, 0.0);
  protected AffineTransform orig;
  protected Graphics2D g;

}
