package edu.byu.isys413.group1a.intex2.BOs;

/**
 * A Video Category
 *
 * @author Group 1A, isys@aaronhardy.com
 */
public class VideoCategory extends BusinessObject {
  private String category = null;
  
  /** Creates a new instance of VideoCategory */
  public VideoCategory() {
  }
  /** Gets category */
  public String getCategory() {
    return category;
  }
  /** Sets category */
  public void setCategory(String category) {
    this.category = category;
  }
  
}
