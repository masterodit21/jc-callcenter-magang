/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.rkrzmail.nikita.data;

import com.rkrzmail.nikita.utility.Utility;
import java.awt.Font;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
//import javafx.scene.input.DataFormat;
 
import jxl.Sheet;
import jxl.Workbook;
import jxl.biff.DisplayFormat;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.read.biff.BiffException;
import jxl.write.BoldStyle;
import jxl.write.Label;
import jxl.write.NumberFormat;
import jxl.write.WritableCell;
import jxl.write.WritableCellFeatures;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.biff.WritableFontRecord;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.format.CellFormat;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author rkrzmail
 */
public class NikitaSpreadsheet {
     public static void saveToExcel(final Nikitaset data, final OutputStream out) {
        saveToExcel("sheet", data, out);
    }
    
    public static void saveToExcel(final String sheetname, final Nikitaset data, final OutputStream out) {
        try {
            WritableWorkbook workbook = Workbook.createWorkbook(out);
            WritableSheet sheet = workbook.createSheet(sheetname, 0);            
            
            for (int i = 0; i < data.getCols(); i++) {
                Label label = new Label(i, 0, data.getHeader(i)); 
                sheet.addCell(label); 
            }
            for (int row = 0; row < data.getRows(); row++) {
                for (int i = 0; i < data.getCols(); i++) {
                    Label label = new Label(i, row+1, data.getText(row, i)); 
                    sheet.addCell(label); 
                }                
            }
            //jxl.write.Number number = new xl.write.Number(3, 4, 3.1459) {}; 
            //sheet.addCell(number);
            workbook.write(); 
            workbook.close();
        } catch (Exception ex) { }
    }
    private static int colCount(String cellname){
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
    private static int rowCount(String cellname){
        cellname = cellname.trim().toUpperCase();//C2
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < cellname.length(); i++) {
            if ("0123456789".contains(cellname.substring(i, i+1))) {
                buffer.append(cellname.substring(i, i+1));
            }
        }
        return Integer.parseInt(buffer.toString())-1;
    }
    public static void saveToExcelx(final String sheetname, final Nikitaset data, final OutputStream out, Nset options) {
        try {
            final int maxShrinkToFit = 32;
            final WritableWorkbook workbook = Workbook.createWorkbook(out);
            final WritableSheet sheet = workbook.createSheet(sheetname, 0);
            int start = 0;   
            
            if (options.containsKey("headers")) {
                String[] keys = options.getData("headers").getObjectKeys();
                for (int i = 0; i < keys.length; i++) {
                    int row = rowCount(keys[i]);
                    start = row>start?row:start;
                    
                    Label label = new Label(colCount(keys[i]), row, options.getData("headers").getData(keys[i]).toString());                    
                    sheet.addCell((WritableCell)label);                    
                }
            }
            if (options.containsKey("start")) {
                start   = options.getData("start").toInteger();  
            }else{
                start   = start +  (options.containsKey("headers")?1:0);  
            }            
            for (int i = 0; i < data.getCols(); ++i) {
                final Label label = new Label(i, start, data.getHeader(i));
                        WritableCellFormat numberFormat = new WritableCellFormat();                       
                        numberFormat.setAlignment(Alignment.CENTRE);
                        
                        numberFormat.setBackground(Colour.GRAY_25);
                        WritableFont  font = new WritableFont(WritableFont.ARIAL);
                        font.setBoldStyle(WritableFont.BOLD);
                        numberFormat.setFont(font);
                                
                        label.setCellFormat(numberFormat);
                sheet.addCell((WritableCell)label);
            }
            start = start+1;//plus header grid
            for (int row = 0; row < data.getRows(); ++row) {
                for (int j = 0; j < data.getCols(); ++j) {
                    final String str = data.getText(row, j);
                    WritableCell cell;
                    if (str.equalsIgnoreCase("0")) {
                        cell = (WritableCell)new jxl.write.Number(j, row + start, (double)Utility.getLong(data.getText(row, j)));
                    } else if (str.startsWith("0")) {
                        cell = (WritableCell)new Label(j, row + start, data.getText(row, j));
                  
                    } else if (Utility.isLongIntegerNumber(str)) {
                        double d = Utility.getLong(data.getText(row, j));
                        cell = (WritableCell)new jxl.write.Number(j, row + start,  d);
                        //String f = options.containsKey("curr") && options.getData("curr").containsValue(String.valueOf(j)) ?  "#,##0": "#" ;
                        NumberFormat decimalNo = new NumberFormat( "#"); 
                        WritableCellFormat numberFormat = new WritableCellFormat(decimalNo); 
                        cell.setCellFormat(numberFormat);                                 
                    }  else if (Utility.isNumeric(str)) {
                        cell = (WritableCell)new jxl.write.Number(j, row + start, Utility.getDouble((Object)data.getText(row, j)));

                    }  else {
                        cell = (WritableCell)new Label(j, row + start, data.getText(row, j));
                        /*
                        if (str.length()<=maxShrinkToFit) {
                            WritableCellFormat numberFormat = new WritableCellFormat();
                            cell.setCellFormat(numberFormat);
                        }
                        */
                    }
                    
                    sheet.addCell(cell);
                }
            }
            
            if (options.containsKey("summary")) {
                
            }
            workbook.write();
            workbook.close();
        }
        catch (Exception ex) {}
    }
    
