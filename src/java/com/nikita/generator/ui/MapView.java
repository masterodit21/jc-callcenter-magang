/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator.ui;

import com.nikita.generator.Component;
import com.nikita.generator.NikitaViewV3;
import com.rkrzmail.nikita.utility.Utility;
/**
 * created by 13k.mail@gmail.com
 */
public class MapView extends Component{
    public String getView(NikitaViewV3 v3) {
        v3.setData(this);
        StringBuilder sb = new StringBuilder();
        sb.append(nDivView("nikitamap"));//sb.append("<div ").append(getStyle()!=null?getStyle().getViewAttr("n-div-", " "):"").append(" style=\"position: relative\" title=\"").append( escapeHtml(getTooltip()) ).append("\"  class=\"component ").append(getVisibleEnable()).append(getViewClass()).append(getFormJsId()).append(" nikitamap\"   id=\"").append(getJsId()).append("\" nid=\"").append(getId()).append("\"  nform=\"").append(getFormId()).append("\"  ntype=\"nikitamap\"  >" );
        sb.append(getTagView());//sb.append("<div style=\"display:none\" ").append("id=\"").append(getJsId()).append("_tag\">").append(escapeHtml(getTag())).append("</div>"); 
        if (!isGone()) {
            sb.append(getLabelView());
            sb.append("<div style=\"display:none\" ").append("id=\"").append(getJsId()).append("_map_text\">").append(escapeHtml(getText())).append("</div>"); 

            sb.append("<div class=\"nmap\" id=\"").append(getJsId()).append("_map\"  compid=\"").append(getJsId()).append("\"   style=\"").append(getViewStyle()).append("\"> ");
            sb.append("</div>");
        }
        sb.append("</div>");
        return sb.toString();
    }
     
     
    
    
}
