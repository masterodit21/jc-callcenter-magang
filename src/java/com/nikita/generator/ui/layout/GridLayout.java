/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator.ui.layout;

import com.nikita.generator.ComponentGroup;
import com.nikita.generator.NikitaViewV3;
import com.rkrzmail.nikita.utility.Utility;
/**
 * created by 13k.mail@gmail.com
 */
public class GridLayout extends ComponentGroup{
    public String getView(NikitaViewV3 v3) {
        v3.setData(this);
        StringBuffer sbuBuffer = new StringBuffer();
        sbuBuffer.append("<table   class=\"nikitagridlayout\"      ").append(getViewAttribut()).append("  style=\"").append(isVisible()?"":"display:none;").append(getViewStyle()).append("\"  width=\"100%\"   id=\"").append(getJsId()).append("\" nid=\"").append(getId()).append("\"  nform=\"").append(getFormId()).append("\"  ntype=\"nikitagridlayout\"   >");

            int col = getCols();
      
            for (int i = 0; i < components.size(); i++) {
                sbuBuffer.append("<tr>");
                for (int j = 0; j < col; j++) {
                    int xi=i*col+j;
                    sbuBuffer.append("<td style=\"").append(components.get(i).getStyle()!=null?components.get(i).getStyle().getViewStyle("n-layout-", " "):"").append("\">");
                        if (xi < components.size()) {
                            sbuBuffer.append(components.get(xi).getView(v3)) ;
                        }
                    sbuBuffer.append("</td>");
                } 
                int xi=i*col+col;
                sbuBuffer.append("</tr>");
               if (!(xi < components.size())) {
                  break;
               } 
            }
        sbuBuffer.append("</table>");
        return sbuBuffer.toString();
    }
    
    private int getCols(){
        String s = getViewStyle();
        if (s.contains("n-cols:")) {
            s=s.substring(s.indexOf("n-cols:")+7)+";";
            return Utility.getInt(s.substring(0,s.indexOf(";")));
        }
        return 0;
    }
    
    private String getStyleCol(int cl){
        String s = getViewAttribut();
        String k = "n-col-"+cl+"-style=";
        if (s.contains(k)) {
           s=s.substring(s.indexOf(k)+k.length())+";"; 
 
           return   s.substring(0,s.indexOf(";"));
        }
        return "";
    }
}
