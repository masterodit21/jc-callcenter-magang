/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.servlet;

import com.nikita.generator.Component;
import com.nikita.generator.IAdapterListener;
import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaServlet;
import com.nikita.generator.Style;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.ui.Combobox;
import com.nikita.generator.ui.Image;
import com.nikita.generator.ui.Label;
import com.nikita.generator.ui.Tablegrid;
import com.nikita.generator.ui.Textarea;
import com.nikita.generator.ui.Textbox;
import com.nikita.generator.ui.Textsmart;
import com.nikita.generator.ui.layout.HorizontalLayout;
import com.nikita.generator.ui.layout.NikitaForm;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import com.web.utility.WebUtility;

/**
 *
 * @author rkrzmail
 */
public class webdatabaseautoquery extends NikitaServlet{
  
    NikitaConnection nikitaConnection ;
    
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        nikitaConnection =response.getConnection(NikitaConnection.LOGIC);
         
        
        NikitaForm nf = new NikitaForm(this);
        nf.setText("Database Detail");
        nf.setStyle(new Style().setStyle("width", "480").setStyle("height", "280"));
        
        final Label label = new Label();
        label.setId("tables"); 
        label.setText(request.getParameter("conn"));
        label.setVisible(false);
        nf.addComponent(label);
        
        final Combobox com = new Combobox();
        com.setId("view");
        com.setLabel("View of"); 
        com.setText("none");
       final  Textarea txtarea = new Textarea();
        txtarea.setId("result");
        txtarea.setLabel("");
        Style st = new Style();
        txtarea.setStyle(st);
        
        request.retainData(label);
            NikitaConnection nc  =  response.getConnection(label.getText());
            if (nc.getError().equals("")) {
                Nikitaset nikiset =nc.Query("SELECT table_name,table_name FROM INFORMATION_SCHEMA.TABLES WHERE table_schema = (SELECT DATABASE())");
                if (nikiset.getRows()==0) {
                    nikiset = nc.Query("SELECT table_name,table_name FROM user_tables ");
                }
                 if (nikiset.getRows()==0) {
                    nikiset = nc.Query("SELECT tbl_name,tbl_name FROM sqlite_master ");
                }
                String s = new Nset( nikiset.getDataAllVector() ).copyNikitasetAtCol(0).toString();                
                txtarea.setTag( Utility.replace(Utility.replace(s, "]", ""), "[", "") );
                txtarea.setText(txtarea.getTag());
                
                com.setData(new Nset( nikiset.getDataAllVector() ).copyNikitasettoObjectCol("id","text").addData(Nset.newObject().setData("id", "none").setData("text", "Show All tables")));
                
            }
        
        nf.addComponent(com);
        com.setOnClickListener(new Component.OnClickListener() {
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                if (com.getText().equals("none")) {                    
                    txtarea.setText(txtarea.getTag());
                }else{
                    NikitaConnection nc  =  response.getConnection(label.getText());
                    Nikitaset nikiset = nc.QueryPage(1,1,"SELECT * FROM "+com.getText() );
                    String s= new Nset( nikiset.getDataAllHeader() ).toString();
                    txtarea.setText( Utility.replace(Utility.replace(s, "]", ""), "[", "") );
                }
                response.refreshComponent(txtarea);
            }
        });
        
        
 
        
        
        txtarea.setStyle(new Style().setStyle("font-size", "15px").setStyle("width", "425px").setStyle("height", "160px").setStyle("resize", "none"));
        nf.addComponent(txtarea);
        
        response.setContent(nf);
    }
    
    
}

