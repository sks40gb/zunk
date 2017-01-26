/*
 * XrefAcceptor.java
 *
 * Created on December 10, 2007, 6:40 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.fossa.servlet.dbload;

/**
 *
 * @author Bala
 */
public interface XrefAcceptor {

    /** Receive an Xref specification */
    void accept(Xref spec);

    /** Receive an error message for the current source line */
    void error(String message);
}

