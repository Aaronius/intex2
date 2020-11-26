package edu.byu.isys413.group1a.intex2.DAOs;

import edu.byu.isys413.group1a.intex2.BOs.SaleVideo;
import edu.byu.isys413.group1a.intex2.Misc.Cache;
import edu.byu.isys413.group1a.intex2.Misc.ConnectionPool;
import edu.byu.isys413.group1a.intex2.Misc.DataException;
import edu.byu.isys413.group1a.intex2.Misc.GUID;
import java.sql.*;

/**
 * A singleton object that CRUD's VideoCategoryReleaseType objects.
 *
 * @@author Group 1A, isys@aaronhardy.com
 */
public class SaleVideoDAO  {
  
  /////////////////////////////////////////////
  ///   A singleton object
  
  private static SaleVideoDAO instance = null;
  
  /** Creates a new instance of SaleVideoDAO */
  private SaleVideoDAO() {
  }//constructor
  
  /** Retrieves the single instance of this class */
  public static synchronized SaleVideoDAO getInstance() {
    if (instance == null) {
      instance = new SaleVideoDAO();
    }
    return instance;
  }//getInstance
  
  ////////////////////////////////////////////
  ///   CREATE methods
  
  /** Creates a new Sale Video in the database */
  public SaleVideo create() throws DataException {
    SaleVideo sv = new SaleVideo();
    sv.setObjectAlreadyInDB(false);
    sv.setId(GUID.generate());
    Cache.getInstance().put(sv.getId(), sv);
    return sv;
  }//create
  
  ////////////////////////////////////////////
  ///   READ methods
  
  /** Reads an existing Sale Video from the database */
  public SaleVideo read(String id) throws DataException {
    if (Cache.getInstance().containsKey(id)) {
      return (SaleVideo)Cache.getInstance().get(id);
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
  
  /** Internal method to read an existing Sale Video from the database */
  synchronized SaleVideo read(String id, Connection conn) throws Exception {
    if (Cache.getInstance().containsKey(id)) {
      return (SaleVideo)Cache.getInstance().get(id);
    }
    
    PreparedStatement stmt;
    if (id.length() == 12){
      stmt = conn.prepareStatement("SELECT * FROM salevideo WHERE sku=?");
    } else {
      stmt = conn.prepareStatement("SELECT * FROM salevideo WHERE id=?");
    }
    
    try{
      stmt.setString(1, id);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        return readRecord(rs);
      }//if
      throw new DataException("SaleVideo with id '" + id + "' not found.");
    }finally{
      stmt.close();
    }
  }//read
  
  /** Internal method to create a Sale Video object from a record */
  synchronized SaleVideo readRecord(ResultSet rs) throws Exception {
    if (Cache.getInstance().containsKey(rs.getString("id"))) {
      return (SaleVideo)Cache.getInstance().get(rs.getString("id"));
    }
    SaleVideo sv = new SaleVideo();
    sv.setObjectAlreadyInDB(true);
    sv.setId(rs.getString("id"));
    Cache.getInstance().put(sv.getId(), sv);
    sv.setSku(rs.getString("sku"));
    sv.setVcrtcv(VCRTCVDAO.getInstance().read(rs.getString("vcrtcvid")));
    sv.setNu(NewUsedDAO.getInstance().read(rs.getString("nuid")));
    sv.setAmount(rs.getFloat("price"));
    sv.setDescription(sv.getVcrtcv().getCv().getTitle() + " (" + sv.getVcrtcv().getVcrt().getVideoCategory().getCategory() + " - " + sv.getNu().getType() + ")");
    return sv;
  }//readRecord
  
  /////////////////////////////////////////////
  ///   UPDATE methods
  
  /** Saves an existing Sale Video in the database */
  public void save(SaleVideo sv) throws DataException {
    Connection conn = ConnectionPool.getInstance().get();
    try {
      save(sv, conn);
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
  
  /** Internal method to update a Sale Video in the database */
  public void save(SaleVideo sv, Connection conn) throws Exception {
    Cache.getInstance().put(sv.getId(), sv);
    if (sv.isObjectAlreadyInDB()) {
      update(sv, conn);
    }else{
      insert(sv, conn);
    }//if
    
  }//save
  
  /** Saves an existing Sale Video to the database */
  private void update(SaleVideo sv, Connection conn) throws Exception {
    PreparedStatement stmt = conn.prepareStatement("UPDATE SaleVideo SET sku=?, vcrtcvid=?, nuid=?, price=? WHERE id=?");
    try {
      stmt.setString(1, sv.getSku());
      stmt.setString(2, sv.getVcrtcv() == null ? null : sv.getVcrtcv().getId());
      stmt.setString(3, sv.getNu() == null ? null : sv.getNu().getId());
      stmt.setFloat(4, sv.getAmount());
      stmt.setString(5, sv.getId());
      stmt.execute();
    }finally{
      stmt.close();
    }
  }
  
  /** Inserts a new Sale Video into the database */
  private void insert(SaleVideo sv, Connection conn) throws Exception {
    PreparedStatement stmt = conn.prepareStatement("INSERT INTO SaleVideo (id, sku, vcrtcvid, nuid, price) VALUES (?, ?, ?, ?, ?)");
    try {
      stmt.setString(1, sv.getId());
      stmt.setString(2, sv.getSku());
      stmt.setString(3, sv.getVcrtcv() == null ? null : sv.getVcrtcv().getId());
      stmt.setString(4, sv.getNu() == null ? null : sv.getNu().getId());
      stmt.setFloat(5, sv.getAmount());
      stmt.execute();
      sv.setObjectAlreadyInDB(true);
    }finally{
      stmt.close();
    }
  }
  
  /////////////////////////////////////////////////
  ///   DELETE methods
  
  /** Deletes an existing Sale Video from the database */
  public void delete(SaleVideo sv) throws DataException {
    delete(sv.getId());
  }
  
  /** Deletes an existing Sale Video from the database, given its id */
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
  
  /** Internal method to delete an existing Sale Video from the database */
  void delete(String id, Connection conn) throws Exception{
    Cache.getInstance().remove(id);
    PreparedStatement stmt = conn.prepareStatement("DELETE FROM SaleVideo where id=?");
    try {
      stmt.setString(1, id);
      stmt.execute();
    }finally{
      stmt.close();
    }
  }//read
  
}//class
