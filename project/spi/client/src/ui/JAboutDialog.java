/* $Header: /home/common/cvsarea/ibase/dia/src/ui/JAboutDialog.java,v 1.3.10.1 2006/03/28 17:02:05 nancy Exp $ */
package ui;

import com.lexpar.util.Log;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import tools.DiaProperties;


/**
 * A basic implementation of the JDialog class, this dialog shows the
 * DIA version number and the Wintertree copyright.
 */
public class JAboutDialog extends javax.swing.JDialog
{
   //final private static String PGM_VERSION = "1.03.020";
    // version of database corresponding to this program version
    //final private static String DB_VERSION = "1.03.020";

   public JAboutDialog(Frame parentFrame)
   {
      super(parentFrame);
      //{{INIT_CONTROLS
      setTitle("JFC Application - About");
      setModal(true);
      getContentPane().setLayout(new GridBagLayout());
      setSize(248, 164);
      setVisible(false);
      okButton.setText("OK");
      okButton.setActionCommand("OK");
      okButton.setOpaque(false);
      okButton.setMnemonic((int) 'O');
      getContentPane().add(okButton, new GridBagConstraints(2, 8, 1, 1, 0.0, 0.0, java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.NONE, new Insets(0, 0, 10, 0), 0, 0));
      aboutLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
      aboutLabel.setText("SPiCA " + DiaProperties.getProperty("program_version"));
      sentryLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
      sentryLabel.setText("The Sentry Spelling-Checker Engine");
      copyrightLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
      copyrightLabel.setText("Copyright \u00a9 2000 Wintertree Software Inc.");
      blankLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
      blankLabel.setText(" ");
      blank2Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
      blank2Label.setText(" ");
      blank3Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
      blank3Label.setText(" ");
      lineLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
      lineLabel.setText("________");
      getContentPane().add(blankLabel, new GridBagConstraints(0, 0, 3, 1, 1.0, 1.0, java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
      getContentPane().add(blank2Label, new GridBagConstraints(0, 1, 3, 1, 1.0, 1.0, java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
      getContentPane().add(aboutLabel, new GridBagConstraints(0, 2, 3, 1, 1.0, 1.0, java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
      getContentPane().add(blank3Label, new GridBagConstraints(0, 3, 3, 1, 1.0, 1.0, java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
      getContentPane().add(lineLabel, new GridBagConstraints(0, 4, 3, 1, 1.0, 1.0, java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
      getContentPane().add(sentryLabel, new GridBagConstraints(0, 5, 3, 1, 1.0, 1.0, java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
      getContentPane().add(copyrightLabel, new GridBagConstraints(0, 6, 3, 1, 1.0, 1.0, java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      getContentPane().add(blankLabel, new GridBagConstraints(0, 7, 3, 1, 1.0, 1.0, java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
      //}}

      //{{REGISTER_LISTENERS
      SymWindow aSymWindow = new SymWindow();
      this.addWindowListener(aSymWindow);
      SymAction lSymAction = new SymAction();
      okButton.addActionListener(lSymAction);
   //}}
   }

   /**
     * Set this dialog visible or not.
     * @param b true to set visible, false to set not visible
     */
   public void setVisible(boolean b)
   {
      if (b) {
         Rectangle bounds = (getParent()).getBounds();
         Dimension size = getSize();
         setLocation(bounds.x + (bounds.width - size.width) / 2,
                 bounds.y + (bounds.height - size.height) / 2);
      }

      super.setVisible(b);
   }

   public void addNotify()
   {
      // Record the size of the window prior to calling parents addNotify.
      Dimension d = getSize();

      super.addNotify();

      if (fComponentsAdjusted) {
         return;
      }
      // Adjust components according to the insets
      Insets insets = getInsets();
      setSize(insets.left + insets.right + d.width, insets.top + insets.bottom + d.height);
      Component components[] = getContentPane().getComponents();
      for (int i = 0; i < components.length; i++) {
         Point p = components[i].getLocation();
         p.translate(insets.left, insets.top);
         components[i].setLocation(p);
      }
      fComponentsAdjusted = true;
   }

   // Used for addNotify check.

   boolean fComponentsAdjusted = false;

   //{{DECLARE_CONTROLS

   javax.swing.JButton okButton = new javax.swing.JButton();
   javax.swing.JLabel aboutLabel = new javax.swing.JLabel();
   javax.swing.JLabel sentryLabel = new javax.swing.JLabel();
   javax.swing.JLabel copyrightLabel = new javax.swing.JLabel();
   javax.swing.JLabel lineLabel = new javax.swing.JLabel();
   javax.swing.JLabel blankLabel = new javax.swing.JLabel();
   javax.swing.JLabel blank2Label = new javax.swing.JLabel();
   javax.swing.JLabel blank3Label = new javax.swing.JLabel();
   //}}

   class SymWindow extends java.awt.event.WindowAdapter
   {

      public void windowClosing(java.awt.event.WindowEvent event)
      {
         try {
            Object object = event.getSource();
            if (object == JAboutDialog.this) {
               jAboutDialog_windowClosing(event);
            }
         } catch (Throwable t) {
            Log.quit(t);
         }
      }

   }

   void jAboutDialog_windowClosing(java.awt.event.WindowEvent event)
   {
      // to do: code goes here.

      jAboutDialog_windowClosing_Interaction1(event);
   }

   void jAboutDialog_windowClosing_Interaction1(java.awt.event.WindowEvent event)
   {
      try {
         // JAboutDialog Hide the JAboutDialog
         this.setVisible(false);
      } catch (Exception e) {
      }
   }

   class SymAction implements java.awt.event.ActionListener
   {

      public void actionPerformed(java.awt.event.ActionEvent event)
      {
         try {
            Object object = event.getSource();
            if (object == okButton) {
               okButton_actionPerformed(event);
            }
         } catch (Throwable e) {
            Log.quit(e);
         }
      }

   }

   void okButton_actionPerformed(java.awt.event.ActionEvent event)
   {
      // to do: code goes here.

      okButton_actionPerformed_Interaction1(event);
   }

   void okButton_actionPerformed_Interaction1(java.awt.event.ActionEvent event)
   {
      try {
         // JAboutDialog Hide the JAboutDialog
         this.setVisible(false);
      } catch (Exception e) {
      }
   }

}