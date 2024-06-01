package custom.solar.eclipse.viewer.astrocalc;

import static custom.solar.eclipse.viewer.config.Constants.NL;
import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.tan;
import static java.lang.Math.sqrt;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import custom.solar.eclipse.viewer.math.Maths;

/**
 Calculate the core data for the local circumstances of a solar eclipse.
  
 The caller must always invoke the {@link Worksheet#compute()} method.
 <P>For a given time, this struct-like class holds most of the pertinent data related to the local circumstances of an eclipse.
 
 <P>This class implements inverse interpolation (to find the times of events) by recomputing the worksheet using a 
 slightly different time, until some condition is met. 
*/
final class Worksheet {
  
  /**
   Constructor.
   @param t decimal hours, to be added to T0, the nominal (integral) hour of maximum eclipse (from the Besselian Elements).
   @param ΔT in seconds.
   @param location for which the local circumstances of the eclipse are calculated.
   @param bessel the core data needed for the standard calculation of local circumstances.
   @param location where on Earth to find the local circumstances of the solar eclipse.
  */
  Worksheet(double t, double ΔT, BesselianElements bessel, Location location){
    this.t = t;
    this.ΔT = ΔT;
    this.bessel = bessel;
    this.location = location;
  }
  
  /**
   Compute the core data for finding the local circumstances of a solar eclipse.
    
   Reference: Elements of Solar Eclipses 1951-2200, by Jean Meeus (1989).
   See also the Explanatory Supplement to the Astronomical Ephemeris (1961). 
  */
  void compute() {
    //See Meeus page 24ff.
    //The Explanatory Supplement names X as x and Y as y
    X = bessel.X().valueAt(t); 
    Y = bessel.Y().valueAt(t);
    d = bessel.d().valueAt(t); //rads
    MU = bessel.mu().valueAt(t); //rads
    //The Explanatory Supplement names L1 as l1 and L2 as l2
    L1 = bessel.L1().valueAt(t);
    L2 = bessel.L2().valueAt(t);
    
    Xprime = bessel.X().derivative().valueAt(t); 
    Yprime = bessel.Y().derivative().valueAt(t);
    
    //The Explanatory Supplement names H as θ
    H = Maths.radsToDegs(MU) - Maths.radsToDegs(location.λrev()) - DEGREES_PER_SIDEREAL_DAY * ΔT; //degrees
    H = Maths.degToRads(H); //rads!!
    
    ξ = location.ρcosφ() * sin(H); 
    η = location.ρsinφ() * cos(d) - location.ρcosφ() * cos(H) * sin(d);
    ζ = location.ρsinφ() * sin(d) + location.ρcosφ() * cos(H) * cos(d);
    
    double factor = 0.01745329;
    M1 = bessel.mu().coefficient(1);
    d1 = bessel.d().coefficient(1);
    ξprime = factor * M1 * location.ρcosφ() * cos(H); 
    ηprime = factor * (M1 * ξ * sin(d) - ζ * d1);
    
    u = X - ξ;
    v = Y - η;
    m = sqrt(u*u + v*v);
    
    //The Explanatory Supplement names a as u' and b as v'
    a = Xprime - ξprime; 
    b = Yprime - ηprime;
    n = sqrt(a*a + b*b);
    
    //these two aren't derivatives; he uses prime only because he is restricted to capital letters
    //The Explanatory Supplement names these L1 and L2
    L1prime = L1 - ζ * bessel.tanF1(); // not a derivative ! always positive 
    L2prime = L2 - ζ * bessel.tanF2(); // not a derivative ! negative for total eclipses; positive for annular
    
    //the correction to be applied in finding the time of local maximum eclipse
    τM = - (u*a + v*b) / (n*n); //hours 

    //the magnitude of the eclipse
    //see Chauvenet page 478: https://archive.org/details/amanualspherica08chaugoog/page/478/mode/2up?view=theater
    G = (L1prime - m) / (L1prime + L2prime); //if < 0, then no eclipse at the given location; compare to A below
    
    //the position angle of the Moon with respect to the Sun's center
    P = atan2(u, v);
    P = Maths.in2pi(P);
    
    //ratio of the Moon's diameter to the Sun's diameter, regardless of how much the Moon obscures the Sun
    A = (L1prime - L2prime) / (L1prime + L2prime); //compare to G above

    //the altitude of the Sun
    h = asin( 
      sin(d) * sin(location.φ()) + 
      cos(d) * cos(location.φ()) * cos(H)
    ); //Meeus page 12
    
    //the azimuth of the Sun
    //see Astronomical Algorithms, Meeus 1991, page 89
    az = atan2(sin(H), (cos(H)*sin(location.φ()) - tan(d)*cos(location.φ())));
    az = az + Math.PI; //use North as the zero-point, instead of south
    az = Maths.in2pi(az);
   
    //the parallactic angle: North Celestial Pole -> Sun -> Moon
    q = asin( (cos(location.φ()) * sin(H)) / cos(h) );
    if (location.φ() < 0) {
      //in the southern hemisphere, measure the angle from the South Celestial Pole instead
      //Meeus does not mention this step, but I found it necessary in order to correct the orientation
      //of the charts showing partial phases
      q = Math.PI - q;
    }
    q = Maths.in2pi(q);
    //the angle Zenith -> Sun -> Moon
    Z = Maths.in2pi(P - q);
  }
  
