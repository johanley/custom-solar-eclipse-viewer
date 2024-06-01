package custom.solar.eclipse.viewer.draw;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import custom.solar.eclipse.viewer.config.Config;
import custom.solar.eclipse.viewer.config.Constants;
import custom.solar.eclipse.viewer.draw.mix.ChangeCoordsCenterText;
import custom.solar.eclipse.viewer.draw.mix.ChangeCoordsRotate;
import custom.solar.eclipse.viewer.draw.mix.ChangeCoordsTranslate;
import custom.solar.eclipse.viewer.draw.mix.ChangeFontSize;
import custom.solar.eclipse.viewer.draw.mix.Draw;
import custom.solar.eclipse.viewer.draw.mix.DrawLine;
import custom.solar.eclipse.viewer.draw.mix.DrawText;
import custom.solar.eclipse.viewer.draw.mix.DrawingContext;
import custom.solar.eclipse.viewer.math.Maths;

/** 
 Ruled edge, for measuring the angular separation of objects in the sky.

 <P>This can be used to track the motion of incoming clouds, or the separation 
 of a planet from the Sun during totality.
 
 <P>One tick for each degree, 0 up to a maximum angle.
  The maximum depends on the configured nominal distance from the user's eye to the viewer.
*/
final class RulerSeparation implements Draw {
  
  RulerSeparation(Config config) {
    this.config = config;
  }
  
  /** 
   Ruled edge on the left side of the viewer card.
   Zero degrees is at the bottom left, and it increases upward.
  */
  @Override public void draw(Graphics2D g) {
    double angle = 0.0;
    double maxAngle = maxAngle();
    double armsLength = cmToPoints(config.armsLength()); //points
    int degrees = 0;
    while (angle < maxAngle) {
      double distanceFromBottomUp = Math.tan(angle) * armsLength; //points
      double yLevel = config.viewerHeight() - distanceFromBottomUp + config.viewerTopMargin(); //points
      
      horizontalLineAt(yLevel, degrees, g);
      if (degrees > 0) {
        numDegreesAt(yLevel, degrees, g);
      }
        
      angle = angle + ANGULAR_INTERVAL;
      ++degrees;
    }
  }

  private Config config;
  private static final double TICK_SIZE = 5.0;
  private static final double ANGULAR_INTERVAL = Maths.degToRads(1.0);
  private static final double CM_PER_INCH = 2.54;
  
  /** Radians. Always within the range 0..pi/4. */
  private double maxAngle() {
    return Math.atan2(config.viewerHeight()/*points*/, cmToPoints(config.armsLength()));
  }
  
  private double cmToPoints(Double cm) {
    return (cm / CM_PER_INCH) * Constants.POINTS_PER_INCH;
  }
  
  private void horizontalLineAt(double yLevel, int count, Graphics2D g) {
    Point2D.Double where = new Point2D.Double(xLevel(), yLevel);
    DrawingContext context = new ChangeCoordsTranslate(g, where);
    Draw draw = new DrawLine(tickLen(count), DrawLine.Direction.right);
    draw.drawIn(context, g);
  }
  
  /** Rotate ninety degrees anti-clockwise. */
  private void numDegreesAt(double yLevel, Integer degrees, Graphics2D g) {
    Point2D.Double where = new Point2D.Double(xLevel() + 10.0, yLevel);
    Float fontSize = (degrees % 5 != 0) ? 0.6f : 0.8f;
    String text = degrees.toString() + "°";
    
    DrawingContext context = new ChangeFontSize(g, fontSize);
    context = ChangeCoordsTranslate.chain(context, g, where);
    context = ChangeCoordsRotate.chain(context, g, -Maths.HALF_PI);
    context = ChangeCoordsCenterText.chain(context, g, text);
    
    Draw drawer = new DrawText(text);
    drawer.drawIn(context, g);
  }
  
  /** The left border */
  private double xLevel() {
    return config.width() * 0.5 - config.viewerWidth() * 0.5;
  }
  
  private double tickLen(int count) {
    return TICK_SIZE;
  }
}
