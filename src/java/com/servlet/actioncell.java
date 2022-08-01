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
import com.nikita.generator.ui.Image;
import com.nikita.generator.ui.layout.HorizontalLayout;
import com.nikita.generator.ui.layout.NikitaForm;
import com.rkrzmail.nikita.data.Nset;

/**
 *
 * @author rkrzmail
 */
public class actioncell extends NikitaServlet{

    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        NikitaForm nf =new NikitaForm(this);
       
        response.setContent(nf);
         
        
        String sheader = request.getParameter("HEADER").trim();
        HorizontalLayout hl = new HorizontalLayout();
        hl.setId("nikita-action-cell");
        
 
        Nset na = Nset.readsplitString("up,down,edit,add,delete,remove,copy,run,play,view,paste,new,find,search,clear,image,map,gmap,camera,mobile,phone,download,upload,newtab,sync,home,cut,move,new1,schrestart,schstop,schstart,grid1,grid2,grid3",",");
        
        for (int i = 0; i < na.getArraySize(); i++) {
            Image 
            img =new Image();
            img.setId("nikita-action-"+na.getData(i).toString());
            img.setText("img/"+na.getData(i).toString()+".png");
            img.setStyle(new Style().setStyle("margin-right","3px")); 
            img.setTag(request.getParameter("ROWDATA"));

            img.setOnClickListener(new Component.OnClickListener() {
                public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {                            
                    
                }
            });
            
            if (sheader.equals("")) {
                hl.addComponent(img);
            }else if (sheader.contains("["+na.getData(i).toString()+"]")) {
                hl.addComponent(img);
            }
            
        }  
        
        response.getContent().addComponent(hl );
        response.getContent().setForm(response.getContent());
    }

    @Override
    public void OnAction(NikitaRequest request, NikitaResponse response, NikitaLogic logic, String component, String action) {
        if (component.startsWith("nikita-action-")) {
            response.setResult(component, Nset.readJSON(response.getComponent("$#"+component).getTag()));
            response.write();
       }else{
           super.OnAction(request, response, logic, component, action);     
       }
    }
    
    
    
}
