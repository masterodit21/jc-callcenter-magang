/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator;

import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.connection.NikitaInternet;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import java.util.Hashtable;
import org.apache.http.HttpResponse;

/**
 *
 * @author rkrzmail
 */
public class NikitaScheduller implements Runnable{
    private String schname;
    public NikitaScheduller(String schname){
        this.schname=schname;
    }
    
    public static void runSchedullerAndWait(String schname) { 
        new NikitaScheduller(schname).run();
    }
    public static void runSchedullerTask(String schname) { 
        
    }
    
    public void run() {
        long istart = System.currentTimeMillis();
        NikitaConnection nikitaConnection = NikitaConnection.getConnection(NikitaConnection.LOGIC);
        //insrt web_schedulle
        String id = insertSchedullerHistory(nikitaConnection);
        String trckid = nikitaConnection.Query("SELECT threadid FROM web_scheduller WHERE threadname = ?   ", schname).getText(0, 0);
        Nikitaset nikitaset =  nikitaConnection.Query("SELECT threadid, action, expression FROM web_scheduller_task WHERE threadid = ?  ORDER BY taskindex ASC ", trckid);        
        System.err.println("nikitaset.id"+trckid+":"+schname);
        System.err.println("nikitaset.getRows()"+nikitaset.getRows());
        for (int i = 0; i < nikitaset.getRows(); i++) {
            long iTaskStart = System.currentTimeMillis();
            
            Nset exp = Nset.readJSON(nikitaset.getText(i, "expression"));
            if (execExpression(exp)) {
                String iTask = insertSchedullerHistory(nikitaConnection);
                 
                Nset action = Nset.readJSON(nikitaset.getText(i, "action"));               
                if (action.getData("tmode").toString().equals("connection")) {
                    NikitaConnection nc = NikitaConnection.getConnection(action.getData("connection").toString());
                    if (!nc.getError().equals("")) {
                        setVirtualString(action.getData("error").toString(), nc.getError());     
                        setVirtualString(action.getData("result").toString(), "");   
                    }else{
                        Nikitaset ns = nc.Call(action.getData("sprocedure").toString(), action);
                        if (!ns.getError().equals("")) {
                            setVirtualString(action.getData("error").toString(), nc.getError());
                            setVirtualString(action.getData("result").toString(), "");   
                        }else{
                            setVirtualString(action.getData("error").toString(), "");
                            setVirtualString(action.getData("result").toString(), ns.toNset().toJSON());   
                        }
                    }                  
                    
                    updateSchedullerHistory(nikitaConnection, iTask, iTaskStart);
                }else if (action.getData("tmode").toString().equals("multiconnection")) {
                    
                    
                    updateSchedullerHistory(nikitaConnection, iTask, iTaskStart);
                }else if (action.getData("tmode").toString().equals("ntask")) {
                    Hashtable<String, String> args = new Hashtable<String, String>();
                    HttpResponse httpResponse = NikitaInternet.postHttp(NikitaService.getBaseUrl() + "/" + action.getData("ntask").toString()+ "/", args);
                    //httpResponse.getStatusLine().getStatusCode()
                    System.err.println("nikitatask:"+NikitaService.getBaseUrl() + "/" + action.getData("ntask").toString()+ "/");
                    System.err.println(NikitaInternet.getString(httpResponse));
                    updateSchedullerHistory(nikitaConnection, iTask, iTaskStart);
                }else if (action.getData("tmode").toString().equals("http")) {
                    Hashtable<String, String> args = new Hashtable<String, String>();
                    HttpResponse httpResponse = NikitaInternet.postHttp(action.getData("url").toString(), args);          
                    int status = httpResponse.getStatusLine().getStatusCode();
                    if (status>=200 && status<300) {
                        setVirtualString("", "");
                        String s = NikitaInternet.getString(httpResponse);
                        
                    }else{
                        setVirtualString(action.getData("error").toString(), status+":");
                        setVirtualString(action.getData("result").toString(), "");
                    }                    
                    updateSchedullerHistory(nikitaConnection, iTask, iTaskStart);
                }else if (action.getData("tmode").toString().equals("stop")||action.getData("code").toString().equals("break")) {
                    String note = getVirtualString(action.getData("note").toString());
                    
                    
                    updateSchedullerHistory(nikitaConnection, iTask, iTaskStart);
                    break;//stop hire
                }else if (action.getData("tmode").toString().equals("deactivate")||action.getData("code").toString().equals("inactivate")) {
                    callDeactivateScheduller(nikitaset.getText(i, "threadid"));
                    
                    Hashtable<String, String> args = new Hashtable<String, String>();
                    NikitaInternet.postHttp(NikitaService.getBaseUrl() + "/res/scheduller/deactivate" , args);                    
                    
                    updateSchedullerHistory(nikitaConnection, iTask, iTaskStart);
                    break;
                } else{                    
                    setVirtualString(action.getData("error").toString(), "action not found");
                    setVirtualString(action.getData("result").toString(), "");
                    
                    updateSchedullerHistory(nikitaConnection, iTask, iTaskStart);
                }  
                
            }             
        }
  
        
        //update
        updateSchedullerHistory(nikitaConnection, id, istart);
        nikitaConnection.closeConnection();
    }
    
