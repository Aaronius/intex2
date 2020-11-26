/*
 * Main.java
 *
 * Created on April 10, 2006, 8:30 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.byu.isys413.group1a.intex2.Misc;

import edu.byu.isys413.group1a.intex2.GUIs.GUIReturnsTransaction;
import java.awt.EventQueue;

/**
 *
 * @author Owner
 */
public class Main {
  
  public static void main(String args[]) {
    
    /*
      GUIReturnsTransaction grt = new GUIReturnsTransaction();
      grt.setVisible(true);
    */
    
    showSplashScreen();
    showMainWindow();
    EventQueue.invokeLater(new SplashScreenCloser());
    
    
  }
  
  private static SplashScreen fSplashScreen;
  
   /**
  * Show a simple graphical splash screen, as a quick preliminary to the main screen.
  */
  private static void showSplashScreen(){
    fSplashScreen = new SplashScreen("splash.jpg");
    fSplashScreen.splash();
  }
  
  /**
  * Display the main window of the application to the user.
  */
  private static void showMainWindow(){
    GUIReturnsTransaction grt = new GUIReturnsTransaction();
    grt.setVisible(true);
  }
  
  /**
  * Removes the splash screen. 
  *
  * Invoke this <code>Runnable</code> using 
  * <code>EventQueue.invokeLater</code>, in order to remove the splash screen
  * in a thread-safe manner.
  */
  private static final class SplashScreenCloser implements Runnable {
    public void run(){
      fSplashScreen.dispose();
    }
  }
  
}
