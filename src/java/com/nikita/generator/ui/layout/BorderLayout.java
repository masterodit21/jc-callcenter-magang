/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator.ui.layout;

import com.nikita.generator.Component;
import com.nikita.generator.ComponentGroup;
import com.nikita.generator.NikitaViewV3;
import com.nikita.generator.Style;
import java.util.Hashtable;
/**
 * created by 13k.mail@gmail.com
 */
public class BorderLayout extends ComponentGroup{
     
    public String getView(NikitaViewV3 v3) {
        v3.setData(this);
        StringBuffer sbuBuffer = new StringBuffer();
        sbuBuffer.append("<div ").append(getStyle()!=null?getStyle().getViewAttr("n-div-", " "):"").append(" style=\"overflow:hidden; ").append(isVisible()?"":"display:none;").append(getViewStyle()).append("\" class=\"component nikitaborderlayout\"  id=\"").append(getJsId()).append("\" nid=\"").append(getId()).append("\"  nform=\"").append(getFormId()).append("\"  ntype=\"nikitaborderlayout\"     >");
        sbuBuffer.append( getViewChildwithStyle("top","float:none;",v3));   
            sbuBuffer.append("<div style=\"display:table;overflow:hidden;width:100%\"  >");  
            sbuBuffer.append("<div style=\"display:table-row;overflow:hidden;width:100%\"  >");  
                sbuBuffer.append(getViewChildwithStyle("left","display:table-cell;",v3));
                sbuBuffer.append(getViewChildwithStyle("center","display:table-cell;vertical-align:top",v3));     
                sbuBuffer.append(getViewChildwithStyle("right","display:table-cell;",v3));
            sbuBuffer.append("</div>"); 
            sbuBuffer.append("</div>"); 
        sbuBuffer.append( getViewChildwithStyle("bottom","float:none;clear: both;",v3));
        sbuBuffer.append("</div>");
        return sbuBuffer.toString();  
    }
     
    public String getViewB(NikitaViewV3 v3) {
        StringBuffer sbuBuffer = new StringBuffer();
        sbuBuffer.append("<div  data-layout='{type: \"border\" }'  style=\" ").append(getViewStyle()).append("\" class=\"component nikitaborderlayout\" id=\"").append(getId()).append("\"   >");
             
            sbuBuffer.append(getViewChild("center","center",v3));
            sbuBuffer.append( getViewChild("top","north",v3));
            sbuBuffer.append(getViewChild("left","west",v3));
            sbuBuffer.append( getViewChild("right","east",v3));
            sbuBuffer.append(getViewChild("bottom","south",v3));

            sbuBuffer.append("</div>");
        return sbuBuffer.toString();  
    }
    Hashtable<String, Component> content = new Hashtable<String, Component>();
 
    public void setComponentTop(Component component) {
           content.put("top", component);
           addComponent(component);
    }
     
    public void setComponentLeft(Component component) {
           content.put("left", component);
           addComponent(component);
    }
    
    public void setComponentRight(Component component) {
          content.put("right", component);
           addComponent(component);
    }
     
    public void setComponentBottom(Component component) {
          content.put("bottom", component);
           addComponent(component);
    }
    
    public void setComponentCenter(Component component) {
          content.put("center", component);
          addComponent(component);
    }

 
 
    private String getViewChildwithStyle(String border, String style, NikitaViewV3 v3){
        if (content.get(border)!=null) {
            StringBuffer sbuBuffer = new StringBuffer();
            if (border.equals("left")) {
                if (content.get(border).getStyle()==null) {
                    content.get(border).setStyle(new Style());
                }
                content.get(border).getStyle().getInternalObject().getData("style").setData("display", "table-cell") ;
                sbuBuffer.append(content.get(border).getView(v3));
            }else{
                sbuBuffer.append("<div  style=\"").append(style).append("\">");
                sbuBuffer.append(content.get(border).getView(v3));
                sbuBuffer.append("</div>");
            }           
            return sbuBuffer.toString();  
        }
        return "";
    }

    @Override
    public void addComponent(Component component) {
        if (component.getViewStyle().contains("border-layout:center")) {
            setComponentCenter(component);
        }else if (component.getViewStyle().contains("border-layout:top")) {
            setComponentTop(component);
        }else if (component.getViewStyle().contains("border-layout:left")) {
            setComponentLeft(component);
        }else if (component.getViewStyle().contains("border-layout:right")) {
            setComponentRight(component);
        }else if (component.getViewStyle().contains("border-layout:bootom")) {
            setComponentBottom(component);
        }   
        super.addComponent(component);
    }
    
    private String getViewChild(String border, String bordercls, NikitaViewV3 v3){
        if (content.get(border)!=null) {
            StringBuffer sbuBuffer = new StringBuffer();
            sbuBuffer.append("<div class=\"").append(bordercls).append("\">");
            sbuBuffer.append(content.get(border).getView(v3));
            sbuBuffer.append("</div>");
            return sbuBuffer.toString();  
        }
        return "";
    }
}
