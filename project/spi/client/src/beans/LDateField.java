/* $Header: /home/common/cvsarea/ibase/dia/src/beans/LDateField.java,v 1.4.8.1 2006/02/22 20:05:51 nancy Exp $ */
package beans;

import client.ClientTask;
import client.TaskSelectFieldDescription;
import com.lexpar.util.Log;
import common.CodingData;
import common.edit.ProjectMapper;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;
import javax.swing.JTextField;


/**
 * Provides an extension to LTextField that edits fields for DIA date format.
 */
public class LDateField extends LTextField
{
    private int projectId = 0;
    private String fieldName;
    private CodingData codingData;
    private String whichStatus;    
    private int child_Id;    
    private ProjectMapper projectMap;
    private static CodingData codingData_F6;
    private String bates_Number;
    private String image_Path;
    private String documentNumber;
    private int batch_Id;
   private static SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
   private static SimpleDateFormat dbformatter = new SimpleDateFormat("yyyyMMdd");
   
    
   /**
     * Create an instance of LDateField that defaults to format "yyyyMMdd".
     */   
   public LDateField(int projectId, String fieldName, CodingData codingData, String whichStatus,String documentNumber, String bates_Number, String image_Path, int child_Id, int batch_Id, ProjectMapper projectMap) {
        super(8);
        setInputVerifier(DateVerifier.createInstance());
        this.projectId = projectId;
        this.fieldName = fieldName;
        this.codingData = codingData;
        this.whichStatus = whichStatus;        
        this.documentNumber = documentNumber;
        this.bates_Number = bates_Number;
        this.image_Path = image_Path;
        this.child_Id = child_Id;
        this.batch_Id = batch_Id;
        this.projectMap = projectMap;                
        setHorizontalAlignment(JTextField.LEFT);
        addFocusListener(new FocusText());
        addKeyListener(new java.awt.event.KeyAdapter() {

            public void keyPressed(java.awt.event.KeyEvent evt) {
                textFieldKeyPressed(evt);
            }
        });
    }
   
   public LDateField(int data, CodingData codingdata) {
        this.child_Id = data;
        codingData_F6 = codingdata;
    }

   /**
     * Get the database version of an edited date
     * (Used for storing dates that come from KeyValueModel -- maybe
     * this isn't the best way of doing this...)
     */
   public static String deEdit(String dateText)
   {
      if (dateText != null &&
              dateText.length() > 0) {
         try {
            if (dateText.indexOf('/') < 0) {
            //ParsePosition pos = new ParsePosition(0);
                    //dateText = formatter.format
                    //           (dbformatter.parse(dateText, pos));
                    //Log.print("no slashes: " + dateText);
            }
            else {
               ParsePosition pos = new ParsePosition(0);
               dateText = dbformatter.format(formatter.parse(dateText, pos));
            //Log.print("with slashes: " + dateText);
            }
         } catch (Throwable th) {
            // GUI should guarantee screen date format
            Log.quit("Error in screen date '" + dateText + "'" + th);
         }
      }
      else {
         dateText = "0";
      }
      return dateText;
   }

   /**
     * This method may be overridden to provide checking and modification
     * of the string to be inserted.  
     * <p>
     * If <code>str</code> contains invalid characters, the method
     * returns <code>null</code>; otherwise it returns a string with
     * any replacements.
     *
     * @param str the text to be inserted (may be empty, but never null)
     */
   protected String checkText(String str, int offs)
   {
      char[] chars = super.getText().toCharArray();
      if (chars.length + str.length() > 8) {
         return null;  // string too long
      }
      else {
         chars = str.toCharArray();
         for (int i = 0; i < chars.length; i++) {
            char chr = chars[i];
            if (chr < '0' || chr > '9') {
               return null;  // non-numeric
            }
         }
      }
      return str;
   }
   
   private void textFieldKeyPressed(java.awt.event.KeyEvent evt) {
        try {
            switch (evt.getKeyCode()) {

                case KeyEvent.VK_F1:
                    dof1(evt);
                    break;
                case KeyEvent.VK_F10:
                    if ("Coding".equals(whichStatus) || "CodingQC".equals(whichStatus) || "Admin".equals(whichStatus)) {
                        dof10(evt);
                    }
                    break;
                case KeyEvent.VK_F6:
                    if ("CodingQC".equals(whichStatus) || "Admin".equals(whichStatus) || "Masking".equals(whichStatus)) {
                        doVerify(evt);
                        break;
                    }
            }
        } catch (Throwable th) {
            Log.quit(th);
        }
    }
   
    
    private int dof1(KeyEvent evt) {
        final ClientTask task = new TaskSelectFieldDescription(fieldName, projectId);
        task.setCallback(new Runnable() {

            public void run() {
                try {
                    String description = (String) task.getResult();
                    String desc = "";
                    String fieldDescription = "";
                    StringTokenizer Tok = new StringTokenizer(description);
                    while (Tok.hasMoreTokens()) {
                        desc = desc + " " + Tok.nextToken();
                        if (desc.length() >= 45) {
                            fieldDescription = fieldDescription + "\n" + desc;
                            desc = "";
                        }
                    }
                    fieldDescription = fieldDescription + "\n" + desc;
                    String dialogm = "HELP TEXT FOR  " + fieldName;
                    Component parent = task.getParent();

                    JOptionPane.showMessageDialog(parent,
                            fieldDescription,
                            dialogm,
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (Throwable th) {
                    Log.quit(th);
                }
            }
        });
        task.enqueue();

        return 0;
    }
    
    private int dof10(KeyEvent evt) {
        if (child_Id == 0) {
            child_Id = codingData_F6.childId;
        }
        AddEditQuery queryDialog;
        queryDialog = new AddEditQuery(this, projectMap, fieldName, bates_Number, image_Path, whichStatus, child_Id, batch_Id);
        queryDialog.setModal(true);
        queryDialog.show();
        return 0;
    }

    private int doVerify(KeyEvent evt) {

         ShowVerifyDialog verifyDialog = null;
         Object getComponent =  evt.getSource();
         LDateField field = (LDateField)getComponent;
        boolean checkIsEnabled = field.isEnabled();      
        if (codingData == null) {           
               if(!checkIsEnabled){
                    verifyDialog = new ShowVerifyDialog(this, getText(), codingData_F6.valueMap, fieldName, codingData_F6.childId, whichStatus, "LTextField");
                    verifyDialog.setLocationRelativeTo(this);
                    verifyDialog.setModal(true);
                    verifyDialog.show();
               }            
        } else {
             if(!checkIsEnabled){
                    verifyDialog = new ShowVerifyDialog(this, getText(), codingData.valueMap, fieldName, codingData.childId, whichStatus, "LTextField");
                    verifyDialog.setLocationRelativeTo(this);
                    verifyDialog.setModal(true);
                    verifyDialog.show();             
             }            
        }
       
        return 0;
    }

}

