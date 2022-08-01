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
import com.nikita.generator.ui.Button;
import com.nikita.generator.ui.Image;
import com.nikita.generator.ui.Label;
import com.nikita.generator.ui.Tablegrid;
import com.nikita.generator.ui.Textbox;
import com.nikita.generator.ui.Textsmart;
import com.nikita.generator.ui.layout.HorizontalLayout;
import com.nikita.generator.ui.layout.NikitaForm;
import com.nikita.generator.ui.layout.VerticalLayout;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import org.apache.commons.lang.WordUtils;

/**
 *
 * @author user
 */
public class webcompcopy extends NikitaServlet{
     private Textsmart idcomp;
     private Textbox index;
     private Textsmart idform;
     NikitaConnection nikitaConnection ;
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        nikitaConnection =response.getConnection(NikitaConnection.LOGIC);
        
        NikitaForm nf = new NikitaForm(this);        
        final Label lblmode = new Label();
        lblmode.setId("logic-lblmode");
        lblmode.setTag(request.getParameter("mode"));
        lblmode.setVisible(false);
        nf.addComponent(lblmode);
        
        Style style = new Style();
        style.setStyle("width", "1024");
        style.setStyle("height", "600");
        nf.setStyle(style);
       
        
        index = new Textbox();
        index.setId("logic-txtFinder");
        index.setLabel("Comp Name");
        index.setTag(request.getParameter("index"));
        index.setEnable(false);
        index.setVisible(false);        
        nf.addComponent(index);
        
        HorizontalLayout horisontalLayout = new HorizontalLayout();
        idform = new Textsmart();
        idform.setId("logic-form");
        idform.setLabel("Form Name");
        idform.setText(request.getParameter("formname"));
        idform.setTag(request.getParameter("idform"));
        idform.setStyle(new Style().setStyle("n-lock", "true"));
        horisontalLayout.addComponent(idform);
        idform.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                request.setParameter("code", "selected");
                request.setParameter("search", response.getContent().findComponentbyId("logic-form").getText() );
                response.showform("webform", request,"form",true);      
                response.write();
            }
        });         
        
        Button btn = new Button();
        btn.setId("newcopy");
        btn.setText("New Component");
        horisontalLayout.addComponent(btn);
        btn.setOnClickListener(new Component.OnClickListener() {
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                    response.setResult("NEWFORM",Nset.newObject().setData("index", index.getTag()).setData("formid", idform.getTag()));
                    response.closeform(response.getContent());
                    response.write();
            }
        });
         
        nf.addComponent(horisontalLayout);
        
        Tablegrid tablegrid = new Tablegrid();
        tablegrid.setId("logic-tblLogic");
        tablegrid.setDataHeader(Nset.readsplitString("No,formid,compname,compindex,comptype,complabel,comptext,comphint,parent,complist,compstyle,enable,visible,mandatory,compdefault,",","));
        tablegrid.showRowIndex(false, true);
        tablegrid.setColHide(0, true);
        tablegrid.setColHide(1, true);
        tablegrid.setColHide(3, true);
        tablegrid.setColHide(7, true);
        tablegrid.setColHide(8, true);
        tablegrid.setColHide(9, true);
        tablegrid.setColHide(10, true);
        tablegrid.setColHide(11, true);
        tablegrid.setColHide(12, true);
        tablegrid.setColHide(13, true);
        tablegrid.setColHide(14, true);
        tablegrid.setColHide(15, true);
        tablegrid.setStyle(new Style().setStyle("n-hide-pageup", "true"));
        
         tablegrid.setOnFilterClickListener(new Tablegrid.OnFilterClickListener() {
            @Override
            public void OnFilter(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);
                response.writeContent();
            }
        });
    
        
        nf.addComponent(tablegrid);  
        
        btn = new Button();
        btn.setId("copy-done");
        btn.setText("Copy");
        btn.setStyle(new Style().setStyle("float", "right"));
        btn.setOnClickListener(new Component.OnClickListener() {
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                
                Nset check = ((Nset)response.getVirtual("@+CHECKEDROWS"));
                Nset result = Nset.newArray();
                
                Nikitaset nikiset = nikitaConnection.Query("select compid from web_component  WHERE formid=? ORDER BY compindex ASC  ",idform.getTag());
                Nset data = new Nset(nikiset.getDataAllVector());
                 
                for (int i = 0; i < data.getArraySize(); i++) {
                    //=================================//
                    for (int j = 0; j < check.getArraySize(); j++) {
                        if (check.getData(j).toInteger()==i) {
                            result.addData(data.getData(check.getData(j).toInteger()).getData(0).toString());
                            break;
                        }
                    }
                    //=================================//
                }
    
                response.setResult("copy-comp",result);
                response.closeform(response.getContent());
                response.write();
            }
        });
        nf.addComponent(btn);
        
        
        nf.setOnActionResultListener(new NikitaForm.OnActionResultListener() {
            @Override
            public void OnResult(NikitaRequest request, NikitaResponse response, Component component, String reqestcode, String responsecode, Nset result) {
                                
                if(reqestcode.equals("form") && responsecode.equals("OK")){
                    response.getContent().findComponentbyId("logic-form").setText(result.getData("name").toString());
                    response.getContent().findComponentbyId("logic-form").setTag(result.getData("id").toString());
                    fillGrid(response);
                    response.writeContent();
                }
            }
        });
        
        nf.setText("Component Only Copy");   
        
        response.setContent(nf);        
        
        
        nf.setOnLoadListener(new NikitaForm.OnLoadListener() {
            @Override
            public void OnLoad(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);

            }
        });
        
    }

    private String expression = "";
    private String action = "";
    private String comment = "";
    
    public void fillGrid(NikitaResponse response){
        Tablegrid tablegrid = (Tablegrid)response.getContent().findComponentbyId("logic-tblLogic") ;
       
        Nikitaset nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"select compid,formid,compname,compindex,comptype,complabel,comptext,comphint,parent,complist,compstyle,enable,visible,mandatory,compdefault,1 from web_component  WHERE formid=? ORDER BY compindex ASC  ",idform.getTag());
        tablegrid.setData(nikiset);
        
    }  
    
}
