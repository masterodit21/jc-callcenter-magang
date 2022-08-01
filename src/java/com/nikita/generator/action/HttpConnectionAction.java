/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator.action;

import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaService;
import static com.nikita.generator.NikitaService.executorQueue;
import com.nikita.generator.connection.NikitaInternet;
import com.rkrzmail.nikita.data.Nset;
import com.web.utility.NikitaLDAP;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Hashtable;
import org.apache.http.HttpResponse;

/**
 *
 * @author rkrzmail
 */
public class HttpConnectionAction  implements IAction{

    public boolean OnAction(Nset currdata, NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        String code = response.getVirtualString(currdata.getData("code").toString());  
        String param1 =response.getVirtualString( currdata.getData("args").getData("param1").toString());//getpost  
        String param2 = response.getVirtualString(currdata.getData("args").getData("param2").toString());//url
        Object param3 = response.getVirtual(currdata.getData("args").getData("param3").toString());//nset param
        String param4 = response.getVirtualString(currdata.getData("args").getData("param4").toString());//string param
        Object hdr5 = response.getVirtual(currdata.getData("args").getData("param5").toString());//header
        String param6  = response.getVirtualString(currdata.getData("args").getData("param6").toString());//string param
        String result = "";
        
        if (code.equals("http")) {
            if (param2.startsWith("generator://")) {
                 return new UrlGeneratorAction().OnAction(currdata, request, response, logic);
            }else if (param1.equals("POST")) {
                Hashtable hashtable = new Hashtable();
                if (param3 instanceof Nset) {
                    if (((Nset)param3).getInternalObject() instanceof Hashtable) {
                        hashtable = (Hashtable)((Nset)param3).getInternalObject();
                    }
                }else if (param3 instanceof Hashtable) {
                    hashtable = (Hashtable)param3;
                }       
                
                Hashtable header = new Hashtable();
                if (hdr5 instanceof Nset) {
                    header = (Hashtable)((Nset)hdr5).getInternalObject();
                }else if(String.valueOf(hdr5).trim().equals("")||String.valueOf(hdr5).equals("null")){                    
                }else{
                    header = (Hashtable)((Nset.readJSON(String.valueOf(hdr5)))).getInternalObject();
                }
                    
                if (currdata.getData("args").getData("param9").toString().equalsIgnoreCase("@ASYNC")) {
                    final String asUrl =  param2+param4;
                    final Hashtable<String, String> asHeader  = header ;   
                    final Hashtable<String, String> asArgs  = hashtable ;   
                    try {
                        NikitaService.schedulerHttp.submit(new Runnable() {
                            @Override
                            public void run() {
                                NikitaInternet.postHttp(asUrl, asHeader, asArgs);
                            }
                        });  
                    } catch (Exception e) { }     
                    return true;
                }else if (currdata.getData("args").getData("param9").toString().equalsIgnoreCase("@QUEUE")) {
                    final String asUrl =  param2+param4;
                    final Hashtable<String, String> asHeader  = header ;   
                    final Hashtable<String, String> asArgs  = hashtable ;   
                    try {
                        if (NikitaService.executorQueue.getQueue().size()<=0) {
                            NikitaService.executorQueue.execute(new Runnable() {
                                @Override
                                public void run() {
                                    NikitaInternet.postHttp(asUrl, asHeader, asArgs);
                                }
                            });  
                        }                        
                    } catch (Exception e) { }     
                    return true;
                }  
                
                HttpResponse httpResponse = null;
                if (param6.trim().length()>=3) {
                    httpResponse = NikitaInternet.postHttpBody(param2+param4, header, param6);
                }else{
                    httpResponse = NikitaInternet.postHttp(param2+param4, header, hashtable);
                }
                
                
                
                try {
                    response.setVirtual(currdata.getData("args").getData("param8").toString(), httpResponse.getStatusLine().getStatusCode());
                } catch (Exception e) {
                    response.setVirtual(currdata.getData("args").getData("param8").toString(), "");
                }
                InputStream is; StringBuffer sb = new StringBuffer();
                try {
                    is = httpResponse.getEntity().getContent();                    
                    byte[] buffer = new byte[1024];int length;
                    while ((length = is.read(buffer)) > 0) {
                        sb.append(new String(buffer, 0, length));
                    }
                } catch (Exception ex) { sb.append(Nset.newObject().setData("error", ex.getMessage()).setData("nfid", "error").toJSON());  }                
                           
                response.setVirtual(currdata.getData("args").getData("param9").toString(), sb.toString());
            }else{//get
                Hashtable hashtable = new Hashtable();
                if (param3 instanceof Nset) {
                    if (((Nset)param3).getInternalObject() instanceof Hashtable) {
                        hashtable = (Hashtable)((Nset)param3).getInternalObject();
                    }
                }else if (param3 instanceof Hashtable) {
                    hashtable = (Hashtable)param3;
                }  
                
                 Hashtable header = new Hashtable();
                if (hdr5 instanceof Nset) {
                    header = (Hashtable)((Nset)hdr5).getInternalObject();
                }else if(String.valueOf(hdr5).trim().equals("")||String.valueOf(hdr5).equals("null")){                    
                }else{
                    header = (Hashtable)((Nset.readJSON(String.valueOf(hdr5)))).getInternalObject();
                }
                    
                
                Nset p = new Nset(hashtable);
                String[] keys = p.getObjectKeys();
                StringBuffer stringBuffer = new StringBuffer();
                for (int i = 0; i < keys.length; i++) {
                    stringBuffer.append("&");
                    stringBuffer.append(keys[i]);
                    stringBuffer.append("=");
                    stringBuffer.append(URLEncoder.encode(  p.getData(keys[i]).toString()  ));
                }                 
                  
                if (currdata.getData("args").getData("param9").toString().equalsIgnoreCase("@ASYNC")) {
                    final String asUrl = param2+(param2.contains("?")?"":"?")+stringBuffer.toString()+param4;
                    final Nset asHeader  = new Nset(header) ;                   
                    try {
                        NikitaService.schedulerHttp.submit(new Runnable() {
                            @Override
                            public void run() {
                                HttpResponse httpResponse = NikitaInternet.getHttp(asUrl, asHeader);
                            }
                        });  
                    } catch (Exception e) { }                  
                    return true;
                }
                        
                HttpResponse httpResponse = NikitaInternet.getHttp(param2+(param2.contains("?")?"":"?")+stringBuffer.toString()+param4, new Nset(header) );
                try {
                    response.setVirtual(currdata.getData("args").getData("param8").toString(), httpResponse.getStatusLine().getStatusCode());
                } catch (Exception e) {
                    response.setVirtual(currdata.getData("args").getData("param8").toString(), "");
                    
                }                    
                
                InputStream is; StringBuffer sb = new StringBuffer();
                try {
                    is = httpResponse.getEntity().getContent();                    
                    byte[] buffer = new byte[1024];int length;
                    while ((length = is.read(buffer)) > 0) {
                        sb.append(new String(buffer, 0, length));
                    }
                } catch (Exception ex) { sb.append(Nset.newObject().setData("error", ex.getMessage()).setData("nfid", "error").toJSON());  }                
                
                response.setVirtual(currdata.getData("args").getData("param9").toString(), sb.toString());
            }
        }else if (code.equals("multipart")) {
            Hashtable hashtable = new Hashtable();
            if (param3 instanceof Nset) {
                if (((Nset)param3).getInternalObject() instanceof Hashtable) {
                    hashtable = (Hashtable)((Nset)param3).getInternalObject();
                }
            }else if (param3 instanceof Hashtable) {
                hashtable = (Hashtable)param3;
            }             
                
            HttpResponse httpResponse = NikitaInternet.multipartHttp(param1+param2, hashtable, null, null);
            try {
                response.setVirtual(currdata.getData("args").getData("param8").toString(), httpResponse.getStatusLine().getStatusCode());
            } catch (Exception e) {
                response.setVirtual(currdata.getData("args").getData("param8").toString(), "");
            } 
            
            InputStream is; StringBuffer sb = new StringBuffer();
                try {
                    is = httpResponse.getEntity().getContent();                    
                    byte[] buffer = new byte[1024];int length;
                    while ((length = is.read(buffer)) > 0) {
                        sb.append(new String(buffer, 0, length));
                    }
                } catch (Exception ex) {  }                
                response.setVirtual(currdata.getData("args").getData("param9").toString(), sb.toString());
        }else if (code.equals("ldap")) {
            String usr =response.getVirtualString( currdata.getData("args").getData("param3").toString());//getpost  
            String psw =response.getVirtualString( currdata.getData("args").getData("param4").toString());//getpost  
            
            try {
                    if (param1.equals("direct")) {
                        boolean  res = NikitaLDAP.ldapNikitaDirect(param2, "com.sun.jndi.ldap.LdapCtxFactory", usr, psw);
                        response.setVirtual(currdata.getData("args").getData("result").toString(), res);
                    }else if (param1.equals("other")) {
                        String res = NikitaLDAP.ldapNikita(param2,"", usr, psw, "");
                        response.setVirtual(currdata.getData("args").getData("result").toString(), res);
                    }else{
                        String res = NikitaLDAP.ldapNikita(param2, usr, psw);
                        response.setVirtual(currdata.getData("args").getData("result").toString(), res);
                    }
            } catch (Exception e) {
                response.setVirtual(currdata.getData("args").getData("result").toString(), String.valueOf(e));
            }
        }else{
            response.setVirtual(currdata.getData("args").getData("param8").toString(), "");
            response.setVirtual(currdata.getData("args").getData("param9").toString(), "");
        }              
        return true;
    }
    
    
}
