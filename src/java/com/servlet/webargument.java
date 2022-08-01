/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.servlet;

import com.nikita.generator.Component;
import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaService;
import static com.nikita.generator.NikitaService.getFileSeparator;
import com.nikita.generator.NikitaServlet;
import com.nikita.generator.Style;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.connection.NikitaInternet;
import com.nikita.generator.storage.NikitaStorage;
import com.nikita.generator.ui.Button;
import com.nikita.generator.ui.Label;
import com.nikita.generator.ui.Textarea;
import com.nikita.generator.ui.Textbox;
import com.nikita.generator.ui.Textsmart;
import com.nikita.generator.ui.layout.DivLayout;
import com.nikita.generator.ui.layout.HorizontalLayout;
import com.nikita.generator.ui.layout.NikitaForm;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import com.web.utility.WebUtility;
import java.util.Hashtable;

/**
 *
 * @author rkrzmail
 */
public class webargument extends NikitaServlet{
    boolean findsetting = false;
    Label fid ;Label fname ;
    int dbCore;
    NikitaConnection nikitaConnection ;
    
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        final NikitaForm nf = new NikitaForm(this);
        
        nikitaConnection =response.getConnection(NikitaConnection.LOGIC);
        dbCore = WebUtility.getDBCore(nikitaConnection);
         
        Style style = new Style();
        style.setStyle("width", "400");
        style.setStyle("height", "400");
        style.setStyle("n-maximizable", "false");
        nf.setStyle(style);
        
         //add 13K
        fid = new Label();
        fid.setId("fid");
        fid.setText(request.getParameter("fid"));
        fid.setVisible(false);
        nf.addComponent(fid);        
        
        
        fname = new Label();
        fname.setId("fname");
        fname.setText(request.getParameter("fname"));
        fname.setVisible(false);
        nf.addComponent(fname);   
        
        nf.setText("Argument ["+fname.getText()+"]");
        
        request.retainData(fid,fname);
        
        Nset n =  Nset.newArray();
        
        //,"code":"arg","class":"DefinitionAction"
        Nikitaset nikiset = nikitaConnection.QueryPage(1,100,"SELECT web_route.compid,web_route.action,web_route.expression FROM web_component LEFT JOIN web_route ON (web_component.compid=web_route.compid) WHERE formid = ? AND web_route.compid is not null AND web_route.action LIKE '%\"class\":\"DefinitionAction\"%' ", fid.getText());
        for (int i = 0; i < nikiset.getRows(); i++) {            
                Nset nv = Nset.readJSON(nikiset.getText(i, 1));
                String[] keys = nv.getData("args").getObjectKeys();
                
                if (nv.getData("class").toString().equals("DefinitionAction") && nv.getData("code").toString().equals("arg") ) {
                    for (int j = 0; j < keys.length; j++) {
                        String s = nv.getData("args").getData(keys[j]).toString();
                         if (s.startsWith("@+")) {
                            s=s.substring(2);
                        }else if (s.startsWith("@")) {
                            s=s.substring(1);
                        }else if (s.startsWith("$#")) {
                            s=s.substring(2);
                        }else if (s.startsWith("$")) {
                            s=s.substring(1);
                        }else if (s.startsWith("&")) {
                            s=s.substring(1);
                        }
                        
                        if (s.trim().equals("")) {
                        }else if ( !n.getInternalObject().toString().contains("\""+s+"\"") ) {
                            n.addData(Nset.newObject().setData("id", s).setData("text", s));
                        }
                    }
               }

        }       
    
        nikiset = nikitaConnection.QueryPage(1,1,"SELECT settingvalue FROM sys_setting WHERE settingusername=? AND settingkey=?", response.getVirtualString("@+SESSION-LOGON-USER"),"link-arg-" + fid.getText());
        Nset nbuff = Nset.readJSON(nikiset.getText(0, 0)); 
        findsetting = nikiset.getRows()>=1;
                
                
        n = Nset.newObject().setData("args", n);
        for (int i = 0; i < n.getData("args").getArraySize(); i++) {    
            Nset v = n.getData("args").getData(i);
            //v.getData("id").toString()
            
            Textsmart txt = new Textsmart();
            txt.setId("arg-"+v.getData("id").toString());
            txt.setLabel(v.getData("id").toString()); 
            txt.setText(nbuff.getData(v.getData("id").toString()).toString());
            
            nf.addComponent(txt);   
        }   
        
        DivLayout horisontalLayout = new DivLayout();
               
        Button
        btn = new Button();
        btn.setId("clear");
        btn.setText("Clear All");
        style = new Style();
        style.setStyle("width", "100px");
        style.setStyle("height", "35px");
        style.setStyle("float", "left");
        btn.setStyle(style); 
              
