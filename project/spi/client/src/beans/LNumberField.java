/* $Header: /home/common/cvsarea/ibase/dia/src/beans/LNumberField.java,v 1.8.2.1 2006/02/22 20:05:51 nancy Exp $ */

package beans;

//import com.lexpar.util.Log;

import client.ClientTask;
import client.TaskSelectFieldDescription;
import common.Log;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;

/**
 * Provides an extension of LTextField which allows for
 * numbers with decimal points.
 */
public class LNumberField extends LTextField implements LField {

    private int decimals;
    private int precision;
    private String zeroText;
    private String fieldName;
    private int projectId;
    /**
     * Create an instance of <code>LNumberField</code> with <code>precision</code>
     * integer places and <code>decimals</code> decimal places.
     * @param precision number of precision places
     * @param decimals number of decimal places
     */
    public LNumberField(int precision, int decimals) {
        super(precision + decimals + (precision / 3) + 1); // add for commas and decimal
        this.precision = precision;
        this.decimals = decimals;
        zeroText = "0.";
        for (int i = 0; i < precision; i++) {
            zeroText = zeroText + "0";
        }
    }
     public LNumberField(int precision, int decimals,int projectId,String fieldName) {
        super(precision + decimals + (precision / 3) + 1); // add for commas and decimal
        this.precision = precision;
        this.decimals = decimals;
        this.projectId=projectId;
        this.fieldName=fieldName;
        zeroText = "0.";
        for (int i = 0; i < precision; i++) {
            zeroText = zeroText + "0";
        }
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                textFieldKeyPressed(evt);
            }
        });
    }
    /**
     * Create an instance of <code>LNumberField</code> with <code>precision</code>
     * integer places and zero decimal places.
     * @param precision number of precision places
     */
    public LNumberField(int precision) {
        this(precision, 0);
    }
    public LNumberField(int precision,int projectId,String fieldName) {
            this(precision, 0,projectId,fieldName);
        }
    /**
     * Create an instance of <code>LNumberField</code> with zero
     * integer places and zero decimal places.
     */
    public LNumberField() {
        this(0, 0);
    }
    
    private void textFieldKeyPressed(java.awt.event.KeyEvent evt) {                                     
        try {
            //System.err.println("ITF-->"+evt.paramString());
            
            switch (evt.getKeyCode()) {
           
            case KeyEvent.VK_F1:    
                dof1(evt);
                break;
           
            }
        } catch (Throwable th) {
            Log.quit(th);
        }
    } 
    
    private int dof1(KeyEvent evt) {           
                final ClientTask task = new TaskSelectFieldDescription(fieldName,projectId);
                task.setCallback(new Runnable() {
                        public void run() {
                            try {                                  
                                 
                                String description =  (String)task.getResult();
                                String desc = "";
                                String fieldDescription = "";
                               StringTokenizer Tok = new StringTokenizer(description);
                               while(Tok.hasMoreTokens()){
                                 desc = desc + " " +Tok.nextToken();
                                 if(desc.length() >= 45 ){
                                  fieldDescription = fieldDescription + "\n" + desc;
                                  desc = "";
                                 }
                               }
                                 fieldDescription = fieldDescription + "\n" + desc;                                
                                 String dialogm="HELP TEXT FOR  "+ fieldName;
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
    
  
    final private static NumberFormat nf = NumberFormat.getNumberInstance();

    protected String checkText(String str, int offs) {
        if (str.length() == 0) {
            return str;
        }
        char[] chars = super.getText().toCharArray();
        int num = 0;
        int dec = 0;
        if (super.getText().indexOf(".") > -1
            && str.indexOf(".") > -1) {
            // already have decimal
            return null;
        }
        // Count precisions and decimals
        for (int i = 0; i < chars.length; i++) {
            char chr = chars[i];
            if (chr >= '0' && chr <= '9') {
                if (super.getText().indexOf(".") > -1
                    && i >= super.getText().indexOf(".")) {
                    dec += 1;
                } else {
                    num += 1;
                }
            }
        }
        //Log.print("checkText dec " + dec + "/" + num + "/" + precision);
        if ((! str.equals("."))
            && (super.getText().indexOf(".") > -1
            && offs >= super.getText().indexOf("."))
            && (dec >= decimals))  {
            // no more places to insert decimal
            return null;
        } else if ((! str.equals("."))
                && ((super.getText().indexOf(".") > -1
                   && offs < super.getText().indexOf("."))
                   || super.getText().indexOf(".") <= -1)
                && num >= precision)
        {
            return null;
        } else {
            chars = str.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                char chr = chars[i];
                if ((chr < '0' || chr > '9')
                    && (chr != ',') && (chr != '.')) {
                    return null;
                }
            }
        }
        return str;
    }

    public String getText() {
        String text = super.getText();
        //Log.print("(LNumberField) getText entry " + text);
        if (text == null) {
            return "0";
        }
        while (text.startsWith("0")) {
            text = text.substring(1);
        }
        if (text.length() != 0) {
            ParsePosition pos = new ParsePosition(0);
            nf.setMaximumFractionDigits(decimals);
            nf.setMinimumFractionDigits(0);
            Number n = nf.parse(text, pos);
            text =  String.valueOf(n);

        }
        //Log.print("(LNumberField) getText " + text);
        return ((text.length() == 0) ? "" : text);

    }

    public void setText(String text) {
        theImpl.checkChanged(text);
        //Log.print("(LNumberField) setText " + text);
        if (text != null
            && text.length() > 0) {
            ParsePosition pos = new ParsePosition(0);
            nf.setMaximumFractionDigits(decimals);
            Number n = nf.parse(text, pos);
            text =  nf.format(n);
        }
        //Log.print("(LNumberField) " + text);
        if (text == null
            || text.equals(zeroText)
            || text.equals("0.0")
            || text.equals("0")
            || text.equals("0.00") ) {
            //Log.print("(LNumberField) set blank");
            super.setText("");
        } else {
            //Log.print("(LNumberField) setText " + text);
            super.setText(text);
        }
    }

    public void clearField() {
        setText("");
    }
    
    public void setComboBox(LComboBox combo)
   {
       theImpl.setComboBox(combo);
   }

   public LComboBox getComboBox()
   {
       return theImpl.getComboBox();
   }
}
