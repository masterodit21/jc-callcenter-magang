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
 * @author user
 */
public class Combolist extends Component{
    @Override
    public String getView(NikitaViewV3 v3) {
        v3.setData(this);
        StringBuilder sb = new StringBuilder();
        sb.append(nDivView("nikitacombolist"));//sb.append("<div ").append(getViewAttributPrefix("n-div-")).append("  style=\"").append(isVisible()?"":"display:none;").append(getViewStylePrefix("n-div-")).append("").append("").append("\" class=\"component ").append(getVisibleEnable()).append(getFormJsId()).append(" nikitacombolist\"  id=\"").append(getJsId()).append("\" nid=\"").append(getId()).append("\"  nform=\"").append(getFormId()).append("\"  ntype=\"nikitacombolist\"   >");
        sb.append(getTagView());//sb.append("<div style=\"display:none\" ").append("id=\"").append(getJsId()).append("_tag\">").append(getTag()).append("</div>");     
        if (!isGone()) {
            sb.append(nTableView());//sb.append("<table><tr>");
                sb.append(nLabelView());

                sb.append("<td     id=\""+getJsId()+"_verr\"  class=\"verrormandatory form-select form-select-sm ").append(getErrorClass()).append("\">");  ;   
                sb.append("<select ").append(isEnable()?"":"disabled").append("  class=\"ncombolist nikitaentry nikitacombolistselect\" multiple=\"multiple\" id=\"").append(getJsId()).append("_select\"  ").append(getClickAction().replaceFirst("onclick=", "onchange=")).append("  > \n");
                for (int i = 0; i < getData().getArraySize(); i++) {
                    if(getData().getData(i).getData("id").toString().length()>=1){
                        sb.append("<option ").append(isChecked(getData().getData(i).getData("id").toString()) ? "selected" : "").append(" name=\"").append(getId()).append("_text\" value=\"").append(escapeHtml(getData().getData(i).getData("id").toString())).append("\" >").append(escapeHtml(getData().getData(i).getData("text").toString())).append(" </option> \n");
                    }else if(getData().getData(i).getArraySize()>=2){
                        sb.append("<option ").append(isChecked(getData().getData(i).getData(0).toString()) ? "selected" : "").append(" name=\"").append(getId()).append("_text\" value=\"").append(escapeHtml(getData().getData(i).getData(0).toString())).append("\" >").append(escapeHtml(getData().getData(i).getData(1).toString())).append(" </option> \n");
                    }else if(getData().getData(i).getArraySize()>=1){
                        sb.append("<option ").append(isChecked(getData().getData(i).getData(0).toString()) ? "selected" : "").append(" name=\"").append(getId()).append("_text\" value=\"").append(escapeHtml(getData().getData(i).getData(0).toString())).append("\" >").append(escapeHtml(getData().getData(i).getData(0).toString())).append(" </option> \n");
                    }else{
                        sb.append("<option ").append(isChecked(getData().getData(i).toString()) ? "selected" : "").append(" name=\"").append(getId()).append("_text\" value=\"").append(escapeHtml(getData().getData(i).toString())).append("\" >").append(escapeHtml(getData().getData(i).toString())).append(" </option> \n");
                    }   
                }
                sb.append("</select>");


            sb.append("</td>");
            sb.append(getCommentView()); 
            sb.append("</tr></table>"); 
        }
        sb.append("</div>");
        return sb.toString();
    }
    
    private boolean isChecked(String value){        
        return getText().contains(Component.DOUBLEQUOTE+value+Component.DOUBLEQUOTE);        
    }
    
    @Override
    public void restoreData(Nset data) {
        super.restoreData(data); //To change body of generated methods, choose Tools | Templates.
    }
}
