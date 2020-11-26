package edu.byu.isys413.group1a.intex2.Controllers;

import edu.byu.isys413.group1a.intex2.BOs.ConceptualVideo;
import edu.byu.isys413.group1a.intex2.DAOs.ConceptualVideoDAO;
import edu.byu.isys413.group1a.intex2.GUIs.GUIDBMConceptualVideoDisplay;
import edu.byu.isys413.group1a.intex2.Misc.ConnectionPool;
import java.sql.Connection;
import java.util.List;
import javax.swing.table.DefaultTableModel;

/**
 * This class controls database maintenance for conceptual videos.
 *
 * @author Group 1A, isys@aaronhardy.com
 */
public class DBMConceptualVideoController {
  
  private GUIDBMConceptualVideoDisplay display;
  private List<ConceptualVideo> conceptualVideoList;
  private final String[] COLUMNS = {"Name"}; //Column names
  private DefaultTableModel model = null;
  
  /**
   * Creates a new instance of class
   */
  public DBMConceptualVideoController(GUIDBMConceptualVideoDisplay display) {
    this.display = display;
  }
  
  /**
   * Creates and returns table model
   */
  public DefaultTableModel getDBModel() throws Exception{
    setConceptualVideoList(ConceptualVideoDAO.getInstance().getAll());
    
    model = new DefaultTableModel(COLUMNS, 0);
    for (ConceptualVideo conceptualVideo: conceptualVideoList) {
      String[] rowArray = new String[1];
      rowArray[0] = conceptualVideo.getTitle();
      model.addRow(rowArray);
    }
    
    return model;
  }
  
  /**
   * Returns conceptualVideoList
   */
  public List<ConceptualVideo> getConceptualVideoList() {
    return conceptualVideoList;
  }
  
  /**
   * Sets conceptualVideoList
   */
  public void setConceptualVideoList(List<ConceptualVideo> conceptualVideoList) {
    this.conceptualVideoList = conceptualVideoList;
  }
  
  /**
   * Saves a new conceptual video
   */
  public void saveConceptualVideo(String title) throws Exception{
    ConceptualVideo cv = ConceptualVideoDAO.getInstance().create();
    saveConceptualVideo(cv, title);
  }
  
  /**
   * Saves an existing conceptual video
   */
  public void saveConceptualVideo(ConceptualVideo cv, String title) throws Exception{
    Connection conn = ConnectionPool.getInstance().get();
    
    cv.setTitle(title);
    
    ConceptualVideoDAO.getInstance().save(cv, conn);
    conn.commit();
    
    // Refresh table model
    // Note: we could have just changed/inserted respective rows, but other rows
    // may have been effected in the meantime. It's safer if we just reload.
    display.setDisplayTableModel();
    model.fireTableDataChanged();
  }
  
}
