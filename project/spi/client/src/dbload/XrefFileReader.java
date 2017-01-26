/* $Header: /home/common/cvsarea/ibase/dia/src/dbload/XrefFileReader.java,v 1.10.2.3 2007/03/27 14:40:53 nancy Exp $ */
package dbload;

import common.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Class to handle reading and parsing a cross-reference file from a file.
 * The read() method reads a file of the given type and either returns
 * a list of Xref objects or throws and exception with a list of error
 * message lines.
 */
public class XrefFileReader implements XrefConstants, XrefAcceptor {

    // list of Xrefs read by this reader
    ArrayList xrefList = new ArrayList();

    // error message lines
    ArrayList errorList = new ArrayList();

    // current line information
    int lineCount = 0;
    String line = null; // Note: not null after last line, still contains line
    boolean lineWrittenToerrorList = true; // don't write if no line ever read

    // Class may only be instantiated from within itself.
    private XrefFileReader() {}

    /**
     * Read and parse cross-reference file.
     * @param  sourcename The name of the source file
     * @param  xrefType The type of the source cross-reference file.  LFP, ...
     * @return an array of Xref's, with the information from the file
     * @throws XrefFileReader.XrefException if there are errors in the file
     */
    public static Xref[] read(String sourceName, int xrefType) throws XrefException {
        return (new XrefFileReader()).doRead(sourceName, xrefType);
    }

