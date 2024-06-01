package custom.solar.eclipse.viewer.math;

import java.util.function.UnaryOperator;

/**
 Polynomials that take a single variable.
  
 This class avoids using the power function, and makes the caller more compact. 
*/
public final class Polynomial {
  
  /** 
   @param converter is intended for conversions from degrees to radians, for example.
   @param coefficients must be in order: constant, first order, second order, and so on. 
  */
  public Polynomial(UnaryOperator<Double> converter, double... coefficients) {
    this.converter = converter;
    this.coefficients = coefficients;
  }

  public Polynomial(double... coefficients) {
    this(null, coefficients);
  }
  
  /** Return the derivative of this polynomial, with respect to its one independent variable. */
  public Polynomial derivative() {
    //abandon the first coefficient
    //multiply by the power
    double[] derivCoeff = new double[coefficients.length - 1];
    for (int idx = 1; idx < coefficients.length; ++idx) {
      derivCoeff[idx-1] = idx * coefficients[idx];
    }
    return new Polynomial(converter, derivCoeff);
  }
  
  public double coefficient(int idx) {
    return coefficients[idx];
  }
  
  /** Evaluate the polynomial for the given independent variable. */
  public double valueAt(double t) {
    //This is called "Horner's method"
    //calculate it 'backwards'
    double result = 0.0;
    for(int idx = coefficients.length - 1; idx > -1; --idx) {
      result = coefficients[idx] + (t * result);
      //result = t * (coefficients[idx] + result);
    }
    if (converter != null) {
      result = converter.apply(result);
    }
    return result;
  }
  
  @Override public String toString() {
    StringBuilder result = new StringBuilder("");
    int count = 0;
    for (Double coeff : coefficients) {
      result.append(sign(coeff) + coeff.toString() + power(count) + " ");
      ++count;
    }
    return result.toString().trim();
  }
  
  private double[] coefficients;
  private UnaryOperator<Double> converter;
  
  private String sign(Double coeff) {
    return coeff < 0 ? "" : "+";
  }
  
  private String power(int count) {
    String result = "";
    if (count == 1) {
      result = "t";
    }
    else if (count > 1) {
      result = "t^"+count;
    }
    return result;
  }
}
