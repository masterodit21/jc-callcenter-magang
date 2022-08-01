/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator.storage;

import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaService;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.connection.NikitaInternet;
import com.rkrzmail.nikita.data.NikitaSpreadsheet;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.data.Nson;
import com.rkrzmail.nikita.utility.Utility;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
/**
 * created by 13k.mail@gmail.com
 */
public class NikitaStorage {
    private String error =""; 
    private String storage ="";
    private String storagename = "";
    private String storageextention = "";
     
    private static final int STORAGE_ERROR = 9;
    private static final int STORAGE_CONNECTION_ERROR = 8;
    private static final int STORAGE_OK = 99;    
    
    private static final int STORAGE_EXIST = 1;    
    private static final int STORAGE_NOT_FOUND = 0;
   
    private static final String ERROR_MSG_CONNECTION_ERROR = "Connection Timeout";
    
    public NikitaStorage(String strname){
        storagename  = strname ;
        
        if (strname.equals("temp")||strname.equals("tmp")) {
            storage = NikitaService.getDirTmp();  
        }else if (strname.equals("asset")) {
            storage =    NikitaConnection.getDefaultPropertySetting().getData("init").getData("asset").toString()  ;
        }else if (strname.equals("storage")) {
            storage =    NikitaConnection.getDefaultPropertySetting().getData("init").getData("storage").toString()  ;
        }else if (strname.equals("document")) {
            storage =    NikitaConnection.getDefaultPropertySetting().getData("init").getData("document").toString()  ;
        }else if (strname.equals("native")) {
            storage =   ""   ;
        }else if (strname.equals("online")) {    
            storage =   NikitaConnection.getDefaultPropertySetting().getData("init").getData("resource").toString()  ;
        } else{
            storage =   NikitaConnection.getDefaultPropertySetting().getData("init").getData("resource").toString()  ;
        }
    }
        
    
    public static NikitaStorage getStorage(String storage){ 
        return new NikitaStorage(  storage  );
    }
 
    public static NikitaStorage getTemporary(){        
        return new NikitaStorage(  "temp"  ) ;
    }
    
    private String getLocalFileName(String path, String fname){
         
        if (path.endsWith(getFileSeparator()) && fname.startsWith(getFileSeparator()) ) {            
            return path + fname.substring(1)+storageextention;
        }else if (   path.endsWith(getFileSeparator())  ) {  
            return path + fname+storageextention; 
        }else if (  fname.startsWith(getFileSeparator())  ) {  
            return path + fname+storageextention; 
        }else if (  path.equals("")  ) {  
            return  fname+storageextention;
        }
        return path + getFileSeparator() + fname+storageextention;
    }
    
