package custom.solar.eclipse.viewer.astrocalc;

import static custom.solar.eclipse.viewer.config.Constants.NL;
import static custom.solar.eclipse.viewer.util.LogUtil.log;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import custom.solar.eclipse.viewer.config.Config;
import custom.solar.eclipse.viewer.draw.TimelineEvent;
import custom.solar.eclipse.viewer.math.Maths;

/** 
 Compute the local circumstances of a given solar eclipse.
 After building this object, the caller must always first call the {@link #compute()} method.
*/
public final class LocalCircumstances {
  
  /** Manual test harness. */
  public static void main(String... args) {
    BesselianElementsLookup lookup = new BesselianElementsLookup();

    /*
    Location location = usNavalObservatory();
    double ΔT = 61.0;
    BesselianElements bessel = lookup.lookupMeeusExample();
    //BesselianElements bessel = lookup.lookup(LocalDate.of(1994, 5, 10));
    */
    
    Location location = skinnersPond();
    //Location location = stratford();
    double ΔT = 69.0;
    BesselianElements bessel = lookup.lookup(LocalDate.of(2024, 4, 8));

    log(location);
    log(bessel);
    
    LocalCircumstances localCircumstances = new LocalCircumstances(location, bessel, ΔT, 10);
    localCircumstances.compute(ShowLogging.Yes);
  }
  
  public enum ShowLogging {Yes, No};
  
  /**
   Constructor.
   @param location where on Earth to calculate the local circumstances of the eclipse.
   @param bessel the standard data needed to calculate local circumstances. 
   @param ΔT is in seconds, the difference TT (physics time) - UTC (the basis for civil time). 
   @param gapBetweenPartialPhases is in minutes. 
  */
  LocalCircumstances(Location location, BesselianElements bessel, Double ΔT, Integer gapBetweenPartialPhases) {
    this.location = location;
    this.bessel = bessel;
    this.ΔT = ΔT;
    this.gapBetweenPartialPhases = gapBetweenPartialPhases;
  }
  
  /** Compute the local circumstances of a solar eclipse. */
  void compute(ShowLogging showLogging) {
    this.showLogging = showLogging;
    maximumEclipse = computeLocalMax();
    if (maximumEclipse.magnitude() < 0) {
      log("There is no eclipse on that date for the given location.");
    }
    else {
      logger("Local Maximum Eclipse " + maximumEclipse);
      logger("TT of local max eclipse: " + maximumEclipse.TT() + NL);
      logger("UTC of local max eclipse: " + maximumEclipse.UTC() + NL);
      logger("Civil time of local max eclipse: " + maximumEclipse.localCivilTime() + NL);
      
      startPartialEclipse = computeContact(START, PENUMBRA, maximumEclipse);
      logger("Start Partial Eclipse " + startPartialEclipse);
      logger("UTC of start of partial eclipse: " + startPartialEclipse.UTC() + NL);
      
      endPartialEclipse = computeContact(END, PENUMBRA, maximumEclipse);
      logger("End Partial Eclipse " + endPartialEclipse);
      logger("UTC of end of partial eclipse: " + endPartialEclipse.UTC() + NL);
      
      confirmTheOrderOf(startPartialEclipse, endPartialEclipse);
      
      if (maximumEclipse.localEclipseType() != EclipseType.Partial) {
        startTotalOrAnnularEclipse = computeContact(START, UMBRA, maximumEclipse);
        logger("Start Total/Annular Eclipse " + startTotalOrAnnularEclipse);
        logger("UTC of start of total/annular eclipse: " + startTotalOrAnnularEclipse.UTC() + NL);
        
        endTotalOrAnnularEclipse = computeContact(END, UMBRA, maximumEclipse);
        logger("End Total/Annular Eclipse " + endTotalOrAnnularEclipse);
        logger("UTC of end of total/annular eclipse: " + endTotalOrAnnularEclipse.UTC() + NL);
        
        confirmTheOrderOf(startPartialEclipse, startTotalOrAnnularEclipse, endTotalOrAnnularEclipse, endPartialEclipse);
      }
      
      partialPhasesStart = computePartialPhases(evenlySpacedTimesWithRespectToPeak(START));
      partialPhasesEnd = computePartialPhases(evenlySpacedTimesWithRespectToPeak(END));
      
      timelineEvents = computeTimelineEvents();
    }
  }
  
  Location location() {  return location;  }
  
  BesselianElements besselianElements() { return bessel;  }
  
