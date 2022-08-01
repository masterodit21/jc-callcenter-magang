/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator.action;

import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaService;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.storage.NikitaStorage;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rkrzmail
 */
public class StorageAction implements IAction{
    @Override
    public boolean OnAction(Nset currdata, NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        String code = response.getVirtualString(currdata.getData("code").toString()); //storage 
        String param1 = response.getVirtualString(currdata.getData("args").getData("param1").toString());//Storage
        String param2 = response.getVirtualString(currdata.getData("args").getData("param2").toString());//Name
        String param3 = response.getVirtualString(currdata.getData("args").getData("param3").toString());//Mode
        String param4 = (currdata.getData("args").getData("param4").toString());//Data/Result
        String param5 = response.getVirtualString(currdata.getData("args").getData("param5").toString());//Result file.exist;
        String param6 = response.getVirtualString(currdata.getData("args").getData("param6").toString());//Target Storage
        String param7 = response.getVirtualString(currdata.getData("args").getData("param7").toString());//Target Name
        
        
        if (code.equals("storage")) {                
            if (param3.equals("exist")||param3.equals("")) {
                boolean result = NikitaStorage.getStorage(param1).existStorage(param2);
                response.setVirtual(param4, result?"true":"false");
            }else if (param3.equals("read")) {
                String result = NikitaStorage.getStorage(param1).readStorage(param2);
                response.setVirtual(param4, result);
            }else if (param3.equals("readline")) {
                Nset result = NikitaStorage.getStorage(param1).readLineStorage(param2);
                response.setVirtual(param4, result);
            }  else if (param3.equalsIgnoreCase("readcsv")) {
                final Nikitaset result4 = NikitaStorage.getStorage(param1).readCsvStorage(param2, ";");
                response.setVirtual(param4, result4);
            } else if (param3.equalsIgnoreCase("readcsvcomma")) {
                final Nikitaset result4 = NikitaStorage.getStorage(param1).readCsvStorage(param2, ",");
                response.setVirtual(param4, result4);            
            }else if (param3.equals("readxls")) {
                Nikitaset result = NikitaStorage.getStorage(param1).readXlsStorage(param2);
                response.setVirtual(param4, result);
            }else if (param3.equals("readxlsx")) {
                Nikitaset result = NikitaStorage.getStorage(param1).readXlsxStorage(param2);
                response.setVirtual(param4, result);
            }else if (param3.equals("write")) {
                boolean result = NikitaStorage.getStorage(param1).writeStorage(param2, response.getVirtualString( param4) );
                response.setVirtual(param4, result?"true":"false");
            }else if (param3.equals("append")) {
                boolean result = NikitaStorage.getStorage(param1).appendStorage(param2,  response.getVirtualString( param4));
                response.setVirtual(param4, result?"true":"false");
            }else if (param3.equals("mkdir")) {                
                boolean result = NikitaStorage.getStorage(param1).makeStorage(param2);
                response.setVirtual(param4, result?"true":"false");
            }else if (param3.equals("create")) {
                boolean result = NikitaStorage.getStorage(param1).createStorage(param2);
                response.setVirtual(param4, result?"true":"false");
            }else if (param3.equals("copy")) {
                boolean result = NikitaStorage.getStorage(param1).copyStorage(param2, param6, param7);
                response.setVirtual(param4, result?"true":"false");
            }else if (param3.equals("cut")) {
                boolean result = NikitaStorage.getStorage(param1).cutStorage(param2, param6, param7);
                response.setVirtual(param4, result?"true":"false");
            }else if (param3.equals("delete")) {
                boolean result = NikitaStorage.getStorage(param1).deleteStorage(param2);
                response.setVirtual(param4, result?"true":"false");
            }
        }else if (code.equals("copy")) {
            
        }
        
        
        return true;
    }
    
}
