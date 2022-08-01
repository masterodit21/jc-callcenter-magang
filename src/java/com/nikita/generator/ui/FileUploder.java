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
public class FileUploder extends Component{

    @Override
    public boolean isFileComponent() {
        return true; //To change body of generated methods, choose Tools | Templates.
    }
    
    public String getView(NikitaViewV3 v3) {
        v3.setData(this);
        StringBuffer sb = new StringBuffer();

        sb.append(nDivView("nikitaupload"));//sb.append("<div ").append(getStyle()!=null?getStyle().getViewAttr("n-div-", " "):"").append(" style=\"").append(isVisible()?"":"display:none;").append(getViewStylePrefix("n-div-")).append("").append("").append("\"  title=\"").append( escapeHtml(getTooltip()) ).append("\"   class=\"component ").append(getVisibleEnable()).append(getFormJsId()).append(" nikitaupload\" id=\"").append(getJsId()).append("\" nid=\"").append(getId()).append("\"  nform=\"").append(getFormId()).append("\"  ntype=\"nikitaupload\"  >\n");
        sb.append(getTagView());  
        if (!isGone()) {
            sb.append(nTableView());//sb.append("<table><tr>");
                sb.append(nLabelView());
 
                sb.append("<td>");  ;   
                sb.append(" <form  "+(getStyle()!=null?getStyle().getViewAttrPrefix("n-form-"):"")+"  fname=\"\" class=\"nformupload\" id=\"").append(getJsId()).append("_form\" filename=\""+escapeHtml(getText())+"\"  method=\"post\" action=\""+getStyleAction()+"\"  enctype=\""+getStyleEnctype()+"\">");    
                    sb.append(" <input type=\"text\" style=\"display:none\" name=\"fnserver\"  id=\"").append(getJsId()).append("_form_fn\"  />\n" );
                   
                   
                    sb.append("<div style=\"position:relative\">");
                        //ui
                        sb.append("<input id=\"").append(getJsId()).append("_form_text\"  class=\"nikitasmartentry").append(getErrorClass()).append("\" style=\"padding-right:20px;\"  placeholder=\"Choose File\" readonly />"); //disabled=\"disabled\"
                            sb.append("<div style=\"position:absolute;right:4px;top:50%;margin-top:-8px\">");
                                sb.append("<img ").append(isEnable()?"":"disabled").append("  style=\"width:18px;height:18px;\" src=\"static/img/browse.png\"  class=\"textsmart\"  alt=\"Nikita Gebnerator\"  id=\"").append(getJsId()).append("_finder\"     ").append(isEnable()?getClickAction("finder"):"").append("    >\n" );
                            sb.append("</div>");
                        sb.append("<input  class=\"textsmart\"   onchange =\" document.getElementById('"+getJsId()+"_form_text"+"').value =  getfname(this.value);  document.getElementById('"+getJsId()+"_form"+"').setAttribute('filename',getfname(this.value) ); \"    style=\"position:absolute;top:0px;left:0px;opacity:0;").append(getViewStyle()).append("\" type=\"file\" id=\"").append(getJsId()).append("_text\"  name=\"").append(getId()).append("\"  ").append(getClickAction()).append("   ").append(isEnable()?"":"disabled").append("   accept=\""+getAccept()+"\"  />\n");
                        sb.append("<input  style=\"width:80px;").append(  listener!=null ?"display:none":"display:none").append(" \" type=\"submit\" id=\"").append(getJsId()).append("_submit\" value=\"").append("Upload").append("\" ").append(getClickAction()).append("    />\n");

                        //progres
                        sb.append("<div style=\"position:absolute;top:0px;left:0px;\">");
                            sb.append("<div style=\"position:absolute;top:0px;\" ").append("id=\"").append(getJsId()).append("_form_progress\"  class=\"nprogress \"> ");
                                 sb.append(" <div  style=\"display:none\" ").append("id=\"").append(getJsId()).append("_form_bar\"   class=\"nbar \"></div >");
                                 sb.append(" <div  style=\"display:none\"").append("id=\"").append(getJsId()).append("_form_percent\"  class=\"npercent \">0%</div > ");
                            sb.append("</div> ");
                            sb.append("<div style=\"display:none\"   ").append("id=\"").append(getJsId()).append("_form_status\" ></div> ");
                        sb.append("<div> ");
                        
                    sb.append("</div>");                    
                sb.append(" </form>");                 
                sb.append("</td>"); 
                sb.append(getCommentView()); 
            sb.append("</tr></table>");
        }
        sb.append("</div>");
        return sb.toString();
    }
    
    private String getStyleAction(){
        Nset attr = getStyle()!=null ? getStyle().getInternalAttr():Nset.newObject();        
        String action = attr.containsKey("n-action")?attr.getData("n-action").toString():"";
        if (action.equals("") && getForm()!=null) {
            action="/@+BASE/" + (getForm().getId().equals("")? getForm().getName():getForm().getId());
        } 
        action = getBaseUrl(action);      
        return action;
    }
    private String getStyleEnctype(){
        /*
        String s = getViewAttribut();
        String k = "n-enctype=";
        if (s.contains(k)) {
           s=s.substring(s.indexOf(k)+k.length()+1)+"\" ";  
           return   s.substring(0,s.indexOf("\" ")).replace("\"", "").replace("\"", "").trim();
        }
        */
        Nset attr = getStyle()!=null ? getStyle().getInternalAttr():Nset.newObject();        
        String enctype = attr.containsKey("n-enctype")?attr.getData("n-enctype").toString():"";
        if (enctype.equals("")) {
            enctype="multipart/form-data";
        }
        return enctype;
    } 
     private String getAccept(){
        /*
        String s = getViewAttribut();
        String k = "n-accept=";
        if (s.contains(k)) {
           s=s.substring(s.indexOf(k)+k.length()+1)+"\" ";  
           return   s.substring(0,s.indexOf("\" ")).replace("\"", "").replace("\"", "").trim();
        }
        */
        Nset attr = getStyle()!=null ? getStyle().getInternalAttr():Nset.newObject();        
        String accept = attr.containsKey("n-accept")?attr.getData("n-accept").toString():"";
        if (accept.equals("")) {
            accept="*.*";
        }
        return accept;
    } 
}
