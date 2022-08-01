package com.nikita.generator.ui;

import com.nikita.generator.Component;
import com.nikita.generator.NikitaViewV3;
import com.nikita.generator.Style;

public class Textbox extends Component{
    
    protected boolean isTextsmart(){
        return false;
    }
    
    @Override
    public String getView(NikitaViewV3 v3) {
        v3.setData(this);
        StringBuilder sb = new StringBuilder();
        sb.append(nDivView("nikitatext"));//sb.append("<div ").append(getStyle()!=null?getStyle().getViewAttr("n-div-", " "):"").append(" style=\"").append(isVisible()?"":"display:none;").append(getViewStylePrefix("n-div-")).append("").append("").append("\"  title=\"").append( escapeHtml(getTooltip()) ).append("\"   class=\"component ").append(getVisibleEnable()).append(getFormJsId()).append(" nikitatext\" id=\"").append(getJsId()).append("\" nid=\"").append(getId()).append("\"  nform=\"").append(getFormId()).append("\"  ntype=\"nikitatext\"   >\n");
            sb.append(getTagView()); 
            if (!isGone()) {
                sb.append(nTableView());
                    sb.append(nLabelView());
                    sb.append("<td style=\"").append(getStyle()!=null?getStyle().getViewStyle("n-table-td-", " "):"").append("\">");                  
                        sb.append("<div style=\"position:relative\">");
                        
                    //onkeydown="maskKeyCode(event,'')" //escapeHtml( getHint() ) 
                      
                    String ncharaccept =  getStyle().getInternalStyle().getData("n-char-accept").toString().trim();
                    String ncharucase =  getStyle().getInternalStyle().getData("n-char-ucase").toString().trim();
                    String ncharlcase =  getStyle().getInternalStyle().getData("n-char-lcase").toString().trim();
                    String neventchange =  getStyle().getInternalStyle().getData("n-event-change").toString().trim();
                    neventchange = neventchange.equals("true")?" onchange=\"sendaction('"+getFormJsId()+"','"+getJsId()+"','change')\" ":"";
                        boolean bfinder = (isTextsmart() && (listener!=null || (getStyle()!=null && (getStyle().getViewStyle().contains("n-finder:true")||getStyle().getViewStyle().contains("n-barcode:true"))  )) );
                        //sb.append("<input  ").append(isTypeTime()?"onkeyup=\"gettimekey(this)\"":"").append(" ").append(getStyle().getInternalStyle().getData("n-currency-format").toString().equalsIgnoreCase("true")?"onkeyup=\"currencykeyup(this)\"":"").append("  type=\""+(isPassword()?"password":"text")+"\" ").append(neventchange).append(ncharaccept.length()>=1||isTypeTime() ?" onkeypress=\"return  "+(isTypeTime()?"gettimemask":"maskKeyCode")+"(event,'"+escapeHtml( ncharaccept ) +"')\" ":"").append(isEnable()?"":"disabled").append("   ").append(  listener!=null ?" onkeydown=\"sendenter(event,'":"onkeydown=\"sendenter(event,'").append(getFormJsId()).append("','").append(getJsId()).append("','").append("enter").append("')\" ").append(isEditable()?"":"readonly").append(" placeholder=\"").append( escapeHtml( getHint() ) ).append("\"  class=\"nikitasmartentry1 ").append(bfinder?"nikitasmartentry":"nikitaentry").append(ncharlcase.equals("true")?" n-char-lcase ":" ").append(ncharucase.equals("true")?" n-char-ucase ":" ").append(getViewClass()).append(getErrorClass()).append("\" style=\"").append(bfinder?"  ":"").append(getViewStyle()).append("\" id=\"").append(getJsId()).append("_text\" value=\"").append(  escapeHtml(getText()) ).append("\"  ").append(getViewAttribut()).append(" />\n" );
                        sb.append("<input  ").append(isTypeTime()?"onkeyup=\"gettimekey(this)\"":"").append(" ").append(getStyle().getInternalStyle().getData("n-currency-format").toString().equalsIgnoreCase("true")?"onkeyup=\"currencykeyup(this)\"":"").append("  type=\""+(isPassword()?"password":"text")+"\" ").append(neventchange).append(ncharaccept.length()>=1||isTypeTime() ?" onkeypress=\"return  "+(isTypeTime()?"gettimemask":"maskKeyCode")+"(event,'"+escapeHtml( ncharaccept ) +"')\" ":"").append(isEnable()?"":"disabled").append("   ").append(  listener!=null ?" onkeydown=\"sendenter(event,'":" onkeydown=\"sendlistener(event,'").append(getFormJsId()).append("','").append(getJsId()).append("','").append("enter").append("')\" ").append(isEditable()?"":"readonly").append(" placeholder=\"").append( escapeHtml( getHint() ) ).append("\"  class=\"nikitasmartentry1 form-control form-control-sm ").append(bfinder?"nikitasmartentry":"nikitaentry").append(ncharlcase.equals("true")?" n-char-lcase ":" ").append(ncharucase.equals("true")?" n-char-ucase ":" ").append(getViewClass()).append(getErrorClass()).append("\" style=\"").append(bfinder?"  ":"").append(getViewStyle()).append("\" id=\"").append(getJsId()).append("_text\" value=\"").append(  escapeHtml(getText()) ).append("\"  ").append(getViewAttribut()).append(" />\n" );
                       
                        if (bfinder) {
                            sb.append("<div style=\"position:absolute;right:4px;top:50%;margin-top:-8px\">");
                            String icon = getSearchIcon();
                                sb.append("<img ").append(isEnable()?"":"disabled").append("  style=\"width:18px;height:18px;\" src=\""+icon+"\"  class=\"textsmart\"  alt=\"Nikita Generator\"  id=\"").append(getJsId()).append("_finder\"     ").append(isEnable()?getClickAction(isbarcode?"barcode":"finder"):" disabled").append("    >\n" );
                            sb.append("</div>");
                        }
                        sb.append("</div>");
                    sb.append("</td>"); 
                    sb.append(getCommentView()); 
                sb.append("</tr></table>");
            }
        sb.append("</div>");
        return sb.toString();
    }
   private boolean isTypeTime(){
        if (getStyle()!=null) {
            if (getStyle().getInternalObject().getData("style").getData("n-type").toString().toLowerCase().equals("time")) {
                return true;
            }
        }
        return false;
    }
    
