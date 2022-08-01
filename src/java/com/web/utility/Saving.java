/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.web.utility;

import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nson;
import com.rkrzmail.nikita.utility.Utility;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

/**
 *
 * @author rkrzmail
 */
public class Saving {
    /*
    [Saving]
    Account
    Status[Active|Lock|NeedActivation|Delete]
    Saldo
    SaldoMin
    InsertDate
    UpdateDate
    TrxDate
    
    
    [Saving_History]
    id
    TrxID
    Account
    Status[TopUp|Refund[Sebagian]|Pending|Success|Transfer|Referal|CashBack|Bonus]
    Debit
    Kredit
    Saldo
    InsertDate
    UpdateDate
    TrxDate
    Referensi[TrxID Referensi]
    UserRef[User Referensi]
    UserMessage[User Message]
    
    */
    
    public static void main(String[] args) {
        System.out.println("====================");
        Saving saving = new Saving();
        saving.openConnection("jdbc:mysql://localhost/saving", "root", "root");
        System.out.println(saving.checkSaldo("1").toJson());
        
        
         System.out.println(saving.topupSaldo("1", 10000));
       System.out.println(saving.checkSaldo("1").toJson());
         System.out.println(saving.requestPending("1", 1200, "REF","USR","MSG").toJson());
         System.out.println(saving.checkSaldo("1").toJson());
         System.out.println(saving.historyTransaksi("1").toJson());
          System.out.println(saving.getPending("1").toJson());
        
        
         System.out.println(saving.succesPending("1", "AAABMT7PX6667P55CISDUMJ2AAAACX6UQNWAMOQAAAAAARLD56732"));
       System.out.println(saving.refundPending("1", "AAABMV7PX66RYNPPX66TUMJ2AAAACX6UQPX37PIOHIAAAAAAIVR67P55"));
       System.out.println(saving.transfer("1","5","rrr","aaaaa",300 ).toJson());
        System.out.println(saving.checkSaldo("1").toJson());
        saving.closeConnection();
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
    
   
    
    public Nson checkSaldo(String account){
        try {
            conn.setAutoCommit(false);
            Nson n = query("SELECT SALDO FROM SAVING WHERE ACCOUNT=?", Nson.newArray().addData(account)); 
            Nson result = Nson.newObject();
            result.setData("ACCOUNT", account);
            result.setData("SALDO", n.getData("data").getData(0).getData(0).asLong());
            result.setData("STATUS", "OK");
            return result;
        } catch (Exception e) {
            Nson result = Nson.newObject();
            result.setData("Exception", e.getMessage());
            return result;
        }        
    }
    
    public Nson historyTransaksi(String account){
         try {
            conn.setAutoCommit(false);
            return query("SELECT KREDIT, DEBIT, STATUS, SALDO, REFERENSI, USERREFERENSI, USERMESSAGE, TRXDATE FROM SAVING_HISTORY WHERE ACCOUNT=? ORDER BY TRXDATE DESC", Nson.newArray().addData(account));            
        } catch (Exception e) {
            Nson result = Nson.newObject();
            result.setData("Exception", e.getMessage());
            return result;
        }     
    }
    
    public Nson getPending(String account){
        try {
            return query("SELECT KREDIT, DEBIT, STATUS, SALDO, REFERENSI, USERREFERENSI, USERMESSAGE, TRXDATE, TRXID, INSERTDATE FROM SAVING_HISTORY WHERE ACCOUNT=? AND STATUS ='PENDING' ORDER BY TRXDATE DESC", Nson.newArray().addData(account));            
        } catch (Exception e) {
            Nson result = Nson.newObject();
            result.setData("Exception", e.getMessage());
            return result;
        }     
    }
    private long createAcountAutoSaldo(String account){
        try {
            Nson n = query("SELECT SALDO FROM SAVING WHERE ACCOUNT=?", account);
            if (n.getData("data").size()==0) {
                Nson nson = Nson.newArray();
                nson.addData(0);
                nson.addData(account);
                nson.addData(Utility.Now());
                nson.addData(Utility.Now());                
                n = query("INSERT INTO SAVING (SALDO,  ACCOUNT, INSERTDATE, UPDATEDATE) VALUES (?, ?, ? , ?)", nson);  
            }
            return n.getData("data").getData(0).getData(0).asLong();
        } catch (Exception e) {
        }
        return 0;
    }
    public Nson topupSaldo(String account, long nominal){
        try {
            conn.setAutoCommit(false);
            String trxID = generateTrxID(account);
            //autocheck
            long saldo = createAcountAutoSaldo(account);            
            //tambah saldo account
            Nson n = query("UPDATE SAVING SET SALDO=? WHERE ACCOUNT=? AND SALDO = ?", Nson.newArray().addData(saldo+nominal).addData(account).addData(saldo));            
            //System.err.println(n.toJson());
            //insert history topup
            if (n.containsKey("Exception")) {
                n.setData("STATUS", "ERROR");
                return n;
            }else if (n.getData("UpdateCount").asInteger()==1) {
                Nson nson = Nson.newArray();
                nson.addData(trxID);
                nson.addData(account);
                nson.addData(nominal);
                nson.addData(0);
                nson.addData("TOPUP");
                nson.addData(saldo+nominal);      
                nson.addData("");  //REFERENSI
                nson.addData("");  
                nson.addData("");  
                nson.addData(Utility.Now()); 
                nson.addData(Utility.Now());
                nson.addData(Utility.Now());
                n = query("INSERT INTO SAVING_HISTORY (TRXID, ACCOUNT, KREDIT, DEBIT, STATUS, SALDO, REFERENSI, USERREFERENSI, USERMESSAGE, TRXDATE, INSERTDATE, UPDATEDATE)VALUES   (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", nson);            
                //System.err.println(n.toJson());
                if (n.containsKey("Exception")||n.getData("UpdateCount").asInteger()<=0) {
                    conn.rollback();                    
                    n.setData("STATUS", "ERROR");
                    return n;
                }
            }else{
                Nson result = Nson.newObject();
                result.setData("STATUS", "ERROR");
                result.setData("ERROR", "Multi Transaksi");
                return result;
            }
            //kurangi saldo base
            
            
            //insert history mentopup
            
                                
            
            conn.commit();
            
            Nson result = Nson.newObject();
            result.setData("TRXID", trxID);
            result.setData("SALDO", saldo+nominal);
            result.setData("STATUS", "OK");
            return result;
        } catch (Exception e) {
            Nson result = Nson.newObject();
            result.setData("Exception", e.getMessage());
            return result;
        }
    }
    
    private String requestWithdraw(String account, long withdraw){
        
        return "";
    }
    
    private String successWithdraw(String trxid){
        
        return "";
    }
    
    private String refundWithdraw(String trxid){
        return "";
    }
    
    private Nson transfer(String account, String accounttujuan, String userref, String usermsg, long nominal){
         try {
                conn.setAutoCommit(false);
                String trxID = generateTrxID(account);
                //autocheck
                long saldo = createAcountAutoSaldo(account);   
                if (saldo<nominal) {
                    Nson result = Nson.newObject();
                    result.setData("ACCOUNT", account);
                    result.setData("SALDO", saldo);
                    result.setData("NOMINAL", nominal);
                    result.setData("STATUS", "ERROR");
                    result.setData("ERROR", "Saldo tidak mencukupi");                
                    return result;
                }           
                //tambah saldo account
                Nson n = query("UPDATE SAVING SET SALDO=? WHERE ACCOUNT=? AND SALDO = ?", Nson.newArray().addData(saldo-nominal).addData(account).addData(saldo));            
                //System.err.println(n.toJson());
                //insert history topup
                if (n.containsKey("Exception")) {
                    n.setData("STATUS", "ERROR");
                    return n;
                }else if (n.getData("UpdateCount").asInteger()==1) {
                    Nson nson = Nson.newArray();
                    nson.addData(trxID);
                    nson.addData(account);
                    nson.addData(0);
                    nson.addData(nominal);
                    nson.addData("TRANSFER");
                    nson.addData(saldo-nominal);      
                    nson.addData("");  //REFERENSI
                    nson.addData(userref);  
                    nson.addData(usermsg);  
                    nson.addData(Utility.Now()); 
                    nson.addData(Utility.Now());
                    nson.addData(Utility.Now());
                    n = query("INSERT INTO SAVING_HISTORY (TRXID, ACCOUNT, KREDIT, DEBIT, STATUS, SALDO, REFERENSI, USERREFERENSI, USERMESSAGE, TRXDATE, INSERTDATE, UPDATEDATE)VALUES   (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", nson);            
                    //System.err.println(n.toJson());
                    if (n.containsKey("Exception")||n.getData("UpdateCount").asInteger()<=0) {
                        conn.rollback();                    
                        n.setData("STATUS", "ERROR");
                        return n;
                    }
                }else{
                    Nson result = Nson.newObject();
                    result.setData("STATUS", "ERROR");
                    result.setData("ERROR", "Multi Transaksi");
                    return result;
                }

                 //tambah saldo accounttujuan
                String trxID2 = generateTrxID(accounttujuan);
                //autocheck
                long saldo2 = createAcountAutoSaldo(accounttujuan);  
                //tambah saldo accounttujuan
                n = query("UPDATE SAVING SET SALDO=? WHERE ACCOUNT=? AND SALDO = ?", Nson.newArray().addData(saldo2+nominal).addData(accounttujuan).addData(saldo2));            
                //System.err.println(n.toJson());
                //insert history topup
                if (n.containsKey("Exception")) {
                    n.setData("STATUS", "ERROR");
                    return n;
                }else if (n.getData("UpdateCount").asInteger()==1) {
                    Nson nson = Nson.newArray();
                    nson.addData(trxID2);
                    nson.addData(accounttujuan);
                    nson.addData(nominal);
                    nson.addData(0);
                    nson.addData("RTRANSFER");
                    nson.addData(saldo2+nominal);      
                    nson.addData(trxID);  //REFERENSI
                    nson.addData(userref);  
                    nson.addData(usermsg);  
                    nson.addData(Utility.Now()); 
                    nson.addData(Utility.Now());
                    nson.addData(Utility.Now());
                    n = query("INSERT INTO SAVING_HISTORY (TRXID, ACCOUNT, KREDIT, DEBIT, STATUS, SALDO, REFERENSI, USERREFERENSI, USERMESSAGE, TRXDATE, INSERTDATE, UPDATEDATE)VALUES   (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", nson);            
                    //System.err.println(n.toJson());
                    if (n.containsKey("Exception")||n.getData("UpdateCount").asInteger()<=0) {
                        conn.rollback();                    
                        n.setData("STATUS", "ERROR");
                        return n;
                    }
                }else{
                    Nson result = Nson.newObject();
                    result.setData("STATUS", "ERROR");
                    result.setData("ERROR", "Multi Transaksi");
                    return result;
                }

                
                
                
                

                conn.commit();

                Nson result = Nson.newObject();
                result.setData("TRXID", trxID);
                result.setData("SALDO", saldo-nominal);
                result.setData("STATUS", "OK");
                return result;
            } catch (Exception e) {
                Nson result = Nson.newObject();
                result.setData("Exception", e.getMessage());
                return result;
            }   
    }    
    public Nson requestPending(String account, long nominal, String userreference, String usermessage){
        return  requestPending(account, nominal, "", userreference, usermessage);
    }
    protected Nson requestPending(String account, long nominal, String reference, String userreference, String usermessage){
       try {
            conn.setAutoCommit(false);
            String trxID = generateTrxID(account);
            //autocheck
            long saldo = createAcountAutoSaldo(account);   
            if (saldo<nominal) {
                Nson result = Nson.newObject();
                result.setData("ACCOUNT", account);
                result.setData("SALDO", saldo);
                result.setData("NOMINAL", nominal);
                result.setData("STATUS", "ERROR");
                result.setData("ERROR", "Saldo tidak mencukupi");                
                return result;
            }           
            //tambah saldo account
            Nson n = query("UPDATE SAVING SET SALDO=? WHERE ACCOUNT=? AND SALDO = ?", Nson.newArray().addData(saldo-nominal).addData(account).addData(saldo));            
            //System.err.println(n.toJson());
            //insert history topup
            if (n.containsKey("Exception")) {
                n.setData("STATUS", "ERROR");
                return n;
            }else if (n.getData("UpdateCount").asInteger()==1) {
                Nson nson = Nson.newArray();
                nson.addData(trxID);
                nson.addData(account);
                nson.addData(0);
                nson.addData(nominal);
                nson.addData("PENDING");
                nson.addData(saldo-nominal);      
                nson.addData(reference);  //REFERENSI
                nson.addData(userreference);  
                nson.addData(usermessage);  
                nson.addData(Utility.Now()); 
                nson.addData(Utility.Now());
                nson.addData(Utility.Now());
                n = query("INSERT INTO SAVING_HISTORY (TRXID, ACCOUNT, KREDIT, DEBIT, STATUS, SALDO, REFERENSI, USERREFERENSI, USERMESSAGE, TRXDATE, INSERTDATE, UPDATEDATE)VALUES   (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", nson);            
                //System.err.println(n.toJson());
                if (n.containsKey("Exception")||n.getData("UpdateCount").asInteger()<=0) {
                    conn.rollback();                    
                    n.setData("STATUS", "ERROR");
                    return n;
                }
            }else{
                Nson result = Nson.newObject();
                result.setData("STATUS", "ERROR");
                result.setData("ERROR", "Multi Transaksi");
                return result;
            }
  
            
            conn.commit();
            
            Nson result = Nson.newObject();
            result.setData("TRXID", trxID);
            result.setData("SALDO", saldo-nominal);
            result.setData("STATUS", "OK");
            return result;
        } catch (Exception e) {
            Nson result = Nson.newObject();
            result.setData("Exception", e.getMessage());
            return result;
        }
    }
    public Nson refundPending(String account, String trxid){
        try {
            conn.setAutoCommit(false);
            String trxID = generateTrxID(account);
            //autocheck
            long saldo = createAcountAutoSaldo(account);  
            
            //getpending
            Nson np = query("SELECT KREDIT, DEBIT, STATUS, SALDO, REFERENSI, USERREFERENSI, USERMESSAGE FROM SAVING_HISTORY WHERE ACCOUNT=? AND TRXID = ?  AND STATUS='PENDING' ", Nson.newArray().addData(account).addData(trxid));            
            if (np.getData("data").size()==0) {
                Nson result = Nson.newObject();
                result.setData("STATUS", "ERROR");
                result.setData("ACCOUNT", account);
                result.setData("TRXID", trxid);
                result.setData("ERROR", "PENDING tidak ditemukan");
                return result;
            } 
            long nominal = np.getData("data").getData(0).getData(0).asLong();
            String reference = trxid;
            String userref = np.getData("data").getData(0).getData(5).asString();
            String usermsg = np.getData("data").getData(0).getData(6).asString();
            //updatepending to finish
            np = query("UPDATE SAVING_HISTORY SET STATUS='RDONE' WHERE ACCOUNT=? AND TRXID = ?  AND STATUS='PENDING' ", Nson.newArray().addData(account).addData(trxid));        
            if (np.containsKey("Exception")) {
                np.setData("STATUS", "ERROR");
                return np;
            }else if (np.getData("UpdateCount").asInteger()==1) {                
                //lanjut
            }else{
                Nson result = Nson.newObject();
                result.setData("STATUS", "ERROR");
                result.setData("ERROR", "Multi Transaksi");
                return result;
            }  
            
            //tambah saldo account
            Nson n = query("UPDATE SAVING SET SALDO=? WHERE ACCOUNT=? AND SALDO = ?", Nson.newArray().addData(saldo+nominal).addData(account).addData(saldo));            
            //System.err.println(n.toJson());
            //insert history topup
            if (n.containsKey("Exception")) {
                conn.rollback();
                n.setData("STATUS", "ERROR");
                return n;
            }else if (n.getData("UpdateCount").asInteger()==1) {
                Nson nson = Nson.newArray();
                nson.addData(trxID);
                nson.addData(account);
                nson.addData(nominal);
                nson.addData(0);
                nson.addData("REFUND");
                nson.addData(saldo+nominal);      
                nson.addData(reference);  //REFERENSI
                nson.addData(userref);  
                nson.addData(usermsg);  
                nson.addData(Utility.Now()); 
                nson.addData(Utility.Now());
                nson.addData(Utility.Now());
                n = query("INSERT INTO SAVING_HISTORY (TRXID, ACCOUNT, KREDIT, DEBIT, STATUS, SALDO, REFERENSI, USERREFERENSI, USERMESSAGE, TRXDATE, INSERTDATE, UPDATEDATE)VALUES   (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", nson);            
                //System.err.println(n.toJson());
                if (n.containsKey("Exception")||n.getData("UpdateCount").asInteger()<=0) {
                    conn.rollback();                    
                    n.setData("STATUS", "ERROR");
                    return n;
                }
            }else{
                conn.rollback();
                Nson result = Nson.newObject();
                result.setData("STATUS", "ERROR");
                result.setData("ERROR", "Multi Transaksi");
                return result;
            }
  
            
            conn.commit();
            
            Nson result = Nson.newObject();
            result.setData("TRXID", trxID);
            result.setData("SALDO", saldo+nominal);
            result.setData("STATUS", "OK");
            return result;
        } catch (Exception e) {
            Nson result = Nson.newObject();
            result.setData("Exception", e.getMessage());
            return result;
        }
    }    
    public Nson succesPending(String account, String trxid){
        try {
            conn.setAutoCommit(false);
            String trxID = generateTrxID(account);
            //autocheck
            long saldo = createAcountAutoSaldo(account);  
            
            //getpending
            Nson np = query("SELECT KREDIT, DEBIT, STATUS, SALDO, REFERENSI, USERREFERENSI, USERMESSAGE FROM SAVING_HISTORY WHERE ACCOUNT=? AND TRXID = ?  AND STATUS='PENDING' ", Nson.newArray().addData(account).addData(trxid));            
            if (np.getData("data").size()==0) {
                Nson result = Nson.newObject();
                result.setData("STATUS", "ERROR");
                result.setData("ACCOUNT", account);
                result.setData("TRXID", trxid);
                result.setData("ERROR", "PENDING tidak ditemukan");
                return result;
            } 
            long nominal = np.getData("data").getData(0).getData(0).asLong();
            String reference = trxid;
            String userref = np.getData("data").getData(0).getData(5).asString();
            String usermsg = np.getData("data").getData(0).getData(6).asString();
            //updatepending to finish
            np = query("UPDATE SAVING_HISTORY SET STATUS='SDONE' WHERE ACCOUNT=? AND TRXID = ?  AND STATUS='PENDING' ", Nson.newArray().addData(account).addData(trxid));        
            if (np.containsKey("Exception")) {
                np.setData("STATUS", "ERROR");
                return np;
            }else if (np.getData("UpdateCount").asInteger()==1) {                
                //lanjut
            }else{
                Nson result = Nson.newObject();
                result.setData("STATUS", "ERROR");
                result.setData("ERROR", "Multi Transaksi");
                return result;
            }  
            
            //tambah saldo account
            Nson n = query("UPDATE SAVING SET SALDO=? WHERE ACCOUNT=? AND SALDO = ?", Nson.newArray().addData(saldo).addData(account).addData(saldo));            
            //System.err.println(n.toJson());
            //insert history topup
            if (n.containsKey("Exception")) {
                conn.rollback();
                n.setData("STATUS", "ERROR");
                return n;
            }else if (n.getData("UpdateCount").asInteger()==1) {
                Nson nson = Nson.newArray();
                nson.addData(trxID);
                nson.addData(account);
                nson.addData(0);
                nson.addData(nominal);
                nson.addData("SUCCES");
                nson.addData(saldo);      
                nson.addData(reference);  //REFERENSI
                nson.addData(userref);  
                nson.addData(usermsg);  
                nson.addData(Utility.Now()); 
                nson.addData(Utility.Now());
                nson.addData(Utility.Now());
                n = query("INSERT INTO SAVING_HISTORY (TRXID, ACCOUNT, KREDIT, DEBIT, STATUS, SALDO, REFERENSI, USERREFERENSI, USERMESSAGE, TRXDATE, INSERTDATE, UPDATEDATE)VALUES   (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", nson);            
                //System.err.println(n.toJson());
                if (n.containsKey("Exception")||n.getData("UpdateCount").asInteger()<=0) {
                    conn.rollback();                    
                    n.setData("STATUS", "ERROR");
                    return n;
                }
            }else{
                conn.rollback();
                Nson result = Nson.newObject();
                result.setData("STATUS", "ERROR");
                result.setData("ERROR", "Multi Transaksi");
                return result;
            }
  
            
            conn.commit();
            
            Nson result = Nson.newObject();
            result.setData("TRXID", trxID);
            result.setData("SALDO", saldo);
            result.setData("STATUS", "OK");
            return result;
        } catch (Exception e) {
            Nson result = Nson.newObject();
            result.setData("Exception", e.getMessage());
            return result;
        }
    } 
    private synchronized String generateTrxID(String account){
        String x = new String(longToBytes(Thread.currentThread().hashCode()) );
        String y = new String(longToBytes(System.currentTimeMillis()));
        String z = new String(longToBytes(System.nanoTime()));   
        byte[] xyz = (z + ":" + account + ":" + y + ":" + x).getBytes();
        
        String encoded = Base32.encode(xyz);
        if (encoded.length()>=256) {
            encoded = encoded.substring(0, 255);
        }
        return  encoded;
    }
    private String toHexString(byte[] ba) {
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < ba.length; i++)
            str.append(String.format("%x", ba[i]));
        return str.toString();
    }
    private String fromHexString(String hex) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < hex.length(); i+=2) {
            str.append((char) Integer.parseInt(hex.substring(i, i + 2), 16));
        }
        return str.toString();
    }
    private byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }

    private long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.put(bytes);
        buffer.flip();//need flip 
        return buffer.getLong();
    }

}
