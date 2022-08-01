/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator;

import static com.nikita.generator.Component.escapeHtml;
import com.nikita.generator.action.StringAction;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.ui.Function;
import com.nikita.generator.ui.SmartGrid;
import com.nikita.generator.ui.layout.DivLayout;
import com.nikita.generator.ui.layout.NikitaForm;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Base64Coder;
import com.rkrzmail.nikita.utility.Utility;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

/**
 *
 * @author rkrzmail
 */
public class NikitaControler {
    //single thread (1thread per nikitacontroler)
    private String formname;
    private String requestcode  =  "" ;
    private Nset args = Nset.newObject() ;
    
    public static NikitaControler getInstance(String formname){
        /*
        if (AppNikita.getInstance().getNikitaComponent(formname)!=null) {
                return AppNikita.getInstance().getNikitaComponent(formname);
        }
        else{
                return new NikitaComponent(formname);
        }
        */
        return new NikitaControler(formname);
    }
    public NikitaControler(){
        //AppNikita.getInstance().addNikitaComponent(getNikitaFormInstance(), this); 
    }
    private NikitaControler(String formname){
        if (formname!=null) {
            this.formname=formname;
        }else{
            this.formname = getClass().getSimpleName();
        }	
        //AppNikita.getInstance().addNikitaComponent(getNikitaFormInstance(), this); 
    }
    public NikitaControler(Object activity){
        this.formname=activity.getClass().getSimpleName();
        //internalstart("", Nset.newObject());
    }

    
    public void doServletAction(NikitaRequest request, NikitaResponse response){
        
        
        //actionpperformance [onloacd,oncabk,onresult,onclick,dll]        
        String action = request.getParameter("nikitaaction");         
        String compid = request.getParameter("nikitacomponentid");
        String nikitagenerator = request.getParameter("nikitageneratorengine");
        
        if (nikitagenerator.equals("")) {
            //nikita generator belum siap,
            write(createNikitaWebEngine("Nikita Generator"));
        }else{
            initializeGenerator();//generator initialize
            onCreateUI();
            
            Component component = getComponent("$#"+compid);//engine active
            if (action.equals("")||action.equals("load")) {
                onLoad();
                for (int i = 0; i < navLoad.size(); i++) {
                    if (navLoad.elementAt(i).getOnActionListener()!=null ) {
                        navLoad.elementAt(i).getOnActionListener().OnAction(this, component, action);
                    } 
                }  
                //write(getContent().);
            }else if (action.equals("result")) {
                onResult(request.getParameter("nikitarequestcode"), request.getParameter("nikitaresponsecode"), Nset.readJSON(request.getParameter("nikitaresult")));
                for (int i = 0; i < navResult.size(); i++) {
                    if (navResult.elementAt(i).getOnActionListener()!=null ) {
                        navResult.elementAt(i).getOnActionListener().OnAction(this, component, action);
                    } 
                }    
            }else if (action.equals("back")) { 
                onBack();
                for (int i = 0; i < navBack.size(); i++) {
                    if (navBack.elementAt(i).getOnActionListener()!=null ) {
                        navBack.elementAt(i).getOnActionListener().OnAction(this, component, action);
                    } 
                }           
            }else if (!component.getId().equals("")) { 
                if (component.getOnActionListener()!=null ) {
                    component.getOnActionListener().OnAction(this, component, action);
                }            
            }             
        }
        write();
    }
    public NikitaConnection getConnection(String name){
        return NikitaConnection.getConnection(name);
    }
        
    
    public String createNikitaWebEngine(String title){      
        
        StringBuilder sbuBuffer = new StringBuilder();
        sbuBuffer.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");       
        sbuBuffer.append("<html>");
        sbuBuffer.append("<head>"); 
        sbuBuffer.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\">");//<meta http-equiv="X-UA-Compatible" content="IE=8" >
        sbuBuffer.append("<meta name=\"viewport\" content=\"width=device-width, user-scalable=no, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0\">");
         
        sbuBuffer.append("<title id=\"nikitatitle\">").append(escapeHtml(title)).append("</title>");
        sbuBuffer.append("<link rel=\"shortcut icon\" href=\"").append(NikitaService.getBaseUrl()+"/static/img/icon.png").append("\" type=\"image/x-icon\" />"); 
        
        String basetheme = "";
        String theme =NikitaConnection.getDefaultPropertySetting().getData("init").getData("theme").toString();
        theme=theme.equals("")?"lightness":theme;
               
