/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.forms;

import com.nikita.generator.NikitaControler;
import com.nikita.generator.ui.layout.NikitaForm;

/**
 *
 * @author rkrzmail
 */
public class SampleForm extends NikitaControler{

 
    public void onCreateUI() {
        super.onCreateUI(); 
    }
     
    public void onLoad() {
        NikitaForm nf = new NikitaForm("");
        
        
        
        
        setContent(nf);
    }
}
