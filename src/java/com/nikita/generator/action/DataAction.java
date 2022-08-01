/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator.action;

import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Vector;
import org.apache.commons.lang.StringEscapeUtils;

/**
 *
 * @author rkrzmail
 */
public class DataAction implements IAction{

    public boolean OnAction(Nset currdata, NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        String code = response.getVirtualString(currdata.getData("code").toString());  
        String param1 = response.getVirtualString(currdata.getData("args").getData("param1").toString());  
        String param2 = currdata.getData("args").getData("param2").toString();
        
        if (code.equals("geterror")) {
            Object obj = response.getVirtual(currdata.getData("args").getData("param1").toString());
            if (obj instanceof Nikitaset) {

                response.setVirtual(param2,  ((Nikitaset)obj).getError() );
            }else if (obj instanceof Nset) {
                
            }
        }else if (code.equals("setresult")) {
             
            Object o = response.getVirtual(param2);
            if (o instanceof Nset) {
                response.setResult(response.getVirtualString(currdata.getData("args").getData("param1").toString()), new Nset(response.getVirtual(currdata.getData("args").getData("param2").toString())));
            }else{
                response.setResult(response.getVirtualString(currdata.getData("args").getData("param1").toString()), Nset.readJSON(response.getVirtualString(currdata.getData("args").getData("param2").toString())));
            }
        }else if (code.equals("broadcast")) {             
            Object o = response.getVirtual(param2);
            if (o instanceof Nset) {
                response.sendBroadcast(response.getVirtualString(currdata.getData("args").getData("param1").toString()), new Nset(response.getVirtual(currdata.getData("args").getData("param2").toString())));
            }else{
                response.sendBroadcast(response.getVirtualString(currdata.getData("args").getData("param1").toString()), Nset.readJSON(response.getVirtualString(currdata.getData("args").getData("param2").toString())));
            }
         }else if (code.equals("broadcastglobal")) {             
            Object o = response.getVirtual(param2);
            if (o instanceof Nset) {
                response.sendBroadcastGlobal(response.getVirtualString(currdata.getData("args").getData("param1").toString()), new Nset(response.getVirtual(currdata.getData("args").getData("param2").toString())));
            }else{
                response.sendBroadcastGlobal(response.getVirtualString(currdata.getData("args").getData("param1").toString()), Nset.readJSON(response.getVirtualString(currdata.getData("args").getData("param2").toString())));
            }
        }else if (code.equals("broadcastlistener")) {             
            Object o = response.getVirtual(param2);
            if (o instanceof Nset) {
                response.sendBroadcastListener(response.getVirtualString(currdata.getData("args").getData("param1").toString()), new Nset(response.getVirtual(currdata.getData("args").getData("param2").toString())));
            }else{
                response.sendBroadcastListener(response.getVirtualString(currdata.getData("args").getData("param1").toString()), Nset.readJSON(response.getVirtualString(currdata.getData("args").getData("param2").toString())));
            }       
        }else if (code.equals("broadcastlisteneradd")) {   
            response.sendBroadcastListenerAdd( response.getVirtualString(currdata.getData("args").getData("param1").toString()) );
        }else if (code.equals("broadcastlistenerremove")) {      
            response.sendBroadcastListenerRemove( response.getVirtualString(currdata.getData("args").getData("param1").toString()) );
        }else if (code.equals("md5")) {
            response.setVirtual(param2,  Utility.MD5(param1) );
        }else if (code.equals("sha1")) {
            response.setVirtual(param2,  Utility.SHA1(param1) );
        }else if (code.equals("urlencode")) {
            response.setVirtual(param2, URLEncoder.encode(param1) );
        }else if (code.equals("urldecode")) {
            response.setVirtual(param2, URLDecoder.decode(param1) );
        }else if (code.equals("escapehtml")) {
            response.setVirtual(param2, StringEscapeUtils.escapeHtml(param1) );
        }else if (code.equals("unescapehtml")) {
            response.setVirtual(param2, StringEscapeUtils.unescapeHtml(param1) );
         }else if (code.equals("escapesql")) {
            response.setVirtual(param2, StringEscapeUtils.escapeSql(param1) );
         }else if (code.equals("escapecsv")) {
            response.setVirtual(param2, StringEscapeUtils.escapeCsv(param1) );
        }else if (code.equals("unescapecsv")) {
            response.setVirtual(param2, StringEscapeUtils.unescapeCsv(param1) );
        }else if (code.equals("nikitaset")) {
            Object o = response.getVirtual(param2);
 
            if (o instanceof Nikitaset) {
                Nikitaset ns = (Nikitaset)o;
                 
                String row = response.getVirtualString(currdata.getData("args").getData("param3").toString());  
                String col = response.getVirtualString(currdata.getData("args").getData("param4").toString());  
             
                if (row.equals("") && col.equals("")) {
                    response.setVirtual( currdata.getData("args").getData("result").toString(), new Nset(ns.getDataAllVector()).toJSON() );
                } else if (row.equals("")) {
                    //all rows with col
                    
                    String[] s = Utility.split(col, ",");
                    int[] cols =new int[s.length];
                    for (int i = 0; i < s.length; i++) {
                        cols[i] = Utility.getInt(s[i]);                        
                    }                    
                    response.setVirtual( currdata.getData("args").getData("result").toString(), new Nset(ns.copyDataAllVectorAtCol(cols)).toJSON() );
                } else if (col.equals("")) {
                    //all cols on current row     
                    try {
                        response.setVirtual( currdata.getData("args").getData("result").toString(),  new Nset(ns.getDataAllVector().elementAt(Utility.getInt(row))).toJSON() );
                    } catch (Exception e) {}
                }else if (Utility.isNumeric(col)) {                    
                    response.setVirtual( currdata.getData("args").getData("result").toString(), ns.getText(Utility.getInt(row), Utility.getInt(col))  );
                }else{
                    response.setVirtual( currdata.getData("args").getData("result").toString(), ns.getText(Utility.getInt(row), col)  );
                }
               
                String nset = currdata.getData("args").getData("param6").toString(); 
                response.setVirtual( nset, new Nset(ns.getDataAllVector()) );
 
            }   
        }else if (code.equals("nikitasetadd")) {
            Object o = response.getVirtual(currdata.getData("args").getData("result").toString()); 
            if (o instanceof Nikitaset) {
                Nikitaset ns = (Nikitaset)o;
                
                Vector<String> col = new Vector<String>();
                for (int i = 0; i < ns.getCols(); i++) {
                    col.addElement( response.getVirtualString(currdata.getData("args").getData("param"+i).toString()) );
                }
                ns.getDataAllVector().addElement(col);
                                 
            }
        }else if (code.equals("nikitasetupd")||code.equals("nikitasetreplace")) {
            Object o = response.getVirtual(param2); 
            if (o instanceof Nikitaset) {
                Nikitaset ns = (Nikitaset)o;
                
                String row = response.getVirtualString(currdata.getData("args").getData("param3").toString());  
                String col = response.getVirtualString(currdata.getData("args").getData("param4").toString());  
                String newdata = response.getVirtualString(currdata.getData("args").getData("param5").toString());  
                
                
                if (!Utility.isNumeric(col)) {
                    for (int i = 0; i < ns.getCols(); i++) {
                        if (ns.getHeader(i).equals(col)) {
                            col=i+"";
                            break;
                        }
                    }
                }
                
                int r = Utility.getInt(row);
                int c = Utility.getInt(col);
                
                Vector<Vector<String>> vector = ns.getDataAllVector();
                if (vector.size()>r) {
                    if (vector.elementAt(r).size()>c) {
                        vector.elementAt(r).setElementAt(newdata, c);
                    }  
                }                
            }
        }else if (code.equals("newnikitaset")) {                      
                    
            Nset n = Nset.newObject();
            n.setData("header", new Nset(response.getVirtual(currdata.getData("args").getData("param1").toString())));
            Nset data = new Nset(response.getVirtual(currdata.getData("args").getData("param2").toString()));
            if (data.isNsetArray()) {
                n.setData("data", data);
            }else{
                n.setData("data", Nset.newArray());
            }            
            n.setData("info", new Nset(response.getVirtual(currdata.getData("args").getData("param3").toString())));
            n.setData("error", response.getVirtualString(currdata.getData("args").getData("param4").toString()));
            Nikitaset ns = new Nikitaset(n);
            
            response.setVirtual( currdata.getData("args").getData("result").toString(), ns  );
         
        } 
       
        return true;
    }
    
}
