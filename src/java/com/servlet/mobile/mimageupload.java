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
import com.rkrzmail.nikita.data.Nset;
import java.io.File;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 *
 * @author rkrzmail
 */
public class mimageupload extends NikitaServlet{
    public void OnRun(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        handleFileMultipart(request, response);
        
        super.OnRun(request, response, logic);
        
        response.writeStream(Nset.readJSON("{'status':'OK'}", true).toJSON());
    }
    
    public boolean handleFileMultipart(NikitaRequest nikitaRequest, NikitaResponse response) {
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
                                nikitaRequest.setParameter(fileItem.getFieldName(), fileItem.getString());
                            }else{ 
                                String path = NikitaConnection.getDefaultPropertySetting().getData("init").getData("storage").toString()+ NikitaService.getFileSeparator() +  nikitaRequest.getParameter("imagename");  
                                 
                                File fileTo = new File(path);
                                fileItem.write(fileTo);     
                 
                                
                                //File fileTo = new File(NikitaService.getDirTmp()+ NikitaService.getFileSeparator() +  fsavename + ".tmp");
                                //fileItem.write(fileTo);    
                            }
                        } 
                        
                        
                    }
                } catch (Exception ex) { }         
         
        }
        return isMultipart;
    }
}
