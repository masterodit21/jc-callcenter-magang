/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator.ui;

import com.nikita.generator.Component;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaService;
import com.nikita.generator.NikitaViewV3;
import com.nikita.generator.Style;
/**
 * created by 13k.mail@gmail.com
 */
public class Textsmart extends Textbox{
    
    @Override
    protected boolean isTextsmart(){
        return true;
    }

    public String getViewA(NikitaViewV3 v3) {
        v3.setData(this);
        StringBuilder sb = new StringBuilder();
        sb.append(nDivView("nikitatext"));//sb.append("<div ").append(getStyle()!=null?getStyle().getViewAttr("n-div-", " "):"").append(" style=\"").append(isVisible()?"":"display:none;").append(getViewStylePrefix("n-div-")).append("").append("").append("\"  title=\"").append( escapeHtml(getTooltip()) ).append("\"   class=\"component ").append(getVisibleEnable()).append(getFormJsId()).append(" nikitatext\" id=\"").append(getJsId()).append("\" nid=\"").append(getId()).append("\"  nform=\"").append(getFormId()).append("\"  ntype=\"nikitatext\"   >\n");
            sb.append(getTagView()); 
            if (!isGone()) {
                sb.append(nTableView());
                    sb.append(nLabelView());
                    sb.append("<td style=\"").append(getStyle()!=null?getStyle().getViewStyle("n-table-td-", " "):"").append("\">");                  
                        sb.append("<div style=\"position:relative\">");
                          
                    //onkeydown="maskKeyCode(event,'')" //escapeHtml( getHint() ) 
                      
                    String ncharaccept =  getStyle().getInternalStyle().getData("n-char-accept").toString().trim();
                    String ncharucase =  getStyle().getInternalStyle().getData("n-char-ucase").toString().trim();
                    String ncharlcase =  getStyle().getInternalStyle().getData("n-char-lcase").toString().trim();
                    String neventchange =  getStyle().getInternalStyle().getData("n-event-change").toString().trim();
                    neventchange = neventchange.equals("true")?" onchange=\"sendaction('"+getFormJsId()+"','"+getJsId()+"','change')\" ":"";
                        
                     
                     sb.append("<input class=\"form-control form-control-sm\"  type=\""+(isPassword()?"password":"text")+"\"  ").append(neventchange).append(ncharaccept.length()>=1||isTypeTime() ?" onkeypress=\"return  "+(isTypeTime()?"gettimekey":"maskKeyCode")+"(event,'"+escapeHtml( ncharaccept ) +"')\" ":"").append(isEnable()?"":"disabled").append("   ").append(  listener!=null ?" onkeydown=\"sendenter(event,'":"onkeydown=\"sendenter(event,'").append(getFormJsId()).append("','").append(getJsId()).append("','").append("enter").append("')\" ").append(isEditable()?"":"readonly").append(" placeholder=\"").append( escapeHtml( getHint() ) ).append("\"  class=\"nikitasmartentry verrormandatory").append(ncharlcase.equals("true")?" n-char-lcase ":" ").append(ncharucase.equals("true")?" n-char-ucase ":" ").append(getViewClass()).append(getErrorClass()).append("\" style=\"padding-right:20px;").append(getViewStyle()).append("\" id=\"").append(getJsId()).append("_text\" value=\"").append(  escapeHtml(getText()) ).append("\"  ").append(getViewAttribut()).append(" />\n" );

                         
                        
                        if (listener!=null || (getStyle()!=null && getStyle().getViewStyle().contains("n-finder:true"))) {
                            sb.append("<div style=\"position:absolute;right:4px;top:50%;margin-top:-8px\">");
                                sb.append("<img ").append(isEnable()?"":"disabled").append("  style=\"width:18px;height:18px;\" src=\""+getSearchIcon()+"\"  class=\"textsmart\"  alt=\"Nikita Generator\"  id=\"").append(getJsId()).append("_finder\"     ").append(isEnable()?getClickAction("finder"):"").append("    >\n" );
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

    
    public Style getStyle() {
        return super.getStyle()!=null?super.getStyle():Style.createStyle(); //To change body of generated methods, choose Tools | Templates.
    }
    
    private String getSearchIcon( ){
        if (getStyle()!=null) {
            if (getStyle().getInternalObject().getData("style").getData("n-searchicon").toString().toLowerCase().equals("true")) {
                return getBaseUrl("static/img/search.png");  
            }else if (getStyle().getInternalObject().getData("style").getData("n-searchicon").toString().trim().length()>=3) {
                return getBaseUrl(getStyle().getInternalObject().getData("style").getData("n-searchicon").toString());
            }
        }
        return getBaseUrl("static/img/find.png");
    } 
    
      private boolean isPassword( ){
          if (getStyle()!=null) {
             if (getStyle().getInternalObject().getData("style").getData("n-password").toString().toLowerCase().equals("true")) {
                 return true;
                }else if (getStyle().getInternalObject().getData("style").getData("n-password-mask").toString().toLowerCase().equals("true")) {
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
    private boolean isTypeTime(){
        if (getStyle()!=null) {
            if (getStyle().getInternalObject().getData("style").getData("n-type").toString().toLowerCase().equals("time")) {
                return true;
            }
        }
        return false;
    }
}
