/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator.ui;

import com.nikita.generator.Component;
import com.nikita.generator.ComponentGroup;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaViewV3;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import java.util.Hashtable;
import java.util.Vector;
/**
 * created by 13k.mail@gmail.com
 */
public class Content extends Component{
    
    /*
    public String getView(NikitaViewV3 v3) {
        v3.setData(this);
        StringBuilder sb = new StringBuilder();
        sb.append(nDivView("nikitacheckbox"));//sb.append("<div ").append(getStyle()!=null?getStyle().getViewAttr("n-div-", " "):"").append(" style=\"").append(isVisible()?"":"display:none;").append(getViewStylePrefix("n-div-")).append("").append("").append("\" class=\"component ").append(getVisibleEnable()).append(getFormJsId()).append(" ncontentmenu\"  id=\"").append(getJsId()).append("\" nid=\"").append(getId()).append("\"  nform=\"").append(getFormId()).append("\"  ntype=\"nikitacheckbox\"   >");
        sb.append(getTagView());//sb.append("<div style=\"display:none\" ").append("id=\"").append(getJsId()).append("_tag\">").append(getTag()).append("</div>"); 
        if (!isGone()) {
            sb.append(nTableView());//sb.append("<table><tr>");
                sb.append(nLabelView()); 
                sb.append("</tr><tr>");   
                sb.append("<td>");                  
                    sb.append("<div ").append(isEnable()?"":"disabled").append(" >");                    
                    if (getData().isNsetArray()) { 
                        sb.append(rekursi(getData(), v3));
                    }
                    sb.append("</div>");
                sb.append("</td>");   
            sb.append("</tr></table>");               
        }
        sb.append("</div>");
        return sb.toString();
    }
    private String rekursi(Nset d, NikitaViewV3 v3){
        StringBuilder bchString = new StringBuilder();
        for (int i = 0; i < d.getArraySize(); i++) {                    
            if (d.getData(i).isNset()) {
                Component comp = new Checkbox(){
                    protected String getClickAction(String action) {
                        return Content.this.getClickAction("click-"+getId()); 
                    }                            
                };
                comp.setId(d.getData(i).getData("i").toString());
                comp.setLabel(d.getData(i).getData("l").toString());
                comp.setName(d.getData(i).getData("n").toString());
                comp.setVisible(isTrue(d.getData(i).getData("v").toString()));
                comp.setEnable(isTrue(d.getData(i).getData("e").toString()));
                comp.setText(d.getData(i).getData("t").toString());
                comp.setData(d.getData(i).getData("d"));
                bchString.append(comp.getView(v3));
                
                if (d.getData(i).getData("c").isNsetArray()) {
                    bchString.append("<div style=\"margin-left:40px\" >");
                        bchString.append( rekursi(d.getData(i).getData("c"), v3) ); 
                    bchString.append("</div>");
                }                
            }
        }
        return bchString.toString();
    }
    private boolean isTrue(String s){
        if (s.equals("")|| s.equals("1")||s.equals("true")) {
            return true;
        }
        return  false;
    }
    */
    
    public String getView(NikitaViewV3 v3) {
        v3.setData(this);
        StringBuilder sb = new StringBuilder();
        sb.append(nDivView("nikitacontentaccess"));// sb.append("<div ").append(getViewAttributPrefix("n-div-")).append(" style=\"").append(isVisible()?"":"display:none;").append(getViewStylePrefix("n-div-")).append("").append("\" class=\"component  nTree ").append(getVisibleEnable()).append(getFormJsId()).append(" nikitacollapsiblelist\"  id=\"").append(getJsId()).append("\" nid=\"").append(getId()).append("\"  nform=\"").append(getFormId()).append("\"  ntype=\"nikitacollapsiblelist\"   >\n");
        sb.append(getTagView()); 
        sb.append(getLabelView()); 
        
        Nset n = Nset.newObject();
        /*
        if (getComponentCount()>=1) {
            n = setDataFromComponent(this);
            sb.append("<div style=\"display:none\"  >").append(super.getView(v3)).append("</div>");     
        }             
        */        
        if (!isGone()) {
            sb.append("<ul class=\"contentaccess ncontentaccess\">");    
            
            if (n.getSize()>=1) {
                sb.append(getLoop(n));    
            }
            if (getData().getSize()>=1) {
                sb.append(getLoop(getData()));   
            }
            sb.append("</ul>");   
        }       
        
        sb.append("</div>"); 
        return sb.toString();
    }
  
         
    private Nset setDataFromComponent(ComponentGroup data ) {
        Nset n = Nset.newArray(); 
        if (getData()!=null && getData().isNsetArray()) {
            n = getData();
        }
        for (int i = 0; i < data.getComponentCount(); i++) {
            prepareComponent(n, data.getComponent(i) );
        }        
        return  n;
    }
    
