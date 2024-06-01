package custom.solar.eclipse.viewer.config;

import java.util.ArrayList;
import java.util.List;

import custom.solar.eclipse.viewer.math.Maths;

/** 
 Configuration data for the generation of the viewer.
 
 The configuration data can come from different sources. 
 When run as a standalone program, the data can come from a text file.
 If this project is deployed in a servlet environment, the data may come from user input in a web form.
 
 <P>This object is 'pilgrim data'. It's passed around from the top-level object to many lower-level objects.
 This design avoids storing data in static fields, and can be used both in a standalone program, and in 
 server environments. 
*/
public final class Config {
  
  public Config(
    String eclipseDateUTC, 
    String location, Double latitude, Double longitude, Integer hoursOffsetFromUT, Integer minutesOffsetFromUT, Double altitude, QRCode qrCode1, QRCode qrCode2, 
    Float width, Float height, Float viewerWidth, Float viewerHeight, Float viewerTopMargin, Float eyeholeWidth, Float eyeholeHeight, Float eyeholeCenter, String outputDir, 
    String fontDir, Double armsLength, List<String> totalityAdvice, String producedBy, Double deltaT, Integer gapBetweenPartialPhases
  ){
    this.eclipseDateUTC = eclipseDateUTC;
    this.location = location;
    this.latitude = latitude;
    this.longitude = longitude;
    this.hoursOffsetFromUT = hoursOffsetFromUT;
    this.minutesOffsetFromUT = minutesOffsetFromUT;
    this.altitude = altitude;
    this.qrCode1 = qrCode1;
    this.qrCode2 = qrCode2;
    this.width = width;
    this.height = height;
    this.viewerWidth = viewerWidth;
    this.viewerHeight = viewerHeight;
    this.viewerTopMargin = viewerTopMargin;
    this.eyeholeWidth = eyeholeWidth;
    this.eyeholeHeight = eyeholeHeight;
    this.eyeholeCenter = eyeholeCenter;
    this.outputDir = outputDir;
    this.fontDir = fontDir;
    this.armsLength = armsLength;
    this.totalityAdvice = totalityAdvice;
    this.producedBy = producedBy;
    this.deltaT = deltaT;
    this.gapBetweenPartialPhases = gapBetweenPartialPhases;
  }

  /** The date of the solar eclipse (UTC, using the Greenwich meridian), in format yyyy-mm-dd.  */
  public String eclipseDateUTC() { return eclipseDateUTC; }
  
  /** Simple description of the observer's location. */
  public String location() {return location;  }
  
  /** The observer's geographical latitude in radians. */
  public Double latitude() { return latitude; }
  
  /** The observer's geographical longitude in radians. */
  public Double longitude() { return longitude; }

  /** Height (meters) of the location above a standard geoid. */
  public Double altitude() { return altitude; }
  
  /** How many hours between the observer's time zone and the prime meridian. */
  public Integer hoursOffsetFromUT() { return hoursOffsetFromUT;  }
  
  /** 
   How many minutes (0..59) to be added to {@link #hoursOffsetFromUT()}.
   For most jurisdictions, this number is 0, since most time zones are offset by a whole
   number of hours from UT. 
  */
  public Integer minutesOffsetFromUT() {  return minutesOffsetFromUT;  }
  
  /** A link of the user's choosing, to be rendered as a QRCode. */
  public QRCode qrCode1() { return qrCode1; }
  
  /** A link of the user's choosing, to be rendered as a QRCode. */
  public QRCode qrCode2() { return qrCode2; }
  
  /** 
   Page width in points (72 points per inch).
   Not the width of the viewer, but the width of the page that contains the chart.
   Same for the height. 
  */
  public Float width() {return width; }
  
  /** Page height in points (72 points per inch). */
  public Float height() { return height; }
  
  /** Viewer width in points (72 points per inch). */
  public Float viewerWidth() {return viewerWidth; }

  /** Viewer height in points (72 points per inch). */
  public Float viewerHeight() {return viewerHeight; }
  
  /** Margin at the top of the viewer, in points (72 points per inch). */
  public Float viewerTopMargin() {return viewerTopMargin; }
  
  /** The width of the eyehole(s) as a fraction of the {@link #viewerWidth()}. */
  public Float eyeholeWidth() {return eyeholeWidth; }
  
  /** The height of the eyehole(s) as a fraction of the {@link #viewerHeight()}. */
  public Float eyeholeHeight() {return eyeholeHeight; }
  
