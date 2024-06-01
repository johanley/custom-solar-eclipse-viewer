package custom.solar.eclipse.viewer.draw.mix;

/** Allow for changing and automatic reversing of the graphics context. */
public class DrawingContextWrapper implements DrawingContext {
  
  /** IMPORTANT: The wrappee is changed first, and reversed last. */
  public DrawingContextWrapper(DrawingContext wrappee) {
    this.wrappee = wrappee;
  }
  
  @Override public void change() {
    if (wrappee != null) wrappee.change();
  }
  
  @Override public void reverse() {
    if (wrappee != null) wrappee.reverse();
  }

  private DrawingContext wrappee;

}
