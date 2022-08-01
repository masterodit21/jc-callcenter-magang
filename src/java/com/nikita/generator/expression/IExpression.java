/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nikita.generator.expression;
/**
 * created by 13k.mail@gmail.com
 */
import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.rkrzmail.nikita.data.Nset;
 
public interface IExpression {
    public boolean OnExpression(Nset currdata, NikitaRequest request, NikitaResponse response, NikitaLogic logic);
}
