package sun.com.concept.ajax;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class AjaxSample extends HttpServlet {

    private static int count = 0;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void destroy() {
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        PrintWriter out = response.getWriter();
        Date df = new Date();
        String per = request.getParameter("percentage");

        if(per == null || per.equals(""))
            {
            increaseCounter();
        }else{
            try{
                int cnt = Integer.parseInt(per);
                count = cnt;
                if(count > 500){
                    count = 0;
                }
            }catch(NumberFormatException e){
                increaseCounter();
            }
        }
        out.println(count);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        doPost(request, response);
    }

    private void increaseCounter(){
        count += 5;
        if(count > 500){
            count = 0;
        }
    }
}