/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator.action;

import com.naa.data.Nson;
import com.naa.utils.InternetX;
import com.nikita.generator.Component;
import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaReport;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaService;
import static com.nikita.generator.NikitaService.getFileSeparator;
import com.nikita.generator.NikitaServlet;
import com.nikita.generator.Style;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.connection.NikitaInternet;
import com.nikita.generator.ui.layout.NikitaForm;
import com.rkrzmail.nikita.data.NikitaSpreadsheet;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsReportConfiguration;


/**
 *
 * @author rkrzmail
 */
public class ReportAction  implements IAction{
    
    private Nset getArguments(String param1){
        Nset args = Nset.newObject();
        if (param1.contains("?")) {
            param1=param1.substring(param1.indexOf("?")+1);
            Vector<String> params = Utility.splitVector(param1, "&");
            for (int i = 0; i < params.size(); i++) {                         
                if (params.elementAt(i).contains("=")) {
                    args.setData(params.elementAt(i).substring(0, params.elementAt(i).indexOf("=")), Utility.decodeURL(params.elementAt(i).substring(params.elementAt(i).indexOf("=")+1)) );
                }else{
                    args.setData(params.elementAt(i), "");
                }
            }
        }
        return args;
    }
    @Override
    public boolean OnAction(Nset currdata, NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        String code = response.getVirtualString(currdata.getData("code").toString());  
InputStream inputStream = null;

        if (code.equals("report")) {              
            String param1 = response.getVirtualString(currdata.getData("args").getData("param1").toString());  
            
            
            Object param2 = response.getVirtual(currdata.getData("args").getData("param2").toString());//arg
            
            
            String param4 = response.getVirtualString(currdata.getData("args").getData("param4").toString());//datasaource
            Object param5 = response.getVirtual(currdata.getData("args").getData("param5").toString());//nikitaset
            String param5s = currdata.getData("args").getData("param5").toString();
            
            String param6 = response.getVirtualString(currdata.getData("args").getData("param6").toString());  //pdf/xls
            String param7 = response.getVirtualString(currdata.getData("args").getData("param7").toString());  //inline
            String param8 = (currdata.getData("args").getData("param8").toString());  //target
  
            param6= param6.equals("xls")?"xls":"pdf";
                    
            
            if (param1.startsWith("http://")||param1.startsWith("https://")) {
                try {
                    inputStream = NikitaInternet.getHttp(param1).getEntity().getContent();
                } catch (Exception e) {   }
            }else if (param1.startsWith("res/resource/")||param1.startsWith("/res/resource/")) {
                Nset args = getArguments(param1);
                
                String path = NikitaConnection.getDefaultPropertySetting().getData("init").getData("resource").toString();
                String resname =args.getData("resname").toString();
                String id = args.getData("id").toString();
                String name = args.getData("name").toString();       
                if (!name.equals("") && resname.equals("")) {
                    resname=name;
                }     
                String where = " resname=? ";
                if (Utility.isNumeric(id)) {
                    where="resid=?";
                    resname=id;
                }                   
                Nikitaset ns = response.getConnection().Query("SELECT resid,resfname,restype,ressize,reshash FROM web_resource  WHERE  "+where, resname);
                if (ns.getRows()>=1) {
                    try {
                        inputStream= new FileInputStream(path+getFileSeparator()+ns.getText(0,0)+".res");
                    } catch (FileNotFoundException ex) { }                     
                }   
            }else if (param1.startsWith("res/storage/")||param1.startsWith("/res/storage/")) {
                Nset args = getArguments(param1);                
                try {
                    String path = NikitaConnection.getDefaultPropertySetting().getData("init").getData("storage").toString();
                    String resname = args.getData("resname").toString();                       
                     
                    File  file = new File(path+getFileSeparator()+ resname);                     
                    if (file.exists()) {
                        inputStream = new FileInputStream(file);
                    }
                } catch (Exception e) { }
            }else{
                
            }
       
            Nset args = Nset.newObject();
            if (param2 instanceof Nset) {
                args = (Nset)param2;
            }
            Nikitaset data = new Nikitaset("");
            if (param5 instanceof Nikitaset) {
                data = (Nikitaset)param5;
            }else if (param5 instanceof Nset) {
                data = new Nikitaset( (Nset)param5);
            }           
            
            if (param7.equals("att")||param7.equals("print")) {
                String xname = "report"+Utility.MD5(System.currentTimeMillis()+response.getVirtualString("@+SESSION-LOGON-USER")+response.getVirtualString("@+RANDOM"));
                try {                       
                    //response.getHttpServletResponse().setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
                    //response.getHttpServletResponse().setHeader("Pragma", "no-cache"); // HTTP 1.0.
                    //response.getHttpServletResponse().setHeader("Cache-Control", "max-age=0");                
                    //response.getHttpServletResponse().setHeader("content-disposition","attachment; filename=\"report"+(param5.equals("xls")?"xls":"pdf")+"\"");//"Content-Disposition","attachment"

                    //openReport( param5.equals("xls")?"xls":"pdf", param6, null, response.getHttpServletResponse().getOutputStream());
                    
                    OutputStream os = new FileOutputStream(NikitaService.getDirTmp() + NikitaService.getFileSeparator() +xname+".tmp");
                    openReport(request, response, param6, param4, param5s, args, data,  inputStream, os);
                    os.close();
                } catch (Exception e) { 
                    e.printStackTrace();
                } 
                if (param7.equals("print")) {
                    String printer = response.getVirtualString(currdata.getData("args").getData("param8").toString());
                    String url = NikitaService.getBaseUrl() +"/res/file/?file="+xname+"&filename=report."+param6;
                    response.getPrintPDF(printer, printer, url);//req,printer/url
                }else{
                    response.openWindows(NikitaService.getBaseContext() +"/res/file/?file="+xname+"&filename=report."+param6, "_self"); 
                }                
            }else if (param7.equals("dialog")||param7.equals("window")) {
                String xname = "report"+Utility.MD5(System.currentTimeMillis()+response.getVirtualString("@+SESSION-LOGON-USER")+response.getVirtualString("@+RANDOM"));
                try {                       
                    OutputStream os = new FileOutputStream(NikitaService.getDirTmp() + NikitaService.getFileSeparator() +xname+".tmp");
                    openReport(request,response, param6, param4, param5s, args, data,  inputStream, os);
                    os.close();
                } catch (Exception e) { 
                    e.printStackTrace();//ddd
                    
                } 
                Nset n = Nset.newObject();
                n.setData("filename", "report."+param6);
                n.setData("file", xname);
                n.setData("inline", "true");
                //response.openDialogPopUp(NikitaService.BASE_CONTEXT +"/res/file/?view="+Utility.encodeBase64(n.toJSON()),""); 
           
                if (param7.equals("dialog")) {
                    xname=xname.substring(6);
                    response.setVirtualRegistered("@+SESSION-"+xname, n.toJSON());
                    response.openDialogPopUp(NikitaService.getBaseContext() +"/res/file/?iv="+xname,""); 
                }else{
                    final StringBuffer sb= new StringBuffer();
                    sb.append("<iframe name  src=\"").append(NikitaService.getBaseContext() +"/res/file/?inline=true&file="+xname+"&filename=report."+param6).append("\"   style=\"border:0px;width:100%;height:100%\"  alt=\"Nikita Gebnerator\"  ></iframe>" );
                    NikitaForm nf = new NikitaForm("reportdialog");
                    nf.setStyle(new Style().setStyle("n-div-height", "100%"));
                    nf.setText("Report Preview");
                    nf.addComponent(new Component(){
                        public String getView() {                        
                            return sb.toString();
                        }                    
                    });
                    response.showform(nf, code, true);
                }
            }else{
                String xname = "report"+Utility.MD5(System.currentTimeMillis()+response.getVirtualString("@+SESSION-LOGON-USER")+response.getVirtualString("@+RANDOM"));
                try {                         
                    OutputStream os = new FileOutputStream(NikitaService.getDirTmp() + NikitaService.getFileSeparator() +xname+".tmp");
                    openReport(request,response, param6, param4, param5s, args, data,  inputStream, os);
                    os.close();
                } catch (Exception e) { 
                e.printStackTrace();
                }                 
                //default/inline
                StringBuffer sb= new StringBuffer();
                sb.append("<iframe name  src=\"").append(NikitaService.getBaseContext() +"/res/file/?inline=true&file="+xname+"&filename=report."+param6).append("\"   style=\"border:0px;width:100%;height:100%\"  alt=\"Nikita Gebnerator\"  ></iframe>" );
                
                response.showContentform( response.getComponent(param8).getJsId(), sb.toString());
            }
         }else if (code.equals("export")) {           
            response.setVirtual("@+EXPORT-MODE", "");
             
            //param1 =nikitaset to xls|csv|json|xml
            Object param1 = response.getVirtual(currdata.getData("args").getData("param1").toString());//nikitaset  
            String param2 = response.getVirtualString(currdata.getData("args").getData("param2").toString());//nama file
            final Object param10 = response.getVirtual(currdata.getData("args").getData("param3").toString());
            Nset option = Nset.newObject();
            if (param10 instanceof Nset && ((Nset)param10).isNsetObject()) {
                option = (Nset)param10;
            }
            
            if (param1 instanceof Nikitaset) {
                Nikitaset nikitaset = (Nikitaset)param1;                 
                String xname = "report"+Utility.MD5(System.currentTimeMillis()+response.getVirtualString("@+SESSION-LOGON-USER")+response.getVirtualString("@+RANDOM"));
                String fname = NikitaService.getDirTmp() + NikitaService.getFileSeparator() + xname + ".tmp";
               
                if (option.getData("format").toString().equalsIgnoreCase("csv")) {
                    try {
                        final OutputStream os2 = new FileOutputStream(fname);
                        NikitaSpreadsheet.saveToCsv(false, nikitaset, os2);
                        os2.close();
                    }
                    catch (Exception e3) {}
                }
                else {
                    final String sheetname = option.containsKey("sheet") ? option.getData("sheet").toString() : "export";
                    try {
                        final OutputStream os3 = new FileOutputStream(fname);
                        if (option.getData("format").toString().equalsIgnoreCase("xlsx")) {
                            NikitaSpreadsheet.saveToExcelx(sheetname, nikitaset, os3, option);
                        }
                        else {
                            NikitaSpreadsheet.saveToExcel(sheetname, nikitaset, os3);
                        }
                        os3.close();
                    }
                    catch (Exception ex3) {}
                }
                if (param2.equalsIgnoreCase("")) {
                    param2 = "report.xls";
                }
                if (option.getData("target").toString().equalsIgnoreCase("url")) {
                    response.setVirtual("@EXPORT-URL", NikitaService.getBaseContext() + "/res/file/report.xls?file=" + xname + "&filename=" + param2);
                }
                else if (option.getData("target").toString().equalsIgnoreCase("post")) {
                    InternetX.postHttpConnectionMultipart(option.getData("target-url").toString(), Nson.newObject(), Nson.newObject(), Nson.newObject().set("export", fname));
                }
                else {
                    response.openWindows(NikitaService.getBaseContext() + "/res/file/report.xls?file=" + xname + "&filename=" + param2, "_self");
                }               
              
            }
        }        
        return true; 
    }
    
