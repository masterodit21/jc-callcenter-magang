/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator;

import com.nikita.generator.action.WebAction;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.ui.Document;
import com.nikita.generator.ui.Receiver;
import com.nikita.generator.ui.SmartGrid;
import com.nikita.generator.ui.Tablegrid;
import com.nikita.generator.ui.Textbox;
import com.nikita.generator.ui.layout.DivLayout;
import com.nikita.generator.ui.layout.NikitaForm;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.data.Nson;
import com.rkrzmail.nikita.utility.Utility;
import java.io.FileOutputStream;
import java.util.Hashtable;
import java.util.Vector;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
/**
 * created by 13k.mail@gmail.com
 */
public class NikitaServlet {
    private NikitaEngine engine;
    
    public boolean isPrivateName(){
        return false;
    }
    public String getVersion(){
        return "1.0.0";
    }
    
    protected void runFirst(NikitaRequest request, NikitaResponse response, NikitaLogic logic, NikitaEngine nikitaEngine){
        if (nikitaEngine!=null && engine==null) {
            engine = nikitaEngine;
        }
        if (request.getHttpServletRequest().getContentType()!=null && request.getHttpServletRequest().getContentType().equalsIgnoreCase("nikita/nikitaconnection")) {
            String data = request.getParameter(Utility.MD5("nikitaconnection").toLowerCase());
            
            Nset n = Nset.readJSON(data);
            request.setParameter("sql", n.getData("sql").toString());
            request.setParameter("paging", n.getData("paging").toString());           
            
            request.setParameter("username", n.getData("username").toString());
            request.setParameter("password", n.getData("password").toString());
        }
        OnRun(request, response, logic);
    }
    public void OnRun(NikitaRequest request, NikitaResponse response, NikitaLogic logic){
        //System.out.println(Thread.currentThread().getName());
        boolean isMultipart = ServletFileUpload.isMultipartContent(request.getHttpServletRequest());
        if (logic!=null && isMultipart) {
            if (response.handleFileMultipart(request)) {
                return;
            }
        }
        
        if (logic!=null && engine==null){
            //engine inisialite in hire         
            //engine = new NikitaEngine(response.getConnection(NikitaConnection.LOGIC),   logic.getFormName(), request.getParameter("runtime").equalsIgnoreCase("developer"));
            engine = NikitaEngineManager.getInstance(response.getConnection(NikitaConnection.LOGIC),   logic.getFormName(),  request.getParameter("runtime").equalsIgnoreCase("developer") );
            response.setNikitaEngine(engine);
        }
                
        
         
        
        //nikitawilly
        String generator = NikitaConnection.getDefaultPropertySetting().getData("init").getData("generator").toString();
        if (generator.equals("file")) {
            //hide tag & text if visible=false
            //save variable
        }
        
        response.clearInitLoad();//add for replaca onready
        if (logic!=null && (logic.getFormType().equals("link")||logic.getFormType().equals("scheduler")) ) {
            
            NikitaConnection nikitaConnection = response.getConnection(NikitaConnection.LOGIC);
            //  Nikitaset nikitaset = nikitaConnection.Query("SELECT * FROM web_component WHERE formid = ? ORDER BY compindex ASC;", logic.getFormId());
            Nikitaset nikitaset = engine.getComponents();
            
            Nset n = new Nset(request.getInternalParameter());
            String[] key = n.getObjectKeys();
 
            for (int i = 0; i < key.length; i++) {
                 response.setVirtual("@"+key[i], n.getData(key[i]).toString());
            }
            execLogic(request, response, logic, nikitaset.getText(0, "compid"));
            if ( response.isContainData() && response.isConsumed()==false ) {
                response.write();
            }
        }else if (logic!=null && (logic.getFormType().equals("mobileform")) ) {
            //NikitaService.showPage("Mobile Form", "Nikita Mobile Application", 200, response.getHttpServletResponse());
            NikitaService.getResource("/img/z1.png", request.getHttpServletRequest(), response.getHttpServletResponse());
        }else{
            //13k 19102015
            response.setCurrentNikitaViewV3( request.setNikitaBufferData(response.getVirtualString("@+NIKITAID")) );            
            OnCreate(request, response, logic);
            
            if (request.getParameter("link").equals("link")) {            
            } else if (request.getParameter("action").equals("")) {                        
                        
                //mobileActivity
                if (response.getContent()!=null) {
                    String[]  keys = response.getMobileActivityStream().getData(response.getContent().getName()).getObjectKeys();
                    for (int i = 0; i < keys.length; i++) {
                        Component comp = response.getContent().findComponentbyName(keys[i]);
                        if (comp!=null) {
                            Nset n = response.getMobileActivityStream().getData(response.getContent().getName());
                            comp.setText(    n.getData(keys[i]).getData(0).toString() );//array 0 text
                            comp.setVisible( n.getData(keys[i]).getData(1).toString().equals("1") );//array 1 Visible
                            comp.setEnable(  n.getData(keys[i]).getData(2).toString().equals("1") );//array 2 Enable
                        }
                    }
                }
               
                if (request.getParameter("findex")!=null ) {
                    String a= request.getParameter("findex");
                    if (response.getContent()!=null &&  request.getParameter("findex").contains(NikitaEngine.NFID_ARRAY_INDEX)) {
                        if (!response.getContent().getName().contains(NikitaEngine.NFID_ARRAY_INDEX)) {
                            response.getContent().setName(response.getContent().getName()+NikitaEngine.NFID_ARRAY_INDEX +NikitaEngine.getFormIndex(request.getParameter("findex"))  ) ;
                        }
                    }                          
                }               
                
                WebAction.latchreadWeb(request, response);//23/01/2017 
                //if (NikitaConnection.getDefaultPropertySetting().getData("init").getData("smod").toString().contains("a")) {                    
                //}else{
                //    WebAction.latchreadWeb(request, response);//23/01/2017 
                //}                        
                //==================================//                
                OnAction(request, response, logic, "", "");//load           
                //if (NikitaConnection.getDefaultPropertySetting().getData("init").getData("smod").toString().contains("a")) {
               //     WebAction.latchreadWeb(request, response);//23/01/2017 
                //}
                
                
                
                if (!response.getVirtualString("@NEW-INSTANCE").equals("")&& response.getContent()!=null) {
                   response.getContent().setInstanceId(""+response.getVirtualString("@NEW-INSTANCE"));
                }  
                if (!response.isConsumed() && response.getContent()!=null) {
                    response.setVirtual("@+SESSION-NIKITAID", response.getVirtualString("@+NIKITAID"));
                    response.getContent().setBaseTheme(response.getVirtualString("@+SESSION-THEME"));
                    response.getContent().setInitLoad(response.getInitLoad());//addd for init              
  
                    NikitaViewV3 nikitaViewV3  = NikitaViewV3.create();
                    response.writeStream(response.getContent().getViewHtml(nikitaViewV3));     
                    nikitaViewV3.save( response.getVirtualString("@+NIKITAID") , response.getContent().getFileNikita());
                    
                }
                response.setVirtual("@NEW-INSTANCE","");
            }else{
                if (response.getContent()!=null) {
                    if (!response.getContent().isNoRestoreData()) {
                        //response.getContent().restoreData(Nset.readJSON(Component.unescapeHtml(request.getParameter("data"))));
                        response.getContent().restoreData(request.getNikitaBufferData());
                         //02/02/2017
                        if (response.getContent().getInstanceId().contains(NikitaEngine.NFID_ARRAY_INDEX)) {
                            if (!response.getContent().getName().contains(NikitaEngine.NFID_ARRAY_INDEX)) {
                                response.getContent().setName(response.getContent().getName()+NikitaEngine.NFID_ARRAY_INDEX+NikitaEngine.getFormIndex(response.getContent().getInstanceId()));
                            }
                        }

                    }                    
                } 
                
                //13k 28102015 (reload is not found file
                if ( response.getVirtualString("@+SESSION-NIKITAID").equals("") ) {
                    response.reloadBrowser();
                    response.write();return;
                }                    
                
                OnAction(request, response, logic, request.getParameter("component"), request.getParameter("action"));
                if ( response.isContainData()) {
                    response.write( );                     
                }           
            }
        }  
    }   
    
