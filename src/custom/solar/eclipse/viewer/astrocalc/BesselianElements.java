package custom.solar.eclipse.viewer.astrocalc;

import static custom.solar.eclipse.viewer.config.Constants.NL;

import java.time.LocalDateTime;

import custom.solar.eclipse.viewer.math.Polynomial;

/** 
 Standard data used to calculate the local circumstances of an eclipse.
 Reference: <em>Elements of Solar Eclipses 1951-2200</em>, Jean Meeus, 1989. 
*/
final class BesselianElements {
  
  /**
    Constructor.
    @param whenMaxEclipse date and time (TT) of maximum eclipse. To change to civil time UTC, ΔT must be subtracted. 
    To change to local civil time, a further offset from Greenwich must be taken into account. 
    @param jdMaxEclipse Julian Date (TT) of the time of maximum eclipse.
    @param eclipseType the general character of the eclipse
    @param t0 nominal integral hour near maximum eclipse, used as a base for calculating local circumstances etc.
    @param x coordinate of the shadow axis on the fundamental plane.
    @param y coordinate of the shadow axis on the fundamental plane.
    @param d declination for the direction of the shadow axis.
    @param mu ephemeris hour angle for the direction of the shadow axis.
    @param L1 radius of the penumbral cone in the fundamental plane, taking the Earth's radius as unit.
    @param L2 radius of the umbral cone in the fundamental plane, taking the Earth's radius as unit. 
    Positive for an annular eclipse, negative for a total eclipse.
    @param tanF1 tangent of the angle between the penumbral cone elements and the shadow axis.
    @param tanF2 tangent of the angle between the umbral cone elements and the shadow axis.
  */
  BesselianElements(
    LocalDateTime whenMaxEclipse, Double jdMaxEclipse, EclipseType eclipseType, Integer t0, Polynomial x, Polynomial y, Polynomial d, Polynomial mu, 
    Polynomial L1, Polynomial L2, Double tanF1, Double tanF2
  ){
    this.whenMaxEclipse = whenMaxEclipse;
    this.jdMaxEclipse = jdMaxEclipse;
    this.eclipseType = eclipseType;
    this.t0 = t0;
    this.x = x;
    this.y = y;
    this.d = d;
    this.mu = mu;
    this.L1 = L1;
    this.L2 = L2;
    this.tanF1 = tanF1;
    this.tanF2 = tanF2;
  }

  LocalDateTime whenMaxEclipse() { return whenMaxEclipse; }
  Double jdMaxEclipse() { return jdMaxEclipse; }
  EclipseType eclipseType() {return eclipseType;}
  Integer T0() { return t0; }
  Polynomial X() { return x; }
  Polynomial Y() {return y; }
  Polynomial d() { return d;  }
  Polynomial mu() { return mu; }
  Polynomial L1() { return L1; }
  Polynomial L2() { return L2;}
  Double tanF1() { return tanF1;  }
  Double tanF2() {return tanF2; }
  
  @Override public String toString() {
    StringBuilder result = new StringBuilder("Besselian Elements:" + NL);
    addTo(result, "When", whenMaxEclipse);
    addTo(result, "JD", jdMaxEclipse);
    addTo(result, "Type", eclipseType);
    addTo(result, "T0" , t0);
    addTo(result, "x" , x);
    addTo(result, "y",  y);
    addTo(result, "d" , d);
    addTo(result, "mu" , mu);
    addTo(result, "L1" , L1);
    addTo(result, "L2" , L2);
    addTo(result, "tanF1" , tanF1);
    addTo(result, "tanF2" , tanF2);
    return result.toString();
  }
  
  /** Physics time, TT. */
  private LocalDateTime whenMaxEclipse;

  private Double jdMaxEclipse;
  private EclipseType eclipseType;
  private Integer t0;
  private Polynomial x;
  private Polynomial y;
  private Polynomial d;
  private Polynomial mu;
  private Polynomial L1;
  private Polynomial L2;
  private Double tanF1;
  private Double tanF2;

  private void addTo(StringBuilder result, String name, Object value) {
    result.append("  " + name + ": " + value.toString() + NL);
  }
}
