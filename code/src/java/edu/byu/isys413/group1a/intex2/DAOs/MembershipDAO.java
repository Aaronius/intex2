package edu.byu.isys413.group1a.intex2.DAOs;

import edu.byu.isys413.group1a.intex2.BOs.Membership;
import edu.byu.isys413.group1a.intex2.Misc.Cache;
import edu.byu.isys413.group1a.intex2.Misc.ConnectionPool;
import edu.byu.isys413.group1a.intex2.Misc.DataException;
import edu.byu.isys413.group1a.intex2.Misc.GUID;
import edu.byu.isys413.group1a.intex2.Misc.RenewalException;
import java.sql.*;
import java.util.Calendar;
import java.util.List;
import java.util.LinkedList;

/**
 * A singleton object that CRUD's Membership objects.
 *
 * @author Group 1A, isys@aaronhardy.com
 */
public class MembershipDAO  {
  
  /////////////////////////////////////////////
  ///   Singleton code
  
  private static MembershipDAO instance = null;
  
  /** Creates a new instance of MembershipDAO */
  private MembershipDAO() {
  }//constructor
  
  /** Retrieves the single instance of this class */
  public static synchronized MembershipDAO getInstance() {
    if (instance == null) {
      instance = new MembershipDAO();
    }
    return instance;
  }//getInstance
  
  ////////////////////////////////////////////
  ///   CREATE methods
  
  /** Creates a new Membership in the database */
  public Membership create() throws DataException {
    Membership m = new Membership();
    m.setObjectAlreadyInDB(false);
    m.setId(GUID.generate());
    Cache.getInstance().put(m.getId(), m);
    return m;
  }//create
  
  ////////////////////////////////////////////
  ///   READ methods
  
  /** Reads an existing Membership from the database */
  public Membership read(String id) throws DataException {
    if (Cache.getInstance().containsKey(id)) {
      return (Membership)Cache.getInstance().get(id);
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
  
  /** Internal method to read an existing Membership from the database */
  synchronized Membership read(String id, Connection conn) throws Exception {
    if (Cache.getInstance().containsKey(id)) {
      return (Membership)Cache.getInstance().get(id);
    }
    PreparedStatement stmt = conn.prepareStatement("SELECT * FROM membership where id=?");
    try{
      stmt.setString(1, id);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        return readRecord(rs);
      }//if
      throw new DataException("Membership with id '" + id + "' not found.");
    }finally{
      stmt.close();
    }
  }//read
  
  /** Internal method to read an existing Membership from the database using  */
  public Membership readByAcctId(String acctid) throws Exception{
    Connection conn = ConnectionPool.getInstance().get();
    PreparedStatement stmt = conn.prepareStatement("SELECT * FROM membership WHERE acctid=? ORDER BY startdate DESC");
    
    try{
      stmt.setString(1, acctid);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        return readRecord(rs);
      }//if
      throw new DataException("Membership with acctid '" + acctid + "' not found.");
    }catch (Exception e) {
      throw new DataException("An error occurred while reading the business object information.", e);
    }finally{
      stmt.close();
      ConnectionPool.getInstance().release(conn);
    }
  }//read
  
