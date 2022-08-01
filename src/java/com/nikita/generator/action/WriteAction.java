/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator.action;

import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;

/**
 *
 * @author rkrzmail
 */
public class WriteAction implements IAction{

    @Override
    public boolean OnAction(Nset currdata, NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        String code = response.getVirtualString(currdata.getData("code").toString());  
          
        if (code.equals("write")) {
            Object obj = response.getVirtual(currdata.getData("args").getData("message").toString());
            if (obj instanceof Nikitaset) {
                return writeStream(response, ((Nikitaset)obj).toNset().toJSON());
            }else if (obj instanceof Nset) {
                return writeStream(response, ((Nset)obj).toJSON());
            }else  if (obj != null) {
                return writeStream(response, obj.toString());
            }           
        }else if (code.equals("error")) {
            String message = response.getVirtualString(currdata.getData("args").getData("message").toString());
            return writeStream(response,  new Nikitaset(message).toNset().toJSON() );
        }else if (code.equals("header")) {
            String message = response.getVirtualString(currdata.getData("args").getData("hdrvalue").toString());
            String hdrname = response.getVirtualString(currdata.getData("args").getData("hdrname").toString());
            return writeStreamHeader(response, hdrname,  message);
        }else if (code.equals("flush")) {
            response.flush();
        }
        return true;
    }
    private static boolean writeStreamHeader(NikitaResponse response, String s, String val){
        boolean r = response.writeStreamHeader(s, val);
        //response.flush();        
        return r;
    }
    private static boolean writeStream(NikitaResponse response, String s){
        boolean r = response.writeStream(  s );
        //response.flush();        
        return r;
    }
}
