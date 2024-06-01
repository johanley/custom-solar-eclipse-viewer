package custom.solar.eclipse.viewer.draw.mix;

/** Temporarily change the graphics context, and then later reverse out the change. */
public interface DrawingContext {
  
  void change();
  
  void reverse();

}
