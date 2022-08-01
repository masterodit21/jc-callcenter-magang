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
public class HorizontalLayout extends ComponentGroup{   
    @Override
    public String getView(NikitaViewV3 v3) {
        v3.setData(this);
        StringBuilder sb = new StringBuilder();
        sb.append(nDivView("nikitahorizontallayout", getViewAttribut(), getViewStyle(), getViewClass()));//sbuBuffer.append("<div ").append(getStyle()!=null?getStyle().getViewAttr("n-div-", " "):"").append(" style=\"").append(isVisible()?"":"display:none;").append(getViewStyle()).append("\"  class=\"component nikitahorizontallayout").append(getViewClass()).append("\"  id=\"").append(getJsId()).append("\" nid=\"").append(getId()).append("\"  nform=\"").append(getFormId()).append("\"  ntype=\"nikitahorizontallayout\"  >");
        sb.append("<table style=\"").append(getStyle()!=null?getStyle().getViewStyle("n-table-", " "):"").append("\"  class=\"nikitahorizontallayout-div\">");
        sb.append("<tr class=\"nikitahorizontallayout-div\" >");        
        for (int i = 0; i < components.size(); i++) {
            sb.append("<td class=\"nikitahorizontallayout-div\" style=\"").append(components.get(i).getStyle()!=null?components.get(i).getStyle().getViewStyle("n-layout-", " "):"").append("\"  ").append(components.get(i).getStyle()!=null?components.get(i).getStyle().getViewAttr("n-layout-", " "):"").append("  >");        
            sb.append(components.get(i).getView(v3)) ;
            sb.append("</td >");
        }
        sb.append("<tr >");        
        sb.append("</table >");
        sb.append("</div>");
        return sb.toString();
    }
    public String getViewA(NikitaViewV3 v3) {
        StringBuffer sbuBuffer = new StringBuffer();
        sbuBuffer.append("<div  data-layout='{type: \"flexgrid\" }' class=\"component nikitahorisontallayout\" id=\"").append(getId()).append("\"   >");
        for (int i = 0; i < components.size(); i++) {
            sbuBuffer.append(components.get(i).getView(v3)) ;
        }
        sbuBuffer.append("</div>");
        return sbuBuffer.toString();
    } 
    public String getViewX(NikitaViewV3 v3) {
        StringBuffer sbuBuffer = new StringBuffer();
        sbuBuffer.append("<div style=\"overflow-x: hidden;  ").append(getViewStyle()).append("\"   class=\"component nikitahorisontallayout\" id=\"").append(getId()).append("\"   >");
        for (int i = 0; i < components.size(); i++) {
            sbuBuffer.append("<div style=\"float: left;\" >");
            sbuBuffer.append(components.get(i).getView(v3)) ;
            sbuBuffer.append("</div>");
         }
        sbuBuffer.append("</div>");
        return sbuBuffer.toString();
    } 
}
