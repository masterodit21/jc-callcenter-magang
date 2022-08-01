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
import com.nikita.generator.ui.Button;
import com.nikita.generator.ui.Label;
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
public class webnewasset extends NikitaServlet{
     private Textsmart idcomp;
     private Textbox index;
     private Textsmart idform;
     NikitaConnection nikitaConnection ;
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        nikitaConnection =response.getConnection(NikitaConnection.LOGIC);
        
        NikitaForm nf = new NikitaForm(this);   
        
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
        
                
        Tablegrid tablegrid = new Tablegrid();
        tablegrid.setId("logic-tblLogic");
        tablegrid.setDataHeader(Nset.readsplitString("No,assetname,assetfname,assetsize,assettype,assetdescribe,assethash,",","));
        tablegrid.showRowIndex(false, true);
        tablegrid.setColHide(0, true);
        tablegrid.setColHide(3, true);
        tablegrid.setColHide(6, true);
        tablegrid.setColHide(7, true);
        tablegrid.setStyle(new Style().setStyle("n-hide-pageup", "true"));
        
         tablegrid.setOnFilterClickListener(new Tablegrid.OnFilterClickListener() {
            @Override
            public void OnFilter(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);
                response.writeContent();
            }
        });
    
        
        nf.addComponent(tablegrid);  
        
        Button btn = new Button();
        btn.setId("copy-done");
        btn.setText("Insert");
        btn.setStyle(new Style().setStyle("float", "right"));
        btn.setOnClickListener(new Component.OnClickListener() {
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                
                Nset check = ((Nset)response.getVirtual("@+CHECKEDROWS"));
                Nset result = Nset.newArray();
                
                Nikitaset nikiset = nikitaConnection.Query("select assetid from web_asset ORDER BY assetid ASC  ");
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
    
                response.setResult("new-asset",result);
                response.closeform(response.getContent());
                response.write();
            }
        });
        nf.addComponent(btn);
        
        
        nf.setOnActionResultListener(new NikitaForm.OnActionResultListener() {
            @Override
            public void OnResult(NikitaRequest request, NikitaResponse response, Component component, String reqestcode, String responsecode, Nset result) {
                   
            }
        });
        
        nf.setText("Asset");   
        
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
       
        Nikitaset nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"select assetid,assetname,assetfname,assetsize,assettype,assetdescribe,assethash,1 from web_asset ORDER BY assetid ASC  ");
        tablegrid.setData(nikiset);
        
    }  
    
}