    public boolean copyStorage(String filename, String targetstorage, String targetname)  {  
        error = ""; 
        try {
            InputStream inputStream = openInputStream(filename);
             
            NikitaStorage target =NikitaStorage.getStorage(targetstorage);
            if (target.isOnline()) {                
                int ifile = target.saveOnline(filename, inputStream, false);
                if (ifile!=STORAGE_OK) {
                    error = ERROR_MSG_CONNECTION_ERROR;
                }
                return ifile==STORAGE_OK?true:false;
            }else{
                OutputStream os = target.openLocalOutputStream(targetname);
                Utility.CopyStream(inputStream, os);
                os.flush(); os.close();
            }   
            inputStream.close();
        } catch (Exception e) { error =e.getMessage();}
        return false;                 
    } 
    public boolean cutStorage(String filename, String targetstorage, String targetname)  {
        boolean copy = copyStorage(filename, targetstorage, targetname) ;
        boolean delete  = deleteStorage(filename);    
        return copy && delete;                 
    } 
    public boolean deleteStorage(String filename)  {  
        error = ""; 
        if (isOnline()) {
            int ifile = deleteOnline(filename);
            if (ifile!=STORAGE_OK) {
                error = ERROR_MSG_CONNECTION_ERROR;
            }
            return ifile==STORAGE_OK?true:false;
        }else  {
            return new File(getLocalFileName(storage, filename) ).delete();
        }        
             
    } 
    public boolean createStorage(String filename)  {  
        error = ""; 
        if (isOnline()) {
            int ifile = createOnlineFile(filename);
            if (ifile!=STORAGE_OK) {
                error = ERROR_MSG_CONNECTION_ERROR;
            }
            return ifile==STORAGE_OK?true:false;
        }else  {
            try {
                new File(getLocalFileName(storage, filename) ).delete();
                return new File(getLocalFileName(storage, filename) ).createNewFile();
            } catch (IOException ex) {
                error =ex.getMessage();
                return false;
            }
        }  
    }
    public boolean makeStorage(String filename)  {  
        error = ""; 
        if (isOnline()) {
            int ifile = makeOnlineDirectory(filename);
            if (ifile!=STORAGE_OK) {
                error = ERROR_MSG_CONNECTION_ERROR;
            }
            return ifile==STORAGE_OK?true:false;
        }else  {

            return new File(getLocalFileName(storage, filename) ).mkdirs();
        }
    }
    public Nset dirStorage(String filename)  {  
         error = ""; 
        if (isOnline()) {            
            return dirOnlineDirectory(filename).getData("files");
        }else  {
           
            
            File file = new File(getLocalFileName(storage, filename) );
            File[] files = file.listFiles();
            Nset n = Nset.newArray();
            for (int i = 0; i < files.length; i++) {
                Nset f = Nset.newObject();
                //BasicFileAttributes attr = Files.readAttributes("", BasicFileAttributes.class);                
                f.setData("name", files[i].getName());
                f.setData("type", files[i].isDirectory() ? "dir" :"file");
                try { f.setData("size", files[i].length());  } catch (Exception e) { }
                n.addData(f);
            }
            return n;
        }
    }
     public String readStorage(String filenam)  {  
        error = ""; 
        try {
            InputStream inputStream = openInputStream(filenam);
            ByteArrayOutputStream baos =new ByteArrayOutputStream();           
            byte[] bytes = new byte[1024];int count;
            while ( (count = inputStream.read(bytes, 0, 1024))!= -1 ) {                
                baos.write(bytes, 0, count);
            }       
            inputStream.close();
            return new String(baos.toByteArray());
        } catch (Exception e) { error =e.getMessage();}
        return "";                 
    } 
     public Nset readLineStorage(String filenam)  {  
        error = ""; Nset result =Nset.newArray();
        try {
            InputStream inputStream = openInputStream(filenam);
            BufferedReader br = new BufferedReader( new InputStreamReader(inputStream, Charset.forName("UTF-8")) );
            String line; 
            while ((line = br.readLine()) != null) {
                result.addData(line);
            }
            inputStream.close();
        } catch (Exception e) { error =e.getMessage();}        
        
        return result;
    } 
    public Nikitaset readXlsStorage(String filenam)  {  
        error = ""; 
        try {
            InputStream inputStream = openInputStream(filenam);
            return NikitaSpreadsheet.readToNikitaset(inputStream);
        } catch (Exception e) { error =e.getMessage();}
         return new Nikitaset(error);
    } 
    public Nikitaset readXlsxStorage(String filenam)  { 
        return readXlsxStorage(filenam, Nson.newObject() );
    }
    public Nikitaset readXlsxStorage(String filenam, Nson setting)  {  
        error = ""; 
        try {
            InputStream inputStream = openInputStream(filenam);
            Nikitaset nikitaset = NikitaSpreadsheet.readxToNikitaset(inputStream, false, setting);
            if (nikitaset.getError().equalsIgnoreCase("")) {
                //no error
            }else{
                inputStream.close();
                inputStream = openInputStream(filenam);
                nikitaset = NikitaSpreadsheet.readxToNikitaset(inputStream, true, setting);
            }                
            return  nikitaset;
        } catch (Exception e) { 
            error =e.getMessage();        
        }
        return new Nikitaset(error);
    } 
    public boolean appendStorage(String filename, String data)  {  
        return  appendStorage(filename, new ByteArrayInputStream(data.getBytes()));
    }
    public boolean appendStorage(String filename, InputStream data)  {  
        error = ""; 
        try {            
            if (isOnline()) {
                int ifile = saveOnline(filename, data, true);
                if (ifile!=STORAGE_OK) {
                    error = ERROR_MSG_CONNECTION_ERROR;
                }
                return ifile==STORAGE_OK?true:false;
            }else{
                FileWriter os = openLocalFileWriter(filename);
                byte[] bytes = new byte[1024];int count;ByteArrayOutputStream baos = new ByteArrayOutputStream();
                while ( (count = data.read(bytes, 0, 1024))!= -1 ) {                
                    baos.write(bytes, 0, count);
                }        
                os.append(new String(baos.toByteArray()));
                os.flush(); os.close();
            }            
        } catch (Exception e) { error =e.getMessage();}

        return false;                 
    } 
    public boolean writeStorage(String filename, String data)  {  
        return writeStorage(filename, new ByteArrayInputStream(data.getBytes()));
    }
    public boolean writeStorage(String filename, InputStream data)  {  
        error = "";  
        try {            
            if (isOnline()) {        
                int ifile = saveOnline(filename, data, false);
                if (ifile!=STORAGE_OK) {
                    error = ERROR_MSG_CONNECTION_ERROR;
                }
                return ifile==STORAGE_OK?true:false;
            }else{
                OutputStream os = openLocalOutputStream(filename);
                byte[] bytes = new byte[1024];int count;
                while ( (count = data.read(bytes, 0, 1024))!= -1 ) {                
                    os.write(bytes, 0, count);
                }       
                os.flush(); os.close();
            }            
        } catch (Exception e) { error =e.getMessage();}
        return false;  
    } 
     public boolean existStorage(String filename)  {  
        error = ""; 
        if (isOnline()) {
            int ifile = existsOnline(filename);
            if (ifile==STORAGE_CONNECTION_ERROR) {
                error = ERROR_MSG_CONNECTION_ERROR;
            }
            return ifile==STORAGE_EXIST?true:false;
        }else  {
            return new File(getLocalFileName(storage, filename) ).exists() ;
        }              
    }
    
