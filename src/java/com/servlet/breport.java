/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.servlet;

import com.naa.data.Nson;
import com.naa.utils.InternetX;
import static com.naa.utils.InternetX.nikitaYToken;
import static com.naa.utils.InternetX.sendBroadcastIfUnauthorized401;
import static com.naa.utils.InternetX.urlEncode;
import com.nikita.generator.Component;
import com.nikita.generator.NikitaControler;
import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaRz;
import com.nikita.generator.NikitaService;
import com.nikita.generator.NikitaServlet;
import com.nikita.generator.Style;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.connection.NikitaInternet;
import com.nikita.generator.ui.Button;
import com.nikita.generator.ui.Combobox;
import com.nikita.generator.ui.DateTime;
import com.nikita.generator.ui.Label;
import com.nikita.generator.ui.Textarea;
import com.nikita.generator.ui.Textsmart;
import com.nikita.generator.ui.layout.HorizontalLayout;
import com.nikita.generator.ui.layout.NikitaForm;
import com.nikita.generator.ui.layout.VerticalLayout;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellCopyPolicy;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author rkrzmail
 */
public class breport extends NikitaServlet{
    public void OnRun(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
                Nson data = Nson.newObject();                
                data.set("sdr-y", Nson.readJson( String.valueOf(request.getParameter("sdry"))  ));
                data.set("sdr-m", Nson.readJson( String.valueOf(request.getParameter("sdrm"))  ));
                data.set("sdr-d", Nson.readJson( String.valueOf(request.getParameter("sdrd"))  ));
                
                data.set("sds-y", Nson.readJson( String.valueOf(request.getParameter("sdsy"))  ));
                data.set("sds-m", Nson.readJson( String.valueOf(request.getParameter("sdsm"))  ));
                data.set("sds-d", Nson.readJson( String.valueOf(request.getParameter("sdsd"))  ));
                
                
                data.set("ntt", Nson.readJson( String.valueOf(request.getParameter("ntt"))  ));
                data.set("dtd", Nson.readJson( String.valueOf(request.getParameter("dtd"))  ));
                data.set("ytd", Nson.readJson( String.valueOf(request.getParameter("ytd"))  ));
                
                
                
                String tanggal =String.valueOf(request.getParameter("tanggal"));                 
                String fname = String.valueOf(request.getParameter("fname"));           
                response.getHttpServletResponse().setContentType("application/vnd.ms-excel");
                response.getHttpServletResponse().setHeader("Content-disposition", "attachment; filename="+fname);  
                
                try {
                    //openTemplteA( tanggal, new FileInputStream(template), data,   new FileOutputStream(filex) );
                    openTemplteA( tanggal, request.getHttpServletRequest().getServletContext().getResourceAsStream("/WEB-INF/Template.xlsx"), data,   response.getHttpServletResponse().getOutputStream()  );
                } catch (IOException ex) {

                }
                 
                
        
    }   
   int colCount(String cellname){
        cellname = cellname.trim().toUpperCase();
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < cellname.length(); i++) {
            if ("ABCDEFGHIJKLMNOPQRSTUVWXYZ".contains(cellname.substring(i, i+1))) {
                buffer.append(cellname.substring(i, i+1));
            }
        }
        cellname = buffer.toString();
        int col = 0;
        for (int i = 0; i < cellname.length(); i++) {             
            col = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(cellname.substring(i, i+1)) + 26*i ;
        }
        return col;
    }
    int rowCount(String cellname){
        cellname = cellname.trim().toUpperCase();//C2
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < cellname.length(); i++) {
            if ("0123456789".contains(cellname.substring(i, i+1))) {
                buffer.append(cellname.substring(i, i+1));
            }
        }
        return Integer.parseInt(buffer.toString())-1;
    }
    void fillSumCol(XSSFSheet sheet , String col, Nson data, String tanggal){
                getCell(sheet, col+"4").setCellValue(tanggal);//dae todate
                getCell(sheet, col+"6").setCellValue(  getTextInt(data, 0, "Tertarik")+
                                                       getTextInt(data, 0, "Tidak_Tertarik") +
                                                       getTextInt(data, 0, "Pikir_Pikir") +
                                                       getTextInt(data, 0, "Minta_Dihubungi_Kembali") +
                                                       getTextInt(data, 0, "Tidak_Diangkat") +
                                                       getTextInt(data, 0, "Nada_Sibuk") +
                                                       getTextInt(data, 0, "Salah_Sambung") +
                                                       getTextInt(data, 0, "Tidak_Terdaftar")  );//total call
                
                getCell(sheet, col+"9").setCellValue(  getTextInt(data, 0, "Tertarik") );//Nasabah tertarik
                getCell(sheet, col+"10").setCellValue( getTextInt(data, 0, "Tidak_Tertarik") );//Nasabah Tidak tertarik
                getCell(sheet, col+"11").setCellValue( getTextInt(data, 0, "Pikir_Pikir") );//Nasabah Pikir Pikir
                getCell(sheet, col+"12").setCellValue( getTextInt(data, 0, "Minta_Dihubungi_Kembali") );//Nasabah Minta Dihubungi Kembali
                getCell(sheet, col+"13").setCellValue( getTextInt(data, 0, "komplit") );//Aplikasi Komplit
                getCell(sheet, col+"14").setCellValue( getTextInt(data, 0, "fos") );//Aplikasi Input FOS
                
                getCell(sheet, col+"17").setCellValue( getTextInt(data, 0, "Tidak_Diangkat") );//Telpon Tidak Diangkat
                getCell(sheet, col+"18").setCellValue( getTextInt(data, 0, "Nada_Sibuk") );//Telpon Nada Sibuk
                getCell(sheet, col+"19").setCellValue( getTextInt(data, 0, "Salah_Sambung")  );//Telpon Salah Sambung
                getCell(sheet, col+"20").setCellValue( getTextInt(data, 0, "Tidak_Terdaftar") );//Telpon Tidak Terdaftar
        
                getCell(sheet, col+"22").setCellValue( getTextInt(data, 0, "Sudah_Naik_Haji") );//Sudah_Naik_Haji
                getCell(sheet, col+"23").setCellValue( getTextInt(data, 0, "Sudah_Punya_Porsi")+getTextInt(data, 0, "Sudah_Punya_Porsi_Muamalat")  );//Sudah Punya Porsi
                getCell(sheet, col+"24").setCellValue( getTextInt(data, 0, "Ingin_Menabung_Saja") );//Ingin Menabung Saja
                getCell(sheet, col+"25").setCellValue( getTextInt(data, 0, "Masalah_Syariah") );//Masalah Syariah
                getCell(sheet, col+"26").setCellValue( getTextInt(data, 0, "Plafon_Kecil") );//Plafon Kecil
                getCell(sheet, col+"27").setCellValue( getTextInt(data, 0, "Pricing") );//Pricing / Nilai Angsuran
                getCell(sheet, col+"28").setCellValue( getTextInt(data, 0, "Tenor_Pendek") );//Tenor Pendek
                getCell(sheet, col+"29").setCellValue( getTextInt(data, 0, "Tanpa_Alasan") );//Tanpa Alasan
                getCell(sheet, col+"30").setCellValue( getTextInt(data, 0, "Salah_Klik") );//Salah Klik (khusus data Sales trigger)
    }
    void filldetCol(XSSFSheet sheet ,   Nson data, String tanggal){
        CellCopyPolicy cellCopyPolicy = new CellCopyPolicy().createBuilder().build();
        getCell(sheet, "C3").setCellValue(tanggal);
        if (data.get("data").size()>=1) {
            sheet.shiftRows(6, sheet.getLastRowNum(), data.get("data").size()); 
        }        
        for (int i = 0; i < data.get("data").size() ; i++) {                    
            if (i < data.get("data").size()-1) {
                sheet.copyRows( 5, 5, 6+i, cellCopyPolicy);
            }                    
            getCell(sheet, "B"+(6+i)).setCellValue(tanggal);
            getCell(sheet, "C"+(6+i)).setCellValue(getText(data, i, "NAMA"));
            getCell(sheet, "D"+(6+i)).setCellValue(getText(data, i, "USER_ASSIGN"));

            getCell(sheet, "H"+(6+i)).setCellValue( getTextInt(data, i, "Tertarik") );                            
            getCell(sheet, "I"+(6+i)).setCellValue( getTextInt(data, i, "Tidak_Tertarik") );//Nasabah Tidak tertarik
            getCell(sheet, "J"+(6+i)).setCellValue( getTextInt(data, i, "Pikir_Pikir") );//Nasabah Pikir Pikir
            getCell(sheet, "K"+(6+i)).setCellValue( getTextInt(data, i, "Minta_Dihubungi_Kembali") );//Nasabah Minta Dihubungi Kembali


            getCell(sheet, "L"+(6+i)).setCellValue( getTextInt(data, i, "Tidak_Diangkat") );//Telpon Tidak Diangkat
            getCell(sheet, "M"+(6+i)).setCellValue( getTextInt(data, i, "Nada_Sibuk") );//Telpon Nada Sibuk
            getCell(sheet, "N"+(6+i)).setCellValue( getTextInt(data, i, "Salah_Sambung")  );//Telpon Salah Sambung
            getCell(sheet, "O"+(6+i)).setCellValue( getTextInt(data, i, "Tidak_Terdaftar") );//Telpon Tidak Terdaftar
        }
                
    }
    void fillTso(XSSFSheet sheet ,   Nson data, String tanggal){
        CellCopyPolicy cellCopyPolicy = new CellCopyPolicy().createBuilder().build();
        for (int i = 0; i < data.get("data").size() ; i++) {  
            int step = 14+i*13; 
            if (i>=1) {
                step =14+(i-1)*13; 
                sheet.copyRows( 2-1, 12-1, step, cellCopyPolicy);
            }else{
                step =14+i*13-13; 
            }                   

            getCell(sheet, "B"+(2+step-1)).setCellValue("TSO  "+ getText(data, i, "NAMA"));
            getCell(sheet, "C"+(2+step-1)).setCellValue(getText(data, i, "USER_ASSIGN"));                    

            getCell(sheet, "C"+(4+step-1)).setCellValue(getTextInt(data, i, "Sudah_Naik_Haji")+getTextInt(data, 0, "Sudah_Punya_Porsi_Muamalat")); 
            getCell(sheet, "C"+(5+step-1)).setCellValue(getTextInt(data, i, "Sudah_Punya_Porsi")); 
            getCell(sheet, "C"+(6+step-1)).setCellValue(getTextInt(data, i, "Ingin_Menabung_Saja")); 
            getCell(sheet, "C"+(7+step-1)).setCellValue(getTextInt(data, i, "Masalah_Syariah")); 
            getCell(sheet, "C"+(8+step-1)).setCellValue(getTextInt(data, i, "Plafon_Kecil")); 
            getCell(sheet, "C"+(9+step-1)).setCellValue(getTextInt(data, i, "Pricing")); 
            getCell(sheet, "C"+(10+step-1)).setCellValue(getTextInt(data, i, "Tenor_Pendek")); 
            getCell(sheet, "C"+(11+step-1)).setCellValue(getTextInt(data, i, "Tanpa_Alasan")); 
            getCell(sheet, "C"+(12+step-1)).setCellValue(getTextInt(data, i, "Salah_Klik")); 

        }                 
    }
    public Cell getCell(XSSFSheet fSheet, String cellname){
         return  fSheet.getRow(rowCount(cellname)).getCell(colCount(cellname));
    } 
    
     private static Number getNumber(Object n) {
        if (n instanceof Number) {
            return ((Number) n);
        } else if (isDecimalNumber(String.valueOf(n))) {
            return Double.valueOf(String.valueOf(n));
        }
        return 0;
    }

    private static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    private static boolean isDecimalNumber(String str) {
        return str.matches("^[-+]?[0-9]*.?[0-9]+([eE][-+]?[0-9]+)?$");
    }

    private static boolean isLongIntegerNumber(String str) {
        return str.matches("-?\\d+");
    }
    
    public int getTextInt(Nson data, int row, String col) { 
        return getNumber(getText(data, row, col)).intValue();
    }
            
    public String getText(Nson data, int row, int col) {
        try {
            String result = data.get("data").get(row).get(col).asString();
            if (result!=null) {
                return result;
            }
        } catch (Exception e) {}
        return "";
    } 
    
  
    public String getText(Nson data, int row, String colname) {
    	//int col = header!=null ? header.indexOf(colname): -1;
        int col = -1;
        for (int i = 0; i < data.get("header").size(); i++) {
            if (data.get("header").get(i).asString().equalsIgnoreCase(colname)) {//nocase sensitife
                col = i;
                break;
            }              
        }
            
    	if (col >=0 ) {
            return getText(data, row, col);
        }else{
            if (data.get("info").isNson()) {
                Nson n = data.get("info") ;                    
                if (n.containsKey("metadata") && n.get("metadata").containsKey("name")) {
                    Nson nAS = n.get("metadata").get("name");	                 
                    for (int i = 0; i < nAS.size(); i++) {
                        String name = nAS.get(i).toString();
                        if (name.equalsIgnoreCase(colname)) {//nocase sensitife
                            return getText(data, row, i);
                        }                        
                    }  
                }               
            } 
        }
    	return "";
    	//return getText(row, header.indexOf(colname));
    }
    
    void openTemplteA(String tanggal , InputStream is, Nson setting, OutputStream outputStream){
        try {
            String value;
            Row row;
            Cell cell;
            Iterator<Row> rowIterator = null;  
            
                XSSFWorkbook wb = new XSSFWorkbook(is);
                XSSFSheet sheet = wb.getSheetAt(0);
                
                /*
                if (setting.get("sheet").isNumber()) {
                    //sheet = wb.getSheetAt(setting.get("sheet").asInteger());
                }else{
                    //sheet = wb.getSheet(setting.get("sheet").asString());
                }
                */
                //data = Nson.readJson("{\"error\":\"\",\"data\":[[\"7\",\"332\",\"114\",\"178\",\"933\",\"234\",\"48\",\"364\",\"0\",\"178\",\"66\",\"37\",\"6\",\"0\",\"3\",\"1\",\"40\",\"1\"]],\"info\":{\"core\":0,\"metadata\":{\"label\":[\"Tertarik\",\"Tidak_Tertarik\",\"Pikir_Pikir\",\"Minta_Dihubungi_Kembali\",\"Tidak_Diangkat\",\"Nada_Sibuk\",\"Salah_Sambung\",\"Tidak_Terdaftar\",\"Sudah_Naik_Haji\",\"Sudah_Punya_Porsi_Muamalat\",\"Sudah_Punya_Porsi\",\"Ingin_Menabung_Saja\",\"Masalah_Syariah\",\"Plafon_Kecil\",\"Pricing\",\"Tenor_Pendek\",\"Tanpa_Alasan\",\"Salah_Klik\"],\"name\":[\"Tertarik\",\"Tidak_Tertarik\",\"Pikir_Pikir\",\"Minta_Dihubungi_Kembali\",\"Tidak_Diangkat\",\"Nada_Sibuk\",\"Salah_Sambung\",\"Tidak_Terdaftar\",\"Sudah_Naik_Haji\",\"Sudah_Punya_Porsi_Muamalat\",\"Sudah_Punya_Porsi\",\"Ingin_Menabung_Saja\",\"Masalah_Syariah\",\"Plafon_Kecil\",\"Pricing\",\"Tenor_Pendek\",\"Tanpa_Alasan\",\"Salah_Klik\"],\"size\":[24,24,24,24,24,24,24,24,24,24,24,24,24,24,24,24,24,24],\"type\":[\"DECIMAL\",\"DECIMAL\",\"DECIMAL\",\"DECIMAL\",\"DECIMAL\",\"DECIMAL\",\"DECIMAL\",\"DECIMAL\",\"DECIMAL\",\"DECIMAL\",\"DECIMAL\",\"DECIMAL\",\"DECIMAL\",\"DECIMAL\",\"DECIMAL\",\"DECIMAL\",\"DECIMAL\",\"DECIMAL\"]},\"coretime\":22,\"time\":22},\"nfid\":\"Nikitaset\",\"header\":[\"Tertarik\",\"Tidak_Tertarik\",\"Pikir_Pikir\",\"Minta_Dihubungi_Kembali\",\"Tidak_Diangkat\",\"Nada_Sibuk\",\"Salah_Sambung\",\"Tidak_Terdaftar\",\"Sudah_Naik_Haji\",\"Sudah_Punya_Porsi_Muamalat\",\"Sudah_Punya_Porsi\",\"Ingin_Menabung_Saja\",\"Masalah_Syariah\",\"Plafon_Kecil\",\"Pricing\",\"Tenor_Pendek\",\"Tanpa_Alasan\",\"Salah_Klik\"]}");
                Nson  data;
                
                sheet = wb.getSheet("Summary_Data Reguler");//Summary_Data Sales Trigger
                data = setting.get("sdr-y");                    
                fillSumCol(sheet, "C", data,tanggal); 
                data = setting.get("sdr-m");  
                fillSumCol(sheet, "G", data,tanggal);
                data = setting.get("sdr-d");  
                fillSumCol(sheet, "K", data,tanggal);
                
                sheet = wb.getSheet("Summary_Data Sales Trigger");//Summary_Data Sales Trigger         
                data = setting.get("sds-y");                        
                fillSumCol(sheet, "C", data,tanggal);
                data = setting.get("sds-m");  
                fillSumCol(sheet, "G", data,tanggal);
                data = setting.get("sds-d");  
                fillSumCol(sheet, "K", data,tanggal);
                
               
                 
             
               
                
                //data = Nson.readJson("{\"error\":\"\",\"data\":[[\"Indiryani\",\"M0001\",\"122\",\"0\",\"14\",\"3\",\"7\",\"65\",\"31\",\"1\",\"1\",\"0\",\"3\",\"5\",\"1\",\"1\",\"0\",\"0\",\"0\",\"3\",\"1\"],[\"Syaiful\",\"M0002\",\"122\",\"0\",\"24\",\"0\",\"17\",\"46\",\"8\",\"1\",\"26\",\"0\",\"6\",\"12\",\"6\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\"],[\"Abdul Hakim\",\"M0003\",\"119\",\"0\",\"19\",\"8\",\"5\",\"31\",\"36\",\"0\",\"20\",\"0\",\"9\",\"3\",\"3\",\"0\",\"0\",\"0\",\"0\",\"4\",\"0\"],[\"Vidi\",\"M0004\",\"105\",\"0\",\"17\",\"16\",\"6\",\"31\",\"15\",\"2\",\"18\",\"0\",\"11\",\"1\",\"0\",\"1\",\"0\",\"0\",\"0\",\"4\",\"0\"],[\"Suryadi\",\"M0005\",\"94\",\"0\",\"13\",\"6\",\"6\",\"51\",\"0\",\"2\",\"16\",\"0\",\"7\",\"0\",\"5\",\"0\",\"0\",\"0\",\"0\",\"1\",\"0\"],[\"Yogi Purnawati\",\"M0006\",\"102\",\"0\",\"16\",\"6\",\"12\",\"21\",\"1\",\"1\",\"45\",\"0\",\"11\",\"0\",\"1\",\"0\",\"0\",\"0\",\"0\",\"4\",\"0\"],[\"Lela Sari\",\"M0007\",\"107\",\"0\",\"31\",\"3\",\"6\",\"29\",\"7\",\"4\",\"27\",\"0\",\"26\",\"1\",\"2\",\"1\",\"0\",\"0\",\"0\",\"1\",\"0\"],[\"Giyo Sapto Wasono\",\"M0008\",\"127\",\"1\",\"15\",\"0\",\"15\",\"72\",\"2\",\"2\",\"20\",\"0\",\"10\",\"0\",\"5\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\"],[\"Ahmad Faizal\",\"M0009\",\"213\",\"0\",\"30\",\"0\",\"9\",\"123\",\"33\",\"2\",\"16\",\"0\",\"3\",\"19\",\"8\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\"],[\"Evrillia\",\"M0010\",\"152\",\"0\",\"27\",\"10\",\"9\",\"67\",\"18\",\"4\",\"17\",\"0\",\"14\",\"5\",\"1\",\"0\",\"0\",\"2\",\"1\",\"4\",\"0\"],[\"Fitri Yerni\",\"M0011\",\"82\",\"1\",\"6\",\"4\",\"9\",\"36\",\"1\",\"4\",\"21\",\"0\",\"6\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\"],[\"Yuri Priyo Sumbodo\",\"M0012\",\"106\",\"0\",\"38\",\"0\",\"8\",\"36\",\"1\",\"1\",\"22\",\"0\",\"27\",\"7\",\"1\",\"0\",\"0\",\"0\",\"0\",\"3\",\"0\"],[\"Septian Antono\",\"M0013\",\"99\",\"0\",\"13\",\"0\",\"8\",\"3\",\"0\",\"7\",\"68\",\"0\",\"7\",\"3\",\"1\",\"0\",\"0\",\"1\",\"0\",\"1\",\"0\"],[\"Novita Arianti\",\"M0014\",\"98\",\"1\",\"21\",\"5\",\"9\",\"38\",\"13\",\"6\",\"5\",\"0\",\"12\",\"1\",\"3\",\"0\",\"0\",\"0\",\"0\",\"5\",\"0\"],[\"Jayanti Sri Lestari\",\"M0015\",\"91\",\"4\",\"7\",\"3\",\"12\",\"56\",\"5\",\"1\",\"3\",\"0\",\"0\",\"5\",\"0\",\"1\",\"0\",\"0\",\"0\",\"1\",\"0\"],[\"Robiah\",\"M0016\",\"127\",\"0\",\"5\",\"0\",\"22\",\"43\",\"47\",\"6\",\"4\",\"0\",\"4\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"1\",\"0\"],[\"Indah Purnama Sari. SE \",\"M0017\",\"110\",\"0\",\"9\",\"29\",\"5\",\"40\",\"14\",\"1\",\"12\",\"0\",\"6\",\"0\",\"0\",\"2\",\"0\",\"0\",\"0\",\"1\",\"0\"],[\"Naswati\",\"M0018\",\"119\",\"0\",\"17\",\"8\",\"10\",\"75\",\"2\",\"1\",\"6\",\"0\",\"8\",\"2\",\"0\",\"0\",\"0\",\"0\",\"0\",\"7\",\"0\"],[\"Indrie Lestari \",\"M0019\",\"115\",\"0\",\"10\",\"13\",\"3\",\"70\",\"0\",\"2\",\"17\",\"0\",\"8\",\"2\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\"]],\"info\":{\"core\":0,\"metadata\":{\"label\":[\"NAMA\",\"USER_ASSIGN\",\"TOTAL\",\"Tertarik\",\"Tidak_Tertarik\",\"Pikir_Pikir\",\"Minta_Dihubungi_Kembali\",\"Tidak_Diangkat\",\"Nada_Sibuk\",\"Salah_Sambung\",\"Tidak_Terdaftar\",\"Sudah_Naik_Haji\",\"Sudah_Punya_Porsi_Muamalat\",\"Sudah_Punya_Porsi\",\"Ingin_Menabung_Saja\",\"Masalah_Syariah\",\"Plafon_Kecil\",\"Pricing\",\"Tenor_Pendek\",\"Tanpa_Alasan\",\"Salah_Klik\"],\"name\":[\"NAMA\",\"USER_ASSIGN\",\"TOTAL\",\"Tertarik\",\"Tidak_Tertarik\",\"Pikir_Pikir\",\"Minta_Dihubungi_Kembali\",\"Tidak_Diangkat\",\"Nada_Sibuk\",\"Salah_Sambung\",\"Tidak_Terdaftar\",\"Sudah_Naik_Haji\",\"Sudah_Punya_Porsi_Muamalat\",\"Sudah_Punya_Porsi\",\"Ingin_Menabung_Saja\",\"Masalah_Syariah\",\"Plafon_Kecil\",\"Pricing\",\"Tenor_Pendek\",\"Tanpa_Alasan\",\"Salah_Klik\"],\"size\":[64,255,21,24,24,24,24,24,24,24,24,24,24,24,24,24,24,24,24,24,24],\"type\":[\"VARCHAR\",\"VARCHAR\",\"BIGINT\",\"DECIMAL\",\"DECIMAL\",\"DECIMAL\",\"DECIMAL\",\"DECIMAL\",\"DECIMAL\",\"DECIMAL\",\"DECIMAL\",\"DECIMAL\",\"DECIMAL\",\"DECIMAL\",\"DECIMAL\",\"DECIMAL\",\"DECIMAL\",\"DECIMAL\",\"DECIMAL\",\"DECIMAL\",\"DECIMAL\"]},\"coretime\":39,\"time\":39},\"nfid\":\"Nikitaset\",\"header\":[\"NAMA\",\"USER_ASSIGN\",\"TOTAL\",\"Tertarik\",\"Tidak_Tertarik\",\"Pikir_Pikir\",\"Minta_Dihubungi_Kembali\",\"Tidak_Diangkat\",\"Nada_Sibuk\",\"Salah_Sambung\",\"Tidak_Terdaftar\",\"Sudah_Naik_Haji\",\"Sudah_Punya_Porsi_Muamalat\",\"Sudah_Punya_Porsi\",\"Ingin_Menabung_Saja\",\"Masalah_Syariah\",\"Plafon_Kecil\",\"Pricing\",\"Tenor_Pendek\",\"Tanpa_Alasan\",\"Salah_Klik\"]}");
                
                data = setting.get("ntt");
                //Nasabah Tidak Tertarik Per TSO
                sheet = wb.getSheet("Nasabah Tidak Tertarik Per TSO");
                fillTso(sheet, data, tanggal);
                
                data = setting.get("dtd");
                sheet = wb.getSheet("Detail_DTD"); //B6
                filldetCol(sheet,   data, tanggal);
                
                data = setting.get("ytd");
                sheet = wb.getSheet("Detail_YTD"); //B6
                filldetCol(sheet,   data, tanggal);
                
                
                //FileOutputStream output_file =new FileOutputStream("D:\\test1.xlsx");//Open FileOutputStream to write updates
                wb.getCreationHelper().createFormulaEvaluator().evaluateAll();              
                wb.write(outputStream); //write changes
                outputStream.flush();  
                wb.close();
        } catch (Exception e) {        
            e.printStackTrace();
        }
    }
    
    
}
