/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.servlet.mobile;

import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;


/**
 *
 * @author rkrzmail
 */
public class mzharasyncdbserver extends mzharasyncfileserver{

    public void OnRun(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        super.runZhara(request, response, logic, "init-syncdb-server");
    }
    
    
}
