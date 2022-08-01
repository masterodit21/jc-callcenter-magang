/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator.ui;

import com.nikita.generator.Component;
import static com.nikita.generator.Component.escapeHtml;
import com.nikita.generator.NikitaViewV3;
import com.rkrzmail.nikita.data.Nset;

/**
 *
 * @author rkrzmail
 */
public class TextAutoComplete extends Textsmart{

    @Override
    public String getView(NikitaViewV3 v3) {
        v3.setData(this);
        StringBuilder sb = new StringBuilder();
        sb.append(nDivView("nikitaautotext"));//sb.append("<div ").append(getStyle()!=null?getStyle().getViewAttr("n-div-", " "):"").append("  style=\"").append(isVisible()?"":"display:none;").append("\"  title=\"").append( escapeHtml(getTooltip()) ).append("\"   class=\"component ").append(getVisibleEnable()).append(getFormJsId()).append(" nikitaautotext\" id=\"").append(getJsId()).append("\" nid=\"").append(getId()).append("\"  nform=\"").append(getFormId()).append("\"  ntype=\"nikitaautotext\"  >\n");
            sb.append(getTagView());//sb.append("<div style=\"display:none\" ").append("id=\"").append(getJsId()).append("_tag\">").append(getTag()).append("</div>");     
            if (!isGone()) {
                sb.append(nTableView());
                    sb.append(nLabelView());  
                    sb.append("<td>");  ;   
                    //sb.append("<input ").append(  listener!=null ?" onkeypress=\"sendenter(event,'":"on=\"sendenter(event,'").append(getFormJsId()).append("','").append(getJsId()).append("','").append("enter").append("')\"   ").append(isEnable()?"":"disabled").append(" placeholder=\"").append( escapeHtml( getHint() ) ).append("\"  class=\"nikitaentry nautocomplete").append(getErrorClass()).append(getViewClass()).append("\" style=\"").append(getViewStyle()).append("\" type=\""+(isPassword()?"password":"text")+"\" id=\"").append(getJsId()).append("_text\" value=\"").append(  escapeHtml(getText()) ).append("\" />\n" );
                    String ncharaccept =  getStyle().getInternalStyle().getData("n-char-accept").toString().trim();
                    String ncharucase =  getStyle().getInternalStyle().getData("n-char-ucase").toString().trim();
                    String ncharlcase =  getStyle().getInternalStyle().getData("n-char-lcase").toString().trim();
                    String neventchange =  getStyle().getInternalStyle().getData("n-event-change").toString().trim();
                    neventchange = neventchange.equals("true")?" onchange=\"sendaction('"+getFormJsId()+"','"+getJsId()+"','change')\" ":"";     
                    boolean bfinder = false;
                    sb.append("<input  ").append(isTypeTime()?"onkeyup=\"gettimekey(this)\"":"").append(" ").append(getStyle().getInternalStyle().getData("n-currency-format").toString().equalsIgnoreCase("true")?"onkeyup=\"currencykeyup(this)\"":"").append("  type=\""+(isPassword()?"password":"text")+"\" ").append(neventchange).append(ncharaccept.length()>=1||isTypeTime() ?" onkeypress=\"return  "+(isTypeTime()?"gettimemask":"maskKeyCode")+"(event,'"+escapeHtml( ncharaccept ) +"')\" ":"").append(isEnable()?"":"disabled").append("   ").append(  listener!=null ?" onkeydown=\"sendenter(event,'":"onkeydown=\"sendenter(event,'").append(getFormJsId()).append("','").append(getJsId()).append("','").append("enter").append("')\" ").append(isEditable()?"":"readonly").append(" placeholder=\"").append( escapeHtml( getHint() ) ).append("\"  class=\"nikitaentry nautocomplete form-control form-control-sm ").append(ncharlcase.equals("true")?" n-char-lcase ":" ").append(ncharucase.equals("true")?" n-char-ucase ":" ").append(getViewClass()).append(getErrorClass()).append("\" style=\"").append(bfinder?"  ":"").append(getViewStyle()).append("\" id=\"").append(getJsId()).append("_text\" value=\"").append(  escapeHtml(getText()) ).append("\"  ").append(getViewAttribut()).append(" />\n" );
                        
                        
                        sb.append("<div style=\"display:none\" id=\"").append(getJsId()).append("_text_data\" ").append(getClickAction()).append(" >" );
                        sb.append(escapeHtml(populateData()));
                        sb.append("</div>");   
                    sb.append("</td>");
                    sb.append(getCommentView()); 
                sb.append("</tr></table>");
            }
        sb.append("</div>");
        return sb.toString();
    }
    private boolean isTypeTime(){         
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
    private String populateData(){
        Nset  n  =  Nset.newArray();
         		 
        for (int i = 0; i < getData().getArraySize(); i++) {
                    if(getData().getData(i).getData("id").toString().length()>=1){
                    //checkBox.setTag(getData().getData(i).getData("id").toString());
                            //code.add(getData().getData(i).getData("id").toString());
                    n.addData(getData().getData(i).getData("text").toString());


            }else if(getData().getData(i).getArraySize()>=2){
                //checkBox.setTag(getData().getData(i).getData(0).toString());
                    //code.add(getData().getData(i).getData(0).toString());
                    n.addData(getData().getData(i).getData(1).toString());

            }else if(getData().getData(i).getArraySize()>=1){
                    // checkBox.setTag(getData().getData(i).getData(0).toString());
                    //code.add(getData().getData(i).getData(0).toString());
                    n.addData(getData().getData(i).getData(0).toString());

            }else{
                    //checkBox.setTag(getData().getData(i).toString());
                    //code.add(getData().getData(i).toString());
                    n.addData(getData().getData(i).toString());

            }  
        }
        return n.toJSON();
    }
        
    private boolean isPassword( ){
          if (getStyle()!=null) {
             if (getStyle().getInternalObject().getData("style").getData("n-password").toString().toLowerCase().equals("true")) {
                 return true;
             }
         }
         return false;
     }
}