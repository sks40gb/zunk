/* $Header: /home/common/cvsarea/ibase/dia/src/common/msg/MessageWriter.java,v 1.23.2.2 2005/11/17 14:54:43 nancy Exp $ */
package com.fossa.servlet.common.msg;

import com.fossa.servlet.common.Log;
import com.fossa.servlet.common.MessageConstants;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Provides methods for writing a message to the server/client socket stream.
 */
public class MessageWriter implements MessageConstants {

    final private boolean DEBUG = false;
    public boolean hasAppendOk = true;
    private OutputStream out;
    private Writer writer;
    private DataOutputStream dataStream;
    private boolean isElementOpen = false;
    private boolean isSubElementSeen = false;
    private List elementStack = new ArrayList();
    private StringBuffer cache;
    private static Logger logger = Logger.getLogger("com.fossa.servlet.common.msg");
    /**
     * Create a new MessageWriter from an existing OutputStream.
     * The OutputStream is closed when the MessageWriter is closed
     * (since only one XML document gets read from a Reader).
     * @param out An open OutputStream.
     */

    public MessageWriter(final OutputStream out, String dtd) throws IOException {
        cache = new StringBuffer();
        this.out = out;
        //// debug -- print the XML
        //this.out= new OutputStream() {
        //        byte[] buffer = new byte[1];
        //        public void write(int A) throws IOException {
        //            buffer[0] = (byte) A;
        //            write(buffer,0,1);
        //        }
        //        public void write(byte[] A) throws IOException {
        //            write(A,0,A.length);
        //        }
        //        public void write(byte[] A, int B, int C) throws IOException {
        //            System.out.print(new String(A,B,C));
        //            out.write(A,B,C);
        //        }
        //        public void flush() throws IOException {
        //            out.flush();
        //        }
        //        public void close() throws IOException {
        //            out.close();
        //        }
        //    };
        writer = new BufferedWriter(new OutputStreamWriter(this.out, MessageConstants.UTF_8));
        //debug only
        cache.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        //debug only

        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        if (dtd != null) {
            //debug only
            cache.append("<!DOCTYPE message SYSTEM \"" + dtd + "\" >\n");
            //debug only
            writer.write("<!DOCTYPE message SYSTEM \"" + dtd + "\" >\n");
        }
        startElement(T_MESSAGE);
    }

    /**
     * Write message postamble and close the message substream.
     * <p>Note.  writer may be closed more than once.  (This allows it to be closed
     * before starting a data stream, but still handle the automatic close
     * from the ServerTask's main loop.)
     */
    public void close() throws IOException {
        if (DEBUG) {
            System.out.println("MessageWriter.close");
        }

        // if the writer is still open, we finish the element
        // otherwise (writer == null) we've already closed it to create a data stream
        if (writer != null) {
            endElement();
            assert elementStack.size() == 0 : "start/end element mismatch writing message";
            //For debug purpose : prints the server-side XML
            System.out.println("serverSide.........." + cache.toString());
            writer.close();
            //debug only
            cache = null;
            //debug only

            writer = null;
        }
    }

    /**
     * Write element start tag.  Element names are stacked, so that endElement will
     * match the currently open element.  Writing the closing bracket of the
     * start tag is deferred, to allow for writing attributes.
     * @param element The element tag.
     */
    public void startElement(String element) throws IOException {
        //System.out.println("inside MessageWriter.startElement method"+    element);
        if (DEBUG) {
        }

        if (hasAppendOk) { //For the first time this will be true
            if (!element.equalsIgnoreCase(T_MESSAGE)) { //Ignore this if this is the start of a response message
                this.hasAppendOk = false;
            }
        }

        if (isElementOpen) {
            //debug only
            cache.append(">\n");
            //debug only            
            writer.write(">\n");
        }
        startLine("<" + element);
        isElementOpen = true;
        isSubElementSeen = false;
        elementStack.add(element);
    }

    /**
     * Write the closing tag for the current element.
     */
    public void endElement() throws IOException {
        //System.out.println("inside MessageWriter.endElement method");
        if (DEBUG) {
            System.out.println("MessageWriter.endElement");
        }
        //System.out.println("endElement"+  elementStack.get(elementStack.size() - 1));
        String element = (String) elementStack.get(elementStack.size() - 1);

        elementStack.remove(elementStack.size() - 1);
        if (isElementOpen) {
            isElementOpen = false;
            //debug only
            cache.append("/>\n");
            //debug only

            writer.write("/>\n");
        } else if (isSubElementSeen) {
            startLine("</" + element + ">");

            //debug only
            cache.append("\n");
            //debug only            
            writer.write("\n");
        } else {
            //debug only
            cache.append("</" + element + ">\n");
            //debug only

            writer.write("</" + element + ">\n");
        }
        isSubElementSeen = true;

    }

    /**
     * Write an attribute and its value.  Attribute names and values are assumed
     * not to require escaping of special XML characters.
     * @param name      Attribute name.
     * @param value     Atribute value.
     */
    public void writeAttribute(String name, String value) throws IOException {
        assert isElementOpen : "Element not open when writing attribute.";
        //debug only
        cache.append(" " + name + "=\"" + escapeString(value) + "\"");
        //debug only

        writer.write(" " + name + "=\"" + escapeString(value) + "\"");
    }

