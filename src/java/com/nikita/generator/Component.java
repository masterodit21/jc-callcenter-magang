/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nikita.generator;

import com.nikita.generator.ui.Tablegrid;
import com.rkrzmail.nikita.data.Nset;
import java.net.URLEncoder;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrBuilder;

/**
 * created by 13k.mail@gmail.com
 */
public class Component {
    public static final String ENTER = "\r\n";
    public static final String DOUBLEQUOTE = "\"";
    public static final String AMPERSAND = "&";
    public static final String SPACE = " ";
    public static final String SEMICOLON = ";";
    public static final String COMPONENTIDNONUI = "qwertyuiopasdfghjklzxcvbnm1234567890nikita";
   
    //public Component(){
     //   setVisible(true);//default
    //    setEnable(true);//default
    //}
    
    //new data
    public boolean isFileComponent(){
		return false;
	}
    public interface OnActionListener {
        public boolean OnAction(NikitaControler form, Component component, String action);
    }      
    private OnActionListener actionListener;
    public void setOnActionListener(OnActionListener actionListener){
        this.actionListener=actionListener;
    }
    public OnActionListener getOnActionListener(){
        return this.actionListener;
    } 
    
    public void OnAction(){
        
    }
    
    public interface OnClickListener {
        public void OnClick(NikitaRequest request, NikitaResponse response, Component component);
    } 
    public interface OnViewListener {
        public NikitaResponse beforeView(Component component);
    }  
    
    protected OnClickListener listener;
    private String label="";
    private String text="";
    boolean visible=true;
    private boolean enable=true;
    private boolean mandatory=false;
    private String name="";
    private String type="";
    private String id="";
    private String tag="";
    private String formid="";
    private String src="";
    private Nset data=Nset.newNull();
    private Style style;
        
    private String tooltip="";
    private String hint="";
 
    
    public String getTagView(){
        StringBuilder sb = new StringBuilder();
        sb.append("<div style=\"display:none\" ").append("id=\"").append(getJsId()).append("_tag\">").append(escapeHtml(getTag())).append("</div>");     
        return sb.toString();
    }
    
    public boolean isGone() {
        return false;
    }
    
    
    public void setHint(String hint) {
        this.hint = hint;
    }
    
    public String getHint() {
        return hint;
    }
     public String getComment() {
        return comment;
    }
 private String comment="";
    public void setComment(String text) {
        this.comment = text;        
    }
    public void setType(String type) {
        this.type = type;
    }
    
    public String getType() {
        return type;
    }
    
    public void setTooltip(String tip) {
        this.tooltip = tip;
    }
    
    public String getTooltip() {
        return tooltip;
    }
    
    public String getViewAttributPrefix(String prefix){        
        return style!=null?style.getViewAttrPrefix(prefix):"";
    }
    public String getViewAttributPrefixHide(String...prefix){        
        return style!=null?style.getViewAttrPrefixHide(prefix):"";
    }
    public String getViewStylePrefix(String prefix){       
        return style!=null?style.getViewStylePrefix(prefix):"";
    }
    public String getViewStylePrefixHide(String...prefix){       
        return style!=null?style.getViewStylePrefixHide(prefix):"";
    }
    
    public void setStyle(Style style){
        this.style=style;
    }
    public Style getStyle(){
        return this.style;
    }
    public Style getAutoInstanceStyle(){
        return this.style!=null?this.style:new Style();
    }
    public String getViewStyle(){        
        return style!=null?style.getViewStyle():"";
    }
    public String getViewClass(){        
        return style!=null?style.getViewClass():"";
    }
    public String getViewAttribut(){        
        return style!=null?style.getViewAttr():"";
    }
    public void setOnClickListener(OnClickListener listener){
        this.listener=listener;
    }
    
    protected OnClickListener getOnClickListener(){
        return listener;
    }
    public String getClickActionString(String action){
        return getClickAction(action); 
    }
    protected String getClickAction(){
        return getClickAction("click");
    }
    protected String getClickAction(String action){
        StringBuilder sb = new StringBuilder();
        if (listener!=null) {           
            sb.append(" onclick=\"sendaction('").append(getFormJsId()).append("','").append(getJsId()).append("','").append(action).append("')\" ");          
        }  
        if (getViewStyle().contains("n-ondblclick:true")) {
            //if (NikitaService.BASE_PATH_INFO.contains("runonmobile")||NikitaService.BASE_BROWSER.equals("MOBILE")) {
            //    return  sb.toString();//not support dblclick
            //} 
            return sb.toString().replaceFirst("onclick=", "ondblclick=");
        }
        return sb.toString();
    }
    public static String appendable(String...str){
        StringBuilder sb = new StringBuilder();
        if (str!=null) {
            for (int i = 0; i < str.length; i++) {
                sb.append(str[i]);
            }
        }                
        return sb.toString();
    }
    public static String escapeHtml(String s){
        return StringEscapeUtils.escapeHtml(s);
    }
    public static String escapeSql(String s){
        return StringEscapeUtils.escapeSql(s);
    }
    public static String unescapeHtml(String s){
        return StringEscapeUtils.unescapeHtml(s);
    }
    public static String urlEncode(String sUrl) {
        StringBuilder urlOK = new StringBuilder();
        for (int i = 0; i < sUrl.length(); i++) {
            char ch = sUrl.charAt(i);
            switch (ch) {
            case '<':
                    urlOK.append("%3C");
                    break;
            case '>':
                    urlOK.append("%3E");
                    break;
            case '/':
                    urlOK.append("%2F");
                    break;
            case ' ':
                    urlOK.append("%20");
                    break;
            case ':':
                    urlOK.append("%3A");
                    break;
            case '-':
                    urlOK.append("%2D");
                    break;
            default:
                    urlOK.append(ch);
                    break;
            }
        }
        return urlOK.toString();
    }

