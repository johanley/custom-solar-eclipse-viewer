package custom.solar.eclipse.viewer.draw;

import static custom.solar.eclipse.viewer.math.Maths.sqr;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import custom.solar.eclipse.viewer.astrocalc.EclipseDisplay;
import custom.solar.eclipse.viewer.astrocalc.EclipseType;
import custom.solar.eclipse.viewer.astrocalc.PartialPhase;
import custom.solar.eclipse.viewer.config.Config;
import custom.solar.eclipse.viewer.draw.mix.ChangeCoordsCenterText;
import custom.solar.eclipse.viewer.draw.mix.ChangeCoordsRightAlignText;
import custom.solar.eclipse.viewer.draw.mix.ChangeCoordsTranslate;
import custom.solar.eclipse.viewer.draw.mix.ChangeFontColor;
import custom.solar.eclipse.viewer.draw.mix.ChangeFontSize;
import custom.solar.eclipse.viewer.draw.mix.Draw;
import custom.solar.eclipse.viewer.draw.mix.DrawCircle;
import custom.solar.eclipse.viewer.draw.mix.DrawText;
import custom.solar.eclipse.viewer.draw.mix.DrawingContext;
import custom.solar.eclipse.viewer.math.Maths;

/** The Sun is a circle, and the various partial phases are circular arcs (that is, circles clipped by the Sun's circle). */
public final class PartialPhasesChart implements Draw {
  
  PartialPhasesChart(Config config, EclipseDisplay eclipse){
    this.config = config;
    this.eclipse = eclipse;
  }
  
  @Override public void draw(Graphics2D g) {
    phasesChart(ON_LEFT, eclipse.phasesBefore(), g);
    phasesChart(ON_RIGHT, eclipse.phasesAfter(), g);
    if (EclipseType.Total != eclipse.eclipseType()) {
      maximumEclipseChart(g);
    }
  }
  
  /*
   * This implementation has been a bit finicky to implement.
   * I wonder if there's a better way, perhaps using more AffineTransforms.
   */

  private Config config;
  private EclipseDisplay eclipse;
  private static final double SOLAR_RADIUS = 0.18;
  private static final double TEXT_JUST_OUTSIDE_SOLAR_DISK = 1.02;
  private static final double MAX_DESIRED_DISTANCE = 1.95;
  private static final int ON_LEFT = 1;
  private static final int ON_RIGHT = -1;
  
  private void phasesChart(int parity, List<PartialPhase> phases, Graphics2D g) {
    Point2D.Double sun = solarCenter(parity);
    String title = phasesTitle(parity);
    drawTitle(title, sun, -1, g);
    drawSun(sun, g);

    for (PartialPhase phase : phases) {
      if (phase.lunarSolarDistance() < MAX_DESIRED_DISTANCE) {
        moon(phase, sun, g, false);
        moonText(phase, sun, g);
      }
    }
  }
  
  private void maximumEclipseChart(Graphics2D g) {
    Point2D.Double sun = solarCenterForMaximum();
    String time = eclipse.maxEclipse().when().format(DateTimeFormatter.ofPattern("hh:mm:ss"));
    drawTitle("Maximum " + Maths.roundToThreePlaces(eclipse.magnitude()) + " at " + time, sun, +1, g);
    drawSun(sun, g);
    moon(eclipse.maxEclipse(), sun, g, true);
  }
  
  private String phasesTitle(int parity) {
    String middle = parity == ON_LEFT ? "Before" : "After";
    String type = EclipseType.Total == eclipse.eclipseType() ? " Totality" : " Maximum";
    return middle + type;
  }

  private void drawTitle(String title, Point2D.Double sun, int parity, Graphics2D g) {
    DrawingContext context = new ChangeCoordsTranslate(g, sun);
    context = ChangeCoordsTranslate.chain(context, g, nearThe(sun, parity));
    context = ChangeCoordsCenterText.chain(context, g, title);
    
    DrawText drawTitle = new DrawText(title);
    drawTitle.drawIn(context, g);
  }
  
  private Point2D.Double nearThe(Point2D.Double sun, int sign){
    return new Point2D.Double(0, sign * solarRadius() * 1.2);
  }

  /** Simple circle. */
  private void drawSun(Point2D.Double sun, Graphics2D g) {
    DrawingContext context = new ChangeCoordsTranslate(g, sun);
    DrawCircle drawSun = new DrawCircle(solarRadius(), false);
    drawSun.drawIn(context, g);
  }
  
  private double solarRadius() {
    return config.viewerWidth() * SOLAR_RADIUS;
  }
  
  private Point2D.Double solarCenter(int parity){
    return new Point2D.Double(config.width() * 0.5 - parity * config.viewerWidth() * 0.25, config.viewerHeight() * 0.50 + config.viewerTopMargin()) ;
  }
  
  private Point2D.Double solarCenterForMaximum(){
    return new Point2D.Double(config.width() * 0.5, config.viewerHeight() * 0.75 + config.viewerTopMargin()) ;
  }
  
  private void moon(PartialPhase phase, Point2D.Double sun, Graphics2D g, boolean fill) {
    DrawingContext context = new ChangeCoordsTranslate(g, sun);
    context = ChangeCoordsTranslate.chain(context, g, moonsCenter(phase));
    if (fill) {
      context = ChangeFontColor.chain(context, g, Color.LIGHT_GRAY);
    }
    
    g.setClip(solarShape(sun));
    DrawCircle moon = new DrawCircle(phase.lunarRadius() * solarRadius(), fill);
    moon.drawIn(context, g);
    g.setClip(null);
  }

  private Shape solarShape(Point2D.Double sun) {
    double radius = solarRadius();
    //this constructer uses the upper-left corner, not the center
    Shape result = new Ellipse2D.Double(sun.x - radius, sun.y - radius, radius*2, radius*2);
    return result;
  }
  
