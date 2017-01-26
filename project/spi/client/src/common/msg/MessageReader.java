/* $Header: /home/common/cvsarea/ibase/dia/src/common/msg/MessageReader.java,v 1.18.8.2 2007/02/26 11:16:35 nancy Exp $ */
package common.msg;

import common.CommonProperties;
import com.lexpar.util.Log;


import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
//import java.lang.reflect.Modifier;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Provides methods for reading a message.
 * <p>
 * TBD: We have assumed that, for now, a message will be read as an
 * XML Document Object Model (DOM), as an easy way of
 * getting a tree structure to walk.  We may, depending on observed timing,
 * what to provide an application-specific structure and/or a specialized
 * XML parser.
 * <p>
 * Design note:  We could have provided just a static call to parse.  Instead,
 * we require that a MessageReader be created and then called.  This may be
 * helpful when dealing with errors and restarts.
 */
final public class MessageReader implements MessageConstants
{

   // A shared instance of a DocumentBuilderFactory
    // Usage is always synchronized on MessageReader.class

   private static DocumentBuilderFactory factory;

   // An XML parser for this MessageReader
    // set to null after reading is complete

   private DocumentBuilder parser;
   // Flag that warning or error seen.  Used to terminate when parser completes.

   private boolean errorSeen = false;
   // The input stream to be parsed

   InputStream stream;
   // The base for resolving the DTD name (null if no DTD URL given)

   String dtdBase = null;   

   /**
     * Create a MessageReader from an open InputStream.
     * @param stream The input stream.  The underlying XML parser will be a validating
     * parser if a non-null URL string is given.
     * @param dtdURL URL (String) of DTD.  <code>null</code> if not validating.
     *          If the DTD is, e.g., relpath/filename.dtd, relative to some
     *          directory in the classpath, then the XML should contain
     * <pre>
     *     &lt;!DOCTYPE message SYSTEM "filename.dtd" &rt;
     * </pre>
     *          and a URL String can be obtained by
     * <pre>
     *     Object.class.getResource(/relpath/filename).toString()
     * </pre>
     *     For example, if C:\test\dia is in the classpath and the DTD file
     *     is C:\test\dia\relpath\filename.dtd, the above call would return:
     * <pre>
     *     "file://C:/test/dia/relpath/filename.dtd"
     * </pre>
     * Note that "filename.dtd" is only used to find the resource directory; it is only required that
     * the file exist in the same directory as the DTD.  The actual name of the dtd is taken
     * from the !DOCTYPE in the XML file.
     */
   public MessageReader(InputStream stream, String dtdURL)
   {
      synchronized (MessageReader.class) {
         if (factory == null) {
            factory = DocumentBuilderFactory.newInstance();
            factory.setCoalescing(true);
            factory.setIgnoringComments(true);
            factory.setIgnoringElementContentWhitespace(true);
         }
         if (dtdURL != null) {
            factory.setValidating(true);
            //System.err.println("dtdURL="+dtdURL);
            int pos = dtdURL.lastIndexOf('/');
            if (pos >= 0) {
               // dtdBase = dtdURL.substring(0,pos+1) + "#GENERATED#";
               dtdBase = dtdURL.substring(0, pos + 1) + CommonProperties.MESSAGE_DTD;
            //System.out.println("dtdBase ..........."+    dtdBase );
            }
            else {
               dtdBase = "file:#GENERATED#";
            }
         }
         else {
            factory.setValidating(false);
         }
         try {
            parser = factory.newDocumentBuilder();
         } catch (ParserConfigurationException e) {
            Log.quit(e);
         }
      }
      parser.setErrorHandler(new MyHandler());
      this.stream = stream;
   }

