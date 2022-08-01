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
import com.nikita.generator.action.ShowDialogAction;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.ui.Button;
import com.nikita.generator.ui.Checkbox;
import com.nikita.generator.ui.Combobox;
import com.nikita.generator.ui.Label;
import com.nikita.generator.ui.Textarea;
import com.nikita.generator.ui.Textsmart;
import com.nikita.generator.ui.layout.HorizontalLayout;
import com.nikita.generator.ui.layout.NikitaForm;
import com.nikita.generator.ui.layout.VerticalLayout;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import java.util.Vector;
 

/**
 *
 * @author rkrzmail
 */
public class websmartgridview extends NikitaServlet{
    Textarea txtX   ;
    VerticalLayout verticalLayout ;
    NikitaConnection nikitaConnection ;
            
    private ComponentGroup autocreate(Nset n, final int i,NikitaConnection nc,String from){
        Nikitaset nikiset = nc.Query("select compcode,comptitle from all_component_list ORDER BY comptitle;");
        VerticalLayout v = new VerticalLayout();
        
        Vector<String> view = new Vector<String>();
        view.addElement("");view.addElement("default");
        
        
        HorizontalLayout h = new HorizontalLayout();
        Combobox com = new Combobox();
        com.setId("comp-text-"+i); 
        com.setLabel("Component ["+(i)+"]");
        com.setText(n.getData(i).getData("text").toString());         
        //com.setData(  new Nset( nikiset.getDataAllVector() ).copyNikitasettoObjectCol("id","text")   );  
        com.setData(  Nset.newArray().addData( Nset.newArray().addData("").addData("default") ) .addData( Nset.newArray().addData("label").addData("Label") )  .addData( Nset.newArray().addData("button").addData("Button") ).addData( Nset.newArray().addData("2button").addData("Button (2)")  ) .addData(   Nset.newArray().addData("3button").addData("Button (3)" )   ).addData(  Nset.newArray().addData("image").addData("Image")  )  .addData(  Nset.newArray().addData("url").addData("Url Link") )        );  
        h.addComponent(com); 

        
        if(from.equals("smartresult")){            
            Textsmart 
            component = new Textsmart();
            component.setId("comp-finder-"+i); 
            component.setText(n.getData(i).getData("finder").toString());
            component.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                request.setParameter("search", response.getContent().findComponentbyId("comp-finder-"+i).getText());
                request.setParameter("paramid", response.getContent().findComponentbyId("comp-finder-"+i).getId());
                request.setParameter("idcomp", response.getContent().findComponentbyId("lbl_tampung").getText()); 
                request.setParameter("idform", response.getContent().findComponentbyId("lbl_tampung").getTag());  
                response.showform("webvariablelist", request,"list2", true);

                response.write();
                }
            });
            h.addComponent(component);
            v.addComponent(h);
            
            Textarea area = new Textarea();
            area.setId("comp-style-"+i); 
            area.setLabel(" ");
            area.setStyle(new Style().setStyle("width", "500px"));
            area.setText(n.getData(i).getData("style").toString()); 
            v.addComponent(area);
            
        }else{
            Textsmart 
            component = new Textsmart();
            component.setId("comp-style-"+i); 
            component.setHint("style");
            component.setText(n.getData(i).getData("style").toString()); 
            h.addComponent(component);
            v.addComponent(h);
        }
        return v;
            
    }
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        NikitaForm nf  = new NikitaForm(this);
        nf.setText("Smart Grid View");
            
        Style style = new Style();
        style.setStyle("width", "625");
        style.setStyle("height", "480");
        style.setStyle("n-maximizable", "false");
        style.setStyle("n-resizable", "false");
        nf.setStyle(style);
        
        nikitaConnection =response.getConnection(NikitaConnection.LOGIC);
        final Label lbljumlah = new Label();
        lbljumlah.setId("lbl_jumlah");
        lbljumlah.setText("1");
        lbljumlah.setTag(request.getParameter("from"));
        lbljumlah.setVisible(false);
        request.retainData(lbljumlah);
        nf.addComponent(lbljumlah);                
        
        txtX = new Textarea();
        txtX.setId("autocreate");
        txtX.setVisible(false);
        txtX.setText(request.getParameter("autodata"));
        request.retainData(txtX);
        
        Label 
        lbl = new Label();
        lbl.setText(request.getParameter("idcomp"));
        lbl.setTag(request.getParameter("idform"));
        lbl.setId("lbl_tampung");
        lbl.setVisible(false);
        nf.addComponent(lbl);  
        
        
        nf.addComponent(txtX);
        verticalLayout = new VerticalLayout();
        verticalLayout.setId("con-autocreate");
        verticalLayout.setVisible(true);
        
        nf.addComponent(verticalLayout);
        
        
        Nset n = Nset.readJSON(txtX.getText());
        for (int i = 0; i < n.getArraySize(); i++) {
            verticalLayout.addComponent( autocreate(n, i,nikitaConnection,lbljumlah.getTag()) ); 
        } 
        
        HorizontalLayout
        hl = new HorizontalLayout();        
        Button btn = new Button();        
        btn.setId("btn_add");
        btn.setText("[+]");
        btn.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {     
                Nset n = Nset.readJSON(txtX.getText());
                if (!n.isNsetArray()) {
                    n= Nset.newArray();
                }
                Nset baru = Nset.newObject();
                n.addData(baru);
                txtX.setText(n.toJSON());
                verticalLayout.setVisible(true);
                verticalLayout.addComponent(autocreate(baru, n.getArraySize()-1 , nikitaConnection,lbljumlah.getTag()));
                response.refreshComponent(txtX);
                response.refreshComponent(verticalLayout);
                response.write();
            }
        }); 
        hl.addComponent(btn);  
        
        btn = new Button();        
        btn.setId("btn_remove");
        btn.setText("[-]");
        btn.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {     
                Nset n = Nset.readJSON(txtX.getText());
                if (!n.isNsetArray()) {
                    n= Nset.newArray();
                }
                
                if (n.getArraySize()>=1) {                        
                    ((Vector)n.getInternalObject()).removeElementAt(n.getArraySize()-1);
                    verticalLayout.removeComponent(verticalLayout.getComponentCount()-1);
                    
                    txtX.setText(n.toJSON());
                    verticalLayout.setVisible(true);
                    response.refreshComponent(txtX);
                    response.refreshComponent(verticalLayout);
                    response.write();
                }
                
            }
        }); 
        hl.addComponent(btn);  
        
        
        btn = new Button();        
        btn.setId("test");
        btn.setText("Save");
        btn.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {     
                Nset n = Nset.readJSON(txtX.getText());
                if (!n.isNsetArray()) {
                    n= Nset.newArray();
                }
                Nset data = Nset.newArray(); boolean splitmode = true;
                for (int i = 0; i < n.getArraySize(); i++) {
                     Nset dataperhoz = Nset.newObject();
                     dataperhoz.setData("text", response.getComponent("$#comp-text-"+i).getText());
                     dataperhoz.setData("style", response.getComponent("$#comp-style-"+i).getText());
                     dataperhoz.setData("finder", response.getComponent("$#comp-finder-"+i).getText());
                     
                     
                     if (!response.getComponent("$#comp-style-"+i).getText().equals("")) {
                        splitmode=false;
                     }
                     data.addData(dataperhoz);
                     
                }
                Nset result = Nset.newObject();
                if (splitmode) {
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < n.getArraySize(); i++) {
                          
                        sb.append(i>=1?",":"").append(response.getComponent("$#comp-text-"+i).getText());

                    }
                    result.setData("autodata",sb.toString());
                }else{
                    result.setData("autodata",data.toJSON());
                }
                
                response.closeform(response.getContent());                
                response.setResult("smartview", result);                
                response.write();
                
            }
        }); 
        hl.addComponent(btn);  
        nf.addComponent(hl);    
        
        
        
        nf.setOnActionResultListener(new NikitaForm.OnActionResultListener() {
            @Override
            public void OnResult(NikitaRequest request, NikitaResponse response, Component component, String reqestcode, String responsecode, Nset result) {
                        
                if(reqestcode.equals("list2")){
//                    response.getComponent("$#comp-finder-"+i).setText(result.getData("variable").toString());
                    System.out.println("==========================> "+result.getData("paramid").toString());
//                    response.getContent().findComponentbyId(result.getData("paramid").toString()).setText(result.getData("variable").toString());
//                    response.writeContent();
                }
            }
        });
        
        
        nf.setOnLoadListener(new NikitaForm.OnLoadListener() {
            @Override
            public void OnLoad(NikitaRequest request, NikitaResponse response, Component component) {
                             
            }
        }); 
        
        
        
        response.setContent(nf);
    }
    
}
