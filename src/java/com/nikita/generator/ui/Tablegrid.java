/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator.ui;

import com.nikita.generator.Component;
import com.nikita.generator.IAdapterListener;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaService;
import com.nikita.generator.NikitaViewV3;
import com.nikita.generator.Style;
import com.nikita.generator.ui.layout.HorizontalLayout;
import com.nikita.generator.ui.layout.NikitaForm;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import java.util.Hashtable;
/**
 * created by 13k.mail@gmail.com
 */
public class Tablegrid extends Component{
    protected IAdapterListener igrid = null;
    private Nset headers = Nset.newArray();
    private Nset coltype = Nset.newObject();
    private Nset colhide = Nset.newObject();
    private Nset csthide = Nset.newObject();
    private Nset colcehck = Nset.newObject();
    private Hashtable<String, Style> colstyle = new Hashtable<String, Style>();
    
    
    public static final int TYPE_STRING = 0;
    public static final int TYPE_CHECKBOK = 1;
    public static final int TYPE_BUTTON = 2;
    public static final int TYPE_IMAGE = 3;
    
    private boolean showrowcount = false;
    
    public Nset getDataHeader(){
        return headers;
    }  
    public void setAdapterListener(IAdapterListener adapterListener){
        igrid=adapterListener;
    }  
    
    public void setDataHeader(Nset header){
        headers=header;
    }  
    public void setColStyle(int col, Style style){
        colstyle.put(col+"", style);
    }
    public void setColType(int col, int type){
        coltype.setData(col+"", type);
    }
    
    public void setColHide(int col, boolean hide){
        colhide.setData(col+"", hide?"true":"false");
    }
    
    public void setColHide(String col, boolean hide){
        csthide.setData(col, hide?"true":"false");
    }    
    public boolean isCheckedOnPage(int row, int col){
        Nset restoreNset = Nset.readJSON(super.getText()); //must super       
        return isChecked(restoreNset.getData(getId()+ "-R"+row+"C"+col).getData("t").toString()) ;
    }
    public Nset getRowChecked(){
        Nset n = Nset.newArray();
       
        String[] key = colcehck.getData("-1").getObjectKeys();
        for (int i = 0; i < key.length; i++) {
            if (colcehck.getData("-1").getData(key[i]).toString().equals("1")) {
                n.addData(key[i]);
            }
        }         
        return n;
    }
    
    public Nset getKeyChecked(){
        Nset n = Nset.newArray();
        
        String[] key = colcehck.getData("-2").getObjectKeys();
        for (int i = 0; i < key.length; i++) {
            if (colcehck.getData("-2").getData(key[i]).toString().equals("1")) {
                n.addData(key[i]);
            }
        }         
        return n;
    }
    public void setCheckedOnPage(int row, int col, boolean b){
        Nset restoreNset = Nset.readJSON(super.getText());  //must super
        if (restoreNset.getObjectKeys().length==0) {
            restoreNset = Nset.newObject();
        }
        if (b) {
            restoreNset.setData(getId()+ "-R"+row+"C"+col ,  Nset.newObject().setData("t", Nset.newArray().addData("true").toJSON() )  );
        }else{
            restoreNset.setData(getId()+ "-R"+row+"C"+col ,  Nset.newObject().setData("t", Nset.newArray().addData(Nset.newNull()).toJSON() )  );
        }
        super.setText(restoreNset.toJSON());   //must super    
    }  
    public boolean isChecked(String key, int col){
        return colcehck.getData(col+"").getData(key).toString().equals("1");
    }
    public boolean isChecked(int absoulterow, int col){
        return isChecked(absoulterow+"",col);
    }
    public boolean isChecked(int absoulterow){
        return isChecked(absoulterow, -1);
    }
    
    public void setChecked(int absoulterow, boolean b){
        setChecked(absoulterow+"",-1, b);
    }
    
