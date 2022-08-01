/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**  
 * created by 13k.mail@gmail.com
 */
public class NikitaBundle {
    public NikitaBundle(){
        
    }
    
    private HttpServlet httpServlet;
    private NikitaResponse resp;
    private NikitaRequest  req;
    private NikitaLogic  logic;
    private Component  component;
    
    private String action = "";
    private String reqresult = "";
    private String resresult = "";
    
    public String getRequestResult(){
        return reqresult;
    }
    
    public String getResponseResult(){
        return resresult;
    }
    
    public String getAction(){
        return action;
    }
    
    public NikitaLogic getNikitaLogic(){
        return logic;
    }
    
    public NikitaRequest getNikitaRequest(){
        return req;
    }
    
    public NikitaResponse getNikitaResponse(){
        return resp;
    }
    
    public Component getComponent(){
        return component;
    }
    
    
    
}
