package edu.byu.isys413.group1a.intex2.BOs;

import edu.byu.isys413.group1a.intex2.DAOs.MembershipDAO;
import edu.byu.isys413.group1a.intex2.DAOs.MembershipTypeDAO;
import edu.byu.isys413.group1a.intex2.DAOs.ProductDAO;
import edu.byu.isys413.group1a.intex2.DAOs.RentalDAO;
import edu.byu.isys413.group1a.intex2.DAOs.RentalVideoDAO;
import edu.byu.isys413.group1a.intex2.DAOs.StoreProductDAO;
import edu.byu.isys413.group1a.intex2.DAOs.TxLineDAO;
import java.util.LinkedList;
import java.util.List;
import java.util.Calendar;
import java.util.Iterator;

/**
 * A Transaction
 *
 * @author Group 1A, isys@aaronhardy.com
 */
public class Tx extends BusinessObject{
  private Customer customer = null;
  private Store store = null;
  private Payment payment = null;
  private Calendar txDate = null;
  private float tax = 0;
  private float total = 0;
  private List<TxLine> txLines = new LinkedList<TxLine>();
  private String txid = null;
  
  // balPayment not saved in Tx table, but convenient for BO
  private float balPayment = 0;
  
  
  /** Creates a new instance of Tx */
  public Tx() {
  }
  /** Sets serial Number */
  public boolean setSerialNum(String serialNum) {
    //TxLine txline = new TxLine(serialNum);
    return true;
  }
  /** Gets customer */
  public Customer getCustomer() {
    return customer;
  }
  /** Sets customer */
  public void setCustomer(Customer customer) {
    this.customer = customer;
  }
  /** Gets store */
  public Store getStore() {
    return store;
  }
  /** Sets store */
  public void setStore(Store store) {
    this.store = store;
  }
  /** Gets date */
  public Calendar getDate() {
    return getTxDate();
  }
  /** Sets date */
  public void setDate(Calendar txDate) {
    this.setTxDate(txDate);
  }
  /** Gets tax */
  public float getTax() {
    return tax;
  }
  /** Sets tax */
  public void setTax(float tax) {
    this.tax = tax;
  }
  /** Gets total */
  public float getTotal() {
    return total;
  }
  /** Sets total */
  public void setTotal(float total) {
    this.total = total;
  }
  /** Gets transaction lines */
  public List<TxLine> getTxLines() {
    return txLines;
  }
  /** Sets transaction lines */
  public void addTxLine(TxLine txLine) {
    if (!this.getTxLines().contains(txLine)) {
      this.getTxLines().add(txLine);
    }
    txLine.setTx(this);
  }
  /** Remove transaction line */
  public void removeTxLine(int num) {
    this.getTxLines().remove(num);
  }
  /** Create a new transaction line */
  public TxLine newTxLine(int quantity, String serialNum, String storeId) throws Exception{
    
    TxLine txLine = null;
    txLine = TxLineDAO.getInstance().create();
    txLine.setSerialNum(serialNum);
    int length = serialNum.length();
    RevenueSource rs = null;
    float amount = 0;
    String description = null;
    Calendar date = Calendar.getInstance();
    date.setTimeInMillis(System.currentTimeMillis());
    Calendar date2 = Calendar.getInstance();
    
    
    switch (length){
      
      case 2:
        MembershipType membershipType = MembershipTypeDAO.getInstance().read(serialNum);
        Membership membership = MembershipDAO.getInstance().create();
        membership.setMembershipType(membershipType);
        
        date2.add(date.DATE,30); //30 days for a membership
        membership.setStartDate(date);
        membership.setExpDate(date2);
        membership.setCancelDate(null);
        rs = membership;
        rs.setAmount(membershipType.getPrice());
        rs.setDescription(membershipType.getDescription());
        break;
        
      case 8:
        RentalVideo rentalVideo = RentalVideoDAO.getInstance().read(serialNum);
        Rental rental = RentalDAO.getInstance().create();
        rental.setRentalVideo(rentalVideo);
        //date.setTimeInMillis(System.currentTimeMillis());
        date2.add(date.DATE,rentalVideo.getVcrtcv().getVcrt().getDuration());
        rental.setOutDate(date);
        rental.setInDate(null);
        rental.setDueDate(date2);
        rs = rental;
        rs.setAmount(rentalVideo.getVcrtcv().getVcrt().getPrice());
        rs.setDescription(rentalVideo.getVcrtcv().getCv().getTitle());
        break;
        
      case 12:
        Product product = ProductDAO.getInstance().read(serialNum);
        
        /////////////
        // For products, we must check to see if there's enough in inventory
        
        int alreadyOnTx = 0;
        List<TxLine> prevTxLines = getTxLines();//?
        
        // First add up how many of the same product are currently on the transaction
        for (Iterator<TxLine> iter = txLines.iterator(); iter.hasNext();){
          TxLine prevTxLine = iter.next();
          if (prevTxLine.getRevenueSource().getId().equals(product.getId())){
            alreadyOnTx = alreadyOnTx + prevTxLine.getQuantity();
          }
        }
        
        // Get the quantity on hand of the product
        StoreProduct storeProduct = StoreProductDAO.getInstance().readByForeign(storeId, product.getId());
        
        // Throw error if the QOH - alreadyOnTx is less than one.
        // This means there are no more left.  Bail.
        if ((storeProduct.getQtyOnHand() - alreadyOnTx -quantity) < 0){
          throw new Exception("There is not enough quantity on hand for this product.");
        }
        
        // End quantity on hand check
        ///////////
        
        rs = product;
        rs.setAmount(product.getAmount());
        rs.setDescription(product.getDescription());
        break;
        
      default:
        break;
    }
    
    txLine.setRevenueSource(rs);
    txLine.setQuantity(quantity);
    txLine.setSerialNum(serialNum);
    addTxLine(txLine);
    
    return txLine;
  }
  /** Removes a customer */
  public void removeCustomer(TxLine txLine) {
    this.getTxLines().remove(getTxLines());
    txLine.setTx(null);
  }
  /** Gets payment */
  public Payment getPayment() {
    return payment;
  }
  /** Sets payment */
  public void setPayment(Payment payment) {
    this.payment = payment;
  }
  /** Gets transaction date */
  public Calendar getTxDate() {
    return txDate;
  }
  /** Sets transaction date */
  public void setTxDate(Calendar txDate) {
    this.txDate = txDate;
  }
  /** Sets transaction lines */
  public void setTxLines(List<TxLine> txLines) {
    this.txLines = txLines;
  }
  /** Gets transaction id */
  public String getTxid() {
    return txid;
  }
  /** Sets transaction id */
  public void setTxid(String txid) {
    this.txid = txid;
  }
  /** Gets balance payment */
  public float getBalPayment() {
    return balPayment;
  }
  /** Sets balance payment */
  public void setBalPayment(float balPayment) {
    this.balPayment = balPayment;
  }
  
}//class
