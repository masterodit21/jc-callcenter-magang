/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator.action;

import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.connection.NikitaConnection;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.data.Nson;
import com.rkrzmail.nikita.utility.Utility;
import com.web.utility.Base32;
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
public class CustomAction implements IAction{

    @Override
    public boolean OnAction(Nset currdata, NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        String code = response.getVirtualString(currdata.getData("code").toString());  
        currdata = currdata.getData("args");
        if (code.equals("web")) {
            String param1 = response.getVirtualString(currdata.getData("param1").toString());  
            if (param1.equalsIgnoreCase("mobisaving")) {   
                Saving saving = new Saving();
                saving.openConnection(NikitaConnection.getDefaultPropertySetting().getData("saving").getData("url").toString() , NikitaConnection.getDefaultPropertySetting().getData("saving").getData("user").toString(), NikitaConnection.getDefaultPropertySetting().getData("saving").getData("pass").toString());
                Nson result = Nson.newObject();
                String id  = response.getVirtualString(currdata.getData("param2").toString());                 
                String status     = response.getVirtualString(currdata.getData("param3").toString()); 
                
                
                if (status.equalsIgnoreCase("0")) {
                    //permintaan
                }else if (status.equalsIgnoreCase("1")) {
                    //Proses //APPRESULT
                    Nson cn = Nson.newArray();
                    cn.addData("PROSES");
                    cn.addData(id);
                    Nson n = saving.query("SELECT   SAVING_HISTORY  SET APPRESULT = ?  WHERE ID=?", cn);
                   
                }else if (status.equalsIgnoreCase("2")) {
                    //Berhasil
                    Nson n = saving.query("SELECT account,TRXID,REKNO,REKNAME,REKJUMLAH,REKADMIN,APPBYUSER,APPRESULT,APPTRXTYPE,STATUS FROM SAVING_HISTORY WHERE ID=?", id);
                    String account = n.getData("data").getData(0).getData(0).asString();
                    String trxid = n.getData("data").getData(0).getData(1).asString();
                    
                    Nson cn = Nson.newObject();                
                    cn.setData("REKNO",     n.getData("data").getData(0).getData(2).asString());
                    cn.setData("REKNAME",   n.getData("data").getData(0).getData(3).asString());
                    cn.setData("REKJUMLAH", n.getData("data").getData(0).getData(4).asString());
                    cn.setData("REKADMIN",  n.getData("data").getData(0).getData(5).asString());

                    cn.setData("APPBYUSER", n.getData("data").getData(0).getData(6).asString());
                    cn.setData("APPRESULT", "");
                    //APPTRXTYPE
                    if (n.getData("data").getData(0).getData(7).asString().equalsIgnoreCase("TABUNGAN")) {
                        if (n.getData("data").getData(0).getData(8).asString().equalsIgnoreCase("PENDING")) {
                                 
                        }
                         
                    }else{
                         
                    }
                    result = saving.succesPending(account, trxid, cn);
                    
                    
                    response.setVirtual(currdata.getData("result").toString(), result.toJson());
                }else if (status.equalsIgnoreCase("9")) {
                    //Gagal/Refund
                    Nson n = saving.query("SELECT account,TRXID,REKNO,REKNAME,REKJUMLAH,REKADMIN,APPBYUSER,APPRESULT,APPTRXTYPE,STATUS FROM SAVING_HISTORY WHERE ID=?", id);
                    String account = n.getData("data").getData(0).getData(0).asString();
                    String trxid = n.getData("data").getData(0).getData(1).asString();
                    
                    Nson cn = Nson.newObject();                
                    cn.setData("REKNO",     n.getData("data").getData(0).getData(2).asString());
                    cn.setData("REKNAME",   n.getData("data").getData(0).getData(3).asString());
                    cn.setData("REKJUMLAH", n.getData("data").getData(0).getData(4).asString());
                    cn.setData("REKADMIN",  n.getData("data").getData(0).getData(5).asString());

                    cn.setData("APPBYUSER", n.getData("data").getData(0).getData(6).asString());
                    cn.setData("APPRESULT", "");
                    //APPTRXTYPE
                    if (n.getData("data").getData(0).getData(7).asString().equalsIgnoreCase("TABUNGAN")) {
                        if (n.getData("data").getData(0).getData(8).asString().equalsIgnoreCase("PENDING")) {
                                 
                        }
                         
                    }else{
                         
                    }
                    result = saving.refundPending(account, trxid, cn);
                    
                    
                    response.setVirtual(currdata.getData("result").toString(), result.toJson());
                }      
                saving.closeConnection();
            }else if (param1.equalsIgnoreCase("mobisaldo")) { 
                String userid  = response.getVirtualString(currdata.getData("param2").toString());    
                Saving saving = new Saving();
                saving.openConnection(NikitaConnection.getDefaultPropertySetting().getData("saving").getData("url").toString() , NikitaConnection.getDefaultPropertySetting().getData("saving").getData("user").toString(), NikitaConnection.getDefaultPropertySetting().getData("saving").getData("pass").toString());
                Nson result = Nson.newObject();
              
                result.setData("SALDO", saving.checkSaldo(userid).getData("SALDO").asLong());
                result.setData("PENDING", saving.checkSaldoPending(userid).getData("SALDO").asLong());
                response.setVirtual(currdata.getData("result").toString(), result.toJson());
                saving.closeConnection();
            }else if (param1.equalsIgnoreCase("mobisavingtrx")) {    
                //transaksi
                String userid  = response.getVirtualString(currdata.getData("param2").toString());                 
                String bank     = response.getVirtualString(currdata.getData("param3").toString()); 
                String rekno     = response.getVirtualString(currdata.getData("param4").toString()); 
                String reknama     = response.getVirtualString(currdata.getData("param5").toString()); 
                String jumlah     = response.getVirtualString(currdata.getData("param6").toString()); 
                String admin     = response.getVirtualString(currdata.getData("param7").toString()); 
                String transfer     = response.getVirtualString(currdata.getData("param8").toString()); 
                Nson cn = Nson.newObject();                
                cn.setData("REKNO", rekno);
                cn.setData("REKNAME", reknama);
                cn.setData("REKJUMLAH", Utility.getInt(jumlah));
                
                
                cn.setData("APPBYUSER", bank);
                cn.setData("APPRESULT", "");
                
                Saving saving = new Saving();
                saving.openConnection(NikitaConnection.getDefaultPropertySetting().getData("saving").getData("url").toString() , NikitaConnection.getDefaultPropertySetting().getData("saving").getData("user").toString(), NikitaConnection.getDefaultPropertySetting().getData("saving").getData("pass").toString());
                Nson result = Nson.newObject();
                if (transfer.equalsIgnoreCase("OK")) {
                    Nson n = saving.query("SELECT daf_biaya_admin FROM daftar_bank WHERE daf_name = ?", bank);
                    admin = n.getData("data").getData(0).getData(0).asString();
                    cn.setData("REKADMIN", Utility.getInt(admin));
                    cn.setData("APPTRXTYPE", "TRANSFER");
                    if (bank.equalsIgnoreCase("TABUNGAN")) {
                        result=saving.transfer(userid, rekno, "", "", Utility.getInt(jumlah)+Utility.getInt(admin), cn);
                    }else{
                        result=saving.requestPending(userid,  Utility.getInt(jumlah)+Utility.getInt(admin), "","","",cn);
                    }                    
                    
                }else if (transfer.equalsIgnoreCase("TOPUP")) {
                    cn.setData("APPTRXTYPE", "TOPUP");
                    result=saving.topupSaldo(rekno,  Utility.getInt(jumlah));
                }else{
                    Nson n = saving.query("SELECT daf_biaya_admin FROM daftar_pembiayaan WHERE daf_name = ?", bank);
                    admin = n.getData("data").getData(0).getData(0).asString();
                    cn.setData("REKADMIN", Utility.getInt(admin));
                    cn.setData("APPTRXTYPE", "PEMBELIAN");
                    result=saving.requestPending(userid,  Utility.getInt(jumlah)+Utility.getInt(admin), "","","",cn);
                }               
                response.setVirtual(currdata.getData("result").toString(), result.toJson());
                saving.closeConnection();
                
                
                
                
                
                
                
                
                
                
                
                
                
                
                
                
                
                
                
            }else if (param1.equalsIgnoreCase("savingtrx")) {
                String account  = response.getVirtualString(currdata.getData("param2").toString()); 
                
                String bank     = response.getVirtualString(currdata.getData("param3").toString()); 
                String norek    = response.getVirtualString(currdata.getData("param4").toString());  
                String namarek  = response.getVirtualString(currdata.getData("param5").toString());  
                
                
                
                long nominal  = Utility.getLong(response.getVirtualString(currdata.getData("param6").toString()));  
                long adminfee = Utility.getLong(response.getVirtualString(currdata.getData("param7").toString())) ;  
                
                String trtype  = response.getVirtualString(currdata.getData("param8").toString()); 
                
                
                String url    = response.getVirtualString("@CUSTOM_SAVING_URL"); 
                String usr    = response.getVirtualString("@CUSTOM_SAVING_USR"); 
                String pas    = response.getVirtualString("@CUSTOM_SAVING_PAS");                 
                Saving saving = new Saving();
                saving.openConnection(url, usr, pas);
                Nson cn = Nson.newObject();
               
                cn.setData("REKNO", norek);
                cn.setData("REKNAME", namarek);
                cn.setData("REKJUMLAH", nominal);
                cn.setData("REKADMIN", adminfee);
                cn.setData("APPTRXTYPE", trtype);
                cn.setData("APPBYUSER", "");
                cn.setData("APPRESULT", "");              
                
                
                Nson n = saving.requestPending(account, nominal+adminfee, "", "","",  cn);
                saving.closeConnection();                
                response.setVirtual("@CUSTOM_RESULT", n);
            } else if (param1.equalsIgnoreCase("savingapprov")) {
                String account   = response.getVirtualString(currdata.getData("param2").toString());                
                String notrx     = response.getVirtualString(currdata.getData("param3").toString()); 
                String remark    = response.getVirtualString(currdata.getData("param4").toString());  
                
                String url    = response.getVirtualString("@CUSTOM_SAVING_URL"); 
                String usr    = response.getVirtualString("@CUSTOM_SAVING_USR"); 
                String pas    = response.getVirtualString("@CUSTOM_SAVING_PAS");                 
                Saving saving = new Saving();
                saving.openConnection(url, usr, pas);
                Nson cn = Nson.newObject();
                 cn.setData("REKNO", "");
                cn.setData("REKNAME", "");
                cn.setData("REKJUMLAH", "");
                cn.setData("REKADMIN", "");
                cn.setData("APPTRXTYPE", "");
                cn.setData("APPBYUSER", "");
                cn.setData("APPRESULT", "");
                
                Nson n = saving.succesPending(account, notrx, cn);
                saving.closeConnection();                
                response.setVirtual("@CUSTOM_RESULT", n);
                 
            } else if (param1.equalsIgnoreCase("savingrefund")) {    
                String account   = response.getVirtualString(currdata.getData("param2").toString());                
                String notrx     = response.getVirtualString(currdata.getData("param3").toString()); 
                String remark    = response.getVirtualString(currdata.getData("param4").toString());  
                
                
                String url    = response.getVirtualString("@CUSTOM_SAVING_URL"); 
                String usr    = response.getVirtualString("@CUSTOM_SAVING_USR"); 
                String pas    = response.getVirtualString("@CUSTOM_SAVING_PAS");                 
                Saving saving = new Saving();
                saving.openConnection(url, usr, pas);
                Nson cn = Nson.newObject();
                
                 cn.setData("REKNO", "");
                cn.setData("REKNAME", "");
                cn.setData("REKJUMLAH", "");
                cn.setData("REKADMIN", "");
                cn.setData("APPTRXTYPE", "");
                cn.setData("APPBYUSER", "");
                cn.setData("APPRESULT", "");
                
                Nson n = saving.refundPending(account, notrx, cn);
                saving.closeConnection();                
                response.setVirtual("@CUSTOM_RESULT", n);
            }        
        }
        return true;
    }
    