    public static void saveToCsv(final boolean quote, final Nikitaset data, final OutputStream out) {
        final byte[] delimiter = ";".getBytes();
        final byte[] newline = "\r\n".getBytes();
        final byte[] bquotes = "\"".getBytes();
        try {
            final StringBuilder stringBuffer = new StringBuilder();
            for (int i = 0; i < data.getCols(); ++i) {
                if (i >= 1) {
                    out.write(delimiter);
                }
                if (quote) {
                    out.write(bquotes);
                }
                out.write(data.getHeader(i).getBytes());
                if (quote) {
                    out.write(bquotes);
                }
            }
            for (int row = 0; row < data.getRows(); ++row) {
                if (row >= 1) {
                    out.write(newline);
                }
                for (int j = 0; j < data.getCols(); ++j) {
                    if (j >= 1) {
                        out.write(delimiter);
                    }
                    if (quote) {
                        out.write(bquotes);
                    }
                    out.write(data.getText(row, j).getBytes());
                    if (quote) {
                        out.write(bquotes);
                    }
                }
            }
        }
        catch (Exception ex) {}
    }
    
    public static Nikitaset readxToNikitaset(InputStream data, boolean isxls, Nson setting){
        try {
            Vector<Vector<String>> xls = new Vector<Vector<String>>();
            Vector<String> hdr = new Vector<String>();
            Vector<String> col = new Vector<String>();
            
            String value;
            Row row;
            Cell cell;
            
            Iterator<Row> rowIterator = null;           
            
            if (isxls) {
                 HSSFWorkbook fWorkbook =  new HSSFWorkbook(data);
                HSSFSheet fSheet = fWorkbook.getSheetAt(0);
                rowIterator = fSheet.iterator();
            }else{
                XSSFWorkbook wb = new XSSFWorkbook(data);
                XSSFSheet sheet = wb.getSheetAt(0);
                rowIterator = sheet.iterator();
            }
             
                    
            int count = 0; boolean b = setting.getData("rows").asBoolean();
            while (rowIterator.hasNext()) {
                    row = rowIterator.next();
                    Iterator<Cell> cellIterator = row.cellIterator();
                    col = new Vector<>();                            
                     
                    while (cellIterator.hasNext()) {
                        //count ++ ;
                         
                        cell = cellIterator.next();
                        if (xls.size() == 0) {
                            //headers
                            col.addElement(cell.getStringCellValue().trim());//tream header       
                        }else if (b){
                            //just get rows
                        }else{
                            //valdasi jumlah col sesuai header
                            if (col.size() < xls.elementAt(0).size()) {
                                switch (cell.getCellType()) {
                                 case Cell.CELL_TYPE_FORMULA:
                                    try {
                                       value = String.valueOf(cell.getStringCellValue());
                                    } catch (Exception e) {
                                        value = "=" + cell.getCellFormula();
                                    }                                 
                                    break;                                    
                                case Cell.CELL_TYPE_BOOLEAN:
                                    value = String.valueOf(cell.getBooleanCellValue());
                                    break;
                                case Cell.CELL_TYPE_NUMERIC:
                                    value =  new DecimalFormat("#.##########", DecimalFormatSymbols.getInstance(Locale.US) ).format( cell.getNumericCellValue() );
                                    //value =  String.valueOf( cell.getNumericCellValue());
                                    break;
                                case Cell.CELL_TYPE_STRING:
                                    value =   cell.getStringCellValue();
                                    break;
                                case Cell.CELL_TYPE_BLANK:
                                    value = "";
                                    break;                                 
                                default:
                                    if (cell.getCellTypeEnum() == CellType.ERROR) {
                                        value = "";                                        
                                    }else if (cell.getCellTypeEnum() == CellType._NONE) {
                                        value = "";
                                    }else{                                         
                                        value = cell.getStringCellValue();
                                    }                                    
                                    
                                    break;
                                }
                                
                                for (int i = col.size(); i < cell.getColumnIndex(); i++) {
                                    col.addElement("");//bacnk       
                                }
                                col.addElement(value);//value 
                            }                                       
                        } 
                    }
                    if (xls.size() >=1) {
                        //valdasi jumlah col sesuai header
                        for (int i = col.size(); i < xls.elementAt(0).size(); i++) {
                            col.addElement("");//bacnk       
                        }
                    }
                    
                    boolean isBlanlAll = true;
                    for (int i = 0; i < col.size(); i++) {
                        if (col.get(i).trim().equalsIgnoreCase("")) {                            
                        }else{
                            isBlanlAll = false; break;
                        }
                    }
                    if (isBlanlAll) {  
                        //count --;
                        break;
                    }
                   
                    xls.addElement(col);//add to row

            }    
            setting.setData("rows", xls.size()-1);
           
            /*
            String 
            find = sheet.getRow(xls.size()).getCell(col.size()).getStringCellValue();
            find = find!=null?find:"";
            //header
            while (find.trim().length()>=1) {                 
                col.addElement(find.trim());//tream header                 
                try {
                    find = sheet.getRow(xls.size()).getCell(col.size()).getStringCellValue();
                    find = find!=null?find:"";
                } catch (Exception e) { find ="";  }
            }             
            xls.addElement(col);
                 
            
            find = sheet.getCell(0,xls.size()).getContents();//col 0 (data pertama_
            find = find!=null?find:"";
            while (find.trim().length()>=1) {
                col = new Vector<>();
                col.addElement(find);
                for (int i = 1; i < xls.elementAt(0).size(); i++) {
                    try {
                        find = sheet.getCell(i, xls.size()).getContents();
                        find = find!=null?find:"";
                    } catch (Exception e) { find ="";  }
                    col.addElement(find);
                }
                xls.addElement(col);//next ++
                
                try {
                    find = sheet.getCell(0, xls.size()).getContents();//col 0 (data selanjutnya)
                    find = find!=null?find:"";
                } catch (Exception e) { find ="";  }
            } 
            */
            data.close();
             
            if (xls.size()>=1) {
                hdr=xls.elementAt(0);
                xls.remove(0);
                return new Nikitaset(hdr, xls);
            }
            data.close();
            return new Nikitaset("Empty");
        } catch (Exception ex) { 
            ex.printStackTrace();
            return new Nikitaset(ex.getMessage()); 
        }
    }
    public static Nikitaset readToNikitaset(InputStream data){
        try {
            
            Workbook workbook = Workbook.getWorkbook(data);    
            //WritableWorkbook copy = Workbook.createWorkbook(new File("output.xls"), workbook);
            
            Sheet sheet = workbook.getSheet(0);
            
            Vector<Vector<String>> xls = new Vector<Vector<String>>();
            Vector<String> hdr = new Vector<String>();
            Vector<String> col = new Vector<String>();
            String 
            find = sheet.getCell(col.size(),xls.size()).getContents();
            find = find!=null?find:"";
 
            //header
            while (find.trim().length()>=1) {                 
                col.addElement(find.trim());//tream header
                 
                try {
                    find = sheet.getCell(col.size(),xls.size()).getContents();
                    find = find!=null?find:"";
                } catch (Exception e) { find ="";  }
            }             
            xls.addElement(col);
                      
            
            find = sheet.getCell(0,xls.size()).getContents();//col 0 (data pertama_
            find = find!=null?find:"";
            while (find.trim().length()>=1) {
                col = new Vector<>();
                col.addElement(find);
                for (int i = 1; i < xls.elementAt(0).size(); i++) {
                    try {
                        find = sheet.getCell(i, xls.size()).getContents();
                        find = find!=null?find:"";
                    } catch (Exception e) { find ="";  }
                    col.addElement(find);
                }
                xls.addElement(col);//next ++
                
                try {
                    find = sheet.getCell(0, xls.size()).getContents();//col 0 (data selanjutnya)
                    find = find!=null?find:"";
                } catch (Exception e) { find ="";  }
            } 
            workbook.close();
             
            if (xls.size()>=1) {
                hdr=xls.elementAt(0);
                xls.remove(0);
                return new Nikitaset(hdr, xls);
            }
            data.close();
            return new Nikitaset("Empty");
        } catch (Exception ex) { 
            return new Nikitaset(ex.getMessage()); 
        }
    }
    public static Nikitaset readCsvToNikitaset(InputStream data){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {            
            byte[] buffer = new byte[1024];
            int length;
            while ((length = data.read(buffer)) > 0) {
                baos.write(buffer, 0, length);
            }
            data.close();            
        } catch (Exception ex) {}
        return readCsvToNikitaset(baos.toString());
    }
    public static Nikitaset readCsvToNikitaset(String data){
        Vector<Vector<String>> xls = new Vector<Vector<String>>();
        Vector<String> hdr = new Vector<String>();
        Vector<String> col = new Vector<String>();
        try {
            char delimiter;
            if (data.indexOf(";")>=0) {
                delimiter=';';
            }else{
                delimiter=',';
            }
            CSVParser parser = CSVParser.parse(data,  CSVFormat.EXCEL.withDelimiter(delimiter));
            List<CSVRecord> cSVRecord = parser.getRecords();
            for (int i = 0; i < cSVRecord.size(); i++) {
                col = new Vector<String>();
                if (i==0) {
                    for (int c = 0; c < cSVRecord.get(i).size(); c++) {
                       String s = cSVRecord.get(i).get(c);
                        col.addElement(s!=null?s:"");
                    }
                }else{
                    for (int c = 0; c < xls.elementAt(0).size(); c++) {
                        try {
                            String s = cSVRecord.get(i).get(c);
                            col.addElement(s!=null?s:"");
                        } catch (Exception e) {
                            col.addElement("");
                        }                        
                    }
                }
                xls.addElement(col);
            } 
            if (xls.size()>=1) {
                hdr=xls.elementAt(0);
                xls.remove(0);
                return new Nikitaset(hdr, xls);
            }
            return new Nikitaset("Empty");
        } catch (Exception ex) { return new Nikitaset(ex.getMessage()); }
    }
}
