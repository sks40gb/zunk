/*
 * Command_ImageServlet.java
 *
 * Created on December 3, 2007, 9:49 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.fossa.servlet;

import com.fossa.servlet.common.Log;
import com.fossa.servlet.common.MessageConstants;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.common.msg.MultiInputStream;
import com.fossa.servlet.common.msg.MultiOutputStream;
import com.fossa.servlet.common.msg.XmlReader;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.server.GoodbyeException;
import com.fossa.servlet.server.ImageSender;
import com.fossa.servlet.session.UserTask;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This class handles the request & response for image between client & server.
 * @author bmurali
 */
public class ImageDispatcherServlet extends HttpServlet implements MessageConstants {

    final protected static String MESSAGE_DTD = "message.dtd";
    private MultiInputStream multiInputStream = null;
    private MultiOutputStream multiOutputStream = null;
    private InputStream inputStream = null;
    private static Logger logger = Logger.getLogger("com.fossa.servlet");
    
    /** Declaring  constructor of the class*/
    public ImageDispatcherServlet() {
    }

     /**
     * This method is invoked whenever a client submits a request with a 'get' method.
     * @param request : is the HttpRequest object
     * @param response : is HttpResponse object
     * @throws java.io.IOException
     */
    public void doGet(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        doPost(request, response);
    }    
    
    /**
     * This method is invoked whenever a client submits a request with a 'post' method or with a 'get' method.
     * @param request : is the HttpRequest object
     * @param response : is HttpResponse object
     * @throws java.io.IOException
     */
    public void doPost(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        try {
            multiInputStream = new MultiInputStream(request.getInputStream());
            inputStream = multiInputStream.newStream();
            multiOutputStream = new MultiOutputStream(response.getOutputStream());
            UserTask task = new UserTask();
            DBTask dbTask = new DBTask();

            ServletContext context = getServletContext();
            String contextPath = context.getRealPath("/");
            task.setContextPath(contextPath + "UPLOAD/");

            XmlReader reader = new XmlReader();
            Node messageAction = reader.readMessage(inputStream);
            do {
                if (messageAction.getNodeType() == Node.ELEMENT_NODE) {
                    String action = messageAction.getNodeName();
                    if (action.equals(T_IMAGE_REQUEST)) {
                        ImageSender.send(task, dbTask, (Element) messageAction, multiOutputStream);
                    } else if (action.equals(T_GOODBYE)) {
                        Log.print("goodbye received");
                        throw new GoodbyeException("goodbye");
                    } else if (action.equals(T_PING)) {
                        MessageWriter writer = new MessageWriter(multiOutputStream.newStream(), MESSAGE_DTD);
                        writer.startElement(T_OK);
                        writer.endElement();
                        writer.close();
                    } else {
                        Log.quit("Invalid message for image server: " + messageAction);
                    }
                }
            } while ((messageAction = messageAction.getNextSibling()) != null);

        } catch (GoodbyeException e) {
            printExceptions("Exception while saying goodbye in dispatching image.", e);
        } catch (IOException e) {
            printExceptions("Exception while dispatching image.", e);
        } catch (Throwable e) {
            logger.error("Exception while dispatching image." + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
        } finally {
        // clean up before ending task
        }
    }
    
     /**
     * This methods prints the stack trace of generated exceptions
     * @param customMessage : is the error message to be shown
     * @param execption : is the exception object caught or thrown
     */
    private void printExceptions(String customMessage,Exception execption){
       logger.error(customMessage + execption);            
       StringWriter swt = new StringWriter();
       execption.printStackTrace(new PrintWriter(swt));
       logger.error(swt.toString());
    }
}