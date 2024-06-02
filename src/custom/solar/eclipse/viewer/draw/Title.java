package custom.solar.eclipse.viewer.draw;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import custom.solar.eclipse.viewer.astrocalc.EclipseType;
import custom.solar.eclipse.viewer.config.Config;
import custom.solar.eclipse.viewer.draw.mix.ChangeCoordsCenterText;
import custom.solar.eclipse.viewer.draw.mix.ChangeCoordsTranslate;
import custom.solar.eclipse.viewer.draw.mix.ChangeFontBold;
import custom.solar.eclipse.viewer.draw.mix.ChangeFontSize;
import custom.solar.eclipse.viewer.draw.mix.Draw;
import custom.solar.eclipse.viewer.draw.mix.DrawText;
import custom.solar.eclipse.viewer.draw.mix.DrawingContext;

/** Title on the front of the viewer. */
final class Title implements Draw {
  
  Title(Config config, EclipseType eclipseType, LocalDateTime eclipseCivilDateTime, double yLevel) {
    this.config = config;
    this.eclipseType = eclipseType;
    this.eclipseCivilDateTime = eclipseCivilDateTime;
    this.yLevel = yLevel;
  }
  
  /** The date of the eclipse, along with the weekday. */
  @Override public void draw(Graphics2D g) {
    DateTimeFormatter writeFormat = DateTimeFormatter.ofPattern("EEEE MMMM d, yyyy");
    String text = eclipseType.toString() + " Solar Eclipse - " + eclipseCivilDateTime.toLocalDate().format(writeFormat);
    
    DrawingContext context = new ChangeFontSize(g, 1.45f); //applied first, reversed last
    context = ChangeFontBold.chain(context, g);
    context = ChangeCoordsTranslate.chain(context, g, where(yLevel));
    context = ChangeCoordsCenterText.chain(context, g, text); //centering needs to be last in the chain
    
    Draw drawer = new DrawText(text);
    drawer.drawIn(context, g);
  }

  private Config config;
  private EclipseType eclipseType;
  private LocalDateTime eclipseCivilDateTime;
  private double yLevel;
  
  private Point2D.Double where(double yPercent){
    return new Point2D.Double(config.width() * 0.5, config.viewerHeight() * yPercent + config.viewerTopMargin());
  }

}