    /**
     * Write an attribute and its value, where the
     *   value is given as an integer.  Attribute names are assumed
     *   not to require escaping of special XML characters.
     * @param name      Attribute name.
     * @param value     Atribute value.
     */
    public void writeAttribute(String name, int value) throws IOException {

        assert isElementOpen : "Element not open when writing attribute.";
        //debug only
        cache.append(" " + name + "=\"" + value + "\"");
        //debug only

        writer.write(" " + name + "=\"" + value + "\"");
    }

    /**
     * Writes content for the current element.  May be called more than once; content just
     * appends.  Escapes the reserved XML characters, if any.
     * @param content The content to be written.
     */
    public void writeContent(String content) throws IOException {
        String str = escapeString(content);
        if (str.length() > 0 && isElementOpen) {
            isElementOpen = false;
            //debug only
            cache.append(">");
            cache.append(str);
            //debug only

            writer.write(">");
            writer.write(str);
        }
    }

    /**
     * Escapes the reserved XML characters in the given string, if any.
     * @param the string to be checked for reserved characters
     * @return the string with reserved characters escaped
     */
    private String escapeString(String str) {
        StringBuffer buffer = null;
        int startIndex = 0;
        char ch;
        for (int i = 0; i < str.length(); i++) {
            ch = str.charAt(i);
            if (ch <= MAX_CHAR && charTable[(int) ch] != null) {
                if (buffer == null) {
                    buffer = new StringBuffer();
                }
                buffer.append(str.substring(startIndex, i));
                buffer.append(charTable[(int) ch]);
                startIndex = i + 1;
            }
        }
        if (buffer != null) {
            buffer.append(str.substring(startIndex));
            str = buffer.toString();
        }
        return str;
    }


    /**
     * Writes an integer or long as content for the current element.
     * May be called more than once; content just appends.
     * @param content The content to be written.
     */
    public void writeContent(long content) throws IOException {
        if (isElementOpen) {
            isElementOpen = false;
            //debug only
            cache.append(">");
            //debug only
            writer.write(">");
        }

        //debug only
        cache.append(Long.toString(content));
        //debug only

        writer.write(Long.toString(content));
    }

    /**
     * Writes a character array as content for the current element.
     * May be called more than once; content just
     * appends.  Escapes the reserved XML characters, if any.
     * @param content The content to be written.
     */
    public void writePassword(char[] content) throws IOException {
        char ch;
        if (isElementOpen) {
            isElementOpen = false;
            //debug only
            cache.append(">");
            //debug only

            writer.write(">");
        }
        //debugMessage.append("************");
        for (int i = 0; i < content.length; i++) {
            ch = content[i];
            if (ch <= MAX_CHAR && charTable[(int) ch] != null) {
                char[] temp = charTable[(int) ch].toCharArray();
                for (int j = 0; j < temp.length; j++) {
                    //debug only
                    cache.append(temp[j]);
                    //debug only

                    writer.write(temp[j]);
                    temp[j] = '\u0000';
                }
            } else {
                //debug only
                cache.append(ch);
                //debug only
                writer.write(ch);
            }
        }
    }

    // Indent and start output line

    private void startLine(String text) throws IOException {
        if (null == cache) {// hack to fix NullPointerException. Happens when the client starts 'Other Activity' like break.
            cache = new StringBuffer();
        }
        for (int i = elementStack.size(); i > 0; i--) {
            //debug only
            cache.append("  ");
            //debug only
            writer.write("  ");
        }
        //debug only
        cache.append(text);

        writer.write(text);
    }
    // Construct table of XML special characters and their replacements

    final private static int MAX_CHAR = Math.max('&',
            Math.max('<',
            Math.max('>',
            Math.max('\r',
            Math.max('"',
            '\'')))));
    final static String[] charTable = new String[MAX_CHAR + 1];
     {
        charTable[(int) '&'] = "&amp;";
        charTable[(int) '<'] = "&lt;";
        charTable[(int) '\r'] = "&#13;";
        charTable[(int) '>'] = "&gt;";
        charTable[(int) '"'] = "&quot;";
        charTable[(int) '\''] = "&apos;";
    }


    /**
     * Write the public variables of a class to the XML stream.
     * Writes a series of <value> elements, one for each variable.
     * <p>
     * Note: Only variables of type String, int or boolean are written.
     * (CodingData contains a HashMap, which is written by another routine.)
     * @param theClass  The class whose public elements are to be used.
     * @param obj       The object whose values are to be used.
     *      obj must be a subclass of theClass.  theClass is specified,
     *      because obj may have more fields than theClass.
     * @throws IOException
     */
    public void encode(Class theClass, Object obj) throws IOException {
        //System.out.println("inside encode block........ ");

        Field[] fields = theClass.getFields();
        for (int i = 0; i < fields.length; i++) {
            Class fieldType = fields[i].getType();
            if (fieldType == String.class || fieldType.getName().equals("int") || fieldType.getName().equals("boolean")) {
                startElement(T_VALUE);
                //Log.print("(MessageWriter.encode) name " + fields[i].getName());                
                writeAttribute(A_NAME, fields[i].getName());
                try {
                    if(fields[i].get(obj) == null){
                      writeContent("");
                   }else{
                        writeContent(fields[i].get(obj).toString());
                   }
                } catch (IllegalAccessException e) {
                    logger.error("Exception while accessing the message dtd file." + e);
                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw));
                    logger.error(sw.toString());
                    Log.quit(e);
                }
                endElement();
            }
        }
    }

    public String toString() {
        return cache.toString();
    }

    public boolean getHasAppendOk() {
        return hasAppendOk;
    }

    public void setHasAppendOk(boolean hasAppendOk) {
        this.hasAppendOk = hasAppendOk;
    }
}
