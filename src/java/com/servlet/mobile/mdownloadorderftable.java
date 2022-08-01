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
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import static java.lang.System.out;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author rkrzmail
 */
public class mdownloadorderftable  extends NikitaServlet{

   public void OnRun(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        NikitaConnection nc = response.getConnection("BCAF_MEMA_SQL");
        String userid = "";
        String action = "";
        String orderid = "";


        userid = Utility.notNull(request.getParameter("userid"));
        action = Utility.notNull(request.getParameter("action")).toUpperCase();
        orderid = Utility.notNull(request.getParameter("orderid"));
        Nikitaset officer = nc.Query("SELECT OFFICER_ID FROM MST_USER_MEMA WHERE USER_ID = ?", new String[] { userid });


        Nset outData = Nset.newArray();
        Nikitaset rstUpdate = null;
        Nikitaset rstInsert = null;
        Nikitaset rstDelete = null;
        Nikitaset rstUpdateStatus = null;
        Nikitaset rstView = null;

        String orderIdArray = "";

        StringBuffer sbOrderId = new StringBuffer();
        Nset orderData = Nset.readJSON(orderid);

        for (int i = 0; i < orderData.getArraySize(); i++) {
          sbOrderId.append(orderData.getData(i).toString());
        }


        if (sbOrderId.toString().equals("''")) {
          SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
          sbOrderId.insert(1, sdf.format(new Date()));
        }

        if (!action.equals("RECEIVED"))   {







          String strView = "SELECT   a.ORDERID, a.CUST_NAME, a.ALAMAT, a.KODE_POS, a.KOTA_KAB, a.PROVINSI, a.NO_TELP_1,a.NO_TELP_2,a.EMAIL, a.NO_KTP, a.NO_REK_BCA, a.KETERANGAN, a.DEALER_NAME, a.JUMLAH_UNIT, a.CREATED_DATE,a.BRANCH_ID, a.OFFICER_ID, a.GELAR, a.TGL_LAHIR, a.RT, a.RW, a.KELURAHAN, a.PEKERJAAN,a.NO_HP_1, a.NO_HP_2, a.LASTACTION, a.CLUSTERS, b.M_ID, b.APP_ID,b.SALES_DEALER, b.MERK_ID,b.MODEL_ID, b.TYPE_ID, b.KONDISI_KENDARAAN, b.TAHUN_KENDARAAN , '' AS MOBILESTREAM, '' AS MOBILESTREAMSTATUS   FROM TRX_APP_IN_HDR a LEFT JOIN TRX_APP_IN_DTL b ON a.ORDERID = b.ORDERID  WHERE a.LASTACTION NOT IN  ('CLOSE') AND a.OFFICER_ID = '" + officer.getText(0, 0) + "' " + " UNION " + " SELECT TOP 5 GROUP_APP_ID,'','','','','','','','','','','','',0,created_date,'','','','','','','','','','','','','','','','','','','',0,MOBILESTREAM,MOBILESTREAMSTATUS FROM ORDER_MOBILE " + " ORDER BY MOBILESTREAM ASC ";



          rstView = nc.Query(strView, new String[0]);

          Vector<Vector<String>> insert = new Vector();
          Vector<Vector<String>> update = new Vector();
          Vector<Vector<String>> delete = new Vector();

          StringBuffer stringBuffer = new StringBuffer();
          for (int x = 0; x < rstView.getRows(); x++) {
            if (rstView.getText(x, "LASTACTION").equalsIgnoreCase("UPDATE")) {
              update.addElement(rstView.getDataAllVector().elementAt(x));
            } else if (orderIdArray.indexOf(rstView.getText(x, 0)) == -1) {
              insert.addElement(rstView.getDataAllVector().elementAt(x));
            }
            stringBuffer.append(rstView.getText(x, 0)).append(";");
          }


          if (rstView.getRows() >= 1) {
            Vector<String> mobileorder = Utility.splitVector(orderid, ",");
            for (int x = 0; x < mobileorder.size(); x++) {
              if (stringBuffer.indexOf((String)mobileorder.elementAt(x)) == -1) {
                Vector<String> cols = new Vector();
                cols.addElement(mobileorder.elementAt(x));
                delete.addElement(cols);
              }
            }
          } else {
            Vector<String> mobileorder = Utility.splitVector(orderIdArray, ",");
            for (int x = 0; x < mobileorder.size(); x++) {
              Vector<String> cols = new Vector();
              cols.addElement(mobileorder.elementAt(x));
              delete.addElement(cols);
            }
          }

            rstInsert = new Nikitaset(rstView.getDataAllHeader(), insert);
            rstUpdate = new Nikitaset(rstView.getDataAllHeader(), update);
            rstDelete = new Nikitaset(rstView.getDataAllHeader(), delete);
            //outData.addData(Nset.newObject().setData("truncate", rstView.toNset()));

            //outData.addData(Nset.newObject().setData("notif", Nset.newObject().setData("title", "Mobile Entry").setData("message", "New Order")));
            //response.writeStream(outData.toJSON().replace("bigint", "int"));
            
            try {
                DataOutputStream dataOutputStream = new DataOutputStream (response.getHttpServletResponse().getOutputStream());
                ordertofile(dataOutputStream,  "truncate", rstView);
                ordertofile(dataOutputStream,  "notif", Nset.newObject().setData("title", "Mobile Entry").setData("message", "New Order"));
                dataOutputStream.flush();
            } catch (Exception e) {  }
        } else if (action.equals("RECEIVED")) {
            String strUpdateStatus = "UPDATE TRX_APP_IN_HDR SET LASTACTION ='DELIVER' WHERE ORDERID  IN (" + sbOrderId + ") AND LASTACTION <> 'CLOSE' AND OFFICER_ID = '" + officer.getText(0, 0) + "'";
            rstUpdateStatus = nc.Query(strUpdateStatus, new String[0]);
            response.writeStream(Nset.newObject().setData("status", "OK").toJSON());
        }
     }
    
    private void ordertofile(DataOutputStream outputStream, String key, Nikitaset data){
        int rows = 0;
        Vector<String> header =  new Vector<String>();
        try {                 
            Nikitaset nikitaset = new Nikitaset(data.getDataAllHeader(), new Vector<Vector<String>>(), "",data.getInfo());
            writeUTFX  (outputStream, Nset.newObject().setData(key, key).setData("rows", rows).setData("tablename", key).setData("nikitaset", nikitaset.toNset()).toJSON());
            for (int i = 0; i < data.getRows(); i++) {
                Vector<String> field = new Vector<String>();
                try {
                    field = data.getDataAllVector().elementAt(i);
                } catch (Exception e) { }
                writeUTFX  (outputStream, Nset.newArray().addData(field).toJSON());
            }                                
        } catch (Exception e) { }     
    }
    private void ordertofile(DataOutputStream outputStream, String key, Nset data){
        int rows = 0;
        try {                 
            Nikitaset nikitaset = new Nikitaset("none");
            writeUTFX  (outputStream, Nset.newObject().setData(key, key).setData("rows", rows).setData("tablename", key).setData("nikitaset", nikitaset.toNset()).toJSON());
            writeUTFX  (outputStream, Nset.newArray().addData(data).toJSON());
        } catch (Exception e) { }     
    }
    private void writeUTFX(DataOutputStream outputStream, String s){
        try {
            outputStream.writeInt(s.length());
            byte[] sb = s.getBytes();
            outputStream.write(sb, 0, sb.length);
        } catch (Exception e) {  }
    }
            
}
    

