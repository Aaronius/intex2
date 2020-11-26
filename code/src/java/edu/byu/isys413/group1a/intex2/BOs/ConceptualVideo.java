package edu.byu.isys413.group1a.intex2.BOs;

/**
 * A conceptual video with a title
 *
 * @author Group 1A, isys@aaronhardy.com
 */
public class ConceptualVideo extends BusinessObject {
  private String title = null;
  
  /** Creates a new instance of ConceptualVideo */
  public ConceptualVideo() {
  }
  /** Gets the title of the conceptual video */
  public String getTitle() {
    return title;
  }
  /** Sets the title of the conceptual video */
  public void setTitle(String title) {
    this.title = title;
  }
  
}//class