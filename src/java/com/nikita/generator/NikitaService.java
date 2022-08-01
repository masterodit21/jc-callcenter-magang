/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nikita.generator;
/**
 * created by 13k.mail@gmail.com
 */
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.nikita.generator.action.IAction;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.connection.NikitaInternet;
import com.nikita.generator.expression.IExpression;
import com.nikita.generator.storage.NikitaStorage;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.data.Nson;
import com.rkrzmail.nikita.utility.About;
import com.rkrzmail.nikita.utility.Utility;
import com.servlet.btask;
import com.servlet.btaskcall;
import com.servlet.mobile.mzharasyncdbclient;
import com.servlet.mobile.mzharasyncfileclient;
import com.servlet.webform;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.text.DateFormatSymbols;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.servlet.AsyncContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.http.HttpResponse;


/**
 *
 * @author rkrzmail
 */

public class NikitaService extends HttpServlet{
    class LocalVariable {
        String BASE_URL;
        String BASE_CONTEXT;
        String BASE_THEME;
        String SERVER_NAME;
        int SERVER_PORT;
        String SERVER_SCHEME;
        Nset NIKITAGENERATOR_DATA;
        String NIKITAGENERATOR_THEME;
        String FORM_NAME;
        String NIKITAGENERATOR_LOGIC;//18/04/2020
        
    }
    //public static String BASE_URL = "http://localhost:8084/NikitaWeb";
    //public static String BASE_CONTEXT = "";
    static final ThreadLocal<LocalVariable> sThreadLocal = new ThreadLocal<LocalVariable>();
    public static final ThreadLocal<String> sLinkName = new ThreadLocal<String>();
    public static String getBaseNikitaTheme(){
        if (sThreadLocal.get()!=null) {
            return (sThreadLocal.get().NIKITAGENERATOR_THEME);
        } 
        return  "";
    }
    public static String getBaseTheme(){
        if (sThreadLocal.get()!=null) {
            return (sThreadLocal.get().BASE_THEME);
        } 
        return  "";
    }
    public static Nset getBaseNikitaGeneratorData(){
        if (sThreadLocal.get()!=null) {
            return (sThreadLocal.get().NIKITAGENERATOR_DATA);
        } 
        return  Nset.newObject();
    }
    public static String getBaseUrl(){
        if (sThreadLocal.get()!=null) {
            return String.valueOf(sThreadLocal.get().BASE_URL);
        } 
        return  "";
    }
    public static String getBaseContext(){        
        if (sThreadLocal.get()!=null) {
            return String.valueOf(sThreadLocal.get().BASE_CONTEXT);
        } 
        return  "";
    }
    public static String getBaseRequestServer(){        
        if (sThreadLocal.get()!=null) {
            return String.valueOf(sThreadLocal.get().SERVER_NAME);
        } 
        return  "";
    }
    public static int getBaseRequestPort(){        
        if (sThreadLocal.get()!=null) {
            return  (sThreadLocal.get().SERVER_PORT);
        } 
        return  80;
    }
    private String getNikitaUrlProxy(String url){
        if (NikitaConnection.getDefaultPropertySetting().getData("nikitaurlproxy").containsKey(url)) {
            return NikitaConnection.getDefaultPropertySetting().getData("nikitaurlproxy").getData(url).toString();
        }
        return url;//nikitaurlproxy
    }
    private void setLocalBase(String url, String context,  String scheme,  String servername, int portnumber, Nset nikitageneratordata, String nikitatheme, String basetheme , String bLogic){     
        LocalVariable lv;
        if (sThreadLocal.get()!=null) {
            lv = sThreadLocal.get();
        } else{
            lv = new LocalVariable();
        }
        lv.BASE_URL = getNikitaUrlProxy (url);
        lv.BASE_CONTEXT = context;   
        lv.BASE_THEME = basetheme;   
        lv.SERVER_NAME = servername;
        lv.SERVER_PORT = portnumber;
        lv.SERVER_SCHEME = scheme;
        lv.NIKITAGENERATOR_THEME = nikitatheme;                        
        lv.NIKITAGENERATOR_DATA = nikitageneratordata;  
        lv.NIKITAGENERATOR_LOGIC = bLogic;
        sThreadLocal.set(lv);
    }         
    public static volatile long imaxcurrent = 0 ;
    public static volatile long iconcurrent = 0 ;
    public static volatile long ithread = 0 ;
    public static volatile long imaxthread = 0 ;
    
    public static volatile long irescount = 0 ;
    public static volatile long istaticrescount = 0 ;
    public static volatile long imobilecount = 0 ;
    public static volatile long starteddate = System.currentTimeMillis() ;
    
    public static volatile long icount = 0;    
    private static volatile long iserv = 0;
    
    
     public static boolean blogquery = false ;
     
     
    private ScheduledExecutorService schedulerDB;
    private ScheduledExecutorService schedulerFile;
    public static ScheduledExecutorService schedulerHttp;
    public static ThreadPoolExecutor executorQueue ;
    public static Vector<AsyncContext> contexts = new Vector<>(); 
    