    StringBuffer buffered = new StringBuffer();
 

    private Component parent;
    protected void setParent(Component parent){
         this.parent=parent;
    }
    public Component getParent(){
         return this.parent;
    }
   
    private String validation="";
    protected void setValidation(String validation){
         this.validation=validation;
    }
    public String getValidation(){
         return this.validation;
    }
    
    private String parentname="";
    protected void setParentName(String parent){
         this.parentname=parent;
    }
    public String getParentName(){
         return this.parentname;
    }
    public void clearParentName(){
        this.parentname="";
    }
    
    public void restoreData(Nset data){
        if (!id.equals("")) {
            if (data.getData(id).getData("u").toString().equals("v3")) {//v3  
                jsid    = data.getData(id).getData("i").toString();
                visible = data.getData(id).getData("v").toString().equals("1")?true:false;
                enable  = data.getData(id).getData("e").toString().equals("1")?true:false;
                text    = data.getData(id).getData("t").toString();
                tag     = data.getData(id).getData("n").toString(); 
                
                comment     = data.getData(id).getData("c").toString();                 
                mandatory  = data.getData(id).getData("m").toString().equals("1")?true:false;
            }else{
                text    = unescapeHtml(data.getData(id).getData("t").toString()) ;
                visible = data.getData(id).getData("v").toString().equals("1")?true:false;
                enable  = data.getData(id).getData("e").toString().equals("1")?true:false;
                tag     = unescapeHtml(data.getData(id).getData("n").toString()); 
                jsid    = unescapeHtml(data.getData(id).getData("i").toString()); 
                
                comment     = unescapeHtml(data.getData(id).getData("c").toString());                
                mandatory  = data.getData(id).getData("m").toString().equals("1")?true:false;
            }            
        }        
    }        
    //=====================================================================//
    private Component form ;
    private String jsid = "";
    private String newinstance = "";
    public void setForm(Component form){
        this.form=form;
    }
    public Component getForm(){
        return this.form;
    }
    public String getFormJsId() {
        formid=form!=null ?form.getId():"";
        newinstance=form!=null ?form.newinstance:"";
        return formid+"-"+newinstance;//eq = form.getJsId()
    }
    public String getJsId() {
        if (jsid.equals("")) {
            return getFormJsId()+"-"+id.replace("[", "-").replace("]", "-");
        }
        return jsid;
    }
    public void setInstanceId(String newinstance) {
        this.newinstance=newinstance;
    }
    public String getInstanceId() {
        return this.newinstance;
    }
    //=====================================================================//
    
    public String getView(){   
        return "";
    } 
    
    public String getView(NikitaViewV3 v3){
        return getView();
    }     

    public String getFormId() {
        return formid;
    }
    
