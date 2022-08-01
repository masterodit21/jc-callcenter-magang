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
import java.io.File;

/**
 *
 * @author rkrzmail
 */
public class mdata extends NikitaServlet{
    
    
    public void OnRun(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
         File file = new File( "D:\\zahra\\sampledb"  );
        File[] files = file.listFiles();
        
        System.out.println("OnRun");
        for(File f : files) {
            System.out.println(f.getName());
        }
        mzharasyncfileclient.sortZharaDBFile (files);
        
        super.OnRun(request, response, logic); 
    }
    
}
