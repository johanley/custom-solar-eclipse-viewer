package custom.solar.eclipse.viewer.draw;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import custom.solar.eclipse.viewer.config.Config;
import custom.solar.eclipse.viewer.draw.mix.ChangeCoordsRightAlignText;
import custom.solar.eclipse.viewer.draw.mix.ChangeCoordsTranslate;
import custom.solar.eclipse.viewer.draw.mix.ChangeFontSize;
import custom.solar.eclipse.viewer.draw.mix.Draw;
import custom.solar.eclipse.viewer.draw.mix.DrawText;
import custom.solar.eclipse.viewer.draw.mix.DrawingContext;

/** 
 Fine print about who produced this custom viewer. 
*/
final class ProducedBy implements Draw {
  
  ProducedBy(Config config) {
    this.config = config;
  }
  
  @Override public void draw(Graphics2D g) {
    render(config.producedBy(), g);
  }

  private Config config;
  
  private void render(String text, Graphics2D g) {
    DrawingContext context = new ChangeFontSize(g, 0.80f); //applied first, reversed last
    context = ChangeCoordsTranslate.chain(context, g, where());
    context = ChangeCoordsRightAlignText.chain(context, g, text); 

    Draw drawer = new DrawText(text);
    drawer.drawIn(context, g);
  }
  
  private Point2D.Double where(){
    return new Point2D.Double(
      config.width() * 0.5 + config.viewerWidth() * 0.45, 
      config.viewerHeight() * 0.96 + config.viewerTopMargin()
    );
  }
}
