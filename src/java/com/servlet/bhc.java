/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.servlet;

import com.naa.data.Nson;
import com.nikita.generator.Component;
import com.nikita.generator.NikitaControler;
import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaService;
import com.nikita.generator.NikitaServlet;
import com.nikita.generator.Style;
import com.nikita.generator.ui.Button;
import com.nikita.generator.ui.Combobox;
import com.nikita.generator.ui.DateTime;
import com.nikita.generator.ui.Label;
import com.nikita.generator.ui.Textarea;
import com.nikita.generator.ui.Textsmart;
import com.nikita.generator.ui.layout.HorizontalLayout;
import com.nikita.generator.ui.layout.NikitaForm;
import com.nikita.generator.ui.layout.VerticalLayout;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author rkrzmail
 */
public class bhc extends NikitaServlet{
    Nson mst = Nson.newObject();
    Nson bhcID = Nson.newObject();
    Nson odrID = Nson.newObject();
    List<Component> bhcComp = new ArrayList();
    
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        NikitaForm nf  = new NikitaForm(this);
        nf.setText("BHC");
        
        
        Label 
        lbl = new Label();
        lbl.setId("bhc-id");
        lbl.setVisible(false);
        lbl.setText(request.getParameter("bhc"));
        request.retainData(lbl);
        nf.addComponent(lbl);
        
        lbl = new Label();
        lbl.setId("bhc-order");
        lbl.setVisible(false);
        lbl.setText(request.getParameter("order"));
        request.retainData(lbl);
        nf.addComponent(lbl);
        
        lbl = new Label();
        lbl.setId("bhc-mst");
        lbl.setVisible(false);
        lbl.setText(request.getParameter("mst"));
        request.retainData(lbl);
        nf.addComponent(lbl);
        
        lbl = new Label();
        lbl.setId("bhc-index");
        lbl.setVisible(false);
        lbl.setText(request.getParameter("index"));
        request.retainData(lbl);
        nf.addComponent(lbl);
        
        bhcID = Nson.readJson( nf.findComponentbyId("bhc-id").getText());
        odrID = Nson.readJson( nf.findComponentbyId("bhc-order").getText()); 
        
