/*
 * ImageXref.java
 *
 * Created on December 10, 2007, 6:45 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.fossa.servlet.dbload;

/**
 *
 * @author Bala
 */
/**variable used to read the Image file*/

public class ImageXref extends Xref {
  
    public char   boundary        = ' ';
    public int    offset          = 0;
    public String volume          = "";
    public String originalVolume = "";
    public String path            = "";
    public String groupOnePath  = ""; // BRS
    public String groupOneFileName  = ""; // BRS
    public String fileName        = "?unknown?";
    public int    fileType        = 0;
    public int    rotation        = ROTATE_0;
    public int    activeGroup     = 0;
    
   public String DTYG ="";
}
