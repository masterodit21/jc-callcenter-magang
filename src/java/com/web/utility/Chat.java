/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.web.utility;

import com.nikita.generator.NikitaNetwork;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nson;
import com.rkrzmail.nikita.utility.Utility;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.Vector;

/**
 *
 * @author rkrzmail
 */
public class Chat {    
    public static void main(String[] args) {
        Chat chat = new Chat();
        chat.openConnection("jdbc:mysql://localhost/chat", "root", "root");
        System.out.println(chat.sendChat("ani", "account1", "hai", ""));
        
        System.out.println(chat.createThread("ani", "account1,account2,accout3,accout4"));        
        System.out.println(chat.sendChatThread("ani", "account1", "hai", ""));
        //System.out.println(chat.getChat("ani", "1"));
        System.out.println(chat.getChatNewOnly("ani"));
        //System.out.println(chat.removeThread("ani"));
        // System.out.println(chat.removeChatGet());
        chat.closeConnection();
    }    
    public Nson getChat(String account, String lastid){
        Nson result = Nson.newObject();
        try {
            conn.setAutoCommit(false);
            Nson nson = Nson.newArray();
            nson.addData(account);
            nson.addData(Utility.getLong(lastid));
            result = query("SELECT  id, threadid, sender, message, mime, createdate, status FROM chat_message WHERE account=? AND id>?", nson);
            
            nson = Nson.newArray();
            nson.addData(account);
            nson.addData(Utility.getLong(lastid));
            nson.addData(result.getData("data").getData(result.getData("data").size()-1).getData(0).asString());
            query("UPDATE  chat_message SET status='GET' WHERE status='' AND account=? AND id>? AND id<=?", nson);
              
            conn.commit();
        } catch (Exception e) {
            result = Nson.newObject();
            result.setData("Exception", e.getMessage());
            return result;
        }        
        return result;
    
    }
    public Nson getChatNewOnly(String account){
        Nson result = Nson.newObject();
        try {
            conn.setAutoCommit(false);
            Nson nson = Nson.newArray();
            nson.addData(account);
             
            result = query("SELECT  id, threadid, sender, message, mime, createdate, status FROM chat_message WHERE account=? AND status=''", nson);
            
            nson = Nson.newArray();
            nson.addData(account);       
            nson.addData(result.getData("data").getData(result.getData("data").size()-1).getData(0).asString());
            query("UPDATE  chat_message SET status='GET' WHERE status='' AND account=? AND id<=?", nson);
              
            conn.commit();
        } catch (Exception e) {
            result = Nson.newObject();
            result.setData("Exception", e.getMessage());
            return result;
        }        
        return result;   
    }    
    public Nson sendChat(String tujuan, String account, String message, String mime){
         return sendChat(tujuan, account, message, mime, "");
    }
    