  Double ΔT() {  return ΔT;  }
  
  /** Warning: returns null if no eclipse occurs for the given configuration. */
  public static EclipseDisplay buildFrom(Config config, ShowLogging showLogging) {
    EclipseDisplay result = null;
    Location location = new Location(config.location(), config.latitude(), config.longitude(), config.altitude(), config.hoursOffsetFromUT(), config.minutesOffsetFromUT());
    Double ΔT = config.ΔT();
    BesselianElementsLookup lookup = new BesselianElementsLookup();
    BesselianElements bessel = lookup.lookup(LocalDate.parse(config.eclipseDateUTC()));
    LocalCircumstances circum = new LocalCircumstances(location, bessel, ΔT, config.gapBetweenPartialPhases());
    circum.compute(showLogging);
    if(circum.maximumEclipse.localEclipseType() == EclipseType.None) {
      //do nothing, return null object
    }
    else {
      LocalDateTime startTotalAnnular = circum.maximumEclipse.localEclipseType() == EclipseType.Partial ? null : circum.startTotalOrAnnularEclipse.localCivilTime(); 
      LocalDateTime endTotalAnnular = circum.maximumEclipse.localEclipseType() == EclipseType.Partial ? null : circum.endTotalOrAnnularEclipse.localCivilTime(); 
      result = new EclipseDisplay(
        circum.maximumEclipse.localEclipseType(), 
        circum.startPartialEclipse.localCivilTime(), 
        circum.endPartialEclipse.localCivilTime(), 
        circum.buildPartialPhaseFrom(circum.maximumEclipse), 
        circum.partialPhasesStart, 
        circum.partialPhasesEnd, 
        circum.maximumEclipse.h, 
        circum.maximumEclipse.az, 
        circum.maximumEclipse.magnitude(), 
        startTotalAnnular,
        endTotalAnnular,
        circum.timelineEvents
      );
    }
    return result;
  }
  
  private Location location;
  private BesselianElements bessel;
  private Double ΔT; //seconds
  private Integer gapBetweenPartialPhases; //minutes 
  
  /** The local maximum eclipse is reused as the starting point in computing the 4 contacts. */
  private Worksheet maximumEclipse;
  private Worksheet startPartialEclipse;
  private Worksheet endPartialEclipse;
  private List<PartialPhase> partialPhasesStart;
  private List<PartialPhase> partialPhasesEnd;
  private Worksheet startTotalOrAnnularEclipse;
  private Worksheet endTotalOrAnnularEclipse;
  private List<TimelineEvent> timelineEvents;

  private ShowLogging showLogging = ShowLogging.Yes;
  
  private static final double FRACTION_OF_A_SECOND = 0.00001; //unit of hours = 0.036s
  
  private Worksheet computeLocalMax() {
    double t = 0.0; //hours difference from T0
    Worksheet w = new Worksheet(t, ΔT, bessel, location);
    w.compute();
    while(Math.abs(w.correctionToTimeOfMaxEclipse()) > FRACTION_OF_A_SECOND) {
      t = w.t + w.correctionToTimeOfMaxEclipse();
      w = new Worksheet(t, ΔT, bessel, location);
      w.compute();
    }
    return w;
  }
  
  private static final boolean START = true;
  private static final boolean END = false;
  
  private static final boolean PENUMBRA = true;
  private static final boolean UMBRA = false;
  
  private Worksheet computeContact(boolean isBefore, boolean isPenumbra, Worksheet localMaxEclipse) {
    double t = localMaxEclipse.t;
    double initialCorr = localMaxEclipse.initialCorrectionToTimeOfContact(isBefore, isPenumbra);
    
    Worksheet w = new Worksheet(t + initialCorr, ΔT, bessel, location);
    w.compute();
    while (Math.abs(w.correctionToTimeOfContact(isBefore, isPenumbra)) > FRACTION_OF_A_SECOND) {
      t = w.t + w.correctionToTimeOfContact(isBefore, isPenumbra);
      w = new Worksheet(t, ΔT, bessel, location);
      w.compute();
    }
    return w;
  }
  
  private static Location usNavalObservatory() {
    return new Location("USNO", Maths.degToRads(38.921389), Maths.degToRads(-77.06556), 84.0, 0, 0);
  }
  
  private static Location skinnersPond() {
    return new Location("Skinner's Pond",  Maths.degToRads(46.96757), Maths.degToRads(-64.12027), 0.0, -3, 0);
  }
  
