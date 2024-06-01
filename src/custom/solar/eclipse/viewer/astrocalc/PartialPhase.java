package custom.solar.eclipse.viewer.astrocalc;

import java.time.LocalDateTime;

/** 
 The geometry defining the disk of the Moon relative to the disk of the Sun, 
 during a partial phase of a solar eclipse. 
*/
public final class PartialPhase {
  
  /**
   All angles are in radians, and distances are in units or the Sun's apparent radius.
   
   @param when is in local civil time.
   @param zenithAngle for the angle zenith-Sun-Moon. Depends on the local hour angle. Negative in the morning, positive in the afternoon.
   @param lunarSolarDistance from the center of the Sun's disk to the center of the Moon's disk.
   @param lunarRadius of the Moon's disk. 
   @param magnitude of the eclipse at this moment.
   @param altitude of the Sun at this moment, in radians.
  */
  public PartialPhase(LocalDateTime when, double zenithAngle, double lunarSolarDistance, double lunarRadius, double magnitude, double altitude) {
    this.when = when;
    this.zenithAngle = zenithAngle;
    this.lunarSolarDistance = lunarSolarDistance;
    this.lunarRadius = lunarRadius;
    this.magnitude = magnitude;
    this.altitude = altitude;
  }
  
  public LocalDateTime when() { return when; }
  public double zenithAngle() { return zenithAngle; }
  public double lunarSolarDistance() { return lunarSolarDistance; }
  public double lunarRadius() { return lunarRadius; }
  public double magnitude() { return magnitude; }
  public double altitude() { return altitude; }

  private LocalDateTime when;
  private double zenithAngle;
  private double lunarSolarDistance;
  private double lunarRadius;
  private double magnitude;
  private double altitude;
}
