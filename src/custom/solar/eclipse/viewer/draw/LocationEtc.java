package custom.solar.eclipse.viewer.draw;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import custom.solar.eclipse.viewer.astrocalc.EclipseDisplay;
import custom.solar.eclipse.viewer.astrocalc.EclipseType;
import custom.solar.eclipse.viewer.config.Config;
import custom.solar.eclipse.viewer.draw.mix.ChangeCoordsCenterText;
import custom.solar.eclipse.viewer.draw.mix.ChangeCoordsTranslate;
import custom.solar.eclipse.viewer.draw.mix.ChangeFontBold;
import custom.solar.eclipse.viewer.draw.mix.ChangeFontSize;
import custom.solar.eclipse.viewer.draw.mix.Draw;
import custom.solar.eclipse.viewer.draw.mix.DrawText;
import custom.solar.eclipse.viewer.draw.mix.DrawingContext;
import custom.solar.eclipse.viewer.math.Maths;

/** Location name and core data for the eclipse. */
final class LocationEtc implements Draw {
  
  LocationEtc(Config config, EclipseDisplay eclipse, Boolean onlyLocation, double yLevel) {
    this.config = config;
    this.eclipse = eclipse;
    this.onlyLocation = onlyLocation;
    this.yLevel = yLevel;
  }
  
  /** Location name (and so on) are placed below the viewer hole. */
  @Override public void draw(Graphics2D g) {
    renderBig(config.location(), yLevel(0), g);
    if (!onlyLocation) {
      if (eclipse.eclipseType() != EclipseType.Partial) {
        String type = eclipse.eclipseType() + "ity";
        render(type + " Starts " + formatted(eclipse.totalityAnnularityStarts()) + ", Lasts " + duration(eclipse.durationTotalityAnnularity()), yLevel(1), g);
        render("Altitude:" + altitude() + "°, Direction:" + direction(), yLevel(2), g);
      }
      else {
        render("Starts " + formatted(eclipse.partialStarts()) + ", Lasts " + duration(eclipse.durationPartial()), yLevel(1), g);
        render("Max Eclipse: " + formatted(eclipse.maxEclipse().when()), yLevel(2), g);
        render("Altitude:" + altitude() + "° Direction:" + direction(),  yLevel(3), g);
      }
    }
  }

  private Config config;
  private EclipseDisplay eclipse;
  private Boolean onlyLocation;
  private double yLevel;
  private static final double LINE_GAP = 0.03;
  
  private String formatted(LocalDateTime when) {
    return when.format(DateTimeFormatter.ofPattern("hh:mm:ss"));
  }
  
  private String duration(Duration duration) {
    //Duration duration = eclipse.durationTotalityAnnularity();
    Integer seconds = duration.toSecondsPart();
    String sec = (seconds < 10) ? "0"+seconds : seconds.toString(); 
    return duration.toMinutes() + "m " + sec + "s";
  }
  
  private String altitude() {
    Double degs = Maths.radsToDegs(eclipse.altitude());
    degs = Maths.roundToOnePlace(degs);
    return degs.toString();
  }
  
  private String direction() {
    List<String> DIRECTION_NAMES = Arrays.asList("North", "North-East", "East", "South-East", "South", "South-West", "West", "North-West");
    double chunkSize = 2*Math.PI / DIRECTION_NAMES.size();
    List<Double> DIRECTIONS = new ArrayList<>();
    for(int chunkIdx=0; chunkIdx < DIRECTION_NAMES.size();++chunkIdx) {
      DIRECTIONS.add(chunkIdx * chunkSize);
    }
    
    Double closestDir = null;
    Double closestDist = Math.PI; //any large value
    for(Double direction : DIRECTIONS) {
      double distFrom = Math.abs(direction - eclipse.azimuth());
      if (distFrom < closestDist) {
        closestDist = distFrom;
        closestDir = direction;
      }
    }
    return DIRECTION_NAMES.get(DIRECTIONS.indexOf(closestDir));
  }
  
  private void renderBig(String text, double yPercent, Graphics2D g) {
    DrawingContext context = new ChangeFontSize(g, 1.45f); //applied first, reversed last
    context = ChangeFontBold.chain(context, g);
    context = ChangeCoordsTranslate.chain(context, g, where(yPercent));
    context = ChangeCoordsCenterText.chain(context, g, text); //centering needs to be last in the chain

    Draw drawer = new DrawText(text);
    drawer.drawIn(context, g);
  }
  
  private void render(String text, double yPercent, Graphics2D g) {
    DrawingContext context = new ChangeCoordsTranslate(g, where(yPercent));
    context = ChangeCoordsCenterText.chain(context, g, text); //centering needs to be last in the chain

    Draw drawer = new DrawText(text);
    drawer.drawIn(context, g);
  }
  
  private Point2D.Double where(double yPercent){
    return new Point2D.Double(config.width() * 0.5, config.viewerHeight() * yPercent + config.viewerTopMargin());
  }
  
  private double yLevel(int line) {
    return yLevel + line * LINE_GAP;
  }
}
