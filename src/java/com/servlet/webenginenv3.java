package com.servlet;

import com.nikita.generator.Component;
import com.nikita.generator.IAdapterListener;
import com.nikita.generator.NikitaEngine;
import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaServlet;
import com.nikita.generator.Style;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.ui.Button;
import com.nikita.generator.ui.Combobox;

import com.nikita.generator.ui.Image;
import com.nikita.generator.ui.Label;
import com.nikita.generator.ui.Tablegrid;
import com.nikita.generator.ui.Textbox;
import com.nikita.generator.ui.Textsmart;
import com.nikita.generator.ui.layout.HorizontalLayout;
import com.nikita.generator.ui.layout.NikitaForm;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import com.web.utility.WebUtility;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Vector;

/**
 *
 * @author user
 */
public class webenginenv3 extends NikitaServlet{
 private static final String ALL = "*";
    /*parameter
    mode
    code
    search
    formname
    formid
    name
    id
    data
    */

    Label condition;
    String user ;
    private Nset componentName = Nset.newObject();
    int dbCore;
    NikitaConnection nikitaConnection ;
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        NikitaForm nf = new NikitaForm(this);
        nf.setText("Engine Nv3");        
        HorizontalLayout horisontalLayout = new HorizontalLayout();
        
        nikitaConnection =response.getConnection(NikitaConnection.LOGIC);
        user = response.getVirtualString("@+SESSION-LOGON-USER");
        dbCore = WebUtility.getDBCore(nikitaConnection);
        
        Style style = new Style();        
        nf.setStyle(new Style().setStyle("width", "1092").setStyle("height", "606"));
       
        
        final Label lblmode = new Label();
        lblmode.setId("logic-lblmode");
        lblmode.setTag(request.getParameter("mode"));        
        lblmode.setVisible(false);
        nf.addComponent(lblmode);
        
        final Label buffered = new Label();
        buffered.setId("comp-buffered");
        buffered.setVisible(false);
        nf.addComponent(buffered);
        
        
        condition = new Label();
        condition.setId("comp-lblcreatedby");
        condition.setTag(request.getParameter("created"));     
        condition.setText(request.getParameter("unlock"));        
        condition.setVisible(false);
        nf.addComponent(condition);
        
        Label lbltype = new Label();
        lbltype.setId("formtype");
        lbltype.setTag(request.getParameter("formtype"));        
        lbltype.setVisible(false);
        nf.addComponent(lbltype);
        
        Textbox txt = new Textsmart();
        txt.setId("comp-txtFinder");
        txt.setLabel("Form Name");
        txt.setText(request.getParameter("formname"));
        txt.setTag(request.getParameter("formid"));
        txt.setStyle(new Style().setStyle("n-lock", "true"));
        txt.setVisible(false);
        nf.addComponent(txt);
        