  /** External method to read all of the expired Memberships from the database, which returns a list*/
  public List<Membership> readExpDate(long today) throws DataException, RenewalException, Exception {
    long cancel = 0, expDate = 0;
    Connection conn = ConnectionPool.getInstance().get();
    PreparedStatement stmt = conn.prepareStatement("SELECT * FROM membership WHERE canceldate=? AND NOT expdate=? AND expdate<=?");
    List<Membership> memberships = new LinkedList();
    try{
      stmt.setLong(1, cancel);
      stmt.setLong(2, expDate);
      stmt.setLong(3, today);
      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        memberships.add(readRecord(rs));
      }//while
      //Checks to see if there any memberships that are expired
      if (!memberships.isEmpty()){
        return memberships;
      }
      throw new RenewalException("Expired memberships not found.");
    }catch (DataException e) {
      throw new DataException("An error occurred while reading the membership object information.", e);
    }finally{
      stmt.close();
      ConnectionPool.getInstance().release(conn);
    }
  }
  
  /** Internal method to create a Membership object from a record */
  synchronized Membership readRecord(ResultSet rs) throws Exception {
    if (Cache.getInstance().containsKey(rs.getString("id"))) {
      return (Membership)Cache.getInstance().get(rs.getString("id"));
    }
    Membership memb = new Membership();
    memb.setObjectAlreadyInDB(true);
    memb.setId(rs.getString("id"));
    Cache.getInstance().put(memb.getId(), memb);
    Calendar startDate = Calendar.getInstance();
    startDate.setTimeInMillis(rs.getLong("startdate"));
    memb.setStartDate(startDate);
    Calendar expDate = Calendar.getInstance();
    expDate.setTimeInMillis(rs.getLong("expDate"));
    memb.setExpDate(expDate);
    Calendar cancelDate = Calendar.getInstance();
    cancelDate.setTimeInMillis(rs.getLong("canceldate"));
    memb.setCancelDate(cancelDate);
    memb.setAccount(AccountDAO.getInstance().read(rs.getString("acctid")));
    memb.setMembershipType(MembershipTypeDAO.getInstance().read(rs.getString("memtypeid")));
    memb.setDescription(memb.getMembershipType().getDescription());
    memb.setAmount(memb.getMembershipType().getPrice());
    return memb;
  }//readRecord
  
  /////////////////////////////////////////////
  ///   UPDATE methods
  
  /** Saves an existing Membership in the database */
  public void save(Membership m) throws DataException {
    Connection conn = ConnectionPool.getInstance().get();
    try {
      save(m, conn);
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
  
  /** Internal method to update a Membership in the database */
  public void save(Membership m, Connection conn) throws Exception {
    Cache.getInstance().put(m.getId(), m);
    if (m.isObjectAlreadyInDB()) {
      update(m, conn);
    }else{
      insert(m, conn);
    }//if
    
  }//save
  
  /** Saves an existing Membership to the database */
  private void update(Membership m, Connection conn) throws Exception {
    PreparedStatement stmt = conn.prepareStatement("UPDATE Membership SET startdate=?, expdate=?, canceldate=?, acctid=?, memtypeid=? WHERE id=?");
    try {
      stmt.setLong(1, m.getStartDate().getTimeInMillis());
      stmt.setLong(2, m.getExpDate() == null ? 0 : m.getExpDate().getTimeInMillis());
      stmt.setLong(3, m.getCancelDate() == null ? 0 : m.getCancelDate().getTimeInMillis());
      stmt.setString(4, m.getAccount() == null ? null : m.getAccount().getId());
      stmt.setString(5, m.getMembershipType() == null ? null : m.getMembershipType().getId());
      stmt.setString(6, m.getId());
      stmt.execute();
    }finally{
      stmt.close();
    }
  }
  
  /** Inserts a new Membership into the database */
  private void insert(Membership m, Connection conn) throws Exception {
    PreparedStatement stmt = conn.prepareStatement("INSERT INTO Membership (id, startdate, expdate, canceldate, acctid, memtypeid) VALUES (?, ?, ?, ?, ?, ?)");
    try {
      stmt.setString(1, m.getId());
      stmt.setLong(2, m.getStartDate().getTimeInMillis());
      stmt.setLong(3, m.getExpDate() == null ? 0 : m.getExpDate().getTimeInMillis());
      stmt.setLong(4, m.getCancelDate() == null ? 0 : m.getCancelDate().getTimeInMillis());
      stmt.setString(5, m.getAccount() == null ? null : m.getAccount().getId());
      stmt.setString(6, m.getMembershipType() == null ? null : m.getMembershipType().getId());
      stmt.execute();
      m.setObjectAlreadyInDB(true);
    }finally{
      stmt.close();
    }
  }
  
  /////////////////////////////////////////////////
  ///   DELETE methods
  
  /** Deletes an existing Membership from the database */
  public void delete(Membership m) throws DataException {
    delete(m.getId());
  }
  
  /** Deletes an existing Membership from the database, given its id */
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
  
  /** Internal method to delete an existing Membership from the database */
  void delete(String id, Connection conn) throws Exception{
    Cache.getInstance().remove(id);
    PreparedStatement stmt = conn.prepareStatement("DELETE FROM Membership where id=?");
    try {
      stmt.setString(1, id);
      stmt.execute();
    }finally{
      stmt.close();
    }
  }//read
  
}//class
