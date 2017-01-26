/*
 * Xref.java
 *
 * Created on December 10, 2007, 6:35 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.fossa.servlet.dbload;

/**
 *
 * @author Bala
 */
/**
 * Parent class of image cross-references.  When loading a cross-reference
 * file, we collect the cross-references, then insert them all in the
 * database in a single transaction.
 */
public abstract class Xref implements XrefConstants {
    public String bates;
    public String documentNumber = ""; // BRS
   
}
