package custom.solar.eclipse.viewer.draw;

import static custom.solar.eclipse.viewer.util.LogUtil.log;

import java.awt.Graphics2D;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import com.itextpdf.text.DocumentException;

import custom.solar.eclipse.viewer.GeneratePdfABC;
import custom.solar.eclipse.viewer.astrocalc.EclipseDisplay;
import custom.solar.eclipse.viewer.astrocalc.EclipseType;
import custom.solar.eclipse.viewer.astrocalc.LocalCircumstances;
import custom.solar.eclipse.viewer.config.Config;
import custom.solar.eclipse.viewer.draw.mix.Draw;

/** Build the PDF file for the viewer. */
public final class GenerateViewer extends GeneratePdfABC {
  
  public GenerateViewer(Config config) {
    super(config);
    this.eclipse = eclipse(config);
  }

  @Override protected void pageOne(Graphics2D g) throws DocumentException, MalformedURLException, IOException {
    log("Building page 1.");
    
    Set<Draw> drawers = new LinkedHashSet<>(); //iteration will mirror insertion-order
    drawers.add(new Border(config, BORDER_WIDTH));
    drawers.add(new Holes(config, false, HOLE_RADIUS));
    drawers.add(new Title(config, eclipse.eclipseType(), eclipse.maxEclipse().when(), Y_LEVEL.TITLE));
    drawers.add(new LocationEtc(config, eclipse, false, Y_LEVEL.LOCATION_ETC));
    drawers.add(new QRLink(config, config.qrCode1(), 0.46, Y_LEVEL.QR_1));
    drawers.add(new FooterFinePrint(config, eclipse.magnitude(), Y_LEVEL.FOOTER_FINE_PRINT));
    drawers.add(new RulerSeparation(config));
    drawers.add(new RulerPositionAngle(config));
    drawThe(drawers, g);
    
    //the table isn't drawn using the graphics context; it's added directly to the document object itself.
    TimelineTable timeline = new TimelineTable(eclipse.eclipseType(), eclipse.timelineEvents(), document);
    timeline.draw();
  }
  
  @Override protected void pageTwo(Graphics2D g) throws DocumentException, MalformedURLException, IOException {
    log("Building page 2.");
    Set<Draw> drawers = new LinkedHashSet<>(); //iteration will mirror insertion-order
    drawers.add(new Border(config, BORDER_WIDTH));
    drawers.add(new Title(config, eclipse.eclipseType(), eclipse.maxEclipse().when(), Y_LEVEL.TITLE - 0.01));
    drawers.add(new Holes(config, true, HOLE_RADIUS));
    drawers.add(new LocationEtc(config, eclipse, true, Y_LEVEL.LOCATION_ETC));
    drawers.add(new PartialPhasesChart(config, eclipse, Y_LEVEL.PARTIAL_PHASES));
    drawers.add(new QRLink(config, config.qrCode2(), 0.50, Y_LEVEL.QR_2));
    if (EclipseType.Total == eclipse.eclipseType()) {
      drawers.add(new TotalityAdvice(config, Y_LEVEL.TOTALITY_ADVICE));
    }
    drawers.add(new ProducedBy(config, Y_LEVEL.PRODUCED_BY));
    drawThe(drawers, g);
  }
  
  private EclipseDisplay eclipse;
  
  private static final float BORDER_WIDTH = 2.0F;
  private static final Double HOLE_RADIUS = 4.0;
  
  /** This class just groups together similar things. */
  private static final class Y_LEVEL {
    static double TITLE = 0.06;
    static double LOCATION_ETC = 0.30;
    static double QR_1 = 0.27;
    static double QR_2 = 0.76;
    static double FOOTER_FINE_PRINT = 0.96;
    static double PRODUCED_BY = 0.96;
    static double TOTALITY_ADVICE = 0.80;
    static double PARTIAL_PHASES = 0.5;
  }
  
  private void drawThe(Collection<Draw> drawers, Graphics2D g) {
    for(Draw drawer : drawers) {
      drawer.draw(g);
    }
  }
  
  private EclipseDisplay eclipse(Config config) {
    return LocalCircumstances.buildFrom(config, LocalCircumstances.ShowLogging.Yes);
  }
}
