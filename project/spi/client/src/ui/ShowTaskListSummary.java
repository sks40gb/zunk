/* $Header: /home/common/cvsarea/ibase/dia/src/ui/SessionAdminPage.java,v 1.18.2.2 2006/03/29 13:54:20 nancy Exp $ */
/*
 * UserAdminPage.java
 *
 * Created on December 14, 2003, 6:17 AM
 */
package ui;

import client.ClientTask;
import client.Global;
import client.ServerConnection;
import client.TaskDisplaySummaryList;
import client.TaskExecuteQuery;
import common.Log;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import model.ResultSetTableModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;

/**
 * On the AdminFrame, the Sessions page shows all users currently
 * logged in to DIA.  A row may be selected and Close Session clicked
 * to log the user out of DIA.
 * @author  Bill
 */
public class ShowTaskListSummary extends JFrame
{

   public static javax.swing.JTable fieldsTable;
   private JFrame parent = null;
   ResultSetTableModel model = null;
   public int userId = 0;
   private String userName = null;
   private String event_break_id = null;
   private String notes = null;
   private ResultSet results = null;
   private final static String GET_USERID_QUERY = "select users_id from users";
   private final static String GET_OTHERS_NOTES = "select notes from event_break";

   /**
     * Creates new form Show Task List Summary.
     * @param parent the frame in which to place this screen
     */
   public ShowTaskListSummary(JFrame parent)
   {
      this.parent = parent;
      initComponents();

      sessionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      sessionTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
              {

                 public void valueChanged(ListSelectionEvent e)
                 {

                    if (e.getValueIsAdjusting()) {
                       return;
                    }

                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();

                    if (lsm.isSelectionEmpty()) {
                       //---details button is enabled false	
                       //detailsButton.setEnabled(false);
                    }
                    else {
                       int row = sessionTable.getSelectedRow();
                       if (sessionTable.getModel().getValueAt(row, 0).toString().equalsIgnoreCase("Others")) {
                          //detailsButton.setEnabled(true);
                          event_break_id = sessionTable.getModel().getValueAt(row, 7).toString();
                          getNotes(event_break_id);
                       }
                       else {
                          //notes = null;
                          //detailsButton.setEnabled(false);
                       }
                    }
                 }

              });
   }

   /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {                          
   
        sessionPane = new javax.swing.JPanel();
        sessionScrollPane = new javax.swing.JScrollPane();
        sessionTable = new javax.swing.JTable();
        bottomPane = new javax.swing.JPanel();
        headerPane = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        closeButton = new javax.swing.JButton();
        
        addWindowListener(new java.awt.event.WindowAdapter() {
           public void windowClosing(java.awt.event.WindowEvent evt) {
                closeButtonActionPerformed();
            }
        });
        
        Date d = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        getUserId();
        dateLabel = new javax.swing.JLabel("Date : "+dateFormat.format(d));
        userLabel = new JLabel("User : "+userName);
        
        dateLabel.setFont(new Font("ARIAL",Font.PLAIN,12));
        userLabel.setFont(new Font("ARIAL",Font.PLAIN,12));
        
        this.setSize(600,300);
        this.setLocationRelativeTo(parent);
        this.setTitle("Task List Summary !");
       
        headerPane.setLayout(new FlowLayout());
        headerPane.add(dateLabel);
        headerPane.add(new JLabel("             "));
        headerPane.add(userLabel);
        
        sessionPane.setLayout(new java.awt.BorderLayout());

        sessionPane.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        sessionScrollPane.setViewportView(sessionTable);

        sessionPane.add(sessionScrollPane, java.awt.BorderLayout.CENTER);
 
        add(sessionPane, java.awt.BorderLayout.CENTER);

        jPanel1.setLayout(new java.awt.GridLayout(1, 0, 20, 0));

        closeButton.setText("Close");
        closeButton.setEnabled(true);
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed();
            }
        });

        jPanel1.add(closeButton);
        bottomPane.add(jPanel1);
          
        add(headerPane,java.awt.BorderLayout.NORTH);
        add(bottomPane, java.awt.BorderLayout.SOUTH);
   
    }                        

                                        
    //Action to be handled for close button action.
    private void closeButtonActionPerformed() {   
       setVisible(false);
       this.parent.setVisible(true);
    }                                                  
 
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        new ShowTaskListSummary(new JFrame());
    }

   //Gets the user id for the corresponding user name.
   private void getUserId()
   {
      final ClientTask task;
      ServerConnection sconn = Global.theServerConnection;
      userName = sconn.getUserName();
      task = new TaskExecuteQuery(GET_USERID_QUERY, userName);
      task.setCallback(new Runnable()
              {

                 public void run()
                 {
                    try {
                       ResultSet rs = (ResultSet) task.getResult();
                       rs.next();
                       int uId = rs.getInt(1);
                       assignUserId(uId);
                       taskSummaryButtonSelected();
                    } catch (Exception e) {
                       e.printStackTrace();
                    }
                 }

              });
      task.enqueue(this);
   }

   private void getNotes(String break_id)
   {
      final ClientTask task = new TaskExecuteQuery(GET_OTHERS_NOTES, break_id);
      task.setCallback(new Runnable()
              {

                 public void run()
                 {
                    try {
                       ResultSet rs = (ResultSet) task.getResult();
                       if (rs.next()) {
                          assignNotes(rs.getString(1));
                       }
                    } catch (Exception e) {
                       e.printStackTrace();
                    }
                 }

              });
      task.enqueue(this);
   }

   //Assigns the userID to a global variable userId.

   private void assignUserId(int uId)
   {
      userId = uId;
   }

   private void assignNotes(String note)
   {
      notes = note;
      if (!notes.isEmpty()) {
         //JOptionPane.showMessageDialog(sessionTable,notes);
         ShowNotesDialog showNotes = new ShowNotesDialog(this, notes);
         showNotes.setVisible(true);
      }
   }
   /**
     * Perform page initialization.  Subclasses must override this to provide any
     * required page-dependent initialization.
     * Displays the list in a table model.
     */

   protected void taskSummaryButtonSelected()
   {
      try {
         final ClientTask taskDisplay = new TaskDisplaySummaryList(userId);
         taskDisplay.setCallback(new Runnable()
                 {

                    public void run()
                    {
                       results = (java.sql.ResultSet) taskDisplay.getResult();
                       model = new ResultSetTableModel(results, new String[]{"Activity", "Duration", "Start Time", "End Time", "Project", "Volume", "Batch #"});

                       sessionTable.setModel(model);
                    }

                 });
         taskDisplay.enqueue(this);
      } catch (Throwable th) {
         Log.quit(th);
      }
   }

   // Variables declaration - do not modify                     

   private javax.swing.JPanel bottomPane;
   private javax.swing.JPanel headerPane;
   private javax.swing.JLabel dateLabel;
   private javax.swing.JLabel userLabel;
   private javax.swing.JButton closeButton;
   private javax.swing.JButton detailsButton;
   private javax.swing.JPanel jPanel1;
   private javax.swing.JPanel sessionPane;
   private javax.swing.JScrollPane sessionScrollPane;
   private javax.swing.JTable sessionTable;
   // End of variables declaration                   

}

class ShowNotesDialog extends javax.swing.JDialog
{

   /** Creates new form ShowNotesDialog */
   public ShowNotesDialog(javax.swing.JFrame parent, String notes)
   {
      super(parent);
      initComponents(parent, notes);
   }

   private void initComponents(JFrame parent, String notes)
   {

      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      setTitle("----Notes----");
      setLocationRelativeTo(parent);
      JPanel jp = new JPanel(new BorderLayout());

      JTextArea jta = new JTextArea(notes, 10, 30);
      jta.setFont(new java.awt.Font("Verdana", 0, 12));
      jta.setEditable(false);
      jta.setLineWrap(true);
      jp.add(jta, BorderLayout.NORTH);

      JButton jb = new JButton("Ok");
      jb.setEnabled(true);
      jb.addActionListener(new java.awt.event.ActionListener()
              {

                 public void actionPerformed(java.awt.event.ActionEvent evt)
                 {
                    setVisible(false);
                 }

              });
      jp.add(jb, BorderLayout.SOUTH);
      add(jp);
      pack();
   }

}