  /** 
   Controls the placement of the center of the eyehole.
   A fraction of the {@link #viewerWidth()}.
   The magic value of 0 means that there's only 1 large eyehole.
   Any other value implies two eyeholes, placed this fraction of the viewer-width away from the center. 
  */
  public Float eyeholeCenter() {return eyeholeCenter; }
  
  /**
   The directory where the PDF file is generated. The directory must already exist.
   This setting may not be needed in a servlet environment, where the files are served as a byte stream to the browser. 
  */
  public String outputDir() { return outputDir; }
  
  /** 
   The directory on the computer that contains the Times New Roman font.
   This tool is hard-coded to that specific font. 
   Times New Roman is pre-installed on almost all computers. 
  */
  public String fontDir() { return fontDir; }
  
  /** Distance in centimeters from your eye to the viewer. */
  public Double armsLength() { return armsLength; }

  /** Remarks about what to do during totality. */
  public List<String> totalityAdvice(){ return totalityAdvice; }
  
  /** The person or organization that produced this eclipse viewer. */
  public String producedBy() { return producedBy; }

  /** The difference TT (physics time) -  UTC (civil time), in seconds. */
  public Double ΔT() { return deltaT; }
  
  /** Integral number of minutes between partial phases, used in charting the phase. */
  public Integer gapBetweenPartialPhases() { return gapBetweenPartialPhases; }
  
  /** Calculated field. */
  public boolean isNorthernHemisphere() { return latitude >= 0; }
  
  /** For debugging. All config settings. */
  @Override public String toString() {
    StringBuilder result = new StringBuilder();
    toStringLine(Setting.eclipse_date_utc, eclipseDateUTC(), result);
    toStringLine(Setting.location_name, location(), result);
    toStringLine(Setting.location_latitude, Maths.radsToDegreeString(latitude()), result);
    toStringLine(Setting.location_longitude, Maths.radsToDegreeString(longitude()), result);
    toStringLine(Setting.location_altitude, altitude(), result);
    toStringLine(Setting.location_hours_offset_from_ut, hoursOffsetFromUT(), result);
    toStringLine(Setting.location_minutes_offset_from_ut, minutesOffsetFromUT(), result);
    toStringLine(Setting.qr_code_1, qrCode1(), result);
    toStringLine(Setting.qr_code_2, qrCode2(), result);
    toStringLine(Setting.output_directory, outputDir(), result);
    toStringLine(Setting.width, width(), result);
    toStringLine(Setting.height, height(), result);
    toStringLine(Setting.viewer_width, viewerWidth(), result);
    toStringLine(Setting.viewer_height, viewerHeight(), result);
    toStringLine(Setting.viewer_top_margin, viewerTopMargin(), result);
    toStringLine(Setting.eyehole_width, eyeholeWidth(), result);
    toStringLine(Setting.eyehole_height, eyeholeHeight(), result);
    toStringLine(Setting.eyehole_center, eyeholeHeight(), result);
    toStringLine(Setting.font_directory, fontDir(), result);
    toStringLine(Setting.arms_length, armsLength(), result);
    toStringLine(Setting.totality_advice, totalityAdvice(), result);
    toStringLine(Setting.produced_by, producedBy(), result);
    toStringLine(Setting.delta_t, ΔT(), result);
    toStringLine(Setting.gap_between_partial_phases, gapBetweenPartialPhases(), result);
    return result.toString().trim();
  }
  
  private String eclipseDateUTC = "";
  private String location = "";
  private Double latitude;
  private Double longitude;
  private Integer hoursOffsetFromUT;
  private Integer minutesOffsetFromUT;
  private Double altitude;
  private QRCode qrCode1;
  private QRCode qrCode2;
  
  private Float width;
  private Float height;
  
  private Float viewerWidth;
  private Float viewerHeight;
  private Float viewerTopMargin;
  
  private Float eyeholeWidth;
  private Float eyeholeHeight;
  private Float eyeholeCenter;
  
  private String outputDir = "";
  private String fontDir = "";
  private Double armsLength;
  private List<String> totalityAdvice = new ArrayList<>();
  private String producedBy = "";
  private Double deltaT;
  private Integer gapBetweenPartialPhases;

  private void toStringLine(Setting setting, Object value, StringBuilder result) {
    result.append("  " + setting.toString() + " = " + value.toString() + Constants.NL); 
  }
}
