package edu.byu.isys413.group1a.intex2.DAOs;

import edu.byu.isys413.group1a.intex2.BOs.Membership;
import edu.byu.isys413.group1a.intex2.BOs.Product;
import edu.byu.isys413.group1a.intex2.BOs.Rental;
import edu.byu.isys413.group1a.intex2.BOs.TxLine;
import edu.byu.isys413.group1a.intex2.Misc.Cache;
import edu.byu.isys413.group1a.intex2.Misc.ConnectionPool;
import edu.byu.isys413.group1a.intex2.Misc.DataException;
import edu.byu.isys413.group1a.intex2.Misc.GUID;
import java.sql.*;
import java.util.*;

/**
 * A singleton object that CRUD's TransactionLine objects.
 *
 * @author Group 1A, isys@aaronhardy.com
 */
public class TxLineDAO  {
  
  /////////////////////////////////////////////
  ///   Singleton code
  
  private static TxLineDAO instance = null;
  
  /** Creates a new instance of TxLneDAO */
  private TxLineDAO() {
  }//constructor
  
  /** Retrieves the single instance of this class */
  public static synchronized TxLineDAO getInstance() {
    if (instance == null) {
      instance = new TxLineDAO();
    }
    return instance;
  }//getInstance
  
  ////////////////////////////////////////////
  ///   CREATE methods
  
  /** Creates a new Transaction Line in the database */
  public TxLine create() throws DataException {
    TxLine txLine = new TxLine();
    txLine.setObjectAlreadyInDB(false);
    txLine.setId(GUID.generate());
    Cache.getInstance().put(txLine.getId(), txLine);
    return txLine;
  }//create
  
  ///////////////////////////////////////
  ///   READ methods
  
  /** Reads an existing Transaction Line from the database */
  public TxLine read(String id) throws DataException {
    if (Cache.getInstance().containsKey(id)) {
      return (TxLine)Cache.getInstance().get(id);
    }
    Connection conn = ConnectionPool.getInstance().get();
    try {
      return read(id, conn);
    }catch (Exception e) {
      throw new DataException("An error occurred while reading the business object information.", e);
    }finally {
      ConnectionPool.getInstance().release(conn);
    }
  }
  
