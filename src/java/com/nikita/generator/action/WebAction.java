/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator.action;

import com.nikita.generator.Component;
import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaService;
import com.nikita.generator.ui.layout.NikitaForm;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import java.util.Vector;

/**
 *
 * @author rkrzmail
 */
public class WebAction implements IAction{

    @Override
    public boolean OnAction(Nset currdata, NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        
        
        return true;
    }
    
    private static Nset getMobileGeneratorNotNull(NikitaResponse nikitaResponse){
        if (nikitaResponse.getMobileGenerator()!=null) {
            return  nikitaResponse.getMobileGenerator();
        }
        return Nset.newObject();
    }
    
    public static void latchreadWeb(NikitaRequest request, NikitaResponse nikitaResponse){
        try {
            if (nikitaResponse.getContent()!=null && nikitaResponse.getContent().isActionDataState()) {                
                Nset nv = getMobileGeneratorNotNull(nikitaResponse);
                
                if (nv.containsKey(nikitaResponse.getContent().getName())) {
                    nv = nv.getData(   nikitaResponse.getContent().getName()  );
                    
                    Vector<Component> components =  nikitaResponse.getContent().populateAllComponents();
                    for (int i = 0; i < components.size(); i++) {
                        String cname = components.elementAt(i).getName();
                        if (nv.containsKey(cname) && cname.length()>=2) {
                            components.elementAt(i).setText(nv.getData(   cname ).getData(0).toString());
                            components.elementAt(i).setVisible(nv.getData(cname ).getData(1).toString().equalsIgnoreCase("1"));
                            components.elementAt(i).setEnable(nv.getData( cname ).getData(2).toString().equalsIgnoreCase("1"));
                             
                            components.elementAt(i).setType(nv.getData(   cname ).getData(4).toString());
                            components.elementAt(i).setComment(nv.getData(cname ).getData(5).toString());
                            components.elementAt(i).setMandatory(nv.getData( cname ).getData(6).toString().equalsIgnoreCase("1"));
                        }
                    }
                }
                
            }
        } catch (Exception e) {  }
    }
    public static void latchsaveWeb(Component component, NikitaForm nikitaForm, NikitaResponse nikitaResponse){
        //refresh
    }
    public static void latchsaveWeb(NikitaForm nikitaForm, NikitaResponse nikitaResponse){
        //show form/content
    }
    public static void latchcloseWeb(String fname){
        
    }
            
    public static void latchsaveWeb(NikitaRequest request, NikitaResponse nikitaResponse){
        /*
        try {
            if (nikitaResponse.getContent()!=null) {
                
                String fname = NikitaService.getDirTmp()+ NikitaService.getFileSeparator() + nikitaResponse.getVirtualString("@+NIKITAID") + "_" + nikitaResponse.getContent().getName();//Nikitaid_formname
                Nset nV = Nset.readJSON(Utility.readInputStreamAsString(fname));
                Vector<Component> components =  nikitaResponse.getContent().populateAllComponents();
                for (int i = 0; i < components.size(); i++) {	
                     
                    
                }
            }
        } catch (Exception e) {  }
        */
    }
    
}
