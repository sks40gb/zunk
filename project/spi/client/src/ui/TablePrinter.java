package ui;

import beans.TableRenderer;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import model.SumTableModel;

/**
 * Called from <code>TableViwer</code>, this is a Thread to print
 * TableModel data with the given title and parameters printed.
 * @see model.SumTableModel
 */
public class TablePrinter extends Thread
{

   SumTableModel model;
   String title;
   String[] paramNames;
   String[] paramDisplay;
   private Printable aPrintable;
   private Pageable aPageable;
   TableRenderer renderer = null;
   PageFormat format;

   private TablePrinter()
   {
   }

   /**
     * Create an instance of this and remember the parameters; start
     * the Thread.
     * @param model the SumTableModel containing the data to be printed
     * @param title the title to print on the report
     * @param paramNames the names of the data selection parameters
     * @param paramDisplay the parameters formatted for display on the report
     */
   final public static void doPrint(SumTableModel model,
           String title, String[] paramNames, String[] paramDisplay)
   {
      TablePrinter tp = new TablePrinter();
      tp.model = model;
      tp.title = title;
      tp.paramNames = paramNames;
      tp.paramDisplay = paramDisplay;
      //System.out.println("tp.title="+tp.title);
      tp.start();
   }

   public void run()
   {

      //System.out.println("run title="+title);

      //// (Following code for testing on screen instead of printer -- for testing)
        //final JFrame frame = new JFrame();
        //final JPanel panel = new JPanel() {
        //
        //        TableRenderer renderer = null;
        //
        //        public void paintComponent(Graphics g) { super.paintComponent(g);
        //            Graphics2D g2 = (Graphics2D) g;
        //            renderer = new TableRenderer(model,g2,
        //                    title, paramNames, paramDisplay,
        //                    (float) this.getX(),
        //                    (float) this.getY(),
        //                    (float) this.getWidth(),
        //                    (float) this.getHeight());
        //            renderer.render(g2, 0);
        //        }
        //    };
        //frame.getContentPane().add(panel);
        //frame.setSize(850,550);
        //frame.show();

      //if (true) {
        //    return;
        //}

      // Note.  Following prints report in LANDSCAPE mode.  Dialog
        // has properties button, which then appears to allow a selection
        // of LANDSCAPE/PORTRAIT and other stuff--but it doesn't seem
        // to work.  I tried sending an PrintRequestAttributeSet to
        // the dialog, which gave some more things to set, but I couldn't
        // make them work, either.

      // Also, the number of pages isn't shown correctly,
        // because we don't know how many pages until we have
        // the Graphics object to allow us to get the Font and the
        // FontRenderContext.  Might be able to fix this by
        // setting the Font absolutely and using the Font's size
        // calculations rather than TextLayout.

      PrinterJob aPrinterJob = PrinterJob.getPrinterJob();
      format = aPrinterJob.defaultPage();
      aPrintable = new TablePrintable(model, title, paramNames, paramDisplay);
      aPageable = new TablePageable();

      if (aPrinterJob.printDialog()) {
         try {
            format.setOrientation(PageFormat.LANDSCAPE);
            format = aPrinterJob.validatePage(format);
            //aPrinterJob.setPrintable(aPrintable, format);
            aPrinterJob.setPageable(aPageable);
            aPrinterJob.print();
         } catch (Exception ex) {
            ex.printStackTrace();
         }
      }
   }

   private class TablePrintable implements Printable
   {

      SumTableModel model;
      String title;
      String[] paramNames;
      String[] paramDisplay;

      TablePrintable(SumTableModel model,
              String title, String[] paramNames, String[] paramDisplay)
      {
         this.model = model;
         this.title = title;
         this.paramNames = paramNames;
         this.paramDisplay = paramDisplay;
      //System.out.println("TablePrintable title="+title);
      }

      public int print(Graphics graphics, PageFormat format, int pageIndex)
      {
         Graphics2D g2 = (Graphics2D) graphics;
         if (renderer == null) {
            renderer = new TableRenderer(model, g2,
                    title, paramNames, paramDisplay,
                    (float) format.getImageableX(), (float) format.getImageableY(),
                    (float) format.getImageableWidth(), (float) format.getImageableHeight());
         }
         return renderer.render(g2, pageIndex);
      }

   }

   private class TablePageable implements Pageable
   {

      public int getNumberOfPages()
      {
         if (renderer == null) {
            return Pageable.UNKNOWN_NUMBER_OF_PAGES;
         }
         else {
            return renderer.getNumberOfPages();
         }
      }

      public java.awt.print.PageFormat getPageFormat(int pageIndex)
      {
         return format;
      }

      public java.awt.print.Printable getPrintable(int A)
      {
         return aPrintable;
      }

   }

}
