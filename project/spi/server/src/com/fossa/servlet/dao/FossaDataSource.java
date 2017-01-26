/*
 * DataSource.java
 *
 * Created on October 29, 2007, 2:57 PM
 */
package com.fossa.servlet.dao;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.apache.log4j.Logger;

/**
 *  
 * @author prakash
 */
/**
 * Class used to define the datasource
 */
public class FossaDataSource {
    
    private static DataSource datasource;
    
    /**constant used to define the data source name*/
    final static String env = "java:jdbc/fossaDS";
    
    private static Logger logger = Logger.getLogger("com.fossa.servlet.dao");  
   
    /** Get the datasource*/
    public static DataSource getDataSource() throws Exception, NamingException {
        if (null == datasource) {
            try {
                //Naming context
                Context context = new InitialContext();
                if (context == null) {
                    throw new Exception("There is no Context");
                }

                datasource = (DataSource) context.lookup(env);
                if (null == datasource) {
                    System.err.println("Null Data Source.");
                }
            } catch (Exception exc) {                
                logger.error("Exception in getting datasource." + exc);
                StringWriter sw = new StringWriter();
                exc.printStackTrace(new PrintWriter(sw));
                logger.error(sw.toString());
            }
        }
        return datasource;
    }
    
    /**Establish datasource connection and returns it */
    public static Connection getConnection() throws Exception, NamingException {
        if (null == datasource) {
            datasource = getDataSource();
        }
        return datasource.getConnection();
    }
}
