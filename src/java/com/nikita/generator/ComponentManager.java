/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator;
/**
 * created by 13k.mail@gmail.com
 */
import com.nikita.generator.ui.Accordion;
import com.nikita.generator.ui.Button;
import com.nikita.generator.ui.Checkbox;
import com.nikita.generator.ui.Collapsible;
import com.nikita.generator.ui.Combobox;
import com.nikita.generator.ui.Combolist;
import com.nikita.generator.ui.Content;
import com.nikita.generator.ui.DateTime;
import com.nikita.generator.ui.DateTimeOnly;
import com.nikita.generator.ui.Document;
import com.nikita.generator.ui.FileUploder;
import com.nikita.generator.ui.Function;
import com.nikita.generator.ui.Image;
import com.nikita.generator.ui.Label;
import com.nikita.generator.ui.ListView;
import com.nikita.generator.ui.MapView;
import com.nikita.generator.ui.Radiobox;
import com.nikita.generator.ui.Receiver;
import com.nikita.generator.ui.SmartGrid;
import com.nikita.generator.ui.Tablegrid;
import com.nikita.generator.ui.TextAutoComplete;
import com.nikita.generator.ui.Textarea;
import com.nikita.generator.ui.Textbox;
import com.nikita.generator.ui.Textsmart;
import com.nikita.generator.ui.Webview;
import com.nikita.generator.ui.layout.BorderLayout;
import com.nikita.generator.ui.layout.DivLayout;
import com.nikita.generator.ui.layout.FrameLayout;
import com.nikita.generator.ui.layout.GridLayout;
import com.nikita.generator.ui.layout.HorizontalLayout;
import com.nikita.generator.ui.layout.NikitaForm;
import com.nikita.generator.ui.layout.TabLayout;
import com.nikita.generator.ui.layout.VerticalLayout;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;

/**
 *
 * @author rkrzmail
 */
public class ComponentManager {
    public static String parseComponentStyle(Component component, String style){        
        style = Utility.replace(style, "@+COMPONENT-ID", component.getId());
        style = Utility.replace(style, "@+COMPONENT-TAG", component.getTag());
        style = Utility.replace(style, "@+COMPONENT-NAME", component.getName());
        style = Utility.replace(style, "@+COMPONENT-TEXT", component.getText());     
        style = Utility.replace(style, "@+COMPONENT-LABEL", component.getLabel());   
        style = Utility.replace(style, "@+COMPONENT-VISIBLE", component.isVisible()?"1":"0");
        style = Utility.replace(style, "@+COMPONENT-ENABLE", component.isEnable()?"1":"0");
        style = Utility.replace(style, "@+COMPONENT-JSID", component.getJsId());
        style = Utility.replace(style, "@+COMPONENT-FORM-ID", component.getFormId());
        style = Utility.replace(style, "@+COMPONENT-FORM-JSID", component.getFormJsId());     
        
        style = Utility.replace(style, "/@+CONTEXT/", NikitaService.getBaseContext()+"/");
        style = Utility.replace(style, "@+COMPONENT-LOGIC", component.getClickAction());   
        return style;
    }
    public static Component createComponent(Nset data, int row){
        return new Component();
    }
        
    public static Component createComponent(Nikitaset ns, int row){
        Component comp = createComponent(ns.getText(row, "comptype"), ns.getText(row, "formid"));
        
        if (Application.componentAdapter!=null) {
            Component v = Application.componentAdapter.onCreateComponent(comp, ns, row);
            comp = v!=null?v:comp; 
        }
        
        if (comp != null) {
            //comp.setFormId(ns.getText(row, "formid")); Not used
            comp.setId(ns.getText(row, "compid"));
            comp.setName(ns.getText(row, "compname"));
            comp.setLabel(ns.getText(row, "complabel"));
            comp.setText(ns.getText(row, "comptext"));
            comp.setHint(ns.getText(row, "comphint"));            
            comp.setParentName(ns.getText(row, "parent"));
            if (ns.getText(row, "visible").equals("1")) {
                comp.setVisible(true);
            }else {
                comp.setVisible(false);
            }
            if (ns.getText(row, "enable").equals("1")) {
                comp.setEnable(true);
            }else {
                comp.setEnable(false);
            }
            if (ns.getText(row, "mandatory").equals("1")) {
                comp.setMandatory(true);
            }else {
                comp.setMandatory(false);
            }
            
            comp.setValidation(ns.getText(row, "validation"));
        }        
        return comp != null ? comp:new Component();
    }
    public static Component createComponent(String typecomp, String nformid){
        Component comp = null;
       
        if (typecomp.equals("button")) {
            comp = new Button();  
        }else if (typecomp.equals("label")) {
            comp = new Label();
        }else if (typecomp.equals("txt")) {
            comp = new Textsmart();
         }else if (typecomp.equals("checkbox")) {
            comp = new Checkbox();
        }else if (typecomp.equals("radiobox")) {
            comp =  new Radiobox();
        }else if (typecomp.equals("collapsible")) {
            comp = new Collapsible();
        }else if (typecomp.equals("listview")) {
            comp = new ListView();
        }else if (typecomp.equals("combobox")) {
            comp = new Combobox();
         }else if (typecomp.equals("datetime")) {
            DateTime combo = new DateTime();
            comp = combo;
         
         }else if (typecomp.equals("time")) {
            comp = new DateTimeOnly();
        }else if (typecomp.equals("combolist")) {
            comp = new Combolist();
        }else if (typecomp.equals("formlayout")) {
            comp = new NikitaForm(nformid);        
        }else if (typecomp.equals("framelayout")) {
            DivLayout framLay = new DivLayout();
            comp = framLay;          
        }else if (typecomp.equals("verticallayout")) {
            comp = new VerticalLayout();
        }else if (typecomp.equals("horizontallayout")) {
            comp = new HorizontalLayout();
         }else if (typecomp.equals("gridlayout")) {
            comp = new GridLayout();
        }else if (typecomp.equals("image")) {
            Image img = new Image();
            comp = img;
        }else if (typecomp.equals("area")) {
            comp = new Textarea();
        }else if (typecomp.equals("tablegrid")) {
            comp = new Tablegrid();
        }else if (typecomp.equals("smartgrid")) {
            comp = new SmartGrid();
        }else if (typecomp.equals("function")) {
            comp = new Function();
        }else if (typecomp.equals("borderlayout")) {
            comp = new BorderLayout();
        }else if (typecomp.equals("map")) {
            comp = new MapView();
        }else if (typecomp.equals("webview")) {
            comp = new Webview();   
        }else if (typecomp.equals("file")) {
            comp = new FileUploder();   
        }else if (typecomp.equals("content")) {
            comp = new Content();   
        }else if (typecomp.equals("tablayout")) {
            comp = new TabLayout();  
        }else if (typecomp.equals("text")) {
            comp = new Textsmart();
        }else if (typecomp.equals("txtauto")) {
            comp = new TextAutoComplete();
        }else if (typecomp.equals("navreceiver")) {
            comp = new Receiver();
        }else  if (typecomp.startsWith("accordionmenu")) {
              comp = new Accordion(); 
        }else  if (typecomp.startsWith("document")) {
            comp = new Document();              
        }else  if (typecomp.startsWith("com.")) {
            try {
                String classname = typecomp;
                comp = (Component) Class.forName(classname).newInstance();               
            } catch (Exception e) {    }
        }else  if (typecomp.startsWith("div")) {
            comp = new DivLayout();
        } else{
            comp = new Component();
        }
        
        comp.setType(typecomp);
        return comp != null ? comp:new Component();
    }
}
