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
import com.nikita.generator.ui.Accordion;
import com.nikita.generator.ui.Button;
import com.nikita.generator.ui.Checkbox;
import com.nikita.generator.ui.Image;
import com.nikita.generator.ui.Label;
import com.nikita.generator.ui.Tablegrid;
import com.nikita.generator.ui.Textbox;
import com.nikita.generator.ui.Textsmart;
import com.nikita.generator.ui.layout.HorizontalLayout;
import com.nikita.generator.ui.layout.NikitaForm;
import com.nikita.generator.ui.layout.TabLayout;
import com.nikita.generator.ui.layout.VerticalLayout;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import com.web.utility.WebUtility;
import java.util.Iterator;
import java.util.Vector;


/**
 *
 * @author rkrzmail
 */
public class webvariablefunctionlist extends NikitaServlet{
    
    NikitaConnection nikitaConnection ;
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        nikitaConnection =response.getConnection(NikitaConnection.LOGIC);
        
        
        Nset ndata = Nset.newArray();
        ndata.addData(Nset.newArray().addData("string").addData(""));
        ndata.addData(Nset.newArray().addData("md5").addData(""));
        
        
        
        NikitaForm nf = new NikitaForm(this);
        nf.setText("Variable Function List");        
        HorizontalLayout horisontalLayout = new HorizontalLayout();
        
        Textbox txt = new Textsmart();
        txt.setId("webactionlist-txtSearch");
        txt.setLabel("Searching");
        txt.setText(request.getParameter("search"));
        txt.setStyle(new Style().setStyle("n-searchicon", "true"));
        horisontalLayout.addComponent(txt);
        
        Button btn = new Button();
        btn.setId("webactionlist-btnSearch");
        btn.setText("Searching");
        btn.setVisible(false);
        horisontalLayout.addComponent(btn);
        txt.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response); 
                response.refreshComponent("webactionlist-tblConn");
                response.write();
            }
        });      
        
        Image img = new Image();
        img.setId("webactionlist-btnAdd");
        img.setText("img/add.png");
        img.setTag(request.getParameter("mode"));
        img.setStyle(new Style().setStyle("margin-top", "6px"));
        horisontalLayout.addComponent(img);
        img.setOnClickListener(new Component.OnClickListener() {

            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                request.setParameter("mode", "add");
                response.showform("webactionlistadd", request,"add",true);
                response.write();
            }
        });
        if(img.getTag().equals("edit")){
            img.setVisible(true);
        }else{
            img.setVisible(false);
        }
             
               
        nf.addComponent(horisontalLayout);
        
        final Tablegrid tablegrid = new Tablegrid();
        tablegrid.setId("webactionlist-tblConn");
        tablegrid.setDataHeader(Nset.readsplitString("No, Function, Describe",","));
        tablegrid.setRowCounter(true);
        
       
        
        
        nf.addComponent(tablegrid);                
        tablegrid.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response); 
                 
                Nset result = Nset.newObject();
                result.setData("variable", tablegrid.getData().getData(tablegrid.getSeletedRow()).getData(1).toString());                 
                response.closeform(response.getContent());
                
                response.setResult("webvariablefunctionlist", result);                
                response.write();
            }
        });
        
        tablegrid.setOnFilterClickListener(new Tablegrid.OnFilterClickListener() {
            public void OnFilter(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);
                response.refreshComponent(response.getContent().findComponentbyId("webactionlist-tblConn"));
            }
        });
        
        //ngambil data lemparan dari form moduleadd
        nf.setOnActionResultListener(new NikitaForm.OnActionResultListener() {
            @Override
            public void OnResult(NikitaRequest request, NikitaResponse response, Component component, String reqestcode, String responsecode, Nset result) {
                if(responsecode.equals("OK")){
                    fillGrid(response);
                    
                }
                response.writeContent();
            }
        });
        
        
        //rule2 
        response.setContent(nf);       
        
        nf.setOnLoadListener(new NikitaForm.OnLoadListener() {
            @Override
            public void OnLoad(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);
            }
        });
    }
    
    private void fillGrid(NikitaResponse response){
        Tablegrid tablegrid = (Tablegrid)response.getContent().findComponentbyId("webactionlist-tblConn") ;
        Textbox txt= (Textbox)response.getContent().findComponentbyId("webactionlist-txtSearch");
        
        String s = txt.getText().trim();
        s=Utility.replace(s, "(", "");
        s=Utility.replace(s, ")", "");
        
        if(s.equals("")){
            Nikitaset nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(), "select 1,varcode,vardescribe,varparameter  "+ 
                                                       "from all_var_function  WHERE vartype=0 ORDER BY varcode;");
            tablegrid.setData(nikiset);
        }
        else{
            s = "%"+s+"%";
            Nikitaset nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"select 1,varcode,vardescribe,varparameter  "+ 
                                                       "from all_var_function where vartype=0  AND ( varcode like ? OR vardescribe like ?  )  ORDER BY varcode ",s,s);
            tablegrid.setData(nikiset);
        }
        
        
    }
}
