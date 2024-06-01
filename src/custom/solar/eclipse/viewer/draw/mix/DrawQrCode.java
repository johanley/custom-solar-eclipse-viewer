package custom.solar.eclipse.viewer.draw.mix;

import java.awt.Graphics2D;
import java.util.Hashtable;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import custom.solar.eclipse.viewer.config.QRCode;

/** 
 Render a QR code in the origin of the current graphics context.
 This class uses the google <code>zxing</code> library to render the QR code graphic. 
*/
public final class DrawQrCode implements Draw {
  
  public DrawQrCode(QRCode qrCode, int size){
    this.qrCode = qrCode;
    this.size = size;
  }
  
  @Override public void draw(Graphics2D g) {
    Hashtable<EncodeHintType, ErrorCorrectionLevel> hints = new Hashtable<>();
    hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
    QRCodeWriter qrCodeWriter = new QRCodeWriter();
    try {
      BitMatrix matrix = qrCodeWriter.encode(qrCode.url(), BarcodeFormat.QR_CODE, size, size, hints);
      //g.setColor(Color.BLACK);
      for (int i = 0; i < matrix.getWidth(); i++) {
        for (int j = 0; j < matrix.getWidth(); j++) {
          if (matrix.get(i, j)) {
            g.fillRect(i, j, 1, 1);
          }
        }
      }
    } 
    catch (WriterException e) {
      e.printStackTrace();
    }
  }
  
  private QRCode qrCode;
  private int size;

}
