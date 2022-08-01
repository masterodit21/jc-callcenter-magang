/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.servlet;


import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaService;
import com.nikita.generator.NikitaServlet;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 *
 * @author dewabrata
 */
public class NikitaFileUpload extends NikitaServlet {
    
    public void OnRun(NikitaRequest nikitaRequest, NikitaResponse nikitaResponse, NikitaLogic logic) {
        HttpServletResponse response = nikitaResponse.getHttpServletResponse();     
        HttpServletRequest request = nikitaRequest.getHttpServletRequest();

        try {
            //application/x-www-form-urlencoded multipart/form-data text/plain
            
            boolean isMultipart = ServletFileUpload.isMultipartContent(request);
            // no multipart form
            if (isMultipart) {     
                // Create a new file upload handler
                ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
                // parse requests
                List<FileItem> fileItems = upload.parseRequest(request);
                

                if (fileItems!=null && fileItems.size() > 0) {
                    for (FileItem fileItem : fileItems) {             
                        if (fileItem.isFormField()) {
                           
                        }else{
                            // upload field
                            //if (fileItem.getFieldName().contains("image")) {
                                //String fileName = fileItem.getName().toString();
                                //fileName = new StringBuilder().append(fileName).toString();
                                File fileTo = new File("D://a"+System.currentTimeMillis()+".png");
                                fileItem.write(fileTo);
                                 
                                //NikitaStorage nikitaStorage = NikitaStorage.getStorage();
                                //nikitaStorage.writeStorage(fileName, fileItem.getInputStream());  
                                 NikitaService.getResourceFile(fileTo, request, response,"hi.png", false);
                                //nikitaResponse.submitDone("OK", Nset.newArray());
                                //nikitaResponse.write();
                            //}
                        }
                    }
                }
                
                
                
                //nikitaResponse.writeStream(Nset.newObject().setData("error", "").setData("status", "1").toString());
            }else{
                 nikitaResponse.writeStream(new Nikitaset("Need Multipart Data").toNset().toJSON());
            }    
        } catch (Exception ex) {
             nikitaResponse.writeStream(new Nikitaset(ex.getMessage()).toNset().toJSON());
        }
        //nikitaResponse.clearContainData();
        
    }
    
    
}
