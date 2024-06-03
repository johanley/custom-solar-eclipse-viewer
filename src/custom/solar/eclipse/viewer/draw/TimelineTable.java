package custom.solar.eclipse.viewer.draw;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

import custom.solar.eclipse.viewer.astrocalc.EclipseType;
import custom.solar.eclipse.viewer.config.Constants;
import custom.solar.eclipse.viewer.math.Maths;

/** Timeline for eclipse milestones. */
final class TimelineTable {
  
  TimelineTable(EclipseType eclipseType, List<TimelineEvent> timelineEvents, Document document) {
    this.eclipseType = eclipseType;
    this.timelineEvents = timelineEvents;
    this.document = document;
  }
  
  /** 
   Table in the middle of the front of the card.
   The implementation of this is different from other items.
   Instead of drawing to a graphics context, the more standard iText API techniques are used
   to add parts to a Document object. 
  */
  void draw() throws DocumentException {
    emptyLines(NUM_EMPTY_LINES);
    tableFor(timelineEvents);
  }

  private List<TimelineEvent> timelineEvents;
  private Document document;
  private EclipseType eclipseType;
  
  private static final int NUM_EMPTY_LINES = 17;
  private static final int PERCENTAGE_WIDTH = 68;
  private static final float FONT_SIZE = 9.0F;
  
  private static final List<String> COLUMN_NAMES = Arrays.asList("-/+ Tot.", "Time", "Mag.", "Comment");
  private static final float[] RELATIVE_COL_WIDTHS = {1.0f, 1.0f, 1.0f, 5.0f};
  
  private static final float[] RELATIVE_COL_WIDTHS_PARTIAL = {1.0f, 1.0f, 1.0f, 1.0f, 5.0f};
  private static final List<String> COLUMN_NAMES_PARTIAL = Arrays.asList("-/+ Max", "Time", "Mag.", "Alt.", "Comment");

  private List<String> columnNames(){
    return EclipseType.Total == eclipseType ? COLUMN_NAMES : COLUMN_NAMES_PARTIAL;
  }
  
  private float[] relativeColWidths() {
    return EclipseType.Total == eclipseType ? RELATIVE_COL_WIDTHS : RELATIVE_COL_WIDTHS_PARTIAL;
  }
  
  private void tableFor(List<TimelineEvent> events) throws DocumentException {
    PdfPTable table = new PdfPTable(columnNames().size());
    table.setWidthPercentage(PERCENTAGE_WIDTH);
    table.setHorizontalAlignment(Element.ALIGN_CENTER);
    table.setWidths(relativeColWidths());
    
    for(String columnName : columnNames()) {
      int align = (columnName.equals("Comment") ? Element.ALIGN_LEFT : Element.ALIGN_CENTER);
      addHeaderCell(table, columnName, align); 
    }
    table.setHeaderRows(1);
    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("hh:mm:ss");
    DecimalFormat magFormat = new DecimalFormat("0.000");
    for(TimelineEvent event : events) {
      addRowCell(table, Maths.hhmmss(event.plusMinus()), Element.ALIGN_CENTER);
      addRowCell(table, event.when().format(dateFormat), Element.ALIGN_CENTER);
      addRowCell(table, magFormat.format(Maths.roundToThreePlaces(event.magnitude())), Element.ALIGN_CENTER);
      if (EclipseType.Total != eclipseType) {
        addRowCell(table, event.altitude().toString() + "°", Element.ALIGN_CENTER);
      }
      addRowCell(table, event.text(), Element.ALIGN_LEFT);
    }
    
    document.add(table);
  }

  private void addHeaderCell(PdfPTable table, String text, int alignment) {
    Chunk chunk = new Chunk(text, normalFont());
    GrayColor grey = new GrayColor(0.8f);
    addChunk(table, chunk, grey, alignment);
  }
  
  private void addRowCell(PdfPTable table, String string, int alignment) {
    Chunk chunk = new Chunk(string, normalFont());
    addChunk(table, chunk, null, alignment);
  }

  private void addChunk(PdfPTable table, Chunk chunk, BaseColor baseColor, int alignment) {
    Phrase phrase = new Phrase(chunk);
    PdfPCell cell = new PdfPCell(phrase);
    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    cell.setHorizontalAlignment(alignment);
    //cell.setLeading(0f, 1.1f); // 0 + 1.5 times the font height
    //cell.setPaddingBottom(1);
    //cell.setPaddingLeft(1);
    if (baseColor != null) {
      cell.setBackgroundColor(baseColor);
    }
    table.addCell(cell);
  }
  
  private com.itextpdf.text.Font normalFont() {
    //WARNING: I added BaseFont.IDENTITY_H to make Greek letters appear; otherwise nothing showed
    /*
     * https://stackoverflow.com/questions/3858423/itext-pdf-greek-letters-are-not-appearing-in-the-resulting-pdf-documents
     * https://itextpdf.com/en/resources/faq/technical-support/itext-5-legacy/how-print-mathematical-characters
     */
    return FontFactory.getFont(Constants.FONT_NAME, BaseFont.IDENTITY_H, FONT_SIZE, com.itextpdf.text.Font.NORMAL);
  }

  /** Used only to control the vertical placement of the table on the page. */
  private void emptyLines(int num) throws DocumentException {
    Chunk chunk = new Chunk(someEmptyLines(num), normalFont());
    Paragraph space = new Paragraph();
    space.add(chunk);
    document.add(space);
  }
  
  /** 
  WARNING: using Paragraph.setSpacingBefore/After injects Helvetica (unembedded) references in the the document!
  Those references cause rejection by lulu.com, because it requires all fonts to be embedded. 
  Hence this method, which just puts empty lines into a para, instead of calling the setSpacingXXX methods.
  */
  private String someEmptyLines(int n) {
    StringBuilder result = new StringBuilder();
    for (int idx = 0; idx < n; ++idx) {
      result.append(Constants.NL);
    }
    return result.toString();
  }
}
