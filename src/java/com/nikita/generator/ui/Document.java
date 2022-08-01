/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator.ui;

import com.nikita.generator.Component;
import static com.nikita.generator.Component.escapeHtml;
import static com.nikita.generator.Component.getBaseUrl;
import com.nikita.generator.NikitaService;
import com.nikita.generator.NikitaViewV3;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.mobile.Generator;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import net.coobird.thumbnailator.Thumbnails;

/**
 *
 * @author rkrzmail
 */
public class Document extends Component{

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
 
                sb.append("<td style=\"width:320px").append(getStyle()!=null?getStyle().getViewStyle("n-table-td1-", " "):"").append("\" >");   
                sb.append("<div style=\"display:none\" ").append("id=\"").append(getJsId()).append("_img\">").append(escapeHtml(getText())).append("</div>"); 
                if (isPDF(getText())) {
                    sb.append("<a href=\""+getDocumentFile(getText())+"\" target=\"_blank\"");
                    sb.append("<img src=\"").append( getDocumentPDFimage(getText())).append("\"  ").append(getViewAttribut()).append("   style=\"").append(getViewStyle()).append("\"   alt=\"\"  id=\"").append(getJsId()).append("_image\"   class=\"ndocumentzoom\"  ").append("").append("    >\n" );
                    sb.append("</a");
                }else{
                    sb.append("<img src=\"").append( getDocumentFile(getText())).append("\"  ").append(getViewAttribut()).append("   style=\"").append(getViewStyle()).append("\"   alt=\"\"  id=\"").append(getJsId()).append("_image\"   class=\"ndocumentzoom\"  ").append("").append("    >\n" );
                    if (showRotate()) {
                        sb.append("<div style\"float:left\">");
                        sb.append("<img src=\"static/img/rotateleft.png\"  ").append(getClickAction("documentrotateleft")).append("   style=\" width:48px;height:48px;right:10px\"   alt=\"\"  id=\"").append(getJsId()).append("_image\"   class=\"\"  ").append("").append("    >\n" );
                        sb.append("<img src=\"static/img/rotateright.png\" ").append(getClickAction("documentrotateright")).append("   style=\" width:48px;height:48px;right:10px\"   alt=\"\"  id=\"").append(getJsId()).append("_image\"   class=\"\"  ").append("").append("    >\n" );
                        sb.append("</div>");
                    }
                           
                     
                }                 
                sb.append("</td></tr><tr>"); 
                
                
                sb.append("<td style=\"").append(getStyle()!=null?getStyle().getViewStyle("n-table-td2-", " "):"").append("\">");  ;   
                sb.append(" <form "+(ifNoUpload()?" style=\"display:none\" ":"")+" "+(getStyle()!=null?getStyle().getViewStylePrefix("n-form-"):"")+"  fname=\"\" class=\"nformupload\" id=\"").append(getJsId()).append("_form\" filename=\""+escapeHtml(getText())+"\"  method=\"post\" action=\""+getStyleAction()+"\"  enctype=\""+getStyleEnctype()+"\">");    
                    sb.append(" <input type=\"text\" style=\"display:none\" name=\"fnserver\"  id=\"").append(getJsId()).append("_form_fn\"  />\n" );
                   
                   
                    sb.append("<div style=\"position:relative\">");
                        //ui                            
                        sb.append("<input id=\"").append(getJsId()).append("_form_text\"  class=\"nikitasmartentry").append(getErrorClass()).append("\" style=\"padding-right:20px;\"  placeholder=\"Choose Document\" readonly />"); //disabled=\"disabled\"
                        //sb.append("<input  class=\"textsmart\"   onchange =\" document.getElementById('"+getJsId()+"_form_text"+"').value =  getfname(this.value);  document.getElementById('"+getJsId()+"_form"+"').setAttribute('filename', getfname(this.value) ); documnetclientchange(this.value, '"+getJsId()+"'); \"    style=\"position:absolute;top:0px;left:0px;opacity:0; ").append(getViewStyle()).append("\" type=\"file\" id=\"").append(getJsId()).append("_text\"  name=\"").append(getId()).append("\"  ").append(getClickAction()).append("   ").append(isEnable()?"":"disabled").append("   accept=\""+getAccept()+"\"  />\n");
                        sb.append("<input  class=\"textsmart\"   onchange =\" document.getElementById('"+getJsId()+"_form_text"+"').value =  getfname(this.value);  document.getElementById('"+getJsId()+"_form"+"').setAttribute('filename', getfname(this.value) ); documnetclientchange(this.value, '"+getJsId()+"'); \"    style=\"position:absolute;top:0px;left:0px;opacity:0; ").append(getViewStylePrefix("n-document-")).append("\" type=\"file\" id=\"").append(getJsId()).append("_text\"  name=\"").append(getId()).append("\"  ").append("").append("   ").append(isEnable()?"":"disabled").append("   accept=\""+getAccept()+"\"  />\n");
                        sb.append("<div style=\"position:absolute;right:4px;top:50%;margin-top:-8px\">");
                            sb.append("<img ").append(isEnable()?"":"disabled").append("  style=\"width:18px;height:18px;\" src=\"static/img/browse.png\"  class=\"textsmart\"  alt=\"Nikita Gebnerator\"  id=\"").append(getJsId()).append("_finder\"     ").append(isEnable()?getClickAction("finder"):" disabled ").append("    >\n" );
                            sb.append("<img ").append(isEnable()?"":"disabled").append("  style=\"width:18px;height:18px;display:none\" src=\"static/img/upload.png\"  class=\"textsmart\"  alt=\"Nikita Gebnerator\"  id=\"").append(getJsId()).append("_upload\"     ").append(isEnable()?getClickAction("upload"):" disabled ").append("    >\n" );
                        sb.append("</div>");                        
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
    private boolean showRotate(){
        if (NikitaConnection.getDefaultPropertySetting().getData("init").getData("documentui").toString().contains("showrotate")) {
            return true;
        }
        return getStyle()!=null?getStyle().getViewStyle().contains("n-show-rotate:true"):false;
    } 
    
