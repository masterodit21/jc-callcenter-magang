/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator.action;

import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import java.util.Hashtable;
import java.util.Vector;
import org.omg.CORBA.INTERNAL;

/**
 *
 * @author rkrzmail
 */
public class VariableAction  implements IAction{

    @Override
    public boolean OnAction(Nset currdata, NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        String code = response.getVirtualString(currdata.getData("code").toString());  
        
        if (code.equals("var")) {
            //array, object
            
            
        }else if (code.equals("new")) {
            Nset n = Nset.newNull(); 
            String 
            var = currdata.getData("args").getData("param1").toString();  
            if (var.length()>=1) {
                n=Nset.newArray();
            }    
             
            if (currdata.getData("args").getData("param2").toString().length()>=1) {
                var = currdata.getData("args").getData("param2").toString(); 
                n=Nset.newObject();
            } 
           
            String varn = response.getVirtualString(currdata.getData("args").getData("param3").toString()).trim();  
            String fill = currdata.getData("args").getData("param4").toString();
            
             
            if (varn.equals("")||fill.equals("")) {                 
            }else if (Utility.isNumeric(varn)) {
                add(n, response.getVirtual(currdata.getData("args").getData("param4").toString()));  
            }else if (fill.length()>=1) {
                set(n, varn, response.getVirtual(currdata.getData("args").getData("param4").toString()));  
            }                
             
             
            response.setVirtual(var, n);
        }else if (code.equals("get")) {
            String 
            varn = currdata.getData("args").getData("param2").toString();  
            Nset n = new Nset(response.getVirtual(varn));
 
           
            //addition to read json 
            if (response.getVirtual(varn) instanceof String) {
                  
                String s = response.getVirtualString(varn) ;
                if (s.startsWith("[") && s.endsWith("]")) {
                    n = Nset.readJSON(s);
                }else if (s.startsWith("{") && s.endsWith("}")) {
                    n = Nset.readJSON(s);
                }
            }
 
             
            
            String m= currdata.getData("args").getData("param1").toString();
            if (m.equals("getbyname")) {
                varn =  response.getVirtualString (currdata.getData("args").getData("param3").toString()); 
                
                response.setVirtual( currdata.getData("args").getData("param4").toString(), n.getData( varn ));
                response.setVirtual( currdata.getData("args").getData("param5").toString(), n.getData( varn ).toString() );
                if (currdata.getData("args").getData("param6").toString().length()>=1) {
                    response.setVirtual( currdata.getData("args").getData("param6").toString(), n.getData( varn ).toJSON() );
                }  
                
            }else if (m.equals("getbyindex")) {
               
                varn = response.getVirtualString (currdata.getData("args").getData("param3").toString() );  
               
                response.setVirtual( currdata.getData("args").getData("param4").toString(), n.getData( Utility.getInt(varn) ));
                response.setVirtual( currdata.getData("args").getData("param5").toString(), n.getData( Utility.getInt(varn) ).toString() );
                if (currdata.getData("args").getData("param6").toString().length()>=1) {
                    response.setVirtual( currdata.getData("args").getData("param6").toString(), n.getData( Utility.getInt(varn) ).toJSON() );
                }                
               
            }else if (m.equals("getkeys")) {
               
                String[] keys = n.getObjectKeys();
                Nset r = Nset.newArray();
                for (int i = 0; i < keys.length; i++) {
                    r.addData(response.getVirtualString(keys[i]));
                }
                response.setVirtual( currdata.getData("args").getData("param4").toString(), r );
            }
            
        }else if (code.equals("set")) {
            
            String 
            varn = currdata.getData("args").getData("param1").toString(); 
            
            Nset n = new Nset(response.getVirtual(varn));
   
            
                if ((varn=response.getVirtualString(currdata.getData("args").getData("param2").toString())).length()>=1)  {
                    set(n, varn, response.getVirtual(currdata.getData("args").getData("param3").toString()));   
                }
                if ((varn=response.getVirtualString(currdata.getData("args").getData("param4").toString())).length()>=1)  
                    set(n, varn, response.getVirtual(currdata.getData("args").getData("param5").toString()));   
                
                if ((varn=response.getVirtualString(currdata.getData("args").getData("param6").toString())).length()>=1)  
                    set(n, varn, response.getVirtual(currdata.getData("args").getData("param7").toString()));   
                
                if ((varn=response.getVirtualString(currdata.getData("args").getData("param8").toString())).length()>=1)  
                    set(n, varn, response.getVirtual(currdata.getData("args").getData("param9").toString()));   
        }else if (code.equals("replace")) {    
            String 
            varn = currdata.getData("args").getData("param1").toString(); 
            
            Nset n = new Nset(response.getVirtual(varn));
   
            
                if ((varn=response.getVirtualString(currdata.getData("args").getData("param2").toString())).length()>=1)  {
                    replace(n, varn, response.getVirtual(currdata.getData("args").getData("param3").toString()));   
                }
                if ((varn=response.getVirtualString(currdata.getData("args").getData("param4").toString())).length()>=1)  
                    replace(n, varn, response.getVirtual(currdata.getData("args").getData("param5").toString()));   
                
                if ((varn=response.getVirtualString(currdata.getData("args").getData("param6").toString())).length()>=1)  
                    replace(n, varn, response.getVirtual(currdata.getData("args").getData("param7").toString()));   
                
                if ((varn=response.getVirtualString(currdata.getData("args").getData("param8").toString())).length()>=1)  
                    replace(n, varn, response.getVirtual(currdata.getData("args").getData("param9").toString()));   
        }else if (code.equals("add")) {
            String 
            varn = currdata.getData("args").getData("param1").toString();  
            Nset n = new Nset(response.getVirtual(varn));

            if ((varn=currdata.getData("args").getData("param2").toString()).length()>=1)  
                add(n, response.getVirtual(varn));            
            if ((varn=currdata.getData("args").getData("param3").toString()).length()>=1)  
                add(n, response.getVirtual(varn));            
            if ((varn=currdata.getData("args").getData("param4").toString()).length()>=1)  
                add(n, response.getVirtual(varn));            
            if ((varn=currdata.getData("args").getData("param5").toString()).length()>=1)  
                add(n, response.getVirtual(varn));
            if ((varn=currdata.getData("args").getData("param6").toString()).length()>=1)  
                add(n, response.getVirtual(varn));
            if ((varn=currdata.getData("args").getData("param7").toString()).length()>=1)  
                add(n, response.getVirtual(varn));
            if ((varn=currdata.getData("args").getData("param8").toString()).length()>=1)  
                add(n, response.getVirtual(varn));            
            if ((varn=currdata.getData("args").getData("param9").toString()).length()>=1)  
                add(n, response.getVirtual(varn));
            if ((varn=currdata.getData("args").getData("param10").toString()).length()>=1)  
                add(n, response.getVirtual(varn));
            
           
        }
        return true;
    }
    private void replace(Nset n, String  keyindex, Object obj){
        int index = Utility.getInt(keyindex);
        if (index==0 && !keyindex.equals("0")  ) {
            index=-1;            
        }
                
        if (index>=0 && index < n.getArraySize()) {
            try {
                ((Vector) n.getInternalObject()).set(index, obj);
            } catch (Exception e) {}
        }else{
            add(n, obj);
        }
    }
    private void add(Nset n, Object obj){
        if (obj instanceof Nset) {
            n.addData( (Nset)obj );
        }else if (obj instanceof String) {
            n.addData( (String)obj );
        }else if (obj instanceof Hashtable) {
            n.addData( (Hashtable)obj );
        }else if (obj instanceof Vector) {
            n.addData( (Vector)obj );
        }else if (obj instanceof Integer) {  
            n.addData( (Integer)obj );
        }else if (obj instanceof Double) {   
            n.addData( (Double)obj );
        }else if (obj instanceof Boolean) {
            n.addData( (Boolean)obj );
        }else   if (obj !=null ){
            n.addData( String.valueOf(obj) );
        }else{
            n.addData( Nset.newNull() );
        }
    }
    private void set(Nset n,String key, Object obj){
        if (obj instanceof Nset) {
            n.setData(key, (Nset)obj );
        }else if (obj instanceof String) {
            n.setData(key,(String)obj );
        }else if (obj instanceof Hashtable) {
            n.setData(key, (Hashtable)obj );
        }else if (obj instanceof Vector) {
            n.setData(key, (Vector)obj );
        }else if (obj instanceof Integer) {  
            n.setData(key, (Integer)obj );
        }else if (obj instanceof Double) {   
            n.setData(key, (Double)obj );
        }else if (obj instanceof Boolean) {
            n.setData(key,(Boolean)obj );
        }else if (obj !=null ) {
            n.setData(key, String.valueOf(obj) );
        }else{
            n.setData(key, Nset.newNull());
        }
    }
}
