package custom.solar.eclipse.viewer.draw;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Objects;

import custom.solar.eclipse.viewer.math.Maths;

/** Something a person might want to know about during an eclipse. */
public final class TimelineEvent implements Comparable<TimelineEvent> {

  /**
   Constructor.
    
   @param when local civil time during the day when the event occurs.
   @param text description of the event.
   @param plusMinus the time interval between this event and another event,
   usually the start/end of totality/annularity, or the time of maximum eclipse.
   @param magnitude the magnitude of the eclipse at the given <code>when</code>
   @param alitude of the Sun in radians at the given <code>when</code>. 
  */
  public TimelineEvent(LocalTime when, String text, Duration plusMinus, Double magnitude, Double altitude){
    this.when = when;
    this.text = text;
    this.plusMinus = plusMinus;
    this.magnitude = magnitude;
    this.altitude = Maths.roundToOnePlace(Maths.radsToDegs(altitude));
  }

  public LocalTime when() {  return when; }
  public String text() { return text; }
  public Duration plusMinus() { return plusMinus;}
  public Double magnitude() {  return magnitude; }
  /** Degrees. */
  public Double altitude() {  return altitude; }
  
  @Override public int compareTo(TimelineEvent that) {
    final int BEFORE = -1;
    final int EQUAL = 0;
    final int AFTER = 1;
    if (this == that) return EQUAL;
    
    int comparison = this.when.compareTo(that.when);
    if (comparison != EQUAL) return comparison;    
    
    comparison = this.text.compareTo(that.text);
    if (comparison != EQUAL) return comparison;
    
    comparison = this.plusMinus.compareTo(that.plusMinus);
    if (comparison != EQUAL) return comparison;
    
    if (this.magnitude < that.magnitude) return BEFORE;
    if (this.magnitude > that.magnitude) return AFTER;
    
    assert this.equals(that) : "compareTo inconsistent with equals.";
    
    return EQUAL;
  }
  
  @Override public boolean equals(Object aThat) {
    if (this == aThat) return true;
    if (!(aThat instanceof TimelineEvent)) return false;
    TimelineEvent that = (TimelineEvent)aThat;
    for(int i = 0; i < this.getSigFields().length; ++i){
      if (!Objects.equals(this.getSigFields()[i], that.getSigFields()[i])){
        return false;
      }
    }
    return true;         
  }
  
  @Override public int hashCode() {
    return Objects.hash(getSigFields());     
  }
  
  @Override public String toString() {
    return "TimelineEvent: " + when + " '" + text + "'" + " mag:" + magnitude + " +/-:" + plusMinus + " alt:" + altitude; 
  }

  private LocalTime when;
  private String text;
  private Duration plusMinus;
  private Double magnitude;
  private Double altitude; //degrees
  
  private Object[] getSigFields() {
    Object[] result = {
     when, text, plusMinus, magnitude, altitude
    };
    return result;     
  }
}
