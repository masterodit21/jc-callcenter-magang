/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator;

import com.nikita.generator.action.IAction;
import com.nikita.generator.expression.TrueExpression;

/**
 *
 * @author rkrzmail
 */
public class NikitaGen {
    public void createClass(){
        StringBuffer sb = new StringBuffer();
        sb.append("package com.servlet;");
        sb.append("import com.nikita.generator.*;");
        
        sb.append("public class Gen  extends NikitaServlet {");
            sb.append("public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {");
                
            sb.append("}");
        sb.append("}");
    }
    public void createForm(){
        StringBuffer sb = new StringBuffer();
        sb.append("public class Gen {");
        
        
        
        
        sb.append("NikitaForm nikitaForm = new NikitaForm();");
        sb.append("nikitaForm.setText();");
        sb.append("nikitaForm.setIcon();"); 
        
        
        
        sb.append("response.setContent(nikitaForm);"); 
    }
    public void createComponent(){
        StringBuffer sb = new StringBuffer();
        sb.append("Component comp = new Component();");
        sb.append("comp.setId();");
        sb.append("comp.setName()");
        sb.append("comp.setLabel()");
        sb.append("comp.setText()");
        sb.append("comp.setHint()");
        sb.append("comp.setData()");
        sb.append("comp.setParentName()");
        sb.append("comp.setVisible()");
        sb.append("comp.setEnable()");
        sb.append("comp.setTooltip()");
        sb.append("comp.setStyle()");
        sb.append("comp.setTag()"); 
        sb.append("comp.setOnClickListener(this);"); 
 
    }
    public void createLogic(){
        TrueExpression trueExpression = new TrueExpression();
        trueExpression.OnExpression(null, null, null, null);
        
         
    }
}