  /** 
   The magnitude of the eclipse reflects how much of the Sun's diameter is covered by the Moon at maximum eclipse. 
   If negative, then there is no eclipse. 
   */
  double magnitude() {
    return G;
  }
  
  /** Used for inverse interpolation. */
  double correctionToTimeOfMaxEclipse() {
    return τM;
  }

  /** Used for inverse interpolation. */
  double initialCorrectionToTimeOfContact(boolean before, boolean penumbra) {
    double Lprime = penumbra ? L1prime : L2prime;
    double S = S(Lprime);
    //Meeus seems to be in error; L1prime is always positive; L2prime is either sign.
    //Because of that, I've added an absolute value here for Lprime.
    return sign(before) * (Math.abs(Lprime) / n) * sqrt(1 - S*S);
  }
  
  /** Used for inverse interpolation. */
  double correctionToTimeOfContact(boolean before, boolean penumbra) {
    double one = - (u*a + v*b) / (n*n);
    double two = initialCorrectionToTimeOfContact(before, penumbra);
    return one + two;
  }
  
  /** 
   The local eclipse type can differ from the global eclipse type.
   This method never returns <code>Hybrid</code>, since that idea is global in nature, not local. 
  */
  EclipseType localEclipseType() {
    EclipseType result = EclipseType.None;
    if (G < 0) {
      //no eclipse at all
    }
    else if (m > Math.abs(L2prime)) {
      result = EclipseType.Partial;
    }
    else if (L2prime < 0){
      result = EclipseType.Total;
    }
    else {
      result = EclipseType.Annular;
    }
    return result;
  }

  /** The date and time corresponding to T0 + t. This doesn't reflect ΔT, or the location's offset from UTC. */
  LocalDateTime TT() {
    return convertToDateTime(t, 0);
  }

  /** The date and time corresponding to T0 + t - ΔT. This doesn't reflect the location's offset from UTC. */
  LocalDateTime UTC() {
    return convertToDateTime(t, ΔT);
  }

  /** The date and time corresponding to T0 + t - ΔT + offset, according to the location's offset from UTC. */
  LocalDateTime localCivilTime() {
    LocalDateTime utc = UTC();
    LocalDateTime result = utc.plusHours(location.offsetHours());
    result = result.plusMinutes(location.offsetMinutes());
    return result;
  }
  