        btn.setOnClickListener(new Component.OnClickListener() {
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                for (int i = 0; i < nf.getComponentCount(); i++) {
                    if (nf.getComponent(i).getId().startsWith("arg-")) {
                       nf.getComponent(i).setText("");
                    }                    
                }
                
                response.writeContent();
            }
        });
        horisontalLayout.addComponent(btn);
        btn = new Button();
        btn.setId("post");
        btn.setText("Send Post");
        style = new Style();
        style.setStyle("width", "130px");
        style.setStyle("height", "35px");     
        style.setStyle("float", "right");
        btn.setStyle(style); 
        horisontalLayout.addComponent(btn);
        
        btn.setOnClickListener(new Component.OnClickListener() {
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
               StringBuffer sb = new StringBuffer();
                sb.append(component.getBaseUrl ("/base/") ).append("").append(fname.getText()).append("/?");
                Nset nbuff = Nset.newObject();
                Hashtable<String, String> args = new Hashtable<String, String>();
                for (int i = 0; i < nf.getComponentCount(); i++) {
                    if (nf.getComponent(i).getId().startsWith("arg-")) {
                       //sb.append("&").append(nf.getComponent(i).getId().substring(4)).append("=").append(Utility.urlEncode(nf.getComponent(i).getText()));
                       nbuff.setData(nf.getComponent(i).getId().substring(4), nf.getComponent(i).getText());
                       args.put( nf.getComponent(i).getId().substring(4), nf.getComponent(i).getText());
                    }                    
                }
                Nikitaset ns;
                if (findsetting) {
                   ns= nikitaConnection.QueryPage(1,1,"UPDATE sys_setting SET settingvalue=?,modifiedby=?,modifieddate="+WebUtility.getDBDate(dbCore)+"  WHERE settingusername=? AND settingkey=?", nbuff.toJSON(),response.getVirtualString("@+SESSION-LOGON-USER"), response.getVirtualString("@+SESSION-LOGON-USER"),"link-arg-" + fid.getText());
                }else{
                   ns=nikitaConnection.QueryPage(1,1,"INSERT INTO sys_setting  (settingusername,settingkey, settingvalue,createdby,createddate ) VALUES (?,?,?,?,"+WebUtility.getDBDate(dbCore)+")", response.getVirtualString("@+SESSION-LOGON-USER"),"link-arg-" + fid.getText(),nbuff.toJSON(),response.getVirtualString("@+SESSION-LOGON-USER"));
                } 
                
                
                String file = Utility.MD5(System.currentTimeMillis()+response.getVirtualString("@+RANDOM"));
                try {
                    Nset n = Nset.newObject();
                    n.setData("filename", file);
                    n.setData("file","post_"+ file);
                    n.setData("inline", "true");
                    //NikitaStorage nikitaStorage = NikitaStorage.getTemporary();
                    //nikitaStorage.writeStorage("post_"+fname+".tmp",  NikitaInternet.postHttp(sb.toString(), args).getEntity().getContent());
                    Utility.copyFile(NikitaInternet.postHttp(sb.toString(), args).getEntity().getContent(), NikitaService.getDirTmp()+ getFileSeparator() +"post_"+file+".tmp");
                    
                    //response.setVirtualRegistered("@+SESSION-"+fname, n.toJSON()); 
                    file = Utility.encodeBase64(n.toJSON());
                } catch (Exception e) {  }
                    
            
                
                //response.openWindows("webbrowser/"+sb.toString(), "_blank");
                request.setParameter("url", component.getBaseUrl ("/base/res/file/?BufferForm="+Utility.encodeURL(fname.getText())+"&view="+file) );
                response.showformGen("webbrowser", request, "", true);
            }
        });
        
        
        btn = new Button();
        btn.setId("hit");
        btn.setText("Send Get");
        style = new Style();
        style.setStyle("width", "130px");
        style.setStyle("height", "35px");        
        style.setStyle("float", "right");
        btn.setStyle(style); 
              
        btn.setOnClickListener(new Component.OnClickListener() {
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
               StringBuffer sb = new StringBuffer();
               sb.append(component.getBaseUrl ("/base/"+fid.getText()) ).append("/").append(fname.getText()).append("/?");
               Nset nbuff = Nset.newObject();
                for (int i = 0; i < nf.getComponentCount(); i++) {
                    if (nf.getComponent(i).getId().startsWith("arg-")) {
                       sb.append("&").append(nf.getComponent(i).getId().substring(4)).append("=").append(Utility.urlEncode(nf.getComponent(i).getText()));
                       nbuff.setData(nf.getComponent(i).getId().substring(4), nf.getComponent(i).getText());
                    }                    
                }
                Nikitaset ns;
                if (findsetting) {
                   ns= nikitaConnection.QueryPage(1,1,"UPDATE sys_setting SET settingvalue=?,modifiedby=?,modifieddate="+WebUtility.getDBDate(dbCore)+"  WHERE settingusername=? AND settingkey=?", nbuff.toJSON(),response.getVirtualString("@+SESSION-LOGON-USER"), response.getVirtualString("@+SESSION-LOGON-USER"),"link-arg-" + fid.getText());
                }else{
                   ns=nikitaConnection.QueryPage(1,1,"INSERT INTO sys_setting  (settingusername,settingkey, settingvalue,createdby,createddate ) VALUES (?,?,?,?,"+WebUtility.getDBDate(dbCore)+")", response.getVirtualString("@+SESSION-LOGON-USER"),"link-arg-" + fid.getText(),nbuff.toJSON(),response.getVirtualString("@+SESSION-LOGON-USER"));
                } 
                //response.openWindows("webbrowser/"+sb.toString(), "_blank");
                request.setParameter("url", sb.toString());
                response.showformGen("webbrowser", request, "", true);
            }
        });
        
        
        horisontalLayout.addComponent(btn);
        
        
        nf.addComponent(horisontalLayout);  
        
        response.setContent(nf);
               
    }
    
    
}