        Nson nson = bhcID;
        Nson data = odrID; 
        
        
        String sIndex = lbl.getText();
        
        
        Combobox 
        combobox = new Combobox();
        combobox.setId("bhc-scall");
        combobox.setData( Nset.readJSON(nf.findComponentbyId("bhc-mst").getText()) );
        combobox.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                    
            }
        });
        
        
        combobox = new Combobox();
        combobox.setId("bhc-sresult");
        combobox.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                
            }
        });
        
        
        
        VerticalLayout hl = new VerticalLayout();
        for (int i = 0; i < nson.size(); i++) {
             if (nson.get(i).get("Field_id").asString().trim().equalsIgnoreCase(sIndex)
                         &&     (nson.get(i).get("PRODUCT").asString().equalsIgnoreCase(  "") ||
                                nson.get(i).get("PRODUCT").asString().equalsIgnoreCase(data.get("PRODUCT").asString() )
                                )
                            &&     (nson.get(i).get("CHAMPION").asString().equalsIgnoreCase( "" ) ||
                                nson.get(i).get("CHAMPION").asString().equalsIgnoreCase(data.get("CHAMPION").asString() )
                             )
                        ){
                 
                 
                    String type =  nson.get(i).get("Type_Tanya").asString().trim();
                    String text =  getDefault (data, nson.get(i).get("Isian").asString().trim());//default
                    String combo = getDefault (data, nson.get(i).get("Tanya").asString().trim() );
                    String label = nson.get(i).get("Tanya_detail").asString().trim();
                    String eval =  nson.get(i).get("Terlihat").asString().trim();
                    String name =  nson.get(i).get("Name_id").asString().trim();
                    //String expr =  nson.get(i).toJson();
                    
                    Component component = null; boolean enable = true;
                    if (type.equalsIgnoreCase("combo")){
                        Vector array = splitVectorTrim(combo, ",");
                        component = new Combobox();                      
                        component.setData(new Nset(array));                      
                      
                    }else if (type.equalsIgnoreCase("label")){
                        component = new Textsmart();        
                         
                        enable = false;//just lable
                    }else if (type.equalsIgnoreCase("text")){
                        component = new Textsmart();   
                    }else if (type.equalsIgnoreCase("area")){
                        component = new Textarea();   
                    }else if (type.equalsIgnoreCase("phone")){
                        component = new Textsmart(); 
                    }else if (type.equalsIgnoreCase("number")){
                        component = new Textsmart(); 
                        component.setStyle(Style.createStyle("n-char-accept:[0123456789];"));
                    }else if (type.equalsIgnoreCase("currency")){
                        component = new Textsmart(); 
                        component.setStyle(Style.createStyle("n-char-accept:[0123456789];n-currency-format:true"));
                    }else if (type.equalsIgnoreCase("decimal")){
                        component = new Textsmart(); 
                    }else if (type.equalsIgnoreCase("view")){
                        component = new Textsmart(); 
                        component.setEnable(false);
                    }else if (type.equalsIgnoreCase("title")){
                        component = new Label(); 
                    }else if (type.equalsIgnoreCase("button")){
                        component = new Button(); 
                        component.setText(label);label = "";
                        if (combo.equalsIgnoreCase("save")){
                                
                        }else if (combo.equalsIgnoreCase("calc")){
                            component.setOnClickListener(new Component.OnClickListener() {
                                @Override
                                public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                                    calc(request, response); 
                                }
                            });
                            
                        }else if (combo.equalsIgnoreCase("validasi")){

                        }else if (combo.startsWith("http://")||combo.startsWith("https://")){

                        }else{

                        }                        
                    }else if (type.equalsIgnoreCase("date")){
                        component = new DateTime(); 
                    }else if (type.equalsIgnoreCase("time")){
                        component = new Combobox();  
                        Vector<String> time = new Vector<String>();
                        for (int j = 6; j < 21; j++) {
                            for (int m = 0; m< 60; m=m+5) {
                                time.add(Utility.right("00"+(j), 2)+":"+Utility.right("00"+(m), 2));
                            }
                        }
                        component.setData(new Nset(time));   
            
                    }else{
                        component = new Component();
                    }
                    component.setId("bhc-"+i);
                    component.setVisible(true);
                    component.setEnable(enable);
                    component.setLabel(label);
                    //component.setTag(eval);
                    component.setName(name);
                    hl.addComponent(component);
                    bhcComp.add(component);//buffer
             }
            
        }  
        hl.setVisible(true);
                    hl.setEnable(true);
        nf.addComponent(hl);
        
        Button button = new Button();
        button.setText("SUMBIT");
        button.setId("bhc-submit");
        button.setStyle(new Style());
        button.getStyle().setStyle("margin-left", "5px");
        button.getStyle().setStyle("margin-right", "5px");
        
        button.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                 /*
                    Nson data = Nson.readJson(getIntentStringExtra("data"));
                    Nson bhc = getData();
                    Nson nson = getDefaultDataRaw();
                    nson.set("body", bhc.toJson());
                    nson.set("data", getIntentStringExtra("data"));
                    nson.set("ID", data.get("ID").asString());
                    nson.set("PRODUCT", data.get("PRODUCT").asString());
                    nson.set("CHAMPION", data.get("CHAMPION").asString());
                    nson.set("CARD_NUMBER", data.get("CARD_NUMBER").asString());
                    nson.set("CUSTOMER_NMBR", data.get("CUSTOMER_NMBR").asString());
                    nson.set("statuscall", getMaster( scall,"CALL"));
                    nson.set("statusapp", getMaster(sapp, "RESULT") );
                    nson.set("CALL_ID", data.get("CALL_ID").asString());

                    String remark = getHasilText(bhc,"REMARK");
                    if (remark.equalsIgnoreCase("")){
                        nson.set("remark", data.get("REMARK").asString());
                    }else{
                        nson.set("remark", remark);
                    }
                */
                //response.showDialog("Informasi", "Sudah Terkirim", "","OK");
                if (!validateMandatory(request, response)) {                   
                    response.showDialog("Informasi", "Semua pertanyaan harus diisi", "1", "OK");
                }else{
                  
                    Nset n = Nset.newObject();
                    Nson bhc = getData(request, response);
                    n.setData("body", Nset.readJSON(bhc.toJson()));
                    n.setData("remark", getHasilText(bhc,"REMARK"));
                    response.setResult("13", n);
                }     
                response.write();
            }
        });
        
        
        nf.addComponent(button); 
        
        response.setContent(nf);
    }
    private boolean validateMandatory(NikitaRequest request, NikitaResponse response){
         Nson nson = bhcID ;//Nson.readJson( response.getContent().findComponentbyId("bhc-id").getText());
         
          for (int c = 0; c < bhcComp.size(); c++) {
            Component v = bhcComp.get(c);     
            //Nson object = Nson.newObject();
            if (v!= null  ){  
                int i = Utility.getNumberOnlyInt(v.getId());
                String type =  nson.get(i).get("Type_Tanya").asString().trim();
                
                if (!type.equalsIgnoreCase("label")){
                    if (type.equalsIgnoreCase("combo")){                        
                        String text = v.getText();
                        if ( text.equalsIgnoreCase("")||text.equalsIgnoreCase("null")  ){
                            return false;
                        }
                    }else if (type.equalsIgnoreCase("date")){                    
                        String text =  Utility.formatDate(  Utility.getDate(  v.getText() )   , "yyyy-MM-dd");
                        if ( text.equalsIgnoreCase("")||text.equalsIgnoreCase("null")  ){
                            return false;
                        }
                    }else if (type.equalsIgnoreCase("time")){
                         String text = v.getText();
                        if ( text.equalsIgnoreCase("")||text.equalsIgnoreCase("null")  ){
                            return false;
                        }
                    }else if (type.equalsIgnoreCase("button")){

                    }else{
                        String text = v.getText();
                        if ( text.equalsIgnoreCase("")||text.equalsIgnoreCase("null")  ){
                            return false;
                        }
                    }
                }
            }       
        }
        return true;
    }
   private String getHasilText(Nson bhc,  String TanyaDetail){
        for (int i = 0; i < bhc.size(); i++) {
            if (bhc.get(i).get("TANYA_DETAIL").asString().equalsIgnoreCase(TanyaDetail)){
                if (!bhc.get(i).get("Hasil").asString().equalsIgnoreCase("") ){
                    return bhc.get(i).get("Hasil").asString();
                }
            }
        }
        return "";
    }
    private String getData(Nson data, String nameid ){
        
        for (int i = 0; i < data.size(); i++) {
                if (data.get(i).get("NAME_ID").asString().equalsIgnoreCase(nameid)){
                    return data.get(i).get("ISIAN").asString();
                }
        }
         
        return "";
    }    
    private Nson getData(NikitaRequest request, NikitaResponse response){
        Nson nson = bhcID;// Nson.readJson( response.getContent().findComponentbyId("bhc-id").getText());
        Nson data = odrID;// Nson.readJson( response.getContent().findComponentbyId("bhc-order").getText()); 
         
        Nson result = Nson.newArray();
        for (int c = 0; c < bhcComp.size(); c++) {
            Component v = bhcComp.get(c);     
            Nson object = Nson.newObject();
            if (v!= null  ){  
                int i = Utility.getNumberOnlyInt(v.getId());
                String type =  nson.get(i).get("Type_Tanya").asString();
                Nson param =  nson.get(i);

                if (param.isNsonObject()){
                    object.asObject().putAll(param.asObject());
                }

                //String.valueOf(textView.getText())
                if (type.equalsIgnoreCase("combo")){                
                    object.set("Hasil", v.getText().trim());
                }else if (type.equalsIgnoreCase("date")){                  
                    object.set("Hasil", Utility.formatDate(  Utility.getDate(  v.getText() )   , "yyyy-MM-dd") );
                }else if (type.equalsIgnoreCase("time")){
                    object.set("Hasil", v.getText().trim());
                }else if (type.equalsIgnoreCase("currency")){
                    object.set("Hasil",  Utility.getNumberOnly(v.getText()));
                }else{
                    object.set("Hasil", v.getText());
                }
                String seq = object.get("seq").asString();
                object.remove("seq");
                object.set("Seq", seq);

                object.set("ISIAN", object.get("Hasil"));//overide
                result.add(object);
            
            }
        }
         

        return result;
    }
     private void calc(final NikitaRequest request, final NikitaResponse response) {
        final Nson bhc = this.getData(request, response);
        final Nson nson = this.bhcID;
        final Nson data = this.odrID;
        String pin = this.getData(bhc, "PINJAMAN");
        final String ten = this.getData(bhc, "TENOR");
        final int tnr = (Utility.getNumberOnlyInt(ten) <= 0) ? 1 : Utility.getNumberOnlyInt(ten);
        pin = Utility.replace(pin, ",", "");
        String sint = data.get("New_Rate_" + tnr).asString().trim();
        String sadm = "0";
        if (data.containsKey("XCASH")) {
            final Nson ns = Nson.readNson(data.get("PLAN").asString());
            for (int i = 0; i < ns.size(); ++i) {
                if (ns.get(i).get("TENOR").asInteger() == tnr) {
                    sint = ns.get(i).get("RATE").asString();
                    sadm = ns.get(i).get("ADMIN_FEE").asString();
                    break;
                }
            }
        }
        double admin = Utility.getDouble((Object)sadm);
        double interest = Utility.getDouble((Object)sint);
        if (sint.endsWith("%")) {
            sint = Utility.replace(sint, "%", "");
            interest = Utility.getDouble((Object)sint) / 100.0;
        }
        if (sadm.endsWith("%")) {
            sadm = Utility.replace(sadm, "%", "");
            admin = Utility.getDouble((Object)sadm) / 100.0;
        }
        final double val = Utility.getLong(pin) * interest;
        final double adm = Utility.getLong(pin) * admin;
        final double angs = (Utility.getLong(pin) + Utility.getLong(pin) * interest * tnr) / tnr;
        this.setData(request, response, "BUNGA", Utility.formatCurrencyBulat(val));
        this.setData(request, response, "ADMIN", Utility.formatCurrencyBulat(adm));
        this.setData(request, response, "ANGSURAN", Utility.formatCurrencyBulat(angs));
        final double plf = Utility.getDouble((Object)pin);
        double max = Utility.getDouble((Object)data.get("TOP_UP_OFFER_" + tnr).asString().trim());
        if (data.containsKey("XCASH")) {
            //ELIGIBLE_ALOP": "Y"
            //if(data.get("ELIGIBLE_ALOP").asString().equalsIgnoreCase("Y")){
                                
            //}                    
            max = Utility.getDouble((Object)data.get("PLAFON").asString().trim());
            this.setData(request, response, "TOTAL_DUE", Utility.formatCurrencyBulat(angs * tnr));
            this.setData(request, response, "TOTAL_INTEREST", Utility.formatCurrencyBulat(val * tnr));
            this.setData(request, response, "BUNGA_PER", String.valueOf(interest));
            this.setData(request, response, "ADMIN_PER", String.valueOf(admin));
            final DecimalFormat df = new DecimalFormat("#.##");
            this.setData(request, response, "BUNGA_PERSEN", String.valueOf(df.format(interest * 100.0)) + "%");
            this.setData(request, response, "ADMIN_PERSEN", String.valueOf(df.format(admin * 100.0)) + "%");
        }
        if (data.get("ALAMAT").asString().contains("Staff Maybank")) {
            response.getContent().findComponentbyId("bhc-submit").setEnable(true);
        }
        else if (max == 0.0) {
            this.setData(request, response, "ANGSURAN", "Plafon Tidak diperbolehkan");
            response.getContent().findComponentbyId("bhc-submit").setEnable(false);
        }
        else if (plf > max) {
            this.setData(request, response, "ANGSURAN", "Angsuran Melebihi Plafon");
            response.getContent().findComponentbyId("bhc-submit").setEnable(false);
        }
        else {
            response.getContent().findComponentbyId("bhc-submit").setEnable(true);
        }
    }
    private void calc_( NikitaRequest request, NikitaResponse response){
        Nson bhc = getData(request, response);
       
        
        Nson nson = bhcID ; //Nson.readJson( response.getContent().findComponentbyId("bhc-id").getText());
        Nson data =odrID;//  Nson.readJson( response.getContent().findComponentbyId("bhc-order").getText()); 

      

        String pin = getData(bhc,"PINJAMAN");
        String ten = getData(bhc,"TENOR");
        int tnr =  (Utility.getNumberOnlyInt(ten)<=0?1:Utility.getNumberOnlyInt(ten));
      
        pin = Utility.replace(pin, ",", "");

        String sint = data.get("New_Rate_"+tnr).asString().trim();
        double interest = Utility.getDouble(sint);
        if (sint.endsWith("%")){
            sint= Utility.replace(sint,"%","");
            interest = Utility.getDouble(sint)/100;
        }
        double val = Utility.getLong(pin) * interest;
        double adm = 0;


        double angs = (Utility.getLong(pin) + (Utility.getLong(pin) * interest * tnr))/tnr ;
        setData(request, response,"BUNGA",Utility.formatCurrencyBulat(val));
        setData(request, response,"ADMIN",Utility.formatCurrencyBulat(adm));
        setData(request, response,"ANGSURAN",Utility.formatCurrencyBulat(angs));

        double plf =  Utility.getDouble(pin);
        double max =  Utility.getDouble(data.get("TOP_UP_OFFER_"+tnr).asString().trim());

        if (data.get("ALAMAT").asString().contains("Staff Maybank")) {
            //findViewById(R.id.tblSimpan).setEnabled(true);
            response.getContent().findComponentbyId("bhc-submit").setEnable(true);       
        } else if (max == 0 ){
            setData(request, response,"ANGSURAN", "Platform Tidak diperbolehkan");
            //findViewById(R.id.tblSimpan).setEnabled(false);
            response.getContent().findComponentbyId("bhc-submit").setEnable(false);       
        }else if (plf>max ){
            setData(request, response,"ANGSURAN", "Angsuran Melebihi Platform");
            //findViewById(R.id.tblSimpan).setEnabled(false);
            response.getContent().findComponentbyId("bhc-submit").setEnable(false);       
        }else{
            //setData(R.id.tblSimpan).setEnabled(true);  
            response.getContent().findComponentbyId("bhc-submit").setEnable(true);       
        }
    }
    private void setData(NikitaRequest request, NikitaResponse response, String name, String text  ){
        setData(request, response, name, text, false);
    }
     private void setData(NikitaRequest request, NikitaResponse response, String name, String text, boolean visible ){
        Nson nson = bhcID;// Nson.readJson( response.getContent().findComponentbyId("bhc-id").getText());
          
        
        
        for (int c = 0; c < bhcComp.size(); c++) {
            Component v = bhcComp.get(c);     
                     
            int i = Utility.getNumberOnlyInt(v.getId());         
            String type =  nson.get(i).get("Type_Tanya").asString();
            

            if (v!= null  && nson.get(i).get("NAME_ID").asString().equalsIgnoreCase(name)){
                if (type.equalsIgnoreCase("combo")){
                }else if (type.equalsIgnoreCase("date")){
                }else if (type.equalsIgnoreCase("time")){
                }else if (type.equalsIgnoreCase("button")){
                }else{
                    if (visible) {
                        v.setVisible(text.equalsIgnoreCase("true"));
                    }else{
                        v.setText(text);
                    }              
                    response.refreshComponent(v);
                    return;
                }
            }

        }
    }
     private String getDefault(Nson data, String s){
        if (s.startsWith("@")){
            s = s.substring(1);
            if (data.containsKey(s)){
                return data.get(s).asString();
            }
            return "";
        }
        return s;
    }
      public static Vector<String> splitVectorTrim(String original, String separator) {
          Vector<String> nodes = new Vector<String>();
        int index = original.indexOf(separator);
        while (index >= 0) {
            nodes.addElement(original.substring(0, index).trim());
            original = original.substring(index + separator.length());
            index = original.indexOf(separator);
        }
        nodes.addElement(original.trim());
        return nodes;
    }
}
