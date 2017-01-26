/*
 * XmlReader.java
 *
 * Created on November 16, 2007, 12:10 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.fossa.servlet.common.msg;

import com.fossa.servlet.common.Log;
import com.fossa.servlet.common.MessageConstants;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *
 * @author Bala
 */
/**
 * Class Used to read the XML getting from the client side
 */
public class XmlReader implements MessageConstants{
       
    private static Logger logger = Logger.getLogger("com.fossa.servlet.common.msg");
    
    /** Creates a new instance of XmlReader */
    public XmlReader() {        
    }
    
    
    public Element readMessage(InputStream rawInStream){  
        //for -debug
        /*BufferedInputStream ins= new BufferedInputStream(rawInStream);
             InputStreamReader   isr= new InputStreamReader(ins);
             BufferedReader      br= new BufferedReader(isr);

            String userInput;
             try {
                userInput= br.readLine(); 
               System.out.println("22222"+userInput);
            if (userInput != null) { 
               // first line wasn't end of file
               userInput= br.readLine(); 
               System.out.println("1111"+userInput);
            }}
            catch (IOException ex) {
            java.util.logging.Logger.getLogger(XmlReader.class.getName()).log(Level.SEVERE, null, ex);
           }*/
        
            DataInputStream dis = new DataInputStream(rawInStream);
            
            //get the factory           
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            Document doc = null;
            DocumentBuilder db = null;
            try {
                //Using factory get an instance of document builder
                db = dbf.newDocumentBuilder();
                //parse using builder to get DOM representation of the input stream
                doc = db.parse(dis);
                dis.close();
                
            } catch (ParserConfigurationException pce) {
                logger.error("Exception while parsing the document." + pce);
                StringWriter sw = new StringWriter();
                pce.printStackTrace(new PrintWriter(sw));
                logger.error(sw.toString());
            } catch (SAXParseException parserException) {
                logger.error("Exception while parsing the document." + parserException);
                StringWriter sw = new StringWriter();
                parserException.printStackTrace(new PrintWriter(sw));
                logger.error(sw.toString());
            } catch (SAXException se) {
                logger.error("Exception while parsing the document." + se);
                StringWriter sw = new StringWriter();
                se.printStackTrace(new PrintWriter(sw));
                logger.error(sw.toString());
            } catch (IOException ioe) {
                logger.error("Exception while accessing the document." + ioe);
                StringWriter sw = new StringWriter();
                ioe.printStackTrace(new PrintWriter(sw));
                logger.error(sw.toString());
            }

            Node result = doc.getDocumentElement().getFirstChild();

            while (result != null && result.getNodeType() != Node.ELEMENT_NODE) {
                result = result.getNextSibling();
            }
            assert result != null;
            Element element = (Element) result;

            return element;
        //} 
    }
    /**
     * Method used to return session Id for each request
     * @param rawInStream inputstream 
     */
    public static String sessionId(InputStream rawInStream){
        
        DataInputStream dis = new DataInputStream(rawInStream);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document doc = null;
        DocumentBuilder db = null;
        try {
            //Using factory get an instance of document builder
            db = dbf.newDocumentBuilder();
            //parse using builder to get DOM representation of the input stream
            doc = db.parse(dis);
            dis.close();
            
        }catch(ParserConfigurationException pce) {
            logger.error("Exception while parsing the document." + pce);
            StringWriter sw = new StringWriter();
            pce.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
        } catch(SAXParseException parserException) {
            logger.error("Exception while parsing the document." + parserException);
            StringWriter sw = new StringWriter();
            parserException.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
        } catch(SAXException se) {
            logger.error("Exception while parsing the document." + se);
            StringWriter sw = new StringWriter();
            se.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
        }catch(IOException ioe) {
            logger.error("Exception while accessing the document." + ioe);
            StringWriter sw = new StringWriter();
            ioe.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
        }
        Node result = doc.getDocumentElement().getFirstChild();
        
        while (result != null && result.getNodeType() != Node.ELEMENT_NODE) {
            result = result.getNextSibling();
        }
        assert result != null;
        Element element=(Element) result;
         String fossaSessionId = null;
                 Element docEle = doc.getDocumentElement();		
                 NodeList nl = docEle.getElementsByTagName(element.getNodeName());
                 
		 if(nl != null && nl.getLength() > 0) {
                    for(int i = 0 ; i < nl.getLength();i++) {
                        Element el = (Element)nl.item(i);
                        boolean hasAttri = el.hasAttributes();
                        if(hasAttri){
                        fossaSessionId =el.getAttribute("fossaSession_id");                        
                       }
                    }
		  }
        return fossaSessionId;    
    }
   
    /**
     * Used to decode the XML
     */
    public static void decode(Element parent, Object obj) throws IOException {
        decode(parent, obj, true);
    }
    
    /**
     * Used to decode the XML
     * @param parent XML elements
     * @param trim boolean variable used to trim the each value of the XML element
     */
    public static void decode(Element parent, Object obj, boolean trim) throws IOException {
        
        Class theClass = obj.getClass();
        Node child = parent.getFirstChild();
         child=child.getNextSibling();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE
                    && T_VALUE.equals(child.getNodeName())) {
                Element childElement = (Element) child;
                String name = childElement.getAttribute(A_NAME);
                Field field = null;
                try {
                    field = theClass.getField(name);
                } catch (NoSuchFieldException e) {
                   logger.error("Exception while decoding the document and getting the fieldname." + e);
                   StringWriter sw = new StringWriter();
                   e.printStackTrace(new PrintWriter(sw));
                   logger.error(sw.toString());
                   Log.quit(e);
                }
                String value = com.fossa.servlet.common.msg.XmlUtil.getTextFromNode(child);
                //String value = child.getFirstChild().getNodeValue();                
                try {
                    if (field.getType().getName().equals("int")) {
                        field.setInt(obj, Integer.parseInt(value));
                    } else if (field.getType().getName().equals("boolean")) {
                        field.setBoolean(obj,value.toLowerCase().startsWith("t"));
                    } else {
                        if (trim) {
                            field.set(obj, value.trim());
                        } else {
                            field.set(obj, value);
                        }
                    }
                } catch (IllegalAccessException e) {
                    logger.error("Exception while accessing the document." + e);
                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw));
                    logger.error(sw.toString());
                    Log.quit(e);
                }
            }
            child = child.getNextSibling();
        }
    }
    
}
