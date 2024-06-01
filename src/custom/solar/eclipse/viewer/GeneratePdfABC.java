 package custom.solar.eclipse.viewer;

import static custom.solar.eclipse.viewer.config.Constants.AUTHOR;
import static custom.solar.eclipse.viewer.config.Constants.MARGIN_BOTTOM;
import static custom.solar.eclipse.viewer.config.Constants.MARGIN_LEFT;
import static custom.solar.eclipse.viewer.config.Constants.MARGIN_RIGHT;
import static custom.solar.eclipse.viewer.config.Constants.PDF_VERSION;
import static custom.solar.eclipse.viewer.util.LogUtil.log;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.Set;
import java.util.TreeSet;

import com.itextpdf.awt.PdfGraphics2D;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

import custom.solar.eclipse.viewer.config.Config;
import custom.solar.eclipse.viewer.config.Constants;
import custom.solar.eclipse.viewer.math.Maths;

/** 
 Abstract base class for generating a two-page PDF.
 Uses a template method design.

 <P>This project creates PDF content using two distinct techniques.
 One is by drawing to the graphics context.
 The other (used for the timeline table) uses a higher level API that adds Paragraph objects to a Document object.
*/
public abstract class GeneratePdfABC {

  protected GeneratePdfABC(Config config){
    this.config = config;
  }
  
  /** 
   Build a two-page PDF file/stream from scratch. 
   When printing, print as two-sided, to output the viewer on a single page of heavy card stock paper.
   
   <P>This method calls two template methods to create the page content. 
  */
  public void outputTo(OutputStream outputStream) throws DocumentException, MalformedURLException, IOException {
    openTheDocument(outputStream, config.fontDir());
    addMetadataToTheDocument();

    initGraphicsContext();
    pageOne(g);
    disposeGraphicsContext();

    startNewPage();
    
    initGraphicsContext();
    pageTwo(g);
    disposeGraphicsContext();
    
    closeTheDocument();
  }
  
  /** Create the first page of the PDF. Template method. */
  protected abstract void pageOne(Graphics2D g) throws DocumentException, MalformedURLException, IOException;
  
  /** Create the second page of the PDF. Template method. */
  protected abstract void pageTwo(Graphics2D g) throws DocumentException, MalformedURLException, IOException;

  // PRIVATE

  protected Config config;
  
  protected Document document;  
  private PdfWriter writer;
  private PdfContentByte contentByte;
  private PdfTemplate template;
  private Graphics2D g;
  
  /**
   Read in settings.
   Set page size, margins, register fonts, etc.
   Fonts need to be in the system's hard drive somewhere.
   The font is not attached to the Document as a whole; it's attached to lower level items. 
  */
  private void openTheDocument(OutputStream outputStream, String fontDir) throws FileNotFoundException, DocumentException {
    log("Open the doc. Initial setup of pdf Document. Setting page size, margins. Reading in fonts.");
    
    embedFonts();
    registerAllFontsIn(fontDir, false);
    
    document = new Document();
    Rectangle rect = new Rectangle(config.width(), config.height());
    document.setPageSize(rect);
    document.setMargins(MARGIN_LEFT, MARGIN_RIGHT, MARGIN_BOTTOM, MARGIN_BOTTOM);

    //should this be passed an encoding, I wonder?
    writer = PdfWriter.getInstance(document, outputStream);
    writer.setPdfVersion(PDF_VERSION); 
    writer.setViewerPreferences(PdfWriter.PageLayoutSinglePage);
    document.open(); //need to call this early!
  }
  
  private void embedFonts() {
    FontFactory.defaultEmbedding = true;
  }
  
  private void registerAllFontsIn(String fontDir, boolean log) {
    log("Registering all fonts in " + fontDir);
    FontFactory.registerDirectory(fontDir);
    if (log) {
      Set<String> fonts = new TreeSet<String>(FontFactory.getRegisteredFonts());
      for (String fontname : fonts) {
          log(fontname);
      } 
    }
  }
  
  private void addMetadataToTheDocument() {
    log("Adding metadata to the PDF.");
    document.addAuthor(AUTHOR); 
    document.addTitle("Eclipse Viewer");
    document.addSubject("Custom solar eclipse viewer.");
    document.addKeywords(
      config.location() + " year:" + config.eclipseDateUTC() + " latitude:" + Maths.radsToDegreeString(config.latitude()) + 
      " longitude:" + Maths.radsToDegreeString(config.longitude()) + 
      " hours from UT:" + config.hoursOffsetFromUT() 
    );
  }

  private void initGraphicsContext() {
    log("Fresh contentByte, template, and graphics context.");
    contentByte = writer.getDirectContent();
    template = contentByte.createTemplate(config.width(), config.height());
    g = new PdfGraphics2D(template, config.width(), config.height(), new MyFontMapper());
    
    BasicStroke thinStroke = new BasicStroke(Constants.STROKE_WIDTH_DEFAULT);
    g.setStroke(thinStroke);
    g.setFont(Constants.baseFont());
    log("Graphics font: " + g.getFont().getFontName());
    
    //rendering hints 
    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    //g = template.createGraphics(PdfConfig.WIDTH, PdfConfig.HEIGHT, new DefaultFontMapper()); //watch out! : deprecated!
  }
  
  /** You need to call this to actually draw the items to the page. */
  private void disposeGraphicsContext() {
    log("Flushing graphics.");
    g.dispose();
    contentByte.addTemplate(template, 0, 0); // x,y positioning of graphics in PDF page; yes, AFTER the disposal
  }
 
  private void startNewPage() {
    log("Starting a new page.");
    document.newPage();
  }
  
  private void closeTheDocument() {
    log("Closing the doc.");
    document.close(); 
  }
}