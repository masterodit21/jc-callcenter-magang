/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.rkrzmail.nikita.data;

import java.util.Vector;

/**
 *
 * @author rkrzmail
 */
public class NikitasetQuery {
    String buffer = "";
    private Nikitaset currNikitaset;
    public NikitasetQuery(Nikitaset n){
        this.currNikitaset=n;
    } 
    
    private Nikitaset buffNikitaset;
    private void createNikitasetBuffer(){
        buffNikitaset = new Nikitaset("");
    }
    private void putNikitasetBuffer(Vector<String> row){
         
    }
    
    private void query(String mode, String fields, String where){
        /* mode : select, insert, delete, update */
    }
    private Nikitaset insert(String fields, String where){
        /*  */ 
        
        return currNikitaset;
    }
    private void delete(String where){
        for (int i = 0; i < currNikitaset.getRows(); i++) {
            if (whereExppression(currNikitaset, i, where)) {
                //delete
            }            
        }
    }
    private void update(String fields, String where){
        
    }
    private Nikitaset select(String fields, String where){
        createNikitasetBuffer();
        for (int i = 0; i < currNikitaset.getRows(); i++) {
            if (whereExppression(currNikitaset, i, where)) {
                putNikitasetBuffer(currNikitaset.getDataAllVector().elementAt(i));
            }            
        }
        return buffNikitaset;
    }
    
    
    private void join(Nikitaset n, String qu){
        
    }
    
    private boolean whereExppression(Nikitaset nikitaset, int row, String where){
        NikitaExpression expression=new NikitaExpression();
        expression.expression(where);
        
        return false;
    }
}
