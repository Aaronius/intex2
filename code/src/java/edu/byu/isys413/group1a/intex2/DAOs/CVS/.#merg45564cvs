package edu.byu.isys413.group1a.intex2.DAOs;

import edu.byu.isys413.group1a.intex2.BOs.VCRTCV;
import edu.byu.isys413.group1a.intex2.Misc.Cache;
import edu.byu.isys413.group1a.intex2.Misc.ConnectionPool;
import edu.byu.isys413.group1a.intex2.Misc.DataException;
import edu.byu.isys413.group1a.intex2.Misc.GUID;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A singleton object that CRUD's VideoCategoryReleaseType-ConceptualVideo objects.
 *
 * @author Group 1A, isys@aaronhardy.com
 */
public class VCRTCVDAO  {
  
  /////////////////////////////////////////////
  ///   Singleton code
  
  private static VCRTCVDAO instance = null;
  
  /** Creates a new instance of VCRTCVDAO */
  private VCRTCVDAO() {
  }//constructor
  
  /** Retrieves the single instance of this class */
  public static synchronized VCRTCVDAO getInstance() {
    if (instance == null) {
      instance = new VCRTCVDAO();
    }
    return instance;
  }//getInstance
  
  ////////////////////////////////////////////
  ///   CREATE methods
  
  /** Creates a new VCRTCV in the database */
  public VCRTCV create() throws DataException {
    VCRTCV vcrtcv = new VCRTCV();
    vcrtcv.setObjectAlreadyInDB(false);
    vcrtcv.setId(GUID.generate());
    Cache.getInstance().put(vcrtcv.getId(), vcrtcv);
    return vcrtcv;
  }//create
  
  ////////////////////////////////////////////
  ///   READ methods
  
  /** Reads an existing VCRTCV from the database */
  public VCRTCV read(String id) throws DataException {
    if (Cache.getInstance().containsKey(id)) {
      return (VCRTCV)Cache.getInstance().get(id);
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
  
  /** Internal method to read an existing VCRTCV from the database */
  synchronized VCRTCV read(String id, Connection conn) throws Exception {
    if (Cache.getInstance().containsKey(id)) {
      return (VCRTCV)Cache.getInstance().get(id);
    }
    PreparedStatement stmt = conn.prepareStatement("SELECT * FROM VCRTCV where id=?");
    try{
      stmt.setString(1, id);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        return readRecord(rs);
      }//if
      throw new DataException("VCRTCV with id '" + id + "' not found.");
    }finally{
      stmt.close();
    }
  }//read
  
  /** Internal method to read an existing VCRTCV from the database */
  public VCRTCV readByForeign(String VCRTId, String conceptualVideoId) throws Exception{
    Connection conn = ConnectionPool.getInstance().get();
    PreparedStatement stmt = conn.prepareStatement("SELECT * FROM vcrtcv WHERE vcrtid=? and cvid=?");
    try{
      stmt.setString(1, VCRTId);
      stmt.setString(2, conceptualVideoId);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        return readRecord(rs);
      }//if
      throw new DataException("A VCRTCV with VCRT ID '" + VCRTId + "' and Conceptual Video ID '"+ conceptualVideoId +"' not found.");
    }finally{
      stmt.close();
      ConnectionPool.getInstance().release(conn);
    }
  }//read
  
  /** Internal method to create a VCRTCV object from a record */
  synchronized VCRTCV readRecord(ResultSet rs) throws Exception {
    if (Cache.getInstance().containsKey(rs.getString("id"))) {
      return (VCRTCV)Cache.getInstance().get(rs.getString("id"));
    }
    VCRTCV vcrtcv = new VCRTCV();
    vcrtcv.setObjectAlreadyInDB(true);
    vcrtcv.setId(rs.getString("id"));
    Cache.getInstance().put(vcrtcv.getId(), vcrtcv);
    vcrtcv.setVcrt(VCRTDAO.getInstance().read(rs.getString("vcrtid")));
    vcrtcv.setCv(ConceptualVideoDAO.getInstance().read(rs.getString("cvid")));
    return vcrtcv;
  }//readRecord
  
  /////////////////////////////////////////////
  ///   UPDATE methods
  
  /** Saves an existing VCRTCV in the database */
  public void save(VCRTCV vcrtcv) throws DataException {
    Connection conn = ConnectionPool.getInstance().get();
    try {
      save(vcrtcv, conn);
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
  
  /** Internal method to update a VCRTCV in the database */
  public void save(VCRTCV vcrtcv, Connection conn) throws Exception {
    Cache.getInstance().put(vcrtcv.getId(), vcrtcv);
    if (vcrtcv.isObjectAlreadyInDB()) {
      update(vcrtcv, conn);
    }else{
      insert(vcrtcv, conn);
    }//if
    
  }//save
  
  /** Saves an existing VCRTCV to the database */
  private void update(VCRTCV vcrtcv, Connection conn) throws Exception {
    PreparedStatement stmt = conn.prepareStatement("UPDATE VCRTCV SET vcrt=?, cvid=? WHERE id=?");
    try {
      stmt.setString(1, vcrtcv.getVcrt() == null ? null : vcrtcv.getVcrt().getId());
      stmt.setString(2, vcrtcv.getCv() == null ? null : vcrtcv.getCv().getId());
      stmt.setString(3, vcrtcv.getId());
      stmt.execute();
    }finally{
      stmt.close();
    }
  }
  
  /** Inserts a new VCRTCV into the database */
  private void insert(VCRTCV vcrtcv, Connection conn) throws Exception {
    PreparedStatement stmt = conn.prepareStatement("INSERT INTO VCRTCV (id, vcrtid, cvid) VALUES (?, ?, ?)");
    try {
      stmt.setString(1, vcrtcv.getId());
      stmt.setString(2, vcrtcv.getVcrt() == null ? null : vcrtcv.getVcrt().getId());
      stmt.setString(3, vcrtcv.getCv() == null ? null : vcrtcv.getCv().getId());
      stmt.execute();
      vcrtcv.setObjectAlreadyInDB(true);
    }finally{
      stmt.close();
    }
  }
  
  /////////////////////////////////////////////////
  ///   DELETE methods
  
  /** Deletes an existing VCRTCV from the database */
  public void delete(VCRTCV vcrtcv) throws DataException {
    delete(vcrtcv.getId());
  }
  
  /** Deletes an existing VCRTCV from the database, given its id */
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
  
  /** Internal method to delete an existing VCRTCV from the database */
  void delete(String id, Connection conn) throws Exception{
    Cache.getInstance().remove(id);
    PreparedStatement stmt = conn.prepareStatement("DELETE FROM VCRTCV where id=?");
    try {
      stmt.setString(1, id);
      stmt.execute();
    }finally{
      stmt.close();
    }
  }//read
  
  /** Method for getting all VCRTCVs by ConceptualVideo id */
  public List<VCRTCV> searchByConceptualVideoId(String conceptualVideoId) throws Exception{
    List<VCRTCV> list = new ArrayList();
    Connection conn = ConnectionPool.getInstance().get();
    
    PreparedStatement stmt = conn.prepareStatement("SELECT * FROM vcrtcv WHERE cvid = ?");
    stmt.setString(1, conceptualVideoId);
    ResultSet rs = stmt.executeQuery();
    
    while (rs.next()) {
      list.add(readRecord(rs));
    }
    
    return list;
  }
  
}//class
