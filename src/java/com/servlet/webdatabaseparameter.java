/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.servlet;

import com.nikita.generator.Component;
import com.nikita.generator.ComponentGroup;
import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaServlet;
import com.nikita.generator.Style;
import com.nikita.generator.action.ConnectionAction;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.ui.Button;
import com.nikita.generator.ui.Checkbox;
import com.nikita.generator.ui.Combobox;
import com.nikita.generator.ui.Combolist;
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
import java.util.Vector;
 

/**
 *
 * @author user
 */
public class webdatabaseparameter extends NikitaServlet{
    boolean findsetting = false;
    Label fsql ;Label fcallz ;Label fconn ;Label fargs ;Label fargsname ;
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
        nf.setStyle(style);
        
         //add 13K
        fsql = new Label();
        fsql.setId("fsql");
        fsql.setText(request.getParameter("sql"));
        fsql.setVisible(false);
        nf.addComponent(fsql);        
        
        
        fcallz = new Label();
        fcallz.setId("fcallz");
        fcallz.setText(request.getParameter("callz"));
        fcallz.setVisible(false);
        nf.addComponent(fcallz);   
        
        fconn = new Label();
        fconn.setId("fconn");
        fconn.setText(request.getParameter("conn"));
        fconn.setVisible(false);
        nf.addComponent(fconn);        
        
        
        fargs = new Label();
        fargs.setId("fargs");
        fargs.setText(request.getParameter("args"));
        fargs.setVisible(false);
        nf.addComponent(fargs);   
        
        
        fargsname = new Label();
        fargsname.setId("fargsname");
        fargsname.setText(request.getParameter("argsname"));
        fargsname.setVisible(false);
        nf.addComponent(fargsname);   
        
        nf.setText("Argument [Databases]");
        
        request.retainData(fsql,fcallz,fconn,fargs,fargsname);
        
        Nset n =  Nset.readJSON(fargsname.getText());
          //response.openWindows("webbrowser/"+sb.toString(), "_blank");
                //request.setParameter("sql",n.getData("query").toString());
                //request.setParameter("callz",n.getData("call").toString());
                //request.setParameter("conn", n.getData("conn").toString()); 
                //request.setParameter("args", n.getData("args").toJSON()); 
                
             
    
        Nikitaset nikiset = nikitaConnection.QueryPage(1,1,"SELECT settingvalue FROM sys_setting WHERE settingusername=? AND settingkey=?", response.getVirtualString("@+SESSION-LOGON-USER"),"databases-arg-"+fconn.getText());
        Nset nbuff = Nset.readJSON(nikiset.getText(0, 0)); 
        findsetting = nikiset.getRows()>=1;
                
                
        
        for (int i = 0; i < n.getArraySize(); i++) {    
            Nset v = n.getData(i);
            //v.getData("id").toString()
            
            Textsmart txt = new Textsmart();
            txt.setId("arg-"+i);
            txt.setLabel(v.toString().equals("")?" ":v.toString()); 
            if (v.toString().startsWith("@")||v.toString().startsWith("$")) {
                txt.setText(nbuff.getData(v.toString()).toString());
            }else{
                txt.setText(v.toString());
            }
            
            
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
        btn.setId("hit");
        btn.setText("Execute");
        style = new Style();
        style.setStyle("width", "130px");
        style.setStyle("height", "35px");        
        style.setStyle("float", "right");
        btn.setStyle(style); 
              
        btn.setOnClickListener(new Component.OnClickListener() {
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                Nset newargs =Nset.newArray();
                Nset nbuff = Nset.newObject();
                for (int i = 0; i < nf.getComponentCount(); i++) {
                    if (nf.getComponent(i).getId().startsWith("arg-")) {
                      nbuff.setData(nf.getComponent(i).getLabel(), nf.getComponent(i).getText());
                      newargs.addData(nf.getComponent(i).getText());
                    }                    
                }
                Nikitaset ns;
                if (findsetting) {
                   ns= nikitaConnection.QueryPage(1,1,"UPDATE sys_setting SET settingvalue=?,modifiedby=?,modifieddate="+WebUtility.getDBDate(dbCore)+"  WHERE settingusername=? AND settingkey=?", nbuff.toJSON(),response.getVirtualString("@+SESSION-LOGON-USER"), response.getVirtualString("@+SESSION-LOGON-USER"),"databases-arg-"+fconn.getText());
                }else{
                   ns=nikitaConnection.QueryPage(1,1,"INSERT INTO sys_setting  (settingusername,settingkey, settingvalue,createdby,createddate ) VALUES (?,?,?,?,"+WebUtility.getDBDate(dbCore)+")", response.getVirtualString("@+SESSION-LOGON-USER"),"databases-arg-"+fconn.getText(),nbuff.toJSON(),response.getVirtualString("@+SESSION-LOGON-USER"));
                } 
                //response.openWindows("webbrowser/"+sb.toString(), "_blank");
                request.setParameter("sql", fsql.getText());
                request.setParameter("callz",fcallz.getText());
                request.setParameter("conn", fconn.getText()); 
                
                
                request.setParameter("args", newargs.toJSON()); 
                request.setParameter("argsname", fargsname.getText()); 
                
                response.showformGen("webtableview", request, "", true);
            }
        });
        horisontalLayout.addComponent(btn);
        
        
        nf.addComponent(horisontalLayout);  
        
        response.setContent(nf);
               
    }
    
}
