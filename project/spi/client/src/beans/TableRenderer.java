package beans;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.font.TextLayout;
import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import model.SumTableModel;

/**
 * Render the tables associated with DIA reporting.  Take care of formatting
 * dates and getting the columns spacing right for the data to be viewed.
 */
public class TableRenderer {

    final private float MIN_SPACING = 6;
    final private float MAX_SPACING = 54;
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private final String formattedDate = DATE_FORMAT.format(new Date());
    private SumTableModel model;
    private Graphics2D g2;
    private String title;
    private String[] paramNames;
    private String[] paramDisplay;
    private float imageableX;
    private float imageableY;
    private float imageableWidth;
    private float imageableHeight;
    private float[] maxColumnWidth;
    private float[] columnX;
    private float maxY;
    private float currentY;
    private ArrayList pageStartRow;
    private FontRenderContext frc;
    private Font normalFont;
    private Font boldFont;
    private Font largeFont;
    private LineMetrics normalMetrics;
    private LineMetrics boldMetrics;
    private LineMetrics largeMetrics;
    private boolean initialize;
    private int row;

    /**
     * Create a new instance of TableRenderer.
     * @param model the <code>model.SumTableModel</code> that contains the
     * data to be viewed and it's properties
     * @param g2 a <code>Graphics2D</code> object derived from SumTableModel
     * @param title the title of the report
     * @param paramNames a String array of the reporting parameter names
     * @param paramDisplay a String array of the reporting parameter values
     * @param imageableX 
     * @param imageableY
     * @param imageableWidth
     * @param imageableHeight
     */
    public TableRenderer(SumTableModel model, Graphics2D g2,
            String title, String[] paramNames, String[] paramDisplay,
            float imageableX, float imageableY,
            float imageableWidth, float imageableHeight) {
        this.model = model;
        this.g2 = g2;
        this.title = title;
        this.paramNames = paramNames;
        this.paramDisplay = paramDisplay;
        this.imageableX = imageableX;
        this.imageableY = imageableY;
        this.imageableWidth = imageableWidth;
        this.imageableHeight = imageableHeight;
        normalFont = g2.getFont().deriveFont(Font.PLAIN, 9);
        boldFont = normalFont.deriveFont(Font.BOLD, normalFont.getSize());
        largeFont = normalFont.deriveFont(Font.BOLD, 2 + normalFont.getSize());
        frc = g2.getFontRenderContext();
        normalMetrics = normalFont.getLineMetrics("Abcdefg", frc);
        boldMetrics = boldFont.getLineMetrics("Abcdefg", frc);
        largeMetrics = largeFont.getLineMetrics("Abcdefg", frc);

        maxColumnWidth = new float[model.getColumnCount()];
        columnX = new float[maxColumnWidth.length];
        pageStartRow = new ArrayList();

        maxY = imageableY + imageableHeight - normalMetrics.getDescent();

        // initialize the pages
        for (int i = 0;; i++) {
            if (render(i, true) == Printable.NO_SUCH_PAGE) {
                break;
            }
        }
        float totalWidth = 0;
        for (int j = 0; j < maxColumnWidth.length; j++) {
            totalWidth += maxColumnWidth[j];
        }
        columnX[0] = imageableX;
        float spacing;
        if (columnX.length <= 1) {
            spacing = MIN_SPACING;
        } else {
            spacing = Math.max(MIN_SPACING,
                    (imageableWidth - totalWidth) / (columnX.length - 1));
            if (spacing > MAX_SPACING) {
                spacing = MAX_SPACING;
                columnX[0] = (imageableWidth - totalWidth - MAX_SPACING * (columnX.length - 1)) / 2 + imageableX;
            }
        }
        for (int i = 1; i < columnX.length; i++) {
            columnX[i] = columnX[i - 1] + spacing + maxColumnWidth[i - 1];
        }
    }

    /**
     * Call render() to format a single page of the report.
     * @param g2
     * @param page the page number of the page that is to be formatted
     */
    public int render(Graphics2D g2, int page) {
        this.g2 = g2;
        return render(page, false);
    }

