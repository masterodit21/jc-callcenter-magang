/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator.ui;

import com.nikita.generator.Component;
import static com.nikita.generator.Component.escapeHtml;
import com.nikita.generator.NikitaViewV3;
import com.nikita.generator.Style;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
/**
 * created by 13k.mail@gmail.com
 */
public class DateTime extends Component {
    @Override
    public String getView(NikitaViewV3 v3) {
        v3.setData(this);
        StringBuilder sb = new StringBuilder();
        sb.append(nDivView("nikitadatetime"));// sb.append("<div ").append(getStyle()!=null?getStyle().getViewAttr("n-div-", " "):"").append(" style=\"").append(isVisible()?"":"display:none;").append(getViewStylePrefix("n-div-")).append("").append("").append("\" class=\"component ").append(getVisibleEnable()).append(getFormJsId()).append(" nikitadatetime\"  id=\"").append(getJsId()).append("\" nid=\"").append(getId()).append("\"  nform=\"").append(getFormId()).append("\"  ntype=\"nikitadatetime\"   >");
        sb.append(getTagView()); 
        if (!isGone()) {
            sb.append(nTableView());// sb.append("<table style=\"").append(getStyle()!=null?getStyle().getViewStyle("n-table-", " "):"").append("\"><tr>");
                    sb.append(nLabelView());
                    String mindate = "";
                    if (getStyle()!=null && getStyle().getInternalStyle().containsKey("n-mindate")) {
                        long l = Utility.getDate(getStyle().getInternalStyle().getData("n-mindate").toString().trim());
                        mindate = " mindate=\""+l+"\" ";
                    }
                    String maxdate = "";
                    if (getStyle()!=null && getStyle().getInternalStyle().containsKey("n-maxdate")) {
                        long l = Utility.getDate(getStyle().getInternalStyle().getData("n-maxdate").toString().trim());
                        maxdate = " maxdate=\""+l+"\" ";
                    }
                    String yrrange = "";
                    if (getStyle()!=null && getStyle().getInternalStyle().containsKey("n-yearrange")) {                        
                        yrrange = " yearrange=\""+ escapeHtml(getStyle().getInternalStyle().getData("n-yearrange").toString().trim()) +"\" ";
                    }
                    sb.append("<td style=\"").append(getStyle()!=null?getStyle().getViewStyle("n-table-td-", " "):"").append("\">");     
                    //sb.append("<td>");                  
                        sb.append("<div style=\"position:relative\">");
                        sb.append("<input type=\"text\"  ").append(isEnable()?"":"disabled").append("  ").append(yrrange).append(maxdate).append(mindate).append(" class=\"ndatetime nikitasmartentry form-control form-control-sm ").append(getErrorClass()).append("\" name=\"").append("").append("\" id=\"").append(getJsId()).append("_text").append("\" readonly=\"readonly\" ").append(getClickAction().replaceFirst("onclick=", "onchange=")).append("  value=\"").append(getText()).append("\"  style=\"text-align:center;padding-right:20px;").append(getViewStyle()).append("\" />") ;
                        //if (listener!=null) {
                            sb.append("<div style=\"position:absolute;right:4px;top:50%;margin-top:-8px\">");
                                sb.append("<img src=\"static/img/"+(isSearchIcon()?"search":"date")+".png\"  style=\"width:18px;height:18px;\"  class=\"textsmart\"  alt=\"Nikita Gebnerator\"  id=\"").append(getJsId()).append("_finder\"     ").append(isEnable()?"onclick=\"showdate('#"+getJsId()+"_text');\"":"").append("    >\n" );
                            sb.append("</div>");
                        //}
                        sb.append("</div>");
                    sb.append("</td>");   
                    sb.append(getCommentView()); 
            sb.append("</tr></table>");
        }
        sb.append("</div>");
        
        
        return sb.toString();
    }
    
 
      public Style getStyle() {
        return super.getStyle()!=null?super.getStyle():Style.createStyle(); //To change body of generated methods, choose Tools | Templates.
    }
    
    private boolean isSearchIcon( ){
         if (getStyle()!=null) {
            if (getStyle().getInternalObject().getData("style").getData("n-searchicon").toString().toLowerCase().equals("true")) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isEditable(){
        if (getStyle()!=null) {
            if (getStyle().getInternalObject().getData("style").getData("n-lock").toString().toLowerCase().equals("true")) {
                return false;
            }
        }
        return isEnable();
    }
}
