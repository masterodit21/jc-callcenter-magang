/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.servlet.mobile;

import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaService;
import com.nikita.generator.NikitaServlet;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.connection.NikitaInternet;
import com.nikita.generator.storage.NikitaStorage;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Vector;
import org.apache.http.HttpEntity;

/**
 *
 * @author rkrzmail
 */
public class mzharasyncfileclient extends NikitaServlet{
    
     public void OnRun(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        runZhara(request, response, logic, "init-syncfile-client", false, "", false);
    }
     
    public void runZhara(NikitaRequest request, NikitaResponse response, NikitaLogic logic, String conf, boolean deletefileaftersync, String downloadfolder, boolean sort) {
        String realPath = NikitaConnection.getDefaultPropertySetting().getData(conf).getData("pathfile").toString();
        Hashtable<String, String> args = new Hashtable<String, String>();
        String url = NikitaConnection.getDefaultPropertySetting().getData(conf).getData("urlserver").toString();
        String path = realPath;
        String client = NikitaConnection.getDefaultPropertySetting().getData(conf).getData("clientcode").toString();
        
        
        if (!new File(path+NikitaService.getFileSeparator()+"sync").isDirectory()) {
            new File(path+NikitaService.getFileSeparator()+"sync" ).mkdirs();            
        }
        if (!new File(path+(downloadfolder.equals("")?"":NikitaService.getFileSeparator())+downloadfolder).isDirectory()) {
            new File(path+(downloadfolder.equals("")?"":NikitaService.getFileSeparator())+downloadfolder ).mkdirs();            
        }
        
        //prepare file & upload
        File file = new File( path+NikitaService.getFileSeparator()+"sync" );
        File[] files = file.listFiles();
        Nset n = Nset.newObject();
        for (int i = 0; i < files.length; i++) {
            n.setData(files[i].getName(), "");
        } 
        
        file = new File( path );
        files = file.listFiles();
        
        if (sort) {
            //Sorted
            mzharasyncfileclient.sortZharaDBFile (files);
        }
 
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()||files[i].isHidden()) {                
            }else if (!n.containsKey(files[i].getName())) {
                //upload
                args = new Hashtable<String, String>();
                //args.put("syncmode", "upload");
                //args.put("client", client);  
                //args.put("filename", files[i].getName()); 
                try {
                    Nset result = Nset.readJSON(NikitaInternet.getString( NikitaInternet.multipartHttp(NikitaInternet.getUrlHttp(url, "syncmode=upload", "client="+client,"filename="+files[i].getName() ), args, files[i].getName(), new FileInputStream(files[i]) ) ));
                
                    //create file di sync
                    if (result.getData("status").toString().toUpperCase().equals("OK")) {
                        if (deletefileaftersync) {
                            files[i].delete();
                        }else{  }
                        new File(path+NikitaService.getFileSeparator()+"sync"+NikitaService.getFileSeparator()+files[i].getName()).createNewFile();
                    } else{
                        break;//stop
                    }                     
               } catch (Exception e) {  }
            }            
        }    
       
        
        //downloadview
        Nset array = Nset.newArray();
        args = new Hashtable<String, String>();
        args.put("syncmode", "downloadview");
        args.put("client", client);
     
        array = Nset.readJSON( NikitaInternet.getString(NikitaInternet.postHttp(url, args)) );          
        downloadfolder=downloadfolder!=null?downloadfolder:"";
        
        String[] dwfiles = new String[array.getArraySize()];        
        for (int i = 0; i < dwfiles.length; i++) {
            dwfiles[i] = array.getData(i).toString();            
        }
        if (sort) {
            sortZharaDBFile(dwfiles);
        }
        
        //dwonload  & delete       
        for (int i = 0; i < dwfiles.length; i++) {
            try {
                //dwonload  
                args = new Hashtable<String, String>();
                args.put("syncmode", "download");
                args.put("client", client);
                args.put("filename", dwfiles[i] );
                
                boolean dowloaded = false;
                HttpEntity entity =  NikitaInternet.postHttp(url, args).getEntity();
                InputStream is = entity .getContent();
                long l =  copyFile(is, path+ (downloadfolder.equals("")?"":NikitaService.getFileSeparator())+downloadfolder+NikitaService.getFileSeparator()+dwfiles[i] );
                if (entity.getContentLength()>=1) {
                    if (entity.getContentLength()==l) {
                        dowloaded=true;
                    }
                }else{
                    dowloaded=true;
                }
                if (dowloaded) {
                    //create file di sync
                    new File(path+NikitaService.getFileSeparator()+"sync"+NikitaService.getFileSeparator()+dwfiles[i] ).createNewFile();
                    //delete
                    args = new Hashtable<String, String>();
                    args.put("syncmode", "delete");
                    args.put("client", client);
                    args.put("filename", dwfiles[i] );
                    //args.put("size", is.available() );
                    NikitaInternet.postHttp(url, args); 
                }
            } catch (Exception e) {
                break;
            }         
        }
        
    }
    public static long copyFile(InputStream is, String destination) {
           try {
               OutputStream os = new FileOutputStream(destination);

               byte[] buffer = new byte[1024];
               int length; long l = 0;
               while ((length = is.read(buffer)) > 0) {
                   l=l+length;
                   os.write(buffer, 0, length);
               }
               os.flush();
               os.close();
               is.close();
               return l;
           } catch (Exception e) { return -1; }
   }
    public static void sortZharaDBFile(String[] files) {
        if (files!=null) {
            Arrays.sort(files, new Comparator<String>() {
                public int compare(String o1, String o2) {
                    long n1 = extractNumber(o1);
                    long n2 = extractNumber(o2);
                    return  Long.compare(n1, n2)  ;
                }
                private long extractNumber(String name) {
                    long i = 0;
                    try {
                        if (name.contains("-")) {
                            name = name.substring(0, name.lastIndexOf("-")) ;
                            if (name.contains("-")) {
                                //new model name
                                name=name.substring(name.indexOf("-")+1);
                            }else{
                                //last model name
                                if (name.contains("144")) {
                                    name=name.substring(name.indexOf("144"));
                                }
                            }                        
                        } 
                        return Long.parseLong(name);
                    } catch(Exception e) { }
                    return i;
                }
            });
             
        }
    }
    public static void sortZharaDBFile(File[] files) {
        if (files!=null) {
            Arrays.sort(files, new Comparator<File>() {
                public int compare(File o1, File o2) {
                    long n1 = extractNumber(o1.getName());
                    long n2 = extractNumber(o2.getName());
                    return  Long.compare(n1, n2)  ;
                }
                private long extractNumber(String name) {
                    long i = 0;
                    try {
                        if (name.contains("-")) {
                            name = name.substring(0, name.lastIndexOf("-")) ;
                            if (name.contains("-")) {
                                //new model name
                                name=name.substring(name.indexOf("-")+1);
                            }else{
                                //last model name
                                if (name.contains("144")) {
                                    name=name.substring(name.indexOf("144"));
                                }
                            }                        
                        } 
                        return Long.parseLong(name);
                    } catch(Exception e) { }
                    return i;
                }
            });
            /*
            System.out.println("Sorted");
            for(File f : files) {
                System.out.println(f.getName());
            }
            */
        }
    }
}
