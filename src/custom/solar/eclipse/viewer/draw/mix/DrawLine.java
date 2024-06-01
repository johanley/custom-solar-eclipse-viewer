package custom.solar.eclipse.viewer.draw.mix;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D;

/** Draw a line from the origin, either horizontally to the right (increasing x) or vertically downward (increasing y). */
public final class DrawLine implements Draw {
  
  public static enum Direction {
    right, 
    down;
  }
  
  public DrawLine(double length, Direction direction){
    this.length = length;
    this.direction = direction;
  }
  
  @Override public void draw(Graphics2D g) {
    Shape line = null;
    if (Direction.right == direction) {
      line = new Line2D.Double(0.0, 0.0, length, 0.0);
    }
    else {
      line = new Line2D.Double(0.0, 0.0, 0.0, length);
    }
    g.draw(line);
  }
  
  private double length;
  private Direction direction;
}
