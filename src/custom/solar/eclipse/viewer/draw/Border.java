package custom.solar.eclipse.viewer.draw;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import custom.solar.eclipse.viewer.config.Config;
import custom.solar.eclipse.viewer.draw.mix.ChangeCoordsTranslate;
import custom.solar.eclipse.viewer.draw.mix.ChangeFontStrokeWidth;
import custom.solar.eclipse.viewer.draw.mix.Draw;
import custom.solar.eclipse.viewer.draw.mix.DrawRectangle;
import custom.solar.eclipse.viewer.draw.mix.DrawingContext;

/** 
 Border intended as guideline for cutting out the viewer from the full page.
*/
final class Border implements Draw {
  
  /**
   Constructor. 
   @param strokeWidth width of the borderline.
  */
  Border(Config config, float strokeWidth) {
    this.config = config;
    this.strokeWidth = strokeWidth;
  }
  
  /** Rectangular border, centered horizontally on the page. */
  @Override public void draw(Graphics2D g) {
    Point2D.Double where = new Point2D.Double(config.width()*0.5, config.viewerHeight()*0.5 + config.viewerTopMargin());
    DrawingContext context = new ChangeCoordsTranslate(g, where);
    context = ChangeFontStrokeWidth.chain(context, g, strokeWidth);
    Draw draw = new DrawRectangle(config.viewerWidth(), config.viewerHeight());
    draw.drawIn(context, g);
  }

  private Config config;
  private float strokeWidth;
} 