package edu.byu.isys413.group1a.intex2.DAOs;

import edu.byu.isys413.group1a.intex2.BOs.ConceptualFee;
import edu.byu.isys413.group1a.intex2.Misc.Cache;
import edu.byu.isys413.group1a.intex2.Misc.ConnectionPool;
import edu.byu.isys413.group1a.intex2.Misc.DataException;
import edu.byu.isys413.group1a.intex2.Misc.GUID;
import java.sql.*;

/**
 * A singleton object that CRUD's ConceptualFee objects.
 *
 * @author Group 1A, isys@aaronhardy.com
 */
public class ConceptualFeeDAO  {
  
  /////////////////////////////////////////////
  ///   Singleton code
  
  private static ConceptualFeeDAO instance = null;
  
  /** Creates a new instance of ConceptualFeeDAO */
  private ConceptualFeeDAO() {
  }//constructor
  
  /** Retrieves the single instance of this class */
  public static synchronized ConceptualFeeDAO getInstance() {
    if (instance == null) {
      instance = new ConceptualFeeDAO();
    }
    return instance;
  }//getInstance
  
  ////////////////////////////////////////////
  ///   CREATE methods
  
  /** Creates a new Conceptual Fee business object */
  public ConceptualFee create() throws DataException {
    ConceptualFee conceptualFee = new ConceptualFee();
    conceptualFee.setObjectAlreadyInDB(false);
    conceptualFee.setId(GUID.generate());
    Cache.getInstance().put(conceptualFee.getId(), conceptualFee);
    return conceptualFee;
  }//create
  
  ////////////////////////////////////////////
  ///   READ methods
  
  /** Reads an existing Conceptual Fee from the database */
  public ConceptualFee read(String id) throws DataException {
    if (Cache.getInstance().containsKey(id)) {
      return (ConceptualFee)Cache.getInstance().get(id);
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
  
  /** Internal method to read an existing Conceptual Fee from the database */
  synchronized ConceptualFee read(String id, Connection conn) throws Exception {
    if (Cache.getInstance().containsKey(id)) {
      return (ConceptualFee)Cache.getInstance().get(id);
    }
    
    PreparedStatement stmt;
    if (id.length()==4){  // pull by sku if sku is given
      stmt = conn.prepareStatement("SELECT * FROM conceptualfee WHERE sku=?");
    } else { // otherwise, pull by guid
      stmt = conn.prepareStatement("SELECT * FROM conceptualfee WHERE id=?");
    }
    
    try{
      stmt.setString(1, id);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        return readRecord(rs);
      }//if
      throw new DataException("Conceptual fee with id/sku '" + id + "' not found.");
    }finally{
      stmt.close();
    }
  }//read
  
  /** Internal method to create a Conceptual Fee object from a record */
  synchronized ConceptualFee readRecord(ResultSet rs) throws Exception {
    if (Cache.getInstance().containsKey(rs.getString("id"))) {
      return (ConceptualFee)Cache.getInstance().get(rs.getString("id"));
    }
    ConceptualFee conceptualFee = new ConceptualFee();
    conceptualFee.setObjectAlreadyInDB(true);
    conceptualFee.setId(rs.getString("id"));
    Cache.getInstance().put(conceptualFee.getId(), conceptualFee);
    //fee.setRental(RentalDAO.getInstance.read(rs.getString(rentId)));
    conceptualFee.setSku(rs.getString("sku"));
    conceptualFee.setDescription(rs.getString("description"));
    conceptualFee.setAmount(rs.getFloat("amount"));
    return conceptualFee;
  }//readRecord
  
  /////////////////////////////////////////////
  ///   UPDATE methods
  
  /** Saves an existing Conceptual Fee in the database */
  public void save(ConceptualFee conceptualFee) throws DataException {
    Connection conn = ConnectionPool.getInstance().get();
    try {
      save(conceptualFee, conn);
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
  
  /** Internal method to update a Conceptual Fee in the database */
  public void save(ConceptualFee conceptualFee, Connection conn) throws Exception {
    Cache.getInstance().put(conceptualFee.getId(), conceptualFee);
    if (conceptualFee.isObjectAlreadyInDB()) {
      update(conceptualFee, conn);
    }else{
      insert(conceptualFee, conn);
    }//if
    
  }//save
  
  /** Saves an existing Conceptual Fee to the database */
  private void update(ConceptualFee conceptualFee, Connection conn) throws Exception {
    PreparedStatement stmt = conn.prepareStatement("UPDATE fee SET sku=?, description=?, amount=? WHERE id=?");
    try {
      stmt.setString(1, conceptualFee.getSku());
      stmt.setString(2, conceptualFee.getDescription());
      stmt.setFloat(3, conceptualFee.getAmount());
      stmt.setString(4, conceptualFee.getId());
      stmt.execute();
    }finally{
      stmt.close();
    }
  }
  
  /** Inserts a new Conceptual Fee into the database */
  private void insert(ConceptualFee conceptualFee, Connection conn) throws Exception {
    PreparedStatement stmt = conn.prepareStatement("INSERT INTO Fee (id, sku, description, amount) VALUES (?, ?, ?, ?)");
    try {
      stmt.setString(1, conceptualFee.getId());
      stmt.setString(2, conceptualFee.getSku());
      stmt.setString(3, conceptualFee.getDescription());
      stmt.setFloat(4, conceptualFee.getAmount());
      stmt.execute();
      conceptualFee.setObjectAlreadyInDB(true);
    }finally{
      stmt.close();
    }
  }
  
  /////////////////////////////////////////////////
  ///   DELETE methods
  
  /** Deletes an existing Conceptual Fee from the database */
  public void delete(ConceptualFee conceptualFee) throws DataException {
    delete(conceptualFee.getId());
  }
  
  /** Deletes an existing Conceptual Fee from the database, given its id */
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
  
  /** Internal method to delete an existing Conceptual Fee from the database */
  void delete(String id, Connection conn) throws Exception{
    Cache.getInstance().remove(id);
    PreparedStatement stmt = conn.prepareStatement("DELETE FROM conceptualfee where id=?");
    try {
      stmt.setString(1, id);
      stmt.execute();
    }finally{
      stmt.close();
    }
  }//read
  
}