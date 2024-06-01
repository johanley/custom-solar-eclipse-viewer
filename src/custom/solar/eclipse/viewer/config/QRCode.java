package custom.solar.eclipse.viewer.config;

/** Value class for a QR code: link (URL) plus label-text. */
public final class QRCode {

  /**
   Constructor.
   
   @param label text to accompany the QR code
   @param url the web link to which the QR code connects
  */
  public QRCode(String label, String url) {
    this.label = label;
    this.url = url;
  }

  /** Separator used in the config file. */
  public static final String SEP = "|";
  
  @Override public String toString() {
    return label + SEP + url;
  }
  public String label() { return label;}
  public String url() { return url; }
  
  private String label;
  private String url;

}
