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
public class FrameLayout extends ComponentGroup{
    
    public String getViewA(NikitaViewV3 v3) {
        v3.setData(this);
        StringBuffer sbuBuffer = new StringBuffer();
        sbuBuffer.append("<div ").append(getStyle()!=null?getStyle().getViewAttr("n-div-", " "):"").append(" style=\"display: block; ").append(isVisible()?"":"display:none;").append(getViewStyle()).append("  \" class=\"component nikitaframelayout\"  id=\"").append(getJsId()).append("\" nid=\"").append(getId()).append("\"  nform=\"").append(getFormId()).append("\"  ntype=\"nikitaframelayout\"    >");
        for (int i = 0; i < components.size(); i++) {
            
            sbuBuffer.append(components.get(i).getView(v3)) ;
        
        }
        sbuBuffer.append("</div>");
        return sbuBuffer.toString();
    }
    public String getView(NikitaViewV3 v3) {
        StringBuffer sbuBuffer = new StringBuffer();
        sbuBuffer.append("<div style=\"display: block; position: relative; ").append(isVisible()?"":"display:none;").append(getViewStyle()).append("\"  class=\"component nikitaframelayout\" id=\"").append(getJsId()).append("\" nid=\"").append(getId()).append("\"  nform=\"").append(getFormId()).append("\"  ntype=\"nikitaframelayout\"  >");
        for (int i = 0; i < components.size(); i++) {
            sbuBuffer.append("<div style=\"display: block; position: absolute;").append(components.get(i).getViewStyle()).append(" \">");
            
            sbuBuffer.append(components.get(i).getView(v3)) ;
            
            sbuBuffer.append("</div>");
        }
        sbuBuffer.append("</div>");
        return sbuBuffer.toString();
    }
    
    public String getViewC(NikitaViewV3 v3) {
        StringBuffer sbuBuffer = new StringBuffer();
        sbuBuffer.append("<div style=\"").append(isVisible()?"":"display:none;").append(getViewStyle()).append("\"  class=\"component nikitaframelayout\" id=\"").append(getJsId()).append("\" nid=\"").append(getId()).append("\"  nform=\"").append(getFormId()).append("\"  ntype=\"nikitaframelayout\"  >");
        sbuBuffer.append("<table><tr>");
            sbuBuffer.append("<td style=\"").append(getStyle()!=null?getStyle().getViewStyle("n-gravity-", " "):"").append("\" >");                  
                for (int i = 0; i < components.size(); i++) {
                    sbuBuffer.append("<div style=\"").append(components.get(i).getViewStyle()).append(" \">");
                    sbuBuffer.append(components.get(i).getView(v3)) ;
                    sbuBuffer.append("</div>");
                }
            sbuBuffer.append("</td>");   
        sbuBuffer.append("</tr></table>");       
        sbuBuffer.append("</div>");
        return sbuBuffer.toString();
    }
}
