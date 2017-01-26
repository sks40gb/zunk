/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.w3c.dom.Element;
import java.util.ArrayList;
import java.util.List;

/**
 * This class returns fields,users which have status either Listing OR Tally.
 * @author anu
 */
public class Command_get_listing_qc_field implements Command
{   
   private Connection con;
   private Statement st;
   private int projectId = 0;
   private int volumeId = 0;
   private String whichStatus = null;     //Either it will be Listing or Tally
   private List allDatalist;   //Conatins all dataList
   private List datalist;      //Contains listing_qc_id,tally_qc_id,username,status

   public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer)
   {
      try {
         con = dbTask.getConnection();
         projectId = Integer.parseInt(action.getAttribute(A_PROJECT_ID));
         volumeId = Integer.parseInt(action.getAttribute(A_VOLUME_ID));
         whichStatus = action.getAttribute(A_STATUS);
         allDatalist = new ArrayList();
         st = con.createStatement();      
         String query = null;
         if(whichStatus.equals("Listing")){
            query = "select listing_qc_id,field_name,user_name,status from listing_qc";
         }else if(whichStatus.equals("Tally")){
            query = "select tally_qc_id,field_name,user_name,status from tally_qc";
         }
         ResultSet rs = st.executeQuery(query + " where project_id = "+ projectId +" and volume_id = " + volumeId);
         while (rs.next()) {
            datalist = new ArrayList();            
            datalist.add(rs.getString(2));
            datalist.add(rs.getString(3));
            datalist.add(rs.getString(4));
            datalist.add(rs.getString(1));
            allDatalist.add(datalist);
         }
         st.close();         
         writeXmlFromResult(user, writer, allDatalist);
      } catch (Exception exc) {
         CommonLogger.printExceptions(this, "Exception while getting the listing qc fields." , exc);
         return null;
      }
      return null;
   }

   //Reply sent back to the client.
   public void writeXmlFromResult(UserTask task, MessageWriter writer, List ls)
           throws SQLException, IOException
   {
      String userSessionId = task.getFossaSessionId();
      writer.startElement(T_RESULT_SET);
      writer.writeAttribute(A_FOSSAID, userSessionId);
      writer.writeAttribute(A_COUNT, 4);

      for (int i = 0; i < ls.size(); i++) {
         writer.startElement(T_ROW);
         List innerList = (List) ls.get(i);
         for (int j = 0; j < innerList.size(); j++) {
            writer.startElement(T_COLUMN);
            writer.writeContent(innerList.get(j).toString());
            writer.endElement();
         }
         writer.endElement();
      }
      writer.endElement();
   }

   public boolean isReadOnly()
   {
      return true;
   }

}
