/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

/**
 *
 * @author rkrzmail
 */
public class NikitaRzConfigurator extends ServerEndpointConfig.Configurator {
    @Override
    public void modifyHandshake(ServerEndpointConfig config,   HandshakeRequest request,   HandshakeResponse response)  {
       // HttpSession httpSession = (HttpSession)request.getHttpSession();
        //config.getUserProperties().put("HttpSession", httpSession); 
//        System.out.println("NikitaRzConfigurator:"+Thread.currentThread().getName());
//        System.out.println("NikitaRzConfigurator:" +String.valueOf(request.getHeaders()));
//        System.out.println("NikitaRzConfigurator:" +String.valueOf(request.getParameterMap()));
//        System.out.println("NikitaRzConfigurator:" +String.valueOf(response.getHeaders()));        
        super.modifyHandshake(config, request, response);
    }

    @Override
    public boolean checkOrigin(String originHeaderValue) {
        return super.checkOrigin(originHeaderValue); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}