  @Override public String toString() {
    StringBuilder result = new StringBuilder("Worksheet:" + NL);
    append(result, "t",t);
    append(result, "ΔT",ΔT);
    append(result, "X",X);
    append(result, "Y",Y);
    append(result, "d",Maths.radsToDegs(d) + "°");
    append(result, "MU",Maths.radsToDegs(MU)+ "°");
    append(result, "L1",L1);
    append(result, "L2",L2);
    append(result, "Xprime",Xprime);
    append(result, "Yprime",Yprime);
    append(result, "H",Maths.radsToDegs(H) + "°");
    append(result, "ξ",ξ);
    append(result, "η",η);
    append(result, "ζ",ζ);
    append(result, "M1",M1);
    append(result, "d1",d1);
    append(result, "ξprime",ξprime);
    append(result, "ηprime",ηprime);
    append(result, "u",u);
    append(result, "v",v);
    append(result, "m",m);
    append(result, "a",a);
    append(result, "b",b);
    append(result, "n",n);
    append(result, "L1prime",L1prime);
    append(result, "L2prime",L2prime);
    append(result, "τM",τM);
    append(result, "G",G);
    append(result, "P", Maths.radsToDegs(P) + "°");
    append(result, "A",A);
    append(result, "h", Maths.radsToDegs(h) + "°");
    append(result, "az", Maths.radsToDegs(az) + "°");
    append(result, "q", Maths.radsToDegs(q) + "°");
    append(result, "Z", Maths.radsToDegs(Z) + "°");
    return result.toString();
  }

  /** Decimal number of hours added to T0 (from the Besselian Elements). */
  double t;
  /** Number of seconds, TT - UT. */
  double ΔT;
  double X;
  double Y;
  double d;
  double MU;
  double L1;
  double L2;
  double Xprime;
  double Yprime;
  double H;
  double ξ;
  double η;
  double ζ;
  double M1;
  double d1;
  double ξprime;
  double ηprime;
  double u;
  double v;
  double m;
  double a; 
  double b;
  double n;
  double L1prime;
  double L2prime;
  double τM;
  /** The magnitude of the eclipse. */
  double G;
  /** Position angle of the Moon with respect to the Sun. */
  double P;
  /** The Moon's diameter in units of the Sun's diameter. */
  double A;
  /** Altitude of the Sun. */
  double h;
  /** Azimuth of the Sun, with 0 being due North. */
  double az;
  /** Parallactic angle of the Moon with respect to the Sun. */
  double q;
  /** The angle Zenith-Sun-Moon. */
  double Z;
  
  private BesselianElements bessel;
  private Location location;
  private static final double DEGREES_PER_SIDEREAL_DAY = 360.0/86164.0905;
  private static long SECONDS_PER_HOUR = 60L * 60L;
  private static long NANOS_PER_HOUR = 60L * 60L * 1_000_000_000L;
  
  private double S(double Lprime) {
    double numer = a*v - u*b;
    double denom = n * Lprime;
    return numer/denom;
  }
  
  private int sign(boolean before) {
    return before ? -1 : +1;
  }
  
  private void append(StringBuilder result, String name, Object value) {
    result.append("  " + name + ": " + value + NL);
  }
  
  /**
   @param t fractional hours from T0.
   @param ΔT in seconds
  */
  private LocalDateTime convertToDateTime(double t , double ΔT) {
    LocalDate date = bessel.whenMaxEclipse().toLocalDate();
    double hours = bessel.T0() + t - ΔT/SECONDS_PER_HOUR; //hours
    if (hours < 0) {
      //need to go to the previous day
      //there are indeed cases in which T0 is 0; must be careful
      date = date.minusDays(1);
      hours = hours + 24.0;
    }
    long nanos = Math.round(hours * NANOS_PER_HOUR);
    LocalTime time = LocalTime.ofNanoOfDay(nanos);
    return LocalDateTime.of(date, time);
  }
}
