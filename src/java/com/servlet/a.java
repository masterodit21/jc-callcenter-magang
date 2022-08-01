/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.servlet;

import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaService;
import com.nikita.generator.NikitaServlet;
import java.io.PrintWriter;
import javax.servlet.AsyncContext;
import javax.servlet.ServletContext;
 
  
/**
 *
 * @author rkrzmail
 */
public class a extends NikitaServlet{
     

    public void OnRun(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {        
        if (request.getParameter("queue").equals("true")) {
            
            response.getHttpServletResponse().setStatus(200);
            response.getHttpServletResponse().setHeader("Access-Control-Allow-Origin", "*"); 
            try {
                request.getHttpServletRequest().setAttribute("org.apache.catalina.ASYNC_SUPPORTED", true);
                AsyncContext asyncContext = request.getHttpServletRequest().startAsync(request.getHttpServletRequest(), response.getHttpServletResponse());
                asyncContext.setTimeout(10 * 60 * 1000);
                NikitaService.contexts.addElement(asyncContext);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
           
            response.Consume();
        }else{
           
            //List<AsyncContext> asyncContexts = new ArrayList<>(this.contexts);
             
            //this.contexts.clear();
            
            String name = request.getParameter("name");
            String message = request.getParameter("message");
            String htmlMessage = "<p><b>" + name + "</b><br/>" + message + "</p>";
            ServletContext sc = request.getHttpServletRequest().getServletContext();
            if (sc.getAttribute("messages") == null) {
                //sc.setAttribute("messages", htmlMessage);
            } else {
                String currentMessages = (String) sc.getAttribute("messages");
                //sc.setAttribute("messages", htmlMessage + currentMessages);
            }
            
             System.out.println("asyncContext:"+NikitaService.contexts.size());
             for (int i = 0; i < NikitaService.contexts.size(); i++) {
                AsyncContext asyncContext = NikitaService.contexts.get(i);
                  
                try (PrintWriter writer = asyncContext.getResponse().getWriter()) {
                   
                    //System.out.println(htmlMessage);
                    writer.println(htmlMessage);
                    writer.flush();
                    asyncContext.complete();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
             NikitaService.contexts.clear();
            
        }
        
    }
    

}
