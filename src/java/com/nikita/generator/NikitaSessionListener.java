/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator;

/**
 *
 * @author rkrzmail
 */
import com.rkrzmail.nikita.utility.Utility;
 import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class NikitaSessionListener implements HttpSessionListener {

  private static int totalActiveSessions;
	
  public static int getTotalActiveSession(){
	return totalActiveSessions;
  }
	
  @Override
  public void sessionCreated(HttpSessionEvent arg0) {
	totalActiveSessions++;
	//System.out.println("sessionCreated:"+arg0.getSession().getId());
      
        
  }

  @Override
  public void sessionDestroyed(HttpSessionEvent arg0) {
	totalActiveSessions--;
	//System.out.println("sessionDestroyed:"+arg0.getSession().getId());
        //Utility.deleteFileAll(NikitaService.getDirForm()+NikitaService.getFileSeparator()+arg0.getSession().getId());     
  }	
  
  
}
