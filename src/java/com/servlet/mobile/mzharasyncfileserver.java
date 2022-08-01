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
import com.nikita.generator.storage.NikitaStorage;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Vector;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 *
 * @author rkrzmail
 */
public class mzharasyncfileserver extends NikitaServlet{

    public void OnRun(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        runZhara(request, response, logic, "init-syncfile-server");
    }
    
    public void runZhara(NikitaRequest request, NikitaResponse response, NikitaLogic logic, String conf) {
        String realPath = NikitaConnection.getDefaultPropertySetting().getData(conf).getData("pathfile").toString();
        String syncfileclinet = NikitaConnection.getDefaultPropertySetting().getData(conf).getData("clientscode").toString();
        
        if (request.getParameter("syncmode").equals("upload")) {
            String path = realPath;
            //save
            handleFileServer(request, path);            
            //buat queuq  ke setiap client           
            
            Vector<String> client = Utility.splitVector(syncfileclinet, ",");            
            for (int i = 0; i < client.size(); i++) {
                if (!new File(path+NikitaService.getFileSeparator()+client.elementAt(i)).isDirectory()) {
                    new File(path+NikitaService.getFileSeparator()+client.elementAt(i)).mkdirs();
                }
                if (!request.getParameter("client").equals(client.elementAt(i))) {
                    try {
                        new File(path+NikitaService.getFileSeparator()+client.elementAt(i)+NikitaService.getFileSeparator()+request.getParameter("filename") ).createNewFile();
                    } catch (Exception e) { }
                 }
            }  
            response.writeStream(Nset.newObject().setData("status", "OK").toJSON());
        }else if (request.getParameter("syncmode").equals("download")) {
            String path = realPath;
            //dwonload
            try {
                NikitaService.getResourceFile(new File(path+NikitaService.getFileSeparator()+request.getParameter("filename") ), request.getHttpServletRequest(), response.getHttpServletResponse(), request.getParameter("filename") , true);
            } catch (Exception e) { }
            //delete queue            
            if (request.getParameter("deletefilename").length()>=3) {
                File file = new File( path+NikitaService.getFileSeparator()+request.getParameter("client") );
                new File(path+NikitaService.getFileSeparator()+request.getParameter("client")+NikitaService.getFileSeparator()+request.getParameter("deletefilename") ).delete();
            }
        }else if (request.getParameter("syncmode").equals("delete")) {
            String path = realPath;
            //delete queue
            new File(path+NikitaService.getFileSeparator()+request.getParameter("client")+NikitaService.getFileSeparator()+request.getParameter("filename") ).delete();
            response.writeStream(Nset.newObject().setData("status", "OK").toJSON());
        }else if (request.getParameter("syncmode").equals("downloadview")) {
            //lihat queue untuk dia (folder dg kode cabang)
            String path = realPath;
            File file = new File( path+NikitaService.getFileSeparator()+request.getParameter("client") );
            File[] files = file.listFiles();
            Nset n = Nset.newArray();
            for (int i = 0; i < files.length; i++) {
                n.addData(files[i].getName());
            } 
            response.writeStream(n.toJSON());
        }        
    }
     public boolean handleFileServer(NikitaRequest nikitaRequest, String path) {
        boolean isMultipart = ServletFileUpload.isMultipartContent(nikitaRequest.getHttpServletRequest());
        if (isMultipart) {             
                HttpServletRequest request = nikitaRequest.getHttpServletRequest();
                try {
                    ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
                    // parse requests
                    List<FileItem> fileItems = upload.parseRequest(request);                    
                    String fsavename = nikitaRequest.getParameter("filename");                    
                    if (fileItems!=null && fileItems.size() > 0) {                        
                        for (FileItem fileItem : fileItems) {             
                            if (fileItem.isFormField()) {
                            }else{ 
                                File fileTo = new File(path+ NikitaService.getFileSeparator() +  fsavename );
                                fileItem.write(fileTo);    
                            }
                        }                        
                    }
                } catch (Exception ex) { }         
         
        }
        return isMultipart;
    }
    
}
