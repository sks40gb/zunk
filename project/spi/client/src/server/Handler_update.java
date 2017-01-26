/*
 * Handler_update.java
 *
 * Created on October 24, 2007, 3:05 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package server;

import client.Global;
import client.ServerConnection;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author bmurali
 */
public class Handler_update extends Handler {
    
    final private ServerConnection scon = Global.theServerConnection;
    /** Creates a new instance of Handler_update */
    public Handler_update() {
    }

    public void run(ServerTask task, Element action) throws IOException, SQLException {
        System.out.println("inside handler update........................."+  "action name="+  action );
        Connection con=task.getConnection();
        PreparedStatement pstmt=null;
        PreparedStatement pstmt2=null;
        PreparedStatement pstmt3=null;
        PreparedStatement pstmt4=null;
        PreparedStatement pstmt5=null;
        PreparedStatement pstmt6=null;
        ResultSet resultSet1=null;
        ResultSet resultSet2=null;
        int i=0;
        int j=0;
        int k=0;
        //Element reply = scon.receiveMessage();
          int projectId = Integer.parseInt(action.getAttribute(A_PROJECT_ID));
          int fieldId = Integer.parseInt(action.getAttribute(A_FIELD_ID)); 
          System.out.println("fieldId.........."+  fieldId);
         
        NodeList errorMessage = action.getElementsByTagName("error_message");
         //System.out.println("errorMessage............."+  errorMessage);
        NodeList errorMessageValue = errorMessage.item(i).getChildNodes();
         //System.out.println("errorMessageValue............."+  errorMessageValue);
        
        NodeList status = action.getElementsByTagName("status");
        NodeList statusValue = status.item(j).getChildNodes();
        
        NodeList user_input = action.getElementsByTagName("user_input");
        NodeList user_inputValue = user_input.item(k).getChildNodes();
        
        String [] errorValueArray = new String[errorMessageValue.getLength()];
        System.out.println("errorValueArray............."+  errorValueArray);
        String [] statusValueArray = new String[statusValue.getLength()];
        String [] userInputValueArray = new String[user_inputValue.getLength()];
        
        for( int err=0; err<errorMessageValue.getLength(); err++ ) {                
                 errorValueArray[err]=errorMessageValue.item(err).getTextContent();
                  System.out.println("errorValueArray............."+  errorValueArray[err]);
            }
        
        for( int sts=0; sts<statusValue.getLength(); sts++ ) {                
                 statusValueArray[sts]=statusValue.item(sts).getTextContent();
            }
        
        for( int uInt=0; uInt<user_inputValue.getLength(); uInt++ ) {                
                 userInputValueArray[uInt]=user_inputValue.item(uInt).getTextContent();
            }
        
        
                    int m = 0;
                    int a = 1;
                    for (int n = 0; n < errorValueArray.length; n++) {
                    pstmt = con.prepareStatement("UPDATE validation_functions SET error_message = ?,status = ?,user_input = ? WHERE validationFun_id = ? AND validation_id = ?");
                    pstmt.setString(1, errorValueArray[n]);
                    pstmt.setString(2, statusValueArray[n]);
                    pstmt.setString(3, userInputValueArray[n]);
                    pstmt.setInt(4, a);
                    pstmt.setInt(5, 1);
                    pstmt.executeUpdate();
                    m++;
                    a++;
                    }
                   pstmt.close(); 
                    
                   pstmt2=con.prepareStatement("UPDATE projectfields SET first_name = ? WHERE project_id = ? AND projectfields_id = ?");
                   pstmt2.setString(1, "yes");
                   pstmt2.setInt(2, projectId);
                   pstmt2.setInt(3, fieldId);
                   pstmt2.executeUpdate();
                   pstmt2.close(); 
                   
                   pstmt3=con.prepareStatement("SELECT validationFun_id,function_name,user_input FROM validation_functions WHERE validation_id = ?  AND status = ?");
                   pstmt3.setInt(1, 1);
                   pstmt3.setString(2, "true");
                   pstmt3.executeQuery();
                   
                   resultSet1=pstmt3.getResultSet();
                   
                   pstmt5=con.prepareStatement("SELECT * FROM validation_selected WHERE projectfields_id = ?");
                   pstmt5.setInt(1, fieldId);
                   pstmt5.executeQuery();
                   
                   resultSet2=pstmt5.getResultSet();
                    if(resultSet2 != null){
                       pstmt6=con.prepareStatement("DELETE FROM validation_selected WHERE projectfields_id = ?");
                       pstmt6.setInt(1, fieldId);
                       pstmt6.executeUpdate();
                       pstmt6.close();
                    }
                    pstmt5.close();
                   while(resultSet1.next()){
                       int valFunId=resultSet1.getInt(1);
                       String funName=resultSet1.getString(2);
                       String userInput=resultSet1.getString(3);
                      pstmt4=con.prepareStatement("INSERT INTO validation_selected (projectfields_id,validationFun_id,function_name,user_input) VALUES(?,?,?,?)"); 
                      pstmt4.setInt(1, fieldId);
                      pstmt4.setInt(2, valFunId);
                      pstmt4.setString(3, funName);
                      pstmt4.setString(4, userInput);
                      pstmt4.executeUpdate();
                      pstmt4.close();
                   }
                   pstmt3.close();
                   
                   
                   
        
    }
    
    
    
    
}