    private void setFormId(String formid) {
        this.formid = formid;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getId() {
        if (id.contains("[")&& id.contains("]")) {
            return id.substring(0,id.indexOf("["));//reservet generator
        }        
        return id;
    }
    
    
    
    public void setTag(String tag) {
        this.tag = tag;
    }
    
    public String getTag() {
        return this.tag;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
    public Nset getData() {
        return data;
    }

    public void setData(Nset data) {
        this.data = data;
    }
     

    public String getLabel() {
        return label;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }
    
    public boolean isVisible() {
        return visible;
    }
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
   

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
    public boolean isMandatory() {
        return mandatory;
    }
    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }
    protected String getMandatoryLabel() {
        if (mandatory) {
            return " <font id =\""+getJsId()+"_label_mand\" style=\"display:block\" color=\"red\">*</font> ";
        }else{
            return " <font id =\""+getJsId()+"_label_mand\" style=\"display:none\" color=\"red\">*</font>";
        } 
    }
            
    protected String getVisibleEnable() {
        StringBuffer sb = new StringBuffer();
        if (!enable) {
            sb.append(" ndisable "); 
        }
        if (!visible) {
            sb.append(" nhidden "); 
        }        
        return sb.toString();
    }
    public String getErrorClass(){
        return (getStyle()!=null && getStyle().getInternalStyle().getData("n-error").toString().equalsIgnoreCase("true"))?" n-error ":"";
    }
    protected OnViewListener viewListener;
    public void setOnViewListener(OnViewListener listener){
        this.viewListener=listener;
    }
    
    protected OnViewListener getOnViewListener(){
        return viewListener;
    }
    
    public static String getBaseUrl(String urlinput){
         
        if (urlinput.startsWith("http")) {
            return urlinput;
        }else if (urlinput.startsWith("/res/asset")) {
            return NikitaService.getBaseContext() +urlinput;
        }else if (urlinput.startsWith("/res/resource")) {
            return NikitaService.getBaseContext() +urlinput;
        }else if (urlinput.startsWith("/res/document")) {
            return NikitaService.getBaseContext() +urlinput;
        }else if (urlinput.startsWith("/static")) {
            return NikitaService.getBaseContext() + urlinput;
        }else if (urlinput.startsWith("img/")) {
            return NikitaService.getBaseContext() +"/static/"+ urlinput; 
        }else if (urlinput.startsWith("/@+CONTEXT/")) {
            return NikitaService.getBaseContext() + urlinput.substring(10);
        }else if (urlinput.startsWith("@+CONTEXT/")) {
            return NikitaService.getBaseContext() + urlinput.substring(9);
        }else if (urlinput.startsWith("/@+BASEURL/")) {
            return NikitaService.getBaseUrl() + urlinput.substring(10);            
        }else if (urlinput.startsWith("/@+BASE/")) {
            return NikitaService.getBaseUrl()  + urlinput.substring(7);
        }else if (urlinput.startsWith("@+BASEURL/")) {
            return NikitaService.getBaseUrl()  + urlinput.substring(9);    
        }else if (urlinput.startsWith("@+BASE/")) {
            return NikitaService.getBaseUrl()  + urlinput.substring(6);
        }else if (urlinput.toLowerCase().startsWith("/context/")) {
            return NikitaService.getBaseContext() + urlinput.substring(8);            
        }else if (urlinput.toLowerCase().startsWith("/base/")) {
            return NikitaService.getBaseUrl()  + urlinput.substring(5);        
        }else{
            return urlinput;
        }
    }
    
    protected String nDivView(String comtype){
        return nDivView(comtype, ""); 
    }
    protected String nDivView(String comtype, String attribut){
        return nDivView(comtype, attribut, ""); 
    }  
    protected String nDivView(String comtype, String attribut, String style){
        return nDivView(comtype, attribut, style, ""); 
    }
    
    protected String nDivView(String comtype, String attribut, String style, String cls){
        StringBuilder sb = new StringBuilder();   
        sb.append("<div title=").append(DOUBLEQUOTE).append(escapeHtml(getTooltip())).append(DOUBLEQUOTE).append(attribut).append(" ").append(getViewAttributPrefix("n-div-")).append("  style=\"").append(style).append(isVisible()?"":"display:none;").append(getViewStylePrefix("n-div-")).append("").append("\" class=\"component ").append(cls).append(" ").append(getVisibleEnable()).append(getFormJsId()).append(" ").append(comtype).append("\" nfid=\"").append(getFormJsId()).append("\" id=\"").append(getJsId()).append("\" nid=\"").append(getId()).append("\" ncname=\"").append(Component.escapeHtml(getName())).append("\"  nform=\"").append(getFormId()).append("\"  ntypecomp=\"").append(getType()).append("\"   ntype=\"").append(comtype).append("\"  >");//
        return sb.toString();
    }
    protected String nTableView(){
        return appendable("<table style=",DOUBLEQUOTE, getViewStylePrefix("n-table-"), DOUBLEQUOTE, " " , getViewAttributPrefix("n-table-") ,"><tr>");
    }
    protected String nLabelView(){
        StringBuilder sb = new StringBuilder();
        if (!getLabel().equals("")) {
            sb.append("<td class=\"nikitacomponentlabel\" ").append(getViewAttributPrefix("n-label-")).append("  style=\"").append(getViewStylePrefix("n-label-")).append("\">");
            sb.append(getLabelView());
            sb.append("</td>") ;   
        }
        return sb.toString(); 
    }
    protected String getLabelView(){
        StringBuilder sb = new StringBuilder();
        if (!getLabel().equals("")) {
            sb.append("<label>").append(Component.escapeHtml(getLabel()) +getMandatoryLabel()).append("</label>\n");
        }
         
        return sb.toString(); 
    }
    protected String getCommentView(){
        StringBuilder sb = new StringBuilder();
        
        if (getStyle()!=null && getStyle().getInternalStyle().containsKey("n-comment") && getStyle().getInternalStyle().containsValue("true")) {   
            sb.append("<td>");
            sb.append("<input type=\"text\"   placeholder=\"").append( escapeHtml( "comment" ) ).append("\"  class=\"nikitasmartentry n-comment \" style=\" ").append("").append("\" id=\"").append(getJsId()).append("_comment\" value=\"").append(  escapeHtml(getComment()) ).append("\"  ").append("").append(" />\n" );
            sb.append("</td>")   ;       
        }
        return sb.toString(); 
    }
}
