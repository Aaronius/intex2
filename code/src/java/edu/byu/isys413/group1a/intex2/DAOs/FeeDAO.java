package edu.byu.isys413.group1a.intex2.DAOs;

import edu.byu.isys413.group1a.intex2.BOs.Fee;
import edu.byu.isys413.group1a.intex2.Misc.Cache;
import edu.byu.isys413.group1a.intex2.Misc.ConnectionPool;
import edu.byu.isys413.group1a.intex2.Misc.DataException;
import edu.byu.isys413.group1a.intex2.Misc.GUID;
import java.sql.*;

/**
 * A singleton object that CRUD's Fee objects.
 *
 * @author Group 1A, isys@aaronhardy.com
 */
public class FeeDAO  {
  
  /////////////////////////////////////////////
  ///   Singleton code
  
  private static FeeDAO instance = null;
  
  /** Creates a new instance of FeeDAO */
  private FeeDAO() {
  }//constructor
  
  /** Retrieves the single instance of this class */
  public static synchronized FeeDAO getInstance() {
    if (instance == null) {
      instance = new FeeDAO();
    }
    return instance;
  }//getInstance
  
  ////////////////////////////////////////////
  ///   CREATE methods
  
  /** Creates a new Fee in the database */
  public Fee create() throws DataException {
    Fee fee = new Fee();
    fee.setObjectAlreadyInDB(false);
    fee.setId(GUID.generate());
    Cache.getInstance().put(fee.getId(), fee);
    return fee;
  }//create
  
  ////////////////////////////////////////////
  ///   READ methods
  
  /** Reads an existing Fee from the database */
  public Fee read(String id) throws DataException {
    if (Cache.getInstance().containsKey(id)) {
      return (Fee)Cache.getInstance().get(id);
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
  
  /** Internal method to read an existing Fee from the database */
  synchronized Fee read(String id, Connection conn) throws Exception {
    if (Cache.getInstance().containsKey(id)) {
      return (Fee)Cache.getInstance().get(id);
    }
    
    PreparedStatement stmt = conn.prepareStatement("SELECT * FROM fee WHERE id=?");
    
    try{
      stmt.setString(1, id);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        return readRecord(rs);
      }//if
      throw new DataException("Fee with id '" + id + "' not found.");
    }finally{
      stmt.close();
    }
  }//read
  
  /** Internal method to create a Fee object from a record */
  synchronized Fee readRecord(ResultSet rs) throws Exception {
    if (Cache.getInstance().containsKey(rs.getString("id"))) {
      return (Fee)Cache.getInstance().get(rs.getString("id"));
    }
    Fee fee = new Fee();
    fee.setObjectAlreadyInDB(true);
    fee.setId(rs.getString("id"));
    Cache.getInstance().put(fee.getId(), fee);
    fee.setConceptualFee(ConceptualFeeDAO.getInstance().read(rs.getString("conceptualfeeid")));
    fee.setRental(RentalDAO.getInstance().read(rs.getString("rentalid")));
    fee.setDescription(fee.getConceptualFee().getDescription());
    fee.setAmount(fee.getConceptualFee().getAmount());
    return fee;
  }//readRecord
  
  /////////////////////////////////////////////
  ///   UPDATE methods
  
  /** Saves an existing Fee in the database */
  public void save(Fee fee) throws DataException {
    Connection conn = ConnectionPool.getInstance().get();
    try {
      save(fee, conn);
      conn.commit();
    }catch (Exception e) {
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
  
  /** Internal method to update a Fee in the database */
  public void save(Fee fee, Connection conn) throws Exception {
    Cache.getInstance().put(fee.getId(), fee);
    if (fee.isObjectAlreadyInDB()) {
      update(fee, conn);
    }else{
      insert(fee, conn);
    }//if
    
  }//save
  
  /** Saves an existing Fee to the database */
  private void update(Fee fee, Connection conn) throws Exception {
    PreparedStatement stmt = conn.prepareStatement("UPDATE fee SET conceptualfeeid=?, rentalid=?, quantity=? WHERE id=?");
    try {
      stmt.setString(1, fee.getConceptualFee().getId());
      stmt.setString(2, fee.getRental().getId());
      stmt.setInt(3, fee.getQuantity());
      stmt.setString(4, fee.getId());
      stmt.execute();
    }finally{
      stmt.close();
    }
  }
  
  /** Inserts a new Fee into the database */
  private void insert(Fee fee, Connection conn) throws Exception {
    PreparedStatement stmt = conn.prepareStatement("INSERT INTO fee (id, conceptualfeeid, rentalid, quantity) VALUES (?, ?, ?, ?)");
    try {
      stmt.setString(1, fee.getId());
      stmt.setString(2, fee.getConceptualFee() == null ? null : fee.getConceptualFee().getId());
      stmt.setString(3, fee.getRental() == null ? null : fee.getRental().getId());
      stmt.setInt(4, fee.getQuantity());
      stmt.execute();
      fee.setObjectAlreadyInDB(true);
    }finally{
      stmt.close();
    }
  }
  
  /////////////////////////////////////////////////
  ///   DELETE methods
  
  /** Deletes an existing Fee from the database */
  public void delete(Fee fee) throws DataException {
    delete(fee.getId());
  }
  
  /** Deletes an existing Fee from the database, given its id */
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
  
  /** Internal method to delete an existing Fee from the database */
  void delete(String id, Connection conn) throws Exception{
    Cache.getInstance().remove(id);
    PreparedStatement stmt = conn.prepareStatement("DELETE FROM Fee where id=?");
    try {
      stmt.setString(1, id);
      stmt.execute();
    }finally{
      stmt.close();
    }
  }//read
  
}