    public void contextInitialized() {
        schedulerHttp =  Executors.newScheduledThreadPool(13);
        executorQueue =  (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
        int timersyncdb = NikitaConnection.getDefaultPropertySetting().getData("init-syncdb-client").getData("timer").toInteger();
        if (timersyncdb>=1) { 
            schedulerDB = Executors.newSingleThreadScheduledExecutor();
            schedulerDB.scheduleAtFixedRate(new Runnable() {
                public void run() {
                    long istart = System.currentTimeMillis();iserv++;iserv++;
                    String sbuffLog = "S["+iserv+"]: /" + " {"+ "init-syncdb-client"+"} at "+ Utility.Now()+ " : ";
                    try {
                        new mzharasyncdbclient().OnRun(null, new NikitaResponse(null, new NikitaServletResponse(null, null)), null);
                        sysout(sbuffLog + Utility.formatCurrency(System.currentTimeMillis() - istart) + " ms"); 
                    } catch (Exception e) {} 
                }
            }, 0, timersyncdb, TimeUnit.SECONDS);
        }
        
        int timersyncfile = NikitaConnection.getDefaultPropertySetting().getData("init-syncfile-client").getData("timer").toInteger();
        if (timersyncfile>=1) {  
            schedulerFile = Executors.newSingleThreadScheduledExecutor();
            schedulerFile.scheduleAtFixedRate(new Runnable() {
                public void run() {
                    long istart = System.currentTimeMillis();iserv++;iserv++;
                    String sbuffLog = "S["+iserv+"]: /" + " {"+ "init-syncfile-client"+"} at "+ Utility.Now()+ " : ";
                    try {
                        new mzharasyncfileclient().OnRun(null, new NikitaResponse(null, new NikitaServletResponse(null, null)), null);
                        sysout(sbuffLog + Utility.formatCurrency( System.currentTimeMillis() - istart) + " ms");   
                    } catch (Exception e) {  }                    
                }
            }, 0, timersyncfile, TimeUnit.SECONDS);
        }
        
        
        ///folder inisiasi
        if (NikitaConnection.getDefaultPropertySetting().getData("init").containsKey("temp")) {
            String file = NikitaConnection.getDefaultPropertySetting().getData("init").getData("temp").toString();
            try {
                if (new File(file).isDirectory()) {                     
                }else if (new File(file).exists()) {
                     System.err.println( "Folder temp tidak ditemukan : " +file );
                }else{
                    new File(file).mkdirs();
                }
            } catch (Exception e) { System.err.println( e.getMessage()); }                    
        }
        if (NikitaConnection.getDefaultPropertySetting().getData("init").containsKey("form")) {
            String file = NikitaConnection.getDefaultPropertySetting().getData("init").getData("form").toString();
            try {
                if (new File(file).isDirectory()) {                     
                }else if (new File(file).exists()) {
                     System.err.println( "Folder form tidak ditemukan : " +file );
                }else{
                    new File(file).mkdirs();
                }
            } catch (Exception e) { System.err.println( e.getMessage()); }                    
        } 
        if (NikitaConnection.getDefaultPropertySetting().getData("init").containsKey("resource")) {
            String file = NikitaConnection.getDefaultPropertySetting().getData("init").getData("resource").toString();
            try {
                if (new File(file).isDirectory()) {                     
                }else if (new File(file).exists()) {
                     System.err.println( "Folder resource tidak ditemukan : " +file );
                }else{
                    new File(file).mkdirs();
                }
            } catch (Exception e) { System.err.println( e.getMessage()); }                    
        }else{
            System.err.println( "Folder resource belum diinisiasi" );
        }        
        if (NikitaConnection.getDefaultPropertySetting().getData("init").containsKey("storage")) {
            String file = NikitaConnection.getDefaultPropertySetting().getData("init").getData("storage").toString();
            try {
                if (new File(file).isDirectory()) {                     
                }else if (new File(file).exists()) {
                     System.err.println( "Folder storage tidak ditemukan : " +file );
                }else{
                    new File(file).mkdirs();
                }
            } catch (Exception e) { System.err.println( e.getMessage()); }                    
        }else{
            System.err.println( "Folder storage belum diinisiasi" );
        }  
        if (NikitaConnection.getDefaultPropertySetting().getData("init").containsKey("document")) {
            String file = NikitaConnection.getDefaultPropertySetting().getData("init").getData("document").toString();
            try {
                if (new File(file).isDirectory()) {                     
                }else if (new File(file).exists()) {
                     System.err.println( "Folder document tidak ditemukan : " +file );
                }else{
                    new File(file).mkdirs();
                }
            } catch (Exception e) { System.err.println( e.getMessage()); }                    
        }else{
            System.err.println( "Folder document belum diinisiasi" );
        } 
    }

    public void contextDestroyed() {
        if (schedulerDB!=null) {
            schedulerDB.shutdownNow();
        } 
        if (schedulerFile!=null) {
            schedulerFile.shutdownNow();
        }  
        if (schedulerHttp!=null) {
            schedulerHttp.shutdownNow();
        }         
    } 

    private Hashtable<String, ScheduledExecutorService>  scheduler = new Hashtable<String, ScheduledExecutorService>  ();
    public void schedullerInit(){
        NikitaConnection nc = NikitaConnection.getConnection(NikitaConnection.LOGIC);
        Nikitaset ns = nc.Query("SELECT threadname,threadmode,runfirst,runinterval FROM web_scheduller WHERE threadtype=?;", "web");  
        for (int i = 0; i < ns.getRows(); i++) {
            schedullerCreate(ns.getText(i, "threadname"), ns.getText(i, "runfirst"), ns.getText(i, "runinterval"));
        }     
        nc.closeConnection();
        
    }
    public void schedullerCreate(String threadname, String runfirst, String runinterval){
            int period = Utility.getInt(runinterval);
            
            if (runfirst.equals("") && period==0) {
            }else{
                int delay = 0; 
                String now = Utility.Now().substring(11, 19);//YYYY-MM-DD HH:NN:SS
                String get = runfirst;
                if (get.contains(":")) {
                    int nn =  getTimeNumber(now, now);
                    int gg =  getTimeNumber(get, now);
                    delay = nn-gg;
                    period = (period==0) ? (24*60*60) : period ; 
                    delay  = (delay<0) ? (24*60*60 + delay) : delay ; 
                }else{
                    //every 
                    delay = 0;
                }     
                        
                ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
                executorService.scheduleAtFixedRate(new Runnable() {
                    String schName;
                    public Runnable get(String schName) {
                        this.schName=schName;
                        return this;
                    }
                    public void run() {
                        long istart = System.currentTimeMillis();iserv++;iserv++;
                        String sbuffLog = "S["+iserv+"]: /" + " {"+ schName +"} at "+ Utility.Now()+ " : ";                        
                        try {
                            NikitaScheduller.runSchedullerAndWait(schName);
                        } catch (Exception e) { 
                            //update query                            
                        }                       
                        sysout(sbuffLog + Utility.formatCurrency( System.currentTimeMillis() - istart) + " ms");                    
                    }
                }.get(threadname), delay, period, TimeUnit.SECONDS) ; 
                if (scheduler.get(threadname)!=null) {
                    if (scheduler.get(threadname).isShutdown()||scheduler.get(threadname).isTerminated()) {                        
                    }else{
                        scheduler.get(threadname).shutdownNow();
                    }
                }
                scheduler.put(threadname, executorService);    
                
            }   
    }
    public void schedullerDestroy(){
        String[] keys = new Nset(scheduler).getObjectKeys();
        for (int i = 0; i < keys.length; i++) {
            if (scheduler.get(keys[i])!=null) {
                scheduler.get(keys[i]).shutdownNow();
            } 
        }
        scheduler.clear();
    }
    public void schedullerActivate(boolean activate){
        
    }
    public String getServletInfo() {
        return "Nikita Generator v2";
    }
    public String getServletPath() {
        try {
            String s = getServletConfig().getServletContext().getContextPath();
            return s.startsWith("/")?s.substring(1):s;
        } catch (Exception e) {  }
        return "";
    }
    public static String newNikitaINI = "";
    public void initIni(){
        /*
        String context = getServletPath();
        if (context.equals("")||context.equals("/")||context.equals("\\")||context.length()<=1) {
            newNikitaINI = "root";
        }else {
            newNikitaINI = context.substring(1);         
        }
        */
        try {
            String rpath = getServletConfig().getServletContext().getRealPath("/");
            if (rpath.endsWith("/")||rpath.endsWith("\\")) {
                rpath = rpath.substring(0, rpath.length()-1);
            }
            newNikitaINI = rpath + ".ini";           
        } catch (Exception e) { newNikitaINI = ""; }
        sysout(":"+newNikitaINI);
        
    } 
    public void init() throws ServletException {
        super.init(); 
        initIni();      
        
        AppNikita.getInstance();//init ==> new AppNikita().onCreate();
         
        
        
        sysout("Nikita: "+getServletPath()+" [init] " ); //+ Thread.currentThread().getName()
        //sysout(getServletConfig().getServletContext().getServerInfo());
        //sysout(getServletConfig().getServletContext().getVirtualServerName());
        //sysout(getServletConfig().getServletContext().getRealPath("/WEB-INF/classes/"));
        //sysout(getServletConfig().getServletContext().getContextPath());
        sysout(getServletConfig().getServletContext().getRealPath("/WEB-INF/classes/"));
        //sysout(getServletConfig().getServletContext().getRealPath("/"));
        //NikitaEngine.compile(NikitaConnection.getConnection(NikitaConnection.LOGIC), "perkasa_snapshoot");
        
        deleteFnikita();//delete Form file
        //BASE_URL = NikitaConnection.getDefaultPropertySetting().getData("init").getData("baseurl").toString();
        
        //NikitaConnection.getConnection();  
        contextInitialized();
        schedullerInit(); 
        
        btask.init();//call firs
        btaskcall.init();//call first
    }
    
    public void destroy() {
        sysout("Nikita: "+getServletPath()+" [Destroy] "); 
        contextDestroyed();
        schedullerDestroy();
        super.destroy();
    }
   
    private int getTimeNumber(String time, String timenow){
    	String h = "0";
    	if (time.startsWith(":") && timenow.contains(":")) {
			h=timenow.substring(0,timenow.indexOf(":"));
			time = time.substring(time.indexOf(":")+1);
	}else if (time.contains(":") ) {
			h=time.substring(0,time.indexOf(":"));
			time = time.substring(time.indexOf(":")+1);
	}
    	String m = "0";
    	if (time.contains(":")) {
                m=time.substring(0,time.indexOf(":"));
                time = time.substring(time.indexOf(":")+1);
        }else{
                m=time;
                time="0";
        }
    	String d = time;
    	if (m.equals("")) {
            m=timenow.substring(3,5);//01:34:6
    	}
    	
    	return Utility.getInt(h)*3600+Utility.getInt(m)*60+Utility.getInt(d);
    }
   
    public static String getDirForm(){
        if (NikitaConnection.getDefaultPropertySetting().getData("init").getData("form").toString().equals("")) {
            return System.getProperty("java.io.tmpdir");//"D:\\zahra\\!temp";
        }
        return NikitaConnection.getDefaultPropertySetting().getData("init").getData("form").toString();
    }
    public static String getDirTmp(){
        if (NikitaConnection.getDefaultPropertySetting().getData("init").getData("temp").toString().equals("")) {
            return System.getProperty("java.io.tmpdir");//"D:\\zahra\\!temp";
        }
        return NikitaConnection.getDefaultPropertySetting().getData("init").getData("temp").toString();
    }
    public static String getFileSeparator(){
        return System.getProperty("file.separator"); 
    }
    private boolean isContainFilterExp(String filterexp, String fname){
        if ( filterexp.contains("*") ) {
            Vector<String> v = Utility.splitVector(filterexp, ",");
            for (int i = 0; i < v.size(); i++) {
                if (v.elementAt(i).startsWith("*")) {
                    String s =v.elementAt(i).substring(1);
                    if (fname.endsWith(s)) {
                        return true;
                    }
                }else if (v.elementAt(i).endsWith("*")) {
                    String s =v.elementAt(i).substring(0, v.elementAt(i).length()-1);
                    if (fname.startsWith(s)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    private void deleteFnikita(){
        if (NikitaConnection.getDefaultPropertySetting().getData("init").containsKey("form")) {
            Utility.deleteFolderChildAll(new File(NikitaService.getDirForm()));  
        }             
    }
    public static String getBrowserType(String useragent){           
            useragent = useragent !=null ?useragent.toLowerCase():"";
            String contain = "mobile|android|meego|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|iphone|opod)|iris|kindle|lge |maemo|midp|mmp|mobile.+firefox|netfront|opera mini|opera mobi|palm os|phone|pocket|psp|series40|series60|symbian|treo|vodafone|wap|windows ce|xda|xiino";
             
            Vector<String> ss = Utility.splitVector(contain,"|");
            for (int i = 0; i < ss.size(); i++) {
                if (useragent.contains(ss.elementAt(i))) {
                    return "MOBILE";
                }
            }
            return "WEB";    
    }
    public static String getAppURL(HttpServletRequest request) {
        StringBuffer url = new StringBuffer();
        int port = request.getServerPort();
        if (port < 0) {
            port = 80; // Work around java.net.URL bug
        }
        String scheme = request.getScheme();
        url.append(scheme);
        url.append("://");
        url.append(request.getServerName());
        if (("http".equals(scheme) && (port != 80)) || ("https".equals(scheme) && (port != 443))) {
            url.append(':');
            url.append(port);
        }
        url.append(request.getContextPath());
        return url.toString();
    }
    private String getPathInfo(HttpServletRequest req){
        if (req.getPathInfo()!=null) {
            return req.getPathInfo();
        }
        return req.getRequestURI().substring(getServletConfig().getServletContext().getContextPath().length());
    }
    protected void doRun(HttpServletRequest req, NikitaServletResponse resp){
        String BASE_URL = "";//http://localhost:8084/NikitaWeb";
        String BASE_CONTEXT = "";
        
        String NIKITAID = "WEB";
        String USERAGENT = "WEB";
        String BASE_PATH_INFO = "";
        
        
        
        USERAGENT    = getBrowserType(req.getHeader("User-Agent"));
        BASE_URL     = req.getScheme()+"://"+req.getServerName()+":"+req.getServerPort()+req.getContextPath();
        BASE_CONTEXT = req.getContextPath();
        BASE_URL     = getAppURL(req) ;
        
         
        Nset nInfo = Nset.readJSON(req.getParameter("info"));
        Nset nNiki = Nset.readJSON( nInfo.getData("nikita").toString());
        String vLogic = String.valueOf(req.getParameter("NGL"));        
        setLocalBase(BASE_URL, BASE_CONTEXT,req.getScheme(),req.getServerName(),req.getServerPort(),  nNiki.isNsetObject()?nInfo:Nset.newObject(), nInfo.getData("theme").toString(), String.valueOf(req.getSession().getAttribute("THEME")), vLogic );
        
        Cookie[] cookies = req.getCookies();
        if(cookies !=null){
            for(Cookie cookie : cookies){             
                if(cookie.getName().equals("JSESSIONID")) 
                    NIKITAID = cookie.getValue();
              
            }
        }else{
            NIKITAID = req.getSession().getId();
        }
        
        
        
        
        
        String linkName = getPathInfo(req);
        linkName        = (linkName!=null)?linkName:"";
        String pathInfo = linkName ;
        BASE_PATH_INFO  = linkName;
                
        if (req.getRequestURI().equals(req.getContextPath()) && !req.getContextPath().endsWith("/")) {
            new NikitaResponse(req, resp).sendRedirect(req.getContextPath()+"/");
            return ;
        }

        if (req.getParameter("form")!=null) {
            pathInfo ="/"+req.getParameter("form");
        }
       
        long istart = System.currentTimeMillis();icount++;
            String sbuffLogDebugId = "";
            sbuffLogDebugId = "N["+icount+"]: "+pathInfo + " : ";
        
            
        if (pathInfo.startsWith("/cloud/") ) {         
            //context/cloud/usercloude/form
        }
         
        
            
        NikitaConnection nc = resp.getConnection(NikitaConnection.LOGIC);
        if (pathInfo.equals("/")) {
            String bas = NikitaConnection.getDefaultPropertySetting().getData("init").getData("base").toString();                      
            if (!bas.equals("")) {
                pathInfo ="/"+bas;
            }
        }
        
        NikitaRequest nikitaRequest = new NikitaRequest(req, resp);
        NikitaResponse nikitaResponse = new NikitaResponse(req, resp);
        String fname = getPathInfo(req).length()>=1 ? getPathInfo(req).substring(1) : "";
                
        nikitaResponse.setVirtualRegistered("@+NIKITAID",  NIKITAID);
        nikitaResponse.setVirtualRegistered("@+USERAGENT", USERAGENT);
        
        nikitaResponse.setVirtualRegistered("@+BASE", BASE_URL);
        nikitaResponse.setVirtualRegistered("@+BASEURL", BASE_URL);
        nikitaResponse.setVirtualRegistered("@+CONTEXT", BASE_CONTEXT);
        nikitaResponse.setVirtualRegistered("@+PATHINFO", BASE_PATH_INFO);
        nikitaResponse.setVirtualRegistered("@+HOST", req.getHeader("host")!=null?req.getHeader("host"):"");
        
        
        
        //context====/form[/formname]/res[static]/
        if (pathInfo.startsWith("/link/") || pathInfo.startsWith("/res/") || pathInfo.startsWith("/static/")) {            
        }else  if ( pathInfo.contains("/res/") || pathInfo.contains("/static/")) {
            String[] xpath = Utility.split(pathInfo+"//", "/");
            pathInfo = pathInfo.substring(xpath[1].length()+1);
            
            if (Utility.isNumeric(xpath[1])) {                
                if ( pathInfo.startsWith("/res/") || pathInfo.startsWith("/static/")) {
                }else{
                    pathInfo = pathInfo.substring(xpath[2].length()+1); 
                }
            }
        }
        
        if (String.valueOf(req.getHeader("Nikita")).equalsIgnoreCase("mobile")) {
            NikitaService.imobilecount ++ ; 
        }
        
        if (pathInfo.startsWith("/static/")) {
            NikitaService.istaticrescount ++ ; 
        }
        if (pathInfo.startsWith("/link/")) {
        }else if (pathInfo.startsWith("/res/")) {
            NikitaService.irescount ++ ; 
            if (pathInfo.startsWith("/res/test/")||pathInfo.startsWith("/res/about/test/")) {
                Nset n = Nset.newObject();
                n.setData("Application", "Nikita Generator");
                n.setData("Generator", "Hello Nikita");
                n.setData("Version", AppNikita.APP_VER_CODE);
                n.setData("Action", "Test Connection");  
                
                writeOut(n.toJSON(), resp);
                return; 
            }else  if (pathInfo.startsWith("/res/nrn/")||pathInfo.equals("/res/nrn")) {
                NikitaRzN.sendtoSID(req.getParameter("sid")!=null?req.getParameter("sid"):"", req.getParameter("message")!=null?req.getParameter("message"):"");
                Nset n = Nset.newObject();
                n.setData("Status", "try");
                writeOut(n.toJSON(), resp);
                return; 
            }else  if (pathInfo.startsWith("/res/about/")||pathInfo.equals("/res/about")) {
                String session = NIKITAID;
                /*
                Cookie[] cookies = req.getCookies();
                if(cookies != null){
                    for(Cookie cookie : cookies){
                        if(cookie.getName().equals("JSESSIONID")){
                            session = cookie.getValue();
                        }                   
                    }  
                    session=session!=null ? session:"";
                    Cookie 
                    cookie= new Cookie("NIKITA", session);
                    cookie.setMaxAge( 100000 ); 
                    resp.addCookie(cookie);
                    
                }  
                */
                
                StringBuffer sb = new StringBuffer();
               
                sb.append("<div style=\"display:none\">Hello Nikita</div>");   
                sb.append("<table>");
                sb.append("<tr><td>   </td><td>"+ "").append("<br></td></tr>");
                sb.append("<tr><td><b>Nikita Generator </b></td><td> v."+ AppNikita.APP_VER_CODE).append("<br></td></tr>");
                sb.append("<tr><td><tr><td>Nikita Mode    </td><td>: "+NikitaConnection.getDefaultPropertySetting().getData("init").getData("mode").toString()).append("<br></td></tr>");
                sb.append("<tr><td>Nikita Theme   </td><td>: "+NikitaConnection.getDefaultPropertySetting().getData("init").getData("theme").toString()).append("<br></td></tr>");
                sb.append("<tr><td>Nikita DBConf  </td><td>: "+NikitaConnection.getDefaultPropertySetting().getData("init").getData("nikitaonline").toString()).append("<br></td></tr>");
                sb.append("<tr><td>Nikita Timer   </td><td>: "+NikitaConnection.getDefaultPropertySetting().getData("init").getData("nikitatimerbroadcast").toString()).append("<br></td></tr>");
                sb.append("<tr><td>Nikita Thread   </td><td>: "+ Utility.formatCurrency(ithread)+ " max: " + Utility.formatCurrency(imaxthread)  ).append("<br></td></tr>");                
                sb.append("<tr><td>Nikita Concurrent   </td><td>: "+ Utility.formatCurrency(iconcurrent) + " max: " + Utility.formatCurrency(imaxcurrent)+ " requestcount: " + Utility.formatCurrency(icount)).append("<br></td></tr>");
                sb.append("<tr><td>Nikita Resource    </td><td>: "+ Utility.formatCurrency(irescount) + " static: " + Utility.formatCurrency(istaticrescount)+ " mobile: " + Utility.formatCurrency(imobilecount)).append("<br></td></tr>");
                sb.append("<tr><td>Nikita Startdate   </td><td>: "+  Utility.formatDate(starteddate, "yyyy-MM-dd HH:mm:ss") + " live: " +  humanReadableDate(System.currentTimeMillis()-starteddate, false) ).append("<br></td></tr>");
                
                
                String security = NikitaConnection.getDefaultPropertySetting().getData("init").getData("scurity").toString();
                if (NikitaConnection.getDefaultPropertySetting().getData("init").containsKey("security")) {
                    security=NikitaConnection.getDefaultPropertySetting().getData("init").getData("security").toString();
                }
                sb.append("<tr><td>Nikita Security </td><td>: "+security).append("<br></td></tr>");                
                sb.append("<tr><td>Nikita Sync File </td><td>: "+NikitaConnection.getDefaultPropertySetting().getData("init-syncfile-client").getData("timer").toString()).append("<br></td></tr>");
                sb.append("<tr><td>Nikita Sync DB </td><td>: "+NikitaConnection.getDefaultPropertySetting().getData("init-syncdb-client").getData("timer").toString()).append("<br></td></tr>");

                sb.append("<tr><td>Nikita Form Nfile </td><td>: "+NikitaConnection.getDefaultPropertySetting().getData("init").getData("nfile").toString()).append("<br></td></tr>");
                sb.append("<tr><td>Nikita Zip Data </td><td>: "+NikitaConnection.getDefaultPropertySetting().getData("init").getData("nzip").toString()).append("<br></td></tr>");
                sb.append("<tr><td>Nikita Zip Browser </td><td>: "+ (nikitaResponse.isSupportGzip() ? "gzip ":"") + "" + (nikitaResponse.isSupportDeflate()?" deflate":"") ).append("<br></td></tr>");
                
                sb.append("<tr><td>   </td><td>"+ "").append("<br></td></tr>");
                sb.append("<tr><td>Nikita Session </td><td>: "+session).append("<br></td></tr>");
           
                
                sb.append("<tr><td>Nikita File Storage </td><td>: "+NikitaConnection.getDefaultPropertySetting().getData("init").getData("storage").toString()).append("<br></td></tr>");
                sb.append("<tr><td>Nikita File Resource </td><td>: "+NikitaConnection.getDefaultPropertySetting().getData("init").getData("resource").toString()).append("<br></td></tr>");
                sb.append("<tr><td>Nikita File Temporary </td><td>: "+NikitaConnection.getDefaultPropertySetting().getData("init").getData("temp").toString()).append("<br></td></tr>");
                
                sb.append("<tr><td>Access Storage </td><td>: "+ fileaccess(NikitaConnection.getDefaultPropertySetting().getData("init").getData("storage").toString())).append("<br></td></tr>");
                sb.append("<tr><td>Access Resource </td><td>: "+fileaccess(NikitaConnection.getDefaultPropertySetting().getData("init").getData("resource").toString())).append("<br></td></tr>");
                sb.append("<tr><td>Access Temporary </td><td>: "+fileaccess(NikitaConnection.getDefaultPropertySetting().getData("init").getData("temp").toString())).append("<br></td></tr>");
             
                sb.append("<tr><td>   </td><td>"+ "").append("<br></td></tr>");
                sb.append("<tr><td>Operating System </td><td>: "+System.getProperty("os.name")).append("<br></td></tr>");
                sb.append("<tr><td>File Sparator </td><td>: "+System.getProperty("file.separator")).append("<br></td></tr>");
                sb.append("<tr><td>Server Info </td><td>: "+req.getServletContext().getServerInfo()).append("<br></td></tr>");
                sb.append("<tr><td>Server Datetime </td><td>: "+Utility.Now()).append("<br></td></tr>");
                sb.append("<tr><td>Free Heap Memory </td><td>: "+ humanReadableByteCount(Runtime.getRuntime().freeMemory(), false) +" of " + humanReadableByteCount(Runtime.getRuntime().totalMemory(),false) +" max " + humanReadableByteCount(Runtime.getRuntime().maxMemory(),false)).append("<br></td></tr>");
                
                
                NumberFormat nfNumberInstance = NumberFormat.getNumberInstance( );
                sb.append("<tr><td>   </td><td>"+ "").append("<br></td></tr>");
                sb.append("<tr><td>Server Country </td><td>: " + Locale.getDefault().getCountry() + " : " + Locale.getDefault().getDisplayCountry()).append("<br></td></tr>");
                sb.append("<tr><td>Server Language </td><td>: "+ Locale.getDefault().getLanguage()+ " : " + Locale.getDefault().getDisplayLanguage()).append("<br></td></tr>");
                sb.append("<tr><td>Server DateMoth </td><td>: "+ new DateFormatSymbols().getMonths()[7]).append("<br></td></tr>");
                sb.append("<tr><td>Server Currency </td><td>: "+ nfNumberInstance.format(10386.13)).append("<br></td></tr>");
                 
                
                sb.append("<tr><td>Client User-Agent</td><td>: "+req.getHeader("User-Agent")!=null?req.getHeader("User-Agent"):"").append("<br></td></tr>");

                
                
                sb.append("</table><br>");
                
                showPage("About Hello Nikita", sb.toString(), 200, resp);
                sysout(sbuffLogDebugId + Utility.formatCurrency( System.currentTimeMillis() - istart) + " ms");
                return;            
            }else if (pathInfo.startsWith("/res/scheduller/")) {///res/scheduller/[schedulername]/[start/startasyntask/stop/restart/activate/deactivate/status]
                if (NikitaConnection.getDefaultPropertySetting().getData("init-scheduller").getData("username").toString().equals(nikitaRequest.getParameter("username"))||NikitaConnection.getDefaultPropertySetting().getData("init-scheduller").getData("username").toString().equals("")) {
                    if (NikitaConnection.getDefaultPropertySetting().getData("init-scheduller").getData("password").toString().equals(nikitaRequest.getParameter("password"))||NikitaConnection.getDefaultPropertySetting().getData("init-scheduller").getData("password").toString().equals("")) {
                        String[] xpath = Utility.split(pathInfo+"///", "/");//[0]/[1]res/[2]scheduller/[3]schedulername/...
                        String conName = xpath[4];
                        if (conName.equals("start")||conName.equals("restart")) {
                            if (scheduler.get(xpath[3])!=null) {
                                if (scheduler.get(xpath[3]).isTerminated()||scheduler.get(xpath[3]).isShutdown()) {
                                    
                                }                                        
                                scheduler.get(xpath[3]).execute(new Runnable() {
                                    String schName;
                                    public Runnable get(String schName) {
                                        this.schName=schName;
                                        return this;
                                    }
                                    public void run() {
                                        NikitaScheduller.runSchedullerAndWait(schName);
                                    }
                                }.get(xpath[3]));
                            } 
                        }else if (conName.equals("startandwait")) {
                            NikitaScheduller.runSchedullerAndWait(xpath[3]);                             
                        }else if (conName.equals("stop")) {
                            if (scheduler.get(xpath[3])!=null) {
                                scheduler.get(xpath[3]).shutdownNow();
                            } 
                        }else if (conName.equals("activate")) {
                            //update mode jadi active
                            nc.Query("UPDATE web_scheduller SET threadmode='active' WHERE threadname=? ", xpath[3]);
                        }else if (conName.equals("deactivate")||conName.equals("inactivate")) {
                            //update mode jadi inac
                            nc.Query("UPDATE web_scheduller SET threadmode='inactive' WHERE threadname=? ", xpath[3]);
                        }else if (conName.equals("status")) {     
                            //query
                            Nikitaset ns =nc.Query("SELECT M.status AS stat, M.startdate, M.finishdate,S.status ,S.action FROM web_scheduller M LEFT JOIN web_scheduller_task S ON (M.threadid=S.threadid) WHERE M.threadname=? ORDER BY S.taskindex ASC  ", xpath[3]);
                            StringBuffer sb = new StringBuffer();
                            sb.append("Nikita Scheduller ("+ xpath[3] +") : "+ ns.getText(0, "stat") +"").append("<br><br>");;
                            sb.append("<table>");
                            sb.append("<tr><td><b>Task</b></td><td>StartDate</td><td>FinishDate</td><td>Status</td></tr>"); 
                            for (int i = 0; i < ns.getRows(); i++) {
                                sb.append("<tr><td><b>"+Nset.readJSON(ns.getText(i, "action")).getData("ntask").toString()+"</b></td><td>"+ns.getText(i, "startdate")+"</td><td>"+ns.getText(i, "finishdate")+"</td><td>"+ns.getText(i, "status")+"</td></tr>");                        
                            }
                            sb.append("</table><br>");     
                            showPage("Nikita Scheduller", sb.toString(), 200, resp);
                            sysout(sbuffLogDebugId + Utility.formatCurrency( System.currentTimeMillis() - istart) + " ms");
                            return;
                        }else if (conName.equals("history")) {     
                            //query
                            Nikitaset ns =nc.QueryPage(1, 50, "SELECT historyid,taskname,startdate,finishdate,status,activitystatus FROM web_scheduller_history WHERE threadname=?  ORDER BY historyid DESC  ", xpath[3]);
                            StringBuffer sb = new StringBuffer();
                            sb.append("Nikita History ("+ xpath[3] +") ").append("<br><br>");;
                            sb.append("<table>");
                            sb.append("<tr><td><b>Task</b></td><td>StartDate</td><td>FinishDate</td><td>Status</td></tr>"); 
                            for (int i = ns.getRows()-1; i >= 0; i--) {
                                sb.append("<tr><td><b>"+ ns.getText(i, "taskname") +"</b></td><td>"+ns.getText(i, "startdate")+"</td><td>"+ns.getText(i, "finishdate")+"</td><td>"+ns.getText(i, "status")+"</td></tr>");                        
                            }
                            sb.append("</table><br>");     
                            showPage("Nikita Scheduller", sb.toString(), 200, resp);
                            sysout(sbuffLogDebugId + Utility.formatCurrency( System.currentTimeMillis() - istart) + " ms");
                            return;
                        }else{
                            showPage("Nikita Scheduller", "Request Unkown", 200, resp);
                            sysout(sbuffLogDebugId + Utility.formatCurrency( System.currentTimeMillis() - istart) + " ms");
                            return;
                        }
                        showPage("Nikita Scheduller", "Auth Wrong", 200, resp);
                        sysout(sbuffLogDebugId + Utility.formatCurrency( System.currentTimeMillis() - istart) + " ms");
                        return;
                    }
                    showPage("Nikita Scheduller", "Auth Wrong", 200, resp);
                    sysout(sbuffLogDebugId + Utility.formatCurrency( System.currentTimeMillis() - istart) + " ms");
                    return;
                }       
            }else if (pathInfo.startsWith("/res/assetfn/")||pathInfo.equals("/res/assetfn")) {                
                String path = NikitaConnection.getDefaultPropertySetting().getData("init").getData("asset").toString();                      
                String assetname = nikitaRequest.getParameter("name");      
                
                if (assetname.equals("") ) {
                    String[] xpath = Utility.split(pathInfo+"/", "/");//[0]/[1]res/[2]resource/[3]name/...
                    assetname = xpath[3];
                }
                
                
                try {
                    getResourceFile(new FileInputStream(path+getFileSeparator()+assetname), req, resp, nikitaRequest.getParameter("f"), nikitaRequest.getParameter("mode").equals("att")?true:false);
                } catch (Exception e) {  }
                     sysout(sbuffLogDebugId + Utility.formatCurrency( System.currentTimeMillis() - istart) + " ms");
                return;     
            }else if (pathInfo.startsWith("/res/asset/")) {
                try {
                    String path = NikitaConnection.getDefaultPropertySetting().getData("init").getData("asset").toString();                      
                    String assetname = nikitaRequest.getParameter("assetname");
                    String id = nikitaRequest.getParameter("assetid");
                    String where = " assetname=? ";
                    if (Utility.isNumeric(id)) {
                        where="assetid=?";
                        assetname=id;
                    }
                    Nikitaset ns = nc.Query("SELECT assetid,assetfname,assettype,assetsize,assethash FROM web_asset WHERE "+where, assetname);
                    if (nikitaRequest.getParameter("hash").trim().equals("true")) {
                        nikitaResponse.writeStream(ns.getText(0, 4));
                    }else if (ns.getRows()>=1) {
                        getResourceFile(new File(path+getFileSeparator()+ns.getText(0,0)+".zip"), req, resp, ns.getText(0,1) + "."+ns.getText(0,2), false);
                    }else{
                        showPageNotFound(resp);
                    }                    
                    sysout(sbuffLogDebugId + Utility.formatCurrency( System.currentTimeMillis() - istart) + " ms");
                    return;
                } catch (Exception e) { }
            }else if (pathInfo.startsWith("/res/file/")||pathInfo.equals("/res/file")) {
                String rfile =nikitaRequest.getParameter("file") ;
                String rfilename = nikitaRequest.getParameter("filename") ;
                String rinline =nikitaRequest.getParameter("inline") ;
                
                if (nikitaRequest.getParameter("view").length()>=8) {
                    String bview = Utility.decodeBase64(nikitaRequest.getParameter("view"));
                    Nset n = Nset.readJSON(bview);
                    
                    rfile= n.getData("file").toString();
                    rfilename= n.getData("filename").toString();
                    rinline= n.getData("inline").toString();
                }
                if (nikitaRequest.getParameter("iv").length()>=8) {
                    Nset n = Nset.readJSON(nikitaResponse.getVirtualString("@+SESSION-"+nikitaRequest.getParameter("iv")));
                    
                    rfile= n.getData("file").toString();
                    rfilename= n.getData("filename").toString();
                    rinline= n.getData("inline").toString();
                }
                
                
                if (rfile.trim().equals("")) {     
                    showPageNotFound(resp);
                }else{
                    try {
                        if (new File(NikitaService.getDirTmp()+ getFileSeparator() +rfile+".tmp").exists()) {
                            NikitaService.getResourceFile(new File(NikitaService.getDirTmp()+getFileSeparator() +rfile+".tmp"), nikitaRequest.getHttpServletRequest(), nikitaResponse.getHttpServletResponse(), rfilename, rinline.equals("true")?false:true );
                            
                            //new File(NikitaService.getDirTmp()+getFileSeparator() +nikitaRequest.getParameter("file")+".tmp").delete();
                        }else{
                            showPageNotFound(resp);
                        }                        
                    } catch (Exception e) { }
                }   
                sysout(sbuffLogDebugId + Utility.formatCurrency( System.currentTimeMillis() - istart) + " ms");
                return;
            }else if (pathInfo.startsWith("/res/connection/")||pathInfo.equals("/res/connection")) {
                if (NikitaConnection.getDefaultPropertySetting().getData(NikitaConnection.NIKITA).getData("remote").toString().equals("true")) {
                    if (NikitaConnection.getDefaultPropertySetting().getData(NikitaConnection.NIKITA).getData("remoteusername").toString().equals(nikitaRequest.getParameter("username")) && NikitaConnection.getDefaultPropertySetting().getData(NikitaConnection.NIKITA).getData("remotepassword").toString().equals(nikitaRequest.getParameter("password"))  ) {
                        if (nikitaRequest.getParameter("paging").contains(",")) {
                            String[] sArr = Utility.split(nikitaRequest.getParameter("paging"), ",");
                            Nikitaset n = resp.getConnection(NikitaConnection.NIKITA).QueryPage(Utility.getInt(sArr[0]),Utility.getInt(sArr[1]), nikitaRequest.getParameter("sql"));
                            nikitaResponse.writeStream(n.toNset().toJSON());
                        }else{
                            Nikitaset n = resp.getConnection(NikitaConnection.NIKITA).Query(nikitaRequest.getParameter("sql"));
                            nikitaResponse.writeStream(n.toNset().toJSON());
                        } 
                    }
                }
                sysout(sbuffLogDebugId + Utility.formatCurrency( System.currentTimeMillis() - istart) + " ms");
                return;
           }else if (pathInfo.startsWith("/res/maps/")||pathInfo.equals("/res/maps")) {  
                //staticmap //streetview
                String url = "https://maps.googleapis.com/maps/api/";
                if (pathInfo.startsWith("/res/maps/staticmap")) {
                    url = "https://maps.googleapis.com/maps/api/staticmap";
                }else if (pathInfo.startsWith("/res/maps/streetview")) {
                    url = "https://maps.googleapis.com/maps/api/streetview";
                }
                //staticmap?center=Brooklyn+Bridge,New+York,NY&zoom=13&size=600x300&maptype=roadmap&markers=color:blue%7Clabel:S%7C40.702147,-74.015794&markers=color:green%7Clabel:G%7C40.711614,-74.012318&markers=color:red%7Clabel:C%7C40.718217,-73.998284
                //streetview?size=600x300&location=46.414382,10.013988&heading=151.78&pitch=-0.76&key=AIzaSyA4rAT0fdTZLNkJ5o0uaAwZ89vVPQpr_Kc
                try {
                    String[] keys = Utility.getObjectKeys(nikitaRequest.getHttpServletRequest().getParameterNames());
                    for (int i = 0; i < keys.length; i++) {
                        keys[i] = keys[i] + "=" + nikitaRequest.getHttpServletRequest().getParameter( keys[i]) ;
                    }
                    
                    HttpResponse httpResponse =  NikitaInternet.getHttp(url, keys);
                    
                    OutputStream os= nikitaResponse.getHttpServletResponse().getOutputStream();                    
                    InputStream is =  httpResponse.getEntity().getContent();
                    
                    byte[] buffer = new byte[1024];
                    int length; long l = 0;
                    while ((length = is.read(buffer)) > 0) {
                        l=l+length;
                        os.write(buffer, 0, length);
                    }
                    os.flush();                   
                } catch (Exception e) {  }
                    
           }else if (pathInfo.startsWith("/res/qrgenerator/")||pathInfo.equals("/res/qrgenerator")) {  

                String qr = nikitaRequest.getParameter("barcode");
                if (qr.equals("")) {
                    qr="N";
                }                        
                int size = 128;int nsize = Utility.getInt(nikitaRequest.getParameter("size"));
                if (nsize>=1) {
                    size = nsize;
                }              
                try {
                    Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
                    String secl = nikitaRequest.getParameter("ecl");
                    if (secl.equalsIgnoreCase("H")) {       //up to 30% damage
                        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
                    }else  if (secl.equalsIgnoreCase("q")) {//up to 25% damage
                        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.Q);
                    }else  if (secl.equalsIgnoreCase("M")) {//up to 15% damage
                        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
                    }else{                                  //up to 7% damage
                        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
                    }   
                    
                    QRCodeWriter qrCodeWriter = new QRCodeWriter();                    
                    BitMatrix byteMatrix = qrCodeWriter.encode(qr,BarcodeFormat.QR_CODE, size, size,hintMap);
                    int CrunchifyWidth = byteMatrix.getWidth();
                    
                    BufferedImage image = new BufferedImage(CrunchifyWidth, CrunchifyWidth, BufferedImage.TYPE_INT_RGB);
                    image.createGraphics();
                    
                    Color fc = Color.BLACK;
                    String sfc = nikitaRequest.getParameter("fc");
                    if (!sfc.equals("")) {
                        fc=Color.decode(sfc);
                    }                        
                
                    Color bg = Color.WHITE;
                    String sbg = nikitaRequest.getParameter("bg");
                    if (!sbg.equals("")) {
                        bg=Color.decode(sbg);
                    }
                    
                    Graphics2D graphics = (Graphics2D) image.getGraphics();
                    graphics.setColor(bg);
                    graphics.fillRect(0, 0, CrunchifyWidth, CrunchifyWidth);
                    graphics.setColor(fc);

                    for (int i = 0; i < CrunchifyWidth; i++) {
                        for (int j = 0; j < CrunchifyWidth; j++) {
                            if (byteMatrix.get(i, j)) {
                                graphics.fillRect(i, j, 1, 1);
                            }
                        }
                    }
                    ImageIO.write(image, "png", nikitaResponse.getHttpServletResponse().getOutputStream());
                } catch (WriterException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }    
                    
                sysout(sbuffLogDebugId + Utility.formatCurrency( System.currentTimeMillis() - istart) + " ms");
                return;
           }else if (pathInfo.startsWith("/res/mobiledebug/")) {
                Nikitaset n = nc.Query("SELECT status,lastlog,request FROM sys_debug WHERE code=?;", nikitaRequest.getParameter("code"));
                if (n.getText(0, 0).equals("0")) {
                    nc.Query("UPDATE sys_debug SET status='1' WHERE code=?;", nikitaRequest.getParameter("code"));
                    nikitaResponse.writeStream(n.toNset().toJSON());
                }  else{
                    nikitaResponse.writeStream(new Nikitaset("empty").toNset().toJSON());
                }
                sysout(sbuffLogDebugId + Utility.formatCurrency( System.currentTimeMillis() - istart) + " ms");
                return;
            }else if (pathInfo.startsWith("/res/mobileparameter/init/")) {
                if (NikitaService.isModeCloud()) {
                    String referral = String.valueOf(nikitaRequest.getParameter("uname"));
                    String string = NikitaService.getUserCloudCode(nc, referral);
                    String prefix = NikitaService.getPrefixUserCloud(nc, string);
                    Nikitaset n = nc.Query("SELECT paramkey,paramvalue FROM web_parameters WHERE createdby = '"+string+"' AND paramtype=? AND paramkey LIKE 'INIT%';", "mobile");
                    String s = n.toNset().toJSON();
                    Nson nson = Nson.readJson(s);                    
                    if (!s.contains("\"INIT\"")) {
                        nson.getData("data").addData(Nson.newArray().addData("INIT").addData(prefix+"mobile"));
                    }
                    if (!s.contains("\"INIT-CONNECTION-CLASS\"")) {
                        nson.getData("data").addData(Nson.newArray().addData("INIT-CONNECTION-CLASS").addData("nikitaconnection"));
                    }
                    if (!s.contains("\"INIT-CONNECTION-URL\"")) {
                        nson.getData("data").addData(Nson.newArray().addData("INIT-CONNECTION-URL").addData("nikitaconnection"));
                    }
                    nikitaResponse.writeStream(nson.toJson());
                }else{
                    Nikitaset n = nc.Query("SELECT paramkey,paramvalue FROM web_parameter WHERE paramtype=? AND paramkey LIKE 'INIT%';", "mobile");
                    nikitaResponse.writeStream(n.toNset().toJSON());
                }           
                
                sysout(sbuffLogDebugId + Utility.formatCurrency( System.currentTimeMillis() - istart) + " ms");
                return;
            }else if (pathInfo.startsWith("/res/mobileparameter/initconnection/")) {           
                Nikitaset n = nc.Query("SELECT * FROM web_connection WHERE conntype=?;", "mobile");
                nikitaResponse.writeStream(n.toNset().toJSON());
                sysout(sbuffLogDebugId + Utility.formatCurrency( System.currentTimeMillis() - istart) + " ms");
                return;
            }else if (pathInfo.startsWith("/res/mobileparameter/")) {
                Nikitaset n = nc.Query("SELECT paramkey,paramvalue FROM web_parameter WHERE paramtype=? ;", "mobile");
                nikitaResponse.writeStream(n.toNset().toJSON());
                sysout(sbuffLogDebugId + Utility.formatCurrency( System.currentTimeMillis() - istart) + " ms");
                return;
            }else if (pathInfo.startsWith("/res/nikitastorage/")||pathInfo.equals("/res/nikitastorage")) {
                if (  NikitaConnection.getDefaultPropertySetting().getData("init").getData("storageauthserver").toString().contains( nikitaRequest.getParameter("storageauth") )  ) {
                    NikitaStorage.handleNikitaStorge(nikitaRequest, nikitaResponse);
                }                 
                sysout(sbuffLogDebugId + Utility.formatCurrency( System.currentTimeMillis() - istart) + " ms");
                return;
            }else if (pathInfo.startsWith("/res/nikitaconnection/")||pathInfo.equals("/res/nikitaconnection")) {
                String[] xpath = Utility.split(pathInfo+"///", "/");//[0]/[1]res/[2]nikitaconnection/[3]connname/...
                String conName = xpath[3];
                if (conName.equals("")) {
                    conName="default";
                }
                if (NikitaConnection.getDefaultPropertySetting().getData("init").getData("nikitaconnection").toString().contains(conName)) {
                    if (NikitaConnection.getDefaultPropertySetting().getData(conName).getData("user").toString().equals(nikitaRequest.getParameter("username")) && NikitaConnection.getDefaultPropertySetting().getData(conName).getData("pass").toString().equals(nikitaRequest.getParameter("password"))  ) {
                        if (nikitaRequest.getParameter("paging").contains(",")) {
                            String[] sArr = Utility.split(nikitaRequest.getParameter("paging"), ",");
                            Nikitaset n = resp.getConnection(conName).QueryPage(Utility.getInt(sArr[0]),Utility.getInt(sArr[1]), nikitaRequest.getParameter("sql"));
                            nikitaResponse.writeStream(n.toNset().toJSON());
                        }else{
                            Nikitaset n = resp.getConnection(conName).Query(nikitaRequest.getParameter("sql"));
                            nikitaResponse.writeStream(n.toNset().toJSON());
                        } 
                    }
                }
                sysout(sbuffLogDebugId + Utility.formatCurrency( System.currentTimeMillis() - istart) + " ms");
                return;                
            }else if (pathInfo.startsWith("/res/generator/")||pathInfo.equals("/res/generator")) {
                fname = nikitaRequest.getParameter("taskname");
            }else if (pathInfo.startsWith("/res/cloudmain/")||pathInfo.equals("/res/cloudmain")) {
                if (NikitaService.isModeCloud()) {
                    String prefix = NikitaService.getPrefixUserCloud(nc, req.getParameter("cloudusername"));
                    writeOut( prefix+"main",   resp);
                      
                }
                return;
            }else if (pathInfo.startsWith("/res/cloudform/")||pathInfo.equals("/res/cloudform")) {
                if (NikitaService.isModeCloud()) {
                    String prefix = NikitaService.getPrefixUserCloud(nc, req.getParameter("cloudusername"));
                    //titlefaviocin
                    writeOut( prefix+"main",   resp);
                      
                }
                return;
            }else if (pathInfo.startsWith("/res/resourcefn/")||pathInfo.equals("/res/resourcefn")) {                
                String path = NikitaConnection.getDefaultPropertySetting().getData("init").getData("resource").toString();                      
                String assetname = nikitaRequest.getParameter("name");      
                
                if (assetname.equals("") ) {
                    String[] xpath = Utility.split(pathInfo+"/", "/");//[0]/[1]res/[2]resource/[3]name/...
                    assetname = xpath[3];
                }
                
                try {
                    getResourceFile(new File(path+getFileSeparator()+assetname), req, resp,  "resource.png", nikitaRequest.getParameter("mode").equals("view")?false:true);
                } catch (Exception e) {  }
                     sysout(sbuffLogDebugId + Utility.formatCurrency( System.currentTimeMillis() - istart) + " ms");
                return;
            }else if (pathInfo.startsWith("/res/document/")||pathInfo.equals("/res/document")) {
                try {
                    String path = NikitaConnection.getDefaultPropertySetting().getData("init").getData("document").toString();
                    String name = nikitaRequest.getParameter("name"); 
                    
                    if (name.equals("") && name.equals("")) {
                        String[] xpath = Utility.split(pathInfo+"//", "/");//[0]/[1]res/[2]resource/[3]name/...
                        name = xpath[3];
                    }                     
                    getResourceFile(new File(path+getFileSeparator()+ name), req, resp, name, nikitaRequest.getParameter("attachment")!=""?true:false);
                    sysout(sbuffLogDebugId + Utility.formatCurrency( System.currentTimeMillis() - istart) + " ms");
                    return;
                } catch (Exception e) { }                
            }else if (pathInfo.startsWith("/res/resource/")||pathInfo.equals("/res/resource")) {
                try {
                    String path = NikitaConnection.getDefaultPropertySetting().getData("init").getData("resource").toString();
                    String resname = nikitaRequest.getParameter("resname");
                    String id = nikitaRequest.getParameter("resid");//
                    String where = " resname=? ";
                    String name = nikitaRequest.getParameter("name");       
                    if (!name.equals("") && resname.equals("")) {
                        resname=name;
                    }      
                    
                    if (name.equals("") && resname.equals("") && id.equals("")) {
                        String[] xpath = Utility.split(pathInfo+"/", "/");//[0]/[1]res/[2]resource/[3]name/...
                        resname = xpath[3];
                        id = xpath[3];
                    }
                    
                    if (Utility.isNumeric(id)) {
                        where="resid=?";
                        resname=id;
                    }     
                    if (new File(path+getFileSeparator()+"manifest.res").isFile()) {
                        try {
                            //resname = resfileid
                            String sb = Utility.readInputStreamAsString(path+getFileSeparator()+"manifest.res");
                            StringTokenizer stringTokenizer = new StringTokenizer(sb);
                            while (stringTokenizer.hasMoreElements()) {
                                String sct = String.valueOf(stringTokenizer.nextElement()).trim() ;
                                if (sct.contains("=")) {
                                    String[] arrsc = sct.split("=");
                                    if (resname.equals(arrsc[0].trim())) {
                                        getResourceFile(new File(path+getFileSeparator()+arrsc[1].trim()), req, resp, resname , nikitaRequest.getParameter("attachment")!=""?true:false);
                                        return;
                                    }
                                }
                            }                            
                        } catch (Exception e) {  }                         
                    }
                    Nikitaset ns = nc.Query("SELECT resid,resfname,restype,ressize,reshash FROM web_resource  WHERE  "+where, resname);
                    if (nikitaRequest.getParameter("hash").trim().equals("true")) {
                        nikitaResponse.writeStream(ns.getText(0, 4));
                    }else if (ns.getRows()>=1) {
                        getResourceFile(new File(path+getFileSeparator()+ns.getText(0,0)+".res"), req, resp, ns.getText(0,1) + "."+ns.getText(0,2), nikitaRequest.getParameter("attachment")!=""?true:false);
                    }else{
                        showPageNotFound(resp);
                    }                    
                    sysout(sbuffLogDebugId + Utility.formatCurrency( System.currentTimeMillis() - istart) + " ms");
                    return;
                } catch (Exception e) { }
            }else if (pathInfo.startsWith("/res/upload/")) {
                /*deprecated*/ 
                /*========================================================================================*/ 
                try {
                    String path = NikitaConnection.getDefaultPropertySetting().getData("init").getData("upload").toString();
                    String resname = nikitaRequest.getParameter("uploadname");
                    String id = nikitaRequest.getParameter("uploadid");
                    String where = " STIMAGE=? ";
                    if (Utility.isNumeric(id)) {
                        where="STID=?";
                        resname=id;
                    }
                    Nikitaset ns = nc.Query("SELECT STID,STIMAGE,STHASH FROM ACT_SURAT_TUGAS  WHERE  "+where, resname);
                    
                    
                    if (nikitaRequest.getParameter("hash").trim().equals("true")) {
                        nikitaResponse.writeStream(ns.getText(0, 2));
                    }else if (ns.getRows()>=1) {
                        int x=ns.getText(0,1).length();
                        x=x;
                        String c = ns.getText(0,1);
                        int y=c.indexOf(".");
                        String z="";
                        z=c.substring(y,x);
                        File  file = new File(path+getFileSeparator()+ resname);  
                        getResourceFile(file, req, resp,  resname  , nikitaRequest.getParameter("attachment")!=""?true:false);
                        getResourceFile(new FileInputStream(path+"\\"+ns.getText(0,0)+".res"), req, resp, ns.getText(0,1) + "."+z, false);
                    }else{
                        showPageNotFound(resp);
                    }                    
                    return;
                } catch (Exception e) { }    
            /*========================================================================================*/    
            }else if (pathInfo.startsWith("/res/storage/")||pathInfo.equals("/res/storage")) {   
                //save by name
                try {
                    String path = NikitaConnection.getDefaultPropertySetting().getData("init").getData("storage").toString();
                    String resname = nikitaRequest.getParameter("name");                          
                    String folder = nikitaRequest.getParameter("folder");      
                    String xmapfile = nikitaRequest.getParameter("xmapfile");
                    String dname = nikitaRequest.getParameter("dname");
                                       
                    if ( resname.equals("")) {
                        String[] xpath = Utility.split(pathInfo+"/", "/");//[0]/[1]res/[2]resource/[3]name/...
                        resname = xpath[3];
                    }                    
                    if (dname.trim().equalsIgnoreCase("")) {
                        dname = resname;
                    }
                    File  file = new File(path+folder+getFileSeparator()+ resname);                     
                    if (xmapfile.length()>=1 && new File(path+xmapfile).exists()){      
                        getResourceFile(new File(path+xmapfile), req, resp,  resname  , nikitaRequest.getParameter("attachment")!=""?true:false, dname);
                    }else if (file.exists()) {
                        getResourceFile(file, req, resp,  resname  , nikitaRequest.getParameter("attachment")!=""?true:false, dname);
                    }else{
                        showPageNotFound(resp);
                    }                    
                    sysout(sbuffLogDebugId + Utility.formatCurrency( System.currentTimeMillis() - istart) + " ms");
                    return;
                } catch (Exception e) { }
            }else if (pathInfo.startsWith("/res/master/data/")) {
                
            }else if (pathInfo.startsWith("/res/progress/")) {                
                //writeOut(nikitaResponse.getVirtualString("@+SESSION-NIKITA-PROGRESS-"+nikitaRequest.getParameter("prbarid")) , nikitaResponse.getHttpServletResponse());
                writeOut( AppNikita.getInstance().getVirtualString("@+SESSION-NIKITA-PROGRESS-"+nikitaRequest.getParameter("prbarid")) , nikitaResponse.getHttpServletResponse());
                sysout(sbuffLogDebugId + Utility.formatCurrency( System.currentTimeMillis() - istart) + " ms");
                return;
            }else if (pathInfo.startsWith("/res/programer/")||pathInfo.startsWith("/res/me/")||pathInfo.equals("/res/programer")||pathInfo.equals("/res/me")) {
                StringBuffer sb = new StringBuffer();
                sb.append(About.getAbout2());
                showPage("Nikita's Programer", sb.toString(), 200, resp);
                sysout(sbuffLogDebugId + Utility.formatCurrency( System.currentTimeMillis() - istart) + " ms");
                return;
            }else{
                
            }
        }else if (pathInfo.startsWith("/res/argument/")) {   
            String link = nikitaRequest.getParameter("name");  
            if (link.equals("")) {
                String[] xpath = Utility.split(pathInfo+"/", "/");//[0]/[1]res/[2]resource/[3]name/...
                link = xpath[3];
            }                    
            Nson nson = NikitaServlet.getLinkArgument(link);
            writeOut(nson.toJson(), nikitaResponse.getHttpServletResponse());
            sysout(sbuffLogDebugId + Utility.formatCurrency( System.currentTimeMillis() - istart) + " ms");
            return;
        }else if (pathInfo.startsWith("/static/priv/")) {
        }else if (pathInfo.startsWith("/static/lib1/")) {
            getResourcePriv(this, pathInfo.substring(8), req, resp);
            
            sysout(sbuffLogDebugId + Utility.formatCurrency( System.currentTimeMillis() - istart) + " ms");
            return;
        }else if (pathInfo.startsWith("/restart")||pathInfo.startsWith("/reconfig")||pathInfo.startsWith("/reload")) {
            NikitaConnection.setDefaultConnectionSetting();
            //configRunService(req, resp);
            StringBuffer sb = new StringBuffer();
            
            NikitaEngineManager.clear();
            NikitaFilter.resetConfig();
            
            
            sb.append("Configuration at ("+ Utility.Now() +") "+ Utility.formatCurrency( System.currentTimeMillis() - istart) + " ms"+" [OK]").append("<br><br>");;
            sb.append("<table>");
            sb.append("<tr><td><b>Nikita Generator </b></td><td> v."+AppNikita.APP_VER_CODE).append("<br></td></tr>");
            sb.append("</table><br>");
            
            //internal schedulre
            contextDestroyed();
            contextInitialized();
            
            //user shceduler
            schedullerDestroy();
            schedullerInit();
            
            AppNikita.getInstance().clearAllVirtual();
            
            showPage("Nikita Configuration", sb.toString(), 200, resp);
            sysout(sbuffLogDebugId + Utility.formatCurrency( System.currentTimeMillis() - istart) + " ms");
            return;
        }else if (pathInfo.startsWith("/link/")) {   
            
        }else if (pathInfo.startsWith("/static/")) {
            getResource(pathInfo.substring(7), req, resp);
            
            sysout(sbuffLogDebugId + Utility.formatCurrency( System.currentTimeMillis() - istart) + " ms");
            return;
        }else if (pathInfo.contains("/static/")) {
            pathInfo=pathInfo.substring(pathInfo.indexOf("/static/"));
            getResource(pathInfo.substring(7).trim(), req, resp );
            
            sysout(sbuffLogDebugId + Utility.formatCurrency( System.currentTimeMillis() - istart) + " ms");
            return;
        }else if (pathInfo.equals("/")||pathInfo.equals("")) {
            fname=NikitaConnection.getDefaultPropertySetting().getData("init").getData("startup").toString();
            sysout(sbuffLogDebugId + Utility.formatCurrency( System.currentTimeMillis() - istart) + " ms");
        }else{            
            
        }      
        
        NikitaLogic nikitaLogic =  new NikitaLogic();
        nikitaResponse.setNikitaLogic(nikitaLogic);
        
        if (fname.contains("/")) {
            fname = fname.substring(0, fname.indexOf("/"));
        }            
        if (req.getParameter("form")!=null) {
            fname=req.getParameter("form");
        }
        sLinkName.set(fname); 
        nikitaResponse.setServletFormName(fname);    
        
        nikitaResponse.setVirtualRegistered("@+FORMID", fname);
        nikitaResponse.setVirtualRegistered("@+FORMNAME", fname);
        nikitaResponse.setVirtualRegistered("@+ACTION", nikitaRequest.getParameter("action"));
        nikitaResponse.setVirtualRegistered("@+COMPONENTID", nikitaRequest.getParameter("component"));
        nikitaResponse.setVirtualRegistered("@+COMPONENTNAME", nikitaRequest.getParameter("component"));
        nikitaResponse.setVirtualRegistered("@+CONNECTIONCOUNT", icount);
        nikitaResponse.setVirtualRegistered("@+LOOP", 0);
        
        
        //filter
        String filter=NikitaConnection.getDefaultPropertySetting().getData("init").getData("filter").toString();
        String filterexp=NikitaConnection.getDefaultPropertySetting().getData("init").getData("filterexp").toString();
        String highscurity=NikitaConnection.getDefaultPropertySetting().getData("init").getData("scurity").toString();
        if (NikitaConnection.getDefaultPropertySetting().getData("init").containsKey("security")) {
            highscurity=NikitaConnection.getDefaultPropertySetting().getData("init").getData("security").toString();
        }
                
        
        
        boolean onfilter = (nikitaRequest.getParameter("action").equals("")|| nikitaRequest.getParameter("action").equals("ready"));
        boolean mustfilter = onfilter;
        if (highscurity.equals("smart")) {
            mustfilter=true;
        }              
         
       
        //filter
        if ((","+filterexp+",").contains(","+fname+",")){  
        }else if (isContainFilterExp(filterexp, fname)) {              
        }else if (!filter.equals("") && mustfilter) {  
            NikitaEngine engine = null ;
            Nikitaset nikitaset ;
            sLinkName.set(fname); 
            nikitaLogic.setFnameIndex(fname);//03022017

            if (NikitaEngine.isNv3Exist(filter, nikitaRequest.getParameter("runtime").equalsIgnoreCase("developer"))) {
                //engine = new NikitaEngine(nc, filter, nikitaRequest.getParameter("runtime").equalsIgnoreCase("developer") );
                engine =  NikitaEngineManager.getInstance(nc, filter, nikitaRequest.getParameter("runtime").equalsIgnoreCase("developer") );
                nikitaset = engine.getForm();
            }else{
                nikitaset = nc.QueryPage(1, 1, "SELECT formid,formname,formtitle,formtype,formstyle FROM web_form WHERE formname = ?",filter);
            }
            if (nikitaset.getRows()>=1) {     
                nikitaLogic.setForm(nikitaset.getText(0, 0), nikitaset.getText(0, 1), nikitaset.getText(0, 2), nikitaset.getText(0, 3), nikitaset.getText(0, 4));
                
                if (!runLocalServlet(fname, nikitaRequest, nikitaResponse, nikitaLogic, engine)) {
                    if (nikitaLogic.getFormType().equals("link")||nikitaLogic.getFormType().equals("scheduler")) {
                        new NikitaServlet().runFirst(nikitaRequest, nikitaResponse, nikitaLogic, engine);
                    }
                }  
               
                String fn= nikitaResponse.getServletFormName().equals("")?fname:nikitaResponse.getServletFormName(); 
                if (highscurity.equals("smart") && !fname.equals(fn)  && !onfilter) {
                    //if (!NikitaConnection.getDefaultPropertySetting().getData("init").getData("scurityauth").toString().trim().equals("")) {
                     //   nikitaResponse.setVirtual("@+SESSION-RELOG", "true");
                    //    nikitaResponse.runServletGen(NikitaConnection.getDefaultPropertySetting().getData("init").getData("scurityauth").toString(), nikitaRequest, nikitaResponse, nikitaLogic);
                    //    nikitaResponse.showform(NikitaConnection.getDefaultPropertySetting().getData("init").getData("scurityauth").toString(), nikitaRequest, "relogin", true);
                    //}else{
                   //    nikitaResponse.reloadBrowser();
                    //}   
                    nikitaResponse.setVirtual("@+SESSION-RELOG", "false");
                    String securityauth = NikitaConnection.getDefaultPropertySetting().getData("init").getData("scurityauth").toString();
                    if (NikitaConnection.getDefaultPropertySetting().getData("init").containsKey("securityauth")) {
                        securityauth=NikitaConnection.getDefaultPropertySetting().getData("init").getData("securityauth").toString();
                    }
                    nikitaResponse.runServletGen(securityauth, nikitaRequest, nikitaResponse, nikitaLogic);
                    //nikitaResponse.showform(nikitaResponse.getContent(),"",true); 
                    nikitaResponse.reloadBrowser();
                    nikitaResponse.write();
                    sysout(sbuffLogDebugId + Utility.formatCurrency( System.currentTimeMillis() - istart) + " ms");
                    return;
                }else{
                    fname = nikitaResponse.getServletFormName().equals("")?fname:nikitaResponse.getServletFormName(); 
                }
                 
                //fname = nikitaResponse.getServletFormName().equals("")?fname:nikitaResponse.getServletFormName(); 
            }else{                 
                runLocalServlet(filter, nikitaRequest, nikitaResponse, nikitaLogic, engine) ;
                
                String fn= nikitaResponse.getServletFormName().equals("")?fname:nikitaResponse.getServletFormName(); 
                if (highscurity.equals("smart") && !fname.equals(fn) && !onfilter ) {
                    nikitaResponse.setVirtual("@+SESSION-RELOG", "false");
                    String securityauth = NikitaConnection.getDefaultPropertySetting().getData("init").getData("scurityauth").toString();
                    if (NikitaConnection.getDefaultPropertySetting().getData("init").containsKey("securityauth")) {
                        securityauth=NikitaConnection.getDefaultPropertySetting().getData("init").getData("securityauth").toString();
                    }                    
                    nikitaResponse.runServletGen(securityauth, nikitaRequest, nikitaResponse, nikitaLogic);
                    //nikitaResponse.showform(nikitaResponse.getContent(),"",true);                   
                    nikitaResponse.reloadBrowser();
                    nikitaResponse.write();
                    sysout(sbuffLogDebugId + Utility.formatCurrency( System.currentTimeMillis() - istart) + " ms");
                    return;
                }else{
                    fname = nikitaResponse.getServletFormName().equals("")?fname:nikitaResponse.getServletFormName(); 
                }
                
                 nc = resp.getConnection(NikitaConnection.LOGIC);
                //fname = nikitaResponse.getServletFormName().equals("")?fname:nikitaResponse.getServletFormName(); 
            }
        }
        //filter
        //15/07/2015 add (mode)
        //Nikitaset filterset = nc.QueryPage(1, 1, "SELECT formid,formname,formtitle,formtype,formstyle FROM web_form WHERE  = ?",fname);

        if (NikitaService.isModeCloud() ) {
            String scn = nikitaRequest.getParameter("SERVER_CLOUD_NAME");
            if (scn.toLowerCase().startsWith("srv_") || scn.toLowerCase().startsWith("ref_")) { 
                scn = NikitaService.getPrefixUserCloud(resp.getConnection(NikitaConnection.LOGIC), scn.substring(4).trim());
            }else if (scn.toLowerCase().startsWith("@") || scn.toLowerCase().startsWith("#")|| scn.toLowerCase().startsWith("$")) { 
                scn = NikitaService.getPrefixUserCloud(resp.getConnection(NikitaConnection.LOGIC), scn.substring(1).trim());
            }else{
                scn = nikitaRequest.getParameter("SERVER_CLOUD_NAME")+"_";
            }
            
            if (fname.equalsIgnoreCase("mobile.mlogin")) {
                //scn to uid
                fname =  scn+"mlogin";
            }else if (fname.equalsIgnoreCase("mobile.mactivity")) {
                fname =  scn+"mactivity";
            }else if (fname.equalsIgnoreCase("mobile.morder")) {   
                fname =  scn+"morder";
            }else if (fname.equalsIgnoreCase("mobile.mdownloadorder")) {   
                fname =  scn+"mdownloadorder";
            }
        }        
        
        sLinkName.set(fname); 
        nikitaResponse.setServletFormName(fname);
        nikitaResponse.isConsumed=false;
        //open real servlet       
        
        
        boolean isID = Utility.isNumeric(fname);
        NikitaEngine engine = null ;
        Nikitaset nikitaset ;
        if (NikitaEngine.isNv3Exist(fname, nikitaRequest.getParameter("runtime").equalsIgnoreCase("developer"))) {
            //engine = new NikitaEngine(nc, fname, nikitaRequest.getParameter("runtime").equalsIgnoreCase("developer") );
            engine  = NikitaEngineManager.getInstance(nc, fname, nikitaRequest.getParameter("runtime").equalsIgnoreCase("developer") );
            nikitaset = engine.getForm();
        }else{ 
            // System.out.println("fnametoNsesssssssss");
            nikitaset = nc.QueryPage(1, 1, "SELECT formid,formname,formtitle,formtype,formstyle FROM web_form WHERE "+(isID?"formid":"formname")+" = ?",fname);
        }
        sLinkName.set(fname); 
        nikitaLogic.setFnameIndex(fname);//03022017
        nikitaLogic.setForm(nikitaset.getText(0, 0), nikitaset.getText(0, 1), nikitaset.getText(0, 2), nikitaset.getText(0, 3), nikitaset.getText(0, 4));
        if (isID && nikitaset.getText(0, 1).trim().length()>=1) {
            sbuffLogDebugId = sbuffLogDebugId + "{"+ nikitaset.getText(0, 1)+"} " ;
        }
        //System.out.println("fnametoNset:"+nikitaset.toNset().toJSON());
        if (nikitaset.getRows()>=1) {     
            nikitaLogic.setForm(nikitaset.getText(0, 0), nikitaset.getText(0, 1), nikitaset.getText(0, 2), nikitaset.getText(0, 3), nikitaset.getText(0, 4));
            if (nikitaLogic.getFormStyle().contains(":")) {
                Style style = Style.createStyle(nikitaLogic.getFormStyle()) ;
                nikitaLogic.setFormVersion(style.getInternalObject().getData("style").getData("version").toString(), style.getInternalObject().getData("style").getData("isprivate").toString().equals("true")||style.getInternalObject().getData("style").getData("n-isprivate").toString().equals("true"));
            }                
            if (!runPublicServlet(fname, nikitaRequest, nikitaResponse, nikitaLogic, engine)) {
                if (nikitaRequest.getHttpServletRequest().getPathInfo()!=null &&( nikitaRequest.getHttpServletRequest().getPathInfo().endsWith("/version")||nikitaRequest.getHttpServletRequest().getPathInfo().endsWith("/version/") )) {               
                    showPage("Version of "+ fname,  nikitaLogic.getFormVersion() , 200, nikitaResponse.getHttpServletResponse());
                }else if (nikitaRequest.getHttpServletRequest().getPathInfo()!=null &&( nikitaRequest.getHttpServletRequest().getPathInfo().endsWith("/argument")||nikitaRequest.getHttpServletRequest().getPathInfo().endsWith("/argument/") ||nikitaRequest.getHttpServletRequest().getPathInfo().endsWith("/argument?") )) {               
                    if (engine!=null) {
                        writeOut(NikitaServlet.getLinkArgument(engine).toJson(), nikitaResponse.getHttpServletResponse());
                    }else{
                        writeOut(NikitaServlet.getLinkArgument(fname).toJson(), nikitaResponse.getHttpServletResponse());
                    } 
                }else if (   !(nikitaLogic.isFormPrivate() && nikitaRequest.getParameter("action").equals(""))   ) {
                    new NikitaServlet().runFirst(nikitaRequest, nikitaResponse, nikitaLogic, engine);
                } 
            }              
        }else if (nikitaset.getError().length()>=1 && isID) {
            showPage("Server Error [500]", nikitaset.getError(), 500, resp);
        }else{
            //System.out.println("fname:"+fname);
            //System.out.println("fname:"+(engine!=null?engine.getComponents().toNset().toJSON():"null"));
            //showPage("Nikita Looping [505]", "Nikita Pusing, Looping terus <br> Nikita Mendeteksi, terjadi perputaran alias looping tanpa henti", 404, resp);
            if (!runPublicServlet(fname, nikitaRequest, nikitaResponse, nikitaLogic, engine)) {
                showPage("Page Not Found [404]", "Page Not Found", 404, resp);
            }  
        }

        long reqsize = 0;
        try {
            Enumeration<String> hdata = req.getParameterNames();              
                while (hdata.hasMoreElements()) {  
                String param = hdata.nextElement();
                reqsize+=req.getParameter(hdata.nextElement()).length()+param.length();
            }     
        } catch (Exception e) { } 
        
        sysout(sbuffLogDebugId + Utility.formatCurrency(System.currentTimeMillis() - istart) + " ms ("+ Utility.formatsizeKByte( reqsize ) +"/"+ Utility.formatsizeKByte( nikitaResponse.getAppxOutData() ) +"KB)"+ nikitaResponse.getInfoSize()+ ":"+Thread.currentThread().getName()  ); //+ ":"+Thread.currentThread().getName() 
        
    }
     public static boolean runActionClass(Nset action, NikitaRequest request, NikitaResponse response, NikitaLogic data){
        //response.writelnLog("runActionClass:"+action);
        try {
                               
            String classname = "com.nikita.generator.action."+action.get("class").toString();
            if (action.get("class").toString().equals("")) {
                return true; 
            }else if (action.get("class").toString().startsWith("com.")) {
                classname = action.get("class").toString();
            }       
            if (Application.actionAdapter!=null) {
                IAction v = Application.actionAdapter.onCreateAction(action, request, response, data);
                if (v!=null) {
                    return v.OnAction(action, request, response, data);
                }
            }
            IAction action1 = (IAction) Class.forName(classname).newInstance();
            return action1.OnAction(action, request, response, data);
        } catch (Exception e) { 
            //e.printStackTrace();
            String s = e.getMessage();
        }
        return false;
    }   
    public static boolean runExpressionClass(Nset expression, NikitaRequest request, NikitaResponse response, NikitaLogic data){
        //response.writelnLog("runExpressionClass:"+expression);
        try {           
            String classname = "com.nikita.generator.expression."+expression.get("class").toString();
            if (expression.get("class").toString().equals("")) {
                return data.expression; 
            }else if (expression.get("class").toString().startsWith("com.")) {
                classname = expression.get("class").toString();
            }   
            if (Application.expressionAdapter!=null) {
                IExpression v = Application.expressionAdapter.onCreateExpression(expression, request, response, data);
                if (v!=null) {
                    return v.OnExpression(expression, request, response, data);
                }
            }
            IExpression expression1 = (IExpression) Class.forName(classname).newInstance();
            return expression1.OnExpression(expression, request, response, data);
        } catch (Exception e) {    }
        return false;
    }   
    public static  boolean isClassExist(String className) {
        try  {
            Class.forName(className);
            return true;
        }  catch (final ClassNotFoundException e) {
            return false;
        }
    }
    public static void loopdetect(NikitaResponse response){
            try {
                int i = ((int)response.getVirtual("@+LOOP"))+1;
                if (i>=100000) {
                    response.setInterupt(true);//stop
                    System.err.println("Nikita Detect Looping without End");
                }
                response.setVirtualRegistered("@+LOOP", i );
            } catch (Exception e) { }
    }
    
    public static boolean isNikitaComponentExist(String name){
        try {
            Class.forName("com.nikita.forms."+name);
            return true;
        } catch (ClassNotFoundException ex) { 
            return false;            
        } 
    }
    public static boolean isNikitaServletExist(String name){
        try {
            Class.forName("com.servlet."+name);
            return true;
        } catch (ClassNotFoundException ex) { 
            return false;            
        } 
    }
    public static void runNikitaComponent(String name){
        try {
            Class.forName("com.nikita.forms."+name).newInstance();
            
        } catch (ClassNotFoundException ex) { 
            NikitaControler nc = new NikitaControler(); 
            
        } catch (InstantiationException ex) {
             
        } catch (IllegalAccessException ex) {
             
        }
    }
    public static boolean runPublicServlet(String name, NikitaRequest request, NikitaResponse response, NikitaLogic data){
        return runServlet(false, name, request, response, data);
    }
    public static boolean runPublicServlet(String name, NikitaRequest request, NikitaResponse response, NikitaLogic data, NikitaEngine nikitaEngine ){
        return runServlet(false, name, request, response, data, nikitaEngine);
    }
    public static boolean runLocalServlet(String name, NikitaRequest request, NikitaResponse response, NikitaLogic data){
        return runServlet(true, name, request, response, data);
    }
    public static boolean runLocalServlet(String name, NikitaRequest request, NikitaResponse response, NikitaLogic data, NikitaEngine nikitaEngine){
        return runServlet(true, name, request, response, data, nikitaEngine);
    }        
    private static boolean runServlet(boolean runlocal, String name, NikitaRequest request, NikitaResponse response, NikitaLogic data){
        return  runServlet(runlocal, name, request, response, data, null);
    }
    private static boolean runServlet(boolean runlocal, String name, NikitaRequest request, NikitaResponse response, NikitaLogic data, NikitaEngine nikitaEngine){
        try {
            //loopdetect(response);            
            if (Utility.isNumeric(name)) {
                if (request.getHttpServletRequest().getPathInfo()!=null &&( request.getHttpServletRequest().getPathInfo().endsWith("/version")||request.getHttpServletRequest().getPathInfo().endsWith("/version/"))) {               
                    showPage("Version of "+ name,  data.getFormVersion() , 200, response.getHttpServletResponse());
                    return true;                
                }
                return false;
            }
            NikitaServlet newServlet = (NikitaServlet) Class.forName("com.servlet."+name).newInstance();
             
            if (request.getHttpServletRequest().getPathInfo()!=null &&( request.getHttpServletRequest().getPathInfo().equals("/"+name+"/version")||request.getHttpServletRequest().getPathInfo().equals("/"+name+"/version/"))) {               
                showPage("Version of "+ name,  newServlet.getVersion(), 200, response.getHttpServletResponse());
            }else{  
                if (!newServlet.isPrivateName()||runlocal) {
                    newServlet.runFirst(request, response, data, nikitaEngine);
                }  
            }      
            return true;
        } catch (Exception e) {   }//System.err.println(e.getMessage());
        return false;
    }
    
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        NikitaServletResponse nsr = new NikitaServletResponse (req,resp);
        doRun(req, nsr); 
        nsr.closeAllConnection();
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        NikitaServletResponse nsr = new NikitaServletResponse (req,resp);
        doRun(req, nsr); 
        nsr.closeAllConnection();
    }

    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        NikitaServletResponse nsr = new NikitaServletResponse (req,resp);
        doRun(req, nsr); 
        nsr.closeAllConnection();
    }    
    
    public static void showPageNotFound(HttpServletResponse resp){
        showPage("Page Not Found [404]", "<h1>Page Not Found <h1>", 404, resp);
    }
    
    public static void showPage(String title, String message, int status, HttpServletResponse response){
        try {
            response.setContentType("text/html;charset=UTF-8");
            response.setStatus(status);
            PrintWriter out = response.getWriter();
            out.write("<!DOCTYPE html><html><head> <link rel=\"shortcut icon\" href=\""+getBaseContext()+"/static/img/icon.png\" type=\"image/x-icon\" /> <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">  <title>"+title+"</title> </head>  <body>"+message+" </body></html>");
        } catch (Exception e) {  }
    }
    public static void writeOut(String message, HttpServletResponse response){
        try {
            response.setStatus(200);
            response.setContentType("text/plain;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.write(message);
        } catch (Exception e) {  }
    }
    private static String getMimeType(String fname, String mime){
        if (fname.toLowerCase().endsWith("css")) {
            mime="text/css"; 
        }else if (fname.toLowerCase().endsWith("js")) {
            mime="application/x-javascript"; 
        }else if (fname.toLowerCase().endsWith("gen")) {
            mime="application/pdf"; 
        }else if (fname.toLowerCase().endsWith("xls")) {
            mime="application/vnd.ms-excel"; 
        }else if (fname.toLowerCase().endsWith("pdf")) {
            mime="application/pdf"; 
        }else if (fname.toLowerCase().endsWith("apk")) {
            mime="application/vnd.android.package-archive"; 
        }else if (mime==null) {
            mime="text/plain"; 
        }        
        return mime;
    }
        
    public static void getResourcePriv(NikitaService ns, String resname, HttpServletRequest request, HttpServletResponse response){
        String fname = resname;
        if (resname.lastIndexOf("/")>=0) {
            fname = resname.substring(resname.lastIndexOf("/")+1);
        }
            
        try {
            FileNameMap fileNameMap = URLConnection.getFileNameMap();
            String mimeType = fileNameMap.getContentTypeFor(fname);
            mimeType = getMimeType(fname, mimeType);
            
            response.setContentType(mimeType); 
            response.setHeader("content-disposition", "inline; filename=\""+fname+"\"");//"Content-Disposition","attachment"
     
            ServletContext context = request.getServletContext();
            InputStream is = ns.getClass().getClassLoader().getResourceAsStream(resname);
            
            
            //is = new FileInputStream("D://!rkrzmail/NetBeansProjects/NikitaWeb/NikitaWeb/build/web"+resname);
            if (is != null) {
                if (request.getParameter("thumb")!=null && request.getParameter("thumb").equals("true")) {
                    Thumbnails.of(is).size(128, 128).toOutputStream(response.getOutputStream());
                    return;
                }
                response.setContentLength(is.available());
                OutputStream out = response.getOutputStream();
                byte[] data = new byte[1024];int len=0;
                while ((len = is.read(data) )!=-1 ) {
                    out.write(data,0, len);
                }
                out.flush();
                out.close();   
                is.close();
            }else{
               showPage("500 Page Error", "Resource not found " , 500, response);
            }
        } catch (Exception e) { 
             showPage("500 Page Exception", e.getMessage(), 500, response);
        }
    }
    private static boolean matches(String matchHeader, String toMatch) {
        String[] matchValues = matchHeader.split("\\s*,\\s*");
        Arrays.sort(matchValues);
        return Arrays.binarySearch(matchValues, toMatch) > -1
            || Arrays.binarySearch(matchValues, "*") > -1;
    }
    //depreceted
    @Deprecated
    public static void getResourceFile(InputStream is, HttpServletRequest request, HttpServletResponse response, String fname, boolean attachment){
          try {
            FileNameMap fileNameMap = URLConnection.getFileNameMap();
            String mimeType = fileNameMap.getContentTypeFor(fname);
            mimeType = getMimeType(fname, mimeType);
             
            response.setContentType(mimeType); 
                response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
                response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
                response.setHeader("Cache-Control", "max-age=0");                
            response.setHeader("content-disposition",(attachment?"attachment;":"inline;")+" filename=\""+fname+"\"");//"Content-Disposition","attachment"

            //is = new FileInputStream("D://!rkrzmail/NetBeansProjects/NikitaWeb/NikitaWeb/build/web"+resname);
            if (is != null) {
                if (request.getParameter("thumb")!=null && request.getParameter("thumb").equals("true")) {
                    imageThumbnails(is, request, response);
                    
                    return;
                }
                //response.setContentLength(is.available());
                OutputStream out = response.getOutputStream();
                byte[] data = new byte[1024];int len=0;
                while ((len = is.read(data) )!=-1 ) {
                    out.write(data,0, len);
                }
                out.flush();
                out.close();   
                is.close();
            }else{
               showPage("500 Page Error", "Resource not found " , 500, response);
            }
        } catch (Exception e) { 
             showPage("500 Page Exception", e.getMessage(), 500, response);
        } 
    }
     public static void getResourceFile(final File is, final HttpServletRequest request, final HttpServletResponse response, final String fname, final boolean attachment) {
        getResourceFile(is, request, response, fname, attachment, fname);
    }
    
    public static void getResourceFile(final File is, final HttpServletRequest request, final HttpServletResponse response, final String fname, final boolean attachment, final String sdownload) {         
            // Validate request headers for caching ---------------------------------------------------
        int age = 86400;//1day;
        long lastModified = is.lastModified();
            String eTag =  Utility.MD5(is.getAbsolutePath()+ ":" +fname+":"+lastModified );                
            long expires = System.currentTimeMillis() + age;//1day
            // If-None-Match header should contain "*" or ETag. If so, then return 304.
            String ifNoneMatch = request.getHeader("If-None-Match");
            if (ifNoneMatch != null && matches(ifNoneMatch, eTag)) {
                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                response.setHeader("ETag", eTag); // Required in 304.
                response.setDateHeader("Expires", expires); // Postpone cache with 1 week.

                return;
            }
            // If-Modified-Since header should be greater than LastModified. If so, then return 304.

            // This header is ignored if any If-None-Match header is specified.
            long ifModifiedSince = request.getDateHeader("If-Modified-Since");
            if (ifNoneMatch == null && ifModifiedSince != -1 && ifModifiedSince + 1000 > lastModified) {
                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                response.setHeader("ETag", eTag); // Required in 304.
                response.setDateHeader("Expires", expires); // Postpone cache with 1 week.

                return;
            }
            //---------------------------------------------------------------------//
        try {
            FileNameMap fileNameMap = URLConnection.getFileNameMap();
            String mimeType = fileNameMap.getContentTypeFor(fname);
            mimeType = getMimeType(fname, mimeType);

            response.setContentType(mimeType); 
            response.setHeader("Cache-Control", "public, max-age="+age);        
            response.setHeader("content-disposition",(attachment?"attachment;":"inline;")+" filename=\""+sdownload +"\"");//"Content-Disposition","attachment"
            
            response.addDateHeader("last-modified", lastModified); 
            response.addDateHeader("expires",  expires); //10day        
            response.addHeader("etag", eTag ); 
            response.setContentLength( Long.valueOf(is.length()).intValue() );
            
            sendResource(new FileInputStream(is), request, response, fname, attachment);
        } catch (FileNotFoundException e) {  }
    }
    public static void getResourceStream(InputStream is, HttpServletRequest request, HttpServletResponse response, String fname, boolean attachment){
        getResourceStream(is, request, response, null, fname, attachment);
    } 
    public static void getResourceStream(InputStream is, HttpServletRequest request, HttpServletResponse response, Hashtable<String, String> header, String fname, boolean attachment){
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String mimeType = fileNameMap.getContentTypeFor(fname);
        mimeType = getMimeType(fname, mimeType);
        
        response.setContentType(mimeType); 
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
        response.setHeader("Cache-Control", "max-age=0");                
        response.setHeader("content-disposition",(attachment?"attachment;":"inline;")+" filename=\""+fname+"\"");//"Content-Disposition","attachment"
       
        if (header!=null) {
            Nset nheader = new Nset(header) ;
            String[] keyheader =  nheader.getObjectKeys();			
            for (int i = 0; i < keyheader.length; i++) {
                response.setHeader(keyheader[i], nheader.getData(keyheader[i]).toString() );
            }	
        }
                
       //response.setContentLength( 0);
        
        sendResource(is, request, response, fname, attachment);
    }
    
        
    private static void sendResource(InputStream is, HttpServletRequest request, HttpServletResponse response, String fname, boolean attachment){
        try {
             
            ServletContext context = request.getServletContext();           
            //is = new FileInputStream("D://!rkrzmail/NetBeansProjects/NikitaWeb/NikitaWeb/build/web"+resname);
            if (is != null) {
                if (request.getParameter("thumb")!=null && request.getParameter("thumb").equals("true")) {
                    imageThumbnails(is, request, response);
                    
                    return;
                }
                
                response.setContentLength(is.available());
                OutputStream out = response.getOutputStream();
                
                String nikita64 = request.getParameter("nikita64");
                if (nikita64 == null || String.valueOf(nikita64) == "") {                      
                    byte[] data = new byte[1024];int len=0;
                    while ((len = is.read(data) )!=-1 ) {
                        out.write(data,0, len);
                    }                    
                }else{
                    com.naa.data.Nson nArgs = com.naa.data.Nson.readJson(Utility.decodeBase64(nikita64));
                    String s =Utility.readInputStreamAsString(is);
                    com.naa.data.Nson keys = nArgs.getObjectKeys();
                    for (int i = 0; i < keys.size(); i++) {
                        String key = keys.get(i).asString();
                        s = Utility.replace(s, key, nArgs.get(key).asString());
                    }
                    out.write(s.getBytes());
                }
                
                out.flush();
                out.close();   
                is.close();
            }else{
               showPage("500 Page Error", "Resource not found " , 500, response);
            }
        } catch (IOException e) { 
             showPage("500 Page Exception", e.getMessage(), 500, response);
        }
    }
    public static void getResource(String resname, HttpServletRequest request, HttpServletResponse response){
            /*
            String fileName = "/path/to/file";
            MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
            // only by file name
            String mimeType = mimeTypesMap.getContentType(fileName);
            // or by actual File instance
            File file = new File(fileName);
            mimeType = mimeTypesMap.getContentType(file);
            */ 
        String fname = resname;
        if (resname.lastIndexOf("/")>=0) {
            fname = resname.substring(resname.lastIndexOf("/")+1);
        }
            
        try {
            FileNameMap fileNameMap = URLConnection.getFileNameMap();
            String mimeType = fileNameMap.getContentTypeFor(fname);
            mimeType = getMimeType(fname, mimeType);
            
            response.setContentType(mimeType); 
            
            if (request.getParameter("attachment")!=null) {
                response.setHeader("content-disposition","attachment; filename=\""+fname+"\"");//"Content-Disposition","attachment"
            }else{
                response.setHeader("content-disposition", "inline; filename=\""+fname+"\"");//"Content-Disposition","attachment"
            }
     
            ServletContext context = request.getServletContext();
           
            InputStream is = context.getResourceAsStream(resname);             
            
            
            //is = new FileInputStream("D://!rkrzmail/NetBeansProjects/NikitaWeb/NikitaWeb/build/web"+resname);
            if (is != null) {
                if (request.getParameter("thumb")!=null && request.getParameter("thumb").equals("true")) {
                    imageThumbnails(is, request, response);
                    return;
                }
                response.setContentLength(is.available());
                OutputStream out = response.getOutputStream();
                byte[] data = new byte[1024];int len=0;
                while ((len = is.read(data) )!=-1 ) {
                    out.write(data,0, len);
                }
                out.flush();
                out.close();
                is.close();
            }else{
               showPage("500 Page Error", "Resource not found " , 500, response);
            }
        } catch (Exception e) { 
             showPage("500 Page Exception", e.getMessage(), 500, response);
        }
    }
    private static synchronized void imageThumbnails(InputStream is,HttpServletRequest request, HttpServletResponse response) throws IOException{
        Thumbnails.of(is).size(128, 128).toOutputStream(response.getOutputStream());
        //BufferedImage srcImg = ImageIO.read(is); 
        //BufferedImage thumbnail = Scalr.resize(srcImg , Scalr.Method.SPEED, Scalr.Mode.FIT_TO_WIDTH,  150, 100, Scalr.OP_ANTIALIAS);
        //ImageIO.write(thumbnail, "png", response.getOutputStream());
                    
    }                
    public static synchronized void sysout(String s){
        if (!NikitaConnection.getDefaultPropertySetting().getData("init").getData("sysout").toString().trim().startsWith("false")) {
            if (NikitaConnection.getDefaultPropertySetting().getData("init").getData("sysout").toString().trim().contains("nostatic")) {
                if (s.contains(": /static/")) {
                    return;
                }
            }
            System.out.println(s);// + " {"+ String.valueOf(Thread.currentThread().getName())+"}"
        }       
    }
    private static String humanReadableDate(long ms, boolean viewms) {
        long msecond =          (ms % 1000);     
            ms = (ms - msecond)/1000;
        long second =    (ms % 60);
            ms = (ms- second)/60 ;
        long minute =  (ms % 60);
            ms = (ms - minute)/60 ;
        long hour =  (ms % 24);
            ms = (ms- hour)/24;  
        long day =  ms;
           
        StringBuilder builder = new StringBuilder();
        if (day>=1) {
            builder.append( (day)).append(" days ");
        }
        if (hour>=1) {
            builder.append(hour).append(":");
        }else if (hour+day>=1) {
            builder.append(hour).append(":");
        }
        if (minute>=1) {
            builder.append(minute).append(":");
        }else if (minute+hour+day>=1) {
            builder.append(minute).append(":");
        }
        if (second>=1) {
            builder.append(second);
        }else if (minute+hour+day>=1) {
            builder.append(second);
        }
        if (viewms) {
            builder.append(".").append(msecond);
        }
        return builder.toString();
    }     
    private static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "KMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
    private static String fileaccess(String path){
        boolean bread = false;
        boolean bwrite = false;
        boolean bexist = false;
        boolean bfolder = false;
        try {
            bread = new File(path).canRead();
        } catch (Exception e) {   }
        try {
            bwrite = new File(path).canWrite();
        } catch (Exception e) {   }
        try {
            bexist = new File(path).exists();
        } catch (Exception e) {   }
        try {
            bfolder = new File(path).isDirectory();
        } catch (Exception e) {   }
        return (bexist?"exist":"     ") +"."+ (bfolder?"folder":"     ")+"."+(bread?"read":"    ") +"."+ (bwrite?"write":" ") ;
    }    
    public static boolean isModeCloud(){        
        return  NikitaConnection.getDefaultPropertySetting().getData("init").getData("mode").toString().equalsIgnoreCase("cloud");
    }
    public static String getPrefixUserCloud(NikitaConnection connection, String user){          
        return  "u"+connection.Query("SELECT userid FROM sys_user WHERE username=?", user).getText(0, 0)+"_";
    }
    public static String getUserCloudCode(NikitaConnection connection, String ucode){    
        if (ucode.startsWith("u")) {
            ucode = ucode.substring(1); 
            if (Utility.isNumeric(ucode)) {  
                if (NikitaConnection.getDefaultPropertySetting().getData("init").getData("ucloud").toString().equalsIgnoreCase("true")) {                    
                }else{
                    return ""; 
                }                
            }else{
                return ucode;
            }
        }
        if (ucode.startsWith("@")||ucode.startsWith("#")||ucode.startsWith("$")) {
            return ucode.substring(1);
        } 
        if (ucode.startsWith("srv_")||ucode.startsWith("ref_")) {
            return ucode.substring(4);
        }
        return  connection.Query("SELECT username FROM sys_user WHERE userid=?", ucode).getText(0, 0);
    }
    public static boolean isPrefixCloud(){
        return  NikitaConnection.getDefaultPropertySetting().getData("init").getData("cloudprefix").toString().equalsIgnoreCase("true");
    }
}
