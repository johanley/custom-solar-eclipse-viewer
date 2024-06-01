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
    drawers.add(new Title(config, eclipse.eclipseType(), eclipse.maxEclipse().when()));
    drawers.add(new LocationEtc(config, eclipse, false));
    drawers.add(new FooterFinePrint(config, eclipse.magnitude()));
    drawers.add(new QRLink(config, config.qrCode1(), 0.46, 0.25));
    drawers.add(new RulerSeparation(config));
    drawers.add(new RulerPositionAngle(config));
    drawThe(drawers, g);
    
    //the table isn't drawn using the graphics context; it's added directly to the document object itself.
    TimelineTable timeline = new TimelineTable(eclipse.eclipseType(), eclipse.timelineEvents(), document);
    timeline.draw();

    //Remove cruft settings from config.ini
    
    //Vertical centering in the table cells.
    
    //ORDER FROM THOUSAND OAKS. ASK THEM FOR GLUING ADVICE.
    //PRINT OFF SKINNERS POND AGAIN.
    //EXPERIMENT WITH HOLE PUNCHES.
    //CHECK IN TO GITHUB.
    //SEND A CARD TO AUSTRALIA.
    
    //batch mode
    
    //Should I put the ylevel values in the ctors?
    
    //average % clouds, average daytime high temperature
    
    // "Objects aligned to the axis of the Sun’s crescent will cast sharper shadows." - https://www.planetary.org/articles/eclipse-2024-checklist
    //  chromosphere - red arc along the Moon's edge; might be visible for a few seconds
    //shadow bands usually 30s, but maybe 2min
    //pictures and descriptions?
    //table for alternate locations?
    /*
     * Thousand Oaks is a good source for filter material:
     * https://thousandoaksoptical.com/products/eclipse/
     */

  }
  
  @Override protected void pageTwo(Graphics2D g) throws DocumentException, MalformedURLException, IOException {
    log("Building page 2.");
    Set<Draw> drawers = new LinkedHashSet<>(); //iteration will mirror insertion-order
    drawers.add(new Border(config, BORDER_WIDTH));
    drawers.add(new Title(config, eclipse.eclipseType(), eclipse.maxEclipse().when()));
    drawers.add(new Holes(config, true, HOLE_RADIUS));
    drawers.add(new LocationEtc(config, eclipse, true));
    drawers.add(new PartialPhasesChart(config, eclipse));
    drawers.add(new QRLink(config, config.qrCode2(), 0.50, 0.76));
    if (EclipseType.Total == eclipse.eclipseType()) {
      drawers.add(new TotalityAdvice(config));
    }
    drawers.add(new ProducedBy(config));
    drawThe(drawers, g);
  }
  
  private EclipseDisplay eclipse;
  
  private static final float BORDER_WIDTH = 2.0F;
  private static final Double HOLE_RADIUS = 4.0;

  private void drawThe(Collection<Draw> drawers, Graphics2D g) {
    for(Draw drawer : drawers) {
      drawer.draw(g);
    }
  }
  
  private EclipseDisplay eclipse(Config config) {
    return LocalCircumstances.buildFrom(config, LocalCircumstances.ShowLogging.Yes);
  }
}
