/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.rkrzmail.nikita.data;

import java.io.FileOutputStream;
import java.io.RandomAccessFile;

/**
 *
 * @author rkrzmail
 */
public class NikitasetNiDB {
    //Text|Number|Date|Boolean|Blob*
    
    /*
    Data per 8KB - 64bit 
    flag 
    0=deleted rows[data]
    1=header
    2=difinisi table
    3
    4
    5
    6
    7
    8=typenikitaset
    9=fragment[0000000000000000000000000000064]
    
    
    */
    FileOutputStream fosSelect;
    RandomAccessFile accessFile;
    public NikitasetNiDB(String file){
        //header [8KB : 8192]
        //flag|tableid|rowdata[enter|\r\n]
        //rowdata == {sqlescape}
        
    }
    private final int buffLen = 8192;
    private void create(String name, String fieldswithtype){
        try {
            byte[] buffer = new byte[buffLen];
            accessFile.seek(0);
            accessFile.readFully(buffer);
            
             
            
        } catch (Exception e) { }        
    }
    private void drop(String table){
        
    }
    private void performace(){
        
    }
    private void delete(String table, String where){
        try {
            //cari amalamt
            
        } catch (Exception e) { }
    }
    private void select(String table, String fields, String where){
        try {
            fosSelect = new FileOutputStream("");
            
            fosSelect.close();
        } catch (Exception e) { }
    }
    private void update(String table, String fields, String where){
        try {
            //cari amalamt
            
        } catch (Exception e) { }
    }
    private void insert(String table, String fields, String where){
        try {
            //cari amalamt
            
        } catch (Exception e) { }
    }
    private void importNikitaset(String table, Nikitaset ns){
        
    }
    
    private boolean expression(){
        return false;
    }
}