    public void setChecked(String absoulterow, int col, boolean b){ //{"cols":{"absoluterow":""}}    
        if (colcehck.getInternalObject() instanceof Hashtable) {
        }else{
            colcehck=Nset.newObject();
        }       
        Nset vcols  = Nset.newObject();       
        if (colcehck.getData(col+"").getInternalObject() instanceof Hashtable) {
            vcols = colcehck.getData(col+"");
        }         
        if (b) {
            vcols.setData(absoulterow+"", "1"); 
        }else{
            ((Hashtable)vcols.getInternalObject()).remove(absoulterow+"");
        }             
        colcehck.setData(col+"", vcols);
        
        
    }
    private boolean isChecked(String value){        
        return !value.equals("[]");        
    }
    private String getBufferCheckboxCellBuffer(){
        return colcehck.toJSON();//{"cols":{"absoluterow":""}}
    } 
    
    private int row =-1;
    public void restoreData(int i) {
        row=i;
    }
    
    public void setRowCounter(boolean b){
        showrowcount=b;
    }
    
    public void showRowIndex(boolean b){
        showrowindex=b;
    }
    public void showRowIndex(boolean b, boolean check){
        showrowindex=b;
        showcheckbox=check;
    }
    public void showActionCols(boolean b){
        showactioncols=b;
    }
    boolean showrowindex=false;
    boolean showactioncols=false;
    boolean showcheckbox=false;
     
     
    public int getSeletedRow(){
        return row;
    }

    public void setText(String text) {
        //dont super
         
        if (showcheckbox) {
            if (multiCheckCol!=-1) {
                Nset nout = Nset.newObject();
                if (text.equals("[*]")||text.equals("*")||text.equals("[\"*\"]")) {
                    if (getData()!=null) {
                        for (int i = 0; i < getData().getArraySize(); i++) {
                            nout.setData(getData().getData(i).getData(multiCheckCol).toString(),"1");
                        }
                    }                    
                }else{
                    Nset ninp = Nset.readJSON(text);
                    for (int i = 0; i < ninp.getArraySize(); i++) {
                        nout.setData(ninp.getData(i).toString(),"1");
                    }
                }                
                colcehck.setData("-1", nout);
            }else{
                Nset nout = Nset.newObject();
                if (text.equals("[*]")||text.equals("*")||text.equals("[\"*\"]")) {
                    for (int i = 0; i < maxrows; i++) {
                        nout.setData(i+"","1");
                    }
                }else{
                    Nset ninp = Nset.readJSON(text);
                    for (int i = 0; i < ninp.getArraySize(); i++) {
                        nout.setData(ninp.getData(i).toString(),"1");
                    }
                }                
                colcehck.setData("-1", nout);
            }            
        }else{
            row = Utility.getInt(text);
        }
    }

    public String getText() {
        //dont super        
  
        if (showcheckbox) {
            if (multiCheckCol!=-1) {
                return getRowChecked().toJSON();
            }else{
                return getRowChecked().toJSON();
            }            
        }else{
            return row+"";//return selected row
        }
    }
    
    private int multiCheckCol= -1;//autonumber
    public void setMultiCheckCol(int col){
        multiCheckCol=col;
    }
        
    public void setData(Nikitaset data) {
        maxrows=data.getDataAllVector().size();
        Nset info = new Nset( data.getInfo( ));
        if ( info.getData("nfid").toString().equals("Nset") && info.getData("mode").toString().equals("paging") ) {
            maxrows=info.getData("rows").toInteger();
            setCurrentPage(info.getData("page").toInteger());
        }   
        if (headers.getArraySize()<=0) {
            headers=new Nset(data.getDataAllHeader());
        }
        super.setData(new Nset(data.getDataAllVector()));  
    }
   
    public void setData(Nset data) {
        maxrows=data.getArraySize();
        if (headers.getArraySize()<=0) {
           // headers=data.getData(0);
        }
        super.setData(data); 
        
    }
    
    
    