        Button btn = new Button();
        btn.setId("comp-btnFinder");
        btn.setText("Finder");
        btn.setVisible(false);
        horisontalLayout.addComponent(btn);
        txt.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                request.setParameter("code", "selected");
                request.setParameter("search", response.getContent().findComponentbyId("comp-txtFinder").getText() );
                response.showform("webform", request,"form",true);      
                response.write();
            }
        });
        
        nf.addComponent(horisontalLayout);
        
        horisontalLayout = new HorizontalLayout();
        txt = new Textsmart();
        txt.setId("comp-txtSearch");
        txt.setLabel("Searching");
        txt.setText(request.getParameter("search"));
        txt.setStyle(new Style().setStyle("n-searchicon", "true"));
        horisontalLayout.addComponent(txt);
        
        
        txt.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);        
               
                response.refreshComponent(response.getContent().findComponentbyId( "comp-btnAdd"));      
                response.refreshComponent(response.getContent().findComponentbyId("comp-parent"));
                response.refreshComponent(response.getContent().findComponentbyId("comp-tblComponent"));
                response.write();
            }
        });
        
        Component img = new Image();
        img.setId("comp-btnAdd");
        img.setText("img/add.png");
        style = new Style();
        style.setStyle("margin-top", "3px").setAttr("n-layout-align", "left");
        img.setStyle(style);
        img.setVisible(false);
        horisontalLayout.addComponent(img);
        img.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                
                if(condition.getTag().equals(user) || !condition.getText().equals("")){
                    request.setParameter("mode","add");
                    request.setParameter("formName", response.getContent().findComponentbyId("comp-txtFinder").getText());
                    request.setParameter("formId", response.getContent().findComponentbyId("comp-txtFinder").getTag());                    
                    response.showform("webcomponentadd", request,"add", true);
                    response.write();
                }else{                    
                    response.showDialogResult("Warning from "+condition.getTag(), "You don't have access to add new", "warning",null, "OK", "");
                }
            }
        });   
        
        
        img = new Image();
        img.setId("comp-btnHierarki");
        img.setText("img/hierarki.png");
        img.setVisible(true);
        style = new Style();
        style.setStyle("margin-top", "3px").setAttr("n-layout-align", "left");
        img.setStyle(style);
        img.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                //webformhierarki
                request.setParameter("fname", response.getContent().findComponentbyId("comp-txtFinder").getText());
                request.setParameter("fid", response.getContent().findComponentbyId("comp-txtFinder").getTag());
                response.showformGen( "webformhierarki" , request, "", false);
                response.write();

            }
        });
        img.setVisible(false);
        horisontalLayout.addComponent(img);
        
        
        //add right forparent
        Combobox combo = new Combobox();
        combo.setId("comp-parent");
        combo.setLabel("Parent");
        combo.setText(ALL);
        combo.setStyle(new Style().setStyle("n-searchicon", "true").setAttr("n-layout-align", "right").setStyle("n-label-width", "80px").setStyle("width", "200px"));
        combo.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);     
                response.refreshComponent(response.getContent().findComponentbyId( "comp-btnAdd")); 
                response.refreshComponent(response.getContent().findComponentbyId("comp-parent"));
                response.refreshComponent(response.getContent().findComponentbyId("comp-tblComponent"));
                response.write();
            }
        });
        combo.setVisible(false);
        HorizontalLayout hr = new HorizontalLayout();
        hr.addComponent(horisontalLayout);
        hr.addComponent(combo);
        
         
                
                
        img = new Image();
        img.setId("comp-aaaaaaaa");
        img.setText("img/play.png");
        img.setVisible(true);
        style = new Style();
        style.setStyle("margin-left", "8px").setStyle("width", "24px").setStyle("height", "24px") ;
        img.setStyle(style);
        img.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                String type = response.getContent().findComponentbyId("formtype").getTag();
                 
                response.write();
            }
        });
        img.setVisible(false);
        hr.addComponent(img);
        
        
        
        hr.setStyle(new Style().setStyle("n-table-width", "100%"));
        
        
       
        nf.addComponent(hr);
        
        final Tablegrid tablegrid = new Tablegrid();
        tablegrid.setId("comp-tblComponent");
        tablegrid.setDataHeader(Nset.readsplitString("No,Form Name,Detail,",","));
        tablegrid.setRowCounter(true);
         
         
        tablegrid.setStyle(new Style().setStyle("n-ondblclick", "true"));
        
        nf.addComponent(tablegrid);  
        
        tablegrid.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);
                
                request.setParameter("idcomp",tablegrid.getData().getData(tablegrid.getSeletedRow()).getData(0).toString()   ); 
                request.setParameter("idform",tablegrid.getData().getData(tablegrid.getSeletedRow()).getData(1).toString()   ); 
                request.setParameter("mode","logiclist");
                if(!condition.getText().equals("")){
                    request.setParameter("unlock","open");  
                }else{                    
                    request.setParameter("unlock",""); 
                }
                request.setParameter("created",condition.getTag());
                response.showform("weblogic", request,"edit", true);
                response.write();
            }
        });
        /*
        tablegrid.setOnFilterClickListener(new Tablegrid.OnFilterClickListener() {
            @Override
            public void OnFilter(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);
                response.refreshComponent(response.getContent().findComponentbyId("comp-btnAdd")); 
                response.refreshComponent(response.getContent().findComponentbyId("comp-parent"));
                response.refreshComponent(response.getContent().findComponentbyId("comp-tblComponent"));
                response.write();
            }
        });
        */
         
        
                
        Component comp = new Component();
        comp.setId("comp-btnEdit");
        nf.addComponent(comp);
        comp.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                request.setParameter("data", component.getTag());
                request.setParameter("mode","edit");
                if(!condition.getText().equals("")){
                    request.setParameter("unlock","open");  
                }else{                    
                    request.setParameter("unlock",""); 
                }
                request.setParameter("created",condition.getTag());
                response.showform("webcomponentadd", request,"edit", true);
                response.write();

            }
        });        
        
        
        
        comp = new Component();
        comp.setId("comp-btnDelete");
        nf.addComponent(comp);
        comp.setOnClickListener(new Component.OnClickListener() {
           
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                Nset nset = Nset.readJSON(component.getTag());
                 
                String path = getClass().getResource("").getPath();
                if (path!=null && path.endsWith("/servlet/")) {
                    path =path.substring(0, path.length()-8)+"nv3/"+nset.getData(1).toString()+".nv3";   
                    new File(path).delete();
                }
                fillGrid(response);
                 
                response.refreshComponent( (Tablegrid)response.getContent().findComponentbyId("comp-tblComponent")  );
                response.write();

            }
        });
        
        comp = new Component();
        comp.setId("comp-btnCompile");
        nf.addComponent(comp);
        comp.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {                
              
                Nset nset = Nset.readJSON(component.getTag());
                 
                String path = getClass().getResource("").getPath();
                if (path!=null && path.endsWith("/servlet/")) {
                    path =path.substring(0, path.length()-8)+"nv3/"+nset.getData(1).toString()+".nv3";   
                    new File(path).delete();
                }
                
                String fname = nset.getData(1).toString();
                NikitaEngine.compile(response.getConnection(NikitaConnection.LOGIC), nset.getData(1).toString()  );  
                 
                        
                
                fillGrid(response);
                response.refreshComponent( (Tablegrid)response.getContent().findComponentbyId("comp-tblComponent")  );
                response.write();
            }
        });
        
        comp = new Component();
        comp.setId("comp-btnCopy");
        nf.addComponent(comp);
        comp.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                
                Nset nset = Nset.readJSON(component.getTag());
                if(condition.getTag().equals(user) || !condition.getText().equals("")){ 
                    response.getContent().findComponentbyId("comp-buffered").setTag(component.getTag());
                    response.refreshComponent(response.getContent().findComponentbyId("comp-buffered"));
                    request.setParameter("idform", response.getContent().findComponentbyId("comp-txtFinder").getTag());
                    request.setParameter("index", nset.getData(3).toString());
                    request.setParameter("formname", "");
                    response.showform("webcompcopy", request,"copy", true);             
                    response.write();
                }else{
                    response.showDialogResult("Warning from "+condition.getTag(), "You don't have access to copy", "warning",null, "OK", "");
                }
            }
        });
        
       
         
       
        response.setContent(nf);        
        nf.setOnLoadListener(new NikitaForm.OnLoadListener() {
            public void OnLoad(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid( response );
            }
        });
        
 
    }
    
    public void fillGrid(NikitaResponse response){
        Combobox combo = (Combobox)response.getContent().findComponentbyId("comp-parent") ;  
        Tablegrid tablegrid = (Tablegrid)response.getContent().findComponentbyId("comp-tblComponent") ;
        Component cmpBtnAdd =response.getContent().findComponentbyId("comp-btnAdd") ;
        Textbox txtSearch= (Textbox)response.getContent().findComponentbyId("comp-txtSearch");
        Textbox txtFinder= (Textbox)response.getContent().findComponentbyId("comp-txtFinder");
        Component mode= response.getContent().findComponentbyId("comp-btnFinder");
        
        String s =  txtSearch.getText() ;
        
         Vector<Vector<String>> datas = new Vector<Vector<String>>() ;
        
      
        String path = getClass().getResource("").getPath();
        if (path!=null && path.endsWith("/servlet/")) {
            path =path.substring(0, path.length()-8)+"nv3/";             
            File[] files= new File(path).listFiles();
            if (files!=null) {
                sortFile(files);
                for (int i = 0; i < files.length; i++) {
                    File file = files[i];
                    String name = file.getName();
                    
                    if (name.toLowerCase().endsWith(".nv3")) {                         
                        name = name.substring(0,name.length()-4)  ;  
                    }
                    
                    
                    if (s.equals("")||name.toLowerCase().contains(s.toLowerCase())) {
                        Vector<String> data = new Vector<String>() ;
                        data.addElement("");
                        data.addElement(name);
                        data.addElement("Modified Date : " +  Utility.formatDate(file.lastModified(), "yyyy-MM-dd HH:mm:ss"));
                        data.addElement("");
                        datas.addElement(data);
                    }
                }
            }
        }
         Vector<String> headers = new Vector<String>() ;
         headers.addElement("no");
         headers.addElement("from");
         headers.addElement("detail");
         headers.addElement("");
         
        
        Nikitaset nikiset = new Nikitaset(headers, datas);
        tablegrid.setData(nikiset);
        tablegrid.setTag(tablegrid.getData().copyNikitasetAtCol(0).toJSON());        
 
        tablegrid.setColStyle(0, new Style().setStyle("width", "50px").setStyle("width", "50px").setStyle("text-align", "right"));
        tablegrid.setColStyle(3, new Style().setStyle("width", "50px").setStyle("width", "50px").setStyle("text-align", "right"));
         
        tablegrid.setAdapterListener(new IAdapterListener() {
            @Override
            public Component getViewItem(int row, int col, Component parent, Nset data) {
                if(col == 3){
                    HorizontalLayout horisontalLayout = new HorizontalLayout();
                    Component btn = new Image();
                    btn.setId("comp-btnCompile["+row+"]");
                    btn.setText("img/play.png");
                    btn.setTag(data.getData(row).toJSON());
                    btn.setStyle(new Style().setStyle("margin-right","3px"));     
                    
                    Style style = new Style();
                    style.setStyle("float", "right");
     
                    horisontalLayout.setStyle(style);
                    
                    horisontalLayout.addComponent(btn);
                    btn.setOnClickListener(new Component.OnClickListener() {
                        public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {                            

                        }
                    });
                    
                      
                    
                    btn = new Image();
                    btn.setId("comp-btnDelete["+row+"]");
                    btn.setText("img/minus.png");
                    if(condition.getTag().equals(user) ||
                       !condition.getText().equals("")){
                        btn.setStyle(new Style().setStyle("margin-right","3px")); 
                    }else{                        
                        btn.setStyle(new Style().setStyle("opacity","0btn.2")); 
                    }  
                    btn.setTag(data.getData(row).toJSON());
                    horisontalLayout.addComponent(btn);
                    btn.setOnClickListener(new Component.OnClickListener() {
                        public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                              
                        }
                    });                   
                 
                    
                    return horisontalLayout;
              
                } else if(col==4){   
                    Component cmp = new Component();
                    cmp.setVisible(false);
                    cmp.setText(componentName.getData(data.getData(row).getData(col).toString()).toString());                   
                    return cmp;                    
               
                }                   
                return null;
            }
        });
    }  
    
    public static void sortFile(File[] files) {
        if (files!=null) {
            Arrays.sort(files, new Comparator<File>() {
                public int compare(File o1, File o2) {
                     
                    String d1 = o1.getName();
                    String d2 = o2.getName();
                    return  d1.compareToIgnoreCase( d2 )  ;
                }
               
            });
            /*
            System.out.println("Sorted");
            for(File f : files) {
                System.out.println(f.getName());
            }
                    */
        }
    }
}
