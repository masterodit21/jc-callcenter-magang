/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rkrzmail.nikita.data;

import java.util.Vector;


/**
 * created by 13k.mail@gmail.com
 */

public interface ICursor {
    public Vector<String> getDataAllHeader();
    public Vector<String> getDataRowsVector() ;
	
    public String getError(); 
    public Object getInfo(); 
        
    public int getRows();
    public int getCols();
    
    public int getRow();    
    public boolean moveNext(); 
 
	
    public String getText(int col);
    public String getText(String colname);
    public String getHeader(int col);
}
