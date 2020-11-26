package edu.byu.isys413.group1a.intex2.BOs;
import edu.byu.isys413.group1a.intex2.DAOs.RentalVideoDAO;
import java.util.Calendar;

/**
 * A Rental
 *
 * @author Group 1A, isys@aaronhardy.com
 */
public class Rental extends RevenueSource {
  private float amount = 0;
  private String description = null;
  private RentalVideo rentalVideo = null;
  private Calendar outDate = null;
  private Calendar dueDate = null;
  private Calendar inDate = null;
  
  /** Creates a new instance of Rental */
  public Rental(){
  }
  /** Creates a new instance of Rental */
  public Rental(String serialNum){
    RentalVideo rv = null;
    try{
      rv = RentalVideoDAO.getInstance().read(serialNum);
    }catch(Exception e){}
    this.setRentalVideo(rv);
  }
  /** Gets amount */
  public float getAmount(){
    return amount;
  }
  /** Sets amount */
  public void setAmount(float amount){
    this.amount = amount;
  }
  /** Gets description */
  public String getDescription(){
    return description;
  }
  /** Sets description */
  public void setDescription(String description){
    this.description = description;
  }
  /** Gets rental video */
  public RentalVideo getRentalVideo() {
    return rentalVideo;
  }
  /** Sets rental video */
  public void setRentalVideo(RentalVideo rentalVideo) {
    this.rentalVideo = rentalVideo;
  }
  /** Gets out date */
  public Calendar getOutDate() {
    return outDate;
  }
  /** Sets out date */
  public void setOutDate(Calendar outDate) {
    this.outDate = outDate;
  }
  /** Gets due date */
  public Calendar getDueDate() {
    return dueDate;
  }
  /** Sets due date */
  public void setDueDate(Calendar dueDate) {
    this.dueDate = dueDate;
  }
  /** Gets in date */
  public Calendar getInDate() {
    return inDate;
  }
  /** Sets in date */
  public void setInDate(Calendar inDate) {
    this.inDate = inDate;
  }
  
}//class