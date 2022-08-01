/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator.action;

import com.nikita.generator.Component;
import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;

/**
 *
 * @author rkrzmail
 */
public class JsonAction implements IAction{

    @Override
    public boolean OnAction(Nset currdata, NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
 
        String data = response.getVirtualString(currdata.getData("args").getData("param1").toString());  
        
        
        if (data.equals("read")) {
            String json = response.getVirtualString(  currdata.getData("args").getData("param2").toString()  );
            String comp = currdata.getData("args").getData("param3").toString();
            
            response.setVirtual(comp, Nset.readJSON(json));
        }else if (data.equals("nikitaset")) {
            
            String json = response.getVirtualString(  currdata.getData("args").getData("param2").toString()  );
            String comp = currdata.getData("args").getData("param3").toString();
            
            Nset n = Nset.readJSON(json);
            if (Nikitaset.isNikitaset(n)) {
                response.setVirtual(comp, new Nikitaset(n));
            }else{
                response.setVirtual(comp, n);
            } 
        }else if (data.equals("write")){

            Object json = response.getVirtual(  currdata.getData("args").getData("param2").toString()  );
            String comp = currdata.getData("args").getData("param3").toString();   
             
            if (json instanceof Nikitaset) {
                response.setVirtual(comp, ((Nikitaset)json).toNset().toJSON() ); 
            }else{
                response.setVirtual(comp, new Nset(json).toJSON() ); 
            }
        }else if (data.equals("writecsv")||data.equals("writecomma")){

            Object json = response.getVirtual(  currdata.getData("args").getData("param2").toString()  );
            String comp = currdata.getData("args").getData("param3").toString();   
             
            if (json instanceof Nikitaset) {
                response.setVirtual(comp, ((Nikitaset)json).toNset().toCsv()); 
            }else{
                response.setVirtual(comp, new Nset(json).toCsv() ); 
            }
        }else if (data.equals("split")){
             String json = response.getVirtualString(  currdata.getData("args").getData("param2").toString()  );
            String comp = currdata.getData("args").getData("param3").toString();
            
            response.setVirtual(comp, Nset.readsplitString(json));
        }else if (data.equals("comma")){
             String json = response.getVirtualString(  currdata.getData("args").getData("param2").toString()  );
            String comp = currdata.getData("args").getData("param3").toString();
            
            response.setVirtual(comp, Nset.readsplitString(json, ","));
        }else if (data.equals("semicolon")){
             String json = response.getVirtualString(  currdata.getData("args").getData("param2").toString()  );
            String comp = currdata.getData("args").getData("param3").toString();
            
            response.setVirtual(comp, Nset.readsplitString(json, ";"));
        }
        return true;
    }
}
