/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator.ui;

import com.nikita.generator.Component;
import static com.nikita.generator.Component.escapeHtml;
import com.nikita.generator.IAdapterListener;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaViewV3;
import com.nikita.generator.Style;
import static com.nikita.generator.ui.Tablegrid.TYPE_BUTTON;
import static com.nikita.generator.ui.Tablegrid.TYPE_CHECKBOK;
import static com.nikita.generator.ui.Tablegrid.TYPE_IMAGE;
import com.nikita.generator.ui.layout.HorizontalLayout;
import com.nikita.generator.ui.layout.NikitaForm;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import java.util.Hashtable;
/**
 * created by 13k.mail@gmail.com
 */
public class ListView extends Tablegrid{
    public String getView(NikitaViewV3 v3) {         
        v3.setData(this);
        Nset restoreNset = Nset.readJSON(super.getText());//must super
        
        StringBuilder sb = new StringBuilder();// border=\"1\" cellpadding=\"5\" cellspacing=\"2\"
        sb.append(nDivView("nikitatablegrid"));//ssb.append("<div ").append(isEnable()?"":"disabled").append("  style=\"").append(isVisible()?"":"display:none;").append("\"  class=\"component ").append(getVisibleEnable()).append(getFormJsId()).append(" nikitatablegrid\" id=\"").append(getJsId()).append("\" nid=\"").append(getId()).append("\"  nform=\"").append(getFormId()).append("\"  ntype=\"nikitatablegrid\"   >\n" );
        sb.append("<p><b>").append(escapeHtml(getLabel())).append("</b></p>" );        
        sb.append(getTagView());//sb.append("<div style=\"display:none\" ").append("id=\"").append(getJsId()).append("_tag\">").append(getTag()).append("</div>"); 
            if (!isGone()) {
            sb.append("<div style=\"display:none\" ").append("id=\"").append(getJsId()).append("_cell_tag\"  nid=\"").append(getId()).append("_cell_tag").append("\" >").append("").append("</div>"); 
            if (!getViewStyle().contains("n-hide-pageup:true")) {
                //sb.append(getViewFilterPage(restoreNset,false)); 
            }       

            sb.append("<div style=\"overflow-x:auto\">");  
            sb.append("<table   id=\"").append(getJsId()).append("_grid\"  class=\"nikitatablegrid-table\"  style=\"width:100%\" ").append(getStyle()!=null?getStyle().getViewAttr("n-table-", " "):"").append("  >");


            //render datagrid
            for (int row = 0; row < getData().getArraySize(); row++) {
                sb.append("<tr class=\"").append((row%2==0)?"odd":"even").append("\"   >");
                sb.append("<td>");
                if (igrid!=null) {
                    Component  vv = igrid.getViewItem(row, 0, this, getData());
                    if (vv instanceof  NikitaForm) {
                        sb.append(((NikitaForm)vv).getViewTag(v3)); 
                        sb.append(vv.getView(v3));
                    }else{
                        sb.append(vv.getView(v3));   
                     }


                } else{
                    sb.append("");
                }    
                sb.append("</td>");       
                sb.append("</tr>");
            }
            sb.append("</table>");
            sb.append("</div>");  
            //sb.append(getViewFilterPage(restoreNset,true));
        }
        sb.append("</div>");  
        return sb.toString();
    }

    @Override
    public int getShowPerPage() {
        return 1000000000; 
    }

    @Override
    public int getSeletedRow() {
        return 0; //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
