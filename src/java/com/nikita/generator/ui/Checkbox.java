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
import com.rkrzmail.nikita.utility.Utility;
/**
 * created by 13k.mail@gmail.com
 */
public class Checkbox extends Component{
    
    @Override
    public String getView(NikitaViewV3 v3) {
        v3.setData(this);
        StringBuilder sb = new StringBuilder();
        sb.append(nDivView("nikitacheckbox"));// sb.append("<div ").append(getViewAttributPrefix("n-div-")).append(" style=\"").append(isVisible()?"":"display:none;").append(getViewStylePrefix("n-div-")).append("\" class=\"component ").append(getVisibleEnable()).append(getFormJsId()).append(" nikitacheckbox\"  id=\"").append(getJsId()).append("\" nid=\"").append(getId()).append("\"  nform=\"").append(getFormId()).append("\"  ntype=\"nikitacheckbox\"   >");
        sb.append(getTagView()); 
        
        if (!isGone()) {
            sb.append(nTableView());//sb.append("<table ").append(getViewAttributPrefix("n-table-")).append("><tr>");
                sb.append(nLabelView());
                
                sb.append("<td id=\""+getJsId()+"_verr\" class=\"verrormandatory ").append(getErrorClass()).append("\" ").append(getViewAttributPrefix("n-table.td-")).append(">");                  
                    sb.append("<div ").append(isEnable()?"":"disabled").append("  >");
                    int cols=getCols();int col = 0;
                    for (int i = 0; i < getData().getArraySize(); i++) {
                        sb.append("<div class=\"").append(getErrorClass()).append("\" style=\"float:left;").append(getViewStylePrefix("n-col-"+col+"-")).append("\">" );

                        if(getData().getData(i).getData("id").toString().length()>=1){
                            sb.append("<input class=\"form-check-input\" type=\"checkbox\" ").append(isEnable()?(isEnabled(getData().getData(i).toString())?"disabled ":""):"disabled ").append(isChecked(getData().getData(i).getData("id").toString())?"checked":"").append(" name=\"").append(getJsId()).append("_text\" value=\"").append(  escapeHtml(getData().getData(i).getData("id").toString()) ).append("\" ").append(getClickAction()).append("  >").append(  escapeHtml(getData().getData(i).getData("text").toString()) ).append("</input>   \n" );
                        }else if(getData().getData(i).getArraySize()>=2){
                            sb.append("<input class=\"form-check-input\" type=\"checkbox\" ").append(isEnable()?(isEnabled(getData().getData(i).toString())?"disabled ":""):"disabled ").append(isChecked(getData().getData(i).getData(0).toString())?"checked":"").append(" name=\"").append(getJsId()).append("_text\" value=\"").append(  escapeHtml(getData().getData(i).getData(0).toString()) ).append("\" ").append(getClickAction()).append("  >").append(  escapeHtml(getData().getData(i).getData(1).toString()) ).append("</input>   \n" );
                        }else if(getData().getData(i).getArraySize()>=1){
                            sb.append("<input class=\"form-check-input\" type=\"checkbox\" ").append(isEnable()?(isEnabled(getData().getData(i).toString())?"disabled ":""):"disabled ").append(isChecked(getData().getData(i).getData(0).toString())?"checked":"").append(" name=\"").append(getJsId()).append("_text\" value=\"").append(  escapeHtml(getData().getData(i).getData(0).toString()) ).append("\" ").append(getClickAction()).append("  >").append(  escapeHtml(getData().getData(i).getData(0).toString()) ).append("</input>   \n" );
                        }else{
                            sb.append("<input class=\"form-check-input\" type=\"checkbox\" ").append(isEnable()?(isEnabled(getData().getData(i).toString())?"disabled ":""):"disabled ").append(isChecked(getData().getData(i).toString())?"checked":"").append(" name=\"").append(getJsId()).append("_text\" value=\"").append(  escapeHtml(getData().getData(i).toString()) ).append("\" ").append(getClickAction()).append("  >").append(  escapeHtml(getData().getData(i).toString()) ).append("</input>   \n" );
                        }  
                        sb.append("</div>");
                        if ( cols >=1 && ((i+1) % cols) == 0) {
                            sb.append("<br>");col=0;
                        }else{
                            col++;
                        }
                    }
                    sb.append("</div>");
                sb.append("</td>"); 
                sb.append(getCommentView()); 
            sb.append("</tr></table>");
        }
        sb.append("</div>");
        return sb.toString();
    }

    private boolean isChecked(String value){   
        return  getText().contains(Component.DOUBLEQUOTE+value+Component.DOUBLEQUOTE)||getText().contains("[\"true\"]");        
    }

 
    private int getCols(){
        String s = getViewStyle();
        if (s.contains("n-cols:")) {
            s=s.substring(s.indexOf("n-cols:")+7)+";";
            return Utility.getInt(s.substring(0,s.indexOf(";")));
        }
        return 0;
    }
    
    
 
    private boolean isEnabled(String value){  
        return  getEnable().contains(value);        
    }
    private String getEnable(){
        String s = getViewStyle();
        if (s.contains("n-enable:")) {
            s=s.substring(s.indexOf("n-enable:")+9)+";";
            return s.substring(0,s.indexOf(";"));
        }
        return "";
    }
}
