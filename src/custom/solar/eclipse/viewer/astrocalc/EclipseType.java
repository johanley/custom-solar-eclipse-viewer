package custom.solar.eclipse.viewer.astrocalc;

/** General character of a solar eclipse. */
public enum EclipseType {
  
  Total, 
  Annular,
  /** An eclipse which is total at some places, and annular at others. */
  Hybrid, 
  Partial,
  None;

  /** Match the first character of the input to the name of one of the enum constants. */
  static EclipseType parse(String raw) {
    EclipseType result = null;
    String firstChar = raw.substring(0,1);
    for (EclipseType et : values()) {
      if (et.name().startsWith(firstChar)) {
        result = et;
        break;
      }
    }
    if (result == null) {
      throw new IllegalArgumentException("Unknown eclipse type:" + raw);
    }
    return result;
  }
}