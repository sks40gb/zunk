/*
 * SessionListener.java
 *
 * Created on November 22, 2007, 3:08 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.fossa.listeners;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionListener;
import javax.servlet.http.HttpSessionEvent;

/**
 * This class handles the user session operations . 
 * @author bmurali
 */
public class FossaSessionListener implements HttpSessionListener {

    /**Usage of a Static Collection Object is what the other Poster is talking about
     * contains all the active sessions.
     */    
    private static Map sessionMX = new HashMap();

    /*Called @time when a new session is created */
    public void sessionCreated(HttpSessionEvent se) {
        
        HttpSession session = se.getSession();
        sessionMX.put(session.getId(), session);

    }

    /*Called @time when an existing session is being destroyed */
    public void sessionDestroyed(HttpSessionEvent se) {

        HttpSession session = se.getSession();
        sessionMX.remove(session.getId());
    }

    /* Utility Methods */
    /**
     * Returns number os active sessions
     *
     **/
    public static int getNoActiveSessions() {

        return sessionMX.size();

    }

    /**
     * Returns a Set of Active SessionIds
     *
     **/
    public static Set getActiveSessionids() {

        return sessionMX.keySet();

    }

    /**
     * Returns whether there is any Active Session or not
     *
     **/
    public static boolean isActive(String sessionId) {

        return sessionMX.containsKey(sessionId);

    }

    /**
     * Returns associated session for specified sessionID
     * if not found returns 'null'
     *
     **/
    public static HttpSession getAssociatedSession(String sessionId) {

        HttpSession session = null;
        if (isActive(sessionId)) {
            session = (javax.servlet.http.HttpSession) sessionMX.get(sessionId);
        }else{
           //Otherwise there is no session associated for the given sessionId.
        }
        return session;
    }

    /**
     * Invalidates the specified session with consequent sessionID
     * returns true if succeful else returns false
     *
     **/
    public static boolean force2Invalidate(String sessionId) {

        boolean flag = false;
        if (isActive(sessionId)) {
            javax.servlet.http.HttpSession session = (javax.servlet.http.HttpSession) sessionMX.get(sessionId);
            session.invalidate();
            flag = true;
        }else{
           //Otherwise the session can't be invalidate for the given sessionId.
        }
        return flag;
    }
}    
    
    