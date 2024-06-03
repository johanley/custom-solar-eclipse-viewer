package custom.solar.eclipse.viewer.config;

import static custom.solar.eclipse.viewer.util.LogUtil.log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import custom.solar.eclipse.viewer.math.Maths;
import custom.solar.eclipse.viewer.util.DataFileReader;

/** Read in configuration data from a text file. */
public final class ConfigFromFile {

  /** 
   Read the <em>config.ini</em> file and return a corresponding {@link Config} object.
   THIS METHOD MUST BE CALLED IMMEDIATELY UPON STARTUP when running as a standalone program (not in a servlet context). 
   The returned object will be passed around to many other classes.
  
   <P>By default, the file is located in the same directory as this class.
   To use a different file as the config file, use the command line setting: 
   
   {@code -DviewerConfigFile=C:\mydirectory\myfile}
   
   <P>in which the full file name in required, including the directory.
  */
  public Config init() {
    DataFileReader reader = new DataFileReader();
    String fileLocationOverride = System.getProperty("viewerConfigFile");
    List<String> lines = null;
    if (fileLocationOverride == null) {
      log("Reading config file: " + CONFIG_INI);
      lines = reader.readFile(this.getClass(), CONFIG_INI);
    }
    else {
      log("Reading config file from System property: " + fileLocationOverride);
      lines = reader.readFile(fileLocationOverride);
    }

    for(String line : lines) {
      processEach(line.trim());
    }
    return buildConfigObjectFromSettings();
  }

  // PRIVATE 

  private static final String CONFIG_INI = "config.ini";
  private static final String SEPARATOR = "=";
  private static final int NOT_FOUND = -1;

  private String eclipseDateUTC;
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

  private void processEach(String line) {
    if (line.startsWith(DataFileReader.COMMENT) || line.length() == 0) {
      //ignore it!
    }
    else {
      //there must be a name-value pair on the line
      NameValuePair pair = parse(line);
      if (matches(Setting.output_directory, pair.name)) {
        outputDir = pair.value;
      }
      else if (matches(Setting.width, pair.name)) {
        width = asPoints(pair.value);
      }
      else if (matches(Setting.height, pair.name)) {
        height = asPoints(pair.value);
      }
      else if (matches(Setting.viewer_width, pair.name)) {
        viewerWidth = asPoints(pair.value);
      }
      else if (matches(Setting.viewer_height, pair.name)) {
        viewerHeight = asPoints(pair.value);
      }
      else if (matches(Setting.viewer_top_margin, pair.name)) {
        viewerTopMargin = asPoints(pair.value);
      }
      else if (matches(Setting.eyehole_width, pair.name)) {
        eyeholeWidth = asFloat(pair.value);
      }
      else if (matches(Setting.eyehole_height, pair.name)) {
        eyeholeHeight = asFloat(pair.value);
      }
      else if (matches(Setting.eyehole_center, pair.name)) {
        eyeholeCenter = asFloat(pair.value);
      }
      else if (matches(Setting.location_name, pair.name)) {
        location = pair.value;
      }
      else if (matches(Setting.location_latitude, pair.name)) {
        latitude = asRads(pair.value);
      }
      else if (matches(Setting.location_longitude, pair.name)) {
        longitude = asRads(pair.value);
      }
      else if (matches(Setting.location_altitude, pair.name)) {
        altitude = asDouble(pair.value);
      }
      else if (matches(Setting.location_hours_offset_from_ut, pair.name)) {
        hoursOffsetFromUT = asInteger(pair.value);
      }
      else if (matches(Setting.location_minutes_offset_from_ut, pair.name)) {
        minutesOffsetFromUT = asInteger(pair.value);
      }
      else if (matches(Setting.qr_code_1, pair.name)) {
        qrCode1 = asQRCode(pair.value);
      }
      else if (matches(Setting.qr_code_2, pair.name)) {
        qrCode2 = asQRCode(pair.value);
      }
      else if (matches(Setting.eclipse_date_utc, pair.name)) {
        eclipseDateUTC = pair.value;
      }
      else if (matches(Setting.font_directory, pair.name)) {
        fontDir = pair.value;
      }
      else if (matches(Setting.arms_length, pair.name)) {
        armsLength = asDouble(pair.value);
      }
      else if (matches(Setting.totality_advice, pair.name)) {
        totalityAdvice = asStringList(pair.value);
      }
      else if (matches(Setting.produced_by, pair.name)) {
        producedBy = pair.value;
      }
      else if (matches(Setting.delta_t, pair.name)) {
        deltaT = asDouble(pair.value);
      }
      else if (matches(Setting.gap_between_partial_phases, pair.name)) {
        gapBetweenPartialPhases = asInteger(pair.value);
      }
    }
  }
  
  private boolean matches(Setting setting, String name) {
    return name.equalsIgnoreCase(setting.toString());
  }

  private Double asDouble(String value) {
    return Double.valueOf(value);
  }
  
  private Float asFloat(String value) {
    return Float.valueOf(value);
  }
  
  private Double asRads(String value) {
    return Maths.degToRads(asDouble(value));
  }
  
  private Integer asInteger(String value) {
    return Integer.valueOf(value);
  }
  
  private static final class NameValuePair {
    String name = "";
    String value = "";
  }
  
  private Float asPoints(String inches) {
    return Float.valueOf(inches) * Constants.POINTS_PER_INCH;
  }
  
  /** Returns null only of the separator char is not found. */
  private QRCode asQRCode(String value) {
    QRCode result = null;
    int first = value.indexOf(QRCode.SEP);
    if (first != NOT_FOUND) {
      String label = value.substring(0, first).trim();
      String url = value.substring(first + 1).trim();
      result = new QRCode(label, url);
    }
    else {
      log("ERROR. Missing expected " + QRCode.SEP + " character, to separate the text from the link: " + value);
    }
    return result;
  }
  
  private List<String> asStringList(String value){
    String[] parts = value.split(Pattern.quote("|"));
    return Arrays.asList(parts);
  }

  /** This was created because URLs can contain an equals sign. Returns null only if no equals sign found in the given line. */
  private NameValuePair parse(String line) {
    NameValuePair result = null;
    int firstEqualSign = line.indexOf(SEPARATOR);
    if (firstEqualSign != NOT_FOUND) {
      result = new NameValuePair();
      result.name = line.substring(0, firstEqualSign).trim();
      result.value = line.substring(firstEqualSign + 1).trim();
    }
    else {
      log("ERROR. Missing expected " + SEPARATOR + " character: " + line);
    }
    
    return result;
  }

  private Config buildConfigObjectFromSettings() {
    return new Config(
      eclipseDateUTC, 
      location, latitude, longitude, hoursOffsetFromUT, minutesOffsetFromUT, altitude, qrCode1, qrCode2,  
      width, height, viewerWidth, viewerHeight, viewerTopMargin, eyeholeWidth, eyeholeHeight, eyeholeCenter, outputDir, 
      fontDir, armsLength, totalityAdvice, producedBy, deltaT, gapBetweenPartialPhases 
    );
  }
}
