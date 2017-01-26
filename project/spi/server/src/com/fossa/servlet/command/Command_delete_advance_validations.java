/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.ValidationsData;
import org.w3c.dom.Element;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.session.UserTask;
import java.sql.Connection;

/**
 * This class deletes the validation functions
 * @author sunil
 */
public class Command_delete_advance_validations implements Command
{
   private Connection connection;

   public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer)
   {
      try {
         int fieldId = Integer.parseInt(action.getAttribute(A_FIELD_ID));
         int validation_mapping_details_id = Integer.parseInt(action.getAttribute(A_VALIDATION_MAPPING_DETAILS_ID));
         int validation_functions_master_id = Integer.parseInt(action.getAttribute(A_VALIDATION_FUNCTIONS_MASTER_ID));
         connection = dbTask.getConnection();
         //keep the record of the validation into the history table before deletion
         ValidationsData data = new ValidationsData();
         data.fieldId = fieldId;
         data.validation_mapping_details_id = validation_mapping_details_id;
         data.validation_functions_master_id = validation_functions_master_id;
         data.insertIntoHistoryTable(connection, user.getUsersId(), Mode.DELETE);

         //delete the record now
         user.executeUpdate(dbTask, "DELETE FROM validation_functions_master WHERE validation_functions_master_id = " + validation_functions_master_id);
         user.executeUpdate(dbTask, "DELETE FROM validation_mapping_master WHERE validation_functions_master_id =" + validation_functions_master_id);
         user.executeUpdate(dbTask, "DELETE FROM validation_mapping_details WHERE validation_mapping_details_id = " + validation_mapping_details_id);
      } catch (Exception exc) {
         CommonLogger.printExceptions(this, "Exception while deleting advance validations.", exc);
      }
      return null;
   }

   public boolean isReadOnly()
   {
      return true;
   }

}

