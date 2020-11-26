package edu.byu.isys413.group1a.intex2.Controllers;

import edu.byu.isys413.group1a.intex2.BOs.ConceptualVideo;
import edu.byu.isys413.group1a.intex2.BOs.ReleaseType;
import edu.byu.isys413.group1a.intex2.BOs.RentalVideo;
import edu.byu.isys413.group1a.intex2.BOs.Store;
import edu.byu.isys413.group1a.intex2.BOs.VCRT;
import edu.byu.isys413.group1a.intex2.BOs.VCRTCV;
import edu.byu.isys413.group1a.intex2.BOs.VideoCategory;
import edu.byu.isys413.group1a.intex2.DAOs.ConceptualVideoDAO;
import edu.byu.isys413.group1a.intex2.DAOs.ReleaseTypeDAO;
import edu.byu.isys413.group1a.intex2.DAOs.RentalVideoDAO;
import edu.byu.isys413.group1a.intex2.DAOs.StoreDAO;
import edu.byu.isys413.group1a.intex2.DAOs.VCRTCVDAO;
import edu.byu.isys413.group1a.intex2.DAOs.VCRTDAO;
import edu.byu.isys413.group1a.intex2.DAOs.VideoCategoryDAO;
import edu.byu.isys413.group1a.intex2.GUIs.GUIDBMRentalVideoDisplay;
import edu.byu.isys413.group1a.intex2.Misc.ConnectionPool;
import edu.byu.isys413.group1a.intex2.Misc.DataException;
import java.sql.Connection;
import java.util.List;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.table.DefaultTableModel;

/**
 * This class controls database maintenance for rental videos.
 *
 * @author Group 1A, isys@aaronhardy.com
 */
public class DBMRentalVideoController {
  
  private GUIDBMRentalVideoDisplay display;
  private List<RentalVideo> rentalVideoList;
  private final String[] COLUMNS = {"Serial #","Status","Title","Release Type","Video Category","Store"}; //Column names
  private DefaultTableModel model = null;
  
  /**
   * Inner class for a list box item which will later be placed into a combo box model.
   */
  class ListBoxItem {
    private String title = null;
    private Object obj = null;
    
    /** Creates a new instance of inner class */
    public ListBoxItem(Object obj, String title) {
      this.title = title;
      this.obj = obj;
    }
    
    /** Returns title */
    public String toString() {
      return title;
    }
    
    /** Returns object */
    public Object getObject(){
      return obj;
    }
  }
  
  /**
   * Creates instance of class
   */
  public DBMRentalVideoController(GUIDBMRentalVideoDisplay display) {
    this.display = display;
  }
  
  /**
   * Creates and returns database model
   */
  public DefaultTableModel getDBModel() throws Exception{
    setRentalVideoList(RentalVideoDAO.getInstance().getAll());
    
    model = new DefaultTableModel(COLUMNS, 0);
    for (RentalVideo rv: rentalVideoList) {
      String[] rowArray = new String[6];
      rowArray[0] = rv.getSerialNum();
      rowArray[1] = rv.getStatus();
      rowArray[2] = rv.getVcrtcv().getCv().getTitle();
      rowArray[3] = rv.getVcrtcv().getVcrt().getReleaseType().getType();
      rowArray[4] = rv.getVcrtcv().getVcrt().getVideoCategory().getCategory();
      rowArray[5] = rv.getStore().getName();
      model.addRow(rowArray);
    }
    
    return model;
  }
  
  /**
   * Returns rental video list
   */
  public List<RentalVideo> getRentalVideoList() {
    return rentalVideoList;
  }
  
  /**
   * Sets rental video list
   */
  public void setRentalVideoList(List<RentalVideo> rentalVideoList) {
    this.rentalVideoList = rentalVideoList;
  }
  
  /**
   * Builds and returns model for "status" combo box
   */
  public DefaultComboBoxModel getStatusModel(){
    DefaultComboBoxModel model = new DefaultComboBoxModel();
    model.addElement("in");
    model.addElement("out");
    return model;
  }
  
  /**
   * Returns the index of the given rental video
   */
  public int getStatusModelIndex(RentalVideo rentalVideo, DefaultComboBoxModel model){
    for (int i=0; i<model.getSize(); i++){
      if(rentalVideo.getStatus().equals(model.getElementAt(i))){
        return i;
      }
    }
    return 0;
  }
  