    // Instance method corresponding to read()
    private Xref[] doRead(String sourceName, int xrefType) throws XrefException {


        // Set up for correct cross-reference file
        // TBD: use reflection, so only one XrefReader gets loaded?
        XrefReader xreader = null;
        switch (xrefType) {
        case DOCULEX:
            xreader = new XrefReaderDOCULEX(this);
            break;
        case LFP:
            xreader = new XrefReaderLFP(this);
            break;
        case OPTICON:
            xreader = new XrefReaderOPTICON(this);
            break;
        case SUMMATION:
            xreader = new XrefReaderSUMMATION(this);
            break;
        case BRS:
            xreader = new XrefReaderBRS(this);
            break;
        default:
            Log.quit("Invalid cross-reference type");
        }

        try {
            // Open the input file
            BufferedReader freader
                    = new BufferedReader(xreader.makeFileReader(sourceName));

            // Collect Xref objects from input file
            String tempLine;
            while ((tempLine = freader.readLine()) != null) {
                line = tempLine;
                xreader.acceptLine(line);
            }
            freader.close();
        } catch (FileNotFoundException e) {
            Log.write("XrefFileReader: "+e);
            error("File not found: "+sourceName);
        } catch (IOException e) {
            Log.write("XrefFileReader: "+e);
            error("I/O error reading: "+sourceName);
            e.printStackTrace();
        }

        xreader.close();

        // Empty file is invalid
        //Xref[] result = (Xref[]) xrefList.toArray(new Xref[xrefList.size()]);
        if (xrefList.size() == 0) {
            error("Input file is empty");
            return null;
        }
        Xref[] result = (Xref[]) xrefList.toArray(new Xref[xrefList.size()]);

        if (xrefType == LFP) {
            //String volume = "";
            //String path = "";
            //String filename = "";
            // sort the results by image path and filename to check offsets
            Arrays.sort(result, 2, 5, new Comparator() {
                    public int compare(Object o1, Object o2) {
                        Xref left = (Xref) o1;
                        Xref right = (Xref) o2;
                        int result = left.bates.compareTo(right.bates);
                        if (result == 0) {
                            if (left instanceof ImageXref) {
                                result = (right instanceof ImageXref ? 0 : -1);
                            } else {
                                result = (right instanceof ImageXref ? 1 : 0);
                            }
                        }
                        return result;
                    }
                }); // by volume, path, filename
            Arrays.sort(result, 1, 2); // by offset (stable sort)

            int j = 0;
            for (int i = 0; i < result.length-1; i++) {
                //Log.print("XrefFileReader: " + i + "/" + result[i].toString());
                if (result[i] instanceof ImageXref
                    && ((ImageXref)result[i]).offset > 0) {
                    // multi-page image, check that offset starts with 0 or 1
                    if (((ImageXref)result[i]).volume.equals(((ImageXref)result[i+1]).volume)
                        && ((ImageXref)result[i]).path.equals(((ImageXref)result[i+1]).path)
                        && ((ImageXref)result[i]).fileName.equals(((ImageXref)result[i+1]).fileName) ) {
                        // first offset is not 0, subtract 1 from each offset in this tif
                        ((ImageXref)result[i]).offset--;
                        for (j = i+1; j < result.length; j++) {
                            if (((ImageXref)result[j]).volume.equals(((ImageXref)result[i]).volume)
                                && ((ImageXref)result[j]).path.equals(((ImageXref)result[i]).path)
                                && ((ImageXref)result[j]).fileName.equals(((ImageXref)result[i]).fileName) ) {
                                ((ImageXref)result[j]).offset--;
                            } else {
                                // set index for next tif
                                i = j-1;
                                break;
                            }
                        }
                    } else {
                        // single-page tiff that does not start with 0
                        ((ImageXref)result[i]).offset--;
                    }
                }
            }
            // last record
            if (((ImageXref)result[result.length-1]).offset > 0) {
                ((ImageXref)result[result.length-1]).offset--;
            }
        }

        // sort the results by bates number and type
        // Note: this comparator imposes orderings that are inconsistent with equals.
        Arrays.sort(result, new Comparator() {
                public int compare(Object o1, Object o2) {
                    Xref left = (Xref) o1;
                    Xref right = (Xref) o2;
                    int result = left.bates.compareTo(right.bates);
                    if (result == 0) {
                        if (left instanceof ImageXref) {
                            result = (right instanceof ImageXref ? 0 : -1);
                        } else {
                            result = (right instanceof ImageXref ? 1 : 0);
                        }
                    }
                    return result;
                }
            });

        // verify that there are images, but no duplicate image bates numbers
        // verify that issues correspond to bates numbers (may have duplicates)
        // only do this verification if there are not already errors detected
        if (errorList.size() == 0) {
            String lastBates = null;
            boolean imageSeen = false;
            for (int i = 0; i < result.length; i++) {
                Xref theXref = result[i];
                if (theXref instanceof ImageXref) {
                    imageSeen = true;
                    if (theXref.bates.equals(lastBates)) {
                        errorList.add(" ** Duplicate Bates number: "+lastBates);
                    } else {
                        lastBates = theXref.bates;
                    }
                } else { assert (theXref instanceof IssueXref);
                    if (! theXref.bates.equals(lastBates)) {
                        errorList.add(" ** Issue does not correspond to image: "
                                   +theXref.bates);
                    }
                }
            }
            if (! imageSeen) {
                errorList.add(" ** there are no image records");
            }
        }

        // check for errors
        if (errorList.size() > 0) {
            throw new XrefException(
                    (String[]) errorList.toArray(new String[errorList.size()]));
        }

        return (result);
    }

    ////////// implementation of XrefAcceptor interface


    /** Receive an Xref specification */
    public void accept(Xref spec) {
        xrefList.add(spec);
    }

    /** Receive an error message for the current source line */
    public void error(String message) {
        if (! lineWrittenToerrorList) {
            errorList.add("");
            errorList.add(leftPad(lineCount,6)+": "+line);
            lineWrittenToerrorList = true;
        }
        errorList.add(" -- "+message);
    }

    //////////

    // Pad an integer on the left with spaces
    // TBD should be in a string utility package
    private String leftPad(int n, int width) {
        String s = Integer.toString(n);
        StringBuffer b = new StringBuffer(width);
        for (int i = s.length(); i < width; i++) {
            b.append(' ');
        }
        b.append(s);
        return b.toString();
    }

    /**
     * Exception to return array of error messages if there are
     * errors in the cross-reference file.
     */
    public static class XrefException extends Exception {

        String[] errors;

        public XrefException(String[] errors) {
            this.errors = errors;
        }

        public String[] getErrors() {
            return errors;
        }
    }
}
