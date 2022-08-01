/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator.action;

import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.rkrzmail.nikita.data.Nset;

/**
 *
 * @author rkrzmail
 */
public class ShowAlertAction  implements IAction{
    @Override
    public boolean OnAction(Nset currdata, NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        String message = response.getVirtualString(currdata.getData("args").getData("message").toString());
        response.showAlert(message);
        return true;
    }
    
}