    private void saveFileNikita(NikitaResponse response, boolean clear){
        //nikitawilly
        String generator = NikitaConnection.getDefaultPropertySetting().getData("init").getData("generator").toString();
        if (generator.equals("file")) {
            //hide tag & text if visible=false
            //save variable
           if (response.getContent()!=null) {
               String fnikita= response.getContent().getFileNikita();
               try {
                   //new FileOutputStream(NikitaConnection.getDefaultPropertySetting().getData("init").getData("generator").toString()+NikitaService.getFileSeparator()+"a");
               } catch (Exception e) {  }
           }
        }
    }
    private final Component.OnClickListener listener = new Component.OnClickListener() {  public void OnClick(NikitaRequest request, NikitaResponse response, Component component) { }                        }; 
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic){
        if (logic!=null) {
            NikitaConnection nikitaConnection =  response.getConnection(NikitaConnection.LOGIC);
              //Nikitaset nikitaset = nikitaConnection.Query("SELECT * FROM web_component WHERE formid = ? ORDER BY compindex ASC;", logic.getFormId());
            Nikitaset nikitaset = engine.getComponents();
             
            
            NikitaForm nf = new NikitaForm(logic.getFormName());//new NikitaForm(logic.getFormId());
            nf.setText(logic.getFormTitle());            
            nf.setName(logic.getFormName());
            
            
            if (logic.getFormStyle().contains(":")||logic.getFormStyle().contains("=")) {
                Style style = Style.createStyle(logic.getFormStyle()) ;    
                nf.setStyle(style);
                if (logic.getFormStyle().contains("stayonpage:true")) {
                    nf.setStayOnPage(true);
                }
            }              
            
            final Vector<Component> navLoad  = new Vector<Component>();
            final Vector<Component> navReady  = new Vector<Component>();
            final Vector<Component> navResult  = new Vector<Component>();
            final Vector<Component> navBack  = new Vector<Component>();
            
            Hashtable<String, Component> hashtable = new Hashtable<String, Component>();
            Vector<Component> components = new Vector<Component>(); 
            for (int i = 0; i < nikitaset.getRows(); i++) {
 
                Component comp = ComponentManager.createComponent(nikitaset, i);       
                components.addElement(comp);
                hashtable.put(comp.getName(), comp);
                /*
                if (nikitaConnection.QueryPage(1, 1, "SELECT routeindex from web_route WHERE compid = ? ;", comp.getId()).getRows()>=1 ) {
                    comp.setOnClickListener(new Component.OnClickListener() {
                        public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                        }
                    });
                }
                */
                
                //fill style
                if (nikitaset.getText(i, "compstyle").contains(":")||nikitaset.getText(i, "compstyle").contains("=") ) {
                    Style style = Style.createStyle(nikitaset.getText(i, "compstyle")) ;    
                    comp.setStyle(style);
                    
                    if (style.getInternalAttr().containsKey("tooltip")) {                        
                        comp.setTooltip(style.getInternalAttr().getData("tooltip").toString());
                        style.getInternalAttr().removeByKey("tooltip");
                    }
                }
                              
                
                //fillaction
                if (engine.isLogicAll()) {
                    if (engine.getComponentLogic(comp.getId()).getRows()>=1) {
                        comp.setOnClickListener(listener);
                    }
                }else{
                    comp.setOnClickListener(listener);
                }
                
                if (comp.getStyle()!=null && comp.getStyle().getInternalStyle().containsKey("n-noaction") && comp.getStyle().getInternalStyle().getData("n-noaction").toString().equalsIgnoreCase("true")) {
                    comp.setOnClickListener(null);
                }
                
                //fill default
                if (nikitaset.getText(i, "compdefault").startsWith("@")||nikitaset.getText(i, "compdefault").startsWith("&")||nikitaset.getText(i, "compdefault").startsWith("$")) {
                    //comp.setText(request.getParameter(nikitaset.getText(i, "compdefault").substring(1)));
                    comp.setText( response.getVirtualString(nikitaset.getText(i, "compdefault") ));
                }  
                if (nikitaset.getText(i, "comptext").startsWith("@")||nikitaset.getText(i, "comptext").startsWith("&")||nikitaset.getText(i, "comptext").startsWith("$")) {
                    //comp.setText(request.getParameter(nikitaset.getText(i, "comptext").substring(1)));
                    comp.setText( response.getVirtualString(nikitaset.getText(i, "comptext") ));
                } 
                                
                
                //fill data
                if (nikitaset.getText(i, "complist").startsWith("@")||nikitaset.getText(i, "complist").startsWith("&")||nikitaset.getText(i, "complist").startsWith("$")) {
                    //comp.setData(Nset.readJSON(request.getParameter(nikitaset.getText(i, "complist").substring(1))));
                    Object o = response.getVirtual( nikitaset.getText(i, "complist") );
                    if (o instanceof Nset) {
                        comp.setData((Nset)o);
                    }else if (o instanceof Nikitaset) {
                        comp.setData(new Nset(((Nikitaset)o).getDataAllVector()));
                    }else{
                        comp.setData(Nset.readJSON(o.toString()) );
                    }
                } else if(nikitaset.getText(i, "complist").startsWith("[")|| nikitaset.getText(i, "complist").startsWith("{")){
                    comp.setData(Nset.readJSON(nikitaset.getText(i, "complist")));
                } else if(nikitaset.getText(i, "complist").contains("|")){
                    comp.setData(Nset.readsplitString(nikitaset.getText(i, "complist"),"|"));
                } else if(nikitaset.getText(i, "complist").contains(",")){
                    comp.setData(Nset.readsplitString(nikitaset.getText(i, "complist"),","));
                }
                //custom data for dvi
                if (comp instanceof DivLayout) {
                    comp.setData(Nset.newArray().addData( nikitaset.getText(i, "complist")).addData( "" ));
                }
                                
                
                if (nikitaset.getText(i, "comptype").equals("navload")) {
                    navLoad.addElement(comp);
                }else if (nikitaset.getText(i, "comptype").equals("navready")) {
                    navReady.addElement(comp);
                }else if (nikitaset.getText(i, "comptype").equals("navresult")) {   
                    navResult.addElement(comp);
                }else if (nikitaset.getText(i, "comptype").equals("navback")) {
                    navBack.addElement(comp);
                }
            }  
            //flat to tree[1]
            for (int i = 0; i < components.size(); i++) {
                String parent =components.elementAt(i).getParentName().trim();
                parent=parent.startsWith("$") ? parent.substring(1):parent;
                
                if (parent.equals(components.elementAt(i).getName())) {
                    components.elementAt(i).setParentName("");
                }else if (!parent.equals("")) {
                    if ( hashtable.get(parent) instanceof ComponentGroup) {
                        ((ComponentGroup)hashtable.get(parent)).addComponent(components.elementAt(i));
                    }else{
                        components.elementAt(i).setParentName("");
                    }
                }else{
                    components.elementAt(i).setParentName("");
                }
            }            
            //flat to tree[2]
            for (int i = 0; i < components.size(); i++) {
                if (components.elementAt(i).getParentName().equals("")) {
                    nf.addComponent(components.elementAt(i));
                }                
            } 
            
            //group load,ready,result
            if ( navReady.size() >=1) {
                nf.setOnReadyListener(new NikitaForm.OnReadyListener() {
                    public void OnReady(NikitaRequest request, NikitaResponse response, Component component) {
                        for (int i = 0; i < navReady.size(); i++) {
                            if (navReady.elementAt(i).getOnClickListener()!=null) {
                                execLogic(request, response, response.getNikitaLogic(), navReady.elementAt(i).getId());
                            }                                
                        }
                    }
                });
            }
            if ( navLoad.size() >=1) {
                nf.setOnLoadListener(new NikitaForm.OnLoadListener() {
                    public void OnLoad(NikitaRequest request, NikitaResponse response, Component component) {
                        boolean b = response.isConsumed;
                        if (navLoad.size()>=1) {
                            response.setWriteLock(true);
                        }
                        for (int i = 0; i < navLoad.size(); i++) {
                            if (navLoad.elementAt(i).getOnClickListener()!=null) {
                                execLogic(request, response, response.getNikitaLogic(), navLoad.elementAt(i).getId());
                            }                                
                        } 

                        if (navLoad.size()>=1) {
                            response.setWriteLock(false);
                            response.isConsumed=b;
                        }

                    }
                });
            }
            if (navResult.size()>=1) {                
                nf.setOnActionResultListener(new NikitaForm.OnActionResultListener() {
                    public void OnResult(NikitaRequest request, NikitaResponse response, Component component, String reqestcode, String responsecode, Nset result) {
                        response.setVirtualRegistered("@+RESULT", result);
                        response.setVirtualRegistered("@+REQUESTCODE", reqestcode);
                        response.setVirtualRegistered("@+RESPONSECODE", responsecode);
 

                        for (int i = 0; i < navResult.size(); i++) {
                            if (navResult.elementAt(i).getOnClickListener()!=null) {   
                                execLogic(request, response, response.getNikitaLogic(), navResult.elementAt(i).getId());
                            }                                
                        }
                         
                         
                        
                        
                        response.write();
                    }
                });
            }
            if ( navBack.size() >=1) {
                nf.setOnBackListener(new NikitaForm.OnBackListener() {
                    public void OnBack(NikitaRequest request, NikitaResponse response, Component component) {
                        for (int i = 0; i < navBack.size(); i++) {
                            if (navBack.elementAt(i).getOnClickListener()!=null) {
                                execLogic(request, response, response.getNikitaLogic(), navBack.elementAt(i).getId());
                            }                                
                        }
                    }
                });
            }
            
            nf.setBackConsume(navBack.size()>=1);            
             
            
            response.setContent(nf);
            nf.setNikitaEngine(engine);
        }       
    }
    
    public static Nson getLinkArgument(String link){
        NikitaEngine engine=new NikitaEngine(NikitaConnection.getConnection(NikitaConnection.LOGIC), link, false);
        return getLinkArgument(engine);
    }
    public static Nson getLinkArgument(NikitaEngine engine){        
        Nson nson = Nson.newObject();
        if (engine!=null) {
            for (int i = 0; i < engine.getComponents().getRows(); i++) {
                Nikitaset nikitaset = engine.getComponentLogic(engine.getComponents().getText(i, "compid"));
                for (int j = 0; j < nikitaset.getRows(); j++) {
                    Nset actn = Nset.readJSON(nikitaset.getText(j, "action"));
                     //DefinitionAction.arg
                    if (actn.get("class").toString().equals("DefinitionAction")&& actn.get("code").toString().equals("arg")) {
                        for (int k = 0; k < 10; k++) {
                            String v = actn.get("args").get("param"+(k+1)).toString().trim();
                            if (v.startsWith("&")) {
                                v = v.substring(1);
                            }
                            if (v.startsWith("$")) {
                                v = v.substring(1);
                            }
                            if (v.startsWith("@")) {
                                v = v.substring(1);
                            }
                            if (v.trim().length()>=1) {
                                nson.setData(v, "");
                            }                            
                        }
                    }
                }
            }
        } else{
            return Nson.newObject().setData("status", "404");
        }   
        return Nson.newObject().setData("status", "200").setData("args", nson.getObjectKeys()) ;
    }
    
    public static void execLogicComponent(NikitaForm form, NikitaRequest request, NikitaResponse response, NikitaLogic logic, String compid){ 
        internallRunLogic(request, response, logic, compid,  form.getNikitaEngine() );
    }
    public void execLogic(NikitaRequest request, NikitaResponse response, NikitaLogic logic, String compid){         
        internallRunLogic(request, response, logic, compid,  engine);
    }
    private static void internallRunLogic(NikitaRequest request, NikitaResponse response, NikitaLogic logic, String compid, NikitaEngine engine){         
        if (logic!=null) {
            response.setNikitaLogic(logic);
            
            int ir = logic.getCurrentRow();
            int lr = logic.getLoopCount();
            boolean b =logic.expression;
            
            
            NikitaConnection nikitaConnection =  response.getConnection(NikitaConnection.LOGIC);
            //Nikitaset nikitaset = nikitaConnection.Query("SELECT expression,action,routeindex FROM web_route WHERE compid = ? ORDER BY routeindex ASC;", compid);
            Nikitaset nikitaset = engine.getComponentLogic(compid);
            
            response.setVirtual("@EXPESSION", false);
            logic.expression=false;
            logic.setLoopCount(0);           
            
     
            for (int i = 0; i < nikitaset.getRows(); i++) {
                logic.setCurrentRow(i);
                response.setVirtualRegistered("@+LOGICCOUNT", logic.getLoopCount());
                response.setVirtualRegistered("@+LOGICCOUNTB1", logic.getLoopCount()+1);
                response.setCurrCompId(compid);//fill
                
                if (!response.isInterupted() ) {  
                    Nset expr = Nset.readJSON(nikitaset.getText(i, "expression"));
                    Nset actn = Nset.readJSON(nikitaset.getText(i, "action"));
                    if ( expr.get("flag").toString().equals("hide")||(expr.get("class").toString().equals("") && actn.get("class").toString().equals("")) ) {
                        //abaikan dan lanjut next logic [flag=hide or exxpresion=accion=emtyclass]
                    }else if ( (actn.get("class").toString().equals("")&& actn.get("code").toString().equals("") && !actn.get("args").get("param1").toString().equals(""))  ) {
                        //abaikan dan lanjut next logic [comment dan commnet tidak kosong, alias ada sisinya]
                    }else if ( NikitaService.runExpressionClass(expr, request, response, logic)) {
                        logic.expression=true;
                        response.setVirtual("@EXPESSION", true);    
                        response.setVirtual(expr.getData("result").toString(), true);
                        response.setCurrCompId(compid);//refill
                        if (!NikitaService.runActionClass(actn, request, response, logic)) {
                            break;
                        }
                        if (logic.getCurrentRow()!=i) {
                            i = logic.getCurrentRow()-1;
                            logic.expression=false;
                            response.setVirtual("@EXPESSION", false);
                            response.setVirtual(expr.getData("result").toString(), false);
                        }                        
                    }else{
                        logic.expression=false;
                        response.setVirtual("@EXPESSION", false);
                        response.setVirtual(expr.getData("result").toString(), false);
                    }            
                }
            } 
            
            logic.expression=b;
            logic.setCurrentRow(ir);
            logic.setLoopCount(lr);
            response.setVirtualRegistered("@+LOGICCOUNT", logic.getLoopCount());
            response.setVirtualRegistered("@+LOGICCOUNTB1", logic.getLoopCount()+1);            
        }
        
    }
    public void OnAction(NikitaRequest request, NikitaResponse response, NikitaLogic logic, String component, String action){
        response.setNikitaLogic(logic);//first
        
        if (response.getContent()!=null) {
            if (!action.equals("")) {//OnRestore first
                if (response.getContent().getOnRestoredListener()!=null) {
                    response.getContent().getOnRestoredListener().OnRestore(request, response, response.getContent());
                }  
            }            
             
            if (!action.equals("") && response.getMobileGenerator()!=null && response.getMobileGenerator().containsKey(response.getContent().getName())) {
                request.setParameter("webformexist", "true");
            }else{
                request.setParameter("webformexist", "");
            }  

            if (action.equalsIgnoreCase("")||component.equalsIgnoreCase("")) { 
                //load dibawah
                /*
                if (response.getMobileGenerator()!=null && response.getMobileGenerator().containsKey(response.getContent().getName())) {                    
                }else{
                    Vector<Component> v = response.getContent().populateComponentbyType(null, Receiver.class.getSimpleName());
                    for (int i = 0; i < v.size(); i++) {
                        if (v.elementAt(i).getText().equalsIgnoreCase("nikitawebonaction")) {
                            execLogic(request, response, logic, v.elementAt(i).getId());
                        }                
                    }     
                } 
                */
            }else{
                Vector<Component> v = response.getContent().populateComponentbyType(null, Receiver.class.getSimpleName());
                for (int i = 0; i < v.size(); i++) {
                    if (v.elementAt(i).getText().equalsIgnoreCase("nikitawebonaction")) {
                        execLogic(request, response, logic, v.elementAt(i).getId());
                    } 
                    response.setInterupt(false);
                    response.setLogicClose(false);
                }      
            }
            if (action.equals("documentrotateleft")||action.equals("documentrotateright")) {
                if (response.getContent()!=null) {
                    Component comp = response.getContent().getComponentbyId(component);
                    if (comp instanceof Document) {
                        Document document = (Document)comp;
                        document.rotate(action);      
                        
                        String s = document.getText();
                        document.setText(s);
                        
                        response.refreshComponent(document);
                        return;
                    }
  
                }
            }         
            
            if (action.equals("result")) {
                if (response.getContent()!=null) {
                    Nset n = Nset.readJSON(request.getParameter("reqrescode"));
                    Nset result = Nset.readJSON(Component.unescapeHtml(request.getParameter("result")));
                    //add for finder 13/02/2015 -edit 30102015
                    if (n.getData("responsecode").toString().equals("FINDER")) {
                        String[]  keys = result.getObjectKeys();
                        for (int i = 0; i < keys.length; i++) {
                            String key = keys[i];//key/component/variable
                            String res = result.getData(key).toString();//data
                            if (key.equals("")) {                                
                            }else if (key.startsWith("@")) {
                                response.setVirtual(key, res);
                            }else{
                                key = (key.startsWith("$")?"":"$")+key;
                                Component c = response.getComponent(key);
                                c.setText(res);
                                if (key.contains(c.getName())) {
                                    response.refreshComponent(c);
                                }
                            }        
                        }
                        /*
                        for (int i = 1; i < 5; i++) {
                            String res = result.getData("fincomp"+i).toString();
                            if (res.equals("")) {                                
                            }else if (res.startsWith("@")) {
                                response.setVirtual(res, result.getData("result"+i).toString());
                            }else{
                                res = (res.startsWith("$")?"":"$")+res;
                                Component c = response.getComponent(res);
                                c.setText(result.getData("result"+i).toString());
                                if (res.contains(c.getName())) {
                                    response.refreshComponent(c);
                                }
                            }                            
                        }
                        */
                    } //end       
                }
                if (response.getContent().getOnActionResultListener()!=null) {
                    Nset n = Nset.readJSON(request.getParameter("reqrescode"));
                    Nset result = Nset.readJSON(Component.unescapeHtml(request.getParameter("result")));
                    response.getContent().getOnActionResultListener().OnResult(request, response, response.getContent(), n.getData("requestcode").toString(),n.getData("responsecode").toString(), result);
                                
                }
            }else if (action.equals("ready")) {
                if (response.getContent().getOnReadyListener()!=null) {
                    response.getContent().getOnReadyListener().OnReady(request, response, response.getContent());
                } 
            }else if (action.equals("back")||action.equals("close")) {
                if (response.getContent().getOnBackListener()!=null) {
                    response.getContent().getOnBackListener().OnBack(request, response, response.getContent());
                } 
            }else if (action.equals("")) {//onload
                if (true) {//response.getContent().getOnLoadListener()!=null
                    //add for iniload
                    boolean b = response.isConsumed;   
                    response.setWriteLock(true);
                    
                    if (response.getContent()!=null && response.getContent().getOnLoadListener()!=null) {                                              
                        response.getContent().getOnLoadListener().OnLoad(request, response, response.getContent());//original
                        response.setInterupt(false);
                        response.setLogicClose(false);
                    }
                    if (response.getMobileGenerator()!=null && response.getMobileGenerator().containsKey(response.getContent().getName())) {                    
                        Vector<Component> v = response.getContent().populateComponentbyType(null, Receiver.class.getSimpleName());
                        for (int i = 0; i < v.size(); i++) {
                            if (v.elementAt(i).getText().equalsIgnoreCase("nikitawebonaction")) {
                                execLogic(request, response, logic, v.elementAt(i).getId());
                            }   
                            response.setInterupt(false);
                            response.setLogicClose(false);
                        }     
                    } else {
                        
                    }
                    
                    response.setWriteLock(false);
                    response.isConsumed=b;
                }
            }else{
                response.setVirtualRegistered("@+GRIDACTION", "");
                response.setVirtualRegistered("@+SELECTEDROW", "");
                response.setVirtualRegistered("@+GRIDROW", "");
                response.setVirtualRegistered("@+GRIDCOL", "");
                
                if ( action.startsWith("item-")) {
                    String row = action.substring(5);
                    if (row.contains("-")) {
                        row=row.substring(0,row.indexOf("-"));
                    }                    
                    request.setParameter("item-row", row);
                    response.setVirtualRegistered("@+SELECTEDROW", row);
                    response.setVirtualRegistered("@+GRIDROW", row);
                    
                }else if (action.startsWith("select-")) {   
                    String row = action.substring(7);
                    if (row.contains("-")) {
                        row=row.substring(0,row.indexOf("-"));
                    }                    
                    request.setParameter("item-row", row);
                    response.setVirtualRegistered("@+SELECTEDROW", row);
                    response.setVirtualRegistered("@+GRIDROW", row);
                    
                    response.setVirtualRegistered("@+GRIDACTION", "select");
                }else if (action.equals("broadcast")) {
                    //Nset n = Nset.readJSON(request.getParameter("reqrescode"));
                    Nset result = Nset.readJSON(Component.unescapeHtml(request.getParameter("result")));
                    response.setVirtualRegistered("@+RESULT", result);
                }else  if ( action.startsWith("actiongrid-")||action.startsWith("actioncell-")||action.startsWith("cellselect-")) {
                    String sact=action.substring(11);//actiongrid-[row]-[col]-[action]
                    if (sact.contains("-")) {
                        String row  =  sact.substring( 0, sact.indexOf("-")  );
                        request.setParameter("item-row", row);
                        response.setVirtualRegistered("@+GRIDROW", row);
                        response.setVirtualRegistered("@+SELECTEDROW", row);
                        
                        sact=sact.substring(sact.indexOf("-")+1);    
                        if (sact.contains("-")) {
                            String col  =  sact.substring( 0, sact.indexOf("-")  );
                            response.setVirtualRegistered("@+GRIDCOL", col);
                            sact=sact.substring(sact.indexOf("-")+1);   
                          
                            response.setVirtualRegistered("@+GRIDACTION", sact); 
                        }  else{
     
                            response.setVirtualRegistered("@+GRIDCOL", sact);
                            response.setVirtualRegistered("@+GRIDACTION", ""); 
                        }                  
                    }
                }else if ( action.startsWith("page-sort-")) {
                    String sact=action.substring(10);
                    if (sact.contains("-")) {
                        String col  = sact.substring( 0, sact.indexOf("-")  );
                        response.setVirtualRegistered("@+GRIDSORT", col);
                        response.setVirtualRegistered("@+GRIDCOL", col);

                        sact=sact.substring(sact.indexOf("-")+1);//asc|desc
                        response.setVirtualRegistered("@+GRIDORDER", sact);
                        
                        response.setVirtualRegistered("@+GRIDACTION", "sort");
                    }
                }                
                
                Component comp = response.getContent().findComponentbyId(component);
                 
                
                execLogic(request, response, logic, request.getParameter("component"));
                if (comp!=null && (comp instanceof Tablegrid)) {
                    if ( action.startsWith("item-")) {
                        if (comp.getOnClickListener()!=null) {
                            String row = action.substring(5);
                            if (row.contains("-")) {
                                row=row.substring(0,row.indexOf("-"));
                            } 
                            request.setParameter("item-row", row);
                            ((Tablegrid)comp).restoreData(Utility.getInt(row));
                            comp.getOnClickListener().OnClick(request, response, comp);
                        }                     
                    } else if (((Tablegrid)comp).getOnFilterClickListener()!=null) {
                        if ( ((Tablegrid)comp).prepareFilter(action) ) {
                            ((Tablegrid)comp).getOnFilterClickListener().OnFilter(request, response, comp);
                        }
                    }  
                }else if (comp!=null && (comp instanceof SmartGrid)) {
                    if ( action.startsWith("item-")||action.startsWith("select-")||action.startsWith("actiongrid-")||action.startsWith("actioncell-")||action.startsWith("cellselect-")) {
                        if (comp.getOnClickListener()!=null) {
                            ((SmartGrid)comp).setItemRow(Utility.getInt( response.getVirtualString("@+GRIDROW")  ));
                            comp.getOnClickListener().OnClick(request, response, comp);
                        }  
                    } else if (((SmartGrid)comp).getOnFilterClickListener()!=null) {
                        
                        if ( ((SmartGrid)comp).onActionFilter(action, request.getParameter("nikitanewaction")) ) {
                            ((SmartGrid)comp).getOnFilterClickListener().OnFilter(request, response, comp);
                        }
                    } 
                }else if (comp!=null && comp.getOnClickListener()!=null) {
                    comp.getOnClickListener().OnClick(request, response, comp);
                }else{
                    if (response.getContent().getOnClickListener()!=null) {
                        response.getContent().getOnClickListener().OnClick(request, response, new Component());
                    }
                }
            }            
        }else{
            execLogic(request, response, logic, request.getParameter("component"));
            //bukan Nikitaform, contahnya schedule or link service
        }        
    }
    
}
