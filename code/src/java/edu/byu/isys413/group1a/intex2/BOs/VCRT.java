package edu.byu.isys413.group1a.intex2.BOs;

/**
 * A Video Category Release Type
 *
 * @author Group 1A, isys@aaronhardy.com
 */
public class VCRT extends BusinessObject {
  private float price = 0;
  private int duration = 0;
  private VideoCategory videoCategory = null;
  private ReleaseType releaseType = null;
  private float overduePrice = 0;
  
  /** Creates a new instance of VideoCategoryReleaseType */
  public VCRT() {
  }
  /** Gets price */
  public float getPrice() {
    return price;
  }
  /** Sets price */
  public void setPrice(float price) {
    this.price = price;
  }
  /** Gets duration */
  public int getDuration() {
    return duration;
  }
  /** Sets duration */
  public void setDuration(int duration) {
    this.duration = duration;
  }
  /** Gets video category */
  public VideoCategory getVideoCategory() {
    return videoCategory;
  }
  /** Sets video category */
  public void setVideoCategory(VideoCategory videoCategory) {
    this.videoCategory = videoCategory;
  }
  /** Gets release type */
  public ReleaseType getReleaseType() {
    return releaseType;
  }
  /** Sets release type */
  public void setReleaseType(ReleaseType releaseType) {
    this.releaseType = releaseType;
  }
  /** Gets overdue price */
  public float getOverduePrice() {
    return overduePrice;
  }
  /** Sets overdue price */
  public void setOverduePrice(float overduePrice) {
    this.overduePrice = overduePrice;
  }
  
}//class