  private static Location stratford() {
    return new Location("Straford",  Maths.degToRads(46.22889), Maths.degToRads(-63.10383), 0.0, -3, 0);
  }
  
  /**
   This exists in part because there seems to be an error in the Meeus documentation, page 26-27, Elements of Solar Eclipses 1951-2200.
   L1prime is always positive, but L2prime is either sign (negative for total eclipses, positive for annular eclipses).
   
   Hence, this can affect the sign of his initial correction value.
   In the example provided by Meeus, only L1prime is used, so he might have missed this. 
  */
  private void  confirmTheOrderOf(Worksheet... ws) {
    for(int idx = 0; idx < ws.length - 1; ++idx) {
      if ((ws[idx].UTC()).isAfter( ws[idx+1].UTC())){
        log("Unexpected time order: " + ws[idx].UTC() + " is after " + ws[idx+1].UTC() );
      }
    }
  }
  
  /** The partial phases for a selected N integral number of minutes before/after either totality/annularity or local maximum (if partial). */
  private List<PartialPhase> computePartialPhases(List<Double> times){
    List<PartialPhase> result = new ArrayList<>();
    for (double t : times) {
      Worksheet worksheet = new Worksheet(t, ΔT, bessel, location);
      worksheet.compute();
      PartialPhase partial = buildPartialPhaseFrom(worksheet);
      result.add(partial);
    }
    return result;
  }
  
  /**
   The times for showing partial phases, expressed as number of hours from T0 of the Besselian Elements.
   The times are always between the start and end of the partial eclipse.
   The times are evenly spaced around a base-time.
   
   For a partial or annular eclipse, the base-time is the time of the local maximum eclipse.
   For a total eclipse, the base-times are the start/end of totality, not the local maximum eclipse.

   @param isBefore refers to before or after the middle of the eclipse.
   @return doubles representing the decimal number of hours with respect to the {@link #baseTimeWithRespectToPeak(boolean, EclipseType)}. 
   The temporal order is not specified, and can be in either direction.
  */
  private List<Double> evenlySpacedTimesWithRespectToPeak(boolean isBefore){
    List<Double> result = new ArrayList<>();
    double baseTime = baseTimeWithRespectToPeak(isBefore, maximumEclipse.localEclipseType());
    int sign = isBefore ? -1 : +1;
    int count = 1;
    double t = 0.0;
    while(true) {
      t = baseTime + sign * count * gapBetweenPartialPhases/60.0;
      if (startPartialEclipse.t < t && t < endPartialEclipse.t) {
        result.add(t);
        ++count;
      }
      else {
        break;
      }
    } 
    return result;
  }

  /** @return double representing the decimal number of hours with respect to the {@link #baseTimeWithRespectToPeak(boolean, EclipseType)}. */ 
  private Double specificTimeWithRespectToPeak( boolean isBefore, double minutes){
    double baseTime = baseTimeWithRespectToPeak(isBefore, maximumEclipse.localEclipseType());
    int sign = isBefore ? -1 : +1;
    return baseTime + sign * minutes/60.0;
  }

  private double baseTimeWithRespectToPeak(boolean isBefore, EclipseType eclipseType) {
    double result = 0.0;
    if (EclipseType.Total == eclipseType) {
      result = isBefore ? startTotalOrAnnularEclipse.t : endTotalOrAnnularEclipse.t;
    }
    else {
      result = maximumEclipse.t;
    }
    return result;
  }
  
  private static final double SOLAR_RADIUS = 1.0;
  
  private PartialPhase buildPartialPhaseFrom(Worksheet w) {
    //To understand this, make a diagram with the Sun's RADIUS = 1.0, Moon's radius = A.
    //G is the fraction of the Sun's DIAMETER that is covered (not radius).
    
    //w.A is the ratio of the Moon's apparent diameter/radius to the Sun's apparent diameter/radius.
    //When the Sun's radius is 1.0, the Moon's radius is just w.A.
    double lunarRadius = w.A; 
    double zenithAngle = w.Z;
    //w.G is the magnitude: the fraction of the Sun's DIAMETER covered by the Moon.
    double lunarSolarDistance = SOLAR_RADIUS - 2*w.G + w.A;  
    LocalDateTime when = w.localCivilTime();
    PartialPhase result = new PartialPhase(when, zenithAngle, lunarSolarDistance, lunarRadius, w.magnitude(), w.h);
    return result;
  }
  
