/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator.ui;

import com.nikita.generator.Component;
import com.nikita.generator.NikitaService;
import com.nikita.generator.NikitaViewV3;
/**
 * created by 13k.mail@gmail.com
 */
public class Image extends Component{

    @Override
    public String getView(NikitaViewV3 v3) {
        v3.setData(this);
        StringBuilder sb = new StringBuilder();
        sb.append(nDivView("nikitaimage"));// sb.append("<div style=\"").append(getStyle()!=null?getStyle().getViewStyle("n-div-", " "):"").append(" \" ").append(getStyle()!=null?getStyle().getViewAttr("n-div-", " "):"").append("  title=\"").append( escapeHtml(getTooltip()) ).append("\"  class=\"component ").append(getVisibleEnable()).append(getViewClass()).append(getFormJsId()).append(" nikitaimage\"   id=\"").append(getJsId()).append("\" nid=\"").append(getId()).append("\"  nform=\"").append(getFormId()).append("\"  ntype=\"nikitaimage\"  >" );
        sb.append(getTagView());
        if (!isGone()) {
            sb.append("<div style=\"display:none\" ").append("id=\"").append(getJsId()).append("_img\">").append(escapeHtml(getText())).append("</div>"); 
            sb.append(getLabelView());
            sb.append("<img src=\"").append( getBaseUrl(getText())).append("\"  ").append(getViewAttribut()).append("   style=\"").append(getViewStyle()).append("\"   alt=\"\"  id=\"").append(getJsId()).append("_text\"   class=\"").append(getViewClass()).append("\"  ").append(getClickAction()).append("    >\n" );
        }
        sb.append("</div>");
        return sb.toString();
    }
    
    protected String getClickAction(){
        if (isNClient()) {
            return super.getClickAction("nclient");
        }                
        return super.getClickAction();
    }
    private boolean isNClient( ){
         if (getStyle()!=null) {
            if (getStyle().getInternalObject().getData("style").getData("n-client").toString().toLowerCase().equals("true")) {
                return true;
            }
        }
        return false;
    }
}