    /**
     * Format a single page of the report.
     * @param page the page number of the page that is to be formatted
     * @param initialize 
     */
    private int render(int page, boolean initialize) {

        this.initialize = initialize;

        currentY = imageableY + largeMetrics.getAscent();

        if (page == 0) {
            row = 0;
            if (initialize) {
                pageStartRow.add(new Integer(row));
            }
        } else {
            if (page >= pageStartRow.size()) {
                return Printable.NO_SUCH_PAGE;
            }
            row = ((Integer) pageStartRow.get(page)).intValue();
        }

        // print the title
        TextLayout aLayout;
        g2.setFont(largeFont);
        draw(title, imageableX);
        g2.setFont(normalFont);
        if (!initialize) {
            String dateAndPage = formattedDate + "  Page " + (page + 1) + " of " + getNumberOfPages();
            aLayout = layout(dateAndPage);
            draw(aLayout, imageableX + imageableWidth - aLayout.getAdvance());
        }
        currentY += largeMetrics.getHeight() + normalMetrics.getHeight();

        // on first page, print the parameters
        if (page == 0 & paramNames != null && paramNames.length > 0) {
            for (int i = 0; i < paramNames.length; i++) {
                draw(paramNames[i] + ": " + paramDisplay[i], imageableX);
                currentY += normalMetrics.getHeight();
            }
            currentY += normalMetrics.getHeight();
        }

        // print column headings
        g2.setFont(boldFont);
        for (int i = 0; i < columnX.length; i++) {
            aLayout = layout(model.getColumnName(i));
            if (initialize) {
                maxColumnWidth[i] = aLayout.getAdvance();
            } else {
                // center the title
                float newX = columnX[i] + Math.max(0, (maxColumnWidth[i] - aLayout.getAdvance()) / 2);
                draw(aLayout, newX);
            }
        }
        currentY += normalMetrics.getHeight() + boldMetrics.getHeight();

        g2.setFont(normalFont);

        // if no data, say so and return
        int rowCount = model.getRowCount();
        if (rowCount == 0) {
            draw("***** No data for report *****", columnX[0]);
            return Printable.PAGE_EXISTS;
        }

        // print rows on this page
        for (int i = row; i < rowCount; i++) {
            // check that row fits
            if (currentY >= maxY && model.getRowType(i) != SumTableModel.BLANK) {
                // doesn't fit; save beginning row and return
                if (initialize) {
                    pageStartRow.add(new Integer(i));
                }
                return Printable.PAGE_EXISTS;
            }
            // fits, print it
            if (model.getRowType(i) != SumTableModel.BLANK) {
                for (int j = 0; j < columnX.length; j++) {
                    Object value = model.getValueAt(i, j);
                    aLayout = value == null ? null : layout(value.toString());
                    if (aLayout == null) {
                    } else if (initialize) {
                        float newMaxColumnWidth = Math.max(maxColumnWidth[j], aLayout.getAdvance());
                        maxColumnWidth[j] = newMaxColumnWidth;
                    } else {
                        // right-justify numerics
                        float newX = columnX[j];
                        if (value instanceof Number) {
                            newX += maxColumnWidth[j] - aLayout.getAdvance();
                        }
                        draw(aLayout, newX);
                    }
                }
            }
            currentY += normalMetrics.getHeight();
        }
        return Printable.PAGE_EXISTS;
    }

    private void draw(String text, float currentX) {
        draw(layout(text), currentX);
    }

    private void draw(TextLayout layout, float currentX) {
        if (!initialize && layout != null) {
            layout.draw(g2, currentX, currentY);
        }
    }

    private TextLayout layout(String text) {
        if (text == null | text.length() == 0) {
            return null;
        } else {
            return new TextLayout(text, g2.getFont(), frc);
        }
    }

    /**
     * Return the number of pages, which is the size of
     * <code>pageStartRow</code>, which holds the starting row index
     * for every page.
     * @return the number of pages
     */
    public int getNumberOfPages() {
        if (initialize) {
            return Pageable.UNKNOWN_NUMBER_OF_PAGES;
        } else {
            return pageStartRow.size();
        }
    }
}