   /**
     * Read a message from an open InputStream as a DOM.
     */
   public org.w3c.dom.Document read()
   {

      org.w3c.dom.Document result = null;

      if (parser == null) {
         Log.quit("May only call read() once for a MessageReader.");
      }

      try {
         if (dtdBase == null) {
            System.out.println("inise  null   " + dtdBase);
            result = parser.parse(stream);
         }
         else {
            // System.out.println("inise not null   " +    dtdBase);
                //Log.print("----debug ", stream);
            result = parser.parse(stream, dtdBase);
         }
         stream.close();
      } catch (IOException e) {
         Log.quit(e);
      } catch (SAXException e) {
         Log.quit(e);
      }

      if (errorSeen) {
         Log.quit("Error seen parsing XML message");
      }

      //// Write the message in the log
        //// Suppress <ping> and <ok>, because there are so many of them
        //String message = result.getDocumentElement().toString();
        //if (message.indexOf("<ping") < 0 && message.indexOf("<ok") < 0) {
        //    Log.write(message);
        //}

      return result;
   }

   /**
     * Error handler for parsing. 
     * Report all errors and warnings.  Flag error seen, so we can terminate after parse completes.
     */
   private class MyHandler implements ErrorHandler
   {

      public void warning(SAXParseException A)
      {
         report(A, "warning");
      }

      public void error(SAXParseException A)
      {
         report(A, "error");
      }

      public void fatalError(SAXParseException A)
      {
         report(A, "fatalError");
         Log.quit("Fatal error parsing XML message");
      }

      private void report(SAXParseException A, String kind)
      {
         errorSeen = true;
         StringBuffer buffer = new StringBuffer();
         String locPublic = A.getPublicId();
         String locSystem = A.getSystemId();
         int locLine = A.getLineNumber();
         int locColumn = A.getColumnNumber();
         buffer.append("XML ");
         buffer.append(kind);
         buffer.append(" while parsing message at: ");
         if (locPublic != null) {
            buffer.append(locPublic + " ");
         }
         ;
         if (locSystem != null) {
            if (locSystem.endsWith("#GENERATED#")) {
               buffer.append("#GENERATED#");
            }
            else {
               buffer.append(locSystem);
            }
         }
         else {
            buffer.append("#UNKNOWN#");
         }
         if (locLine >= 0) {
            buffer.append(" line " + locLine);
         }
         if (locColumn >= 0) {
            buffer.append(" column " + locColumn);
         }
         Log.print(buffer.toString());
         Log.print(A.getMessage());
      }

   }


   /**
     * Restore the public variables of a class from a DOM Element.
     * For each <value> child of the given element, stores
     * the value in the named variable of the given object.
     * <p>
     * Note: This implementation assumes that the variables
     * are of type String, int or boolean.
     * @param parent The element whose children are <value> elements.
     * @param obj       The object whose values are to be stored.
     * @throws IOException
     */
   public static void decode(Element parent, Object obj) throws IOException
   {
      decode(parent, obj, true);
   }

   public static void decode(Element parent, Object obj, boolean trim) throws IOException
   {
      Class theClass = obj.getClass();
      Node child = parent.getFirstChild();
      while (child != null) {
         if (child.getNodeType() == Node.ELEMENT_NODE && T_VALUE.equals(child.getNodeName())) {
            Element childElement = (Element) child;
            String name = childElement.getAttribute(A_NAME);
            Field field = null;
            try {
               field = theClass.getField(name);
            } catch (NoSuchFieldException e) {
               Log.quit(e);
            }
            String value = common.msg.XmlUtil.getTextFromNode(child);
            //String value = child.getFirstChild().getNodeValue();
                //System.out.println("decode: Setting "+name+" to "+value);
            try {
               if (field.getType().getName().equals("int")) {
                  field.setInt(obj, Integer.parseInt(value));
               }
               else if (field.getType().getName().equals("boolean")) {
                  field.setBoolean(obj, value.toLowerCase().startsWith("t"));
               }
               else {
                  if (trim) {
                     field.set(obj, value.trim());
                  }
                  else {
                     field.set(obj, value);
                  }
               }
            } catch (IllegalAccessException e) {
               Log.quit(e);
            }
         }
         child = child.getNextSibling();
      }
   }

}
