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
import com.nikita.generator.ui.Tablegrid;
import com.nikita.generator.ui.Textbox;
import com.nikita.generator.ui.Textsmart;
import com.nikita.generator.ui.layout.HorizontalLayout;
import com.nikita.generator.ui.layout.NikitaForm;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;

/**
 *
 * @author user
 */
public class webroute extends NikitaServlet{
   
    NikitaConnection nikitaConnection;       
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        nikitaConnection = response.getConnection(NikitaConnection.LOGIC);
        
        NikitaForm nf = new NikitaForm(this);
        nf.setText("Route List");
        
        Textsmart txt = new Textsmart();
        txt.setId("webroute-txtSearch");
        txt.setLabel("Searching");
        txt.setStyle(new Style().setStyle("n-searchicon", "true"));
        txt.setTag(request.getParameter("paramid"));
         
        txt.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);                  
                response.writeContent("webroute-txtSearch_text");
            }
        }); 
        
      
        
        nf.addComponent(txt);
        
        final Tablegrid tablegrid = new Tablegrid();
        tablegrid.setId("webroute-tblLogicList");
        tablegrid.setRowCounter(true);
        nf.addComponent(tablegrid);
        
        tablegrid.setOnClickListener(new Component.OnClickListener() {
                public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                    fillGrid(response);
                    
                    Nset result = Nset.newObject();
                    result.setData("variable", tablegrid.getData().getData(tablegrid.getSeletedRow()).getData(1).toString()); 

                    result.setData("paramid",response.getContent().findComponentbyId("webroute-txtSearch").getTag());
                    response.closeform(response.getContent());

                    response.setResult("webvariablelist", result); 
                    response.write();
                }
            });        
        
        tablegrid.setOnFilterClickListener(new Tablegrid.OnFilterClickListener() {
            public void OnFilter(NikitaRequest request, NikitaResponse response, Component component) {
                 fillGrid(response);
                 response.writeContent();
            }
        });     
        
        response.setContent(nf);
        
        nf.setOnLoadListener(new NikitaForm.OnLoadListener() {
            @Override
            public void OnLoad(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);
            }
        });
    }
    
    public void fillGrid(NikitaResponse response){
        Tablegrid tablegrid = (Tablegrid)response.getContent().findComponentbyId("webroute-tblLogicList");
        Textbox txt = (Textbox)response.getContent().findComponentbyId("webroute-txtSearch");
       
        Nikitaset nikiset = null;
        if(txt.getText().equals("")){
            
            nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"select compid, count(compid) "+ 
                                                       "from web_route GROUP BY compid");
            
        }
        else{
            String s = "%"+txt.getText()+"%";
            nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"select compid, count(compid) "+ 
                                                       "from web_route where compid like ? GROUP BY compid ; ",s);
            
        }
        
        nikiset.getDataAllHeader().addElement("compname");
        nikiset.getDataAllHeader().addElement("formid");
        nikiset.getDataAllHeader().addElement("formname");
        for (int i = 0; i < nikiset.getRows(); i++) {
            Nikitaset nikiset2 = nikitaConnection.QueryPage(1,1,"SELECT compname,formid FROM web_component WHERE compid = ?",nikiset.getText(i, 0));
            Nikitaset nikiset3 = nikitaConnection.QueryPage(1,1,"SELECT formname FROM web_form WHERE formid = ?",nikiset2.getText(0, 1));
            
            nikiset.getDataAllVector().elementAt(i).addElement(nikiset2.getText(0, 0));            
            nikiset.getDataAllVector().elementAt(i).addElement(nikiset2.getText(0, 1));          
            nikiset.getDataAllVector().elementAt(i).addElement(nikiset3.getText(0, 0));
        }
        nikiset.swapColomOrder(1, 4);
        tablegrid.setData(nikiset);
        tablegrid.setColHide(3, true);
      
    }
}