    public InputStream openInputStream(String filename) throws FileNotFoundException, IOException{  
        if (isOnline()) {
            return openInputStreamOnline(filename);
        }else  {
            return new FileInputStream(getLocalFileName(storage, filename) );
        }                        
    } 
    public OutputStream openLocalOutputStream(String filename) throws FileNotFoundException  {  
        return new FileOutputStream (getLocalFileName(storage, filename) );
    } 
    public FileWriter openLocalFileWriter(String filename) throws IOException   {  
        return  new FileWriter(getLocalFileName(storage, filename) ,true);
    } 
    public int saveOnline(String filename, InputStream inputStream, boolean append){  
        if (isOnline()) {
            Hashtable args = new Hashtable();
            args.put("image", filename);
            args.put("storagename", storagename);
            args.put("storagemode", append ?"write":"append");  
 
            args.put("storageauth", NikitaConnection.getDefaultPropertySetting().getData("init").getData("storageauthclient").toString());            
            try {
                Nset n = NikitaInternet.getNset(NikitaInternet.multipartHttp(storage, args , filename , inputStream)) ;
                if (n.containsKey("status") && n.getData("status").toString().equalsIgnoreCase("true")) {
                     return STORAGE_OK;
                }
            } catch (Exception e) { return  STORAGE_CONNECTION_ERROR ;}            
        }
        return STORAGE_CONNECTION_ERROR;    
    }
    public String getErorMessage(){
         return error;
    } 
    
