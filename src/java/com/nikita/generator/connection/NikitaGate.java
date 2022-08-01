/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator.connection;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author rkrzmail
 */
public class NikitaGate  extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
       /*
        String forwardingAddress;
        if(true)
            forwardingAddress = "http://red.example.com/doStuff";
        else
            forwardingAddress = "http://blue.example.com/doStuff";

        PrintWriter writer;
        try {
            writer = response.getWriter();
             writer.write(getResponseFromBackend(forwardingAddress, request));
        } catch (IOException ex) {
            Logger.getLogger(NikitaGate.class.getName()).log(Level.SEVERE, null, ex);
        }
*/
        
        
        //cara 2
        RequestDispatcher dispatcher = request.getRequestDispatcher(request.getPathInfo());
        try {
            // here you have the choice whether to use include(..) or forward(..) see below
            dispatcher.include(request, response);
            //dispatcher.forward(httpRequest, httpResponse);
        } catch (ServletException ex) {
            Logger.getLogger(NikitaGate.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(NikitaGate.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String getResponseFromBackend(String addr, HttpServletRequest req) {
        // Somehow forward req to addr and get HTML response...
        return "";
    }
}
