/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nikita.generator.action;

import com.naa.utils.InternetX;
import static com.naa.utils.InternetX.nikitaYToken;
import static com.naa.utils.InternetX.sendBroadcastIfUnauthorized401;
import static com.naa.utils.InternetX.urlEncode;
import com.nikita.generator.Component;
import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaRz;
import com.nikita.generator.NikitaService;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.storage.NikitaStorage;
import com.nikita.generator.ui.SmartGrid;
import com.nikita.generator.ui.Tablegrid;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.data.Nson;
import com.rkrzmail.nikita.utility.Utility;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author rkrzmail
 */
public class UrlGeneratorAction implements IAction{

    @Override
    public boolean OnAction(Nset currdata, NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        String code = response.getVirtualString(currdata.getData("code").toString());  
        String param1 =response.getVirtualString( currdata.getData("args").getData("param1").toString());//getpost  
        String param2 = response.getVirtualString(currdata.getData("args").getData("param2").toString());//url
        Object param3 = response.getVirtual(currdata.getData("args").getData("param3").toString());//nset param
        String param4 = response.getVirtualString(currdata.getData("args").getData("param4").toString());//string param
        Object hdr5 = response.getVirtual(currdata.getData("args").getData("param5").toString());//header
        String param6  = response.getVirtualString(currdata.getData("args").getData("param6").toString());//string param
        String  result = currdata.getData("args").getData("param9").toString();
            
        
        if (param2.startsWith("generator://date/")) {
            Nset n = new Nset(param3);
            if (n.getData("flag").toString().equalsIgnoreCase("add")){
                Calendar calendar = get(n.getData("date").toString());// Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, n.getData("day").toInteger());
                calendar.add(Calendar.MONTH, n.getData("month").toInteger());
                calendar.add(Calendar.YEAR, n.getData("year").toInteger());
                response.setVirtual(currdata.getData("args").getData("param9").toString(),
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
            }else if (n.getData("flag").toString().equalsIgnoreCase("dif")){
                response.setVirtual(currdata.getData("args").getData("param9").toString(),
                        get(n.getData("date2").toString()).getTimeInMillis()- get(n.getData("date1").toString()).getTimeInMillis());
            }else if (n.getData("flag").toString().equalsIgnoreCase("format")){
                try {
                    response.setVirtual(currdata.getData("args").getData("param9").toString(),
                            new SimpleDateFormat(n.containsKey("format")?n.getData("format").toString():"yyyy-MM-dd HH:mm:ss").format(get(n.getData("date").toString()).getTime()));
                }catch (Exception e){
                    response.setVirtual(currdata.getData("args").getData("param9").toString(), "");
                }
            }else if (n.getData("flag").toString().equalsIgnoreCase("idate")){
                try {
                    response.setVirtual(currdata.getData("args").getData("param9").toString(),
                            new SimpleDateFormat(n.containsKey("format")?n.getData("format").toString():"yyyy-MM-dd HH:mm:ss").format( n.getData("date").toLong() ));
                }catch (Exception e){
                    response.setVirtual(currdata.getData("args").getData("param9").toString(), "");
                }
            }
 
        }else if (param2.startsWith("generator://webopen/")) {
            Nset n = new Nset(param3);
            response.openWindows(n.getData("url").toString(), n.getData("target").toString());
            //response.write();
        }else if (param2.startsWith("generator://copen/")) {
            Nset n = new Nset(param3);
            response.clientWindows(n.getData("url").toString(), n.getData("methode").toString(), n.getData("reqrescode").toString(), n.getData("args").toString());//args json string
            //response.write();
        }else if (param2.startsWith("generator://popup/")) {
            Nset n = new Nset(param3);
            response.openDialogPopUp(n.getData("url").toString(), n.getData("style").toString());
            //response.write();
        }else if (param2.startsWith("generator://wssend/broadcast/")) {
            Nset n = new Nset(param3);
            NikitaRz.sendtoBroadcast(NikitaConnection.getDefaultPropertySetting().getData("init").getData("nikitarz-appid").toString(), n.getData("receiver").toString(), n.getData("message").toString());
        }else if (param2.startsWith("generator://wssend/listener/")) {
            Nset n = new Nset(param3);
            NikitaRz.sendtoListener(NikitaConnection.getDefaultPropertySetting().getData("init").getData("nikitarz-appid").toString(), n.getData("receiver").toString(), n.getData("message").toString());
       }else if (param2.startsWith("generator://wssend/pbar/")) {
            Nset n = new Nset(param3);
            long istart   = Utility.getLong(response.getVirtualString("_LAST_PBAR"));
            if ( (System.currentTimeMillis() - istart)>=1000 ) {
                response.setVirtual("_LAST_PBAR",( System.currentTimeMillis()));
                String value = n.getData("message").toString();
                int ivalue =  n.getData("value").toInteger()*100 / n.getData("max").toInteger();
                if (!n.containsKey("message")) {
                    value = String.valueOf(ivalue);
                }
                if (n.getData("action").toString().equalsIgnoreCase("listener")) {
                    NikitaRz.sendtoListener(NikitaConnection.getDefaultPropertySetting().getData("init").getData("nikitarz-appid").toString(), n.getData("receiver").toString(), n.getData("message").toString());
                }else{
                    NikitaRz.sendtopBar(NikitaConnection.getDefaultPropertySetting().getData("init").getData("nikitarz-appid").toString(), n.getData("receiver").toString(), value);
                }
            }     
        }else if (param2.startsWith("generator://wclose/")) {
            try {
                response.getHttpServletResponse().setStatus(HttpServletResponse.SC_NO_CONTENT);
                response.getHttpServletResponse().setContentLength(0);
            } catch (Exception e) { }
            try {
                response.getHttpServletResponse().getOutputStream().close();
            } catch (Exception e) { }
        }else if ( param2.startsWith("generator://excel/") ) {
        }else if ( param2.startsWith("generator://download/") ) {
            Nset n = new Nset(param3); 
            String url = n.getData("url").toString();
            
            String filename = downloadFile(url);
            response.setVirtual(result, filename);//old
         }else if ( param2.startsWith("generator://badge/")) {    
            Nset n = new Nset(param3); 
            String sClass = n.getData("class").toString().equalsIgnoreCase("")?"nikitabadge":n.getData("class").toString();
            String sBadge = n.getData("badge").toString();
             
            response.refreshBadge(  sClass, sBadge);
        }else if ( param2.startsWith("generator://pbar/")) {    
            Nset n = new Nset(param3); 
            String sClass = n.getData("class").toString().equalsIgnoreCase("")?"nikitapbar":n.getData("class").toString();
            String ivalue = n.getData("value").toString();
             
            response.refreshPbar(sClass, ivalue);
        }else if ( param2.startsWith("generator://other/")) {    
            Nset n = new Nset(param3); 
            String sClass = n.getData("class").toString().equalsIgnoreCase("")?"nikitaother":n.getData("class").toString();
            String sText = n.getData("text").toString();
             
            response.refreshOther(sClass, sText);
        }else if ( param2.startsWith("generator://excelcursol/")) {    
            String currentrow = currdata.getData("args").getData("param4").toString();//start with 0
        }else if ( param2.startsWith("generator://jsonmerger/") ) {
            Nset n = new Nset(param3);
            Nset body = n.getData("body"); 
            if (!body.isNset()) {
                body = Nset.readJSON(n.getData("body").toString());
            }
            Nset data = n.getData("data"); 
            if (!data.isNset()) {
                data = Nset.readJSON(n.getData("data").toString());
            }
            String[] keys = data.getObjectKeys();
            for (int i = 0; i < keys.length; i++) {
                String key = keys[i];
                body.setData(key, data.getData(key).toString());
            }            
            response.setVirtual(result, body);//old
        }else if ( param2.startsWith("generator://jsonarraytoobject/") ) {
            Nset n = new Nset(param3);
            String key = n.getData("key").toString();
            Nset body = n.getData("body");//net
            if (!body.isNset()) {
                body = Nset.readJSON(n.getData("body").toString());
            }
            Nset rest = Nset.newObject();
            for (int i = 0; i < body.getSize(); i++) {                
                rest.setData(body.getData(i).getData(key).toString(), body.getData(i));
            }            
            response.setVirtual(result, rest);//old
        }else if ( param2.startsWith("generator://bhc/") ) {
            Nset n = new Nset(param3);             
            Nset body = n.getData("body");//must nset
            Nset rest = Nset.newObject();
            for (int i = 0; i < body.getSize(); i++) {                
                rest.setData(body.getData(i).getData("NAME_ID").toString(), body.getData(i));
            }            
            n.setData("body", rest);//fill again
            response.setVirtual(result, n);//nset
        }else if ( param2.startsWith("generator://values/") ) {
            Nset rest = Nset.newObject();
            Vector<Component> components = response.getContent().populateAllComponents();
            for (int i = 0; i < components.size(); i++) {
                Component component = components.elementAt(i);
                if (!component.getName().equalsIgnoreCase("")) {
                    rest.setData(component.getName(), component.getText());
                }
            }
            response.setVirtual(result, rest);//nset
        }else if (param2.startsWith("generator://paging/")  ) {
            String page = currdata.getData("args").getData("param4").toString();
            Nikitaset n; //out
            Nikitaset xsql = null;     
  
            if (param3 instanceof Nikitaset) {
                xsql =  (Nikitaset)param3;
            }else if (param3 instanceof Nset) {
                xsql = new Nikitaset((Nset)param3);
            }else{
                xsql = new Nikitaset("nodata");     
            }
            //grid
            Nset data = Nset.newObject();
            Component comp = response.getComponent(page);
            if (comp instanceof Tablegrid) {
                    if (request.getParameter("component").equals(comp.getId())) {
                        ((Tablegrid)comp).prepareFilter(request.getParameter("action"));
                    }                    
                    ((Tablegrid)comp).setOnFilterClickListener(new Tablegrid.OnFilterClickListener() {
                        public void OnFilter(NikitaRequest request, NikitaResponse response, Component component) {                            
                        }
                    }, ((Tablegrid)comp).getCurrentPage()!=-1?((Tablegrid)comp).getCurrentPage():1, ((Tablegrid)comp).getShowPerPage()>=1? ((Tablegrid)comp).getShowPerPage():10);
                    n = createQueryNs(xsql,    ((Tablegrid)comp).getCurrentPage() ,((Tablegrid)comp).getShowPerPage() );

                    response.refreshComponent(comp);
            }else if(comp instanceof SmartGrid){//new Component                
                if (request.getParameter("component").equals(comp.getId())) {
                        ((SmartGrid)comp).onActionFilter(request.getParameter("action"), request.getParameter("actionnew"));
                }                    
                ((SmartGrid)comp).setOnFilterClickListener(new SmartGrid.OnFilterClickListener() {
                    public void OnFilter(NikitaRequest request, NikitaResponse response, Component component) {                            
                    }
                }, ((SmartGrid)comp).getCurrentPage()!=-1?((SmartGrid)comp).getCurrentPage():1, ((SmartGrid)comp).getShowPerPage()>=1? ((SmartGrid)comp).getShowPerPage():10);

                n = createQueryNs(xsql,   ((SmartGrid)comp).getCurrentPage() ,((SmartGrid)comp).getShowPerPage() );

                response.refreshComponent(comp);           
            }else if(!page.trim().equals("")){  
                if(page.trim().startsWith("@")){ 
                    page = response.getVirtualString(page);
                }
                //page
                if (page.contains(",")) {
                      n = createQueryNs(xsql,    Utility.getInt(page.substring(0, page.indexOf(","))),Utility.getInt(page.substring(page.indexOf(",")+1)));
                }else if (Utility.isNumeric(page)) {     
                    n = createQueryNs(xsql,     1, Utility.getInt(page));                                       
                }else{
                    n = createQueryNs(xsql,   1, 10);
                }                               
            }else{
                n = createQueryNs(xsql,     -1, -1);                  
            }
          
             
            
            //add 11/01/2021
            result = currdata.getData("args").getData("param9").toString();
            if (result.startsWith("$")) {
                Component component = response.getComponent(result);
                component.setData(n.toNset());
                response.refreshComponent(component);
            }else{
                response.setVirtual(result, n);//old
            }              
        }
        return true;
     } 
    public Nikitaset createQueryCursor(Nikitaset resultSet, String fname, int cursor){  
         String error = "";
         Object info = null;   
         Vector<String> header = resultSet.getDataAllHeader();
         Vector<Vector<String>> data = new Vector<Vector<String>>();         
         Vector<Vector<String>> dataresultSet = resultSet.getDataAllVector();                        
         
        Nset infoNset = new Nset(resultSet.getInfo()  );
        int rowperpage = infoNset.getData("row").toInteger();
        int page = infoNset.getData("page").toInteger();
                 
        if (rowperpage<=0  ) {
            //reload first page;  
            Nson settings = Nson.newObject() ;
            settings.setData("setting", "first");
            settings.setData("row", 100);//row per page default
            settings.setData("page", 1);
            settings.setData("fname", fname);
            resultSet = NikitaStorage.getStorage("temp").readXlsxStorage(fname, settings);
            
            rowperpage = infoNset.getData("row").toInteger();
            page = infoNset.getData("page").toInteger();
        }
        int rows = infoNset.getData("rows").toInteger();//penting!!
        
        if (rows >= 1) {                
            page=page<=0?0:page;                  
            int pmax = rows/rowperpage+ (rows%rowperpage<=0?0:1);
            page=page>=pmax?pmax:page;
            page=page<=0?1:page;  
            
            int currrow = rowperpage*(page-1); 
            currrow=currrow<0?0:currrow;   
            
            if (currrow>=rows) {//9>=10
                //tidak ada data
            }else if (cursor>currrow) {
                //reload nex page
                Nson settings = Nson.newObject() ;
                settings.setData("setting", "first");
                settings.setData("row", 100);
                settings.setData("page", page = page+1);
                resultSet = NikitaStorage.getStorage("temp").readXlsxStorage(fname, settings);

                rowperpage = infoNset.getData("row").toInteger();
                page = infoNset.getData("page").toInteger();
            }         
            
            for (int row = currrow; row < Math.min(currrow+rowperpage, rows); row++) {                
                int current = row;//resultSet.absolute(row+1);                 
                data.addElement(dataresultSet.elementAt(current));//start with 0
            }
            //real feetdata
                
            info = Nset.newObject().setData("nfid", "Nset").setData("fname", fname).setData("mode", "paging").setData("rows", rows).setData("row", rowperpage).setData("page", page);
        }else{
            //tidak ada data
            info = Nset.newObject().setData("nfid", "Nset").setData("fname", fname).setData("mode", "paging").setData("rows", 0).setData("row", rowperpage).setData("page", page);
        }
        return new Nikitaset(header, data, error, info);
     }
     public Nikitaset createQueryNs(Nikitaset resultSet, int page, int rowperpage){  
         String error = "";
         Object info = null;   
         Vector<String> header = resultSet.getDataAllHeader();
         Vector<Vector<String>> data = new Vector<Vector<String>>();         
         Vector<Vector<String>> dataresultSet = resultSet.getDataAllVector();   
             
           
         
         
        if (rowperpage<=0  ) {
             return resultSet;
        }else{    
            int rows = resultSet.getRows(); 
            page=page<=0?0:page;                  
            int pmax = rows/rowperpage+ (rows%rowperpage<=0?0:1);
            page=page>=pmax?pmax:page;
            page=page<=0?1:page;  

            int currrow = rowperpage*(page-1); 
            currrow=currrow<0?0:currrow;   
            
            for (int row = currrow; row < Math.min(currrow+rowperpage, rows); row++) {                
                int current = row;//resultSet.absolute(row+1);                 
                data.addElement(dataresultSet.elementAt(current));//start with 0
            }
            //real feetdata
                
            info = Nset.newObject().setData("nfid", "Nset").setData("mode", "paging").setData("rows", rows).setData("row", rowperpage).setData("page", page);
    
        }
        return new Nikitaset(header, data, error, info);
     }
     private static Calendar get(String now){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(sdf.parse(now));
        } catch (Exception e) { }
        return calendar;
    }
     