        sbuBuffer.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"static/lib/ui/").append(basetheme.equals("")?theme:basetheme).append("/jquery-ui.min.css\">");
        sbuBuffer.append("<script src=\"static/lib/maps/maps.api.v3.js\"></script> ");
              
         //combolis
        sbuBuffer.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"static/lib/css/easy/easyui.css\"/>\n");
        
        //new datagrid
        sbuBuffer.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"static/lib/css/ui.dropdownchecklist.css\"/>\n");
        sbuBuffer.append("<script type=\"text/javascript\" src=\"static/lib/jquery.easyui.min.js\"></script>" ); 
       
        sbuBuffer.append("<script type=\"text/javascript\" src=\"static/lib/jquery-1.11.1.min.js\"></script>"); 
        sbuBuffer.append("<script type=\"text/javascript\" src=\"static/lib/jquery-ui.min.js\"></script>");

        sbuBuffer.append("<script type=\"text/javascript\" src=\"static/lib/jquery.dialogextend.js\"></script>");
        sbuBuffer.append("<script type=\"text/javascript\" src=\"static/lib/nikita.js\"></script>" );

        
        sbuBuffer.append("<script type=\"text/javascript\" src=\"static/lib/jquery.form.js\"></script>");

        //combolis       
        sbuBuffer.append("<script type=\"text/javascript\" src=\"static/lib/ui.dropdownchecklist.js\"></script>\n");
        
        //collapsablelist
        sbuBuffer.append("<script type=\"text/javascript\" src=\"static/lib/collapsiblelists.js\"></script>\n");

        sbuBuffer.append("<script type=\"text/javascript\" charset=\"UTF-8\" src=\"static/lib/contentmenu.js\"></script>"); 
        sbuBuffer.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"static/lib/css/contentmenu.css\">");

        sbuBuffer.append("<link rel=\"stylesheet\" type=\"text/css\" media=\"screen\" href=\"static/lib/css/nikita.css\"/>" );   
        
        
        sbuBuffer.append("<script type=\"text/javascript\" src=\"static/lib/css/easytree/ui.easytree.js\"></script>" );
        sbuBuffer.append("<script type=\"text/javascript\" src=\"static/lib/jquery.easytree.min.js\"></script>" );
        
         
          
        sbuBuffer.append("</head>");
        sbuBuffer.append("<script type=\"text/javascript\">" );
        sbuBuffer.append(" $(document).ready(function() {"); 
        sbuBuffer.append("  first(); "); 
         
        int i =  NikitaConnection.getDefaultPropertySetting().getData("init").getData("nikitatimerbroadcast").toInteger();
        if (i>=1) { 
            sbuBuffer.append(" timerbroadcast(").append(i*1000).append("); "); 
        }  
        String s =  NikitaConnection.getDefaultPropertySetting().getData("init").getData("chat").toString().trim();
        if (s.startsWith("ws")) { 
            sbuBuffer.append(" websocket('").append(s).append("'); "); 
        }    
        if (NikitaConnection.getDefaultPropertySetting().getData("init").getData("stayonpage").toString().trim().equals("true")) { 
            sbuBuffer.append(" stayonpage(); "); 
        }          
        sbuBuffer.append("  init(); "); 
        sbuBuffer.append("  sendNikitaEngineActive(form); "); 
        
        sbuBuffer.append(" $( document ).tooltip(); "); 
        
        sbuBuffer.append(" });"); 
        sbuBuffer.append("</script>");  
        
       
        sbuBuffer.append("<body>");   
        sbuBuffer.append("</body>");
        sbuBuffer.append("</html>");
       
        int len = sbuBuffer.toString().length();
        int max = 1024*(NikitaConnection.getDefaultPropertySetting().getData("init").getData("nikitaformbuffer").toInteger()) ;//(12KB)
        if (len<max) {
            sbuBuffer.append("<!--");        
            for (int x = len; x < max; x++) {
                sbuBuffer.append(" ");   
            }   
            sbuBuffer.append("-->");  
        }       
        return sbuBuffer.toString();
    }
 
    
    private Vector<Component> refreshComponent  = new Vector<Component>();
    public void refreshComponent(Component comp){
        if (refreshComponent.contains(comp)) {
            refreshComponent.remove(comp);
        }
        refreshComponent.addElement(comp);
    } 
    private void write(){
          
        refreshComponent.removeAllElements();
    }
    public void write(String text){
                
    }
    public void write(byte[] data){
                
    }
    private HttpRequest request;
    private HttpResponse response;
    
    
    
    
    private Vector<Component> navLoad  = new Vector<Component>();
    private Vector<Component> navBack  = new Vector<Component>();
    private Vector<Component> navResult  = new Vector<Component>();
    
    private Nset getRestoreData(){
        return Nset.newArray();
    }
    public void onCreateUI(){
        Nset restore = getRestoreData();
        for (int i = 0; i < restore.getArraySize(); i++) {
            Component comp = ComponentManager.createComponent(restore, i);   
            
        }
       
    }
    public void onRestoreInstanceState() {
        
    }
    public void onSaveInstanceState() {
        
    }
    public void initializeGenerator() {     
        NikitaConnection nikitaConnection =  getConnection(NikitaConnection.LOGIC);
        Nikitaset nikitaset = nikitaConnection.Query("SELECT * FROM web_component WHERE formid = ? ORDER BY compindex ASC;", "");
        NikitaForm nf = new NikitaForm("");
        nf.setText("");            
        nf.setName("");

        /*
        if (logic.getFormStyle().contains(":")||logic.getFormStyle().contains("=")) {
            Style style = Style.createStyle(logic.getFormStyle()) ;    
            nf.setStyle(style);
            if (logic.getFormStyle().contains("stayonpage:true")) {
                nf.setStayOnPage(true);
            }
        }              
        */
        navLoad.removeAllElements();
        navResult.removeAllElements();
        navBack.removeAllElements();
        
        Hashtable<String, Component> hashtable = new Hashtable<String, Component>();
        Vector<Component> components = new Vector<Component>(); 
        for (int i = 0; i < nikitaset.getRows(); i++) {

            Component comp = ComponentManager.createComponent(nikitaset, i);       
            components.addElement(comp);
            hashtable.put(comp.getName(), comp);
            Nikitaset ns = nikitaConnection.QueryPage(1, 1, "SELECT routeindex from web_route WHERE compid = ? ;", comp.getId());
            if (ns.getRows()>=1 ) {
                comp.setOnClickListener(new Component.OnClickListener() {
                    public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                    }
                });                
            }

            //fill default
            if (nikitaset.getText(i, "compdefault").startsWith("@")||nikitaset.getText(i, "compdefault").startsWith("&")||nikitaset.getText(i, "compdefault").startsWith("$")) {
                //comp.setText(request.getParameter(nikitaset.getText(i, "compdefault").substring(1)));
                comp.setText( getVirtualString(nikitaset.getText(i, "compdefault") ));
            }  
            if (nikitaset.getText(i, "comptext").startsWith("@")||nikitaset.getText(i, "comptext").startsWith("&")||nikitaset.getText(i, "comptext").startsWith("$")) {
                //comp.setText(request.getParameter(nikitaset.getText(i, "comptext").substring(1)));
                comp.setText( getVirtualString(nikitaset.getText(i, "comptext") ));
            } 


            //fill data
            if (nikitaset.getText(i, "complist").startsWith("@")||nikitaset.getText(i, "complist").startsWith("&")||nikitaset.getText(i, "complist").startsWith("$")) {
                //comp.setData(Nset.readJSON(request.getParameter(nikitaset.getText(i, "complist").substring(1))));
                Object o = getVirtual( nikitaset.getText(i, "complist") );
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

            //fill style
            if (nikitaset.getText(i, "compstyle").contains(":")||nikitaset.getText(i, "compstyle").contains("=") ) {
                Style style = Style.createStyle(nikitaset.getText(i, "compstyle")) ;    
                comp.setStyle(style);
            }

            if (nikitaset.getText(i, "comptype").equals("navload")) {
                navLoad.addElement(comp);
            }else if (nikitaset.getText(i, "comptype").equals("navready")) {
                //navReady.addElement(comp);
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
        
        setContent(nf);
    }
    public void onLoad() {
        if (getContent()!=null && getContent().getOnLoadListener()!=null) {
            getContent().getOnLoadListener().OnLoad(null, null, getContent());
        }  
    }
    public void onResult(String requestCode, String resultCode, Nset data) {
        if (getContent()!=null && getContent().getOnActionResultListener()!=null) {
            getContent().getOnActionResultListener().OnResult(null, null, getContent(), requestCode, resultCode, data);
        }   
    }
    public void onBack() {
        if (getContent()!=null && getContent().getOnBackListener()!=null) {
            getContent().getOnBackListener().OnBack(null, null, getContent());
        }                
    }
      
    
    
    
    private NikitaForm nikitaForm;
    public void setContent(NikitaForm  nf){
        this.nikitaForm=nf;
    }
    public NikitaForm getContent(){
        return  this.nikitaForm;
    }
     
    public NikitaServlet getActivity(){
        return null;
    }
    
    
    
    
    
    private Nset param ;
    private Hashtable virtual = new Hashtable();
    public String getParameter(String key){
    if (param!=null) {
            return param.getData(key).toString();
            }
    return "";
    }
    public void setParameter(String key, String data){
    if (param==null) {
            param=Nset.newObject();
            }
    param.setData(key, data);
    }

    public boolean expression;
    public void setInterupt(boolean b){
       setVirtualRegistered("@+INTERUPT", b?"true":"false");
    }
    public void setLogicClose(boolean b){
            getVirtualString("@+INTERUPT").equals("true");
    }

    public boolean isInterupted(){
       return getVirtualString("@+INTERUPT").equals("true"); 
    }
    private int irow = 0;
    private int lcount = 0;
    public void setCurrentRow(int i){
        irow=i;
    }
    public void setLoopCount(int m){
        lcount=m;
    }    
    public int getCurrentRow(){
        return irow;
    }
    public int getLoopCount(){
        return lcount;
    }
    public Component getComponent(String key){
        if (key.contains("[")) {
            key=key.substring(0,key.indexOf("["));
        }
        Component comp=null;        
        if (getContent()==null) {
        	comp=null;
        }else if (key.startsWith("$#")) {
            comp = getContent().findComponentbyId(key.substring(2));
        }else if (key.startsWith("$")) {  
            comp = getContent().findComponentbyName(key.substring(1));
        }
        
        
    
        return comp!=null?comp:new Component();
        
    }
    private String getCookieOrSessionKey(String key){
        if (key.startsWith("@+SESSION")) {
            if (key.startsWith("@+SESSION.")) {
                key = key.substring(10);
            }else if (key.startsWith("@+SESSION-")) {
                key = key.substring(10);
            }else{
                key = key.substring(2);
            }  
        }else if (key.startsWith("@+COOKIE")) {  
            if (key.startsWith("@+COOKIE.")) {
                key = key.substring(9);
            }else if (key.startsWith("@+COOKIE-")) {
                key = key.substring(9);
            }else{
                key = key.substring(2);
            }       
        }          
        return  key;
    }
    public void setVirtualRegistered(String key, Object data){
        if (key.startsWith("@+SESSION")) {
        	    //Utility.getSetting(AppNikita.getInstance(), getCookieOrSessionKey(key), (String)data);      
            return;
        }else if (key.startsWith("@+COOKIE")) {
        	 	//Utility.getSetting(AppNikita.getInstance(), getCookieOrSessionKey(key), (String)data);      
            return;
        }else if (key.startsWith("@+")||key.startsWith("@-")) {
            AppNikita.getInstance().setVirtual(key, data);  
        }
    }
    public void setVirtual(String key, Object data){
        if (key.equals("")) {
        }else if (key.startsWith("@+SESSION")||key.startsWith("@+COOKIE")||key.startsWith("@+AUTHENTICATION")||key.startsWith("@+AUTHENTICATION")) {
            //Utility.setSetting(AppNikita.getInstance(), getCookieOrSessionKey(key), (String)data); 
            return;
        }else if (key.startsWith("@+")||key.startsWith("@-")) {
            setVirtualRegistered(key, data);
            return;
        }else  if (key.startsWith("@")||key.startsWith("!")) {
            virtual.put(key, data);  
        }        
    }  
    
	private Object getVirtualStream(String key){
		String var = key;
		if ( key.startsWith("!") ) {
            if (!key.contains("[")) {
                key=key+"[\"data\",0,\""+key.substring(1).trim()+"\"]";
            }else if (key.contains("[")&& key.contains("[")) {
                String index = key.substring(key.indexOf("[")+1);
                key=key.substring(0,key.indexOf("["));
                if (index.contains("]")) {
                    index=index.substring(0,index.indexOf("]"));
                }
                key=key+"[\"data\","+index+",\""+key.substring(1).trim()+"\"]";
            }
        }
		
		if ( key.startsWith("@#") ) {
            return key.substring(2);
		}else if (key.startsWith("@+CORE")) {    
			return AppNikita.getInstance().getVirtual(key);
		 }else if (key.startsWith("@+SETTING-")) {
             //return com.nikita.mobile.utility.Utility.getSetting(AppNikita.getInstance(), key.substring(10), "");   
        }else if (key.startsWith("@+SESSION")) {
              //return com.nikita.mobile.utility.Utility.getSetting(AppNikita.getInstance(), key, "");           
        }else if (key.startsWith("@+COOKIE")) {    
        	//  return com.nikita.mobile.utility.Utility.getSetting(AppNikita.getInstance(), key, "");       
        }else if (key.startsWith("@+CHECKEDROWS")) {  
            /*
            key=key.substring(13);
            if (key.startsWith(".")) {
                key=key.substring(1);
            }
            if (key.startsWith("[")) {
                key=key.substring(1);
            }
            if (key.endsWith("]")) {
                key=key.substring(0, key.length()-1);
            }
            if (key.startsWith("$#")) {
                Component cm = getContent().findComponentbyId(key.substring(2));
                if (cm instanceof Tablegrid) {                    
                    return ((Tablegrid)cm).getRowChecked();
                }
            }else if (key.startsWith("$")) {
                Component cm = getContent().findComponentbyName(key.substring(1));
                if (cm instanceof Tablegrid) {                    
                    return ((Tablegrid)cm).getRowChecked();
                }
            }else if (key.length()>=2) {
                Component cm = getContent().findComponentbyId(key);
                if (cm instanceof Tablegrid) {                    
                    return ((Tablegrid)cm).getRowChecked();
                }    
            }else if (key.equals("")) {
                for (int i = 0; i < getContent().getComponentCount(); i++) {
                    if (getContent().getComponent(i) instanceof Tablegrid) {                    
                        return ((Tablegrid)getContent().getComponent(i)).getRowChecked();
                    }
                }     
            }
            */
        }else if (key.equals("@+COREMYSQL")) {
            return NikitaConnection.CORE_MYSQL;
        }else if (key.equals("@+COREORACLE")) {
            return NikitaConnection.CORE_ORACLE;
        }else if (key.equals("@+CORESQLSERVER")) {
            return NikitaConnection.CORE_SQLSERVER;
        }else if (key.equals("@+CORESQLITE")||key.equals("@+CORESQLLITE")) {
            return NikitaConnection.CORE_SQLITE; 
        }else if (key.equals("@+BUTTON1")) {
            return "button1";
        }else if (key.equals("@+BUTTON2")) {
            return "button2";
        }else if (key.startsWith("@+BUTTONGRID")) {
            try {
                String sact = getParameter("action");
                if (sact.startsWith("item-")) {
                    sact=sact.substring(5);
                    if (sact.contains("-")) {
                        sact=sact.substring(sact.indexOf("-")+1);
                    }else{
                        sact="";
                    }
                }else{
                    sact="";
                }
                return sact;    
            } catch (Exception e) {}
            return "" ;
        }else if (key.equals("@+ENTER")) {
            return "\r\n";
        }else if (key.equals("@+SPACE")) {
            return " ";
        }else if (key.equals("@+SPACE32")) {
            return "                                ";
        }else if (key.equals("@+TAB")) {
            return "\t";
        }else if (key.equals("@+EMPTYSTRING")) {
            return "";
        }else if (key.equals("@+VERSION")) {
            return "MOBILE 1.0.15 Beta";//MOBILE DEKSTOP
        }else if (key.equals("@+FORMTITLE")) { 
        	
        	return virtual.get(key);
        }else if (key.equals("@+FORMNAME")) {         
        	return virtual.get(key);
        }else if (key.equals("@+DEVICEOS")) {
              
            return "ANDROID";    
        }else if (key.equals("@+DEVICENAME")) {  
            
            return "MOBILE-ANDROID" ;
        }else if (key.equals("@+DEVICEINFO")) {
        	return "MOBILE-ANDROID" ;
        }else if (key.equals("@+NOW")) {
            return Utility.Now();
        }else if (key.equals("@+TIME")) {
            return System.currentTimeMillis();    
        }else if (key.equals("@+RANDOM")) {
            StringBuffer sb = new StringBuffer();
            Random randomGenerator = new Random();
            for (int idx = 1; idx <= 16; ++idx){
                sb.append( randomGenerator.nextInt(100)  );
            }
            return sb.toString();
        }else if (key.equals("@+FILESEPARATOR")) {    
            return  "/";
        }else if (key.equals("@+UNIQUE")) {    
            return  System.currentTimeMillis()+"";
        }else if (key.startsWith("$")) {
            if (getContent()!=null) {
                Component comp ; String ky = "";
                if (key.contains("[")&& key.contains("]")) {
                    ky=key.substring( key.indexOf("["));
                    key=key.substring(0,  key.indexOf("["));
                    
                    ky=Utility.replace(ky, "[", "");
                    ky=Utility.replace(ky, "]", "");
                    ky=Utility.replace(ky, "\"", "");
                }
                
                ky=ky.trim();
                key=key.trim();
                        
                if (key.startsWith("$#")) {
                    comp = getContent().findComponentbyId(key.substring(2));
                }else{   
                    comp = getContent().findComponentbyName(key.substring(1));
                }
                
                if (comp==null) {
                    comp= new Component();
                }
                
                if (ky.equals("tag")) {
                    return  comp.getTag();
                }else if (ky.equals("visible")) {
                    return comp.isVisible()?"true":"false";
                }else if (ky.equals("enable")) {
                    return comp.isEnable()?"true":"false";   
                }else if (ky.equals("id")) {
                    return comp.getId();  
                }else if (ky.equals("name")) {
                    return comp.getName();  
                }else if (ky.equals("data")) {
                    return comp.getData()!=null?comp.getData().toJSON():"";  
                }else if (ky.equals("style")) {
                    return comp.getViewStyle();  
                }else if (ky.equals("class")) {
                    return comp.getViewClass();
                }else if (ky.equals("attribut")) {
                    return comp.getViewAttribut();
                }
                return comp.getText();
            }
        }else if (key.startsWith("@+")||key.startsWith("@-")) {
        	 return AppNikita.getInstance().getVirtual(key);
        }else if (key.startsWith("@")||key.startsWith("!")) {
            String stream ="";
            if ( key.contains("[")) {                
                stream=key.substring( key.indexOf("["));
                key=key.substring(0,  key.indexOf("["));
            } 
             
            key=key.trim();
            stream=stream.trim();
            Object obj = virtual.get(key);
            if (key.equals("@")) {
            	obj = "";
			}            
            if (obj==null) {
                try {
                    return getParameter(key.substring(1));    
                } catch (Exception e) {} 
            }  else if (obj instanceof  Nset && !stream.equals("")) {
                stream=runArrayStream(stream);
                return ((Nset)obj).get(stream);
            }  else if (obj instanceof  Nikitaset && !stream.equals("")) {
                stream=runArrayStream(stream);
                return ((Nikitaset)obj).getStream(stream);
            }  else if (obj instanceof  String && !stream.equals("")) {
                stream=runArrayStream(stream);
                return StringAction.getStringStream(((String)obj), stream);
            }          
            return obj;        
        }else if (key.startsWith("&")) {
            try {
                return getParameter(key.substring(1));    
            } catch (Exception e) {}
        }else if (key.startsWith("!")||key.startsWith("'")) {
            return key.substring(1);
        }
        return var;
	}
	private Nset fillStreamNset(Nset stream){
        if (stream.isNsetArray()) {
            Nset out = Nset.newArray();
            for (int i = 0; i < stream.getArraySize(); i++) {
                out.addData(  fillStreamNset(stream.getData(i))  );                
            }
            return out;
        }else if (stream.isNsetObject()) {
            Nset out = Nset.newObject();
            String[] keys = stream.getObjectKeys();
            for (int i = 0; i < keys.length; i++) {
                out.setData(keys[i], fillStreamNset(stream.getData(keys[i])) );
            }
            return out;
        }else{
            if (stream.toString().startsWith("@")||stream.toString().startsWith("$")||stream.toString().startsWith("!")) {
                Object obj = getVirtual(stream.toString());
                if (obj instanceof Nset) {
                    obj =  ((Nset)obj)  ;                        
                }else if (obj instanceof String) {
                    obj =  (String)obj  ;
                }else if (obj instanceof Double) {
                    obj =    (Double)obj  ;
                }else if (obj instanceof Integer) {
                    obj =   (Integer)obj  ;
                }else if (obj instanceof Long) {
                    obj =  (Long)obj  ;
                }else if (obj instanceof Boolean) {
                    obj =  (Boolean)obj  ;
                }else if (obj instanceof Vector) {  
                    obj = (((Vector)obj))   ;   
                }else if (obj instanceof Hashtable) {  
                    obj =  (((Hashtable)obj))  ;
                }else{
                    obj = "";
                }       
                return new Nset(obj);
            }else {  
                return stream;
            }     
        }        
    }
	private String runArrayStream(String stream){
        if (stream.contains("@")||stream.contains("$")||stream.contains("!")) {
            Nset n = Nset.readJSON(stream);
            Nset out = Nset.newArray();
            for (int i = 0; i < n.getArraySize(); i++) {
                if (n.getData(i).toString().startsWith("@")||n.getData(i).toString().startsWith("$")||n.getData(i).toString().startsWith("!")) {
                    Object obj = getVirtual(n.getData(i).toString());
                    if (obj instanceof Nset) {
                        out.addData(  (Nset)obj  );                        
                    }else if (obj instanceof String) {
                        out.addData(  (String)obj  );
                    }else if (obj instanceof Double) {
                        out.addData(  (Double)obj  );
                    }else if (obj instanceof Integer) {
                        out.addData(  (Integer)obj  );
                    }else if (obj instanceof Long) {
                        out.addData(  (Long)obj  );
                    }else if (obj instanceof Boolean) {
                        out.addData(  (Boolean)obj  );
                    }else if (obj instanceof Vector) {  
                        out.addData(  (Vector)obj  );   
                    }else if (obj instanceof Hashtable) {  
                        out.addData(  (Hashtable)obj  );
                    }else if (obj == null) {  
                        out.addData(  n.getData(i).toString()  );
                    }else{
                        out.addData(  obj.toString()  );
                    }
                }else {  
                    out.addData(n.getData(i).toString());
                }                
            }      
            return out.toJSON();
        }
        return stream;
    }
	public Object getVirtual(String key){
		if ( (key.startsWith("@[") && key.endsWith("]") ) || (key.startsWith("@{") && key.endsWith("}"))) {
			return Nset.readJSON(key.substring(1));
		}
		 
		
		String f = "";
        if ( (key.startsWith("!") && key.contains("(") ) || (key.startsWith("@") && key.contains("(") ) || (key.startsWith("$") && key.contains("("))) {
            f = key.substring(key.lastIndexOf("(")+1);//yang terakhir
            if (key.contains(")")) {
                f=f.substring(0,f.indexOf(")"));
            }
            key=key.substring(0,key.indexOf("("));
        }   
        f=f.trim();
         
        
        Object reObject = getVirtualStream(key);
        if (f.startsWith("$")) {//"$"
            //new function
            Component component = getComponent(f);
            if (component instanceof Function) {
                //component.setOn               
                Function function = ((Function)component);
                if (reObject instanceof Nikitaset) {
                    function.setText("");
                }else if (reObject instanceof Nset) {
                    function.setText("");
                }else if (reObject instanceof String) { 
                    function.setText((String)reObject);
                }else if (reObject instanceof Long) {   
                    function.setText("");
                }else if (reObject instanceof Integer) {
                    function.setText("");
                }else if (reObject instanceof Double) {  
                    function.setText("");
                }else if (reObject instanceof Boolean) {  
                    function.setText("");
                }else{
                    function.setText("");
                }                
                //function.runRouteAction("function");
                return function.getText();
            }                    
        }else if (f.equals("integer")||f.equals("int")||f.equals("inc")||f.equals("dec")) {
            int inc = f.equals("inc")?1:0;  inc = f.equals("dec")?-1:inc;
            
            if (reObject instanceof String) {               
                return Utility.getInt((String)reObject);
            }else if (reObject instanceof Integer) {
                return (Integer)reObject;
            }else if (reObject instanceof Long) {
                return (Integer)reObject;
            }else if (reObject instanceof Double) {
                return Utility.getInt(  (Double)reObject +"");
            }else if (reObject == null) {
                return 0;
            }
            return Utility.getInt(reObject.toString());
        }else if (f.equals("curr")||f.equals("fcurrview")) { 
            if (reObject instanceof String) {
                return Utility.formatCurrency((String)reObject);
            }else if (reObject instanceof Integer) {
                return Utility.formatCurrency(((Integer)reObject)+"");
            }else if (reObject instanceof Long) {
                return Utility.formatCurrency(((Integer)reObject)+"");
            }else if (reObject instanceof Double) {
                return Utility.formatCurrency(((Double)reObject)+"");
            }else if (reObject == null) {
                return 0;
            }
            return Utility.formatCurrency((reObject.toString())) ;
        }else if (f.equals("num")) { 
            if (reObject instanceof String) {
                return Utility.getNumber((String)reObject);
            }else if (reObject instanceof Integer) {
                return Utility.getNumber(((Integer)reObject)+"");
            }else if (reObject instanceof Long) {
                return Utility.getNumber(((Integer)reObject)+"");
            }else if (reObject instanceof Double) {
                return Utility.getNumber(((Double)reObject)+"");
            }else if (reObject == null) {
                return 0;
            }
            return Utility.getNumber((reObject.toString())) ;
        }else if (f.equals("name")) {
            return key;
        }else if (f.equals("exec")) {
        	String key2 = getVirtualString(key);
            if (key2.startsWith("@")||key2.startsWith("$")) {
                return getVirtual(key2);
            }
            return "";
        }else if (f.equals("long")) { 
            if (reObject instanceof String) {
                return Utility.getLong((String)reObject);
            }else if (reObject instanceof Integer) {
                return (Integer)reObject;
            }else if (reObject instanceof Long) {
                return (Long)reObject;
            }else if (reObject instanceof Double) {
                return Utility.getLong(  (Double)reObject +"");
            }else if (reObject == null) {
                return 0;
            }
            return Utility.getLong(reObject.toString());
        }else if (f.equals("double")) { 
            if (reObject instanceof String) {
                return Utility.getDouble((String)reObject);
            }else if (reObject instanceof Integer) {
                return (Integer)reObject;
            }else if (reObject instanceof Long) {
                return (Integer)reObject;
            }else if (reObject instanceof Double) {
                return (Double)reObject;
            }else if (reObject == null) {
                return 0;
            }
            return Utility.getDouble(reObject.toString());
        }else if (f.equals("json")) {
            if (reObject instanceof String) {
                return  Nset.newArray().addData((String)reObject).toJSON();
            }else if (reObject instanceof Nikitaset) {
                return  ((Nikitaset)reObject).toNset().toJSON() ;
            }else if (reObject instanceof Nset) {
                return  ((Nset)reObject).toJSON() ;
            }
        }else if (f.equals("csv")||f.equals("comma")) {
            if (reObject instanceof String) {
                return  Nset.readJSON((String)reObject).toCsv();
            }else if (reObject instanceof Nikitaset) {
                return  ((Nikitaset)reObject).toNset().toCsv() ;
            }else if (reObject instanceof Nset) {
                return  ((Nset)reObject).toCsv() ;
            }  
        }else if (f.equals("string")) {  
            if (reObject instanceof String) {
                return  (String)reObject;
            }else if (reObject instanceof Nikitaset) {
                return  ((Nikitaset)reObject).toNset().toString() ;
            }else if (reObject instanceof Nset) {
                return  ((Nset)reObject).toString() ;
            }            
        }else if (f.equals("trim")) {  
            if (reObject instanceof String) {
                return  ((String)reObject).trim();
            }else if (reObject instanceof Nikitaset) {
                return  ((Nikitaset)reObject).toNset().toString().trim() ;
            }else if (reObject instanceof Nset) {
                return  ((Nset)reObject).toString().trim() ;
            }
        }else if (f.startsWith("escape")||f.startsWith("unescape")||f.startsWith("encode")||f.startsWith("decode")) {  
            String buffer = "";
            if (reObject instanceof String) {
                buffer =  StringEscapeUtils.escapeSql((String)reObject );
            }else if (reObject instanceof Nikitaset) {
                buffer =  StringEscapeUtils.escapeSql(((Nikitaset)reObject).toNset().toString()) ;
            }else if (reObject instanceof Nset) {
                buffer =  StringEscapeUtils.escapeSql(((Nset)reObject).toString()) ;
            }   
            if (f.equals("escapesql")) {
               return  StringEscapeUtils.escapeSql(buffer) ; 
            }else if (f.equals("escapehtml")) {
               return  StringEscapeUtils.escapeHtml(buffer) ; 
            }else if (f.equals("escapejs")) {
               return  StringEscapeUtils.escapeJavaScript(buffer) ; 
            }else if (f.equals("escapejava")) {
               return  StringEscapeUtils.escapeCsv(buffer) ; 
            }else if (f.equals("escapecsv")) {
               return  StringEscapeUtils.escapeJava(buffer) ; 
            //==============================================//
            }else if (f.equals("unescapehtml")) {
               return  StringEscapeUtils.unescapeHtml(buffer) ; 
            }else if (f.equals("unescapejs")) {
               return  StringEscapeUtils.unescapeJava(buffer) ; 
            }else if (f.equals("unescapejava")) {
               return  StringEscapeUtils.unescapeJavaScript(buffer) ; 
            }else if (f.equals("unescapecsv")) {
               return  StringEscapeUtils.unescapeCsv(buffer) ; 
            //==============================================//
            }else if (f.equals("encodeurl")) {
               return URLEncoder.encode( buffer   )   ; 
            }else if (f.equals("decodeurl")) {
                return URLDecoder.decode(buffer   )   ; 
            }else if (f.equals("encodebase64")) {                 
               return  Base64Coder.encodeString(buffer)  ; 
            }else if (f.equals("decodebase64")) {
               return   Base64Coder.decodeString(buffer)  ; 
            }
        }else if (f.equals("md5")) {  
            if (reObject instanceof String) {
                return  Utility.MD5((String)reObject);
            }else if (reObject instanceof Nikitaset) {
                return  Utility.MD5(((Nikitaset)reObject).toNset().toString()) ;
            }else if (reObject instanceof Nset) {
                return  Utility.MD5(((Nset)reObject).toString()) ;
            }
        }else if (f.equals("nset")) {
            if (reObject instanceof String) {
                return  Nset.readJSON((String)reObject);
            }else if (reObject instanceof Nikitaset) {
                return  ((Nikitaset)reObject).toNset() ;
            }else if (reObject instanceof Nset) {
                return   ((Nset)reObject)  ;
            }
        }else if (f.equals("filltonset")) {            
            if (reObject instanceof String) {
                return   fillStreamNset(Nset.readJSON((String)reObject));
            }else if (reObject instanceof Nikitaset) {
                return   fillStreamNset(((Nikitaset)reObject).toNset()) ;
            }else if (reObject instanceof Nset) {
                return   fillStreamNset((Nset)reObject)  ;
            }        
        }else if (f.equals("nikitaset")) {
            if (reObject instanceof String) {
                Nset n = Nset.readJSON((String)reObject);
                if (Nikitaset.isNikitaset(n)) {
                    return new Nikitaset(n);
                }else{
                    return  n;
                }
            }else if (reObject instanceof Nikitaset) {
                return  ((Nikitaset)reObject).toNset() ;
            }else if (reObject instanceof Nset) {
                Nset n =  ((Nset)reObject) ;
                if (Nikitaset.isNikitaset(n)) {
                    return new Nikitaset(n);
                }else{
                    return  n;
                }
            }
        }else if (f.equals("type")) {
            if (reObject instanceof String) {
                return  "string";
            }else if (reObject instanceof Nikitaset) {
                return  "nikitaset" ;
            }else if (reObject instanceof Nset) {
                return  "nset"  ;
            }else if (reObject instanceof Integer) {
                return  "integer"  ;
            }else if (reObject instanceof Long) {
                return  "long"  ;
            }else if (reObject instanceof Double) {
                return  "double"  ;     
            }else {
                return  ""  ;     
            }
        }else if (f.equals("newarray")) {
            Nset n =Nset.newArray();
            setVirtual(key, n);
            return n;
        }else if (f.equals("newobject")) { 
            Nset n =Nset.newObject();
            setVirtual(key, n);
            return n;
        }else if (f.equals("newstring")) { 
            setVirtual(key, "");
            return "";
        }else if (f.equals("newint")||f.equals("newlong")||f.equals("newfloat")||f.equals("newdecimal")||f.equals("newdouble")) { 
            setVirtual(key, 0);
            return 0;
        }else if (f.equals("fdate")||f.equals("fdatenumber")||f.equals("fdateint")||f.equals("fdatelong")) {
            if (reObject instanceof String) {
                return Utility.getDateTime((String)reObject);
            }else if (reObject instanceof Long) {
                return  (Long)reObject;
            }
        }else if (f.equals("fdateview")) {
            if (reObject instanceof String) {
                long l = Utility.getDate((String)reObject);
                if (l!=0) {
                    return Utility.formatDate(l, "dd/MM/yyyy");
                }
                //return DateFormatAction.FormatDate(-1, (String)reObject, "");
            }else if (reObject instanceof Long) {
                long l = (Long)reObject;
                if (l!=0) {
                    return Utility.formatDate(l, "dd/MM/yyyy");
                }
            }
        }else if (f.equals("fdatetimeview")) { 
            if (reObject instanceof String) {
                long l = Utility.getDateTime((String)reObject);
                if (l!=0) {
                    return Utility.formatDate(l, "dd/MM/yyyy HH:mm:ss");
                }
            }else if (reObject instanceof Long) {
                long l = (Long)reObject;
                if (l!=0) {
                    return Utility.formatDate(l, "dd/MM/yyyy HH:mm:ss");
                }
            }
            //return DateFormatAction.FormatDate(-1, reObject.toString(), Utility.NowTime());
        }else if (f.equals("fdatedb")||f.equals("todate")) {   
            if (reObject instanceof String) {
                long l = Utility.getDate((String)reObject);
                if (l!=0) {
                    return Utility.formatDate(l, "yyyy-MM-dd");
                }
            }else if (reObject instanceof Long) {
                long l = (Long)reObject;
                if (l!=0) {
                    return Utility.formatDate(l, "yyyy-MM-dd");
                }
            }
            //return DateFormatAction.FormatDate(-1, reObject.toString(), Utility.NowTime());
        }else if (f.equals("fdatetimedb")||f.equals("todatetime")) {   
            //yyyy-mm-dd hh:nn:ss
            if (reObject instanceof String) {
                long l = Utility.getDateTime((String)reObject);
                if (l!=0) {
                    return Utility.formatDate(l, "yyyy-MM-dd HH:mm:ss");
                }
             }else if (reObject instanceof Long) {
                long l = (Long)reObject;
                if (l!=0) {
                    return Utility.formatDate(l, "yyyy-MM-dd HH:mm:ss");
                }
            }
       //return DateFormatAction.FormatDate(-1, reObject.toString(), Utility.NowTime());
        }else if (f.equals("fdatetmaxdb")||f.equals("todatetmax")) {   
            //yyyy-mm-dd hh:nn:ss
            if (reObject instanceof String) {
                long l = Utility.getDateTime((String)reObject);
                if (l!=0) {
                    return Utility.formatDate(l, "yyyy-MM-dd") + " 23:59:59";
                }
             }else if (reObject instanceof Long) {
                long l = (Long)reObject;
                if (l!=0) {
                    return Utility.formatDate(l, "yyyy-MM-dd") + " 23:59:59";
                }
            }
        }else if (f.equals("fdatetnowdb")||f.equals("todatetnow")) {   
            //yyyy-mm-dd hh:nn:ss
            if (reObject instanceof String) {
                long l = Utility.getDateTime((String)reObject);
                if (l!=0) {
                    return Utility.formatDate(l, "yyyy-MM-dd") + Utility.Now().substring(10);
                }
             }else if (reObject instanceof Long) {
                long l = (Long)reObject;
                if (l!=0) {
                    return Utility.formatDate(l, "yyyy-MM-dd") + Utility.Now().substring(10);
                }
            }
        }else if (f.equals("fdatetmmindb")||f.equals("todatetmin")) {   
            //yyyy-mm-dd hh:nn:ss
            if (reObject instanceof String) {
                long l = Utility.getDateTime((String)reObject);
                if (l!=0) {
                    return Utility.formatDate(l, "yyyy-MM-dd") + " 00:00:00";
                }
             }else if (reObject instanceof Long) {
                long l = (Long)reObject;
                if (l!=0) {
                    return Utility.formatDate(l, "yyyy-MM-dd") + " 00:00:00";
                }
            }        
        }else if (f.equals("length")||f.equals("rows")) {
            if (reObject instanceof String) {
                return  ((String)reObject).length();
            }else if (reObject instanceof Nikitaset) {
                return  ((Nikitaset)reObject).getRows();
            }else if (reObject instanceof Nset) {
                return  ((Nset)reObject).getArraySize()>=0 ?((Nset)reObject).getArraySize():((Nset)reObject).getObjectKeys().length  ;
            }else if (reObject instanceof Integer) {
                return  (""+((Integer)reObject) ).length();
            }else if (reObject instanceof Long) {
                return  (""+((Long)reObject) ).length();
            }else if (reObject instanceof Double) {
                return  (""+((Double)reObject) ).length();    
            }else {
                return  "0"  ;     
            }
        }else if (f.equals("error")) {
            if (reObject instanceof String) {
                return  "";
            }else if (reObject instanceof Nikitaset) {
                return  ((Nikitaset)reObject).getError();
            }else if (reObject instanceof Nset) {
                return "";
            }else{
                return  ""  ;      
            }
        }else if (f.equals("header")||f.equals("headernset")) {
            if (reObject instanceof Nikitaset) {
                return  new Nset(((Nikitaset)reObject).getDataAllHeader());
            }else{
                return  Nset.newArray() ;      
            }
        }else if (f.equals("data")||f.equals("datanset")) {
            if (reObject instanceof Nikitaset) {
                return  new Nset(((Nikitaset)reObject).getDataAllVector());
            }else{
                return  Nset.newArray()  ;      
            }
        }
        return reObject;
	}
	public String getVirtualString(String key){
        Object obj = getVirtual(key);
        return obj!=null?obj.toString():"";
	}
	public boolean isComponent(String key){
	        return key.startsWith("$");
    }
    public boolean isVirtual(String key){
        return key.startsWith("@");
    }
    
    public static final String NIKITA_SHOWDIALOG_KEY_NAME = "NIKITA-SHOWDIALOG-DATA";
    
    private Nset getBufferDialogData(){
    	if (AppNikita.getInstance().getVirtual(NIKITA_SHOWDIALOG_KEY_NAME) instanceof Nset) {
    		return (Nset)AppNikita.getInstance().getVirtual(NIKITA_SHOWDIALOG_KEY_NAME) ;
		}
    	return Nset.newObject();
    }
    public void showDialog(String title, String message, String button1, String button2, String button3, String reqcode, Nset data) {
        if (getActivity()!=null) {
            
        }		
    }
   
    
    
}
