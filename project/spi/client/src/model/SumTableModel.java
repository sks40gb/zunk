/* $Header: /home/common/cvsarea/ibase/dia/src/model/SumTableModel.java,v 1.10.2.4 2006/03/22 20:27:15 nancy Exp $ */
package model;

import common.Log;

import java.awt.Component;
import java.math.BigDecimal;
import java.util.ArrayList;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * Used in DIA reporting, SumTableModel formats and performs calculations
 * on tableModel data.
 */
public class SumTableModel extends AbstractTableModel {

    // Flag values for table properties
    final private static int INTEGER = 1;
    final private static int SUM = 2;
    final private static int GROUP = 4;
    final private static int PERCENT = 8;
    final private static int DOLLAR = 16;

    // Magic values for row types.
    /** Row type for a detail line */
    final public static int DETAIL = -1;
    /** Row type for a blank line */
    final public static int BLANK = -2;

    //private TableColumnModel columnModel;
    private TableModel model;

    private int columnCount;            
    private int[] columnFlags;          // Flags for table properties; see above
    private String[] columnNames;       // names with code characters stripped
    private boolean sumSeen = false;    // true if any column is summed
    private int groupCount = 0;         // number of group columns (must be leading columns)

    private int[] rowType;              // row type.  see above for special values
                                        // 0 = final total, 1,2,... = subtotals
    private int[] indirect;             // underlying mode row OR total row in totals
    private String[][] totals;          // values for total rows

    final private char GROUP_COLUMN   = '@';   // indicates group column
    final private char SUM_COLUMN     = '+';   // indicates integer column with sums
    final private char PERCENT_COLUMN = '%';   // indicates percent of preceding sum column
    final private char NUMERIC_COLUMN = '#';
    final private char DOLLAR_COLUMN  = '$';  // indicates 3 decimal places with sum containing 2 decimal places

    /**
     * Create a new SumTableModel.
     * @param model The underlying TableModel
     */
    public SumTableModel(TableModel model) {
        //this.columnModel = columnModel; 
        this.model = model; 
        columnCount = model.getColumnCount();
        columnFlags = new int[columnCount];
        columnNames = new String[columnCount];

        // Scan columns for leading code characters
        // Set columnFlags and strip off code characters
        for (int c = 0; c < columnCount; c++ ) {
            columnNames[c] = model.getColumnName(c);
            switch (columnNames[c].charAt(0)) {
            case GROUP_COLUMN:   // indicates group column
                assert groupCount == c;
                groupCount++;
                assert groupCount <= 2;
                columnFlags[c] |= GROUP;
                columnNames[c] = columnNames[c].substring(1);
                break;
            case SUM_COLUMN:   // indicates integer column with sums
                sumSeen = true;
                columnFlags[c] |= SUM | INTEGER;
                columnNames[c] = columnNames[c].substring(1);
                break;
            case PERCENT_COLUMN:
                assert sumSeen;
                columnFlags[c] |= SUM | INTEGER | PERCENT ;
                columnNames[c] = columnNames[c].substring(1);
                break;
            case NUMERIC_COLUMN:
                columnFlags[c] |= INTEGER;
                columnNames[c] = columnNames[c].substring(1);
                break;
            case DOLLAR_COLUMN:
                sumSeen = true;
                columnFlags[c] |= SUM | DOLLAR;
                columnNames[c] = columnNames[c].substring(1);
                break;
            }
        }

        ArrayList holdRowType = new ArrayList(model.getRowCount() + 50);
        String[] lastGroup = null;     // last group value seen; used to find breaks
        int totalCount = 0;            // number of total lines; used as dimension of totals array
        if (groupCount > 0) {
            lastGroup = new String[groupCount];
        }
        // scan rows looking for report breaks
        for (int r = 0; r < model.getRowCount(); r++) {
            int groupBreak = 0;
            for (int c = groupCount - 1; c >= 0; c--) {
                String value = (String) model.getValueAt(r,c);
                if (! value.equals(lastGroup[c])) {
                    if (lastGroup[c] != null) {
                        groupBreak = c + 1;
                    }
                    lastGroup[c] = value;
                }
            }
            if (groupBreak > 0) {
                // There is a report break
                if (sumSeen) {
                    for (int j = groupCount; j >= groupBreak; j--) {
                        // add a total line
                        holdRowType.add(new Integer(j));
                        totalCount++;
                    }
                }
                // add a blank line after the break totals
                holdRowType.add(new Integer(BLANK));
            }
            // add detail row type
            holdRowType.add(new Integer(DETAIL));
        }

        if (sumSeen) {
            for (int j = groupCount; j >= 1; j--) {
                // add a total line
                holdRowType.add(new Integer(j));
                totalCount++;
            }
            // add a blank line after last break and subtotals
            holdRowType.add(new Integer(BLANK));
            // add a grand total line
            holdRowType.add(new Integer(0));
            totalCount++;
        }

        // create and fill in rowType, totals and indirect arrays
        rowType = new int[holdRowType.size()];
        indirect = new int[rowType.length];
        if (sumSeen) {
            totals = new String[totalCount + 1][columnCount];
        }

        int dx = -1;   // index of row in underlying model
        int sx = -1;   // index of total row in totals array
        for (int i = 0; i < rowType.length; i++) {
            rowType[i] = ((Integer) holdRowType.get(i)).intValue();
            if (rowType[i] == DETAIL) {
                dx++;
                indirect[i] = dx;
            } else if (rowType[i] >= 0) {
                sx++;
                indirect[i] = sx;
                if (rowType[i] == 0 && groupCount > 1) {
                    totals[sx][0] = "Grand Total:";
                } else if (rowType[i] == groupCount && groupCount != 0) {
                    totals[sx][0] = "Subtotal:";
                } else {
                    totals[sx][0] = "Total:";
                }
            }
        }

        if (sumSeen) {
            int lastSumColumn = -1;
            for (int c = 0; c < columnCount; c++) {
                if ((columnFlags[c] & SUM) != 0) {
                    Object[] holdSum = new Object[groupCount+1];
                    for (int i = 0; i < rowType.length; i++) {
                        if (rowType[i] == DETAIL) {
                            for (int j = 0; j < holdSum.length; j++) {
                                if (holdSum[j] == null) {
                                    holdSum[j] = "0";
                                }
                                //Log.print("(SumTableModel) raw value " + c + "/" + columnFlags[c]);
                                if ((columnFlags[c] & INTEGER) != 0) {
                                    holdSum[j] = Integer.toString(Integer.parseInt((String)holdSum[j])
                                                                  + getInt(getRawValueAt(i, c)));
                                } else if ((columnFlags[c] & DOLLAR) != 0) {
                                    holdSum[j] = (new BigDecimal((String)holdSum[j])).add(
                                        getBigDecimal(getRawValueAt(i, c))).toString();
                                }
                            }
                        } else if (rowType[i] >= 0) {
                            if ((columnFlags[c] & INTEGER) != 0) {
                                totals[indirect[i]][c] = (String)holdSum[rowType[i]];
                            } else if ((columnFlags[c] & DOLLAR) != 0
                                       && holdSum[rowType[i]] != null) {
                                //BigDecimal bd = (new BigDecimal((String)holdSum[rowType[i]])).divide(
                                //    new BigDecimal("100"),3,BigDecimal.ROUND_HALF_UP);
                                // (fix for java 1.5)
                                //totals[indirect[i]][c] = (new BigDecimal((String)holdSum[rowType[i]])).divide(
                                //    new BigDecimal(1),2,BigDecimal.ROUND_HALF_UP).toString();
                                totals[indirect[i]][c] = (new BigDecimal((String)holdSum[rowType[i]])).divide(
                                    new BigDecimal("1"),2,BigDecimal.ROUND_HALF_UP).toString();
                            }
                            holdSum[rowType[i]] = "0";
                        }
                    }
                }
            }
        }
    }

