/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.servlet;

import com.nikita.generator.Component;
import com.nikita.generator.IAdapterListener;
import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaService;
import com.nikita.generator.NikitaServlet;
import com.nikita.generator.Style;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.ui.Button;
import com.nikita.generator.ui.Checkbox;
import com.nikita.generator.ui.Image;
import com.nikita.generator.ui.Label;
import com.nikita.generator.ui.Tablegrid;
import com.nikita.generator.ui.Textbox;
import com.nikita.generator.ui.Textsmart;
import com.nikita.generator.ui.layout.HorizontalLayout;
import com.nikita.generator.ui.layout.NikitaForm;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.AES;
import com.rkrzmail.nikita.utility.Utility;
import com.web.utility.WebUtility;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import org.apache.commons.lang.WordUtils;

/**
 *
 * @author user
 */
public class webimport extends NikitaServlet{
    Label  lblfname ;
    Label  lblext ;
    int dbCore;
        Nikitaset ns; 
    NikitaConnection nikitaConnection ;
    boolean importNoPrefix = false;
    String user ;
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        
        user = response.getVirtualString("@+SESSION-LOGON-USER");
        
        nikitaConnection =response.getConnection(NikitaConnection.LOGIC);
        dbCore = WebUtility.getDBCore(nikitaConnection);
        
       Nikitaset position = nikitaConnection.Query("SELECT position FROM sys_user WHERE username = ? ", response.getVirtualString("@+SESSION-LOGON-USER"));
       importNoPrefix = Utility.getInt(position.getText(0, 0)) >=1 ;//check supervisor
        
        NikitaForm nf = new NikitaForm(this);
        nf.setText("Detail Import");        
        HorizontalLayout horisontalLayout = new HorizontalLayout();
        
        lblfname = new Label();
        lblfname.setId("fname");
        lblfname.setText(request.getParameter("fname"));
        lblfname.setVisible(false);
        nf.addComponent(lblfname);
        request.retainData(lblfname);
        
        lblext = new Label();
        lblext.setId("ext");
        lblext.setText(request.getParameter("ext"));
        lblext.setVisible(false);
        nf.addComponent(lblext);
        request.retainData(lblext);
        
        
        Style style = new Style();
        style.setStyle("width", "800");
        style.setStyle("height", "670");
        nf.setStyle(style);
        
