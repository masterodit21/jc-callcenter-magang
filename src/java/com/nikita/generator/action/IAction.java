/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nikita.generator.action;

import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.rkrzmail.nikita.data.Nset;



/**
 *
 * @author Rama
 */
public interface IAction {
    public boolean OnAction(Nset currdata, NikitaRequest request, NikitaResponse response, NikitaLogic logic);
}
