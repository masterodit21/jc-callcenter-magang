/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator.ui;

import com.nikita.generator.Component;
import static com.nikita.generator.Component.escapeHtml;
import com.nikita.generator.NikitaViewV3;
/**
 * created by 13k.mail@gmail.com
 */
public class Label extends Component{
    
    @Override
    public String getView(NikitaViewV3 v3) {
        v3.setData(this);
        StringBuilder sb = new StringBuilder();
        sb.append(nDivView("nikitalabel", getViewAttribut(), getViewStyle(), getViewClass()));//sb.append("<div ").append(getStyle()!=null?getStyle().getViewAttr("n-div-", " "):"").append(" style=\"").append(isVisible()?"":"display:none;").append(getViewStylePrefix("n-div-")).append("").append("").append(getViewStyle()).append("\" ").append(getViewAttribut()).append("   title=\"").append( escapeHtml(getTooltip()) ).append("\"  class=\"component ").append(getVisibleEnable()).append("").append(getFormJsId()).append(" nikitalabel\"  id=\"").append(getJsId()).append("\" nid=\"").append(getId()).append("\"  nform=\"").append(getFormId()).append("\"  ntype=\"nikitalabel\" >\n");
        sb.append(getTagView()); 
        //sb.append(getLabelView()); 
        if (!isGone()) {
            sb.append("<label class=\"form-label\" style=\"").append("").append("\" id=\"").append(getJsId()).append("_text\" ").append(getClickAction()).append("  >").append("").append(getLabelText()).append("</label>\n");
        }
        sb.append("</div>");
        return sb.toString();
        
        
    }
    private String getLabelText(){
        if (getStyle()!=null && getStyle().getInternalStyle().containsKey("n-escape")) {
            String s = getStyle().getInternalStyle().getData("n-escape").toString();
            if (s.equalsIgnoreCase("true")||s.equalsIgnoreCase("html")) {
                return escapeHtml(getText());
            }            
        }
        return getText();
    }
}
