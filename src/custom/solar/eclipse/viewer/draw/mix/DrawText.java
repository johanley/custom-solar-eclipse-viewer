package custom.solar.eclipse.viewer.draw.mix;

import java.awt.Graphics2D;

/** Write text using the current graphics context, at the origin. */
public final class DrawText implements Draw {
  
  public DrawText(String text){
    this.text = text;
  }
  
  @Override public void draw(Graphics2D g) {
    g.drawString(text, 0, 0);
  }
  
  private String text;
}
