/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator.ui;

import com.nikita.generator.Component;
import com.nikita.generator.NikitaViewV3;
import com.rkrzmail.nikita.data.Nset;
/**
 * created by 13k.mail@gmail.com
 */
public class Combobox extends Textbox{
    @Override
    public String getView(NikitaViewV3 v3) {
        v3.setData(this);
        StringBuilder sb = new StringBuilder();
        
        sb.append(nDivView("nikitacombobox"));// sb.append("<div ").append(getViewAttributPrefix("n-div-")).append("  style=\"").append(isVisible()?"":"display:none;").append(getViewStylePrefix("n-div-")).append("").append("\" class=\"component ").append(getVisibleEnable()).append(getFormJsId()).append(" nikitacombobox\"  id=\"").append(getJsId()).append("\" nid=\"").append(getId()).append("\"  nform=\"").append(getFormId()).append("\"  ntype=\"nikitacombobox\"  >");
        sb.append(getTagView());//sb.append("<div style=\"display:none\" ").append("id=\"").append(getJsId()).append("_tag\">").append(getTag()).append("</div>");     
        if (!isGone()) {
            sb.append(nTableView());//sb.append("<table ").append(getViewAttributPrefix("n-table-")).append(" ><tr>");
                sb.append(nLabelView());  

                sb.append("<td ").append(getViewAttributPrefix("n-table.td-")).append(">");                    
                sb.append("<div style=\"position:relative;").append(getViewStylePrefix("n-table.div-")).append("\">");
                            String textview = getText();String defview = "";

                            StringBuffer org = new StringBuffer();
                            //origin
                            org.append("<select ").append(isEnable()?"":"disabled").append("  style=\"position:absolute;top:0px;left:0px;opacity:0;").append(getViewStylePrefixHide("n-")).append("\"  class=\"nikitacomboboxselect ncombobox form-select form-select-sm \"  id=\"").append(getJsId()).append("_text\" ").append(getClickAction().replaceFirst("sendaction", "sendactioncombo").replaceFirst("onclick=", "onchange=")).append(" ").append(!getClickAction().equals("")?"":"onchange=\"showactioncombo('").append(getFormJsId()).append("','").append(getJsId()).append("','").append("click").append("')\" ").append("   > \n");
                            //add 02/02/2016		
                            if (getStyle()!=null && getStyle().getInternalStyle().containsKey("n-list-default")) {
                                    defview="";
                                    if (getText().equals( defview )) {
                                        textview = getStyle().getInternalStyle().getData("n-list-default").toString();
                                    }
                                    org.append("<option ").append(isChecked("") ? "selected=\"true\"" : "").append("value=\"").append("").append("\" >").append(escapeHtml(  getStyle().getInternalStyle().getData("n-list-default").toString()  )).append(" </option> \n");
                            } 
                            
                            
                            
                            for (int i = 0; i < getData().getArraySize(); i++) {
                                if(getData().getData(i).getData("id").toString().length()>=1){
                                    if (getText().equals( getData().getData(i).getData("id").toString() )) {
                                        textview=getData().getData(i).getData("text").toString();
                                    }
                                    defview=getData().getData(0).getData("text").toString();;
                                    org.append("<option ").append(isChecked(getData().getData(i).getData("id").toString()) ? "selected=\"true\"" : "").append("value=\"").append(escapeHtml(getData().getData(i).getData("id").toString())).append("\" >").append(escapeHtml(getData().getData(i).getData("text").toString())).append(" </option> \n");
                                }else if(getData().getData(i).getArraySize()>=2){
                                     if (getText().equals( getData().getData(i).getData(0).toString() )) {
                                        textview=getData().getData(i).getData(1).toString();
                                    }
                                     defview=getData().getData(0).getData(1).toString();;
                                    org.append("<option ").append(isChecked(getData().getData(i).getData(0).toString()) ? "selected=\"true\"" : "").append("value=\"").append(escapeHtml(getData().getData(i).getData(0).toString())).append("\" >").append(escapeHtml(getData().getData(i).getData(1).toString())).append(" </option> \n");
                                }else if(getData().getData(i).getArraySize()>=1){
                                     if (getText().equals( getData().getData(i).getData(0).toString() )) {
                                        textview=getData().getData(i).getData(0).toString();
                                    }
                                    defview=getData().getData(0).getData(0).toString();;
                                    org.append("<option ").append(isChecked(getData().getData(i).getData(0).toString()) ? "selected=\"true\"" : "").append("value=\"").append(escapeHtml(getData().getData(i).getData(0).toString())).append("\" >").append(escapeHtml(getData().getData(i).getData(0).toString())).append(" </option> \n");
                                }else{
                                    if (getText().equals( getData().getData(i).toString() )) {
                                        textview=getData().getData(i).toString();
                                    }
                                    defview=getData().getData(0).toString();;
                                    org.append("<option ").append(isChecked(getData().getData(i).toString()) ? "selected=\"true\"" : "").append("value=\"").append(escapeHtml(getData().getData(i).toString())).append("\" >").append(escapeHtml(getData().getData(i).toString())).append(" </option> \n");
                                }            
                            }
                            org.append("</select>");
                            
                            if (getStyle()!=null && getStyle().getInternalStyle().containsKey("n-list-default")  && textview.equals("")) {
                                
                            }else  if (getData().getArraySize()>=1 && textview.equals("")) {
                                textview = defview;
                            }

                            //ui
                            sb.append("<input  ").append(isEnable()?"":"disabled").append(" value=\"").append(  escapeHtml(textview) ).append("\"  id=\"").append(getJsId()).append("_combo_text\"  class=\"nikitasmartview verrormandatory").append(getErrorClass()).append(" \" style=\"").append(getViewStylePrefixHide("n-")).append("\"  readonly />"); //disabled=\"disabled\" //placeholder=\"Select One\" 
                            sb.append("<div style=\"position:absolute;right:4px;top:4px;\">");
                                sb.append("<img ").append(isEnable()?"":"disabled").append("  style=\"width:18px;height:18px;\" src=\"static/img/combo.png\"  class=\"textsmart\"  alt=\"Nikita Gebnerator\"  id=\"").append(getJsId()).append("_finder\"     ").append(isEnable()?getClickAction("finder"):"").append("    >\n" );
                            sb.append("</div>");

                            sb.append(org.toString());

                sb.append("</div>");   
            sb.append("</td>"); 
            sb.append(getCommentView()); 
            sb.append("</tr></table>");
        }
        sb.append("</div>");
        return sb.toString();
    }
    
    private boolean isChecked(String value){
        return getText().equals(value);   //text==text     
    }

    private boolean isChecked(Nset value){
        return getText().equals(value.getData("id").toString()); //txt=={"id":"text","id":"text"}      
    }
 
    
}