    public boolean isOnline(){
         return storage.startsWith("http");
    }
    public String getFileSeparator(){
         return NikitaService.getFileSeparator();
    }
    private int deleteOnline(String filename) { 
        try {
            Nset n = NikitaInternet.getNset( NikitaInternet.postHttp(storage, "storagename="+storagename ,"image="+filename , "storagemode=delete" , "storageauth="+NikitaConnection.getDefaultPropertySetting().getData("init").getData("storageauthclient").toString()));
            if (n.containsKey("status") && n.getData("status").toString().equalsIgnoreCase("true")) {
                 return STORAGE_OK;
            }
        } catch (Exception e) { return  STORAGE_CONNECTION_ERROR ;}
        return STORAGE_CONNECTION_ERROR;    
    } 
    private int existsOnline(String filename) { 
        try {
            Nset n = NikitaInternet.getNset( NikitaInternet.postHttp(storage, "storagename="+storagename ,"image="+filename , "storagemode=exists", "storageauth="+NikitaConnection.getDefaultPropertySetting().getData("init").getData("storageauthclient").toString()));
            if (n.containsKey("status") && n.getData("status").toString().equalsIgnoreCase("exists")) {
                return STORAGE_EXIST;
            }else if (n.containsKey("status") ) {
                return STORAGE_NOT_FOUND;   
            }
        } catch (Exception e) { return  STORAGE_CONNECTION_ERROR ;}
        return STORAGE_CONNECTION_ERROR;    
    } 
    private InputStream openInputStreamOnline(String filename) throws IOException { 
         return NikitaInternet.postHttp(storage, "storagename="+storagename ,"image="+filename, "storagemode=open", "storageauth="+NikitaConnection.getDefaultPropertySetting().getData("init").getData("storageauthclient").toString()).getEntity().getContent() ;
    }  
    private int createOnlineFile(String filename)  { 
        try {
            Nset n = NikitaInternet.getNset( NikitaInternet.postHttp(storage, "storagename="+storagename ,"image="+filename , "storagemode=create" , "storageauth="+NikitaConnection.getDefaultPropertySetting().getData("init").getData("storageauthclient").toString()));
            if (n.containsKey("status") && n.getData("status").toString().equalsIgnoreCase("true")) {
                 return STORAGE_OK;
            }
        } catch (Exception e) { return  STORAGE_CONNECTION_ERROR ;}
        return STORAGE_CONNECTION_ERROR;  
    }  
    private int makeOnlineDirectory(String filename)  { 
        try {
            Nset n = NikitaInternet.getNset( NikitaInternet.postHttp(storage,  "storagename="+storagename , "image="+filename , "storagemode=mkdir" , "storageauth="+NikitaConnection.getDefaultPropertySetting().getData("init").getData("storageauthclient").toString()));
            if (n.containsKey("status") && n.getData("status").toString().equalsIgnoreCase("true")) {
                 return STORAGE_OK;
            }
        } catch (Exception e) { return  STORAGE_CONNECTION_ERROR ;}
        return STORAGE_CONNECTION_ERROR; 
    }  
    private Nset dirOnlineDirectory(String filename)  { 
        try {
            Nset n =  NikitaInternet.getNset( NikitaInternet.postHttp(storage,  "storagename="+storagename , "image="+filename , "storagemode=dir" , "storageauth="+NikitaConnection.getDefaultPropertySetting().getData("init").getData("storageauthclient").toString()));
            if (n.containsKey("status") && n.getData("status").toString().equalsIgnoreCase("true")) {
            }else{
                 error = n.getData("error").toString();
            }
            return n;
        } catch (Exception e) { 
            error =e.getMessage();
            return Nset.newObject();
        }
    } 
    public static void handleNikitaStorge(NikitaRequest nikitaRequest, NikitaResponse nikitaResponse) {
        Nset result = Nset.newObject();
        NikitaStorage storage = new NikitaStorage(nikitaRequest.getParameter("storagename"));
        if (nikitaRequest.getParameter("storagemode").equals("exists")) {
            storage.existStorage( nikitaRequest.getParameter("image") );
            if (storage.getErorMessage().equals("")) {
                result.setData("status", "exists");
            }else{
                result.setData("status", "").setData("error", storage.getErorMessage());
            }            
        }else if (nikitaRequest.getParameter("storagemode").equals("open")) {
            try {
                NikitaService.getResourceStream(storage.openInputStream( nikitaRequest.getParameter("image")  ), nikitaRequest.getHttpServletRequest(), nikitaResponse.getHttpServletResponse(), nikitaRequest.getParameter("image"), true );
            } catch (IOException ex) { }
            return;
        }else if (nikitaRequest.getParameter("storagemode").equals("mkdir")) {       
            storage.makeStorage(nikitaRequest.getParameter("image") );
            if (storage.getErorMessage().equals("")) {
                result.setData("status", "true");
            }else{
                result.setData("status", "").setData("error", storage.getErorMessage());
            }  
        }else if (nikitaRequest.getParameter("storagemode").equals("create")) {
            storage.createStorage(nikitaRequest.getParameter("image") );
            if (storage.getErorMessage().equals("")) {
                result.setData("status", "true");
            }else{
                result.setData("status", "").setData("error", storage.getErorMessage());
            }  
        }else if (nikitaRequest.getParameter("storagemode").equals("delete")) {
            storage.deleteStorage( nikitaRequest.getParameter("image") );
            if (storage.getErorMessage().equals("")) {
                result.setData("status", "true");
            }else{
                result.setData("status", "").setData("error", storage.getErorMessage());
            }  
        }else if (nikitaRequest.getParameter("storagemode").equals("write")) {
            storage.writeStorage( nikitaRequest.getParameter("image") , storage.handleSingleMultipart(nikitaRequest)  );
            if (storage.getErorMessage().equals("")) {
                result.setData("status", "true");
            }else{
                result.setData("status", "").setData("error", storage.getErorMessage());
            }  
        }else if (nikitaRequest.getParameter("storagemode").equals("append")) {
            storage.appendStorage( nikitaRequest.getParameter("image") , storage.handleSingleMultipart(nikitaRequest) );
            if (storage.getErorMessage().equals("")) {
                result.setData("status", "true");
            }else{
                result.setData("status", "").setData("error", storage.getErorMessage());
            }  
        }else if (nikitaRequest.getParameter("storagemode").equals("dir")) {
            Nset n = storage.dirStorage(nikitaRequest.getParameter("image"));//return files(nsetarray) only
            if (storage.getErorMessage().equals("")) {
                result.setData("status", "true").setData("files", n);
            }else{
                result.setData("status", "").setData("error", storage.getErorMessage());
            }  
        }else{
            result.setData("status", "none");
        }        
        nikitaResponse.writeStream(result.toJSON());        
    }
    
