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

/** General remarks about what to look for during totality. */
final class TotalityAdvice implements Draw {
  
  TotalityAdvice(Config config, double yLevel) {
    this.config = config;
    this.where = where(0.25, yLevel);
  }
  
  @Override public void draw(Graphics2D g) {
    drawHeader("During Totality", g);
    int lineNum = 0;
    for (String line : config.totalityAdvice()) {
      drawLine(line, lineNum, g);
      ++lineNum;
    }
  }

  private Config config;
  private Point2D.Double where;
  
  private Point2D.Double where(double xFrac, double yFrac){
    return new Point2D.Double(
      (config.width() - config.viewerWidth())* 0.5 + config.viewerWidth() * xFrac, 
      config.viewerHeight() * yFrac + config.viewerTopMargin()
    );
  }
  
  private Point2D.Double whereHeader(){
    return new Point2D.Double(
      config.width() * 0.5, 
      where.y - 0.04 * config.viewerHeight()
    );
  }
  
  private void drawHeader(String text, Graphics2D g) {
    DrawingContext context = new ChangeCoordsTranslate(g, whereHeader());
    context = ChangeCoordsCenterText.chain(context, g, text);
    Draw drawer = new DrawText(text);
    drawer.drawIn(context, g);
  }

  private void drawLine(String line, int lineNum, Graphics2D g) {
    DrawingContext context = new ChangeCoordsTranslate(g, where);
    context = ChangeFontSize.chain(context, g, 0.8f);
    if (lineNum > 0) {
      context = ChangeCoordsTranslate.chain(context, g, new Point2D.Double(0, lineNum * config.viewerHeight() * 0.025));
    }
    Draw drawer = new DrawText(line);
    drawer.drawIn(context, g);
  }

}
