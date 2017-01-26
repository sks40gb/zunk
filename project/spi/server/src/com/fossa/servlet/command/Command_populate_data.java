/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet.command;

import com.fossa.servlet.common.Log;
import com.fossa.servlet.common.PopulateData;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.common.msg.XmlReader;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.dbload.DiaPopulate;
import com.fossa.servlet.server.ServerProperties;
import com.fossa.servlet.session.UserTask;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This class handles the data population during import.
 * @author ashish
 */
class Command_populate_data implements Command{

    private String database = "";   
    
    public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer) {       
         try {
            Log.print("Command_populate_data");    
            
            database = ServerProperties.getProperty("database");    
            
            Element givenValueList = action;
            Node firstChild = givenValueList.getFirstChild();
            while (firstChild != null && firstChild.getNodeType() != Node.ELEMENT_NODE) { 
                firstChild = firstChild.getNextSibling();
            }
            if (firstChild != null) {
                PopulateData data = new PopulateData();
                // fill in the int and String fields of the ExportData
                XmlReader xmlReader=new XmlReader();
                xmlReader.decode(givenValueList, data, /* trim? */ false);
                if (data != null) {
                    // update or insert the export row contained in data
                    Log.print("(Command_populate_data.run) project is " + data.dataFilename);
        
                    ArrayList list = new ArrayList();
                    // project_name
                    list.add("-p");
                    list.add(data.project_name);
        
                    // volume_name
                    list.add("-v");
                    list.add(data.volume_name);
        
                    list.add("-d");
                    list.add(data.dataFilename);

                    if (data.force.equals("Yes")) {
                        list.add("--replace");
                    }
                    // convert the args to a string array
                    String[] args = (String[])list.toArray(new String[list.size()]);
        
                    DiaPopulate diaPopulate = new DiaPopulate(data);
                    diaPopulate.setDatabase(database);
                    diaPopulate.run();
        
                    String status = diaPopulate.getStats();
                    if (status.equals(T_OK)) {
                        writer.startElement(T_OK);
                    } else {
                        writer.startElement(T_ERROR);
                        writer.writeAttribute(A_DATA, status);
                    }
                    writer.endElement();
                }
            }
        } catch (Throwable t) {
            logger.error("Exception while populating the data." + t);
            StringWriter sw = new StringWriter();
            t.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
            Log.quit(t);
        }
         return null;
    }

    public boolean isReadOnly() {
        return true;
    }

}
