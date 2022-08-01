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
import com.nikita.generator.NikitaServlet;
import com.nikita.generator.Style;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.connection.NikitaInternet;
import com.nikita.generator.ui.Button;
import com.nikita.generator.ui.Checkbox;
import com.nikita.generator.ui.SmartGrid;
import com.nikita.generator.ui.TextAutoComplete;
import com.nikita.generator.ui.Textsmart;
import com.nikita.generator.ui.layout.HorizontalLayout;
import com.nikita.generator.ui.layout.NikitaForm;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.web.utility.WebUtility;

/**
 *
 * @author rkrzmail
 */
public class webpublishnv3 extends NikitaServlet{
    int dbCore;
    boolean findsetting = false;
    NikitaConnection nikitaConnection ;
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        NikitaForm nf = new NikitaForm(this);
        nf.setText("Publish Nv3");
             
        nikitaConnection =response.getConnection(NikitaConnection.LOGIC);
        dbCore = WebUtility.getDBCore(nikitaConnection);
         
        TextAutoComplete textsmart =new TextAutoComplete();        
        textsmart.setId("url");
        textsmart.setLabel("URL Server Publish");
        textsmart.setStyle(new Style().setStyle("n-label-width", "180px").setStyle("n-width", "320px"));
        
        
        HorizontalLayout hl = new HorizontalLayout();
        hl.addComponent(textsmart);
        
        Button
        button = new Button();
        button.setId("conn");
        button.setText("Connection");
        button.setOnClickListener(new Component.OnClickListener() {
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                Component compurl = response.getContent().findComponentbyId("url");
                String url = compurl.getText();
                if (url.endsWith("/")) {		
                url=url.substring(0,url.length()-1);
				}
                String result = NikitaInternet.getString(NikitaInternet.getHttp(url+ "/res/about/")) ;
                if (result.contains("Hello Nikita")) { 
                    Nset vtsave =  Nset.newArray();
                    Nset n = compurl.getData();
                    if (n.isNsetArray()) {
                        for (int i =  Math.max(0, n.getSize()- 50) ; i < n.getSize(); i++) {                           
                            if (!n.getData(i).toString().equals(url)) {
                                vtsave.addData(n.getData(i));
                            }
                        }
                        vtsave.addData(url);
                    }else{
                        vtsave.addData(url);
                    }
                    nikitaConnection.QueryPage(1,1,"UPDATE settingvalue FROM sys_setting WHERE settingusername=? AND settingkey=?", response.getVirtualString("@+SESSION-LOGON-USER"),"link-arg-publish-nv3" );
                    if (findsetting) {
                        nikitaConnection.QueryPage(1,1,"UPDATE sys_setting SET settingvalue=?,modifiedby=?,modifieddate="+WebUtility.getDBDate(dbCore)+"  WHERE settingusername=? AND settingkey=?", vtsave.toJSON(),response.getVirtualString("@+SESSION-LOGON-USER"), response.getVirtualString("@+SESSION-LOGON-USER"),"link-arg-publish-nv3");
                    }else{
                        nikitaConnection.QueryPage(1,1,"INSERT INTO sys_setting  (settingusername,settingkey, settingvalue,createdby,createddate ) VALUES (?,?,?,?,"+WebUtility.getDBDate(dbCore)+")", response.getVirtualString("@+SESSION-LOGON-USER"),"link-arg-publish-nv3",vtsave.toJSON(),response.getVirtualString("@+SESSION-LOGON-USER"));
                    } 
                    compurl.setData(vtsave);
                    response.refreshComponent(compurl);
                    
                    //ok
                    response.showAlert("OK");
                    
                }
            }
        });
        
        hl.addComponent(button);
        
         
        Nikitaset nikiset = nikitaConnection.QueryPage(1,1,"SELECT settingvalue FROM sys_setting WHERE settingusername=? AND settingkey=?", response.getVirtualString("@+SESSION-LOGON-USER"),"link-arg-publish-nv3" );
        textsmart.setData(Nset.readJSON(nikiset.getText(0, 0)));
        findsetting = nikiset.getRows()>=1;
        
        
        nf.addComponent(hl);
        
        SmartGrid grid = new SmartGrid();
        grid.setId("grid");
        
        
        nf.addComponent(grid);
         
        button = new Button();
        button.setId("publish");
        button.setText("Publish");
        button.setOnClickListener(new Component.OnClickListener() {
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                 
            }
        });
        
        
        nf.addComponent(button);
        response.setContent(nf);
    }
    
    
}
