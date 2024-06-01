package custom.solar.eclipse.viewer.math;

import java.time.Duration;

/** Various utility methods of general use. */
public class Maths {
  
  public static double degToRads(double deg) {
    return deg * DEG_TO_RADS;   
  }
  
  public static double radsToDegs(double rad) {
    return rad * RADS_TO_DEG;   
  }
  
  public static double hoursToRads(double hours) {
    return degToRads(hours * HOURS_TO_DEGS);
  }
  
  /**
   Round the given value. 
    
   <P>Many graphics operations take only an int.
   To preserve as much accuracy as possible when converting from double to int, 
   you need to call this method, instead of doing a cast.
   Casting simply abandons the decimal part. 
   I saw this cause a problem: in drawing a large circle, it was actually slightly oval, I believe. 
  */
  public static int round(double val) {
    long result = Math.round(val);
    return (int)result;
  }

  public static double roundToOnePlace(double val) {
    return Math.round(val * 10.0) / 10.0; 
  }
  
  public static double roundToTwoPlaces(double val) {
    return Math.round(val * 100.0) / 100.0; 
  }
 
  public static double roundToThreePlaces(double val) {
    return Math.round(val * 1000.0) / 1000.0; 
  }
 
  /** Similar to Math.atan2(y, x), but ensures the result is in 0..2pi. */
  public static double atan3(double y, double x) {
    double result = Math.atan2(y, x); //-pi .. +pi (range is 2pi, but the sign is usually undesirable)
    if (result < 0) {
      result = result + TWO_PI;
    }
    return result;
  }

  /** Ensure that the given value is placed the range 0..2pi. */
  public static double in2pi(double rads){
    double result = rads % TWO_PI;
    if (result < 0){
      //some rads are in -pi..+pi; this will manage them
      result = result + TWO_PI;
    }
    return result;
  };
  
  public static double sqr(double val) {
    return val * val;
  }
  
  public static String hhmm(Duration duration) {
    long hours = duration.abs().toHours();
    long minutes = duration.abs().toMinutesPart();
    String padMin = minutes < 10 ? "0" : "";
    String sign = duration.isNegative() ? "-" : "+";
    return sign + hours + ":" + padMin + minutes;
  }
  
  public static String hhmmss(Duration duration) {
    Duration d = duration.abs();
    String sign = duration.isNegative() ? "-" : "+";
    
    long hours = d.toHours();
    long minutes = d.toMinutesPart();
    long seconds = d.toSecondsPart();
    String padMin = minutes < 10 ? "0" : "";
    String padSec = seconds < 10 ? "0" : "";
    return sign + hours + ":" + padMin + minutes + ":" + padSec + seconds;
  }
  
  public static final double TWO_PI = 2 * Math.PI;
  public static final double HALF_PI = Math.PI / 2.0;
 
  /** Format as +2° 16' 22.2'', for example ;*/
  public static String radsToDegreeString(double rads) {
    double val = Math.abs(rads);
    String sign =  rads > 0 ? "+" : "-";
    
    double degs = Maths.radsToDegs(val); //25.123
    double degrees = Math.floor(degs); //25
    double arcmins = (degs - degrees) * ARCMINUTES_PER_DEGREE; // 0.123 * 60 = 7.38
    double arcminutes = Math.floor(arcmins); //7
    double arcsecs = (arcmins - arcminutes) * SECONDS_PER_ARCMIN; // 0.38 * 60 =  22.8
    
    return sign + (int)degrees + "°" + (int)arcminutes + "'" + roundToThreePlaces(arcsecs) + "''";
  }
  
  //PRIVATE 
  
  private static final Double DEG_TO_RADS = TWO_PI/360.0;
  private static final Double RADS_TO_DEG = 360.0/(2*Math.PI);
  private static final Double HOURS_TO_DEGS = 15.0;
  private static final int ARCMINUTES_PER_DEGREE = 60;
  private static final int SECONDS_PER_ARCMIN = 60;
}