    public class Saving {            
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
        public Nson query(String sql, String arg){
            return query(sql, Nson.newArray().addData(arg));
        }
        public Nson query(String sql, Nson args){
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
        public Nson checkSaldoPending(String account){
            try {
                conn.setAutoCommit(false);
                Nson n = query("SELECT SUM(DEBIT) FROM SAVING_HISTORY WHERE ACCOUNT=? && STATUS='PENDING' ;", Nson.newArray().addData(account)); 
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
                    conn.rollback();    
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

        private Nson transfer(String account, String accounttujuan, String userref, String usermsg, long nominal, Nson x){
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
                        
                        nson.addData(x.getData("REKNO").asString()); //REKNO
                        nson.addData(x.getData("REKNAME").asString()); //REKNAME
                        nson.addData(x.getData("REKJUMLAH").asString()); //REKJUMLAH
                        nson.addData(x.getData("REKADMIN").asString()); //REKADMIN
                        nson.addData(x.getData("APPTRXTYPE").asString()); //APPTRXTYPE
                        nson.addData(x.getData("APPBYUSER").asString()); //APPBYUSER
                        nson.addData(x.getData("APPRESULT").asString()); //APPRESULT


                        n = query("INSERT INTO SAVING_HISTORY (TRXID, ACCOUNT, KREDIT, DEBIT, STATUS, SALDO, REFERENSI, USERREFERENSI, USERMESSAGE, TRXDATE, INSERTDATE, UPDATEDATE, REKNO,REKNAME,REKJUMLAH, REKADMIN, APPTRXTYPE, APPBYUSER, APPRESULT)VALUES    (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?,?,?)", nson);            
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
                        
                        nson.addData(x.getData("REKNO").asString()); //REKNO
                        nson.addData(x.getData("REKNAME").asString()); //REKNAME
                        nson.addData(x.getData("REKJUMLAH").asString()); //REKJUMLAH
                        nson.addData(x.getData("REKADMIN").asString()); //REKADMIN
                        nson.addData(x.getData("APPTRXTYPE").asString()); //APPTRXTYPE
                        nson.addData(x.getData("APPBYUSER").asString()); //APPBYUSER
                        nson.addData(x.getData("APPRESULT").asString()); //APPRESULT
                    
                    
                        n = query("INSERT INTO SAVING_HISTORY (TRXID, ACCOUNT, KREDIT, DEBIT, STATUS, SALDO, REFERENSI, USERREFERENSI, USERMESSAGE, TRXDATE, INSERTDATE, UPDATEDATE, REKNO,REKNAME,REKJUMLAH,REKADMIN,APPTRXTYPE,APPBYUSER,APPRESULT)VALUES    (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?,?,?)", nson);            
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
        
        protected Nson requestPending(String account, long nominal, String reference, String userreference, String usermessage, Nson x){
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
                //tambah saldo account REKNO,REKNAME,REKJUMLAH,REKADMIN,APPTRXTYPE,APPBYUSER,APPRESULT
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
                    
                    nson.addData(x.getData("REKNO").asString()); //REKNO
                    nson.addData(x.getData("REKNAME").asString()); //REKNAME
                    nson.addData(x.getData("REKJUMLAH").asString()); //REKJUMLAH
                    nson.addData(x.getData("REKADMIN").asString()); //REKADMIN
                    nson.addData(x.getData("APPTRXTYPE").asString()); //APPTRXTYPE
                    nson.addData(x.getData("APPBYUSER").asString()); //APPBYUSER
                    nson.addData(x.getData("APPRESULT").asString()); //APPRESULT
                    
                    
                    n = query("INSERT INTO SAVING_HISTORY (TRXID, ACCOUNT, KREDIT, DEBIT, STATUS, SALDO, REFERENSI, USERREFERENSI, USERMESSAGE, TRXDATE, INSERTDATE, UPDATEDATE, REKNO,REKNAME,REKJUMLAH,REKADMIN,APPTRXTYPE,APPBYUSER,APPRESULT)VALUES   (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?,?,?)", nson);            
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
        public Nson refundPending(String account, String trxid, Nson x){
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
                    
                    
                    nson.addData(x.getData("REKNO").asString()); //REKNO
                    nson.addData(x.getData("REKNAME").asString()); //REKNAME
                    nson.addData(x.getData("REKJUMLAH").asString()); //REKJUMLAH
                    nson.addData(x.getData("REKADMIN").asString()); //REKADMIN
                    nson.addData(x.getData("APPTRXTYPE").asString()); //APPTRXTYPE
                    nson.addData(x.getData("APPBYUSER").asString()); //APPBYUSER
                    nson.addData(x.getData("APPRESULT").asString()); //APPRESULT
                    n = query("INSERT INTO SAVING_HISTORY (TRXID, ACCOUNT, KREDIT, DEBIT, STATUS, SALDO, REFERENSI, USERREFERENSI, USERMESSAGE, TRXDATE, INSERTDATE, UPDATEDATE, REKNO,REKNAME,REKJUMLAH,REKADMIN,APPTRXTYPE,APPBYUSER,APPRESULT)VALUES    (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?,?,?)", nson);            
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
        public Nson succesPending(String account, String trxid, Nson x){
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
                    
                    nson.addData(x.getData("REKNO").asString()); //REKNO
                    nson.addData(x.getData("REKNAME").asString()); //REKNAME
                    nson.addData(x.getData("REKJUMLAH").asString()); //REKJUMLAH
                    nson.addData(x.getData("REKADMIN").asString()); //REKADMIN
                    nson.addData(x.getData("APPTRXTYPE").asString()); //APPTRXTYPE
                    nson.addData(x.getData("APPBYUSER").asString()); //APPBYUSER
                    nson.addData(x.getData("APPRESULT").asString()); //APPRESULT
                    
                    n = query("INSERT INTO SAVING_HISTORY (TRXID, ACCOUNT, KREDIT, DEBIT, STATUS, SALDO, REFERENSI, USERREFERENSI, USERMESSAGE, TRXDATE, INSERTDATE, UPDATEDATE, REKNO,REKNAME,REKJUMLAH,REKADMIN,APPTRXTYPE,APPBYUSER,APPRESULT)VALUES    (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?,?,?)", nson);            
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
            String x = new String( longToBytes(Thread.currentThread().hashCode()) );
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
}
