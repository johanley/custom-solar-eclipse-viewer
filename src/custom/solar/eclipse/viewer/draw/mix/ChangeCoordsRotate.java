package custom.solar.eclipse.viewer.draw.mix;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

/** Rotate the coordinate system about the origin by a given angle. */
public class ChangeCoordsRotate extends ChangeCoords {
  
  public static ChangeCoordsRotate chain(DrawingContext context, Graphics2D g, double rotationAngle) {
    return new ChangeCoordsRotate(context, g, rotationAngle);
  }

  public ChangeCoordsRotate(Graphics2D g, double rotationAngle) {
    this(null, g, rotationAngle);
  }
  
  public void change() {
    super.change();
    AffineTransform affTr = new AffineTransform();
    affTr.rotate(rotationAngle);
    g.transform(affTr);
  };
  
  private double rotationAngle;
  
  private ChangeCoordsRotate(DrawingContext wrappee, Graphics2D g, double rotationAngle) {
    super(wrappee, g);
    this.rotationAngle = rotationAngle;
  }
}