    public Style getStyle() {
        return super.getStyle()!=null?super.getStyle():Style.createStyle(); //To change body of generated methods, choose Tools | Templates.
    }
    private boolean isbarcode = false;
    private String getSearchIcon( ){
        if (getStyle()!=null) {
            if (getStyle().getInternalObject().getData("style").getData("n-searchicon").toString().toLowerCase().equals("true")) {
                return getBaseUrl("static/img/search.png");  
            }else if (getStyle().getInternalObject().getData("style").getData("n-searchicon").toString().trim().length()>=3) {
                return getBaseUrl(getStyle().getInternalObject().getData("style").getData("n-searchicon").toString());
            }else if (getStyle().getInternalObject().getData("style").getData("n-barcode").toString().toLowerCase().equals("true")) {
                isbarcode = true;
                return getBaseUrl("static/img/barcode.png");  
            }
        }
        return getBaseUrl("static/img/find.png");
    } 
    
      private boolean isPassword( ){
          if (getStyle()!=null) {
             if (getStyle().getInternalObject().getData("style").getData("n-password").toString().toLowerCase().equals("true")) {
                 return true;
                }else if (getStyle().getInternalObject().getData("style").getData("n-password-mask").toString().toLowerCase().equals("true")) {
                 return true;
             }
         }
         return false;
     }
    private boolean isEditable(){
        if (getStyle()!=null) {
            if (getStyle().getInternalObject().getData("style").getData("n-lock").toString().toLowerCase().equals("true")) {
                return false;
            }
        }
        return isEnable();
    }
    /*
    public String getView(NikitaViewV3 v3) {
        v3.setData(this);
        StringBuilder sb = new StringBuilder();
        sb.append("<div ").append(getStyle()!=null?getStyle().getViewAttr("n-div-", " "):"").append("  style=\"").append(isVisible()?"":"display:none;").append("\"  title=\"").append( escapeHtml(getTooltip()) ).append("\"   class=\"component ").append(getVisibleEnable()).append(getFormJsId()).append(" nikitatext\" id=\"").append(getJsId()).append("\" nid=\"").append(getId()).append("\"  nform=\"").append(getFormId()).append("\"  ntype=\"nikitatext\"  >\n");
            sb.append(getTagView());//sb.append("<div style=\"display:none\" ").append("id=\"").append(getJsId()).append("_tag\">").append(getTag()).append("</div>");     
            if (!isGone()) {
                sb.append("<table><tr>");
                    if (!getLabel().equals("")) {
                        sb.append("<td class=\"nikitacomponentlabel\" ").append(getStyle()!=null?getStyle().getViewAttr("n-label-", " "):"").append("  style=\"").append(getStyle()!=null?getStyle().getViewStyle("n-label-", " "):"").append("\">");
                        sb.append("<label>").append(Component.escapeHtml(getLabel()) +getMandatoryLabel()).append("</label>\n");
                        sb.append("</td>") ;   
                    }
                    sb.append("<td>");  ;   
                    
                    
                    //onkeydown="maskKeyCode(event,'')" //escapeHtml( getHint() ) 
                   
                     String ncharaccept =   getStyle().getInternalStyle().getData("n-char-accept").toString().trim();
                     String ncharaucase =     getStyle().getInternalStyle().getData("n-char-ucase").toString().trim();
                   
                    
                    
                            
                    
                    sb.append("<input ").append(listener!=null ?" onkeypress=\"sendenter(event,'":"on=\"sendenter(event,'").append(getFormJsId()).append("','").append(getJsId()).append("','").append("enter").append("')\"   ").append(isEnable()?"":"disabled").append(" placeholder=\"").append( escapeHtml( getHint() ) ).append("\"  class=\"nikitaentry").append(getErrorClass()).append(ncharaucase.equals("true")?" n-char-ucase ":"").append(getViewClass()).append("\" style=\"").append(getViewStyle()).append("\" type=\""+(isPassword()?"password":"text")+"\" id=\"").append(getJsId()).append("_text\" value=\"").append(  escapeHtml(getText()) ).append("\" />\n" );
                    sb.append("</td>");   
                sb.append("</tr></table>");
            }
        sb.append("</div>");
        return sb.toString();
    }
      public Style getStyle() {
        return super.getStyle()!=null?super.getStyle():Style.createStyle(); //To change body of generated methods, choose Tools | Templates.
    }
    private boolean isPassword( ){
          if (getStyle()!=null) {
             if (getStyle().getInternalObject().getData("style").getData("n-password").toString().toLowerCase().equals("true")) {
                 return true;
             }else if (getStyle().getInternalObject().getData("style").getData("n-password-mask").toString().toLowerCase().equals("true")) {
                 return true;
             }
         }
         return false;
     }
    */
}
