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
import com.nikita.generator.ui.Label;
import com.nikita.generator.ui.layout.NikitaForm;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.ui.Button;
import com.nikita.generator.ui.Combobox;
import com.nikita.generator.ui.Textbox;
import com.nikita.generator.ui.Textsmart;
import com.nikita.generator.ui.layout.HorizontalLayout;
import com.nikita.generator.ui.layout.VerticalLayout;
import java.util.Hashtable;
 

/**
 *
 * @author user
 */
public class webcompproperty extends NikitaServlet{

    NikitaConnection nikitaConnection ;
    Label idtype ;
    Label styles ;
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        final NikitaForm nf = new NikitaForm(this);nf.setText("Component Property");
        nikitaConnection =response.getConnection(NikitaConnection.LOGIC);
        
        idtype = new Label();
        idtype.setId("compproperty-type");
        idtype.setText(request.getParameter("type"));
        idtype.setVisible(false);
        nf.addComponent(idtype);
        
        styles = new Label();
        styles.setId("compproperty-style");
        styles.setText(request.getParameter("style"));
        styles.setVisible(false);
        nf.addComponent(styles);
        request.retainData(idtype,styles);
        
        Style style = new Style();
        style.setStyle("width", "400");
        style.setStyle("height", "450");
        nf.setStyle(style);
        
        final Textsmart txts = new Textsmart();
        txts.setId("compproperty-txtSearch");
        txts.setLabel("Searching");
        txts.setStyle(new Style().setStyle("n-searchicon", "true"));
        txts.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
//                response.refreshComponent("compproperty-txtSearch");
                //response.getContent().findComponentbyId("compproperty-1").setVisible(true);
                response.refreshComponent("compproperty-1");
                response.write();
            }
        });
        nf.addComponent(txts);        
        request.retainData(txts);
        
        Nset n =  Nset.newArray();
        
        final Style xMain =  Style.createStyle(styles.getText());
        Nset xV = xMain.getInternalObject().getData("style");
        
        Nikitaset nikiset = nikitaConnection.Query("SELECT compparameter FROM all_component_list WHERE compcode = ?", idtype.getText());
        
        Nset nv = Nset.readJSON(nikiset.getText(0, 0));
        for (int k = 0; k < nv.getArraySize(); k++) {
            
                n.addData(nv.getData(k).toString());
            
        }
        
        
        final VerticalLayout v = new VerticalLayout();
        v.setId("compproperty-1");
        request.retainData(v);
        for (int i = 0; i < n.getArraySize(); i++) {
            Nikitaset ns = nikitaConnection.Query("SELECT registerdata FROM all_style_list WHERE registername = ?", n.getData(i).toString());
            Nset nx = Nset.readJSON(ns.getText(0, 0));
            Component txt = new Component(); 
            
            if (nx.getData("type").toString().equals("finder")) {
                txt = new Textsmart();
                txt.setText(xV.getData(n.getData(i).toString()).toString());
                txt.setTag(nx.getData("data").toJSON());
                txt.setOnClickListener(new Component.OnClickListener() {
                    public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                        
                    }
                });
            }else if (nx.getData("type").toString().equals("combobox")) {
                txt = new Combobox();
                txt.setData(nx.getData("data"));
            }else{
                txt = new Textbox();
            }
            txt.setId("arg-"+n.getData(i).toString());
            txt.setLabel(n.getData(i).toString());
            if(!txts.getText().equals("")){
                if(n.getData(i).toString().contains(txts.getText()))
                    txt.setVisible(false);
            }
            txt.setText(xV.getData(n.getData(i).toString()).toString());
            v.addComponent(txt);
           
//            request.retainData(v);
        }   
//            nf.addComponent(v);
        v.setEnable(true);
        v.setVisible(true);
        nf.addComponent(v);
        
        Button btn = new Button();
        btn.setId("compproperty-save");
        btn.setText("Save");
        nf.addComponent(btn);
        btn.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                StringBuffer sb = new StringBuffer();
                Nset xV = xMain.getInternalObject().getData("style");
//                String z = xV.getData("alignment").toString();
                
//                String[] keys = xV.getObjectKeys();
               
                
                for (int i = 0; i < v.getComponentCount(); i++) {
                    if (v.getComponent(i).getId().startsWith("arg-")) {                        
                        if(!v.getComponent(i).getText().trim().equals("")){
                            xV.setData(v.getComponent(i).getId().substring(4), v.getComponent(i).getText());
                        } else{
                            try {
                                ((Hashtable) xV.getInternalObject()).remove(v.getComponent(i).getId().substring(4));
                            } catch (Exception e) { }
                        }
                    }                    
                }
                
                Nset result = Nset.newObject();
                result.setData("data",xMain.getViewStyle().replace(";", ";\n")+xMain.getViewAttr().replace(";", ";\n"));
                response.closeform(response.getContent());
                
                response.setResult("compproperty", result);                
                response.write();
                
            }
        });
        
        nf.setOnRestoreListener(new NikitaForm.OnRestoreListener() {

            public void OnRestore(NikitaRequest request, NikitaResponse response, Component component) {
                  for (int i = 0; i < v.getComponentCount(); i++) {
                      
                    v.getComponent(i).setVisible(true);
                    v.getComponent(i).setEnable(true);
                    
                    if(!txts.getText().equals("")){
                        if(v.getComponent(i).getLabel().contains(txts.getText())){
                        }else{
                            v.getComponent(i).setVisible(false);
                            v.getComponent(i).setEnable(false);
                        }
                    }
                }
                v.  setVisible(true);
                    
            }
        });
        
        response.setContent(nf);
    }
    
}