    private String  insertSchedullerHistory(NikitaConnection nikitaConnection){        
        String id = nikitaConnection.Query("INSERT INTO web_scheduller_history (threadname, taskname, startdate, finishdate, status, activitystatus, createdby, createddate, modifiedby, modifieddate) VALUES ('1', '1', '2015-10-07 00:00:00', '2015-10-07 00:00:00', '0', '1', '1', '2015-10-07 00:00:00', '1', '2015-10-07 00:00:00');", "").getText(0, 0);
       
        return id;
    }
    private void updateSchedullerHistory(NikitaConnection nikitaConnection, String id , long start){
        Hashtable<String, String> args = new Hashtable<String, String>();
//        if (args!=null) {
//            StringBuffer stringBuffer = new StringBuffer();
//            Nset narg = new Nset(args);
//            String[] keys = narg.getObjectKeys();
//            for (int i = 0; i < args.size(); i++) {
//                                
//                    int split = paramvalue[i].indexOf("=");String sdata = Utility.urlEncode(paramvalue[i].substring(split+1));
//                    stringBuffer.append(keys[i]).append("=").append(sdata).append("&");
//                }				
//            }		
//            url = url + (url.contains("?")?"&":"?")+stringBuffer.toString(); 
//        } 
        
        nikitaConnection.Query("UPDATE web_scheduller_history SET finishdate, status, activitystatus='finish', createdby, createddate, modifiedby, modifieddate WHERE historyid=?", id);
        
    }
    
    private Hashtable<String, String> parameter = new Hashtable<String, String>();
    private String getVirtualString(String name){
        if (name.startsWith("@")||name.startsWith("$")||name.startsWith("!")) {
            if (parameter.get(name)!=null) {
                return (String)parameter.get(name);
            }else{
               return ""; 
            }            
        }
        return name;
    }
    private void setVirtualString(String name, String value){
        if (name.startsWith("@")||name.startsWith("$")||name.startsWith("!")) {
            parameter.put(name, value);
        }       
    }  
    private boolean execExpression(Nset exp){
        String param1 = getVirtualString(exp.getData("param1").toString());
        String parama = getVirtualString(exp.getData("parama").toString());
        String param2 = getVirtualString(exp.getData("param2").toString());
        String paramb = getVirtualString(exp.getData("paramb").toString());
        String param3 = getVirtualString(exp.getData("param3").toString());
        String paramc = getVirtualString(exp.getData("paramc").toString());
        String param4 = getVirtualString(exp.getData("param4").toString());
        String paramd = getVirtualString(exp.getData("paramd").toString());
        String param5 = getVirtualString(exp.getData("param5").toString());
        String param6 = getVirtualString(exp.getData("param6").toString());
             
        boolean b = iif(iif(param1, parama, param2),  paramb, iif(param3, paramc, param4));
        return iif(b, paramd, iif(param5, "=", param6));

    }
    private boolean iif(boolean b1, String exp, boolean b2){
        if (exp.equals("OR")) {
            return b1 || b2;
        }else if (exp.equals("AND")) {
            return b1 &&  b2;
        }else{
            return b1;
        }
    }
    private boolean iif(String s1, String exp ,String s2 ){
       
        if (exp.equals("<>")) {
            return !(s1.equals(s2)); 
        }else if (exp.equals("=")) {
            return s1.equals(s2); 
        }else if (exp.equals(">=")) { 
            return Utility.getLong(s1) >= Utility.getLong(s2) ;
        }else if (exp.equals(">")) {   
            return Utility.getLong(s1) > Utility.getLong(s2) ;
        }else if (exp.equals("<=")) {
            return Utility.getLong(s1) <= Utility.getLong(s2) ;
        }else if (exp.equals("<")) {
            return Utility.getLong(s1) < Utility.getLong(s2) ;
        }else if (exp.equals("equalignorecase")) {    
            return s1.equalsIgnoreCase(s2); 
        }else if (exp.equals("containignorecase")) {
            return s1.toLowerCase().contains(s2.toLowerCase()); 
        }else if (exp.equals("contain")) {
            return s1.contains(s2); 
        }else if (exp.equals("startwithcignorecase")) {
            return s1.toLowerCase().startsWith(s2.toLowerCase()); 
        }else if (exp.equals("startwith")) {
            return s1.startsWith(s2); 
        }else if (exp.equals("endwithignorecase")) {   
            return s1.toLowerCase().endsWith(s2.toLowerCase()); 
        }else if (exp.equals("endwith")) {       
            return s1.endsWith(s2); 
        }else if (exp.equals("error")) {       
            return !Nset.readJSON(s1).getData("error").toString().equals(""); 
        }else{
            return s1.equals(s2);//default equal
        }
    }
    
   
    private void callStopCurrentScheduller(String threadid){
        callDeactivateScheduller(threadid);
    }
    private void callDeactivateScheduller(String threadid){
        //callDeactivateScheduller
    }
}