    private boolean isPDF(String s){
        return s.toLowerCase().endsWith(".pdf");
    } 
    private boolean ifNoUpload(){
        return getStyle()!=null?getStyle().getViewStyle().contains("n-noupload:true"):false;
    } 
    private String getDocumentPDFimage(String s){
        return getBaseUrl("/static/img/pdf.png");
    }
    private String getDocumentFile(String s){            
        if (s.contains("/")) {            
        }else{
            return getBaseUrl("/res/document/"+s);
        }
        return getBaseUrl(s);
    }
    public void rotate(String action){
        if (action.equals("documentrotateleft")||action.equals("documentrotateright")) {
            //Thumbnails.of(is).size(128, 128).toOutputStream(response.getOutputStream());
            if (getText().startsWith("http://")) {
                int id = getText().indexOf("/", 9);
                int ib = getBaseUrl("/base/").indexOf("/", 9);
                try {
                    if (getText().substring(0,id).equalsIgnoreCase(getBaseUrl("/base/").substring(0, ib))) {
                        String name = getText();
                        if (getText().contains("/res/document/")) {
                            name = getText().substring(  getText().indexOf("/res/document/")+"/res/document/".length() );
                             if (name.contains("/")) {
                                name = name.substring(0,   name.indexOf("/") );
                            }
                            if (name.contains("?")) {
                                name = name.substring(0,   name.indexOf("?") );
                            }


                            String path = NikitaConnection.getDefaultPropertySetting().getData("init").getData("document").toString()+ NikitaService.getFileSeparator() +  name;  
                            rotate(action, path, name);      
                        }                       
                    }
                } catch (Exception e) {  }                 
            }else{
                String path = NikitaConnection.getDefaultPropertySetting().getData("init").getData("document").toString()+ NikitaService.getFileSeparator() +  getText();  
                rotate(action, path, getText());                
            }
        }
    }
    public void rotate(String action, String fullfname, String fname){
        String fullfnameNew = fullfname;
        if (fullfnameNew.endsWith("-a.png")) {
            fullfnameNew = fullfnameNew.substring(0,fullfnameNew.length()-6);
            if (fullfnameNew.contains("-")) {
                int l = fullfnameNew.lastIndexOf("-");
                fullfnameNew = fullfnameNew.substring(0, l);
            }
            
        }
        fullfnameNew = fullfnameNew + "-" +System.currentTimeMillis() + "-a.png";        
        
        //String fullfnameNew = fullfname.endsWith("-a.png")?fullfname.substring(0,fullfname.length()-6):(fullfname+"-a.png");
        fname = fullfnameNew;
        if (fname.contains("/")) {
            fname = fname.substring(fname.lastIndexOf("/")+1 );
        }
        if (fname.contains("\\")) {
            fname = fname.substring(fname.lastIndexOf("\\")+1 );
        }
        try {
            int r = action.equals("documentrotateleft")?-90:90;
            InputStream is = new FileInputStream(fullfname);
            OutputStream os = new FileOutputStream(fullfnameNew);
                Thumbnails.of(is).scale(1).rotate(r).toOutputStream(os);
            is.close();
            os.close();
            
            //new File(fullfname).delete();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
        
        setText(fname);        
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
