/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator.ui;

import com.nikita.generator.Component;
import static com.nikita.generator.Component.escapeHtml;
import com.nikita.generator.NikitaViewV3;

/**
 *
 * @author rkrzmail
 */
public class Function extends Component{
    //no UI'
    public String getView(NikitaViewV3 v3) {
        v3.setData(this);
        StringBuilder sb = new StringBuilder();
        return sb.toString();
    }    
    public void runLogic( String action){
        
    }
}
