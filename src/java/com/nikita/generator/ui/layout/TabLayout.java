/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator.ui.layout;

import com.nikita.generator.Component;
import com.nikita.generator.ComponentGroup;
import com.nikita.generator.NikitaViewV3;
import com.rkrzmail.nikita.utility.Utility;

/**
 *
 * @author rkrzmail
 */
public class TabLayout extends ComponentGroup{
    private int icount = -99999;
  
    public void setActive(int i){
        icount=i;
    }
    
    public String getView(NikitaViewV3 v3) {
        v3.setData(this);
        icount=getStyleActivate();
        StringBuffer sbuBuffer = new StringBuffer();
        sbuBuffer.append("<div  activate=\"").append(icount).append("\"  ").append(getViewAttribut()).append("  style=\" ").append(isVisible()?"":"display:none;").append(getViewStyle()).append("\"  class=\"component tablayout ntab\"   id=\"").append(getJsId()).append("\" nid=\"").append(getId()).append("\"  nform=\"").append(getFormId()).append("\"  ntype=\"tablayout\"     >");
        sbuBuffer.append("<ul>");
        for (int i = 0; i < components.size(); i++) {
            sbuBuffer.append("<li><a href=\"#").append(getJsId()).append("-").append(i).append("\">").append(components.elementAt(i).getText()).append("</a></li>");
        }
        sbuBuffer.append("</ul>");
        for (int i = 0; i < components.size(); i++) {
            sbuBuffer.append("<div id=\"").append(getJsId()).append("-").append(i).append("\">");
            sbuBuffer.append(components.get(i).getView(v3)) ;
            sbuBuffer.append("</div>");
        }        
        sbuBuffer.append("</div>");
        return sbuBuffer.toString();
    }
    private int getStyleActivate(){
        String s = getViewAttribut();
        String k = "n-activate=";
        if (s.contains(k)) {
           s=s.substring(s.indexOf(k)+k.length())+";";  
           return  Utility.getInt(s);
        }
        return icount;
    }
}
