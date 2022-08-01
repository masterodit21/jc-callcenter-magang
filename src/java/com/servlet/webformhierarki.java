/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.servlet;

import com.nikita.generator.Component;
import com.nikita.generator.ComponentGroup;
import com.nikita.generator.ComponentManager;
import com.nikita.generator.IAdapterListener;
import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaServlet;
import com.nikita.generator.Style;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.ui.Button;
import com.nikita.generator.ui.Collapsible;
import com.nikita.generator.ui.Combobox;
import com.nikita.generator.ui.Image;
import com.nikita.generator.ui.Label;
import com.nikita.generator.ui.Tablegrid;
import com.nikita.generator.ui.Textbox;
import com.nikita.generator.ui.Textsmart;
import com.nikita.generator.ui.layout.HorizontalLayout;
import com.nikita.generator.ui.layout.NikitaForm;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.web.utility.WebUtility;
import java.util.Hashtable;
import java.util.Vector;

/**
 *
 * @author user
 */
public class webformhierarki extends NikitaServlet{
    private static final String ALL = "*";
    /*parameter
    formname
    code
    mode
    formid
    data
    id
    name
    */
    Label idform ;
     
    NikitaConnection nikitaConnection ;

    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        nikitaConnection =response.getConnection(NikitaConnection.LOGIC);
         
        
        NikitaForm nf = new NikitaForm(this);
        
        nf.setStyle(new Style().setStyle("width", "480").setStyle("height", "650"));
        
        HorizontalLayout horisontalLayout = new HorizontalLayout();
        
        idform = new Label();
        idform.setId("form-title");
        idform.setText(request.getParameter("fname"));
        idform.setTag(request.getParameter("fid"));
        idform.setVisible(false);
        request.retainData(idform);
        nf.addComponent(idform);
        
        Collapsible hi = new Collapsible();
        hi.setId("hierarki");
        
        
            NikitaConnection nikitaConnection =  response.getConnection(NikitaConnection.LOGIC);
            Nikitaset nikitaset = nikitaConnection.Query("SELECT * FROM web_component WHERE formid = ? ORDER BY compindex ASC;", idform.getTag());
            ComponentGroup group = new ComponentGroup();
            group.setId(idform.getTag());
            group.setName(idform.getText());
            
            Hashtable<String, Component> hashtable = new Hashtable<String, Component>();
            Vector<Component> components = new Vector<Component>(); 
            for (int i = 0; i < nikitaset.getRows(); i++) { 
                Component comp = ComponentManager.createComponent(nikitaset, i);       
                components.addElement(comp);
                hashtable.put(comp.getName(), comp);
            }  
            //flat to tree[1]
            for (int i = 0; i < components.size(); i++) {
                String parent =components.elementAt(i).getParentName().trim();
                parent=parent.startsWith("$") ? parent.substring(1):parent;
                
                if (parent.equals(components.elementAt(i).getName())) {
                    components.elementAt(i).clearParentName();
                }else if (!parent.equals("")) {
                    if ( hashtable.get(parent) instanceof ComponentGroup) {
                        ((ComponentGroup)hashtable.get(parent)).addComponent(components.elementAt(i));
                    }else{
                        components.elementAt(i).clearParentName();
                    }
                }else{
                    components.elementAt(i).clearParentName();
                }
            }            
            //flat to tree[2]
            for (int i = 0; i < components.size(); i++) {
                if (components.elementAt(i).getParentName().equals("")) {
                    group.addComponent(components.elementAt(i));
                }                
            } 
        
             
            
            hi.setData(getDataComponent(group));
            
        nf.addComponent(hi);
        nf.setText("Heararki [ " +  idform.getText()+" ]");       
        response.setContent(nf);      
    }
    
    
    private Nset getDataComponent(ComponentGroup data) {
        Nset n = Nset.newArray(); 
         
        for (int i = 0; i < data.getComponentCount(); i++) {
            prepareComponent(n, data.getComponent(i));
        }        
        return n;        
    }
    
    private void prepareComponent(Nset cmp, Component component){
        Nset n = Nset.newObject();  
        
        if (component instanceof ComponentGroup) {
            Nset child = Nset.newArray();
            for (int i = 0; i < ((ComponentGroup)component).getComponentCount(); i++) {
                prepareComponent(child, ((ComponentGroup)component).getComponent(i));
            } 
               
            n.setData("text",component.getId()+" [ "+ component.getName()+" ]");
             
            n.setData("id", component.getId());  
            n.setData("child", child);
        }else  {             
            n.setData("text",component.getId()+" [ "+ component.getName()+" ]");
            n.setData("id", component.getId());  
             
        }
        
        cmp.addData(n);
    }
    
}
