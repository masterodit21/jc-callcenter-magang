/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.servlet;

import com.nikita.generator.ComponentGroup;
import com.nikita.generator.NikitaControler;
import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaService;
import com.nikita.generator.NikitaServlet;
import com.nikita.generator.Style;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.ui.Checkbox;
import com.nikita.generator.ui.layout.NikitaForm;
import com.nikita.generator.ui.layout.VerticalLayout;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;

/**
 *
 * @author rkrzmail
 */
public class defmenuaccess extends NikitaServlet{
     private String getClickID(Nset n){
        if (n.containsKey("id")) {
            return n.getData("id").toString();
        }else if (n.containsKey("text")) {
            String s = n.getData("text").toString();
            if (s.length()>=1) {
                return s.replaceAll("[^A-Za-z0-9_.]+", "");
            }
        }
                
        return "";
    }
    private Nset flatToTree(Nikitaset nflat){
        Nset nfroot = Nset.newArray();
            Nset buffer = Nset.newObject();
            //flat to tree[0] == pareent all (id,text,parentid,additionalinfo)
            for (int i = 0; i < nflat.getRows(); i++) {
                String parent = nflat.getText(i, 2).trim();
                buffer.setData(parent, Nset.newArray());
            }

            //flat to tree[1] == id,text,parentid,additionalinfo
            for (int i = 0; i < nflat.getRows(); i++) {
                String parent = nflat.getText(i, 2).trim();
                                
                if (parent.equalsIgnoreCase( nflat.getText(i, 0).trim() )) {
                     //none
                }else if (!parent.equals("")) {
                    if ( buffer.getData(parent).isNsetArray()) {
                        Nset n = Nset.newObject();
                        n.setData("id", "");
                        n.setData("text", "");

                        buffer.getData(parent).addData("");
                    }else{
                        //none
                    }
                }else{
                    //none
                }
            }    
            
            //flat to tree[2] == gettroot
            for (int i = 0; i < nflat.getRows(); i++) {
                if (nflat.getText(i, 2).trim().equals("")) {
                    Nset n = Nset.newObject();
                    n.setData("id", "");
                    n.setData("text", "");
                    n.setData("child", "");
                    nfroot.addData(n);
                }                
            } 
            
            return nfroot;
    }
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        NikitaForm nf = new NikitaForm(this);
            VerticalLayout parent = new VerticalLayout();
            Nikitaset nikiset =  null ;  
            for (int i = 0; i < nikiset.getRows(); i++) {
                /*
                Label lbl = new Label(); 
                lbl.setText(nikiset.getText(i, "MENUNAME")); 
                parent.addComponent(lbl); 
                */
                
                Checkbox cb = new Checkbox(); 
                cb.setId("parent-"+nikiset.getText(i, "MENUCODE"));      
                cb.setData(  Nset.newArray().addData( Nset.newArray().addData(nikiset.getText(i, "MENUCODE")).addData(nikiset.getText(i, "MENUNAME"))) ); 
                parent.addComponent(cb); 
                
                
                VerticalLayout accessmenu = new VerticalLayout();
                accessmenu.setId("vparent-"+nikiset.getText(i, "MENUCODE"));
                accessmenu.setStyle(new Style().setStyle("margin-left", "20px"));
                
                Nikitaset nikiChield = response.getConnection(NikitaConnection.DEFAULT).Query("SELECT MENUCODE,MENUNAME FROM SYS_MENU WHERE MENUPARENT='"+nikiset.getText(i, "MENUCODE")+"' ORDER BY MENUSEQUENCE  ASC");  
                for (int iJ = 0; iJ < nikiChield.getRows(); iJ++) {
                    cb = new Checkbox(); 
                    cb.setId("menu-"+nikiChield.getText(iJ, "MENUCODE"));      
                    cb.setData(  Nset.newArray().addData( Nset.newArray().addData(nikiChield.getText(iJ, "MENUCODE")).addData(nikiChield.getText(iJ, "MENUNAME") + " (View)")) ); 
                    accessmenu.addComponent(cb); 

                    //delete/edit/add
                    VerticalLayout addeditdelete = new VerticalLayout();
                    addeditdelete.setStyle(new Style().setStyle("margin-left", "40px"));
                        if(!nikiChield.getText(iJ, "MENUCODE").contains("report")){
                        Checkbox add = new Checkbox(); 
                            add.setId("dea-"+nikiChield.getText(iJ, "MENUCODE"));  
                            add.setData( Nset.readJSON("[['add','Add'],['edit','Edit'],['del','Delete']]", true)); 
                            add.setStyle(new Style().setStyle("n-cols", "3"));
                    addeditdelete.addComponent(add);                     
                        }
                    //add
                    accessmenu.addComponent(addeditdelete);                    
                    
                }
                
                parent.addComponent(accessmenu);
            }
        nf.addComponent(parent);
        
        
        
        response.setContent(nf);
    }
    
}
