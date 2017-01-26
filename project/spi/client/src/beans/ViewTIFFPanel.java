/* $Header: /home/common/cvsarea/ibase/dia/src/beans/ViewTIFFPanel.java,v 1.13.8.1 2006/03/09 12:09:16 nancy Exp $ */

package beans;

import common.Log;

import com.acordex.vtj.ImageBean;
import java.awt.BorderLayout;
import java.awt.Point;

/**
 * This is a JPanel that holds the viewer for <code>ui.SplitPaneViewer</code>
 * and allows the image to be customized.
 */
public class ViewTIFFPanel extends javax.swing.JPanel {

    //final private static String license = "PNSQFQF";          // Set you license here
    //final private static String expires = "7/28/04";          // Set your expires date here
    final private static String license = "RKFPTKE";          // Set you license here
    final private static String expires = "12/31/9999";       // Set your expires date here

    final private static int ZOOM_FACTS[] = {200, 150, 125, 100, 80, 50, 40};

    //boolean appletActive = false;
    private ImageBean ourBean = new ImageBean();

    /**
     * Create an instance of ViewTIFFPanel using DIA defaults from
     * <code>com.acordex.vtj.ImageBean</code>.
     */
    public ViewTIFFPanel() {
        setLayout(new BorderLayout());
        add(ourBean, BorderLayout.CENTER);
        ourBean.setLicense(license, expires);
        ourBean.setRotation(0);
        ourBean.setGrayScale(true);
        ourBean.setCursorMode(ImageBean.kHandCursor);
        ourBean.setZoomFactor(ImageBean.kFitWindow);
        ourBean.setMagWinTitle("Magnify");
        ourBean.setDefaultMagWindowPos(new Point(0, 0), true);
    }

    public void zoomPercent(int percent) {
        if (percent <= 3) {
            percent = percent << 24;
        }
        ourBean.setZoomFactor(percent);
        ourBean.drawImage();
    }

    public void zoomFitWindow() { zoomPercent(3); }
    public void zoomFitWidth()  { zoomPercent(1); }
    public void zoomFitHeight() { zoomPercent(2); }
        
    public void zoomEnlarge()
    {
        int oldZoomFact = ourBean.getZoomPercent();
        int zoomFact = oldZoomFact;
        for (int i = ZOOM_FACTS.length - 1; i >= 0; i--) {
            if (ZOOM_FACTS[i] > oldZoomFact) {
                zoomFact = ZOOM_FACTS[i];
                break;
            }
        }
        zoomPercent(zoomFact);
    }

    public void zoomReduce()
    {
        int oldZoomFact = ourBean.getZoomPercent();
        int zoomFact = oldZoomFact;
        for (int i = 0; i < ZOOM_FACTS.length; i++) {
            if (ZOOM_FACTS[i] < oldZoomFact) {
                zoomFact = ZOOM_FACTS[i];
                break;
            }
        }
        zoomPercent(zoomFact);
    }

    public void rotateClockwise() {
        ourBean.changeRotation(-1);
        ourBean.drawImage();
    }
    
    public void rotateCounterClockwise() {
        ourBean.changeRotation(+1);
        ourBean.drawImage();
    }

    public void openMagWindow() {
        ourBean.setCursorMode(ImageBean.kMagnifyCursor);
        ourBean.showMagRect(0, 0, 320, 198, 2);
        ourBean.setCursorMode(ImageBean.kHandCursor);
    }

    public void hideMagRect() {
        ourBean.hideMagRect();
    }

    //public void cursorStyle(boolean handCursor)
    //{
    //    String cmd = "<CURSORSTYLE>" + (handCursor ? "HAND" : "RECT");
    //    ourApplet.setDocName(cmd);
    //}

    public boolean ViewFromSource(java.net.URL inputURL, java.io.InputStream is, byte[] imageBytes, int contentLength)
    {
        try {
            ourBean.setInputBuffer(imageBytes, imageBytes.length);
            ourBean.drawImage();
        } catch (Exception e) {
            docNotDrawn(e);
            return false;
        }
        return true;
    }

    /**
        Display errors if exception during document display
    */
    private void docNotDrawn(Exception e)
    {
        String reason = e.getMessage();

        String errStr = "Document not drawn";
        if (reason == null || reason.compareTo("Document not drawn") == 0
        || reason.indexOf("null") != -1) {
        } else {
            errStr = errStr + " (" + reason + ")";
        }
        Log.print(errStr);
    }
}

