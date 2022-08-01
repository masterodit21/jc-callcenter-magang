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
import com.nikita.generator.NikitaService;
import com.nikita.generator.NikitaServlet;
import com.nikita.generator.Style;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.ui.Button;
import com.nikita.generator.ui.FileUploder;
import com.nikita.generator.ui.Tablegrid;
import com.nikita.generator.ui.Textbox;
import com.nikita.generator.ui.Textsmart;
import com.nikita.generator.ui.layout.HorizontalLayout;
import com.nikita.generator.ui.layout.NikitaForm;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import java.io.File;
import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 *
 * @author user
 */
public class webimportgoexcel extends NikitaServlet{

    
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
                
        NikitaForm nf = new NikitaForm(this);
        nf.setText("Import Excel");        
        HorizontalLayout horisontalLayout = new HorizontalLayout();
        
        
        Style style = new Style();
        style.setStyle("width", "400");
        style.setStyle("height", "180");
        style.setStyle("n-resizable", "false");
        style.setStyle("n-maximizable", "false");
        style.setStyle("height", "180");
        nf.setStyle(style);
        
        
        FileUploder upd = new FileUploder();
        upd.setId("detailimport-updfile");
        upd.setLabel("File");    
        upd.setStyle(new Style().setAttr("n-action", "webimportgo").setAttr("n-accept", ".xls, .csv"));
        nf.addComponent(upd);
         
        
        Button btn = new Button();
        btn.setId("webimport-btngo");
        btn.setText("Go");
        btn.setStyle(new Style().setStyle("width", "100px").setStyle("height", "32px").setStyle("position", "absolute").setStyle("bottom", "10px").setStyle("right", "20px"));
        btn.setOnClickListener(new Component.OnClickListener() {
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                response.submit(response.getContent().findComponentbyId("detailimport-updfile"), "import_"  + Utility.MD5( System.currentTimeMillis()+"" )  )  ;

                //response.submitDone("import", Nset.newArray());
                response.write();
            }
        });
        
        nf.addComponent(btn);
         
        
        response.setContent(nf);        
        
        nf.setOnLoadListener(new NikitaForm.OnLoadListener() {

            @Override
            public void OnLoad(NikitaRequest request, NikitaResponse response, Component component) {
            }
        });
        
        nf.setOnActionResultListener(new NikitaForm.OnActionResultListener() {

            @Override
            public void OnResult(NikitaRequest request, NikitaResponse response, Component component, String reqestcode, String responsecode, Nset result) {
                if (reqestcode.equals("submit")) {
                     
                    try {
                         response.callWait();
                        //response.closeform(response.getContent());
                        request.setParameter("fname", response.getContent().findComponentbyId("detailimport-updfile").getText());
                        request.setParameter("fname", result.getData("fsavename").toString());
                        response.showformGen("webimportexcel", request, "", true, "import");                        
                    } catch (Exception e) { }
                     
                }
            }
        });
    }
    
     
    public void OnRun(NikitaRequest nikitaRequest, NikitaResponse nikitaResponse, NikitaLogic logic) {
        if (!nikitaResponse.handleFileMultipart(nikitaRequest)) {  
            super.OnRun(nikitaRequest, nikitaResponse, logic);
        }
    }
    
    
}