    private InputStream handleSingleMultipart(NikitaRequest nikitaRequest) {
        boolean isMultipart = ServletFileUpload.isMultipartContent(nikitaRequest.getHttpServletRequest());
        if (isMultipart) {  
                HttpServletRequest request = nikitaRequest.getHttpServletRequest();
                try {
                    ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
                    // parse requests
                    List<FileItem> fileItems = upload.parseRequest(request);
                 
                    if (fileItems!=null && fileItems.size() > 0) {                        
                        for (FileItem fileItem : fileItems) {             
                            if (fileItem.isFormField()) {
                            }else{
                                return fileItem.getInputStream();                               
                            }
                        }  
                    }
                } catch (Exception ex) { }         
         
        }
        return null;
    }
    
    public Nikitaset readCsvStorage(final String filenam, final String delimiter) {
        this.error = "";
        final Vector<String> header = new Vector<String>();
        final Vector<Vector<String>> data = new Vector<Vector<String>>();
        try {
            final InputStream inputStream = this.openInputStream(filenam);
            final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            String line;
            while ((line = br.readLine()) != null) {
                final Vector<String> dt = (Vector<String>)Utility.splitVector(line, delimiter);
                if (header.isEmpty()) {
                    for (int i = 0; i < dt.size(); ++i) {
                        header.add(dt.elementAt(i).trim());
                    }
                }
                else {
                    final Vector<String> da = new Vector<String>();
                    for (int j = 0; j < Math.min(dt.size(), header.size()); ++j) {
                        da.add(dt.elementAt(j));
                    }
                    for (int j = da.size(); j < header.size(); ++j) {
                        da.add("");
                    }
                    if (dt.size() < 1) {
                        break;
                    }
                    if (da.elementAt(0).trim().equalsIgnoreCase("")) {
                        break;
                    }
                    data.add(da);
                }
            }
            inputStream.close();
        }
        catch (Exception e) {
            this.error = e.getMessage();
        }
        return new Nikitaset((Vector)header, (Vector)data);
    }
    
}