    public String getPagingString(int page, int rowperpage, int rows) {
        int maxbutton = 6 /2;
        int pages = (rows/rowperpage) + (rows%rowperpage<=0?0:1);
        
        StringBuffer sb = new StringBuffer();
        sb.append("<ul class=\"pagination\">"); 
        if (rows > 1) {
            sb.append("<li><a href=\"#\"  class=\"").append(  page==1 ?"disable\"":  "\" " +getFilterAction("page-first")).append("  >&lt;&lt;</a></li>");
            sb.append("<li><a href=\"#\"  class=\"").append(  page==1 ?"disable\"":  "\" "+ getFilterAction("page-back") ).append("   >&lt;</a></li>");
            
            int low = (page-maxbutton)<=0?maxbutton-page:0;
            int hig = (page+maxbutton)>=pages?(page+maxbutton)-pages:0;            
            
            for (int i = Math.max(0, page-maxbutton-hig); i < Math.min(pages, page+maxbutton+low); i++) {
                    sb.append("<li><a href=\"#\"  class=\"").append( page==(i+1)?"current":  "").append("\"").append(getFilterAction("page-"+(i+1))).append("  \">" + (i+1) + "</a></li>");
            } 
            
            for (int i = 0; i < (pages); i++) {
                    //sb.append("<li><a href=\"#\"  class=\"").append( page==(i+1)?"current":  "").append("\"").append(getFilterAction("page-"+(i+1))).append("  \">" + (i+1) + "</a></li>");
            }  
            sb.append("<li><a href=\"#\"  class=\"").append(  page==pages ?"disable\"":  "\" ").append(" \" "+getFilterAction("page-next")).append("  >&gt;</a></li>");
            sb.append("<li><a href=\"#\"  class=\"").append(  page==pages ?"disable\"":  "\" ").append(" \" "+getFilterAction("page-last")).append("  >&gt;&gt;</a></li>");
        }
        sb.append("</ul>");
        return sb.toString();
    }
    
    
    protected String getFilterAction(String action){
        StringBuffer sb = new StringBuffer();        
        sb.append(" onclick=\"sendaction('").append(getFormJsId()).append("','").append(getJsId()).append("','").append(action).append("')\" ");          
        return sb.toString();
    }
    @Override
    public String getView(NikitaViewV3 v3) {        
        v3.setData(this);
        Nset restoreNset = Nset.readJSON(super.getText());//must super
        
        Nset hiden = Nset.newArray();
        String[] keys = colhide.getObjectKeys();
        for (int i = 0; i < keys.length; i++) {
            if (colhide.getData(keys[i]).toString().equals("true")) {
                hiden.addData(Utility.getInt(keys[i]));
            } 
           
        }   
        for (int col = 0; col < headers.getArraySize(); col++) {// 
             if (  csthide.getData(headers.getData(col).toString()).toString().equals("true") ) {
                hiden.addData(col);
            } 
        }
        
        
        StringBuilder sb = new StringBuilder();// border=\"1\" cellpadding=\"5\" cellspacing=\"2\"
        sb.append(nDivView("nikitatablegrid"));//sb.append("<div ").append(isEnable()?"":"disabled").append("  style=\"").append(isVisible()?"":"display:none;").append("\"  class=\"component ").append(getVisibleEnable()).append(getFormJsId()).append(" nikitatablegrid\" id=\"").append(getJsId()).append("\" nid=\"").append(getId()).append("\"  nform=\"").append(getFormId()).append("\"  ntype=\"nikitatablegrid\"   >\n" );
        sb.append("<p><b>").append(escapeHtml(getLabel())).append("</b></p>" );        
        sb.append(getTagView()); 
        sb.append(getLabelView()); 
        if (!isGone()) {
                sb.append("<div style=\"display:none\" ").append("id=\"").append(getJsId()).append("_cell_tag\"  nid=\"").append(getId()).append("_cell_tag").append("\" >").append(getBufferCheckboxCellBuffer()).append("</div>"); 
                if (!getViewStyle().contains("n-hide-pageup:true")) {
                    sb.append(getViewFilterPage(restoreNset,false , v3)); 
                }       
                if (getStyle()==null) {
                    setStyle(new Style());
                }
                if (!getStyle().getViewAttr().contains("n-table-border")) {
                    getStyle().setAttr("n-table-border", "1");
                }
                if (!getStyle().getViewAttr().contains("n-table-cellpadding")) {
                    getStyle().setAttr("n-table-cellpadding", "3");
                }
                if (!getStyle().getViewAttr().contains("n-table-cellspacing")) {
                    getStyle().setAttr("n-table-cellspacing", "0");
                }

                if (getStyle().getViewStyle().contains("n-multicheck-col")) {
                    setMultiCheckCol(getStyle().getInternalObject().getData("style").getData("n-multicheck-col").toInteger());
                }

                sb.append("<div style=\"overflow-x:auto\">");  
                sb.append("<table colshide=\"").append(hiden.toJSON()).append("\" id=\"").append(getJsId()).append("_grid\"  class=\"nikitatablegrid-table\"  style=\"width:100%\" ").append(getStyle()!=null?getStyle().getViewAttr("n-table-", " "):"").append("  >");
                sb.append("<thead class=\"ui-widget-header\" style=\"  ").append(getStyle()!=null?getStyle().getViewStyle("n-header-", " "):"").append("\"  >");// [ui-widget-header ][ui-state-default ui-state-active ui-corner-top]  ui-menu-item ui-state-focus ui-state-hover ui-state-default
                    if (showrowindex || showcheckbox) {
                        sb.append("<th style=\"").append(getViewStyleCol(-1)).append(";padding:10px;text-align:center;").append(getViewVisibleCol(-1)).append("\" >");
                        sb.append("<label ><b>").append(showrowindex?"No":"").append("</b></label> ");
                        sb.append("</th>");
                    }
                    for (int col = 0; col < headers.getArraySize(); col++) {// 
                        sb.append("<th style=\"").append(getViewStyleCol(col)).append(" padding:10px;text-align:center;").append(getViewStyleCol(col)).append(getViewVisibleCol( hiden, col)).append("\"  class=\"").append(getViewVisibleCol(hiden, col)).append("\"  >");
                        if (headers.getData(col).toString().startsWith("[") && col>=2 ) {
                            String caption = headers.getData(col).toString();
                            if (caption.endsWith("]")) {                        
                                sb.append("<label  ><b> ").append("").append("</b>  </label> ");
                            }else{
                                caption=caption.substring(caption.lastIndexOf("]")+1);
                                sb.append("<label  ><b> ").append(caption.trim()).append("</b>  </label> ");
                            }                       
                        }else{                    
                            sb.append("<label  ><b> ").append(headers.getData(col).toString()).append("</b>  </label> ");
                        }
                        sb.append("</th>");

                    } 
                    if (showactioncols) {
                        sb.append("<th >");
                        sb.append("<label ><b></b> </label> ");
                        sb.append("</th>");
                    }
                sb.append("</thead>");       
                
                //render datagrid
                for (int row = 0; row < getData().getArraySize(); row++) {
                    sb.append("<tr class=\"").append((row%2==0)?"odd":"even").append("\"   >");

                    int absoulterow = 0;
                    if ( getCurrentPage()==-1 && getShowPerPage()==-1) {
                        absoulterow = row;
                    }else{
                        absoulterow = (row  + (getCurrentPage()-1)*getShowPerPage());
                    }
                    //inflate from onther nikitaform  
                    if (showrowindex || showcheckbox) {
                        if (showcheckbox) {
                            final String idx = "-R"+absoulterow+"C-1" ;
                            Checkbox v = new Checkbox(){
                                protected String getClickAction(String action) {
                                    return Tablegrid.this.getClickAction(action); 
                                }                    
                                public String getJsId() {
                                    return Tablegrid.this.getJsId()+idx;  
                                }   
                                public String getFormJsId() {
                                    return Tablegrid.this.getJsId()+"_grid_cell";   
                                } 
                            };

                            v.setId(getId()+"-R"+absoulterow+"C-1");
                            v.setData( Nset.readsplitString(showrowindex?(absoulterow+1)+"":" "+"") );


                            if (multiCheckCol!=-1) {
                                v.setTag(getData().getData(row).getData(multiCheckCol).toString());
                            }else{
                                v.setTag((absoulterow)+"");
                            }
                            v.setText(isChecked(v.getTag(), -1)?"[\"true\"]":"");

                            sb.append("<td style=\"text-align:right;padding-right:5px\">");
                            sb.append(v.getView(v3)); 
                            sb.append("</td>");
                        }else{
                            sb.append("<td style=\"text-align:right;padding-right:5px\">");
                            sb.append((absoulterow+1));
                            sb.append("</td>");
                        }
                    }
                    for (int col = 0; col < (headers.getArraySize()==0 ?getData().getData(row).getArraySize():headers.getArraySize())     +(showactioncols?1:0); col++) {
                        Component v = null;

                        String stext = getData().getData(row).getData(col).toString();


                        if (showrowcount && col==0) {
                            stext=(absoulterow+1 )+"";                    
                            if ( colstyle.get("0")==null ) {
                                Style style = new Style();
                                style.setStyle("text-align", "right");                        
                                colstyle.put("0", style);
                            }                    
                        }                
                        final String idx = "-R"+absoulterow+"C"+col ;
                        if (coltype.getData(col+"").toInteger()==TYPE_CHECKBOK) {
                            v = new Checkbox(){
                                protected String getClickAction(String action) {
                                    return Tablegrid.this.getClickAction(action); 
                                }                    
                                public String getJsId() {
                                    return Tablegrid.this.getJsId()+idx;  
                                }   
                                public String getFormJsId() {
                                    return Tablegrid.this.getJsId()+"_grid_cell";   
                                } 
                            };

                            v.setData( Nset.readsplitString(stext) );
                        }else if (coltype.getData(col+"").toInteger()==TYPE_IMAGE) {
                            v = new Image(){
                                protected String getClickAction(String action) {
                                    return Tablegrid.this.getClickAction(action); 
                                }                    
                                public String getJsId() {
                                    return Tablegrid.this.getJsId()+idx;  
                                }   
                                public String getFormJsId() {
                                    return Tablegrid.this.getJsId()+"_grid_cell";   
                                } 
                            };
                            Style style = new Style();
                            style.setStyle(Style.WIDTH, "42px");
                            style.setStyle(Style.HEIGHT, "42px");
                            v.setStyle(style);
                            v.setText(stext);
                        }else if (coltype.getData(col+"").toInteger()==TYPE_BUTTON) {
                            v = new Button(){
                                protected String getClickAction(String action) {
                                    return Tablegrid.this.getClickAction(action); 
                                }                    
                                public String getJsId() {
                                    return Tablegrid.this.getJsId()+idx;  
                                }   
                                public String getFormJsId() {
                                    return Tablegrid.this.getJsId()+"_grid_cell";   
                                } 
                            };
                            v.setText(stext);
                        }   


                        if (v!=null) {
                            v.setId(getId()+"-R"+absoulterow+"C"+col);
                        }

                        Component vv = null;
                        if (igrid!=null) {
                            vv =igrid.getViewItem(row, col, this, getData());
                            if (vv!=null && vv.isVisible()!=true && (vv.getId().equals("")||vv.getName().equals(""))) {
                                stext=vv.getText();
                                vv=null;                        
                            }else{
                                v=(vv!=null)?vv:v;   
                            }                               
                        }       

                        if (v!=null) {                            
                            sb.append("<td ").append(vv!=null?"":getClickAction("item-"+row)).append(" ").append(getViewAttrCol(col)).append(" ").append(v.getStyle()!=null?v.getStyle().getViewAttr("n-td-", " "):"").append("  style=\"padding:8px;").append( getViewVisibleCol( hiden, col)).append(v.getStyle()!=null?v.getStyle().getViewStyle("n-td-", " "):"").append(getViewStyleCol(col)).append(" \">");
                            /*
                            if (!v.getFormId().contains( getId()+"_grid_cell" )) {
                                if (v.getId().contains("[")) {
                                    v.setId(v.getId().substring(0, v.getId().indexOf("[") ));
                                    v.setForm(Tablegrid.this.getForm());
                                }
                            } 
                            */


                            if ( !(v.getForm()!=null) ) {
                                v.setForm(Tablegrid.this.getForm());
                            }

                            if (vv==null && coltype.getData(col+"").toInteger()==TYPE_CHECKBOK) {                        
                                v.setText(isChecked(absoulterow, col)?"[\"true\"]":""); 
                            }
                            sb.append(v.getView(v3));

                        }else{

                            if (headers.getData(col).toString().startsWith("[#]") || headers.getData(col).toString().startsWith("[*]")|| headers.getData(col).toString().startsWith("[v]") ) { 
                            }  else if (headers.getData(col).toString().contains("[") && col>=2 ) { 
                                String 
                                s = Utility.replace(headers.getData(col).toString(), "]","]|");
                                s = Utility.replace(s, "[","|[");
                                Nset na = Nset.readsplitString(s,"|");

                                String deficon = "[up][down][edit][add][delete][remove][copy][run][play][view][paste][new][find][search][clear][image][map][gmap][camera][mobile][phone][download][upload][newtab][sync][home][cut][move][new1][schrestart][schstop][schstart][grid1][grid2][grid3]";                        


                                HorizontalLayout h1 =new HorizontalLayout();
                                for (int i = 0; i < na.getArraySize(); i++) {
                                    if (na.getData(i).toString().trim().equals("")) {                                
                                    }else if (deficon.contains(na.getData(i).toString())) {
                                        Image 
                                        img =new Image(){
                                            protected String getClickAction(String action) {
                                                return  Tablegrid.this.getClickAction(getId()).replace("ondblclick=", "onclick=");  
                                            }
                                        };
                                        String id =na.getData(i).toString();
                                        try {
                                            id=id.substring(1,id.length()-1).trim();
                                        } catch (Exception e) {  }
                                        String icon = id;
                                        img.setId("item-"+row+"-"+id);
                                        img.setText("img/"+icon+".png");
                                        img.setStyle(new Style().setStyle("margin-right","3px")); 
                                        img.setOnClickListener(new Component.OnClickListener() {
                                            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {                            
                                            }
                                        });
                                        h1.addComponent(img);
                                    }else if (na.getData(i).toString().contains(".")) {
                                        //UI-GRID-ACTION-ICON -["photo":{"icon":""}]
                                        Image 
                                        img =new Image(){
                                            protected String getClickAction(String action) {
                                                return  Tablegrid.this.getClickAction(getId()).replace("ondblclick=", "onclick=");  
                                            }
                                        };
                                        String id =na.getData(i).toString();
                                        try {
                                            id=id.substring(1,id.length()-1).trim();
                                        } catch (Exception e) {  }
                                        String icon = Utility.replace(id, ".", "/");
                                        img.setId("item-"+row+"-"+id);
                                        img.setText("img/"+icon+".png");
                                        img.setStyle(new Style().setStyle("margin-right","3px")); 
                                        img.setOnClickListener(new Component.OnClickListener() {
                                            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {                            
                                            }
                                        });
                                        h1.addComponent(img);
                                    }

                                }  
                                //sb.append("<td ").append(getClickAction("item-"+row)).append(" style=\"padding:8px;").append(getViewVisibleCol(hiden,col)).append("").append(getViewStyleCol(col)).append(" \">");
                                sb.append("<td ").append("").append(" style=\"padding:8px;").append(getViewVisibleCol(hiden,col)).append("").append(getViewStyleCol(col)).append(" \">");

                                //sb.append("<td>");
                                sb.append(h1.getView(v3));
                            }else{
                                sb.append("<td ").append(getClickAction("item-"+row)).append(" style=\"padding:8px;").append(getViewVisibleCol(hiden,col)).append("").append(getViewStyleCol(col)).append(" \">");
                                String cellformat = "" ;
                                //n-col-"+col+"-"
                                if (getStyle()!=null  ) {
                                    cellformat = getStyle().getViewStylePrefix("n-col-"+col+"-format").toLowerCase();
                                }
                                if (cellformat.startsWith(":currency")) {
                                   sb.append(Utility.formatCurrency(stext));    
                                }else if (cellformat.startsWith(":fdateview")) {
                                    sb.append(Utility.formatDate(Utility.getDate(stext), "dd/MM/yyyy"));   
                                }else if (cellformat.startsWith(":fdatetimeview")) {
                                    sb.append(Utility.formatDate(Utility.getDate(stext), "dd/MM/yyyy HH:mm:ss"));   
                                }else if (cellformat.startsWith(":image")) {
                                   sb.append("<img src=\"").append( getBaseUrl(stext)).append("\"  style=\"").append("").append("\"   alt=\"Nikita Generator\"   ").append("").append("    >\n" );      
                                }else{
                                    if (getStyle()!=null && getStyle().getInternalStyle().containsKey("n-select-absrow")) {
                                        if (getStyle().getInternalStyle().getData("n-select-absrow").toInteger()==absoulterow) {
                                            sb.append("<b>").append( (stext)).append("</b>"); 
                                        } else{
                                            sb.append(stext); 
                                        } 
                                    }else if (getStyle()!=null && getStyle().getInternalStyle().containsKey("n-selected")) {
                                        if (getStyle().getInternalStyle().getData("n-selected").toInteger()==row) {
                                            sb.append("<b>").append( (stext)).append("</b>"); 
                                        } else{
                                            sb.append(stext); 
                                        }                                       
                                    }else{
                                        sb.append(stext); 
                                    }
                                }                                
                            }

                        }    
                        sb.append("</td>");
                    } 
                    sb.append("</tr>");
                }
                sb.append("</table>");
                sb.append("</div>");  
                sb.append(getViewFilterPage(restoreNset,true, v3));
        }
        sb.append("</div>");  
        return sb.toString();
    }
    