    /**
     * Compute and set preferred column widths for a given table.
     * The preferred widths are computed as the maxima of the
     * preferred widths for the column headers and cells.
     * This method may be called for a table with any TableModel;
     * it is not limited to those with SumTableModel.
     */
    public static void computeColumnWidths(JTable table) {
        TableModel model = table.getModel();
        TableColumnModel columnModel = table.getColumnModel();
        for (int c = 0; c < model.getColumnCount(); c++) {
            TableColumn column = columnModel.getColumn(c);
            TableCellRenderer renderer = column.getHeaderRenderer();
            if (renderer == null) {
                renderer = table.getDefaultRenderer(String.class);
            }
            int width = calculateWidth(table, renderer, column.getHeaderValue()) + 4; // allow for border
            renderer = column.getCellRenderer();
            if (renderer == null) {
                renderer = table.getDefaultRenderer(table.getColumnClass(c));
            }
            for (int r = 0; r < model.getRowCount(); r++) {
                width = Math.max(width, calculateWidth(
                        table, renderer, model.getValueAt(r,c)));
            }
            column.setPreferredWidth(width + 1); // allow for roundoff
        }
    }

    // calculate the preferred width for a value.  Called by computeColumnWidths.
    private static int calculateWidth(JTable table, TableCellRenderer renderer, Object value) {
        if (value == null) {
            return 0;
        }
        Component cell = renderer.getTableCellRendererComponent(
                                                          table,    // table
                                                          value,    // value
                                                          false,    // isSelected
                                                          false,    // hasFocus
                                                          0,        // row
                                                          0);       // column
        return (int) cell.getPreferredSize().getWidth();
    }


    /**
     * Return the type of a row.  May be DETAIL, BLANK, 0 for
     * grand total, or 1,2,... for subtotal.
     */
    public int getRowType(int rowIndex) {
        return rowType[rowIndex];
    }


    ////////// Overrides and implementation of TableModel interface

    /**
     * Returns the number of rows in the model.
     */
    public int getRowCount() {
        return rowType.length;
    }

    /**
     * Returns the number of columns in the model.
     */
    public int getColumnCount() {
        return model.getColumnCount();
    }

