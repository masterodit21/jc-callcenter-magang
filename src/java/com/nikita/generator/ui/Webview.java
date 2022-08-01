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
public class Webview extends Component{
    public String getView(NikitaViewV3 v3) {
        v3.setData(this);
        StringBuilder sb = new StringBuilder();
        sb.append(nDivView("nikitawebview")) ;//sb.append("<div ").append(getViewAttributPrefix("n-div-")).append(" style=\"").append(isVisible()?"":"display:none;").append("" ).append(getViewStylePrefix("n-div-")).append("\"   class=\"component ").append(getVisibleEnable()).append(getFormJsId()).append(" nikitawebview\"  id=\"").append(getJsId()).append("\" nid=\"").append(getId()).append("\"  nform=\"").append(getFormId()).append("\"  ntype=\"nikitawebview\"   >  \n" );
        sb.append(getTagView()); 
        if (!isGone()) {
            sb.append(getLabelView()); 
            sb.append("<iframe name=\"").append(getJsId()).append("\" src=\"").append(getBaseUrl(getText())).append("\"   style=\"border:0px;").append(getViewStyle()).append("\"  alt=\"Nikita Gebnerator\"  id=\"").append(getId()).append("_text\"   ").append(getClickAction()).append("    ></iframe>" );
        }
        sb.append("</div>");
        return sb.toString();
    }
    
    
    
}