    private boolean isSelected(int row, int absrow){
        return false;
    }
        
    protected String getViewFilterPage(Nset restoreNset, boolean showrows, NikitaViewV3 v3){
        StringBuffer sb = new StringBuffer();
        if (getCurrentPage()!=-1) {  
            Style style = new Style();
            style.setStyle("width", "60px");
            style.setStyle("padding-right", "4px");
            Combobox combobox =new Combobox(){
                protected String getClickAction(String action) {
                    return getFilterAction("page-view"); 
                }                    
                public String getJsId() {
                    return Tablegrid.this.getJsId()+"_show";  
                }   
                public String getFormJsId() {
                    return Tablegrid.this.getJsId()+"_grid_cell";   
                } 
            };
            combobox.setData(Nset.readsplitString("10|25|50|100|500"));
                     
            combobox.setId(getId()+"_show");
            combobox.setLabel("Show Page : ");      
            combobox.setStyle(style);
            combobox.setText(restoreNset.getData(getId()+"_show").getData("t").toString());            
            combobox.setTag(Nset.newObject().setData("page", getCurrentPage()).setData("sort", getSortCol()).setData("text",restoreNset.getData(getId()+"_show").getData("t").toString()).toJSON());//tag
                    
            sb.append("<div style=\"float:left;overflow-x: hidden;overflow-y: hidden;\">");
            sb.append(showrows?combobox.getView(v3):"");
            sb.append("</div>");
       
            if (showrows) {
                sb.append("<div style=\"float:left;overflow-x: hidden;overflow-y: hidden;\">");
                if (maxrows==0) {
                    sb.append("<br> &nbsp;no entries"); 
                }else{
                    sb.append("<br> &nbsp;Showing "+ Utility.formatCurrency((getCurrentPage()-1)*getShowPerPage()+1) +" to "+ Utility.formatCurrency(((getCurrentPage()-1)*getShowPerPage()) +getData().getArraySize())+" of "+Utility.formatCurrency(maxrows)+" entries"); 
                }            
                sb.append("</div>");
            }            
            sb.append("<div style=\"float:right;overflow-x: hidden;overflow-y: hidden;\">");
            sb.append(getPagingString(getCurrentPage(), getShowPerPage(), maxrows));
            sb.append("</div>");
            sb.append("<div style=\"clear:both\"></div>");
        }
        return sb.toString();
    }
    private String getViewStyleCol(int col){
        return colstyle.get(col+"")!=null ?colstyle.get(col+"").getViewStyle():(getStyle()!=null?getStyle().getViewStyle("n-col-"+col+"-", " "):"");
    }
    private String getViewAttrCol(int col){
        return colstyle.get(col+"")!=null ?colstyle.get(col+"").getViewAttr():(getStyle()!=null?getStyle().getViewAttr("n-col-"+col+"-", " "):"");
    }
    private String getViewVisibleCol(int col){
        return colhide.getData(col+"").toString().equals("true")?" display:none; ":"";
    }
    private String getViewVisibleCol(Nset hidden, int col){
        String s = " " + hidden.toString().substring(1, hidden.toString().length()-1)+",";
        return s.contains(" "+col+",")?" display:none; ":"";
    }
    
 
    public void restoreData(Nset data) {       
        super.restoreData(data);  
                      
        currPage=-1;         
        rowperPage=-1;
        sortcol=-1;
        
        
        
        Nset restoreNset = Nset.readJSON(super.getText());//must super
        
        
        
        int i = Utility.getInt(restoreNset.getData(getId()+"_show").getData("t").toString());
        if (i>=1)  
            rowperPage=i;
        
        
        Nset x = Nset.readJSON(restoreNset.getData(getId()+"_show").getData("n").toString());
        
        int p = x.getData("page").toInteger();
        if (p>=1)  
            currPage=p;
         
        
        int sort =  x.getData("sort").toInteger();
        if (sort>=1)  
            sortcol=sort;
         
        
        //restore checkbook
        colcehck = Nset.readJSON(restoreNset.getData(getId()+"_cell_tag").getData("n").toString());//{"cols":{"absoluterow":""}}
        
        
        String[] keys = restoreNset.getObjectKeys();
        for (int j = 0; j < keys.length; j++) {
            if (keys[j].startsWith(getId()+"-R")) {
                
                String s = keys[j].substring((getId()+"-R").length()  );
                int absrow = Utility.getInt(s.substring(0,s.indexOf("C")));
                int col = Utility.getInt(s.substring(s.indexOf("C")+1));

                setChecked(restoreNset.getData(keys[j]).getData("n").toString(), col,  isChecked(restoreNset.getData(keys[j]).getData("t").toString()) );
            }  
            
        }
      
        
    }
    private int currPage    =-1;
    private int rowperPage  =-1;
    private int maxrows     =0;
    private int sortcol     =-1;
    public int getSortCol(){
        return sortcol;//?
    }
    
