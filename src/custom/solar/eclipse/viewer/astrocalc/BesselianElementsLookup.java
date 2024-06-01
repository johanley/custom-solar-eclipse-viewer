package custom.solar.eclipse.viewer.astrocalc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.regex.Pattern;

import custom.solar.eclipse.viewer.math.Maths;
import custom.solar.eclipse.viewer.math.Polynomial;
import custom.solar.eclipse.viewer.util.DataFileReader;
import static custom.solar.eclipse.viewer.util.LogUtil.log;
import static custom.solar.eclipse.viewer.util.LogUtil.warn;

/** 
 Given the calendar date of the eclipse (TT/UTC, with no offset from Greenwich), look up its {@link BesselianElements}.
 The Besselian Elements have been pre-calculated by others.
 This class simply reads in that data from the file system.
 
 <P>Reference: <a href='https://eclipse.gsfc.nasa.gov/SEpubs/5MCSE.html'>NASA</a>, which has kindly made 
 this data freely available.
 
  The supported range of years here is 1900-2200 inclusive.
*/
final class BesselianElementsLookup {

  /** Read in the Besselian Elements of a given eclipse, using the calendar date (UTC/TT) as its identifier. */
  BesselianElements lookup(LocalDate dateOfTheEclipse) {
    int year = dateOfTheEclipse.getYear();
    int month = dateOfTheEclipse.getMonthValue();
    int day = dateOfTheEclipse.getDayOfMonth();
    
    DataFileReader reader = new DataFileReader();
    List<String> lines = reader.readFile(this.getClass(), NASA_FILE);
    
    rawEclipseData = findLineMatching(year, month, day, lines);
    
    BesselianElements result = null;
    if (rawEclipseData == null) {
      warn("ECLIPSE NOT FOUND, given the date " + dateOfTheEclipse);
    }
    else {
      result = besselianElements();
    }
    return result;
  }

  /** Hard-coded to an example in Meeus' book (Elements of Solar Eclipses 1951-2200). Used for testing only. */
  BesselianElements lookupMeeusExample() {
    return meeus();
  }
  
  private String[] rawEclipseData;
  private static final String NASA_FILE = "nasa-besselian-elements-from-1900-to-2200.csv";
  
  /* 
  All columns in the underlying file, 5 per line:
  
  "year","month","day","td_ge","dt",
  "luna_num","saros","eclipse_type","gamma","magnitude",
  "lat_ge","lng_ge","lat_dd_ge","lng_dd_ge","sun_alt",
  "sun_azm","path_width","central_duration","duration_secs","cat_no",
  "canon_plate","julian_date","t0","x0","x1",
  
  "x2","x3","y0","y1","y2",
  "y3","d0","d1","d2","mu0"
  "mu1","mu2","l10","l11","l12",
  "l20","l21","l22","tan_f1","tan_f2",
  "tmin","tmax","etype","PNS","UNS",
  
  "NCN","nSer","nSeq","nJLE"
  */

  //These start at 1!
  private static final int YEAR = 1;
  private static final int MONTH = 2;
  private static final int DAY = 3;
  private static final int TIME_OF_DAY = 4;
  private static final int ECLIPSE_TYPE = 8;
  private static final int JULIAN_DATE = 22;
  private static final int T0 = 23;

  private static final int X0 = 24;
  private static final int X1 = 25;
  private static final int X2 = 26;
  private static final int X3 = 27;
  
  private static final int Y0 = 28;
  private static final int Y1 = 29;
  private static final int Y2 = 30;
  private static final int Y3 = 31;

  private static final int D0 = 32;
  private static final int D1 = 33;
  private static final int D2 = 34;

  private static final int MU0 = 35;
  private static final int MU1 = 36;
  private static final int MU2 = 37;

  private static final int L10 = 38;
  private static final int L11 = 39;
  private static final int L12 = 40;

  private static final int L20 = 41;
  private static final int L21 = 42;
  private static final int L22 = 43;
  
  private static final int TAN_F1 = 44;
  private static final int TAN_F2 = 45;
  
  private String[] findLineMatching(Integer year, Integer month, Integer day, List<String> lines) {
    String[] result = null;
    for(String line : lines) {
      String[] parts = line.split(Pattern.quote(","));
      if (
        parts[idx(YEAR)].equals(year.toString()) && 
        parts[idx(MONTH)].equals(month.toString()) && 
        parts[idx(DAY)].equals(day.toString())
      ) {
        result = parts;
        return result; //early return !
      }
    }
    return result; //null in this case
  }

  private Double asDouble(int columnNumber) {
    return Double.valueOf(raw(columnNumber));
  }
  
  private Integer asInteger(int columnNumber) {
    return Integer.valueOf(raw(columnNumber));
  }
  
  private Integer dropUselessDecimals(int columnNumber) {
    Double raw = asDouble(columnNumber);
    return raw.intValue();
  }

  private String raw(int columnNumber) {
    return rawEclipseData[idx(columnNumber)];
  }
  
  private String withoutQuotes(int columnNumber) {
    String raw = raw(columnNumber);
    return raw.substring(1, raw.length()-1);
  }
  
