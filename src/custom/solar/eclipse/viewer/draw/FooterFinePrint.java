package custom.solar.eclipse.viewer.draw;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import custom.solar.eclipse.viewer.config.Config;
import custom.solar.eclipse.viewer.draw.mix.ChangeCoordsCenterText;
import custom.solar.eclipse.viewer.draw.mix.ChangeCoordsTranslate;
import custom.solar.eclipse.viewer.draw.mix.ChangeFontSize;
import custom.solar.eclipse.viewer.draw.mix.Draw;
import custom.solar.eclipse.viewer.draw.mix.DrawText;
import custom.solar.eclipse.viewer.draw.mix.DrawingContext;
import custom.solar.eclipse.viewer.math.Maths;

/** Minor technical details. */
final class FooterFinePrint implements Draw {
  
  FooterFinePrint(Config config, Double magnitude, Double yLevel) {
    this.config = config;
    this.magnitude = magnitude;
    this.yLevel = yLevel;
  }
  
  /** Technical details placed at the bottom of the card. */
  @Override public void draw(Graphics2D g) {
    render("ΔT = " + config.ΔT() + "s", yLevel, g);
    //leaving this out leaves more room for the table, which seems more important:  
    //render(line1(), 0.87, g);
    //render(line2(), 0.90, g);
  }

  private Config config;
  private Double magnitude;
  private Double yLevel;
  
  private void render(String text, double yPercent, Graphics2D g) {
    DrawingContext context = new ChangeFontSize(g, 0.75f); //applied first, reversed last
    context = ChangeCoordsTranslate.chain(context, g, where(yPercent));
    context = ChangeCoordsCenterText.chain(context, g, text); //centering needs to be last in the chain

    Draw drawer = new DrawText(text);
    drawer.drawIn(context, g);
  }
  
  private String line1(){
    return 
      " UT" + config.hoursOffsetFromUT() + "h" + config.minutesOffsetFromUT() + "m" + 
      "  φ = " + Maths.roundToThreePlaces(Maths.radsToDegs(config.latitude())) + "°" +  
      "  λ = " + Maths.roundToThreePlaces(Maths.radsToDegs(config.longitude()))+ "°" 
    ; 
  }
  
  private String line2(){
    return 
      "Mag " + Maths.roundToThreePlaces(magnitude) + 
      "  ΔT = " + config.ΔT() + "s"
    ; 
  }
  
  private Point2D.Double where(double yPercent){
    return new Point2D.Double(config.width() * 0.50 - config.viewerWidth() * 0.37, config.viewerHeight() * yPercent + config.viewerTopMargin());
  }
}