  /** Internal method to read an existing Transaction Line from the database */
  TxLine read(String id, Connection conn) throws Exception{
    if (Cache.getInstance().containsKey(id)) {
      return (TxLine)Cache.getInstance().get(id);
    }
    PreparedStatement stmt = conn.prepareStatement("SELECT * FROM txline where id=?");
    try{
      stmt.setString(1, id);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        return readRecord(rs, conn);
      }//if
      throw new DataException("TxLine with id '" + id + "' not found.");
    }finally{
      stmt.close();
    }
  }//read
  
  /** Internal method to read an existing Transaction Line from the database using and RS id */
  public TxLine readByRsid(String rsid) throws Exception{
    Connection conn = ConnectionPool.getInstance().get();
    PreparedStatement stmt = conn.prepareStatement("SELECT * FROM txline WHERE rsid=?");
    
    try{
      stmt.setString(1, rsid);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        return readRecord(rs, conn);
      }//if
      throw new DataException("TxLine with rsid '" + rsid + "' not found.");
    }catch (Exception e) {
      throw new DataException("An error occurred while reading the business object information.", e);
    }finally{
      stmt.close();
      ConnectionPool.getInstance().release(conn);
    }
  }//readByRSId
  
  /** Internal method to read an existing Transaction Line from the database using  */
  public List<TxLine> getTxLinesFromRentals(List<Rental> rentals) throws Exception{
    List<TxLine> txLineList = new ArrayList();
    Connection conn = ConnectionPool.getInstance().get();
    //Get txids from txline where they contain rental
    //call txline and return txs
    //getTxForRentals()
    PreparedStatement stmt = conn.prepareStatement("SELECT * FROM txline WHERE rsid=?");
    try{
      for (int i = 0; i < rentals.size(); i++){
        Rental rental = rentals.get(i);
        if (rental == null){
          System.out.println("Shoot me now");
        }
        
        stmt.setString(1, rental.getId());
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
          txLineList.add(readRecord(rs, conn));
        }
      }
    } catch (Exception e){
      throw new DataException("An error occurred while reading the business object information.", e);
    }finally{
      stmt.close();
      ConnectionPool.getInstance().release(conn);
    }
    return txLineList;
  }//read
  
  /** Internal method to create a Transaction Line object from a record */
  public TxLine readRecord(ResultSet rs, Connection conn) throws Exception {
//    if (Cache.getInstance().containsKey(rs.getString("id"))) {
    //return (TxLine)Cache.getInstance().get(rs.getString("id"));
    //  }
    TxLine txLine = new TxLine();
    txLine.setObjectAlreadyInDB(true);
    txLine.setId(rs.getString("id"));
    Cache.getInstance().put(txLine.getId(), txLine);
    txLine.setTx(TxDAO.getInstance().read(rs.getString("txid")));
    txLine.setSubTotal(rs.getFloat("subtotal"));
    txLine.setQuantity(rs.getInt("quantity"));
    txLine.setSerialNum(rs.getString("serialnum"));
    String serialNum = txLine.getSerialNum();
    String rsid = rs.getString("rsid");
    switch (serialNum.length()) {
      case 2: // Membership
        Membership m = MembershipDAO.getInstance().read(rsid);
        txLine.setRevenueSource(m);
        break;
        
      case 4: // Fee
        //Should not be here
        
      case 8: // Rental
        Rental r = RentalDAO.getInstance().read(rsid);
        txLine.setRevenueSource(r);
        break;
        
      case 12: // Product
        Product p = ProductDAO.getInstance().read(rsid);
        txLine.setRevenueSource(p);
        break;
    }
    return txLine;
  }//readRecord
  
  /////////////////////////////////////////////
  ///   UPDATE methods
  
  /** Saves an existing Transaction Line in the database */
  public void save(TxLine txLine) throws DataException {
    Connection conn = ConnectionPool.getInstance().get();
    try {
      save(txLine, conn);
      conn.commit();
    }catch (Exception e) {
      System.out.println(e);
      try{
        conn.rollback();
      }catch (SQLException e2) {
        throw new DataException("Could not roll back the database transaction!", e2);
      }
      throw new DataException("An error occurred while saving the business object information.", e);
    }finally {
      ConnectionPool.getInstance().release(conn);
    }
  }//update
  
  /** Internal method to update a Transaction Line in the database */
  public void save(TxLine txLine, Connection conn) throws Exception {
    Cache.getInstance().put(txLine.getId(), txLine);
    if (txLine.isObjectAlreadyInDB()) {
      update(txLine, conn);
    }else{
      insert(txLine, conn);
    }//if
  }//save
  
  /** Saves an existing Transaction Line to the database */
  private void update(TxLine txLine, Connection conn) throws Exception {
    PreparedStatement stmt = conn.prepareStatement("UPDATE txline SET txid=?, rsid=?, subtotal=?, quantity=?, serialnum=? WHERE id=?");
    try {
      stmt.setString(1, txLine.getTx() == null ? null : txLine.getTx().getId());
      stmt.setString(2, txLine.getRevenueSource() == null ? null : txLine.getRevenueSource().getId());
      stmt.setFloat(3, txLine.getSubTotal());
      stmt.setFloat(4, txLine.getQuantity());
      stmt.setString(5, txLine.getSerialNum());
      stmt.setString(6, txLine.getId());
      stmt.execute();
    }finally{
      stmt.close();
    }
  }
  
  /** Inserts a new Transaction Line into the database */
  private void insert(TxLine txLine, Connection conn) throws Exception {
    
    PreparedStatement stmt = conn.prepareStatement("INSERT INTO txline (id, txid, rsid, subtotal, quantity, serialnum) VALUES (?, ?, ?, ?, ?, ?)");
    try {
      stmt.setString(1, txLine.getId());
      stmt.setString(2, txLine.getTx() == null ? null : txLine.getTx().getId());
      stmt.setString(3, txLine.getRevenueSource() == null ? null : txLine.getRevenueSource().getId());
      stmt.setFloat(4, txLine.getSubTotal());
      stmt.setInt(5, txLine.getQuantity());
      stmt.setString(6, txLine.getSerialNum());
      stmt.execute();
      txLine.setObjectAlreadyInDB(true);
    }finally{
      stmt.close();
    }
  }
  
  /////////////////////////////////////////////////
  ///   DELETE methods
  
  /** Deletes an existing Transaction Line from the database */
  public void delete(TxLine txLine) throws DataException {
    delete(txLine.getId());
  }
  
  /** Deletes an existing Transaction Line from the database, given its id */
  public void delete(String id) throws DataException {
    Connection conn = ConnectionPool.getInstance().get();
    try {
      delete(id, conn);
      conn.commit();
    }catch (Exception e) {
      try{
        conn.rollback();
      }catch (SQLException e2) {
        throw new DataException("Could not roll back the database transaction!", e2);
      }
      throw new DataException("An error occurred while deleting the business object information.", e);
    }finally {
      ConnectionPool.getInstance().release(conn);
    }
  }
  
  /** Internal method to delete an existing Transaction Line from the database */
  void delete(String id, Connection conn) throws Exception{
    Cache.getInstance().remove(id);
    PreparedStatement stmt = conn.prepareStatement("DELETE FROM txline where id=?");
    try {
      stmt.setString(1, id);
      stmt.execute();
    }finally{
      stmt.close();
    }
  }//read
  
}//class
