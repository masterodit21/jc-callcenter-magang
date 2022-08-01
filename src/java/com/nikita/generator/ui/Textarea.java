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
public class Textarea extends Textbox{
    public String getView(NikitaViewV3 v3) {
        v3.setData(this);
        StringBuilder sb = new StringBuilder();
        sb.append(nDivView("nikitaarea"));//sb.append("<div ").append(getStyle()!=null?getStyle().getViewAttr("n-div-", " "):"").append(" style=\"").append(isVisible()?"":"display:none;").append("\"  title=\"").append( escapeHtml(getTooltip()) ).append("\"  class=\"component ").append(getVisibleEnable()).append(getFormJsId()).append(" nikitaarea\" id=\"").append(getJsId()).append("\" nid=\"").append(getId()).append("\"  nform=\"").append(getFormId()).append("\"  ntype=\"nikitaarea\"   >\n");
        sb.append(getTagView()); 
        if (!isGone()) {
            sb.append(nTableView());
                sb.append(nLabelView());
                
                String ncharaccept =  getStyle().getInternalStyle().getData("n-char-accept").toString().trim();
                String ncharucase =  getStyle().getInternalStyle().getData("n-char-ucase").toString().trim();
                String ncharlcase =  getStyle().getInternalStyle().getData("n-char-lcase").toString().trim();
                String neventchange =  getStyle().getInternalStyle().getData("n-event-change").toString().trim();
                
                sb.append("<td>");  
                    sb.append("<div style=\"position:relative\">");
                        //sb.append("<textarea   ").append(neventchange).append(ncharaccept.length()>=1 ?" onkeypress=\"return  maskKeyCode(event,'"+escapeHtml( ncharaccept ) +"')\" ":"").append(isEnable()?"":"disabled").append("   ").append(  listener!=null ?" onkeydown=\"sendenter(event,'":"onkeydown=\"sendenter(event,'").append(getFormJsId()).append("','").append(getJsId()).append("','").append("enter").append("')\" ").append(isEnable()?"":"disabled").append("  placeholder=\"").append( escapeHtml( getHint() ) ).append("\" ").append(isEnable()?"":"disabled").append("  class=\"nikitaentryarea ").append(ncharlcase.equals("true")?" n-char-lcase ":" ").append(ncharucase.equals("true")?" n-char-ucase ":" ").append(getViewClass()).append(getErrorClass()).append("\" style=\"").append(getViewStyle()).append("\"  ").append(getViewAttribut()).append("  id=\"").append(getJsId()).append("_text\">").append(getText()).append("</textarea>\n" );
                        sb.append("<textarea   ").append(neventchange).append(ncharaccept.length()>=1 ?" onkeypress=\"return  maskKeyCode(event,'"+escapeHtml( ncharaccept ) +"')\" ":"").append(isEnable()?"":"disabled").append("   ").append(  listener!=null ?" onkeydown=\"sendenter(event,'":"onkeydown=\"sendlistener(event,'").append(getFormJsId()).append("','").append(getJsId()).append("','").append("enter").append("')\" ").append(isEnable()?"":"disabled").append("  placeholder=\"").append( escapeHtml( getHint() ) ).append("\" ").append(isEnable()?"":"disabled").append("  class=\"nikitaentryarea ").append(ncharlcase.equals("true")?" n-char-lcase ":" ").append(ncharucase.equals("true")?" n-char-ucase ":" ").append(getViewClass()).append(getErrorClass()).append("\" style=\"").append(getViewStyle()).append("\"  ").append(getViewAttribut()).append("  id=\"").append(getJsId()).append("_text\">").append(getText()).append("</textarea>\n" );
                       
                        //sb.append("<textarea ").append(getViewAttribut()).append("  ").append(isEnable()?"":"disabled").append("  class=\"nikitaentryarea").append(getErrorClass()).append(getViewClass()).append("\" style=\"").append(getViewStyle()).append("\"  id=\"").append(getJsId()).append("_text\">").append(getText()).append("</textarea>\n" );
                        if (listener!=null) {
                            sb.append("<div style=\"position:absolute;right:4px;top:0px;margin-top:8px\">");
                            sb.append("<img src=\"static/img/find.png\"  style=\"width:18px;height:18px;\" class=\"textsmart\"  alt=\"Nikita Gebnerator\"  id=\"").append(getJsId()).append("_img\"     ").append(isEnable()?getClickAction("finder"):"").append("    >\n" );
                            sb.append("</div>");
                        }                        
                    sb.append("</div>");
                sb.append("</td>");  
                sb.append(getCommentView()); 
            sb.append("</tr></table>");
        }
        sb.append("</div>");
        return sb.toString();
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
