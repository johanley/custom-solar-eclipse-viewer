package custom.solar.eclipse.viewer.draw.mix;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

/** Draw a rectangle of a given width, height, centered at the origin. */
public final class DrawRectangle implements Draw {
  
  public DrawRectangle(double width, double height){
    this.width = width;
    this.height = height;
  }
  
  @Override public void draw(Graphics2D g) {
    Shape rectangle = new Rectangle2D.Double(-width*0.5, -height*0.5, width, height);
    g.draw(rectangle);
  }
  
  private double width;
  private double height;
}
