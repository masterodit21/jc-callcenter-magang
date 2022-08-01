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
public class mimagerotate extends NikitaServlet{
    public void OnRun(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        handleFileMultipart(request, response);
        
        super.OnRun(request, response, logic);
        
        response.writeStream(Nset.readJSON("{'status':'OK'}", true).toJSON());
    }
    
    public boolean handleFileMultipart(NikitaRequest nikitaRequest, NikitaResponse response) {
        boolean isMultipart = ServletFileUpload.isMultipartContent(nikitaRequest.getHttpServletRequest());
          String path = NikitaConnection.getDefaultPropertySetting().getData("init").getData("storage").toString()+ NikitaService.getFileSeparator() +  nikitaRequest.getParameter("imagename");  
                               
        return isMultipart;
    }
}
