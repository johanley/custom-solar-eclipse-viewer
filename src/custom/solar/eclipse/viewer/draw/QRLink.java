package custom.solar.eclipse.viewer.draw;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import custom.solar.eclipse.viewer.config.Config;
import custom.solar.eclipse.viewer.config.QRCode;
import custom.solar.eclipse.viewer.draw.mix.ChangeCoordsCenterText;
import custom.solar.eclipse.viewer.draw.mix.ChangeCoordsTranslate;
import custom.solar.eclipse.viewer.draw.mix.ChangeFontSize;
import custom.solar.eclipse.viewer.draw.mix.Draw;
import custom.solar.eclipse.viewer.draw.mix.DrawQrCode;
import custom.solar.eclipse.viewer.draw.mix.DrawText;
import custom.solar.eclipse.viewer.draw.mix.DrawingContext;

/** QR code linking to configured web sites. */
final class QRLink implements Draw {

  /** 
   Constructor.
   @param xFrac fraction of the viewer width from the center. 
   @param yFrac fraction of the viewer height. 
  */
  QRLink(Config config, QRCode qrCode, double xFrac, double yFrac){
    this.qrCode = qrCode;
    this.where = new Point2D.Double(
      config.width() * 0.5  - config.viewerWidth() * xFrac, 
      config.viewerHeight() * yFrac + config.viewerTopMargin()
    );
  }

  @Override public void draw(Graphics2D g) {
    render(qrCode, SIZE, g);
    renderLabel(qrCode, g);
  }
  
  private QRCode qrCode;
  private Point2D.Double where;
  private static final int SIZE = 90;
  
  private void render(QRCode qrCode, int size, Graphics2D g) {
    DrawingContext context = new ChangeCoordsTranslate(g, where);

    Draw drawer = new DrawQrCode(qrCode, size);
    drawer.drawIn(context, g);
  }
  
  private void renderLabel(QRCode qrCode, Graphics2D g) {
    Point2D.Double whereLabel = new Point2D.Double(where.x + SIZE * 0.5, where.y + SIZE * 0.95);
    DrawingContext context = new ChangeFontSize(g, 0.75f);
    context = ChangeCoordsTranslate.chain(context, g, whereLabel);
    context = ChangeCoordsCenterText.chain(context, g, qrCode.label());
    
    Draw drawer = new DrawText(qrCode.label());
    drawer.drawIn(context, g);
  }
}
