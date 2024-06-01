package custom.solar.eclipse.viewer.draw.mix;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

/** Translate the origin of the coordinate system by a given delta. */
public class ChangeCoordsTranslate extends ChangeCoords {
  
  public static ChangeCoordsTranslate chain(DrawingContext context, Graphics2D g, Point2D.Double delta) {
    return new ChangeCoordsTranslate(context, g, delta);
  }

  public ChangeCoordsTranslate(Graphics2D g, Point2D.Double delta) {
    this(null, g, delta);
  }
  
  public void change() {
    super.change();
    AffineTransform affTr = new AffineTransform();
    affTr.translate(delta.x, delta.y);
    g.transform(affTr);
  };
  
  private Point2D.Double delta;
  
  private ChangeCoordsTranslate(DrawingContext wrappee, Graphics2D g, Point2D.Double where) {
    super(wrappee, g);
    this.delta = where;
  }
  
}
