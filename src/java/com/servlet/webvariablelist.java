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
public class webvariablelist extends NikitaServlet{
    final TabLayout ac = new TabLayout();   
    NikitaConnection nikitaConnection; 
    private String include=""; String func = "";
    int dbCore;
    boolean findsetting = false;
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        nikitaConnection = response.getConnection(NikitaConnection.LOGIC);        
        dbCore = WebUtility.getDBCore(nikitaConnection);
        
        NikitaForm nf = new NikitaForm(this);
        nf.setText("Variable List");        
        
        final Label idcomp = new Label();
        idcomp.setId("logic-lblidcomp");
        idcomp.setTag(request.getParameter("idcomp"));
        idcomp.setVisible(false);
        nf.addComponent(idcomp);
        
        final Label idform = new Label();
        idform.setId("logic-lblidform");
        idform.setTag(request.getParameter("idform"));
        idform.setVisible(false);
        nf.addComponent(idform);
        
        final Label idmodule = new Label();
        idmodule.setId("logic-lblidmodule");
        idmodule.setTag(request.getParameter("idmodule"));
        idmodule.setVisible(false);
        nf.addComponent(idmodule);
        
        HorizontalLayout hl = new HorizontalLayout();
        Textsmart txt = new Textsmart();
        txt.setId("webvariablelist-txtSearch");
        txt.setLabel("Searching");
        txt.setText(request.getParameter("search"));
        txt.setTag(request.getParameter("paramid"));
        nf.addComponent(hl);
        txt.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);  
                response.refreshComponent("webvariablelist-tblConn");
                response.refreshComponent("webvariablelist-tblConn2");
                response.refreshComponent("webvariablelist-tblConn3");
                response.refreshComponent("webvariablelist-tblConn4");
                response.refreshComponent("webvariablelist-tblConn5");
                response.write();
            }
        });    
        
        
        request.retainData(txt);
        if (txt.getText().contains("[")) {
            if (txt.getText().trim().endsWith(")")) {
                if (txt.getText().trim().contains("(")) {
                    func = txt.getText().substring(txt.getText().lastIndexOf("("));
                    include = txt.getText().substring(txt.getText().indexOf("["));
                    include = include.substring(0,include.lastIndexOf("(")).trim();
                }else{
                    func = "";
                    include = txt.getText().substring(txt.getText().indexOf("["));
                    include = include.substring(0,include.lastIndexOf("(")).trim();
                }                
            }else{
                include = txt.getText().substring(txt.getText().indexOf("["));
            }   
            txt.setText(  txt.getText().substring(0, txt.getText().indexOf("[")).trim() );
        }else if (txt.getText().contains("(")) {
            func = txt.getText().substring(txt.getText().indexOf("("));
            txt.setText( txt.getText().substring(0, txt.getText().indexOf("(")).trim());
        }    
        
        
        
        hl.addComponent(txt);
        Checkbox cb = new Checkbox();
        cb.setId("include");
        cb.setData(Nset.readsplitString("inc"));
        Textsmart txtinclude = new Textsmart();
        txtinclude.setId("txtinclude");
        
        if(include.contains("[") || func.contains("(")){
            cb.setVisible(true);
            cb.setText(Nset.readJSON("['inc']",true ).toJSON());
            txtinclude.setVisible(true);
        }else{
            txtinclude.setVisible(false);
            cb.setVisible(false);
            cb.setText("");
        }
        
        txtinclude.setOnClickListener(new Component.OnClickListener() {
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                request.setParameter("search", component.getText());
                response.showform("webvariablearraylist", request);  
            }
        });
        
        
        
       
      
        
        Textsmart txtFunc = new Textsmart();
        txtFunc.setId("txtfunc");        
        if(func.contains("(")){
            txtFunc.setVisible(true);
            txtFunc.setText(func);
        }else{
            txtFunc.setVisible(false);
        }
        
        txtFunc.setStyle(new Style().setStyle("width", "90px"));
        
        txtFunc.setOnClickListener(new Component.OnClickListener() {
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                String s = component.getText();
                s=Utility.replace(s, "(", "");
                s=Utility.replace(s, ")", "");
                request.setParameter("search",s );
                response.showform("webvariablefunctionlist", request);  
            }
        });
        
        txtinclude.setText(include);
        txtFunc.setText(func);
        
        request.retainData(txtinclude,cb,txtFunc);
        include=txtinclude.getText();
        func=txtFunc.getText();
        
        if(!cb.getText().contains("inc")){
           include="";
           func="";
        }
        
        hl.addComponent(cb);
        hl.addComponent(txtinclude);
        hl.addComponent(txtFunc);
        
        final Tablegrid tablegrid = new Tablegrid();
        tablegrid.setId("webvariablelist-tblConn");
        tablegrid.setDataHeader(Nset.readsplitString("No|Name|Describe"));
        tablegrid.setRowCounter(true);
               
        
        tablegrid.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);
                
                Nset result = Nset.newObject();
                result.setData("variable",willSaveVariable( tablegrid.getData().getData(tablegrid.getSeletedRow()).getData(1).toString(),include+" "+func)); 
                result.setData("paramid",response.getContent().findComponentbyId("webvariablelist-txtSearch").getTag());
                response.closeform(response.getContent());
                
                response.setResult("webvariablelist", result);                
                response.write();
            }
        });
        
        
        ac.setId("webvariablelist-accdion");
        
        
        VerticalLayout 
        verticalLayout = new VerticalLayout();
        verticalLayout.addComponent(tablegrid);
        verticalLayout.setId("webvariablelist-table-comp1");
        verticalLayout.setText("Static Variable");
        ac.addComponent(verticalLayout);
        
        
        
        final Tablegrid tablegrid3 = new Tablegrid();
        tablegrid3.setId("webvariablelist-tblConn3");
        tablegrid3.setDataHeader(Nset.readsplitString("No|Name|Describe"));
        tablegrid3.setRowCounter(true);
               
        
        tablegrid3.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);
                
                
                Nset result = Nset.newObject();
                result.setData("variable",willSaveVariable( tablegrid3.getData().getData(tablegrid3.getSeletedRow()).getData(1).toString(),include+" "+func));                 
                result.setData("paramid",response.getContent().findComponentbyId("webvariablelist-txtSearch").getTag());
                response.closeform(response.getContent());
                
                response.setResult("webvariablelist", result);                
                response.write();
            }
        });
        
        verticalLayout = new VerticalLayout();
        verticalLayout.addComponent(tablegrid3);
        verticalLayout.setId("webvariablelist-table-comp3");
        verticalLayout.setText("Virtual Variable");
        ac.addComponent(verticalLayout);
        
        
        final Tablegrid tablegrid2 = new Tablegrid();
        tablegrid2.setId("webvariablelist-tblConn2");
        tablegrid2.setDataHeader(Nset.readsplitString("No|Name|Describe"));
        tablegrid2.setRowCounter(true);
               
        
        tablegrid2.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);
                
                Nset result = Nset.newObject();
                result.setData("variable", willSaveVariable(tablegrid2.getData().getData(tablegrid2.getSeletedRow()).getData(1).toString(),include+" "+func)); 
                result.setData("paramid",response.getContent().findComponentbyId("webvariablelist-txtSearch").getTag());
                
                response.closeform(response.getContent());
                
                response.setResult("webvariablelist", result);                
                response.write();
            }
        });
        
        verticalLayout = new VerticalLayout();
        verticalLayout.addComponent(tablegrid2);
        verticalLayout.setId("webvariablelist-table-comp2");
        verticalLayout.setText("Form Component");
        ac.addComponent(verticalLayout);
        
        
        
        ac.setActive(1);
        
        
        
        
        //-----------Mobile
        final Tablegrid tablegrid4 = new Tablegrid();
        tablegrid4.setId("webvariablelist-tblConn4");
        tablegrid4.setDataHeader(Nset.readsplitString("No|FormName.CompName|Describe"));
        tablegrid4.setRowCounter(true);
               
        
        tablegrid4.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);
                
                Nset result = Nset.newObject();
                result.setData("variable",willSaveVariable( tablegrid4.getData().getData(tablegrid4.getSeletedRow()).getData(1).toString(),include+" "+func ));                 
                result.setData("paramid",response.getContent().findComponentbyId("webvariablelist-txtSearch").getTag());
                response.closeform(response.getContent());
                
                response.setResult("webvariablelist", result);                
                response.write();
            }
        });        
        verticalLayout = new VerticalLayout();
        
       Nikitaset nikiset = nikitaConnection.QueryPage(1,1,"SELECT settingvalue FROM sys_setting WHERE settingusername=? AND settingkey=?", response.getVirtualString("@+SESSION-LOGON-USER"),"mobile-activity");
       Nset nbuff = Nset.readJSON(nikiset.getText(0, 0)); 
       findsetting = nikiset.getRows()>=1;
       
        Textsmart module =new Textsmart();
        module.setId("module");
        module.setLabel("Module");
        module.setStyle(new Style().setStyle("n-lock", "true"));
        module.setOnClickListener(new Component.OnClickListener() {
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                request.setParameter("mode", "mobile");
                request.setParameter("search",response.getContent().findComponentbyId("module").getText());
                response.showform("webmodule", request,"module",true);
                response.write();
            }
        });
        module.setText(nbuff.getData("data").toString());
        module.setTag(nbuff.getData("id").toString());
            
        
        verticalLayout.addComponent(module);
        verticalLayout.addComponent(tablegrid4);
        verticalLayout.setId("webvariablelist-table-comp4");
        verticalLayout.setText("Mobile Activity Component");
        ac.addComponent(verticalLayout);      
        
        //-------------
        
