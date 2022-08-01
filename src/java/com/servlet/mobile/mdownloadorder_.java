/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.servlet.mobile;

import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaServlet;
import com.nikita.generator.connection.NikitaConnection;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import static java.lang.System.out;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import java.util.stream.Stream;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author rkrzmail
 */
public class mdownloadorder_  extends NikitaServlet{

    @Override
    public void OnRun(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
       
        NikitaConnection nc = response.getConnection("mandalasales");
//        NikitaConnection nc = response.getConnection("monitoring_conn");
        String userid = "";
        String action = "";
        String orderid = "";
        
        
        userid = Utility.notNull(request.getParameter("userid"));
        action = Utility.notNull(request.getParameter("action")).toUpperCase();
        orderid = Utility.notNull(request.getParameter("orderid"));//Nsetarray
        
//            String[]  keys = orderData.getObjectKeys();
        Nset outData = Nset.newArray();
        Nikitaset rstUpdate=null;
        Nikitaset rstInsert=null;
        Nikitaset rstDelete =null;
        Nikitaset rstUpdateStatus =null;
        Nikitaset rstView =null;
            
        String orderIdArray =  "";
        
        StringBuffer sbOrderId = new StringBuffer();
        Nset orderData = Nset.readJSON(orderid);
//        sbOrderId.PA
        for (int i = 0; i < orderData.getArraySize(); i++) {
               sbOrderId.append(orderData.getData(i).toString() );
        }
//        sbOrderId
        
            if(sbOrderId.toString().equals("''")){
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
                sbOrderId.insert(1, sdf.format(new Date()));
            }

            if (!action.equals("RECEIVED")){
                String strView   = "SELECT * FROM MST_ORDER WHERE LASTACTION NOT IN  ('CLOSE') AND USERID = '"+userid+"'";
                rstView = nc.Query(strView);

                Vector<Vector<String>> insert = new  Vector<Vector<String>>();
                Vector<Vector<String>> update = new  Vector<Vector<String>>();
                Vector<Vector<String>> delete = new  Vector<Vector<String>>();

                StringBuffer stringBuffer = new StringBuffer();
                for (int x =0 ; x < rstView.getRows();x++){
                    if (rstView.getText(x, "LASTACTION").equalsIgnoreCase("UPDATE")) {
                        update.addElement(rstView.getDataAllVector().elementAt(x));
                    }else if (orderIdArray.indexOf( rstView.getText(x, 0)) == -1) {
                        insert.addElement(rstView.getDataAllVector().elementAt(x));
                    }
                    stringBuffer.append( rstView.getText(x, 0) ).append(";");

                }

                if (rstView.getRows()>=1) {
                    Vector<String> mobileorder = Utility.splitVector(orderid, ",");
                    for (int x =0 ; x < mobileorder.size();x++){
                        if (stringBuffer.indexOf( mobileorder.elementAt(x) ) == -1) {
                            Vector<String> cols = new Vector<String> ();
                            cols.addElement(mobileorder.elementAt(x) );
                            delete.addElement(  cols );
                       }
                    }
                }else{
                    Vector<String> mobileorder = Utility.splitVector(orderIdArray, ",");
                    for (int x =0 ; x < mobileorder.size();x++){
                            Vector<String> cols = new Vector<String> ();
                            cols.addElement(mobileorder.elementAt(x) );
                            delete.addElement(  cols );
                    } 
                }

                rstInsert = new  Nikitaset(rstView.getDataAllHeader(), insert);
                rstUpdate = new  Nikitaset(rstView.getDataAllHeader(), update);
                rstDelete = new  Nikitaset(rstView.getDataAllHeader(), delete);
                outData.addData(Nset.newObject().setData("truncate", rstView.toNset()));
                // outData.addData(Nset.newObject().setData("table", orderData).setData("data", Nset.newArray().addData(Nset.newObject().setData("code", "update").setData("data", rstInsert.toNset().toJSON())).addData(Nset.newObject().setData("code", "delete").setData("data", rstDelete.toNset().toJSON())).addData(Nset.newObject().setData("code", "update").setData("data", rstUpdate.toNset().toJSON()))));
            
                response.writeStream(outData.toJSON());
            }
//            else  if (action.equals("RECEIVED")){
//                String strUpdateStatus =  "UPDATE MST_ORDER SET LASTACTION ='DELIVER' WHERE ORDERID  IN ("+sbOrderId+") AND LASTACTION <> 'CLOSE' AND USERID = '"+userid+"'";
//                rstUpdateStatus = nc.Query(strUpdateStatus);
//                response.writeStream(Nset.newObject().setData("status", "OK").toJSON());
//            }


        
            
       
    }
}
    