  private void logger(Object thing) {
    if(ShowLogging.Yes == showLogging) {
      log(thing);
    }
  }
  
  private List<TimelineEvent> computeTimelineEvents(){
    List<TimelineEvent> result = new ArrayList<>(); 
    
    if (maximumEclipse.localEclipseType() == EclipseType.Total) {
      Worksheet base = startTotalOrAnnularEclipse;
      result.add(eventFor("Start of the partial phase.", startPartialEclipse, base));
      result.add(standardOffsetEventFor("Shadows of pinholes start to appear strange.", 20.0, START, base));
      result.add(standardOffsetEventFor("Shadow-bands may appear on the ground, buildings.", 2.0, START, base));
      result.add(standardOffsetEventFor("Baily's beads/diamond ring start.", 10.0/60.0, START, base));
      result.add(eventFor("TOTALITY STARTS. View with naked eye, unfiltered.", startTotalOrAnnularEclipse, base));
      
      base = maximumEclipse;
      result.add(eventFor("Maximum eclipse.", maximumEclipse, base));
      
      base = endTotalOrAnnularEclipse;
      result.add(eventFor("TOTALITY ENDS. Resume viewing with filter.", endTotalOrAnnularEclipse, base));
      result.add(standardOffsetEventFor("Baily's beads/diamond ring just after totality.", 3.0/60.0, END, base));
      result.add(standardOffsetEventFor("Shadow-bands no longer appear on the ground, buildings.", 2.0, END, base));
      result.add(standardOffsetEventFor("Shadows of pinholes no longer appear strange.", 20.0, END, base));
      result.add(eventFor("End of the partial phase.", endPartialEclipse, base));
    }
    else if (maximumEclipse.localEclipseType() == EclipseType.Partial) {
      Worksheet base = maximumEclipse;
      result.add(eventFor("Start of the partial phase.", startPartialEclipse, base));
      result.addAll(eventsFor("Partial phase increasing.", partialPhasesStart, base));
      result.add(eventFor("MAXIMUM eclipse.", maximumEclipse, base));
      result.addAll(eventsFor("Partial phase decreasing.", partialPhasesEnd, base));
      result.add(eventFor("End of the partial phase.", endPartialEclipse, base));
    }
    else if (maximumEclipse.localEclipseType() == EclipseType.Annular) {
      Worksheet base = maximumEclipse;
      result.add(eventFor("Start of the partial phase.", startPartialEclipse, base));
      result.addAll(eventsFor("Partial phase increasing.", partialPhasesStart, base));
      result.add(eventFor("ANNULARITY STARTS.", startTotalOrAnnularEclipse, base));
      result.add(eventFor("Maximum eclipse.", maximumEclipse, base));
      result.add(eventFor("ANNULARITY ENDS.", endTotalOrAnnularEclipse, base));
      result.addAll(eventsFor("Partial phase decreasing.", partialPhasesEnd, base));
      result.add(eventFor("End of the partial phase.", endPartialEclipse, base));
    }
    Collections.sort(result);
    return result;
  }
  
  private TimelineEvent eventFor(String text, Worksheet w, Worksheet base) {
    return new TimelineEvent(
      w.localCivilTime().toLocalTime(), 
      text, 
      Duration.between(base.localCivilTime(), w.localCivilTime()), 
      w.magnitude(),
      w.h
    );
  }
  
  private List<TimelineEvent> eventsFor(String text, List<PartialPhase> partialPhases, Worksheet base){
    List<TimelineEvent> result = new ArrayList<>();
    for(PartialPhase phase : partialPhases) {
      TimelineEvent event = new TimelineEvent(
        phase.when().toLocalTime(), 
        text, 
        Duration.between(base.localCivilTime(), phase.when()), 
        phase.magnitude(),
        phase.altitude()
      );
      result.add(event);
    }
    return result;
  }
  
  /**
   * @param minutes is in decimal minutes; this offset is with respect to the <code>t</code> attached to the <code>base</code>.
   */
  private TimelineEvent standardOffsetEventFor(String text, Double minutes, boolean isBefore, Worksheet base) {
    Double t = specificTimeWithRespectToPeak(isBefore, minutes); //decimal hours 
    //calc a worksheet to get the time and mag
    Worksheet w = new Worksheet(t, ΔT, bessel, location);
    w.compute();
    return new TimelineEvent(w.localCivilTime().toLocalTime(), text, Duration.between(base.localCivilTime(), w.localCivilTime()), w.magnitude(), w.h);
  }
}
