package custom.solar.eclipse.viewer.astrocalc;

import custom.solar.eclipse.viewer.math.Maths;

/** The place on the Earth for which local circumstances of the eclipse are to be calculated. */
final class Location {
  
  /** 
   Angles are all all in radians. The height is in meters.
   The offsets are positive east of Greenwich, negative west of Greenwich.
   The pair of offsets (hours and minutes) are always the same sign.
   @param name text describing the location (city, town, etc.)
   @param φ geographic latitude in radians.
   @param λ geographic longitude in radians.
   @param height altitude in meters.
   @param offsetHours number of hours difference from Greenwich. Negative west of Greenwich.
   @param offsetMinutes number of minutes difference from Greenwich. Negative west of Greenwich. 
   This value is added to <code>offsetHours</code>, to get the full offset. 
   For most places, this value is 0.
  */
  Location(String name, Double φ, Double λ, Double height, int offsetHours, int offsetMinutes){
    this.name = name;
    this.φ = φ;
    this.λ = λ;
    this.height = height;
    this.offsetHours = offsetHours;
    this.offsetMinutes = offsetMinutes;
    compute();
  }
  
  /** In units of the Earth's radius. Accounts for the flattening of the Earth. */
  Double ρsinφ() { return ρsinφ; }
  /** In units of the Earth's radius. Accounts for the flattening of the Earth. */
  Double ρcosφ() { return ρcosφ; }

  /*** Latitude. */
  Double φ() { return φ; }
  
  /** Longitude, positive east from Greenwich. */
  Double λ() { return λ; }
  /** Longitude, positive west from Greenwich (the convention used by Meeus in Elements of Solar Eclipses). */
  Double λrev() { return -λ; }
  
  /** Altitude in meters. */
  Double height() { return height; }
  
  /** Offset from Greenwich. */
  int offsetHours() { return offsetHours; }
  /** Some jurisdictions (for example Newfoundland and Labrador in Canada) are offset by hours and minutes from Greenwich, not just hours. */
  int offsetMinutes() { return offsetMinutes; }
  
  @Override public String toString() {
    return name + " φ:" + Maths.radsToDegs(φ) + " λ:" + Maths.radsToDegs(λ) + " h:" + height + "m" + " offset:" + offsetHours + "h" + offsetMinutes + "m"; 
  }
  
  private String name;
  private Double φ;
  private Double λ;
  private Double height; 
  private int offsetHours;
  private int offsetMinutes;
  
  //these names are missing the primes at the end, compared to Meeus 1989, Elements of Solar Eclipses 1951-2200.
  private Double ρsinφ;
  private Double ρcosφ;
  
  private static final double EARTHS_RADIUS = 6378140.0; //meters
  private static final double FLATTEN_EARTH = 0.99664719; // 1 - 1/298.257
  
  private void compute() {
    double u = Math.atan(FLATTEN_EARTH * Math.tan(φ));
    ρsinφ = FLATTEN_EARTH * Math.sin(u) + (height / EARTHS_RADIUS) * Math.sin(φ);
    ρcosφ = Math.cos(u) + (height / EARTHS_RADIUS ) * Math.cos(φ);
  }
}
