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
import com.nikita.generator.ui.Tablegrid;
import com.rkrzmail.nikita.data.Nset;

/**
 *
 * @author rkrzmail
 */
public class SetDataAction   implements IAction{
    @Override
    public boolean OnAction(Nset currdata, NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        String data = response.getVirtualString(currdata.getData("args").getData("data").toString());  
        String hdrs = response.getVirtualString(currdata.getData("args").getData("header").toString());  
        Component component  = response.getComponent(currdata.getData("args").getData("target").toString());
        component.setData(Nset.readJSON(data));        
        if (component instanceof Tablegrid) {
            ((Tablegrid)component).setDataHeader(Nset.readsplitString(hdrs));
        }
        //response.showContent(response.getContent());
        response.refreshComponent(component);
        return true;
    }
    
}
