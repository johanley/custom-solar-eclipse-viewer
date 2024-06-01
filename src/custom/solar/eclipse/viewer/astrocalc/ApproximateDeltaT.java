package custom.solar.eclipse.viewer.astrocalc;

import static custom.solar.eclipse.viewer.util.LogUtil.log;

import custom.solar.eclipse.viewer.math.Polynomial;

/**
 Return an approximation to the difference between UTC (civil time) and TT (physics time). 
 
 <P><a href='https://www.eclipsewise.com/help/deltatpoly2014.html'>Reference</a>. 
 (This reference isn't very precise about the boundaries between the various year ranges.)
 
 <P>This class is meant to be used only as a guide. 
 As an eclipse approaches, you should provide an explicit value for ΔT in the configuration file.
*/
public final class ApproximateDeltaT {

  /** Run this method to calculate an approximate ΔT for a given year and month, in the supported range. */
  public static void main(String... args) {
    log(lookup(2000, 1));
  }

  /** The year must be in the range 1941..3000. */
  public static double lookup(int year, int month) {
    double result = 0.0;
    double y = y(year, month);
    if (year >= 2015) {
      result = from2015to3000(y);
    }
    else if (year >= 2005) {
      result = from2005to2015(y);
    }
    else if (year >= 1986) {
      result = from1986to2005(y);
    }
    else if (year >= 1961) {
      result = from1961to1986(y);
    }
    else if (year >= 1941) {
      result = from1941to1961(y);
    }
    return result;
  }

  /** Corresponds to the middle of the month. */
  private static double y(int year, int month) {
    return year + (month - 0.5)/12.0;
  }
  
  private static double from2015to3000(double y) {
    double t = y - 2015;
    return polynomial(t, 67.62, +0.3645, +0.0039755);
  }
  
  private static double from2005to2015(double y) {
    double t = y - 2005;
    return polynomial(t, 64.69, +0.2930);
  }
  
  private static double from1986to2005(double y) {
    double t = y - 2000;
    return polynomial(t, 63.86, +0.3345, -0.060374, +0.0017275, +0.000651814, +0.00002373599);
  }
  
  private static double from1961to1986(double y) {
    double t = y - 1975;
    return polynomial(t, 45.45, +1.067, -1.0/260.0, -1.0/718.0);
  }
  
  private static double from1941to1961(double y) {
    double t = y - 1950;
    return polynomial(t, 29.07, +0.407, -1.0/233.0, +1.0/2547.0);
  }
  
  /** Avoid using the power function, and make the caller more compact. */
  private static double polynomial(double t, double... coefficients) {
    Polynomial poly = new Polynomial(coefficients);
    return poly.valueAt(t);
  }
}