    public Nson sendChat(String tujuan, String account, String message, String mime, String status){
        Nson result = Nson.newObject();
        try {
            conn.setAutoCommit(false);
            Nson nson = Nson.newArray();
            nson.addData(tujuan);
            nson.addData(account);
            nson.addData(message);
            nson.addData(mime);
            nson.addData(Utility.Now());
            nson.addData(status);
            result = query("INSERT INTO chat_message (account, sender, message, mime, createdate, status) VALUES (?, ?, ?, ?, ?, ?)", nson);
            conn.commit();
        } catch (Exception e) {
            result = Nson.newObject();
            result.setData("Exception", e.getMessage());
            return result;
        }        
        return result;
    }
    public Nson removeChatGet(){
        Nson result = Nson.newObject();               
        try {
            conn.setAutoCommit(false);
                Nson nson = Nson.newArray();
                result = query("DELETE FROM chat_message WHERE status = 'GET'", nson);
                if (result.containsKey("Exception")) {
                    conn.rollback();
                    return result;                    
                }                 
            conn.commit();
        } catch (Exception e) {
            result = Nson.newObject();
            result.setData("Exception", e.getMessage());
            return result;
        }
        return result;
    }
    public Nson removeThread(String threadid){
        Nson result = Nson.newObject();               
        try {
            conn.setAutoCommit(false);
                Nson nson = Nson.newArray();                    
                nson.addData(threadid);

                result = query("DELETE FROM chat_message WHERE threadid = ?", nson);
                if (result.containsKey("Exception")) {
                    conn.rollback();
                    return result;                    
                }
                result = query("DELETE FROM chat_thread  WHERE threadid = ?", nson);
                if (result.containsKey("Exception")) {
                    conn.rollback();
                    return result;                    
                }
            conn.commit();
        } catch (Exception e) {
            result = Nson.newObject();
            result.setData("Exception", e.getMessage());
            return result;
        }
        return result;
    }
    public Nson createThread(String threadid, String accountarray){
        Nson result = Nson.newObject();               
        try {
            conn.setAutoCommit(false);
                Nson nson = Nson.newArray(); 
                nson.addData(threadid);
                nson.addData(accountarray); 
                nson.addData(Utility.Now());                

                result = query("INSERT INTO chat_thread (threadid, accountarray, createdate) VALUES (?, ?, ?)", nson);
                if (result.containsKey("Exception")) {
                    nson = Nson.newArray();       
                    nson.addData(accountarray); 
                    nson.addData(Utility.Now());
                    nson.addData(threadid);
                    result = query("UPDATE chat_thread SET  accountarray = ?, createdate =? WHERE threadid = ?", nson);
                    
                }
            conn.commit();
        } catch (Exception e) {
            result = Nson.newObject();
            result.setData("Exception", e.getMessage());
            return result;
        }
        return result;
    }
    
    public Nson sendChatThread(String threadid, String account, String message, String mime){
        Nson result = Nson.newObject();
        try {
            conn.setAutoCommit(false);
            Nson nson = Nson.newArray();                  
            nson.addData(threadid);

            Nson nt = query("SELECT accountarray  FROM chat_thread WHERE   threadid = ?", nson);
            Vector<String> vector = Utility.splitVector(nt.getData("data").getData(0).getData(0).asString(), ",");
            for (int i = 0; i < vector.size(); i++) {
                if (vector.get(i).equalsIgnoreCase(account)) {
                }else if (vector.get(i).equalsIgnoreCase("null")||vector.get(i).equalsIgnoreCase("")) { 
                }else{
                    nson = Nson.newArray();
                    nson.addData(threadid);
                    nson.addData(vector.get(i));
                    nson.addData(account);
                    nson.addData(message);
                    nson.addData(mime);
                    nson.addData(Utility.Now());
                    result = query("INSERT INTO chat_message (threadid, account, sender, message, mime, createdate) VALUES (?, ?, ?, ?, ?, ?)", nson);
                    if (result.containsKey("Exception")) {
                        conn.rollback();
                        return result;
                    }       
                }                        
            }
            conn.commit();            
        } catch (Exception e) {            
            result = Nson.newObject();
            result.setData("Exception", e.getMessage());
            return result;
        }
        return result;
    }
    
    private Connection conn ;    
    public void openConnection(String url, String user, String pass){
        try {            
            String cname = "com.mysql.jdbc.Driver";
            Class.forName(cname); 
            conn = DriverManager.getConnection(url, user, pass);          
        } catch (ClassNotFoundException | SQLException e) {  
           
        }
    }
    
    public void closeConnection(){
        try {
            conn.close();
        } catch (Exception e) {
        }
    } 
    private Nson query(String sql, String arg){
        return query(sql, Nson.newArray().addData(arg));
    }
    private Nson query(String sql, Nson args){
        try {
            conn.prepareCall(sql);
            PreparedStatement statement = conn.prepareStatement(sql);
            for (int i = 0; i < args.size(); i++) {
                if (args.isNumber()) {                    
                    statement.setLong(i+1, args.getData(i).asLong());
                }else  {                    
                    statement.setString(i+1, args.getData(i).asString());
                }                
            }
            statement.execute();             
            //System.err.println( new Nikitaset(statement.getResultSet()).toNset().toJSON());
            Nikitaset n = new Nikitaset(statement.getResultSet());
            return new Nson((Map)n.toNset().getInternalObject()).setData("UpdateCount", statement.getUpdateCount());
        } catch (Exception e) {
            return Nson.newObject().setData("Exception", e.getMessage());
        }        
    }
    
   
     
}
