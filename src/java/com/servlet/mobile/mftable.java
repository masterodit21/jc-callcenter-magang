/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.servlet.mobile;

import com.nikita.generator.Component;
import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaService;
import com.nikita.generator.NikitaServlet;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.connection.NikitaInternet;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.data.Nson;
import com.rkrzmail.nikita.utility.Utility;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.util.Hashtable;
import java.util.Vector;
import org.apache.http.HttpResponse;
/**
 *
 * @author rkrzmail
 */
public class mftable extends NikitaServlet{
    
    @Override
    public void OnRun(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        //String fname = NikitaConnection.getDefaultPropertySetting().getData("init").getData("temp").toString()+NikitaService.getFileSeparator() + request.getParameter("userid")+ request.getParameter("imei")+ response.getVirtual("@+RANDOM");
        //NikitaConnection nc = response.getConnection(NikitaConnection.MOBILE); 
        //NikitaConnection nc = response.getConnection("wom_survey"); 
        /*
        String  sql = "";
        if (request.getParameter("table").trim().equals("WMOB_MST_MARKETPRICE")) {
            sql="select * from WMOB_MST_MARKETPRICE where cab_id in (select  CAB_ID from MT_USER where user_id = '"+Component.escapeSql(request.getParameter("userid"))+"')";
        }else if (request.getParameter("table").trim().equals("WMOB_MST_PRICELISTCODEBRANCH")) {
            sql="select * from WMOB_MST_PRICELISTCODEBRANCH where cab_id in (select  CAB_ID from MT_USER where user_id = '"+Component.escapeSql(request.getParameter("userid"))+"')";
        //}else if (request.getParameter("table").trim().equals("WMOB_MST_DEALER")) {
        //    sql="select * from WMOB_MST_DEALER where cab_id in (select  CAB_ID from MT_USER where user_id = '"+Component.escapeSql(request.getParameter("userid"))+"')";
        }else if (request.getParameter("table").trim().equals("VMOB_STP_DP")) {
            sql="select * from VMOB_STP_DP where REGIONAL_ID in (select  REGIONAL_ID from WMOB_MST_CABANG a join MT_USER b on b.cab_id = a.CAB_ID where b.user_id = '"+Component.escapeSql(request.getParameter("userid"))+"')";
        }else if (request.getParameter("table").trim().equals("MOBILE_ORDER_BLACKLIST")) {
            sql="select * from MOBILE_ORDER_BLACKLIST where CREATEDBY = '"+Component.escapeSql(request.getParameter("userid"))+"'";
        }else if (request.getParameter("table").trim().equals("MOBILE_ORDER_INITIATION")) {
            sql="select * from MOBILE_ORDER_INITIATION where CREATEDBY = '"+Component.escapeSql(request.getParameter("userid"))+"'";
        }else if (request.getParameter("table").trim().equals("MOBILE_ORDER_LIST")) {
            sql="select * from MOBILE_ORDER_LIST where MOBILE_ORDER_ID in (select MOBILE_ORDER_ID from MOBILE_ORDER_BLACKLIST where CREATEDBY = '"+Component.escapeSql(request.getParameter("userid"))+"')";
        }else if (request.getParameter("table").trim().equals("MOBILE_ORDER_SCORING")) {
            sql="select * from MOBILE_ORDER_SCORING where CREATEDBY = '"+Component.escapeSql(request.getParameter("userid"))+"'";
        }else if (request.getParameter("table").trim().equals("WMOB_TRN_MATCHING_RESULT")) {
            sql="select * from WMOB_TRN_MATCHING_RESULT where MOBILE_ORDER_ID in (select MOBILE_ORDER_ID from MOBILE_ORDER_BLACKLIST where CREATEDBY = '"+Component.escapeSql(request.getParameter("userid"))+"')";
        }else{
            sql="SELECT * FROM " + request.getParameter("table");
        } 
        Nikitaset ns = nc.Query("::"+fname+"::"+request.getParameter("table")+"::" +sql );    
                
        
        try {
            FileInputStream fileInputStream = new FileInputStream(fname);
            Hashtable<String, String> hdr =new Hashtable<String, String>();
            hdr.put("rows", Nset.readJSON(ns.getError()).getData("rows").toString());
            NikitaService.getResourceStream(new FileInputStream(fname), request.getHttpServletRequest(), response.getHttpServletResponse(),hdr,  "nikitaftable.db", true);
            fileInputStream.close();
            new File(fname).delete();//delete
        } catch (Exception e) { }
            */
        String fname = NikitaConnection.getDefaultPropertySetting().getData("init").getData("temp").toString()+NikitaService.getFileSeparator() + request.getParameter("userid")+ request.getParameter("imei")+ response.getVirtual("@+RANDOM");
        Hashtable<String, String> hdr =new Hashtable<String, String>();
                
 
        if (request.getParameter("table").trim().startsWith("api_kodepos_mg")) {
            HttpResponse httpResponse = NikitaInternet.getHttp("http://kamm-group.com:8094/m/getOfflineKodePOS?app=253&token=0d03754524e12eed600f3448ddee675c", "");
            Nson n = Nson.readJson(NikitaInternet.getString(httpResponse));
            hdr.put("rows", String.valueOf(n.size()));  
            resulttoFile(n, fname, "kodepos");
        }else if (request.getParameter("table").trim().startsWith("api_kodepos")) {
            HttpResponse httpResponse = NikitaInternet.getHttp("http://202.56.171.19:8185/s/getOfflineKodePOS?app=253&token=0d03754524e12eed600f3448ddee675c", "");
            Nson n = Nson.readJson(NikitaInternet.getString(httpResponse));
            hdr.put("rows", String.valueOf(n.size()));  
            resulttoFile(n, fname, "kodepos");
         }else if (request.getParameter("table").trim().startsWith("api_pinjaman_mg")) {
            HttpResponse httpResponse = NikitaInternet.getHttp("http://kamm-group.com:8094/m/getOfflinePinjaman?app=253&token=0d03754524e12eed600f3448ddee675c", "");
            Nson n = Nson.readJson(NikitaInternet.getString(httpResponse));
            hdr.put("rows", String.valueOf(n.size()));
            resulttoFile(n, fname, "pinjaman");    
        }else if (request.getParameter("table").trim().startsWith("api_pinjaman")) {
            HttpResponse httpResponse = NikitaInternet.getHttp("http://202.56.171.19:8185/s/getOfflinePinjaman?app=253&token=0d03754524e12eed600f3448ddee675c", "");
            Nson n = Nson.readJson(NikitaInternet.getString(httpResponse));
            hdr.put("rows", String.valueOf(n.size()));
            resulttoFile(n, fname, "pinjaman");
        }else if (request.getParameter("table").trim().startsWith("api_kendaraan")) {
               HttpResponse httpResponse = NikitaInternet.getHttp("http://202.56.171.19:8185/s/getOfflineKendaraan?app=253&token=0d03754524e12eed600f3448ddee675c", "");
            Nson n = Nson.readJson(NikitaInternet.getString(httpResponse));
            hdr.put("rows", String.valueOf(n.size()));
              resulttoFile(n, fname, "kendaraan");
        }else{
            NikitaConnection nc = response.getConnection("FTABLE");
            Nikitaset ns = nc.Query("::"+fname+"::"+request.getParameter("table")+"::" +"SELECT * FROM " +request.getParameter("table") );    
            hdr.put("rows", Nset.readJSON(ns.getError()).getData("rows").toString());
        }
 
        
            try {
                FileInputStream fileInputStream = new FileInputStream(fname);
               
                NikitaService.getResourceStream(new FileInputStream(fname), request.getHttpServletRequest(), response.getHttpServletResponse(),hdr,  "nikitaftable.db", true);
                fileInputStream.close();
                new File(fname).delete();//delete
            } catch (Exception e) { } 
    }
    private int resulttoFile(Nson nson, String fname, String tname){
        int rows = 0;
        Vector<String> header =  new Vector<String>();
        try {    
            if (fname==null) {
                return 0;
            }
            FileOutputStream fos = new FileOutputStream(fname);
            DataOutputStream outputStream = new DataOutputStream(fos);
            try {
                    Nson keys = nson.getData(0).getObjectKeys();
                    for (int i = 0; i < keys.size(); i++) {
                        header.addElement(keys.getData(i).asString());//new
                    }
                    Nset info=Nset.newObject(); 
                    Nset metatype = Nset.newArray();
                    Nset metasize = Nset.newArray();
                    Nset metaname = Nset.newArray();
                    Nset metalabl = Nset.newArray();
                    for (int i = 0; i < keys.size(); i++) {                
                        metatype.addData("text") ;
                        metasize.addData(255) ;  
                        metaname.addData(keys.getData(i).asString()) ;  //colname
                        metalabl.addData(keys.getData(i).asString()) ;  //collabel
                    }
                    info.setData("metadata", Nset.newObject().setData("type", metatype).setData("size", metasize).setData("name", metaname).setData("label", metalabl)  );
                    Nikitaset nikitaset = new Nikitaset(header, new Vector<Vector<String>>(), "",info);

                    
                    outputStream.writeUTF(Nset.newObject().setData("rows", rows).setData("tablename", tname!=null?tname:"").setData("nikitaset", nikitaset.toNset()).toJSON());
                    outputStream.flush();
                    for (int r = 0; r < nson.size(); r++) {
                            rows = r; 
                            Vector<String> field = new Vector<String>();
                            for (int i = 0; i < keys.size(); i++) {
                                try {
                                    String sdata = nson.getData(r).getData(keys.getData(i).asString()).asString();
                                    field.addElement(sdata!=null?sdata:"");
                                } catch (Exception e) {
                                    field.addElement("");
                                }
                            }
                            outputStream.writeUTF(Nset.newArray().addData(field).toJSON());
                    }                
                    outputStream.flush();
            
            } catch (Exception e) {  }
            fos.close();
        } catch (Exception e) { 
            e.printStackTrace();
        }     
        return rows;
    }
    
}
