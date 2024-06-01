package custom.solar.eclipse.viewer.astrocalc;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import custom.solar.eclipse.viewer.draw.TimelineEvent;

/** Displayed data for the local circumstances of an eclipse. Simple data carrier. */
public final class EclipseDisplay {

  /**
   Constructor.
    
   All dates and times reflect the location's local offset from UTC.
     
   @param totalityAnnularityStarts null only if the eclipse is partial.
   @param totalityAnnularityEnds null only if the eclipse is partial.
  */
  public EclipseDisplay(
    EclipseType eclipseType,
    LocalDateTime partialStarts,
    LocalDateTime partialEnds,
    PartialPhase maxEclipse,
    List<PartialPhase> phasesBefore,
    List<PartialPhase> phasesAfter,
    double altitude,
    double azimuth,
    double magnitude,
    LocalDateTime totalityAnnularityStarts,
    LocalDateTime totalityAnnularityEnds,
    List<TimelineEvent> timelineEvents
  ){
    this.eclipseType = eclipseType;
    this.partialStarts = partialStarts;
    this.partialEnds = partialEnds;
    this.maxEclipse = maxEclipse;
    this.phasesBefore = phasesBefore;
    this.phasesAfter = phasesAfter;
    this.altitude = altitude;
    this.azimuth = azimuth;
    this.magnitude = magnitude;
    this.totalityAnnularityStarts = totalityAnnularityStarts;
    this.totalityAnnularityEnds = totalityAnnularityEnds;
    this.timelineEvents = timelineEvents;
  }
  
  public EclipseType eclipseType() { return eclipseType; }

  public LocalDateTime partialStarts() { return partialStarts; }
  public LocalDateTime partialEnds() { return partialEnds; }
  public Duration durationPartial() { return Duration.between(partialStarts, partialEnds); }

  /** Null for a partial eclipse. */
  public LocalDateTime totalityAnnularityStarts() { return totalityAnnularityStarts; }
  /** Null for a partial eclipse. */
  public LocalDateTime totalityAnnularityEnds() { return totalityAnnularityEnds; }
  public Duration durationTotalityAnnularity() { return Duration.between(totalityAnnularityStarts, totalityAnnularityEnds); }
  
  /** Partial phases before max eclipse. */
  public List<PartialPhase> phasesBefore() { return phasesBefore; }
  /** Partial phases after max eclipse. */
  public List<PartialPhase> phasesAfter() {return phasesAfter;}
  
  public PartialPhase maxEclipse() { return maxEclipse; }
  
  /** Altitude of the Sun at max-eclipse (radians). */ 
  public double altitude() { return altitude; }
  
  /** Azimuth of the Sun at max-eclipse (radians). */ 
  public double azimuth() { return azimuth;  }

  /** Magnitude at maximum eclipse. */
  public double magnitude() { return magnitude;  }
  
  public List<TimelineEvent> timelineEvents(){ return timelineEvents; }
  
  private EclipseType eclipseType;
  
  private LocalDateTime partialStarts;
  private LocalDateTime partialEnds;
  private LocalDateTime totalityAnnularityStarts;
  private LocalDateTime totalityAnnularityEnds;
  
  /** Before local max eclipse. */
  private List<PartialPhase> phasesBefore;
  /** After local max eclipse. */
  private List<PartialPhase> phasesAfter;
  private PartialPhase maxEclipse;
  
  private double altitude;
  private double azimuth;
  private double magnitude;
  
  private List<TimelineEvent> timelineEvents;
  
}
