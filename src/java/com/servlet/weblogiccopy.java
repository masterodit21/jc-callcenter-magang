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
import com.rkrzmail.nikita.utility.Utility;
import java.util.Vector;
import org.apache.commons.lang.WordUtils;

/**
 *
 * @author rkrzmail
 */
public class weblogiccopy extends NikitaServlet{
     private Textsmart idcomp;
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
       
        
        Textbox txt = new Textbox();
        txt.setId("logic-txtFinder");
        txt.setLabel("Comp Name");
        txt.setEnable(false);
        txt.setVisible(false);
        
        nf.addComponent(txt);
        
        VerticalLayout horisontalLayout = new VerticalLayout();
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
         
        idcomp = new Textsmart();
        idcomp.setId("logic-component");
        idcomp.setLabel("Component");
        idcomp.setTag(request.getParameter("idcomp"));
        idcomp.setStyle(new Style().setStyle("n-lock", "true"));
        horisontalLayout.addComponent(idcomp);
        
        idcomp.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                request.setParameter("code", "selected");
                request.setParameter("idform", idform.getTag());
                request.setParameter("search", response.getContent().findComponentbyId("logic-component").getText() );
                response.showform("webcompparent", request,"comp",true);      
                response.write();
            }
        });
        
         
       
        nf.addComponent(horisontalLayout);
        
        Tablegrid tablegrid = new Tablegrid();
        tablegrid.setId("logic-tblLogic");
        tablegrid.setDataHeader(Nset.readsplitString("No,Component Id, Route Index, Action, Expression,",","));
        tablegrid.showRowIndex(false, true);
        tablegrid.setColHide(0, true);
        tablegrid.setColHide(1, true);
        tablegrid.setColHide(2, true);
        tablegrid.setColHide(5, true);
        tablegrid.setStyle(new Style().setStyle("n-hide-pageup", "true"));
        
         tablegrid.setOnFilterClickListener(new Tablegrid.OnFilterClickListener() {
            @Override
            public void OnFilter(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);
                response.writeContent();
            }
        });
    
        
        nf.addComponent(tablegrid);  
        
        Button tbldone = new Button();
        tbldone.setId("copy-done");
        tbldone.setText("Copy");
        tbldone.setStyle(new Style().setStyle("float", "right"));
        tbldone.setOnClickListener(new Component.OnClickListener() {
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                
                Nset check = ((Nset)response.getVirtual("@+CHECKEDROWS"));
                //Nset data = Nset.readJSON(response.getContent().findComponentbyId("logic-tblLogic").getTag());
                Nset result = Nset.newArray();
                
                Nikitaset nikiset = nikitaConnection.Query("select routeid from web_route  WHERE compid=? ORDER BY routeindex ASC  ",idcomp.getTag());
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
    
                response.setResult("copy-logic",result);
                response.closeform(response.getContent());
                response.write();
            }
        });
        nf.addComponent(tbldone);
        
        
        nf.setOnActionResultListener(new NikitaForm.OnActionResultListener() {
            @Override
            public void OnResult(NikitaRequest request, NikitaResponse response, Component component, String reqestcode, String responsecode, Nset result) {
                if(reqestcode.equals("add") && responsecode.equals("OK")){
                    fillGrid(response);
                    response.writeContent();
                }
                if(reqestcode.equals("edit") && responsecode.equals("OK")){
                    fillGrid(response);
                    response.writeContent();
                }
                
                if(reqestcode.equals("delete") && responsecode.equals("button2")){
                    
                    Nikitaset nikitaset = nikitaConnection.Query("delete from web_route where routeid=?",result.getData("id").toString());
                    fillGrid(response);
                    response.writeContent();
                }
                
                if(reqestcode.equals("form") && responsecode.equals("OK")){
                    response.getContent().findComponentbyId("logic-form").setText(result.getData("name").toString());
                    response.getContent().findComponentbyId("logic-form").setTag(result.getData("id").toString());
                    response.writeContent();
                }
                
                if(reqestcode.equals("comp") && responsecode.equals("OK")){
                    if (result.getData("name").toString().startsWith("$")) {
                        response.getContent().findComponentbyId("logic-component").setText(result.getData("name").toString().substring(1));
                    }else{
                        response.getContent().findComponentbyId("logic-component").setText("");
                    }
                    response.getContent().findComponentbyId("logic-component").setTag(result.getData("code").toString());
                    fillGrid(response);
                    response.writeContent();
                }
            }
        });
                
          
        
        
        /*
        if(lblmode.getTag().equals("logiclist")){     
            NikitaConnection nikitaConnection = NikitaConnection.getConnection("logic");
            Nikitaset nikitaset = nikitaConnection.Query("SELECT compname FROM web_component WHERE compid=?", idcomp.getTag()); 
            nf.findComponentbyId("logic-txtFinder").setText(nikitaset.getText(0, 0));
            nf.findComponentbyId("logic-txtFinder").setTag(idcomp.getTag());
            
            nf.setText("Logic/Route ["+nikitaset.getText(0, 0)+"]");       
        
        }
        else{            
            nf.setText("Logic/Route");       
        }
        */
         nf.setText("Logic Copy");   
        
        response.setContent(nf);        
        
        
        nf.setOnLoadListener(new NikitaForm.OnLoadListener() {
            @Override
            public void OnLoad(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);

            }
        });
        
    }
