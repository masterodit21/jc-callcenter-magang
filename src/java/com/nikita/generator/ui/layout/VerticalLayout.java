/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator.ui.layout;

import com.nikita.generator.ComponentGroup;
import com.nikita.generator.NikitaViewV3;
/**
 * created by 13k.mail@gmail.com
 */
public class VerticalLayout extends ComponentGroup{
       public String getView(NikitaViewV3 v3) {
        v3.setData(this);
        StringBuilder sbuBuffer = new StringBuilder();
        sbuBuffer.append("<div ").append(getStyle()!=null?getStyle().getViewAttr("n-div-", " "):"").append(" style=\" ").append(isVisible()?"":"display:none;").append(getViewStyle()).append("\"  class=\"component verticallayout\"  style=\"overflow-x: hidden;float:none\"   id=\"").append(getJsId()).append("\" nid=\"").append(getId()).append("\"  nform=\"").append(getFormId()).append("\"  ntype=\"verticallayout\"     >");
        for (int i = 0; i < components.size(); i++) {
            sbuBuffer.append(components.get(i).getView(v3)) ;
        }        
        sbuBuffer.append("</div>");
        return sbuBuffer.toString();
    }
     
}
