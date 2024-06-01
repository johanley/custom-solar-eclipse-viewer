package custom.solar.eclipse.viewer.config;

import java.awt.Font;

import com.itextpdf.text.pdf.PdfWriter;

/** Various constants that aren't configurable. */
public final class Constants {

  public static final String FONT_NAME = "Times New Roman";

  public static final String NL = System.getProperty("line.separator");

  /** File name for the output PDF file - {@value}.  */
  public static final String OUTPUT_PDF_FILE = "EclipseViewer.pdf";
  
  /** Name of the document's creator. */
  public static final String AUTHOR = "John O'Hanley";
  
  public static final char PDF_VERSION = PdfWriter.VERSION_1_3;
  public static final float MARGIN_LEFT = pointsFromIn(0.75f);
  public static final float MARGIN_RIGHT = pointsFromIn(0.75f);
  public static final float MARGIN_TOP = pointsFromIn(0.75f);
  public static final float MARGIN_BOTTOM = pointsFromIn(0.5f);
  public static final float FONT_SIZE_NORMAL = 12F;
  
  public static final int POINTS_PER_INCH = 72; //the itext default is 72

  /**
   Width of a stroke. 
   Setting stroke-width to 0 forces the minimum width.
   I've seen printers in which a 0-setting prints really poorly; perhaps best to avoid. 
  */
  public static final float STROKE_WIDTH_DEFAULT = 0.25f;

  public static Font baseFont() {
    return new Font(Constants.BASE_FONT_NAME, Font.PLAIN, Constants.BASE_FONT_SIZE);
    //return FontFactory.getFont(BASE_FONT_NAME, BaseFont.IDENTITY_H, BASE_FONT_SIZE, com.itextpdf.text.Font.NORMAL);
  }

  /**
   The font used for the PDF.   
   WARNING: Greek letters don't render in all fonts.
   Please see {@link mag5.book.MyFontMapper} as well.
  */
  public static final String BASE_FONT_NAME = FONT_NAME;
  public static final int BASE_FONT_SIZE = 12; //8 for planisphere
  
  private static float pointsFromIn(double inches) {
    return (float) inches * POINTS_PER_INCH;
  }

  
}