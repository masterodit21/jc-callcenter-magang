/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rkrzmail.nikita.data;

import java.util.Vector;


/**
 * created by 13k.mail@gmail.com
 */

public interface IRecordset {
    public Vector<String> getDataAllHeader();
    public Vector<Vector<String>> getDataAllVector() ;
	
    public String getError(); 
    public Object getInfo(); 
        
    public int getRows();
    public int getCols();
	
    public String getText(int row, int col);
    public String getText(int row, String colname);
    public String getHeader(int col);
}
