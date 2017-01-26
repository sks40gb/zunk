/* $Header: /home/common/cvsarea/ibase/dia/src/ui/TableExporter.java,v 1.2.6.1 2006/03/29 13:54:20 nancy Exp $ */
package ui;

import beans.ExampleFileFilter;
import common.Log;
import model.SumTableModel;

import java.awt.Component;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.TableModel;

/**
 * From </code>ui.TableViewer</code>, <code>TableExporter</code> is called to format
 * and write to a file a TableModel of data.  Used to export report data.
 * @see TableViewer
 */
public class TableExporter {

    private TableExporter() {}

    /**
     * Call doExport to export the given model to a file chosen by the user.
     * @param parent Component used in the positioning of the JFileChooser
     * @param model the TableModel containing the data to export
     */
    public static void export(Component parent, TableModel model) {

        JFileChooser chooser = new JFileChooser();
        // Note: source for ExampleFileFilter can be found in FileChooserDemo,
        // under the demo/jfc directory in the Java 2 SDK, Standard Edition.
        ExampleFileFilter filter = new ExampleFileFilter();
        filter.addExtension("csv");
        filter.setDescription("Comma-delimited text");
        chooser.addChoosableFileFilter(filter);
        chooser.setFileFilter(filter);
        int returnVal = chooser.showSaveDialog(parent);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
           File selected = chooser.getSelectedFile();
           System.out.println("Selected: " + selected.getName());
           if (selected.exists()) {
               int answer = JOptionPane.showConfirmDialog(parent, 
                                             "File "+selected+" exists"
                                             +"\n Overwrite?",
                                             "Confirm", 
                                             JOptionPane.YES_NO_OPTION);
               if (answer != JOptionPane.YES_OPTION) {
                   return;
               }
           }
           
           try {
               Log.print("calling doExport");
               doExport(model, selected);
           } catch (IOException e) {
                JOptionPane.showConfirmDialog(parent, 
                                             "Error writing "+selected+":"
                                              +"\n"+e,
                                             "Error",
                                              JOptionPane.ERROR_MESSAGE);
           }
        }
    }
    
    private static void doExport(TableModel model, File selected) throws IOException {
        PrintWriter writer = new PrintWriter(new FileWriter(selected));
        
        SumTableModel stm = null;
        if (model instanceof SumTableModel) {
            stm = (SumTableModel) model;
        }
        
        for (int r = 0; r < model.getRowCount(); r++) {
            if (stm == null || stm.getRowType(r) == SumTableModel.DETAIL) {
                StringBuffer buffer = new StringBuffer();
                for (int c = 0; c < model.getColumnCount(); c++) {
                    buffer.append(',');
                    Object value = model.getValueAt(r,c);
		    if (value == null) {
                        // nothing to emit
                    } else if (value instanceof Number) {
                        buffer.append(value.toString());
                    } else {
                        String stringValue = value.toString();
                        buffer.append('"');
                        int quoteCol;
                        while ((quoteCol = stringValue.indexOf('"')) >= 0) {
                            buffer.append(stringValue.substring(0,quoteCol - 1));
                            buffer.append("\"\"");
                            stringValue = stringValue.substring(quoteCol + 1);
                        }
                        buffer.append(stringValue);
                        buffer.append('"');
                    }
                }
                writer.println(buffer.substring(1));
            }
        }
        writer.close();
    }
}
