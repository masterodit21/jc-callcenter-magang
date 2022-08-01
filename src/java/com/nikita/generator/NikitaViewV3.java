/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator;

import com.nikita.generator.connection.NikitaConnection;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 *
 * @author rkrzmail
 */
public class NikitaViewV3 {
    private Nset nikitaV3 = Nset.newObject() ;
    
    public static NikitaViewV3 create(){
        return new NikitaViewV3();
    }
    
    public void setData(String jsid, String id,  boolean visible, boolean enable, String text, String tag, String typeclass){
        nikitaV3.setData(jsid, Nset.newArray().addData(id).addData(visible).addData(enable).addData(text).addData(tag).addData(typeclass));//[id, visible, enable, text, tag, type, style, view]
    }
    
    public void setData(Component component){
        setData(component.getJsId(), component.getId(),component.isVisible(), component.isEnable(), component.getText(), component.getTag(), component.getClass().getSimpleName());
    }
    
    public void updateData(NikitaViewV3 cuV3,Component component){
         
    }
    
    private String fnikita = "nv3" ;
    public void setFormNikita(String fnikita){
        this.fnikita=fnikita;
    }
    public void setDataNikita(Nset data){
        this.nikitaV3=data;
    }
    public void save(String nikitaid){
         //System.out.println(nikitaid+" : "+fnikita); 
         //System.out.println(nikitaV3.toJSON());
         save(nikitaid, fnikita);
    }
    public void save(String nikitaid, String fnikita){
        if (fnikita.equals("") && false) {
            return;
        }
        try {
            File file= new File (NikitaService.getDirForm()+NikitaService.getFileSeparator()+nikitaid);
            if (file.isDirectory()) {                
            }else if (file.exists()) {
                file.delete();
                file.mkdirs();
            }else{
                file.mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(NikitaService.getDirForm()+NikitaService.getFileSeparator()+nikitaid+NikitaService.getFileSeparator()+fnikita);
            nikitaV3.toJSON(fos);

            fos.close();
        } catch (Exception e) { }
    }
    
    public static Nset read(String nikitaid, String fnikita){
        Nset result = Nset.newArray();
        try {
            String fileName = NikitaService.getDirForm()+NikitaService.getFileSeparator()+nikitaid+NikitaService.getFileSeparator()+fnikita ;
            File file= new File (fileName);
            if (file.exists()) {                                
                FileInputStream fis = new FileInputStream(fileName) ;
                result = Nset.readJSON(Utility.readFile(fis));
                fis.close();
            }
        } catch (Exception e) { }
        return result;
    }
}
