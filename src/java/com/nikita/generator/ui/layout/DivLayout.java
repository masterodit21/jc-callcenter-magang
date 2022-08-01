/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator.ui.layout;

import com.nikita.generator.ComponentGroup;
import com.nikita.generator.ComponentManager;
import com.nikita.generator.NikitaViewV3;
import com.nikita.generator.Style;
import com.rkrzmail.nikita.data.Nset;
/**
 * created by 13k.mail@gmail.com
 */
public class DivLayout extends ComponentGroup{
    public String getView(NikitaViewV3 v3) {
        v3.setData(this);
        StringBuilder sbuBuffer = new StringBuilder();
        sbuBuffer.append("<div ").append(getViewAttribut()).append(" style=\"").append(isVisible()?"":"display:none;").append(getViewStyle()).append("\" class=\"").append("").append(getViewClass()).append("\"  id=\"").append(getJsId()).append("\" nid=\"").append(getId()).append("\"  nform=\"").append(getFormId()).append("\"  ntype=\"nikitadivlayout\">");
        sbuBuffer.append( ComponentManager.parseComponentStyle( this, getText() ) );
            for (int i = 0; i < components.size(); i++) {
                sbuBuffer.append(components.get(i).getView(v3)) ;
            }
        sbuBuffer.append( ComponentManager.parseComponentStyle(this, getData().getData(0).toString()) );
        sbuBuffer.append("</div>");
        return sbuBuffer.toString();
    }
}
