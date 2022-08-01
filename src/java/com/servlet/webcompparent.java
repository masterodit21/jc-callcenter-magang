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
public class webcompparent extends NikitaServlet{
    private Nset componentName = Nset.newObject();
    NikitaConnection nikitaConnection;    
    
        public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        nikitaConnection = response.getConnection(NikitaConnection.LOGIC);
        
        NikitaForm nf = new NikitaForm(this);
        nf.setText("Parent List");        
        HorizontalLayout horisontalLayout = new HorizontalLayout();
        
        final Label idform = new Label();
        idform.setId("parentlist-lblidform");
        idform.setTag(request.getParameter("idform"));
        idform.setVisible(false);
        nf.addComponent(idform);
        
        Textsmart txt = new Textsmart();
        txt.setId("parentlist-txtSearch");
        txt.setLabel("Searching");
        txt.setText(request.getParameter("search"));
        txt.setStyle(new Style().setStyle("n-searchicon", "true"));
        horisontalLayout.addComponent(txt);
        txt.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);   
                response.refreshComponent("parentlist-tblParent");
            }
        }); 
        
        nf.addComponent(horisontalLayout);
        
        final Tablegrid tablegrid = new Tablegrid();
        tablegrid.setId("parentlist-tblParent");
        tablegrid.setDataHeader(Nset.readsplitString("No,Form Id,Name,Index,Type,Label,Text,Hint,Default,List,Style,Enable,Visible,Mandatory,Validation",","));
        tablegrid.setRowCounter(true);
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
        
        tablegrid.setOnFilterClickListener(new Tablegrid.OnFilterClickListener() {
            @Override
            public void OnFilter(NikitaRequest request, NikitaResponse response, Component component) {
                    fillGrid(response);
                    response.writeContent();
            }
        });
        
        nf.addComponent(tablegrid);                
        tablegrid.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);
                
                    Nset nset = Nset.newObject();
                    nset.setData("code", tablegrid.getData().getData(tablegrid.getSeletedRow()).getData(0).toString());
                    nset.setData("name", tablegrid.getData().getData(tablegrid.getSeletedRow()).getData(2).toString());
                    response.closeform(response.getContent());
                    response.setResult("OK",nset);
                
                
                
                response.write();
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
        
        Nikitaset nikiset = nikitaConnection.Query("select compcode,comptitle from all_component_list ORDER BY comptitle;");
        for (int i = 0; i < nikiset.getRows(); i++) {
            componentName.setData(nikiset.getText(i, 0), nikiset.getText(i, 1));
        }  
        
        
    }
    
    private void fillGrid(NikitaResponse response){
        Tablegrid tablegrid = (Tablegrid)response.getContent().findComponentbyId("parentlist-tblParent") ;
        Textbox txt= (Textbox)response.getContent().findComponentbyId("parentlist-txtSearch");
        String formid = response.getContent().findComponentbyId("parentlist-lblidform").getTag();
        
       Nikitaset nikiset;
        if(txt.getText().equals("")){
            nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"select compid,formid,compname,compindex,comptype,complabel,comptext,"+ 
                                                       "comphint,compdefault,complist,compstyle,enable,visible,mandatory,validation "+ 
                                                       "from web_component  where formid= ?  ORDER BY compindex ASC",formid);
             
        }
        else{String s = "%"+(txt.getText().startsWith("$")?txt.getText().substring(1):txt.getText())+"%";
            nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"select compid,formid,compname,compindex,comptype,complabel,comptext,comphint,"+ 
                                                       "compdefault,complist,compstyle,enable,visible,mandatory,validation "+ 
                                                       "from web_component where formid= ? AND compname like ? ORDER BY compindex ASC ; ",formid,s);//  AND comptype LIKE ?
        }
        
        for (int i = 0; i < nikiset.getRows(); i++) {
            nikiset.getDataAllVector().elementAt(i).setElementAt((nikiset.getText(i, 2).equals("")?"":"$")+nikiset.getText(i, 2), 2);                   
        }
        for (int i = 0; i < nikiset.getDataAllVector().size(); i++) {
            if (nikiset.getText(i, 2).trim().equals("")) {
                nikiset.getDataAllVector().remove(i);
            }            
        }
        tablegrid.setData(nikiset);
        tablegrid.setAdapterListener(new IAdapterListener() {
            public Component getViewItem(int row, int col, Component parent, Nset data) {
                if(col == 4){
                    Component cmp = new Component();
                    cmp.setVisible(false);
                    cmp.setText(componentName.getData(data.getData(row).getData(col).toString()).toString());                   
                    return cmp;           
                }
                return null;
            }
        });
    }

}
