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
import com.nikita.generator.ui.Label;
import com.nikita.generator.ui.Textbox;
import com.nikita.generator.ui.Webview;
import com.nikita.generator.ui.layout.NikitaForm;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;

/**
 *
 * @author rkrzmail
 */
public class webworkflow extends NikitaServlet{

    Label lbldata ;
 
    public void OnRun(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
         //System.out.println("wfmode:"+request.getParameter("wfmode"));
        if (request.getParameter("wfmode").equals("file")) {
            //inp=getClass().getClassLoader().getResourceAsStream(inifile);
            String s = "";
            ServletContext context = request.getHttpServletRequest().getServletContext();         
            try {                
                response.getHttpServletResponse().setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
                response.getHttpServletResponse().setHeader("Pragma", "no-cache"); // HTTP 1.0.
                response.getHttpServletResponse().setHeader("Cache-Control", "max-age=0");                
                response.getHttpServletResponse().setContentType("text/html"); 
                InputStream is = context.getResourceAsStream("/oth/wf/workflow.html"); 
           
                s = Utility.readInputStreamAsString(is);               
            } catch (IOException ex) {System.out.println(ex.getMessage()); }
          
            Nikitaset nikiset = response.getConnection(NikitaConnection.LOGIC).Query("select wfdesigndata from web_workflow where wfid =?  ", request.getParameter("wfid"));
 
            String dv = nikiset.getText(0, 0).trim();
            if (dv.equals("")||dv.equals("{}")) {
                dv = "{\"class\":\"go.GraphLinksModel\", \"linkFromPortIdProperty\":\"fromPort\", \"linkToPortIdProperty\":\"toPort\", \"nodeDataArray\":[], \"linkDataArray\":[]}";
            }
            s = Utility.replace(s, "{\"uuid\":\"$data\"}", dv);
            s = Utility.replace(s, "src=\"workflow.js\""," src=\""+response.getVirtualString("@+BASEURL")  +"/static/oth/wf/workflow.js\" " );
            s = Utility.replace(s, "{\"uuid\":\"$urlform\"}", response.getVirtualString("@+BASEURL") +"/webworkflow/" );
            s = Utility.replace(s, "{\"uuid\":\"$wfid\"}", request.getParameter("wfid") );
          
            s = Utility.replace(s, "{\"uuid\":\"$formid\"}", request.getParameter("wfformid"));
            s = Utility.replace(s, "{\"uuid\":\"$responsecode\"}", "");
            s = Utility.replace(s, "{\"uuid\":\"$result\"}", "");
           
            
            response.writeStream(s);
        }else if (request.getParameter("wfmode").equals("udata")) {
            
            Nikitaset nikiset =  response.getConnection(NikitaConnection.LOGIC).Query("UPDATE web_workflow SET wfdesigndata=? where wfid =?  ", request.getParameter("datastream"), request.getParameter("wfid"));
            //System.out.println(nikiset.toNset().toJSON());
        }else{
            super.OnRun(request, response, logic); //To change body of generated methods, choose Tools | Templates.
        }        
    }
    
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        NikitaForm nf = new NikitaForm(this);
        nf.setText("Nikita Workflow " );
        
        
          
        lbldata = new Label();
        lbldata.setId("lblwfdata");
        lbldata.setLabel("");  
        lbldata.setVisible(false);
        lbldata.setText(request.getParameter("wfdata"));
        nf.addComponent(lbldata);
        
        request.retainData(lbldata);
        Nset n = Nset.readJSON(lbldata.getText());
        nf.setText("Nikita Workflow "+ n.getData(1).toString());
        
        
        Webview webview = new Webview();
        webview.setId("workflow");
        webview.setText("/base/webworkflow?wfmode=file&wfid="+n.getData(0).toString()+"&wfformid="+nf.getJsId()  );
        ///base/static/oth/wf/workflow.html
        Style style = new Style();
        style.setStyle("position", "absolute");
        style.setStyle("top", "0px");
        style.setStyle("bottom", "0px"); 
        style.setStyle("left", "0px"); 
        style.setStyle("right", "0px"); 
        style.setStyle("width", "100%"); 
        style.setStyle("height", "100%"); 
        //style.setStyle("overflow", "auto"); 
        webview.setStyle(style); 
        
        nf.setOnActionResultListener(new NikitaForm.OnActionResultListener() {
 
            public void OnResult(NikitaRequest request, NikitaResponse response, Component component, String reqestcode, String responsecode, Nset result) {
                if (responsecode.equals("save")) {
                    Nset n = Nset.readJSON(lbldata.getText());
                    response.getConnection(NikitaConnection.LOGIC).Query("UPDATE web_workflow SET wfdesigndata=? where wfid =?  ", result.getData("data").toString(), n.getData(0).toString());
                    response.showDialog("Information", "Your data has been successfully saved", "", "OK");
                    response.write();
                }else if (!reqestcode.equals("")) {
                    //response.showDialog("aaa", result.toJSON(), reqestcode, responsecode);
                    request.setParameter("wfargs", result.getData("data").toString());
                    request.setParameter("wfdata", lbldata.getText());
                    response.showform("webworkflowaction", request, "action", true);
                   response.write();
                }
                
            }
        });
        
        
        
        nf.addComponent(webview);
        response.setContent(nf);
         
    }
    
}