    /**
     * TableModel interface method to return the value at
     * a given position.
     * @param rowIndex the horizontal position
     * @param columnIndex the vertical position
     * @return the requested value, formatted according to the class of
     * the object in the given position
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object result = getRawValueAt(rowIndex, columnIndex);
        if (result == null || (columnFlags[columnIndex] & PERCENT) == 0) {
            return result;
        } else {
            // calculate percent, using preceding sum column as divisor
            int sumColumn = -1;
            for (int j = columnIndex; j >= 0; j--) {
                if ((columnFlags[j] & (SUM | PERCENT)) == SUM) {
                    sumColumn = j;
                    break;
                }
            }
            Object obj = getRawValueAt(rowIndex, sumColumn);
            if (obj instanceof BigDecimal) {
                BigDecimal divisor = getBigDecimal(obj);
                //Log.print("(SumTableModel.getValueAt%) result/divisor " + result
                //          + "/" + divisor);
                if (divisor.equals(new BigDecimal("0.00"))) {
                    return null;
                } else {
                    double resultDouble = 0;
                    if (result instanceof BigDecimal) {
                        resultDouble = ((BigDecimal)result).doubleValue();
                    } else {
                        resultDouble = ((Integer) result).doubleValue();
                    }
                    Float fl = new Float(
                        (( resultDouble * 1000 
                        + divisor.doubleValue() - 1)
                        / divisor.doubleValue())
                        / ((float) 10.0));
                    fl = (fl.toString().equals("-Infinity")) ? 0 : fl;                  
                    BigDecimal bd = new BigDecimal(fl.doubleValue());                    
                    fl = new Float(bd.setScale(1, BigDecimal.ROUND_UP).floatValue());
                    //Log.print("(SumTableModel.getValueAt%) return " + fl);
                    return fl;
                }
            } else {
                int divisor = getInt(obj);
                if (divisor == 0) {
                    return null;
                } else {
                    //// Round to nearest percent.  0 rounds to 0 and 100 to 100
                    //return new Integer(
                    //        (((Integer) result).intValue() * 200 + divisor) / (divisor * 2));
                    // Round to next higher tenth of a percent.  0 rounds to 0 and 100 to 100
                    //Log.print("(STM.getValueAt) class: " + result.getClass() + "/"
                    //          + result + "/" + divisor);
                    Float result2 = new Float(
                            ((((Integer) result).intValue() * 1000 + divisor - 1) / divisor)
                            / ((float) 10.0));
                    return result2;
                }
            }
        }
    }

    /**
     * Get the value at the given row and column, before calculation
     * of percentages.
     */
    private Object getRawValueAt(int rowIndex, int columnIndex) {
        String result;
        if (rowType[rowIndex] == DETAIL) {
            result = (String) model.getValueAt(indirect[rowIndex], columnIndex);
            //Log.print("(STM.getRawValueAt) returning detail " + result);
        } else if (rowType[rowIndex] >= 0) {
            result = totals[indirect[rowIndex]][columnIndex];
            //Log.print("(STM.getRawValueAt) returning >= 0 " + result);
        } else {
            //Log.print("(STM.getRawValueAt) returning null");
            return result = null;
        }
        if (result == null) {
            return null;
        }
        if ((columnFlags[columnIndex] & INTEGER) != 0) {
            //Log.print("(STM.getRawValueAt) returning Integer " + result);
            // could have a decimal
            if (result.indexOf(".") < 0) {
                return new Integer(Integer.parseInt(result));
            } else {
                return new BigDecimal(result);
            }
        } else if ((columnFlags[columnIndex] & DOLLAR) != 0) {
            //Log.print("(STM.getRawValueAt) returning Dollar " + result);
            return new BigDecimal(result);
        }
        return result;
    }

    private int getInt(Object o) {
        return getBigDecimal(o).intValue();
    }

    private BigDecimal getBigDecimal(Object o) {
        BigDecimal bd;
        if (o == null) {
            // (fix for java 1.5)
            //bd = new BigDecimal(0);
            bd = new BigDecimal("0");
        } else if (o instanceof Integer) {
            bd = new BigDecimal(Integer.toString(((Integer)o).intValue()));
        } else if (o instanceof BigDecimal) {
            bd = ((BigDecimal)o);
        } else {
            // string
            bd = new BigDecimal((String)o);
        }
        return bd;
    }

    /**
     * Class not supported.
     */
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        throw new UnsupportedOperationException();
    }

    /**
     * Return the name of the column at the given position.
     * @param columnIndex position of the requested name
     * @return String column name
     */
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    /**
     * Get the Class of the data at the given position.
     * @param columnIndex position of the requested name
     */
    public Class getColumnClass(int columnIndex) {
        Class result = ((columnFlags[columnIndex] & PERCENT) != 0
                        || (columnFlags[columnIndex] & DOLLAR) != 0 ? Float.class
               :(columnFlags[columnIndex] & INTEGER) != 0 ? Integer.class
               :                                            String.class);
        return result;
    }
}
