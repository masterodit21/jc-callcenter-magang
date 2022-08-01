/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator.ui;

import com.nikita.generator.Component;
import com.nikita.generator.ComponentGroup;
import com.nikita.generator.NikitaServlet;
import com.nikita.generator.NikitaViewV3;
import com.rkrzmail.nikita.utility.Utility;
/**
 * created by 13k.mail@gmail.com
 */

public class Accordion extends ComponentGroup{
    private int icount = -99999;
  
    public void setActive(int i){
        icount=i;
    }
    
    @Override
    public String getView(NikitaViewV3 v3) {
        v3.setData(this);
        icount=getStyleActivate();
        StringBuilder sb = new StringBuilder();
        sb.append("<div activate=\"").append(icount).append("\"  ").append(getViewAttributPrefixHide("n-")).append(getViewAttributPrefix("n-div-")).append(" style=\"").append(isVisible()?"":"display:none;").append(getViewStylePrefixHide("n-")).append(getViewStylePrefix("n-div-")).append("\" class=\"component naccordion nikitaaccordion").append(getVisibleEnable()).append(getViewClass()).append(getFormJsId()).append(" nikitaaccordion\" id=\"").append(getJsId()).append("\" nid=\"").append(getId()).append("\"  nform=\"").append(getFormId()).append("\"  ntype=\"nikitaaccordion\" >"); 
        //sb.append( nDivView( "nikitaaccordion"  , "activate=\""+icount+"\"" , "", "naccordion") );
        boolean headerAccordion = false;
            
        for (int i = 0; i < getComponentCount(); i++) {
            if (getComponent(i) instanceof ComponentGroup && ((ComponentGroup)getComponent(i)).isVisible() ) {
                sb.append("<h3 style=\"margin:1px;\">").append(((ComponentGroup)getComponent(i)).getText()).append("</h3>");
                sb.append("<div style=\"padding:5px;\">");
                for(int j=0 ;j< ((ComponentGroup)getComponent(i)).getComponentCount() ;j++){
                    sb.append(((ComponentGroup)getComponent(i)).getComponent(j).getView(v3)) ;                    
                }                
                sb.append("</div>");
            }else if(  ((ComponentGroup)getComponent(i)).isVisible() ){
                if (!headerAccordion) {
                    headerAccordion=true;
                    sb.append("<h3 style=\"margin:1px;\">").append(escapeHtml(getText())).append("</h3>");
                }
                sb.append("<div>");
                for (i = i; i < getComponentCount(); i++) { 
                    sb.append(getComponent(i).getView(v3)) ;       
                }                             
                sb.append("</div>");
            }             
        }   
        
        if (getData().getArraySize()>=1 ) {
            for (int i = 0; i < getData().getArraySize(); i++) {
                sb.append("<h3 style=\"margin:1px;\">").append(getData().getData(i).getData("text").toString()).append("</h3>");   
                sb.append("<div style=\"padding:5px;overflow:hidden;\">");

                sb.append("<ul class=\"nmenulist\"  style=\"border:0px;\" >");
                for(int j=0 ;j< getData().getData(i).getData("child").getArraySize() ;j++){
                    sb.append( "<li  style=\"padding-left:20px;padding-top:5px;padding-bottom:5px;\"  ").append(getClickAction("click-"+getData().getData(i).getData("child").getData(j).getData("id").toString() )).append(" >").append(  getData().getData(i).getData("child").getData(j).getData("text").toString()  ).append("</li >" ) ;                    
                } 
                sb.append("</ul>");
                sb.append("</div>");
            }
        }
        
        sb.append("</div>");        
        sb.append(getTagView());
        sb.append("<div style=\"display:none\" ").append("id=\"").append(getJsId()).append("_text\">").append(getText()).append("</div>"); 
        return sb.toString();
    }
    
    private int getStyleActivate(){
        String s = getViewAttribut();
        String k = "n-activate=";
        if (s.contains(k)) {
           s=s.substring(s.indexOf(k)+k.length())+";";  
           return  Utility.getInt(s);
        }
        return icount;
    }
}