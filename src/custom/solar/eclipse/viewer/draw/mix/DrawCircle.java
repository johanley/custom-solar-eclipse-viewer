package custom.solar.eclipse.viewer.draw.mix;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

/** Draw a circle of a given radius at the origin. */
public final class DrawCircle implements Draw {
  
  public DrawCircle(double radius, boolean fill){
    this.radius = radius;
    this.fill = fill;
  }
  
  @Override public void draw(Graphics2D g) {
    //this method uses an enclosing rectangle to define the circle
    Shape circle = new Ellipse2D.Double(-radius, -radius, radius*2, radius*2);
    if (fill) {
      g.fill(circle);
    }
    else {
      g.draw(circle);
    }
  }
  
  private double radius;
  private boolean fill;
}
