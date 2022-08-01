/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator.ui;

import com.nikita.generator.Component;
import com.nikita.generator.NikitaViewV3;
/**
 * created by 13k.mail@gmail.com
 */
public class Button extends Component{


    public String getViewNoLabel(NikitaViewV3 v3) {
        v3.setData(this);
        StringBuilder sb = new StringBuilder();
        
        sb.append(nDivView("nikitabutton", appendable("title=",DOUBLEQUOTE, escapeHtml(getTooltip()),DOUBLEQUOTE ) ));//sb.append("<div ").append(getViewAttributPrefix("n-div-")).append(" style=\"").append(isVisible()?"":"display:none;").append(getViewStylePrefix("n-div-")).append("\"  title=\"").append( escapeHtml(getTooltip()) ).append("\" class=\"component ").append(getVisibleEnable()).append(getFormJsId()).append(" nikitabutton \" id=\"").append(getJsId()).append("\" nid=\"").append(getId()).append("\"  nform=\"").append(getFormId()).append("\"  ntype=\"nikitabutton\" >"); 
        sb.append(getTagView()); 
        if (!isGone()) {
            sb.append(nTableView());
                sb.append(getLabelView()); 
                sb.append("<input  style=\"display:none\"  type=\"button\" id=\"").append(getJsId()).append("_text\" value=\"").append(escapeHtml(getText())).append("\" ").append(getClickAction()).append("    />\n");

                sb.append("<button ").append(getViewAttributPrefixHide("n-")).append(" ").append(isEnable()?"":"disabled").append("  class=\"nbutton btn btn-primary btn-sm\"  style=\"margin-top:2px;margin-bottom:2px;").append(getViewStylePrefixHide("n-")).append("\"  ").append(getClickAction()).append(" id=\"").append(getId()).append("_query\"    >").append("").append("");
                    String icon   = getIcon();   
                    if (icon.length()>=3) {
                        sb.append("<img style=\"vertical-align:middle\" ").append(isEnable()?"":"disabled").append("  src=\"static/img/add.png\"     alt=\"Nikita Generator\"    >\n" );
                    }
                    sb.append(escapeHtml(getText()));
                    if (getStyle()!=null && getStyle().getViewStyle().contains("n-badge")) {       
                        String classs = getStyle().getInternalStyle().getData("n-badge").toString();
                        sb.append("<span style=\"display:none\" class=\"nikitabadge w3-red "+classs+"\"></span>");
                    }                   
                sb.append("</button>");
            sb.append("</td>"); 
            sb.append(getCommentView()); 
            sb.append("</tr></table>");
        }
        
        sb.append(" </div>");
        return sb.toString();
    }
    
    public String getView(NikitaViewV3 v3) {
        if (getStyle()!=null && getStyle().getViewStyle().contains("n-newbutton")) {            
        }else if (getStyle()!=null  &&  getStyle().getViewStyle().contains("n-label")) {           
        }else {
             return getViewNoLabel(v3);
        }
                
        
        v3.setData(this);
        StringBuilder sb = new StringBuilder();
        
        sb.append(nDivView("nikitabutton", appendable("title=",DOUBLEQUOTE, escapeHtml(getTooltip()),DOUBLEQUOTE ) ));//sb.append("<div ").append(getViewAttributPrefix("n-div-")).append(" style=\"").append(isVisible()?"":"display:none;").append(getViewStylePrefix("n-div-")).append("\"  title=\"").append( escapeHtml(getTooltip()) ).append("\" class=\"component ").append(getVisibleEnable()).append(getFormJsId()).append(" nikitabutton \" id=\"").append(getJsId()).append("\" nid=\"").append(getId()).append("\"  nform=\"").append(getFormId()).append("\"  ntype=\"nikitabutton\" >"); 
        sb.append(getTagView()); 
        if (!isGone()) {
            sb.append(nTableView());                
                sb.append(nLabelView());
                sb.append("<td style=\"").append(getStyle()!=null?getStyle().getViewStyle("n-table-td-", " "):"").append("\">");  
                    sb.append("<input  style=\"display:none\"  type=\"button\" id=\"").append(getJsId()).append("_text\" value=\"").append(escapeHtml(getText())).append("\" ").append(getClickAction()).append("    />\n");

                    sb.append("<button ").append(getViewAttributPrefixHide("n-")).append(" ").append(isEnable()?"":"disabled").append("  class=\"nbutton\"  style=\"margin-top:0px;margin-bottom:0px;").append(getViewStylePrefixHide("n-")).append("\"  ").append(getClickAction()).append(" id=\"").append(getId()).append("_query\"    >").append("").append("");
                        String icon   = getIcon();   
                        if (icon.length()>=3) {
                            sb.append("<img style=\"vertical-align:middle\" ").append(isEnable()?"":"disabled").append("  src=\"static/img/add.png\"     alt=\"Nikita Generator\"    >\n" );
                        }
                        sb.append(escapeHtml(getText()));
                    sb.append("</button>");    
                sb.append("</td>");
            sb.append("</tr></table>");
        }
        sb.append(" </div>");
        return sb.toString();
    }
    
     private String getIcon( ){
        if (getStyle()!=null) {
            if (getStyle().getInternalObject().getData("style").containsKey("web-icon")) {
                return getBaseUrl(getStyle().getInternalObject().getData("style").getData("web-icon").toString());  
            }
        }
        return "";
    } 
}
