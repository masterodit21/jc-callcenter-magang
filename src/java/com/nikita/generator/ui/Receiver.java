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
 *
 * @author rkrzmail
 */
public class Receiver  extends Component{
    @Override
    public String getView(NikitaViewV3 v3) {
        v3.setData(this);
        StringBuilder sb = new StringBuilder();
        sb.append(nDivView("nikitareceiver", appendable("receiver=",DOUBLEQUOTE,escapeHtml(getText()),DOUBLEQUOTE) ));//sb.append("<div style=\"display:none;\" receiver=\"").append(escapeHtml(getText())).append("\"  class=\"component ").append(getVisibleEnable()).append("").append(getFormJsId()).append(" nikitareceiver\"  id=\"").append(getJsId()).append("\" nid=\"").append(getId()).append("\"  nform=\"").append(getFormId()).append("\" jsform=\"").append(getFormJsId()).append("\"  ntype=\"nikitareceiver\" >\n");
        sb.append(getTagView());
        sb.append("</div>");
        return sb.toString();
    }

  
    
}