  /**
   * Builds and returns model for "conceptual video" combo box
   */
  public DefaultComboBoxModel getConceptualVideoModel() throws Exception{
    DefaultComboBoxModel model = new DefaultComboBoxModel();
    List<ConceptualVideo> conceptualVideos = ConceptualVideoDAO.getInstance().getAll();
    for (ConceptualVideo conceptualVideo: conceptualVideos){
      model.addElement(new ListBoxItem(conceptualVideo, conceptualVideo.getTitle()));
    }
    return model;
  }
  
  /**
   * Builds and returns model for "release type" combo box
   */
  public DefaultComboBoxModel getReleaseTypeModel() throws Exception{
    DefaultComboBoxModel model = new DefaultComboBoxModel();
    List<ReleaseType> releaseTypes = ReleaseTypeDAO.getInstance().getAll();
    for (ReleaseType releaseType: releaseTypes){
      model.addElement(new ListBoxItem(releaseType, releaseType.getType()));
    }
    return model;
  }
  
  /**
   * Builds and returns model for "video category" combo box
   */
  public DefaultComboBoxModel getVideoCategoryModel() throws Exception{
    DefaultComboBoxModel model = new DefaultComboBoxModel();
    List<VideoCategory> videoCategories = VideoCategoryDAO.getInstance().getAll();
    for (VideoCategory videoCategory: videoCategories){
      model.addElement(new ListBoxItem(videoCategory, videoCategory.getCategory()));
    }
    return model;
  }
  
  /**
   * Builds and returns model for "store" combo box
   */
  public DefaultComboBoxModel getStoreModel() throws Exception{
    DefaultComboBoxModel model = new DefaultComboBoxModel();
    List<Store> stores = StoreDAO.getInstance().getAll();
    for (Store store: stores){
      model.addElement(new ListBoxItem(store, store.getName()));
    }
    return model;
  }
  
  /**
   * Returns index for given object in given model
   */
  public int getModelIndex(Object objectElementBeingModified, ComboBoxModel model){
    for (int i=0; i<model.getSize(); i++){
      if(objectElementBeingModified == ((ListBoxItem)model.getElementAt(i)).getObject()){
        return i;
      }
    }
    return 0;
  }
  
  /**
   * Saves new rental video
   */
  public void saveRentalVideo(String serialNumber, Object objStatus,
          Object objConceptualVideo, Object objReleaseType,
          Object objVideoCategory, Object objStore) throws Exception{
    
    RentalVideo rv = RentalVideoDAO.getInstance().create();
    saveRentalVideo(rv, serialNumber, objStatus, objConceptualVideo,
            objReleaseType, objVideoCategory, objStore);
  }
  
  /**
   * Saves existing rental video
   */
  public void saveRentalVideo(RentalVideo rv, String serialNumber, Object objStatus,
          Object objConceptualVideo, Object objReleaseType,
          Object objVideoCategory, Object objStore) throws Exception{
    
    Connection conn = ConnectionPool.getInstance().get();
    
    // Parameters are objects straight from the combo box model.
    // They are first cast as a ListBoxItem, then the inner object is pulled from them,
    // and finally they are cast as what they really are.
    String status = (String)objStatus;
    ConceptualVideo conceptualVideo = (ConceptualVideo)((ListBoxItem)objConceptualVideo).getObject();
    ReleaseType releaseType = (ReleaseType)((ListBoxItem)objReleaseType).getObject();
    VideoCategory videoCategory = (VideoCategory)((ListBoxItem)objVideoCategory).getObject();
    Store store = (Store)((ListBoxItem)objStore).getObject();
    
    // All combinations of Video Category and Release Type should exist in DB already.
    VCRT vcrt = VCRTDAO.getInstance().readByForeign(videoCategory.getId(), releaseType.getId());
    
    VCRTCV vcrtcv;
    
    // If vcrtcv already exists in DB, then we don't need to recreate it. Otherwise, we do.
    try{
      vcrtcv = VCRTCVDAO.getInstance().readByForeign(vcrt.getId(), conceptualVideo.getId());
    } catch(DataException de) {
      vcrtcv = VCRTCVDAO.getInstance().create();
      vcrtcv.setVcrt(vcrt);
      vcrtcv.setCv(conceptualVideo);
      VCRTCVDAO.getInstance().save(vcrtcv, conn);
    }
    
    rv.setSerialNum(serialNumber);
    rv.setStatus(status);
    rv.setVcrtcv(vcrtcv);
    rv.setStore(store);
    RentalVideoDAO.getInstance().save(rv, conn);
    conn.commit();
    
    // Refresh table model
    // Note: we could have just changed/inserted respective rows, but other rows
    // may have been effected in the meantime. It's safer if we just reload.
    display.setDisplayTableModel();
    model.fireTableDataChanged();
  }
  
}
