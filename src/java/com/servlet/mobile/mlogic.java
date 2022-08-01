/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.servlet.mobile;

import com.nikita.generator.NikitaEngine;
import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaService;
import com.nikita.generator.NikitaServlet;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.ui.Tablegrid;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import com.servlet.webexport;
import com.servlet.webform;

/**
 *
 * @author rkrzmail
 */
public class mlogic extends NikitaServlet{

    @Override
    public void OnRun(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        //mobileform, mobilecomponent, mobilelogic, mobilemodule, mobileparam, mobileasset
        NikitaConnection nc = response.getConnection(NikitaConnection.LOGIC);  
         
        if (request.getParameter("mode").equals("connection")) {
            if (NikitaService.isModeCloud()) {
                String referral = String.valueOf(request.getParameter("uname"));
                String user = NikitaService.getUserCloudCode(nc, referral);
                Nikitaset ns = nc.Query( "SELECT * FROM web_connection_mobile where  createdby = '"+user+"'"  );   
                response.writeStream(ns.toNset().toJSON());
            }else{
                Nikitaset ns = nc.Query( "SELECT * FROM web_connection_mobile");   
                response.writeStream(ns.toNset().toJSON());
            }   
            
        }else if (request.getParameter("mode").equals("nv3")) { 
            if (NikitaEngine.isNv3Exist( request.getParameter("formname"), false)) {                
                response.writeStream(NikitaEngine.viewNv3( request.getParameter("formname") ));
            }else{
                response.writeStream(NikitaEngine.createNv3(nc, request.getParameter("formname")));
            }
        }else if (request.getParameter("mode").equals("frmcomplogic")) { 
            if (request.getParameter("nv3").equalsIgnoreCase("true")) {
                response.writeStream(NikitaEngine.createNv3(nc, request.getParameter("formid")));
            }else{
                //[{form[{componet[{logic}]...]}]
                Nset out = Nset.newArray();
                Nikitaset nikitaset = nc.Query("SELECT * FROM web_form WHERE formid=?",request.getParameter("formid"));
                out.addData(nikitaset.toNset());

                nikitaset = nc.Query("SELECT * FROM web_component WHERE formid=?",request.getParameter("formid"));
                out.addData(nikitaset.toNset());

                Nikitaset ns = nc.Query("SELECT * FROM web_route WHERE compid IN (SELECT compid FROM web_component WHERE formid = ?) ",  request.getParameter("formid")); 
                out.addData(ns.toNset());            

                response.writeStream(out.toJSON());
            }
        }else if (request.getParameter("mode").equals("formname")) {
            response.setVirtual("@formidsall", request.getParameter("formid"));
            Nikitaset nikitaset = nc.Query("SELECT formid, formname FROM web_form  WHERE formid IN "+ response.getVirtualString("@formidsall (arraydb)") + " ORDER BY formname ");
            response.writeStream(nikitaset.toNset().toJSON()); 
        }else if (request.getParameter("mode").equals("module")) {
            Nikitaset nikitaset = nc.Query("SELECT DISTINCT web_form.formid,web_module_form.formname FROM web_module LEFT JOIN web_module_form ON(web_module.moduleid = web_module_form.moduleid) LEFT JOIN  web_form ON (web_form.formname=web_module_form.formname) WHERE web_module.modulename=?",request.getParameter("module"));
            Nset forms = Nset.newArray();           
            for (int i = 0; i < nikitaset.getRows(); i++) {
                forms.addData(nikitaset.getText(i, "formid"));    
            }   
            if (!request.getParameter("recurse").equals("false")) {
                for (int i = 0; i < nikitaset.getRows(); i++) {
                    //forms = populateAllForms(forms, nc, nikitaset.getText(i, "formid"));
                    webexport.populateAllForms(forms, nc, nikitaset.getText(i, "formid"));
                } 
            } 
            response.writeStream(forms.toJSON());   
        }else if (request.getParameter("mode").equals("forms")) {
            Nikitaset nikitaset = nc.Query("SELECT DISTINCT web_form.formid,web_module_form.formname FROM web_module LEFT JOIN web_module_form ON(web_module.moduleid = web_module_form.moduleid) LEFT JOIN  web_form ON (web_form.formname=web_module_form.formname) WHERE web_module.modulename=?",request.getParameter("module"));
            Nset forms = Nset.newArray(); 
            Nset formnames = Nset.newArray();  
            for (int i = 0; i < nikitaset.getRows(); i++) {
                forms.addData(nikitaset.getText(i, "formid"));
                formnames.addData(nikitaset.getText(i, "formname"));
            }   
            if (!request.getParameter("recurse").equals("false")) {
                for (int i = 0; i < nikitaset.getRows(); i++) {
                    //forms = populateAllForms(forms, nc, nikitaset.getText(i, "formid"));
                    webexport.populateAllForms(forms, formnames, Nset.newArray(), nc, nikitaset.getText(i, "formid"));
                } 
            } 
            response.writeStream(formnames.toJSON());     
        }       
 
    }
    
    private Nset populateAllForms(Nset forms, NikitaConnection nc, String formid){
        
        Nikitaset ns = nc.Query("SELECT action FROM web_route WHERE compid IN (SELECT compid FROM web_component WHERE formid = ?) ",  formid); 
        for (int i = 0; i < ns.getRows(); i++) {
            Nset nv = Nset.readJSON(ns.getText(i, 0));
            String code = nv.getData("code").toString();
            String classes = nv.getData("class").toString();
            if(classes.equals("FormAction")){
                String param = "";
                if (code.equals("calllink")||code.equals("calltask")||code.equals("callfunction")||code.equals("showform")||code.equals("showwindows")||code.equals("openwindows")||code.equals("showfinder")){
                    param = nv.getData("args").getData("param1").toString();
                    if (param.startsWith("{")) {
                            Nset n = Nset.readJSON(param);
                            param=n.getData("formid").toString();
                            if (!n.getData("formname").toString().equals("")) {
                                param=n.getData("formname").toString();
                            }
                    }                    
                }else if(code.equals("inflate")){
                    param = nv.getData("args").getData("param3").toString();                         
                }
                if (param.length()>=1){
                    String fid = nc.Query("SELECT formid FROM web_form WHERE formname = ?",  param).getText(0, 0); 
                    
                    boolean found = false;
                    for (int j = 0; j < forms.getArraySize(); j++) {
                        if (forms.getData(j).toString().equals(fid)) {
                            found=true;
                            break;
                        }
                    }
                    if (!found) { 
                        forms.addData(fid);
                        populateAllForms(forms, nc, fid);
                    }                           
                }
            }
        }        
        return forms;
    }
}