private String wrapLogic(String s){
    return s.length()>=12?s.substring(0,12)+"...":s;
}
    private boolean isVariable(String s){
        return s.startsWith("@")||s.startsWith("$")||s.startsWith("!")||s.startsWith("#")||s.startsWith("&");
    }
    private String expression = "";
    private String action = "";
    private String comment = "";
    
    public void fillGrid(NikitaResponse response){
        Tablegrid tablegrid = (Tablegrid)response.getContent().findComponentbyId("logic-tblLogic") ;
       
        Nikitaset nikiset = nikitaConnection.QueryPage(tablegrid.getCurrentPage(),tablegrid.getShowPerPage(),"select routeid, compid, routeindex, action, expression,1 from web_route  WHERE compid=? ORDER BY routeindex ASC  ",idcomp.getTag());
        tablegrid.setData(nikiset);
        //tablegrid.setTag(new Nset(nikiset.copyDataAllVectorAtCol(0)).toJSON());
        
        tablegrid.setColStyle(-1, new Style().setStyle("width", "50px").setStyle("width", "50px").setStyle("text-align", "right"));
        tablegrid.setAdapterListener(new IAdapterListener() {
            @Override
            public Component getViewItem(int row, int col, Component parent, Nset data) {
                
                if(col == 3){//action
                    
                    HorizontalLayout horisontalLayout = new HorizontalLayout();
                    Label lbl = new Label();
                    lbl.setId("logic-btnLabel");
                    Nset n = Nset.readJSON(data.getData(row).getData(3).toString());
                    StringBuffer sb = new StringBuffer();
                    String[] keys = n.getData("args").getObjectKeys();
                    sb.append("(");
                    for (int i = 0; i < keys.length; i++) {
                        String s = wrapLogic(n.getData("args").getData(keys[i]).toString());
                        if (s.trim().length()==0) {
                            s="''";
                        }
                        sb.append(i>=1?",":"").append("<a style=\"color:"+(isVariable(s)?"#F88017":"blue")+"\">"+s+"</a>");
                        
                    }
                    sb.append(")");
                     
                    String flag = Nset.readJSON(data.getData(row).getData(4).toString()).getData("flag").toString();
                    String sact="<a style=\"color:black\">"+n.getData("class").toString().replace("Action", "")+(n.getData("code").toString().equals("")?"":".")+WordUtils.capitalizeFully(n.getData("code").toString())+"</a>"+ (keys.length!=0?sb.toString():""); 
                    if (flag.equals("hide")) {
                        lbl.setText("<strike><em>"+sact+"</em></strike>"); 
                    }else{
                        lbl.setText(sact); 
                    } 
                    
                    if (n.getData("class").toString().equals("") && n.getData("code").toString().equals("")) {
                        String color = n.getData("args").getData("param2").toString().trim();
                        lbl.setText("<a style=\"color:"+(color.equals("")?"#7f7f7f":color)+"\"><em>"+n.getData("args").getData("param1").toString()+"</em></a>"); 
                        comment=n.getData("args").getData("param1").toString();
                        if (!comment.equals("")) {
                            horisontalLayout.setStyle(new Style().setAttr("n-td-colspan", "2"));
                        }                        
                    }else{
                        comment="";
                    }
                    if (n.getData("class").toString().trim().length()!=0) {
                        action="true";
                    }else{
                        action="";
                    }
                    
                    Component btn = new Image();
                    btn.setId("logic-btnAction["+row+"]");
                    btn.setText("img/edit.png");
                    btn.setTag(Nset.newObject().setData("logicid", data.getData(row).getData(0)).setData("action", data.getData(row).getData(3)).toJSON());
                    btn.setOnClickListener(new Component.OnClickListener() {
                        public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {                            

                        }
                    });
                    btn.setStyle(new Style().setStyle("margin-right", "3px"));
                    
                    if (!comment.equals("")) {
                        horisontalLayout.addComponent(lbl);
                        horisontalLayout.addComponent(btn);
                        btn.getStyle().setStyle("float", "right");
                        if (horisontalLayout.getStyle()==null) {
                            horisontalLayout.setStyle(new Style());
                        }
                        horisontalLayout.getStyle().setStyle("n-table-width", "100%");        
                        
                        
                    }else{
                        horisontalLayout.addComponent(btn);
                        horisontalLayout.addComponent(lbl);
                    }
                    
                    if (flag.equals("hide")) {
                        if (horisontalLayout.getStyle()==null) {
                            horisontalLayout.setStyle(new Style());
                        }
                        horisontalLayout.getStyle().setStyle("opacity","0.3");
                    }
                    return horisontalLayout;      
                }
                if(col == 4){//exp
                    HorizontalLayout horisontalLayout = new HorizontalLayout();
                    Label lbl = new Label();
                    lbl.setId("logic-btnLabel");
                     Nset n = Nset.readJSON(data.getData(row).getData(4).toString());
                    StringBuffer sb = new StringBuffer();
                    String[] keys = n.getData("args").getObjectKeys();
                    sb.append("(");
                    for (int i = 0; i < keys.length; i++) {
                        String s = wrapLogic(n.getData("args").getData(keys[i]).toString());
                        if (s.trim().length()==0) {
                            s="''";
                        }
                        sb.append(i>=1?",":"").append("<a style=\"color:"+(isVariable(s)?"#F88017":"blue")+"\">"+s+"</a>");
                    }
                    sb.append(")");
                    expression="<a style=\"color:black\">"+n.getData("class").toString().replace("Expression", "")+ (n.getData("code").toString().equals("")?"":".")+WordUtils.capitalizeFully(n.getData("code").toString())+"</a>"+ (keys.length!=0?sb.toString():"") ;
                    if (n.getData("flag").toString().equals("hide")) {
                        lbl.setText("<strike><em>"+expression+"</em></strike>"); 
                    }else{
                        lbl.setText(expression); 
                    }     
                    if (n.getData("class").toString().trim().length()==0) {
                        if (action.equals("true")) {
                            lbl.setText("<a style=\"color:#7f7f7f\"><em>Block</em></a>"); 
                        }
                    }
                    if (!comment.equals("")) {
                        horisontalLayout.setStyle(new Style().setStyle("n-td-display", "none"));
                    }
                    
                    Component btn = new Image();
                    btn.setId("logic-btnExpression["+row+"]");
                    btn.setText("img/edit.png");
                    btn.setTag(Nset.newObject().setData("logicid", data.getData(row).getData(0)).setData("expression", data.getData(row).getData(4)).toJSON());
                    btn.setOnClickListener(new Component.OnClickListener() {
                        public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {                            

                        }
                    });
                    btn.setStyle(new Style().setStyle("margin-right", "3px"));
                    if (n.getData("flag").toString().equals("hide")) {
                        if (horisontalLayout.getStyle()==null) {
                            horisontalLayout.setStyle(new Style());
                        }
                        horisontalLayout.getStyle().setStyle("opacity","0.3");
                    }
                    horisontalLayout.addComponent(btn);
                    horisontalLayout.addComponent(lbl);
    
                    return horisontalLayout;      
                }
                             
                return null;
            }
        });
    }  
    
}
