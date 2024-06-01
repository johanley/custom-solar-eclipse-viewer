package custom.solar.eclipse.viewer.draw;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Point2D;

import custom.solar.eclipse.viewer.config.Config;
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
 Ruled edge, for measuring the position angle of objects with respect to the Sun.
 The object could be a planet, or an approaching cloud.

 <P>One tick for every 5 degrees, 0..180.
 
 <P>The central point for measuring the angle is the pin-hole near the middle of the viewer.
 That way, the pin-hole can be used to center the Sun versus this scale.
*/
final class RulerPositionAngle implements Draw {
  
  RulerPositionAngle(Config config) {
    this.config = config;
  }
  
  /** 
   Ruled edge on the top, right, and bottom of the viewer card.
   Zero degrees is at the top, and it increases clockwise. 
   Thus, to get the actual position angle a mental calculation is needed.
  */
  @Override public void draw(Graphics2D g) {
    double angle = 0.0;
    double maxAngle = Maths.degToRads(180.0);
    int degrees = 0;
    while (angle < maxAngle) {
      Point2D.Double shadowHole = shadowHole();
      CornerAngle cornerAngle = new CornerAngle(shadowHole);
      radialTickLine(g, angle, shadowHole);
      if (degrees > 0) {
        renderNumDegrees(g, angle, degrees, shadowHole, cornerAngle);
      }
      angle = angle + ANGULAR_INTERVAL;
      degrees = degrees + DEGREE_INTERVAL;
    }
  }

  private Config config;
  private static final int DEGREE_INTERVAL = 5;
  private static final double ANGULAR_INTERVAL = Maths.degToRads(DEGREE_INTERVAL);
  private static final double OFFSET = 0.97;

  /** Offset the text a bit from the tick line. */
  private static final class OffsetText {
    static double top = 0.94;
    static double bottom = 0.96;
    static double side = 0.94;
  }
  
  private Point2D.Double shadowHole(){
    return new Point2D.Double(config.width()*0.5, config.viewerHeight()*Holes.AT_SHADOW_HOLE_LEVEL + config.viewerTopMargin()); 
  }
  
  private void radialTickLine(Graphics2D g, double angle, Point2D.Double shadowHole) {
    //straight lines from the shadow-hole to an edge
    DrawingContext context = new ChangeCoordsTranslate(g, shadowHole);
    context = ChangeCoordsRotate.chain(context, g, -angle);
    
    //use a rectangular clipping region to block off most of radial lines drawn below, such that the line is only rendered near the edge of the card
    g.setClip(rectangularMask()); //NOTE: the clipping region is not sensitive to the above changes in the coord system
    Draw draw = new DrawLine(config.width()*4, DrawLine.Direction.down); //any large length will do here
    draw.drawIn(context, g);
    g.setClip(null); //removes the clipping region
  }

  /** 
   Take the border rectangle, and subtract an 'inner' rectangle similar to it.
   These operations are done when the context is already at the center of the viewer. 
  */
  private Shape rectangularMask() {
    Shape border = rectangle(1.0);
    Shape insetBorder = rectangle(OFFSET);
    Area result = new Area(border);
    result.subtract(new Area(insetBorder));
    return result;
  }
  
  private Shape rectangle(double factor) {
    Point topLeft = new Point(
      Maths.round((config.width()*0.5 - config.viewerWidth()*0.5*factor)), 
      Maths.round(config.viewerTopMargin() + config.viewerHeight()*0.5*(1-factor)) 
    );
    Dimension dim = new Dimension(
      Maths.round(config.viewerWidth()*factor), 
      Maths.round(config.viewerHeight()*factor)
    );
    return new Rectangle(topLeft, dim);
  }
  
  /** This is trickier because the clipping region can't be used; we need to tediously calculate a 'where'. */
  private void renderNumDegrees(Graphics2D g, double angle, int degrees, Point2D.Double shadowHole, CornerAngle cornerAngle) {
    Point2D.Double where = whereToWriteNumber(angle, shadowHole, cornerAngle);
    String numDegrees = (180 - degrees) + "°";
    
    DrawingContext context = new ChangeFontSize(g, 0.6f); 
    context = ChangeCoordsTranslate.chain(context, g, shadowHole);
    context = ChangeCoordsTranslate.chain(context, g, where);
    context = ChangeCoordsCenterText.chain(context, g, numDegrees);
    
    Draw draw = new DrawText(numDegrees);
    draw.drawIn(context, g);
  }

  /** 
   The angles defining the transition points between the side and the top/bottom of the view.
   These angles are from the point of view of the shadow-hole, not the center of the card. 
   Both angles sweep starting from '6:00' (straight down), in the counter-clockwise direction.
  */
  private final class CornerAngle {
    CornerAngle(Point2D.Double shadowHole){
      this.top = Math.PI - Math.atan2(config.viewerWidth()*0.5, shadowHole.y - config.viewerTopMargin());
      this.bottom = Math.atan2(config.viewerWidth()*0.5, config.viewerHeight() + config.viewerTopMargin() - shadowHole.y );
    }
    double top() { return top; }
    double bottom() {return bottom; }
    private double top;
    private double bottom;
  }
  
  /** THESE CALCULATIONS ARE A BIT TRICKY (error-prone). Is there a simpler technique, I wonder? */
  private Point2D.Double whereToWriteNumber(double angle, Point2D.Double shadowHole, CornerAngle cornerAngle){
    return new Point2D.Double(
      degreesXPosition(angle, cornerAngle, shadowHole), 
      degreesYPosition(angle, cornerAngle, shadowHole)
    );
  }
  
  /** With respect to the shadow hole (not the geometrical center of the card).*/
  private Double degreesXPosition(double angle, CornerAngle cornerAngle, Point2D.Double shadowHole) {
    //this math is a bit tedious; is there a simpler way?
    Double result = 0.0;
    Double base = 0.0;
    if (angle < cornerAngle.bottom()) {
      //bottom
      base = config.viewerHeight() + config.viewerTopMargin() - shadowHole.y;
      result = Math.tan(angle) * base * OffsetText.bottom;
    }
    else if (angle > (cornerAngle.top())) {
      //top
      base = shadowHole.y - config.viewerTopMargin();
      result = Math.tan(Math.PI - angle) * base * OffsetText.top;
    }
    else {
      //side
      base = config.viewerWidth() * 0.5;
      result = base * OffsetText.side;
    }
    return result;
  }
  
  /** With respect to the shadow-hole (not the geometrical center of the card).*/
  private Double degreesYPosition(double angle, CornerAngle cornerAngle, Point2D.Double shadowHole) {
    Double result = 0.0;
    Double base = 0.0;
    //repetition: the 'base' items are the same as in the XPosition
    if (angle < cornerAngle.bottom()) {
      //bottom
      base = config.viewerHeight() + config.viewerTopMargin() - shadowHole.y;
      result = base * OffsetText.bottom;
    }
    else if (angle > cornerAngle.top()) {
      //top
      base = shadowHole.y - config.viewerTopMargin();
      result = - base * OffsetText.top;
    }
    else {
      //side
      base = config.viewerWidth() * 0.5;
      result = Math.tan(Math.PI*0.5 - angle) * base * OffsetText.side;
    }
    return result;
  }
}