     public static String downloadFile(String stringURL, String...paramvalue) {
        String fname = Utility.MD5(Utility.Now());
        URL object;
        try {
            stringURL = nikitaYToken(stringURL);
            if (paramvalue!=null) {
                StringBuffer stringBuffer = new StringBuffer();
                for (int i = 0; i < paramvalue.length; i++) {
                    if (paramvalue[i].contains("=")) {
                        int split = paramvalue[i].indexOf("=");
                        String sdata = urlEncode(paramvalue[i].substring(split+1));
                        stringBuffer.append(paramvalue[i].substring(0, split)).append("=").append(sdata).append("&");
                    }
                }
                stringURL =  stringURL+(stringURL.contains("?")?"&":"?")+stringBuffer.toString();
            }
            object = new URL(stringURL);



            HttpURLConnection con;
            try {
                con = (HttpURLConnection) object.openConnection();

                con.setDoOutput(true);
                con.setDoInput(true);
                con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                con.setRequestProperty("Accept", "application/json");
                con.setRequestMethod("GET");
                con.setConnectTimeout(30000);
                //display what returns the POST request

                StringBuilder sb = new StringBuilder();
                int HttpResult = con.getResponseCode();
                sendBroadcastIfUnauthorized401(HttpResult);
                if (HttpResult >= HttpURLConnection.HTTP_OK) {
                    //Utility.sessionExpired(con.getHeaderFields());
                    try {
                        String storage =     NikitaService.getDirTmp();
                        String sfile = storage + NikitaService.getFileSeparator() + fname+ ".tmp";
                        FileOutputStream fos = new FileOutputStream(sfile);
                        Utility.CopyStream(con.getInputStream(), fos);
                        fos.close();
                        return fname;
                    } catch (Exception e) {
                        
                    }         

                    return "";
                } else {
                    System.out.println(con.getResponseMessage());
                    System.out.println(con.getResponseMessage());
                    //System.out.println(con.getResponseMessage());
                    InputStream inputStream = con.getErrorStream();
                    /*String contentEncodingHeader = con.getHeaderField("Content-Encoding");
                    if (contentEncodingHeader != null && contentEncodingHeader.equals("gzip")) {
                        // Read the zipped contents.
                        inputStream = new GZIPInputStream(con.getInputStream());
                    } else {
                        // Read the normal contents.
                        inputStream = con.getInputStream();
                    }*/
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    return ""; //return sb.toString();
                }
            } catch (IOException e) {
                //Utility.nikitaErrorConn();
                // TODO Auto-generated catch block
                e.printStackTrace();
                com.naa.data.Nson nson = com.naa.data.Nson.newObject();
                nson.set("STATUS", "ERROR");
                nson.set("ERROR", e.getMessage());
                return ""; //return nson.toJson();
            } catch (Exception e) {
                //Utility.nikitaErrorConn();
                e.printStackTrace();
                com.naa.data.Nson nson = com.naa.data.Nson.newObject();
                nson.set("STATUS", "ERROR");
                nson.set("ERROR", e.getMessage());
                return ""; //return nson.toJson();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            //Utility.nikitaErrorConn();
            com.naa.data.Nson nson = com.naa.data.Nson.newObject();
            nson.set("STATUS", "ERROR");
            nson.set("ERROR", e.getMessage());
            return ""; //return nson.toJson();
        }

    }
}