    private void prepareComponent(Nset cmp, Component component ){
        Nset n = Nset.newObject();  
        
        if (component instanceof ComponentGroup && component.isVisible()) {
            Nset child = Nset.newArray();
            for (int i = 0; i < ((ComponentGroup)component).getComponentCount(); i++) {
                prepareComponent(child, ((ComponentGroup)component).getComponent(i));
            } 
               
            n.setData("text",component.getText() +" ");
            n.setData("id", component.getId());  
            n.setData("child", child);
            
            cmp.addData(n);
        }else if (component.isVisible()) {             
            n.setData("text",component.getText() +" ");
            n.setData("id", component.getId());  
            if (component.isEnable()) {
                n.setData("action", component.getClickActionString("click"));  
            }else{
                n.setData("style", "style=\"font-style:italic;opacity: 0.5;\"");  
            }
            cmp.addData(n);
        }
        
    }
    
    private String getClickID(Nset n){
        if (n.containsKey("id")) {
            return n.getData("id").toString();
        }else if (n.containsKey("text")) {
            String s = n.getData("text").toString();
            if (s.length()>=1) {
                return s.replaceAll("[^A-Za-z0-9_.]+", "");
            }
        }                
        return "";
    }
    
    private Nset nikitasetToNset(Nset nflat){
         Nset nfroot = Nset.newArray();
            Nset buffer = Nset.newObject();
            //flat to tree[0] ==  all (id,text,parentid,additionalinfo)
            for (int i = 0; i < nflat.getSize(); i++) {   
                String id = nflat.getData(i).getData(0).toString().trim();
                id = id.equalsIgnoreCase("")?nflat.getData(i).getData(3).toString().trim():id;
                Nset n = Nset.newObject();
                n.setData("id", id);
                n.setData("text",  nflat.getData(i).getData(1).toString().trim());
                n.setData("child", Nset.newArray());
                buffer.setData(id, n);
            }

            //flat to tree[1] == id,text,parentid,additionalinfo
            for (int i = 0; i < nflat.getSize(); i++) {
                String id =  nflat.getData(i).getData(0).toString().trim();
                id = id.equalsIgnoreCase("")?nflat.getData(i).getData(3).toString().trim():id;
                String parent = nflat.getData(i).getData(2).toString().trim();
                                
                if (parent.equalsIgnoreCase( nflat.getData(i).getData(0).toString().trim() )) {
                     //none
                    //buffer.getData(id).removeByKey("child");
                }else if (!parent.equals("")) {
                    if ( buffer.getData(parent).getData("child").isNsetArray()) {
                        buffer.getData(parent).getData("child").addData(buffer.getData(id));
                    }else{
                        //none
                        //buffer.getData(id).removeByKey("child");
                    }
                }else{
                    //none
                    //buffer.getData(id).removeByKey("child");
                }
            }    
            
            //flat to tree[2] == gettroot
            for (int i = 0; i < nflat.getSize(); i++) {
                String id =  nflat.getData(i).getData(0).toString().trim();
                id = id.equalsIgnoreCase("")?nflat.getData(i).getData(3).toString().trim():id;
                String parent = nflat.getData(i).getData(2).toString().trim();
                if (parent.equals("")) {                    
                    nfroot.addData(buffer.getData(id));
                }                
            } 
            
            return nfroot;
    }
    
    private String getLoop(Nset n){
        if (Nikitaset.isNikitaset(n)) {
            n =nikitasetToNset(n.getData("data"));
        }   
        
        //id,text,child 
        StringBuilder sb = new StringBuilder();
        
        if (n.getInternalObject() instanceof Vector) {
                for (int i = 0; i < n.getArraySize(); i++) {
                     sb.append(getLoop(n.getData(i)));
                }
        }else if (n.getInternalObject() instanceof Hashtable) {
            sb.append("<li   >");         // class="isFolder">   
            //sb.append(escapeHtml(n.getData("text").toString())); 
            if (n.getData("child").getArraySize()>=1 || n.getData("child").getObjectKeys().length >=1 ) {
                sb.append("<b >").append(escapeHtml(n.getData("text").toString())).append("</b>");
                sb.append("<ul>");
                sb.append(getLoop(n.getData("child"))); 
                sb.append("</ul>");
            }else{
                String id = n.containsKey("id") ? n.getData("id").toString(): n.getData("text").toString();
                if (n.getData("action").toString().contains("sendaction")) {
                    sb.append("<input type=\"checkbox\" name=\"").append(getJsId()).append("_text\" value=\"").append( escapeHtml(id) ).append("\" class=\"onaction\" ").append( n.getData("style").toString() ).append(" ").append( n.getData("action").toString() ).append(" >").append(escapeHtml(n.getData("text").toString())).append("</input>");
                }else{
                    sb.append("<input type=\"checkbox\" name=\"").append(getJsId()).append("_text\" value=\"").append( escapeHtml(id) ).append("\" class=\"onaction\" ").append( n.getData("style").toString() ).append(" ").append(getClickAction("click-"+id)).append(" >").append(escapeHtml(n.getData("text").toString())).append("</input>");
                }                   
            }
            sb.append("</li>");            
        }else{
            sb.append("<li>");
            sb.append(escapeHtml(n.toString())); 
            sb.append("</li>");
        }
         
         return sb.toString();
    }
}
