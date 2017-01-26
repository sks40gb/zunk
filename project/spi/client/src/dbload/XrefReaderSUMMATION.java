/* $Header: /home/common/cvsarea/ibase/dia/src/dbload/XrefReaderSUMMATION.java,v 1.1.6.1 2006/01/10 15:28:16 nancy Exp $ */
package dbload;

import common.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;

/**
 * Implementation of cross-reference reader for SUMMATION files.
 */
public class XrefReaderSUMMATION extends XrefReader {

    private String volume = "";
    private String path = "";
    private String batesNumber = "";
    private char boundary = 'D';

    /**
     * Create a new XrefReaderSUMMATION for the given XrefAcceptor.
     */
    public XrefReaderSUMMATION(XrefAcceptor acceptor) {
        super(acceptor);
    }

    /**
     * Accept and process the given input line.
     */
    public void acceptLine(String line) {
        try {

            line = line.trim();

            // skip blank lines and comments
            if (line.length() == 0 || line.charAt(0) == ';') {
                return;
            }

            // look for token line
            if (line.charAt(0) == '@') {
                switch (line.charAt(1)) {
                case 'C':
                    // skip
                    break;
                case 'T':
                    // bates number
                    batesNumber = line.substring(2).trim();
                    // Since we have a new bates number given, must be doc boundary
                    boundary = 'D';
                    break;
                case 'D':
                    // default directory
                    String line2 = line.substring(2).trim();
                    char tokenAtSign = '\0';
                    char tokenLetter = '\0';
                    if (line2.length() >= 2) {
                        tokenAtSign = line2.charAt(0);
                        tokenLetter = line2.charAt(1);
                    }
                    if (tokenAtSign != '@'
                    || tokenLetter != 'I' && tokenLetter != 'V') {
                        throw new ErrorException("Expecting @I or @V after @D");
                    }
                    line2 = changeToForwardSlashes(line2.substring(2).trim());
                    int endVolumePos;
                    if (tokenLetter == 'D') {
                        endVolumePos = line2.indexOf(':');
                    } else {
                        if (line2.length() > 0 && line2.charAt(0) == '/') {
                            line2 = line2.substring(1);
                        }
                        endVolumePos = line2.indexOf('/');
                    }
                    if (endVolumePos < 0) {
                        throw new ErrorException("No volume delimiter");
                    }
                    volume = line2.substring(0, endVolumePos).trim();
                    line2 = line2.substring(endVolumePos + 1).trim();
                    if (line2.length() > 0 && line2.charAt(0) == '/') {
                        line2 = line2.substring(1);
                    }
                    path = line2;
                    break;
                default:
                    Log.print ("initial token @"+line.charAt(1));
                }
                return;
            }

            // line should be an image filename

            if (volume.length() == 0) {
                throw new ErrorException("No volume given");
            }
            if (path.length() == 0) {
                throw new ErrorException("No path given");
            }
            if (batesNumber.length() == 0) {
                throw new ErrorException("No Bates number given");
            }

            ImageXref data = new ImageXref();
            data.bates = batesNumber;
            data.volume = volume;
            data.originalVolume = volume;
            data.path = path;
            data.boundary = boundary;

            // set up for next time - clear boundary
            boundary = ' ';

            // set up for next time - increment Bates number, if possible
            // if overflow or no integer, clear Bates number
            int integerPos = batesNumber.length();
            while (integerPos > 0) {
                char ch = batesNumber.charAt(integerPos - 1);
                if (ch >= '0' && ch <= '9') {
                    integerPos--;
                } else {
                    break;
                }
            }
            if (integerPos < batesNumber.length()) {
                String tail = Integer.toString(
                        1 + Integer.parseInt(batesNumber.substring(integerPos)));
                if (integerPos + tail.length() < batesNumber.length()) {
                    batesNumber
                        = batesNumber.substring(0, batesNumber.length() - tail.length())
                          + tail;
                } else {
                    batesNumber = "";
                }
            } else {
                batesNumber = "";
            }

            // Accept the data.
            accept(data);

        } catch (ErrorException e) {
            error(e.getMessage());
        }
    }

    // change backslashes to forward slashes
    private String changeToForwardSlashes(String str) {
        StringBuffer buf = new StringBuffer(str);
        for (int i = 0; i < buf.length(); i++) {
            if (buf.charAt(i) == '\\') {
                buf.setCharAt(i, '/');
            }
        }
        return buf.toString();
    }
}