  private Point2D.Double moonsCenter(PartialPhase phase){
    double x = - Math.sin(phase.zenithAngle()) * phase.lunarSolarDistance() * solarRadius();
    double y = - Math.cos(phase.zenithAngle()) * phase.lunarSolarDistance() * solarRadius();
    return new Point2D.Double(x, y);
  }
  
  private static final class Intersection {
    static Intersection from(Point2D.Double point, boolean isBase) {
      Intersection result = new Intersection();
      result.point = point;
      result.isBase = isBase;
      return result;
    }
    Intersection flipped(){
      return Intersection.from(new Point2D.Double(-point.x, point.y), false);
    }
    Intersection rotate(double angle){
      double x = Math.cos(angle) * point.x + Math.sin(angle) * point.y;
      double y = - Math.sin(angle) * point.x + Math.cos(angle) * point.y;
      return Intersection.from(new Point2D.Double(x, y), isBase);
    }
    Point2D.Double point;
    boolean isBase;
  }
  
  /**  Add text near the intersection points between the two circles (Sun and Moon). */
  private void moonText(PartialPhase phase, Point2D.Double sun, Graphics2D g) {
    double angleMoonSunPoint = fromCosineLawForTriangles(phase); //0..pi
    double scale = solarRadius();
    
    Intersection base = Intersection.from(new Point2D.Double(Math.sin(angleMoonSunPoint) * scale, - Math.cos(angleMoonSunPoint) * scale), true);
    Intersection flipped = base.flipped();
    
    double rotation = phase.zenithAngle();
    base = base.rotate(rotation);
    flipped = flipped.rotate(rotation);
    
    annotateThe(base, phase, sun, g);
    annotateThe(flipped, phase, sun, g);
  }
  
  /** Place text nearby the intersection point to describe the partial phase. */
  private void annotateThe(Intersection intersection, PartialPhase phase, Point2D.Double sun, Graphics2D g)  {
    Quadrant quadrant = Quadrant.forPoint(intersection.point);
    String theText = displayText(intersection, phase);
    
    Point2D.Double outsideRim = rescale(intersection.point, TEXT_JUST_OUTSIDE_SOLAR_DISK);
    
    DrawingContext context = new ChangeCoordsTranslate(g, sun);
    context = ChangeCoordsTranslate.chain(context, g, outsideRim); 
    context = ChangeCoordsTranslate.chain(context, g, new Point2D.Double(quadrant.dx, quadrant.dy));
    context = ChangeFontSize.chain(context, g, 0.5f);
    if (Quadrant.SW == quadrant || Quadrant.NW == quadrant) {
      context = ChangeCoordsRightAlignText.chain(context, g, theText);
    }
    Draw drawer = new DrawText(theText);
    drawer.drawIn(context, g);
  }
  
  /** Angle 0..pi. The angle is formed by MoonCenter > SunCenter > intersection point on the Sun's circumference. */
  private double fromCosineLawForTriangles(PartialPhase phase) {
    //cosine law cos(a) = (B^2 + C^2 - A^2) / 2BC 
    double numer = 1 + sqr(phase.lunarSolarDistance()) - sqr(phase.lunarRadius());
    double denom = 2 * 1 * phase.lunarSolarDistance();
    return Math.acos(numer/denom); //0..pi
  }

  private Point2D.Double rescale(Point2D.Double point, double scale){
    return new Point2D.Double(point.x * scale, point.y * scale);
  }

  private static enum Quadrant {
    NE(0,0), SE(0,4), SW(0,4), NW(0,0);
    static Quadrant forPoint(Point2D.Double point) {
      Quadrant result = null;
      boolean isNorth = point.y < 0;
      boolean isEast = point.x > 0;
      if (isNorth && isEast) {
        result = NE;
      }
      else if (isNorth && !isEast) {
        result = NW;
      }
      else if (!isNorth && isEast) {
        result = SE;
      }
      else if (!isNorth && !isEast) {
        result = SW;
      }
      return result;
    }
    private double dx;
    private double dy;
    
    private Quadrant(double dx, double dy) {
      this.dx = dx;
      this.dy = dy;
    }
  }
  
  private String hourMin(PartialPhase phase) {
    return phase.when().format(DateTimeFormatter.ofPattern("h:mm"));
  }
  
  /** Hours and minutes from either the start or end of totality/annularity. */
  private String timeFromTotalityAnnularity(PartialPhase phase) {
    //assume the phase is NOT between the start and end of totality
    Boolean beforeTotality = phase.when().isBefore(eclipse.totalityAnnularityStarts());
    LocalDateTime totality = beforeTotality ? eclipse.totalityAnnularityStarts() : eclipse.totalityAnnularityEnds();
    Duration duration = Duration.between(totality, phase.when());
    return Maths.hhmm(duration);
  }
  
  /** Hours and minutes from max eclipse. */
  private String timeFromMaxEclipse(PartialPhase phase) {
    Duration duration = Duration.between(eclipse.maxEclipse().when(), phase.when());
    return Maths.hhmm(duration);
  }
  
  private boolean isBefore(PartialPhase phase) {
    return phase.when().isBefore(eclipse.maxEclipse().when());
  }

  private String displayText(Intersection intersection, PartialPhase phase) {
    String timeFrom = eclipse.eclipseType() == EclipseType.Partial ? timeFromMaxEclipse(phase) : timeFromTotalityAnnularity(phase);
    boolean isBefore = isBefore(phase);
    String result = timeFrom; 
    boolean switchIt = (!intersection.isBase && isBefore) || (intersection.isBase && !isBefore);  
    if (switchIt) {
      result = hourMin(phase);
    }
    return result;
  }

}