//        databases fields
        
        
        final Tablegrid tablegrid5 = new Tablegrid();
        tablegrid5.setId("webvariablelist-tblConn5");
        tablegrid5.setDataHeader(Nset.readsplitString("No|Name|Describe"));
        tablegrid5.setRowCounter(true);
        tablegrid5.setOnClickListener(new Component.OnClickListener() {
            @Override
                public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);
                
                
                Nset result = Nset.newObject();
                result.setData("variable",  willSaveVariable( tablegrid5.getData().getData(tablegrid5.getSeletedRow()).getData(1).toString(),include+" "+func));                 
                result.setData("paramid",response.getContent().findComponentbyId("webvariablelist-txtSearch").getTag());
                response.closeform(response.getContent());
                
                response.setResult("webvariablelist", result);                
                response.write();
            }
        
        });
        
        verticalLayout = new VerticalLayout();
        verticalLayout.addComponent(tablegrid5);
        verticalLayout.setId("webvariablelist-table-comp5");
        verticalLayout.setText("Databases Fields");
        ac.addComponent(verticalLayout);
        
        
        
        
        nf.addComponent(ac); 
        
                
        
        
        //rule2 
        response.setContent(nf);       
        
        nf.setOnActionResultListener(new NikitaForm.OnActionResultListener() {
            @Override
            public void OnResult(NikitaRequest request, NikitaResponse response, Component component, String reqestcode, String responsecode, Nset result) {
                //webvariablefunctionlist
                if(responsecode.equals("webvariablefunctionlist")){  
                    //
                    response.getContent().findComponentbyId("txtfunc").setText("("+result.getData("variable").toString()+")"); 
                    response.refreshComponent(response.getContent().findComponentbyId("txtfunc"));
                    return;
                } 
                //webvariablearraylist
                if(responsecode.equals("webvariablearraylist")){  
                    //
                    response.getContent().findComponentbyId("txtinclude").setText(result.getData("variable").toString()); 
                    response.refreshComponent(response.getContent().findComponentbyId("txtinclude"));
                    return;
                } 
                
                if(reqestcode.equals("module") && responsecode.equals("OK")){                     
                    response.getContent().findComponentbyId("module").setText(result.getData("namemodule").toString());
                    response.getContent().findComponentbyId("module").setTag(result.getData("idmodule").toString());//actid
//                    response.write();
                }
                Nset nbuff = Nset.newObject();
                nbuff.setData("data", response.getContent().findComponentbyId("module").getText()).setData("id",response.getContent().findComponentbyId("module").getTag());
                Nikitaset ns;
                if (findsetting) {
                   ns= nikitaConnection.QueryPage(1,1,"UPDATE sys_setting SET settingvalue=?,modifiedby=?,modifieddate="+WebUtility.getDBDate(dbCore)+"  WHERE settingusername=? AND settingkey=?", nbuff.toJSON(),response.getVirtualString("@+SESSION-LOGON-USER"), response.getVirtualString("@+SESSION-LOGON-USER"),"mobile-activity");
                }else{
                   ns=nikitaConnection.QueryPage(1,1,"INSERT INTO sys_setting  (settingusername,settingkey, settingvalue,createdby,createddate ) VALUES (?,?,?,?,"+WebUtility.getDBDate(dbCore)+")", response.getVirtualString("@+SESSION-LOGON-USER"),"mobile-activity",nbuff.toJSON(),response.getVirtualString("@+SESSION-LOGON-USER"));
                } 
                response.refreshComponent("module");
                fillGrid(response);
                response.refreshComponent("webvariablelist-tblConn4");
                
            }
        });
        
        nf.setOnLoadListener(new NikitaForm.OnLoadListener() {
            @Override
            public void OnLoad(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);
            }
        });
    }
    private String willSaveVariable(String vnama, String include){
        if (vnama.contains("[")) {
            return vnama;
        }
        if (include.trim().equals("")) {
            include=include.trim();
        }else{
            include=" "+include.trim();
        }
        return  vnama +include;
    }
    private void fillGrid(NikitaResponse response){
        Tablegrid tablegrid = (Tablegrid)response.getContent().findComponentbyId("webvariablelist-tblConn") ;
        Tablegrid tablegrid2 = (Tablegrid)response.getContent().findComponentbyId("webvariablelist-tblConn2") ;
        Tablegrid tablegrid3 = (Tablegrid)response.getContent().findComponentbyId("webvariablelist-tblConn3") ;
        Tablegrid tablegrid4 = (Tablegrid)response.getContent().findComponentbyId("webvariablelist-tblConn4") ;
        Tablegrid tablegrid5 = (Tablegrid)response.getContent().findComponentbyId("webvariablelist-tblConn5") ;
        
        Textbox txt= (Textbox)response.getContent().findComponentbyId("webvariablelist-txtSearch");
        String formid = response.getContent().findComponentbyId("logic-lblidform").getTag() ;
        String idmodule = response.getContent().findComponentbyId("module").getTag() ;
        String s = txt.getText().toString(); String st = "";String p = txt.getText().toString();
        
        if (s.startsWith("$#")) {
           s=s.substring(2);
           ac.setActive(2);
        }else if (s.startsWith("$")) {
           s=s.substring(1);
           ac.setActive(2);
         }else if (s.startsWith("@+")) {
           st="@";
           s=s.substring(2);
           ac.setActive(0);
         }else if (s.startsWith("@#")) {
           st="@";
           s=s.substring(2);
           ac.setActive(0);
        
        }else  if (s.startsWith("@")) {
           st="@";
           s=s.substring(1);
           ac.setActive(1); 
        }else  if (s.startsWith("!")) {
           st="!";
           s=s.substring(1);
           ac.setActive(4);
        }else  if (s.startsWith("+")) {
           s=s.substring(1);
           ac.setActive(0);
        }
        
        if (s.contains("[")) {
            s=s.substring(0,s.indexOf("["));
        }
        if (s.contains("(")) {
            s=s.substring(0,s.indexOf("("));
        }    
        if (s.contains(".")) {
            ac.setActive(3);
        }
        s=s.trim();
 
        
        Nikitaset 
        nikiset = populateRegistered(nikitaConnection,  s );
        nikiset.setInfo(null);        
        tablegrid.setData(nikiset);
        
        nikiset=(populateOnForm(nikitaConnection,  formid, s));
        nikiset.setInfo(null);   
        tablegrid2.setData(nikiset);
        
        nikiset=(populateOnRoute(nikitaConnection, formid,  s , st));
        nikiset.setInfo(null);   
        tablegrid3.setData(nikiset);
        
        nikiset=(populateOnMobile(nikitaConnection, idmodule,  s ));
        nikiset.setInfo(null);   
        tablegrid4.setData(nikiset);
        
        nikiset=(populateOnDatabase(nikitaConnection, formid,  s , st));
        nikiset.setInfo(null);   
        tablegrid5.setData(nikiset);
        
    
    }
    private Nikitaset populateRegistered(NikitaConnection nikitaConnection, String search ){
        
        if(search.equals("")){
            Nikitaset nikiset = nikitaConnection.QueryPage(1,10, "select 1,registername,registerdescribe from all_register_list ORDER BY registername ASC;");
            
            return nikiset;
        } else{
            String s = "%"+search+"%";
            Nikitaset nikiset = nikitaConnection.QueryPage(1,10, "select 1,registername,registerdescribe from all_register_list where registername like ?  ORDER BY registername ASC; ",s);
            
            return nikiset;
        }
    }
    private Nikitaset populateOnMobile(NikitaConnection nikitaConnection , String idmodule,String search ){
        String fname = "";
        if (search.contains(".")) {
            fname=search.substring(0,search.indexOf("."));
            search=search.substring(search.indexOf(".")+1);
        }
        
        Nikitaset nikiset ;
        String s = "";
        String b = "";
            
        if(idmodule.equals("")){
            if(fname.equals("") && search.equals("")){
                nikiset = nikitaConnection.QueryPage(1,10,"SELECT A.moduleid,C.formname,B.compname,B.complabel,B.comptext FROM web_module_form A LEFT JOIN web_component B ON (A.formid=B.formid) LEFT JOIN web_form C ON (b.formid=C.formid) WHERE B.formid IS NOT NULL ORDER BY C.formname ; ");
            }else if (!fname.equals("") && search.equals("")){
                b = "%"+fname+"%";
                nikiset = nikitaConnection.QueryPage(1,10,"SELECT A.moduleid,C.formname,B.compname,B.complabel,B.comptext FROM web_module_form A LEFT JOIN web_component B ON (A.formid=B.formid) LEFT JOIN web_form C ON (b.formid=C.formid) WHERE B.formid IS NOT NULL AND C.formname LIKE ? ORDER BY C.formname ; ",b);
            }else if (fname.equals("") && !search.equals("")){
                s = "%"+search+"%";
                nikiset = nikitaConnection.QueryPage(1,10,"SELECT A.moduleid,C.formname,B.compname,B.complabel,B.comptext FROM web_module_form A LEFT JOIN web_component B ON (A.formid=B.formid) LEFT JOIN web_form C ON (b.formid=C.formid) WHERE B.formid IS NOT NULL AND B.compname LIKE ? ORDER BY C.formname ; ",s);
            }else{
                s = "%"+search+"%";
                b = "%"+fname+"%";
                nikiset = nikitaConnection.QueryPage(1,10,"SELECT A.moduleid,C.formname,B.compname,B.complabel,B.comptext FROM web_module_form A LEFT JOIN web_component B ON (A.formid=B.formid) LEFT JOIN web_form C ON (b.formid=C.formid) WHERE B.formid IS NOT NULL AND B.compname LIKE ? AND C.formname LIKE ? ORDER BY C.formname ; ",s,b);
            }
            
        }else{
            if(fname.equals("") && search.equals("")){
                nikiset = nikitaConnection.QueryPage(1,10,"SELECT A.moduleid,C.formname,B.compname,B.complabel,B.comptext FROM web_module_form A LEFT JOIN web_component B ON (A.formid=B.formid) LEFT JOIN web_form C ON (b.formid=C.formid) WHERE A.moduleid=? AND B.formid IS NOT NULL ORDER BY C.formname ; ",idmodule);
            }else if (!fname.equals("") && search.equals("")){
                b = "%"+fname+"%";
                nikiset = nikitaConnection.QueryPage(1,10,"SELECT A.moduleid,C.formname,B.compname,B.complabel,B.comptext FROM web_module_form A LEFT JOIN web_component B ON (A.formid=B.formid) LEFT JOIN web_form C ON (b.formid=C.formid) WHERE A.moduleid=? AND B.formid IS NOT NULL AND C.formname LIKE ? ORDER BY C.formname ; ",idmodule,b);
            }else if (fname.equals("") && !search.equals("")){
                s = "%"+search+"%";
                nikiset = nikitaConnection.QueryPage(1,10,"SELECT A.moduleid,C.formname,B.compname,B.complabel,B.comptext FROM web_module_form A LEFT JOIN web_component B ON (A.formid=B.formid) LEFT JOIN web_form C ON (b.formid=C.formid) WHERE A.moduleid=? AND B.formid IS NOT NULL AND B.compname LIKE ? ORDER BY C.formname ; ",idmodule,s);
            }else{
                s = "%"+search+"%";
                b = "%"+fname+"%";
                nikiset = nikitaConnection.QueryPage(1,10,"SELECT A.moduleid,C.formname,B.compname,B.complabel,B.comptext FROM web_module_form A LEFT JOIN web_component B ON (A.formid=B.formid) LEFT JOIN web_form C ON (b.formid=C.formid) WHERE A.moduleid=? AND B.formid IS NOT NULL AND B.compname LIKE ? AND C.formname LIKE ? ORDER BY C.formname ; ",idmodule,s,b);
            }
        }
        for (int i = 0; i < nikiset.getRows(); i++) {
            nikiset.getDataAllVector().elementAt(i).setElementAt((nikiset.getText(i, 1).equals("")?"":"$")+nikiset.getText(i, 1)+"."+nikiset.getText(i, 2), 1);            
            nikiset.getDataAllVector().elementAt(i).setElementAt(nikiset.getText(i, 3)+" "+nikiset.getText(i, 4), 2); 
        }   
        for (int i = 0; i < nikiset.getDataAllVector().size(); i++) {
            if (nikiset.getText(i, 1).equals("")) {
                nikiset.getDataAllVector().removeElementAt(i);
                i--;
            }            
        }
        return nikiset;
    }
    private Nikitaset populateOnForm(NikitaConnection nikitaConnection , String formid,String search ){
        if (search.contains(".")) {
            
        }
    
        
        Nikitaset nikiset ;
        if(search.equals("")){
            nikiset = nikitaConnection.QueryPage(1,10,"select compid,compname,comptext from web_component where formid=?",formid);
         } else{
            String s = "%"+search+"%";
            nikiset = nikitaConnection.QueryPage(1,10,"select compid,compname,comptext from web_component where formid=? AND compname like ?  ; ",formid,s);
        }
        for (int i = 0; i < nikiset.getRows(); i++) {
            nikiset.getDataAllVector().elementAt(i).setElementAt((nikiset.getText(i, 1).equals("")?"":"$")+nikiset.getText(i, 1), 1);            
        }
        for (int i = 0; i < nikiset.getDataAllVector().size(); i++) {
            if (nikiset.getText(i, 1).equals("")) {
                nikiset.getDataAllVector().removeElementAt(i);
                i--;
            }            
        }
        return nikiset;
    }
     
    private Nikitaset populateOnRoute(NikitaConnection nikitaConnection, String formid, String search , String st){
        
        Nikitaset nikiset ;
        /*
        if(search.equals("")){
            nikiset = nikitaConnection.QueryPage(1,10,"select 1,action,2 from web_route where action like ? ;","%@%");
        } else{
            String s = "%"+search+"%";
            nikiset = nikitaConnection.QueryPage(1,10,"select 1,action,2 from web_route where (action like ?) AND (action like ? ); ","%@%",s);
        }
        
        if(search.equals("")){
            nikiset = nikitaConnection.QueryPage(1,10,"select 1,action,2 from web_route where action like ?;","%\"class\":\"DefinitionAction\"%");
        } else{
            String s = "%"+search+"%";
            nikiset = nikitaConnection.QueryPage(1,10,"select 1,action,2 from web_route where action like ? AND action like ? ; ","%\"class\":\"DefinitionAction\"%",s);
        }
        */
        if(search.equals("")){
            nikiset = nikitaConnection.QueryPage(1,100,"SELECT web_route.compid,web_route.action,web_route.expression FROM web_component LEFT JOIN web_route ON (web_component.compid=web_route.compid) WHERE formid = ? AND web_route.compid is not null ", formid);
        } else{
            String s = "%"+search+"%";
            nikiset = nikitaConnection.QueryPage(1,100,"SELECT web_route.compid,web_route.action,web_route.expression FROM web_component LEFT JOIN web_route ON (web_component.compid=web_route.compid) WHERE formid = ? AND web_route.compid is not null ", formid);
        }
        
         
        Nikitaset nresult = new Nikitaset( nikiset.getDataAllHeader(), new Vector<Vector<String>>()  );
         
        for (int i = 0; i < nikiset.getRows(); i++) {
                
                if (nikiset.getText(i, 2).contains("\"result\":\"@")) {
                    Nset n = Nset.readJSON(nikiset.getText(i, 2));
                    if (n.getData("result").toString().contains(st) && n.getData("result").toString().startsWith("@") ) {
                            if (!nresult.getDataAllVector().toString().contains(n.getData("result").toString()+",") ) {
                               Vector<String> cols =  new Vector<String>();
                                cols.addElement("");
                                cols.addElement(getVirtualName(n.getData("result").toString()));
                                cols.addElement("");
                                nresult.getDataAllVector().addElement(cols);
                            }
                    }
                }
                
                Nset n = Nset.readJSON(nikiset.getText(i, 1));
                String[] keys = n.getData("args").getObjectKeys();
                
                for (int j = 0; j < keys.length; j++) {
                    
                    String arg = (n.getData("args").getData(keys[j]).toString());
                    String s = getVirtualName(arg);
                    
                    if ( s.startsWith("@+") ) {
                    }else if ( (st.equals("") || st.equals("@")) && s.startsWith("@")) {
                        if (s.indexOf(search)>=0 && !nresult.getDataAllVector().toString().contains(s+",") ) {
                            Vector<String> cols =  new Vector<String>();
                            cols.addElement("");
                            cols.addElement(s);
                            cols.addElement("");
                            nresult.getDataAllVector().addElement(cols);
                        }
                    }
                    /*
                    if ( (st.equals("") || st.equals("@")) && arg.startsWith("{") && arg.contains("\"fields\":") && arg.contains("\"dbmode\":\"select\"") && !nresult.getDataAllVector().toString().contains(s+",")  ) {
                        String vname =  n.getData("args").getData("result").toString();
                        
                        Nset xn =  Nset.readJSON(arg);
                        String v = xn.getData("fields").toString();
                        String tbl = xn.getData("tbl").toString(); 
                        if (v.length()<=7) {
                            v= xn.getData("fhide").toString();
                        }
                        
                        Nset vn = Nset.readJSON(v);
                        for (int k = 0; k < vn.getArraySize(); k++) {
                            s = getVirtualName(vn.getData(k).toString());
                            
                            if (s.indexOf(search)>=0 && !nresult.getDataAllVector().toString().contains(s+",") ) {
                                Vector<String> cols =  new Vector<String>();
                                cols.addElement("");
                                cols.addElement(vname+"[\"data\",0,\""+s+"\"]");
                                cols.addElement("Table Name : "+tbl);
                                if (vname.startsWith("@")) {
                                    nresult.getDataAllVector().addElement(cols);
                                }                                
                            }
                        }
               
                    }
                    */
                    
                }
        }      
       
        return nresult;
    }
    
    private Nikitaset populateOnDatabase(NikitaConnection nikitaConnection, String formid, String search , String st){
        
        Nikitaset nikiset ;
        if(search.equals("")){
            nikiset = nikitaConnection.QueryPage(1,100,"SELECT web_route.compid,web_route.action,web_route.expression FROM web_component LEFT JOIN web_route ON (web_component.compid=web_route.compid) WHERE formid = ? AND web_route.compid is not null ", formid);
        } else{
            String s = "%"+search+"%";
            nikiset = nikitaConnection.QueryPage(1,100,"SELECT web_route.compid,web_route.action,web_route.expression FROM web_component LEFT JOIN web_route ON (web_component.compid=web_route.compid) WHERE formid = ? AND web_route.compid is not null ", formid);
        }
        
         
        Nikitaset nresult = new Nikitaset( nikiset.getDataAllHeader(), new Vector<Vector<String>>()  );
         
        for (int i = 0; i < nikiset.getRows(); i++) {
                
                if (nikiset.getText(i, 2).contains("\"result\":\"@")) {
                    Nset n = Nset.readJSON(nikiset.getText(i, 2));
                    if (n.getData("result").toString().contains(st) && n.getData("result").toString().startsWith("@") ) {
                            if (!nresult.getDataAllVector().toString().contains(n.getData("result").toString()+",") ) {
                                Vector<String> cols =  new Vector<String>();
                                cols.addElement("");
                                cols.addElement(getVirtualName(n.getData("result").toString()));
                                cols.addElement("");
                               // nresult.getDataAllVector().addElement(cols);
                            }
                    }
                }
                
                Nset n = Nset.readJSON(nikiset.getText(i, 1));
                String[] keys = n.getData("args").getObjectKeys();
                for (int j = 0; j < keys.length; j++) {
                    String arg = (n.getData("args").getData(keys[j]).toString());
                    String s = getVirtualName(arg);
                    
                    if ( s.startsWith("@+") ) {
                    }else if ( (st.equals("") || st.equals("@")) && s.startsWith("@")) {
                        if (s.indexOf(search)>=0 && !nresult.getDataAllVector().toString().contains(s+",") ) {
                            Vector<String> cols =  new Vector<String>();
                            cols.addElement("");
                            cols.addElement(s);
                            cols.addElement("");
                           // nresult.getDataAllVector().addElement(cols);
                        }
                    }else if ( (st.equals("") || st.equals("!")) && arg.startsWith("{") && arg.contains("\"fields\":") && (arg.contains("\"dbmode\":\"select\"")|| arg.contains("\"dbmode\":\"query\"") )&& !nresult.getDataAllVector().toString().contains(s+",")  ) {
                        String vname =  n.getData("args").getData("result").toString();
                        Nset xn =  Nset.readJSON(arg);
                        String v = xn.getData("fields").toString();
                        String tbl = xn.getData("tbl").toString(); 
                        if (v.length()<=7) {
                            v= xn.getData("fhide").toString();
                        }
                        
                        Nset vn = Nset.readJSON(v);
                        for (int k = 0; k < vn.getArraySize(); k++) {
                            s = getVirtualName(vn.getData(k).toString());
                            if (tbl.equals("")||tbl.equals("none")) {
                                tbl="Query";
                            }
                            
                            if (s.indexOf(search)>=0 && !nresult.getDataAllVector().toString().contains(s+",") ) {
                                Vector<String> cols =  new Vector<String>();
                                cols.addElement("");
                                cols.addElement("!"+s);
                                cols.addElement(""+tbl +" "+ vname );
                                nresult.getDataAllVector().addElement(cols);
                            }else if(nresult.getDataAllVector().toString().contains(s+",")){
                                for (int l = 0; l < nresult.getDataAllVector().size(); l++) {
                                    if (nresult.getDataAllVector().elementAt(l).elementAt(1).equals("!"+s)) {
                                        nresult.getDataAllVector().elementAt(l).set(2, nresult.getDataAllVector().elementAt(l).elementAt(2) + " | "+tbl +" "+ vname );
                                        break;
                                    }
                                }
                            }
                        }
                       
                    }
                    else{
                       
                    }
                    
                }
        }      
       
        return nresult;
    }
    
    
    
    public String getVirtualName(String s){
        if (s.contains("[")) {
            s=s.substring(0,s.indexOf("[")).trim();
        }
        if (s.contains("(")) {
            s=s.substring(0,s.indexOf("(")).trim();
        }   
        return s.trim();
    }
}