  private int idx(int columnNumber) {
    return columnNumber - 1; 
  }
  
  private BesselianElements besselianElements() {
    return new BesselianElements(
      whenMaxEclipse(), jdMaxEclipse(), eclipseType(), 
      t0(), x(), y(), d(), mu(), L1(), L2(), 
      tanF1(), tanF2() 
    );
  }

  /*
   ge: greatest eclipse
   td: dynamical time?
   dt: delta T
   dd: decimal form
   t0: the hour of the day 0..24
   Eclipse Types?: T, A, P, H, Pb, Pe, A+, H3, Hm, Tm
   "year","month","day","td_ge","dt","luna_num","saros","eclipse_type","gamma","magnitude","lat_ge","lng_ge","lat_dd_ge","lng_dd_ge","sun_alt","sun_azm","path_width","central_duration","duration_secs","cat_no","canon_plate","julian_date","t0","x0","x1","x2","x3","y0","y1","y2","y3","d0","d1","d2","mu0","mu1","mu2","l10","l11","l12","l20","l21","l22","tan_f1","tan_f2","tmin","tmax","etype","PNS","UNS","NCN","nSer","nSeq","nJLE"
   2024,4,8,"18:18:29",74.00000000,300,139,"T",.34314000,1.05655000,"25.3N","104.1W",25.28945000,-104.12775000,69.80000000,149.40000000,197.50000000,"04m28s",268.10000000,9561.00000000,479.00000000,2460409.26300000,18.00000000,-.31824400,.51171160,.00003260,-.00000842,.21976400,.27095890,-.00005950,-.00000466,7.58620000,.01484400,-.00000200,89.59122000,15.00408000,.00000000,.53581400,.00006180,-.00001280,-.01027200,.00006150,-.00001270,.00466830,.00464500,-3.00000000,3.00000000,1,0,0,0,71,30,40
   */
  
  /** Physics time of the max eclipse. */
  private LocalDateTime whenMaxEclipse() {
    int y = asInteger(YEAR);
    int m = asInteger(MONTH);
    int d = asInteger(DAY);
    LocalDate date = LocalDate.of(y, m, d);
    LocalTime time = LocalTime.parse(withoutQuotes(TIME_OF_DAY));
    return LocalDateTime.of(date, time);
  }
  
  private Integer t0() {
    return dropUselessDecimals(T0);
  }
  private Polynomial x() {
    return new Polynomial(asDouble(X0), asDouble(X1), asDouble(X2), asDouble(X3));
  }
  private Polynomial y() {
    return new Polynomial(asDouble(Y0), asDouble(Y1), asDouble(Y2), asDouble(Y3));
  }
  private Polynomial d() {
    return new Polynomial(Maths::degToRads, asDouble(D0), asDouble(D1), asDouble(D2));
  }
  private Polynomial mu() {
    return new Polynomial(Maths::degToRads, asDouble(MU0), asDouble(MU1), asDouble(MU2));
  }
  private Polynomial L1() {
    return new Polynomial(asDouble(L10), asDouble(L11), asDouble(L12));
  }
  private Polynomial L2() {
    return new Polynomial(asDouble(L20), asDouble(L21), asDouble(L22));
  }
  private Double tanF1() {
    return asDouble(TAN_F1);
  }
  private Double tanF2() {
    return asDouble(TAN_F2);
  }
  private Double jdMaxEclipse() {
    return asDouble(JULIAN_DATE);
  }
  private EclipseType eclipseType() {
    return EclipseType.parse(withoutQuotes(ECLIPSE_TYPE));
  }

  /** Informal test harness. */
  public static void main(String[] args) {
    BesselianElementsLookup lookup = new BesselianElementsLookup();
    BesselianElements eclipseElements = lookup.lookup(LocalDate.of(2024, 4, 8));
    //BesselianElements eclipseElements = lookup.lookup(LocalDate.of(1951, 3, 7));
    if (eclipseElements == null) {
      //do nothing      
    }
    else {
      log(eclipseElements);
    }
  }
  
  /** 
   WARNING: Meeus' example (page 27) seems to have a tiny error in the computation of H, of about 0.01 arcseconds:
   His stated value is -1.411188, but the computed value according to his formual should be  -1.411192.
   This is likely due to a small difference in the constant, which represents the number of degrees 360 / length of the sidereal day in seconds. 
  */ 
  private BesselianElements meeus() {
    return new BesselianElements(
        LocalDateTime.of(1994, 5, 10, 17, 0, 0),
        0.0, //not used
        EclipseType.Annular,
        17,
        new Polynomial(-0.173367, 0.4990629, 0.0000296, -0.00000563), //X
        new Polynomial(0.383484, 0.0869393, -0.0001183, -0.00000092), //Y
        new Polynomial(Maths::degToRads, 17.68613, 0.010642, -0.000004), //d
        new Polynomial(Maths::degToRads, 75.90923, 15.001621), //MU
        new Polynomial(0.566906, -0.0000318, -0.0000098), //L1
        new Polynomial(0.020679, -0.0000317, -0.0000097), //L2
        0.0046308, //tanF1 
        0.0046077 //tanF2
    );
  }
}