        Textsmart txt = new Textsmart();
        txt.setId("detailimport-txtSearch");
        txt.setLabel("Searching");
        txt.setText(request.getParameter("search"));
        txt.setStyle(new Style().setStyle("n-searchicon", "true"));
        //nf.addComponent(txt);
        txt.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);                  
                response.refreshComponent(response.getContent().findComponentbyId("detailimport-tblForm"));
            }
        }); 
                    
        HorizontalLayout horisontalLayout2 = new HorizontalLayout();
              
        Image img = new Image();
        img.setText("img/black.png");
        img.setStyle(new Style().setStyle("width", "20px").setStyle("height", "10px"));
        //horisontalLayout.addComponent(img);
        
        Label lbl = new Label();
        lbl.setText("Form doesn't exist");
        lbl.setStyle(Style.createStyle().setStyle("color", "black"));
        horisontalLayout.addComponent(lbl);
           
        horisontalLayout2.addComponent(horisontalLayout);
        
        
        horisontalLayout = new HorizontalLayout();
        img = new Image();
        img.setText("img/orange.png");
        img.setStyle(new Style().setStyle("width", "20px").setStyle("height", "10px"));
        //horisontalLayout.addComponent(img);
        
        lbl = new Label();
        lbl.setText("Form is exist");
        lbl.setStyle(Style.createStyle().setStyle("color", "orange"));
        horisontalLayout.addComponent(lbl);
        
        horisontalLayout2.setStyle(new Style().setStyle("float", "right"));        
        horisontalLayout2.addComponent(horisontalLayout);
        nf.addComponent(horisontalLayout2);
        
        horisontalLayout = new HorizontalLayout();
        Checkbox checkbox = new Checkbox();
        checkbox.setId("replace");
        checkbox.setData(Nset.readJSON("[['replace','Replace Forms']]", true));
        checkbox.setText("[\"replace\"]");   
        horisontalLayout.addComponent(checkbox);
        
        checkbox = new Checkbox();        
        checkbox.setId("chkall");
        checkbox.setData(Nset.readJSON("[['chkall','Check All']]", true)); 
        checkbox.setOnClickListener(new Component.OnClickListener() {

            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);
                if(response.getContent().findComponentbyId("chkall").getText().contains("chkall")){
                    response.getContent().findComponentbyId("detailimport-tblForm").setText("[*]");
                }else{
                    response.getContent().findComponentbyId("detailimport-tblForm").setText("[]");
                }
                    response.refreshComponent(response.getContent().findComponentbyId("detailimport-tblForm"));
            }
        });
        
        horisontalLayout.addComponent(checkbox);
        nf.addComponent(horisontalLayout);

        
        Tablegrid tablegrid = new Tablegrid();
        tablegrid.setId("detailimport-tblForm");
        
        tablegrid.setColHide(3, true);
        tablegrid.showRowIndex(true, true);
        nf.addComponent(tablegrid);        
        
         
        /*
        tablegrid.setOnFilterClickListener(new Tablegrid.OnFilterClickListener() {
            @Override
            public void OnFilter(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);
                response.writeContent();
            }
        });
        */        
        Button btn = new Button();
        btn.setId("detailimport-btnimport");
        btn.setText("Import");
        btn.setStyle(new Style().setStyle("width", "150px").setStyle("height", "50px"));
        btn.setOnClickListener(new Component.OnClickListener() {
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                boolean replace = response.getContent().findComponentbyId("replace").getText().equals("[\"replace\"]");                            
                String readln= "";  Nset data = Nset.newArray();
                Nset check = ((Nset)response.getVirtual("@+CHECKEDROWS"));
                String user = response.getVirtualString("@+SESSION-LOGON-USER");
                try {                    
                    //BufferedReader in = new BufferedReader(new FileReader(NikitaService.getDirTmp()+NikitaService.getFileSeparator() +lblfname.getText()+(NikitaService.isModeCloud()?".temp":"tmp")));    
                    InputStream gZIPInputStream = (new FileInputStream(NikitaService.getDirTmp() +NikitaService.getFileSeparator() +lblfname.getText()+".tmp")  );    
                    if (NikitaService.isModeCloud() || lblext.getText().endsWith(".txt")) {
                        gZIPInputStream = new GZIPInputStream(  gZIPInputStream );    
                    }
                    BufferedReader in = new BufferedReader (new InputStreamReader(gZIPInputStream)) ;
                    
                    Nset id = Nset.readJSON(in.readLine());
                    Nikitaset nikitaset  = nikitaConnection.Query("SELECT MAX(formindex) FROM web_form", null);
                    int index = Utility.getInt(nikitaset.getText(0, 0));int row = 0;
                    boolean bCloud = false ;
                    String prefix = "";
                    if (NikitaService.isModeCloud()) {
                        bCloud = true;
                        prefix = NikitaService.getPrefixUserCloud(nikitaConnection, user);
                    }
                    if (id.getData("nfid").toString().equals("export")) {
                        while ((readln = in.readLine())!=null) {       
                            for (int k = 0; k < check.getArraySize(); k++) {
                                if (check.getData(k).toInteger()==row) {
                                    index++;
                                    Nset form = Nset.newArray().readJSON(readln);
                                    
                                    
                                    String userImport = user;// form.getData(6).toString() 23/05/20  
                                    String fname = form.getData(0).toString();
                                    String orgPrefix = "";
                                    if (bCloud) {                                        
                                        if (fname.startsWith(prefix)) { 
                                            orgPrefix  = prefix;
                                        }else{  
                                            if (importNoPrefix) {                                                
                                                orgPrefix  = orgPrefix(fname);
                                                fname = rePrefix(prefix, fname);//fname = prefix + fname;
                                            }else{
                                                fname = prefix + fname;
                                            }
                                            
                                        }
                                    }        
                                    
                                    if(replace){
                                        nikitaset = nikitaConnection.Query("SELECT formid FROM web_form WHERE formname=?", fname);
                                        for (int i = 0; i < nikitaset.getRows(); i++) {
                                            nikitaConnection.Query("DELETE from web_form where formid=?",nikitaset.getText(i, 0));
                                            nikitaConnection.Query("DELETE FROM sys_lock_form WHERE formid=?",nikitaset.getText(i, 0));
                                            nikitaConnection.Query("DELETE FROM web_route WHERE compid=(SELECT compid FROM  web_component WHERE formid=?)",nikitaset.getText(i, 0));
                                            nikitaConnection.Query("DELETE FROM web_component WHERE formid=?",nikitaset.getText(i, 0));
                                        }                                        
                                    }
                                    nikitaset =  nikitaConnection.Query("insert into web_form( formname,formtitle,formtype,formstyle,formdescribe,formindex,createdby) values(?,?,?,?,?,?,?)",fname,form.getData(1).toString(),form.getData(2).toString(),form.getData(3).toString(),form.getData(4).toString(),""+( index ),userImport);
                            
                                    if(dbCore == WebUtility.CORE_ORACLE){
                                        nikitaset = nikitaConnection.Query("SELECT fromid FROM web_form WHERE ROWID=?", nikitaset.getText(0, 0));
                                    }
                                    
                                        
//                                    System.err.println("COM:"+form.getData(7).getArraySize());
//                                    System.err.println("COM:"+form.getData(7).toJSON());
                                    for (int i = 0; i < form.getData(7).getArraySize(); i++) {
                                        Nset logic = form.getData(7).getData(i).getData(13);//last data is logic
                                        userImport = user;// form.getData(6).toString()  
                                        Nikitaset v = nikitaConnection.Query("insert into web_component("+
                                                  "formid,compname,compindex,comptype,complabel,comptext,comphint,"+ 
                                                  "compdefault,complist,compstyle,enable,visible,mandatory,parent,validation,createdby)"+
                                                  "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", nikitaset.getText(0, 0),
                                                  form.getData(7).getData(i).getData(0).toString(),
                                                  form.getData(7).getData(i).getData(1).toString(),
                                                  form.getData(7).getData(i).getData(2).toString(),
                                                  form.getData(7).getData(i).getData(3).toString(),
                                                  form.getData(7).getData(i).getData(4).toString(),
                                                  form.getData(7).getData(i).getData(5).toString(),
                                                  form.getData(7).getData(i).getData(6).toString(),
                                                  form.getData(7).getData(i).getData(7).toString(),
                                                  form.getData(7).getData(i).getData(8).toString(),
                                                  form.getData(7).getData(i).getData(9).toString(),
                                                  form.getData(7).getData(i).getData(10).toString(),
                                                  form.getData(7).getData(i).getData(11).toString(),
                                                  form.getData(7).getData(i).getData(12).toString(),
                                                  form.getData(7).getData(i).getData(14).toString(), //[validation]29/01/2016[14]
                                                  userImport) ;
                                                if(dbCore == WebUtility.CORE_ORACLE){
                                                    v = nikitaConnection.Query("SELECT compid FROM web_component WHERE ROWID=?", v.getText(0, 0));
                                                }
                                                String newcompid = v.getText(0, 0);  
                                                System.out.println(newcompid+":"+logic.getArraySize());
                                                for (int j = 0; j < logic.getArraySize(); j++) {
                                                    userImport = user;// form.getData(6).toString() 
                                                    String action = logic.getData(j).getData(0).toString();
                                                    if (bCloud && importNoPrefix) {      
                                                        //connection ANd showFormTask
                                                        Nset nv = Nset.readJSON(action);
                                                        String code = nv.getData("code").toString();
                                                        String classes = nv.getData("class").toString();
                                                        if(classes.equals("FormAction")){
                                                            if (code.equals("calllink")||code.equals("calltask")||code.equals("callfunction")||code.equals("showform")||code.equals("showwindows")||code.equals("showcontent")||code.equals("openwindows")||code.equals("showfinder")){
                                                                //\"formname\":\"u13_register\"  param1
                                                                action = Utility.replace(action, "\\\"formname\\\":\\\""+orgPrefix, "\\\"formname\\\":\\\""+prefix); 
                                                            }else if(code.equals("inflate")){
                                                               //\"formname\":\"u13_register\"  param3
                                                              action = Utility.replace(action, "\\\"formname\\\":\\\""+orgPrefix, "\\\"formname\\\":\\\""+prefix); 
                                                            }                                                           
                                                        }else if(classes.equals("ComponentAction")){
                                                            if ( code.equals("resultgrid")||code.equals("smartgrid") ){
                                                                //\\\"formname\\\":\\\"u14_main\\\"  param9
                                                                action = Utility.replace(action, "\\\\\\\"formname\\\\\\\":\\\\\\\""+orgPrefix, "\\\\\\\"formname\\\\\\\":\\\\\\\""+prefix); 
                                                            }                                                           
                                                        }else if(classes.equals("ConnectionAction")){
                                                            if ( code.equals("query") ){
                                                                //\"conn\":\"sample\"   param2
                                                                 action = Utility.replace(action, "\\\"conn\\\":\\\""+orgPrefix, "\\\"conn\\\":\\\""+prefix); 
                                                            }                                                            
                                                        }                                                            
                                                    }
                                                    nikitaConnection.Query("insert into web_route("+
                                                     "compid, action, expression, routeindex,createdby)"+
                                                     "values(?,?,?,?,?)",newcompid, action , logic.getData(j).getData(1).toString(),  logic.getData(j).getData(2).toString(),userImport);

                                                }//loop logic
                                    }//loop component
                                    break;
                                }//checked                                
                            }  
                            row++;
                        }//l0op form
                    }            
                    in.close();    
                } catch (Exception e) {  System.err.println(e.getMessage() ); }
                response.showDialog("Import", "Complete", "",  "OK");
                response.write();
            }
        });
        horisontalLayout = new HorizontalLayout();
        horisontalLayout.addComponent(btn);
        horisontalLayout.setStyle(new Style().setStyle("float", "right"));
        
        nf.addComponent(horisontalLayout);
        
        
        response.setContent(nf);        
        
        nf.setOnLoadListener(new NikitaForm.OnLoadListener() {

            @Override
            public void OnLoad(NikitaRequest request, NikitaResponse response, Component component) {
                fillGrid(response);
            }
        });
    }
    private static String rePrefix(String prefix, String name){
        String pattern = "^u([0-9]+)_([0-9a-zA-Z_]+)$";//"^(.+)@(.+)$";
        Pattern r = Pattern.compile(pattern);
        // Now create matcher object.
        Matcher m = r.matcher(name);//"u2121_idada_adad"
        if (m.matches()) {           
           return prefix + m.group(2);
        }else {
           return prefix + name;
        }
    }
    private static String orgPrefix(String name){
        String pattern = "^u([0-9]+)_([0-9a-zA-Z_]+)$";//"^(.+)@(.+)$";
        Pattern r = Pattern.compile(pattern);
        // Now create matcher object.
        Matcher m = r.matcher(name);//"u2121_idada_adad"
        if (m.matches()) {           
           return  "u"+m.group(1)+"_";
        }else {
           return  "";
        }
    }
    private void fillGrid(NikitaResponse response){
        Tablegrid tablegrid = (Tablegrid)response.getContent().findComponentbyId("detailimport-tblForm") ;
        Textbox txt= (Textbox)response.getContent().findComponentbyId("detailimport-txtSearch");
        String readln= "";  Nset data = Nset.newArray();
        try { 
            /*            
            if (NikitaService.isModeCloud() || lblext.getText().endsWith(".txt")) {
                try {
                    FileInputStream fileInputStream  = new FileInputStream(NikitaService.getDirTmp() +NikitaService.getFileSeparator() +lblfname.getText()+".tmp");
                    byte[] buffer = new byte[1042];  int length; 
                    ByteArrayOutputStream arrayInputStream = new ByteArrayOutputStream(1024);
                    while ((length = fileInputStream.read(buffer)) > 0) {
                            arrayInputStream.write(buffer, 0,length );
                    }  
                    fileInputStream.close();                    
                    FileOutputStream fileOutputStream = new FileOutputStream(NikitaService.getDirTmp() +NikitaService.getFileSeparator() +lblfname.getText()+".temp");
                    fileOutputStream.write(AES.decrypt(arrayInputStream.toByteArray(), "NiGnMSY"));
                    fileOutputStream.close();  
                    
                    
                } catch (Exception e) { }
            }       
            */
            InputStream gZIPInputStream = (new FileInputStream(NikitaService.getDirTmp() +NikitaService.getFileSeparator() +lblfname.getText()+".tmp")  );    
            if (NikitaService.isModeCloud() || lblext.getText().endsWith(".txt")) {
                gZIPInputStream = new GZIPInputStream(  gZIPInputStream );    
            }
            //BufferedReader in = new BufferedReader(new FileReader(NikitaService.getDirTmp() +NikitaService.getFileSeparator() +lblfname.getText()+".tmp"));    
            BufferedReader in = new BufferedReader (new InputStreamReader(gZIPInputStream)) ;
            Nset id = Nset.readJSON(in.readLine());
            
            if (id.getData("nfid").toString().equals("export")) {
                while ((readln = in.readLine())!=null) {               
                    data.addData(Nset.newArray().readJSON(readln));
                }
            }            
            in.close();    
             
        } catch (Exception e) {  System.err.println(e.getMessage() ); }
        
        tablegrid.setData(data);                    
                    
        tablegrid.setDataHeader(Nset.readsplitString("Name|Title|Type||Describe|"));
        tablegrid.setColHide("", true);
        
        Label lbl = new Label();
        lbl.setId("logic-btnLabel");
                    
            for (int i = 0; i < data.getArraySize(); i++) {
            ns = nikitaConnection.Query("SELECT formname FROM web_form WHERE "+(NikitaService.isModeCloud()?"createdby = '"+user+"' AND ":"")+" formname = ?", data.getData(i).getData(0).toString());
            String h= ns.getText(0, 0);
            if(ns.getText(0, 0).equals(data.getData(i).getData(0).toString())){                    
            }else{
                tablegrid.setChecked(i, true);
            }                     
        }
        
        response.getContent().findComponentbyId("detailimport-btnimport").setEnable(data.getArraySize()>=1?true:false);
       
        tablegrid.setAdapterListener(new IAdapterListener() {
            @Override
            public Component getViewItem(int row, int col, Component parent, Nset data) {
                HorizontalLayout horisontalLayout = new HorizontalLayout();
                Label lbl = new Label();
                lbl.setId("logic-btnLabel");
                ns = nikitaConnection.Query("SELECT formname FROM web_form WHERE "+(NikitaService.isModeCloud()?"createdby = '"+user+"' AND ":"")+" formname = ?", data.getData(row).getData(0).toString());
                String h= ns.getText(0, 0);
                if(ns.getText(0, 0).equals(data.getData(row).getData(0).toString())){           
                    lbl.setText("<a style=\"color:orange\">"+data.getData(row).getData(col).toString()+"</a>");          
                }else{
                    lbl.setText(data.getData(row).getData(col).toString()); 
                }
                
                horisontalLayout.addComponent(lbl);
                return horisontalLayout;      
            }
        });
        
        
    }
    
}