    private static JREmptyDataSource nikitasettoDataSource(Nikitaset nikitaset,final NikitaResponse res){
        return new JREmptyDataSource(nikitaset.getRows()){
            private Nikitaset nikitaset;
            public JREmptyDataSource create(Nikitaset nikitaset){
               this.nikitaset=nikitaset;
                return this;
            }
            int icount =-1;
            public Object getFieldValue(JRField field) {
                if (field.getName().equals("NIKITAROWNUMBER")) {
                    return (icount+1);
                }else if (field.getName().equals("NIKITAROWCOUNT")) {
                    return (nikitaset.getRows());
                }  
//                //26/06/2016 not used
                if (field.getName().startsWith("@") && field.getName().contains(".")) {
                    String vname = field.getName().substring(0,field.getName().indexOf("."));
                    String fname = field.getName().substring(field.getName().indexOf(".")+1);
                    if (res.getVirtual(vname) instanceof Nikitaset) {
                        Nikitaset ns = (Nikitaset)res.getVirtual(vname);
                        return ns.getText(icount, fname);  
                    }
                }                   
                return nikitaset.getText(icount, field.getName());  
            }
            protected String getPropertyName(JRField field) {
                return nikitaset.getHeader(icount);  
            }                    
            public void moveFirst() {
                icount=0;
            }
            public boolean next() {
                if (icount<nikitaset.getRows()-1) {
                    icount++;
                    return true;
                } else  if (icount==-1) {
                    icount++;
                    return true;
                }   
                return false;
            }  
        }.create(nikitaset);
    }
    public static void generateReport(NikitaRequest req, NikitaResponse res, String pdfxlstype, String datasource,  String nikitaconnection,  Nset args, Nikitaset nikitaset , InputStream jasper, OutputStream outputStream)  {     
        try {
            openReport(req, res, pdfxlstype, datasource, nikitaconnection, args, nikitaset, jasper, outputStream);
        } catch (Exception e) {  }
    }       
    private static void openReport(final NikitaRequest req, final NikitaResponse res, String type, String datasource,  String nikitaconnection,   Nset args, final Nikitaset nikitaset , InputStream inputStream, OutputStream outputStream) throws JRException  {         
            /*
            //JRXML to jasper
            System.out.println("Compiling Report Design ...");
            try {
                Utility.copyFile(inputStream, "D://a.xml");
                JasperCompileManager.compileReportToFile("D://a.xml","D://a.jasper" );
            } catch (Exception e) {}       
            */
            
            
            //data parameter berupa nset
            Hashtable vMap = new Hashtable() ;
            if (args.isNsetObject()) {
                vMap = (Hashtable)args.getInternalObject();
            }
            if (!vMap.containsKey("BASE_URL")) {
                 vMap.put("BASE_URL", NikitaService.getBaseUrl());
            }
            if (!vMap.containsKey("BASEURL")) {
                 vMap.put("BASEURL", NikitaService.getBaseUrl());
            }
            if (!vMap.containsKey("urlbase")) {
                 vMap.put("urlbase", NikitaService.getBaseUrl());
            } 
            
            vMap.put("nikitavm", "runtime");//nikita virtual mechin
           
            Hashtable<String, Object> hashMap = new Hashtable() ;
            Nset nvMap = new Nset(vMap);
            String[] keys  = nvMap.getObjectKeys();
            for (int i = 0; i < keys.length; i++) {
                hashMap.put(keys[i], nvMap.getData(keys[i]).toString());            
            }
            
            String base = NikitaService.getBaseUrl();
            if (NikitaConnection.getDefaultPropertySetting().getData("init").containsKey("nikitareportimagebaseurl")) {
                base = NikitaConnection.getDefaultPropertySetting().getData("init").getData("nikitareportimagebaseurl").toString();
            }
            if (args.containsKey("nikitareportimagebaseurl")) {
                base = args.getData("nikitareportimagebaseurl").toString();
            }
            
            final String baseImageString = base;
            
            hashMap.put("nikitareport",  new NikitaReport(){
                public boolean isNikitaRuntime() {
                    return true;
                }                
                public Object getVirtual(String keyname) {
                    return res.getVirtual(keyname);
                }
                public String getVirtualString(String keyname) {
                    return res.getVirtualString(keyname); 
                }
                public String getComponentText(String comp) {
                    if (!comp.startsWith("$")) {
                        comp="$"+comp;
                    }         
                     return res.getComponent(comp).getText();
                }
                public Object getComponentAction(String componentname, String... args) {
                    String virtualcomname;
                    if (componentname.startsWith("$")) {                        
                        virtualcomname = "@"+componentname.substring(1);
                        componentname  = componentname;
                    }else{                        
                        virtualcomname = "@"+componentname;
                        componentname  = "$"+componentname;
                    }                      
                     
                    req.setParameter("action", "function");     
                    req.setParameter("component", res.getComponent(componentname).getId());  
        
                    if (args!=null && args.length>=1) {
                        Nset nargs = Nset.newArray();
                        for (int i = 0; i < args.length; i++) {
                            nargs.addData(args[i]);
                        }                        
                        res.setVirtual("@reportargs", nargs);
                    }else{
                        res.setVirtual("@reportargs", Nset.newArray());
                    }                    
                    System.out.println("getComponentAction:"+res.getComponent(componentname).getId());
                    NikitaServlet.execLogicComponent(res.getContent(), req,  res,  res.getNikitaLogic(), res.getComponent(componentname).getId());
                    
                    return res.getVirtual(virtualcomname);  
                }
                
                public JREmptyDataSource getDataSource(String componentname, String... args) {
                     
                    String virtualcomname;
                    if (componentname.startsWith("$")) {                        
                        virtualcomname = "@"+componentname.substring(1);
                        componentname  = componentname;
                    }else{                        
                        virtualcomname = "@"+componentname;
                        componentname  = "$"+componentname;
                    }                      
                     
                    req.setParameter("action", "function");     
                    req.setParameter("component", res.getComponent(componentname).getId());  
        
                    if (args!=null && args.length>=1) {
                        Nset nargs = Nset.newArray();
                        for (int i = 0; i < args.length; i++) {
                            nargs.addData(args[i]);
                        }                        
                        res.setVirtual("@reportargs", nargs);
                    }else{
                        res.setVirtual("@reportargs", Nset.newArray());
                    }                    
                     
                    NikitaServlet.execLogicComponent(res.getContent(), req, res,  res.getNikitaLogic(), res.getComponent(componentname).getId());
                    
                    Object obj  = res.getVirtual(virtualcomname);
                    if (obj instanceof  Nikitaset) {
                        return nikitasettoDataSource( (Nikitaset)obj, res );
                    }else if ( res.getVirtual("datasource")!=null  ) {
                        
                    }                        
                    
                    return new JREmptyDataSource(0); 
                }

                public String getImageString(String fname, String resname) {
                    return baseImageString+"/res/resource/?resname="+resname;  
                }
                public InputStream getInputStream(String fname, String resname) {
                    return super.getInputStream(fname, resname);
                }
                
            });//nikita virtual NikitaReport
            hashMap.put("nikitaconnection", res.getConnection(nikitaconnection));//nikita virtual nikitaengine
            
            
            
            //JRDataSource  JsonDataSourc
            JasperReport report = (JasperReport) JRLoader.loadObject(inputStream);
            JasperPrint jasperPrint = null;
  
            try {
                JRParameter[] jrs = report.getParameters();
                for (int i = 0; i < jrs.length; i++) {
                    JRParameter jRParameter = jrs[i];
                    
                    //System.out.println(jRParameter.getName());
                    //System.out.println(jRParameter.getValueClassName());
                    
                    if (jRParameter.getName().startsWith("@")) {
                        hashMap.put(jRParameter.getName(), res.getVirtual(jRParameter.getName()));
                    }
                            
                     
                }
            } catch (Exception e) { }
            
            if (datasource.equals("")||datasource.equals("empty")) {  
                jasperPrint = JasperFillManager.fillReport(report,  hashMap,  new JREmptyDataSource(nikitaset.getRows()){
                    int icount =-1;
                    public Object getFieldValue(JRField field) {
                        if (field.getName().equals("NIKITAROWNUMBER")) {
                            return (icount+1);
                        }else if (field.getName().equals("NIKITAROWCOUNT")) {
                            return (nikitaset.getRows());
                        } 
                        //26/06/2016
                        if (field.getName().startsWith("@") && field.getName().contains(".")) {
                            String vname = field.getName().substring(0,field.getName().indexOf("."));
                            String fname = field.getName().substring(field.getName().indexOf(".")+1);
                            if (res.getVirtual(vname) instanceof Nikitaset) {
                                Nikitaset ns = (Nikitaset)res.getVirtual(vname);
                                return ns.getText(icount, fname);  
                            }                        
                        } 
                        
                        return nikitaset.getText(icount, field.getName());  
                    }
                    protected String getPropertyName(JRField field) {
                        return nikitaset.getHeader(icount);  
                    }                    
                    public void moveFirst() {
                        icount=0;
                    }
                    public boolean next() {
                        if (icount<nikitaset.getRows()-1) {
                            icount++;
                            return true;
                        } else  if (icount==-1) {
                            icount++;
                            return true;
                        }   
                        return false;
                    }  
                    
                });
            }else if (datasource.equals("custom")) {
                 
            }else if (datasource.equals("array")) {
                jasperPrint = JasperFillManager.fillReport(report, hashMap,  new JRBeanArrayDataSource( null){
                    int icount = -1;
                    public int getRecordCount() {
                        return nikitaset.getRows();
                    }
                    public Object getFieldValue(JRField field) {
                        return nikitaset.getText(icount, field.getName());  
                    }
                    protected String getPropertyName(JRField field) {
                        return nikitaset.getHeader(icount);  
                    }                    
                    public void moveFirst() {
                        icount=0;
                    }
                    public boolean next() {
                        if (icount<nikitaset.getRows()-1) {
                            icount++;
                            return true;
                        }   
                        return false;
                    }                    
                });
            }else if (datasource.equals("collection")) {
                jasperPrint = JasperFillManager.fillReport(report, hashMap,  new JRBeanCollectionDataSource(null){
                    int icount = -1;
                    public int getRecordCount() {
                        return nikitaset.getRows();
                    }
                    public Object getFieldValue(JRField field) {
                        return nikitaset.getText(icount, field.getName());  
                    }
                    protected String getPropertyName(JRField field) {
                        return nikitaset.getHeader(icount);  
                    }                    
                    public void moveFirst() {
                        icount=0;
                    }
                    public boolean next() {
                        if (icount<nikitaset.getRows()-1) {
                            icount++;
                            return true;
                        }   
                        return false;
                    }     
                });               
            }else if (datasource.equals("connection")) {
                jasperPrint = JasperFillManager.fillReport(report, hashMap,  res.getConnection(nikitaconnection).getInternalConnection());    
            }
                
             
            JRParameter[] params = report.getParameters();
            for(JRParameter param : params) {
                if(!param.isSystemDefined() && param.isForPrompting()){
                  // param.getName();
                  // param.getDescription();
                   //System.out.println("JREPORT:"+ param.getName()+":"+ param.getValueClassName()); 
                }
            }    
                 
            /*
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
            response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
            response.setHeader("Cache-Control", "max-age=0");
            response.setHeader("content-disposition", (!fname.equals("")?"attachment;":"inline;")+" filename=\""+fname+"\""); 
            response.setDateHeader("Expires", 0);  
            */
            
            if(type.equals("pdf")||type.equals("")) {
               JasperExportManager.exportReportToPdfStream(jasperPrint   , outputStream); 
            }else  if(type.equals("xls")) {
                JRXlsExporter exporter = new JRXlsExporter();     
                /*
                exporter.setParameter(JRXlsExporterParameter.JASPER_PRINT, jasperPrint);
                exporter.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, outputStream);
                exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);
                exporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
                exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
                exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);                 
                exporter.exportReport();        
                */
                
                
               List<JasperPrint> jasperPrintList = new ArrayList<JasperPrint>();
               jasperPrintList.add(  jasperPrint );
               exporter.setExporterInput(SimpleExporterInput.getInstance(jasperPrintList));
               exporter.setExporterOutput(new SimpleOutputStreamExporterOutput( outputStream ));

               SimpleXlsReportConfiguration configuration = new SimpleXlsReportConfiguration();
               configuration.setOnePagePerSheet(false);
               configuration.setWhitePageBackground(false);
               configuration.setDetectCellType(true);
               configuration.setRemoveEmptySpaceBetweenRows(Boolean.TRUE);
               exporter.setConfiguration(configuration);




               exporter.exportReport();
            }
 
    }
  
}
