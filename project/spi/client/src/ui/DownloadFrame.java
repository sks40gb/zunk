/* $Header: /home/common/cvsarea/ibase/dia/src/ui/DownloadFrame.java,v 1.4 2003/12/12 22:26:08 weaston Exp $ */
package ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;



/*
 * Dialog for downloading upgraded files
 */
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class DownloadFrame extends JFrame
{

   final private static JTextArea textArea = new JTextArea(12, 40);
    
   
   ;
    final private static JButton okButton = new JButton("OK");
   final private static JButton exitButton = new JButton("Exit");

   // temporary, until old versions of client are gone

   public DownloadFrame()
   {
      this(false);
   }

   public DownloadFrame(boolean admin)
   {
      buildGui(textArea, okButton, exitButton);

      // set the frame's icon
      Toolkit toolkit = Toolkit.getDefaultToolkit();
      Image iconImage;
      iconImage = toolkit.getImage(admin
              ? "images/ibase_admin.gif"
              : "images/ibase8-32.gif");
      setIconImage(iconImage);
   }

   public void setText(final String text)
   {
      SwingUtilities.invokeLater(new Runnable()
              {

                 public void run()
                 {
                    textArea.setText(text);
                 }

              });
   }

   public void setOkListener(ActionListener listener)
   {
      okButton.addActionListener(listener);
      okButton.setEnabled(true);
      getRootPane().setDefaultButton(okButton);
   }

   public void clearOkListener(ActionListener listener)
   {
      if (listener != null) {
         okButton.removeActionListener(listener);
      }
      okButton.setEnabled(false);
      getRootPane().setDefaultButton(null);
   }

   public void setExitEnabled(final boolean flag)
   {
      if (SwingUtilities.isEventDispatchThread()) {
         exitButton.setEnabled(flag);
      }
      else {
         try {
            SwingUtilities.invokeAndWait(new Runnable()
                    {

                       public void run()
                       {
                          exitButton.setEnabled(flag);
                       }

                    });
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }


   /*
     * Set up the GUI, using the 3 given widgets.
     */
   private void buildGui(final JTextArea textArea,
           final JButton okButton,
           final JButton exitButton)
   {

      Font theFont = new Font("Dialog", Font.PLAIN, 18);

      okButton.setFont(theFont);
      exitButton.setFont(theFont);

      JPanel contentPanel = new JPanel();
      contentPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 40, 50));
      contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
      contentPanel.add(textArea);
      textArea.setFont(theFont);
      textArea.setEditable(false);
      textArea.setBackground(null);
      textArea.setLineWrap(true);
      textArea.setWrapStyleWord(true);

      JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 90, 90));
      buttonPanel.add(okButton);
      buttonPanel.add(exitButton);

      JPanel buttonArea = new JPanel(new FlowLayout());
      buttonArea.add(buttonPanel);
      contentPanel.add(buttonArea);

      this.setContentPane(contentPanel);
      this.pack();

      // Center the dialog on the screen.
      Toolkit toolkit = Toolkit.getDefaultToolkit();
      Dimension dialogSize = this.getSize();
      Dimension screenSize = toolkit.getScreenSize();
      int hpos = (int) (screenSize.getWidth() - dialogSize.getWidth()) / 2;
      int vpos = (int) (screenSize.getHeight() - dialogSize.getHeight()) / 2;
      this.setLocation(new Point((hpos < 0 ? 0 : hpos), (vpos < 0 ? 0 : vpos)));

      this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

      this.addWindowListener(new WindowAdapter()
              {

                 public void windowOpened(WindowEvent e)
                 {
                    if (okButton.isEnabled()) {
                       getRootPane().setDefaultButton(okButton);
                    }
                    else {
                       getRootPane().setDefaultButton(null);
                    }
                 }

                 public void windowClosing(WindowEvent e)
                 {
                    if (exitButton.isEnabled()) {
                       System.exit(1);
                    }
                 }

              });


      exitButton.addActionListener(new ActionListener()
              {

                 public void actionPerformed(ActionEvent e)
                 {
                    // User has requested termination.
                    System.exit(1);
                 }

              });
   }

}
