package custom.solar.eclipse.viewer.draw;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import custom.solar.eclipse.viewer.config.Config;
import custom.solar.eclipse.viewer.draw.mix.ChangeCoordsRightAlignText;
import custom.solar.eclipse.viewer.draw.mix.ChangeCoordsTranslate;
import custom.solar.eclipse.viewer.draw.mix.ChangeFontSize;
import custom.solar.eclipse.viewer.draw.mix.Draw;
import custom.solar.eclipse.viewer.draw.mix.DrawCircle;
import custom.solar.eclipse.viewer.draw.mix.DrawRectangle;
import custom.solar.eclipse.viewer.draw.mix.DrawText;
import custom.solar.eclipse.viewer.draw.mix.DrawingContext;

/** 
 Outlines for the holes needed for the viewer.

 <P>There are 2 small holes: a pin-hole, and a lanyard-hole.
 These should be made with a circular punch, after you have printed the viewer. 
 
 <P>There are 1 or 2 holes for the viewer's filter material.
 These holes are larger, and are cut out with a sharp-edged tool. 
*/
final class Holes implements Draw {
 
  Holes(Config config, Boolean isBackOfViewer, double holeRadius) {
    this.config = config;
    this.isBackOfViewer = isBackOfViewer;
    this.holeRadius = holeRadius;
  }
  
  /** Draw the outlines for holes. */
  @Override public void draw(Graphics2D g) {
    circle(AT_SHADOW_HOLE_LEVEL, holeRadius, g);
    circle(AT_LANYARD_HOLE_LEVEL, holeRadius, g);
    if (!isBackOfViewer) {
      textAffordance("Pinhole", AT_SHADOW_HOLE_LEVEL + 0.005, g);
      textAffordance("Lanyard Hole", AT_LANYARD_HOLE_LEVEL + 0.005, g);
    }
    
    if (hasOneEyehole()) {
      oneEyehole(AT_EYE_HOLE_LEVEL, g);
    }
    else {
      twoEyeholes(AT_EYE_HOLE_LEVEL, Parity.Left, g);
      twoEyeholes(AT_EYE_HOLE_LEVEL, Parity.Right, g);
    }
  }
  
  public static final Double AT_SHADOW_HOLE_LEVEL = 0.42;
  
  private static final Double AT_LANYARD_HOLE_LEVEL = 0.96;
  
  private static final Float AT_EYE_HOLE_LEVEL = 0.17F;
  private static enum Parity {
    Left(+1),
    Right(-1);
    int sign() { return sign; }
    private Parity(int sign) {
      this.sign = sign;
    }
    private int sign;
  }
  private Config config;
  private Boolean isBackOfViewer;
  private double holeRadius;
  
  private boolean hasOneEyehole() {
    return config.eyeholeCenter() == 0.0f;
  }
  
  private void circle(double yLevelFrac, double radius, Graphics2D g) {
    DrawingContext context = new ChangeCoordsTranslate(g, whereOneEyehole(yLevelFrac));
    Draw drawer = new DrawCircle(radius, false);
    drawer.drawIn(context, g);
  }
  
  private void textAffordance(String text, double yLevelFrac, Graphics2D g) {
    DrawingContext context = new ChangeFontSize(g, 0.7f);
    context = ChangeCoordsTranslate.chain(context, g, whereTextAffordance(yLevelFrac));
    context = ChangeCoordsRightAlignText.chain(context, g, text);
    Draw drawer = new DrawText(text);
    drawer.drawIn(context, g);
  }
  
  private void oneEyehole(double yLevelFrac, Graphics2D g) {
    Point2D.Double where = whereOneEyehole(yLevelFrac);
    DrawingContext context = new ChangeCoordsTranslate(g, where);
    
    double width = config.viewerWidth() * config.eyeholeWidth(); 
    double height = config.viewerHeight() * config.eyeholeHeight(); 
    Draw drawer = new DrawRectangle(width, height);
    drawer.drawIn(context, g);
  }
  
  private Point2D.Double whereOneEyehole(double yLevelFrac){
    double ctr_x = config.width() * 0.5;
    double ctr_y = config.viewerHeight() * yLevelFrac + config.viewerTopMargin();
    return new Point2D.Double(ctr_x, ctr_y);
  }

  private void twoEyeholes(double yLevelFrac, Parity parity, Graphics2D g) {
    Point2D.Double left = whereTwoEyeholes(parity, yLevelFrac);
    DrawingContext context = new ChangeCoordsTranslate(g, left);
    
    Draw drawer = new DrawRectangle(config.viewerWidth() * config.eyeholeWidth(), config.viewerHeight() * config.eyeholeHeight());
    drawer.drawIn(context, g);
  }

  private Point2D.Double whereTwoEyeholes(Parity parity, double yLevelFrac){
    double x = config.width() * 0.5 - parity.sign() * config.eyeholeCenter() * config.viewerWidth();
    double y = config.viewerHeight() * yLevelFrac;
    return new Point2D.Double(x, y);
  }
  
  private Point2D.Double whereTextAffordance(double yLevelFrac){
    return new Point2D.Double(config.width() * 0.48, config.viewerHeight() * yLevelFrac + config.viewerTopMargin());
  }
}
