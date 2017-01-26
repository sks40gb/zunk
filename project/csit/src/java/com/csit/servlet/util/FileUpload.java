/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csit.servlet.util;

import com.csit.servlet.ParentServlet;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author sunil
 */
public class FileUpload extends ParentServlet {

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("image/gif ");
        PrintWriter out = response.getWriter();
        try {

            ServletInputStream in = request.getInputStream();
            byte[] line = new byte[128];
            int i = in.readLine(line, 0, 128);
            while (i != -1) {
                out.print(new String(line, 0, i));
                i = in.readLine(line, 0, 128);
            }
            out.println("</pre>");

        } finally {
            out.close();
        }
    }
}