    public int getCurrentPage(){
        return currPage;
    }
    public int getShowPerPage(){
        return rowperPage;
    }
    public void setCurrentPage(int page){
        currPage=page;
    }
    public boolean prepareItemClick(String action){
        if ( action.startsWith("item-")) {
            String row = action.substring(5);               
            restoreData(Utility.getInt(row));
            return true;                         
        }
        return false;
    }
    public boolean prepareFilter(String action){
        if (action.startsWith("page-first")) {
            setCurrentPage(1);
            return true;
        }else if (action.startsWith("page-back")) {
            setCurrentPage(getCurrentPage()-1);
            return true;
         }else if (action.startsWith("page-next")) { 
            setCurrentPage(getCurrentPage()+1);
            return true;
        }else if (action.startsWith("page-last")) { 
            setCurrentPage(999999999);
            return true;
        }else if (action.startsWith("page-view")) { 
            return true;
        }else if (action.startsWith("page-")) {     
            String page = action.substring(5);
            setCurrentPage(Utility.getInt(page));
            return true;
        }else if (action.startsWith("page-sort")) {     
            return true;
        }
        return false;
    }
    
    private OnFilterClickListener listener; 
    public void setOnFilterClickListener(OnFilterClickListener listener){
        setOnFilterClickListener(listener, 1, 10);
    }
    public void setOnFilterClickListener(OnFilterClickListener listener, int curpage, int rowperpage){
        setCurrentPage(curpage);//first
        rowperPage=rowperpage;//def
        this.listener=listener;
    }    
    public OnFilterClickListener getOnFilterClickListener(){
        return listener;
    }

    public interface OnFilterClickListener {
        public void OnFilter(NikitaRequest request, NikitaResponse response, Component component);
    }
}
