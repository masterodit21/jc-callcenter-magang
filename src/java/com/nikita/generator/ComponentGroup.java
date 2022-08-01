/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator;

import com.nikita.generator.ui.layout.NikitaForm;
import com.rkrzmail.nikita.data.Nset;
import java.util.Vector;

/**
 * created by 13k.mail@gmail.com
 */
public class ComponentGroup extends Component{
    protected Vector<Component> components = new Vector<Component>();
    
    public String getView(NikitaViewV3 v3){        
        StringBuilder sbuBuffer = new StringBuilder();
        for (int i = 0; i < components.size(); i++) {
            sbuBuffer.append(components.get(i).getView(v3)) ;
        }
        return sbuBuffer.toString();
    }
    
    public void restoreData(Nset data) {
        for (int j = 0; j < components.size(); j++) {
            components.elementAt(j).restoreData(data);             
        }
        super.restoreData(data);  
    }   
    
    public void addComponent(Component component){
        component.setParent(this);        
        components.addElement(component);
        if (getForm()!=null) {
            component.setForm(getForm());
        }
    }
    
    public Component getComponent(int i){
        return components.elementAt(i);
    }
    
    public int getComponentCount(){
        return components.size();
    }

    public void removeComponent(int i){
        try {
            components.removeElementAt(i);
        } catch (Exception e) {}
    }
    public void setComponentAt(Component comp, int i){
        try {
            components.setElementAt(comp, i);
        } catch (Exception e) {}
    }
    public void removeAllComponents(){
         components.removeAllElements();
    }
    
    @Override
    public void setForm(Component formid) {
        super.setForm(formid);
        for (int i = 0; i < getComponentCount(); i++) {
             getComponent(i).setForm(formid);
        }
    }
    public void setInstanceId(String newinst) {
        super.setInstanceId(newinst);
        for (int i = 0; i < getComponentCount(); i++) {
             getComponent(i).setInstanceId(newinst);
        }
    }
    public void setZindex(String newId) {
        for (int i = 0; i < getComponentCount(); i++) {
            //??
            if (components.elementAt(i) instanceof ComponentGroup) {                
                 ((ComponentGroup)components.elementAt(i)).setZindex(newId);
            }
        }
    }
    public Component findComponentbyName(String name){
        if (getName().equals(name)) {
          return this;
        }
        for (int j = 0; j < components.size(); j++) {
            if (components.elementAt(j).getName().equals(name)) {
                return components.elementAt(j);
            }      
            if (components.elementAt(j) instanceof ComponentGroup) {                
                Component component = ((ComponentGroup)components.elementAt(j)).findComponentbyName(name);
                if (component!=null) {
                    return component;
                }
            }          
        }
        return  null;
    }
    
    public Component findComponentbyId(String id){
        if (getId().equals(id)) {
          return this;
        }
        for (int j = 0; j < components.size(); j++) {
            if (components.elementAt(j).getId().equals(id)) {
                return components.elementAt(j);
            }   
            if (components.elementAt(j) instanceof ComponentGroup) {
                Component component = ((ComponentGroup)components.elementAt(j)).findComponentbyId(id);
                if (component!=null) {
                    return component;
                }
            }              
        }
        return null;
    }
    public Vector<Component> populateComponentbyType(Vector<Component> buffer,String simpleclassname){
        if (buffer == null) {
            buffer = new Vector<Component>();
        }
        for (int j = 0; j < components.size(); j++) {
            if (components.elementAt(j).getClass().getSimpleName().equals(simpleclassname)) {                 
                buffer.addElement(components.elementAt(j));
            }   
            if (components.elementAt(j) instanceof ComponentGroup) {
                ((ComponentGroup)components.elementAt(j)).populateComponentbyType(buffer, simpleclassname);                 
            }              
        }
        return buffer;
    }
    public Component getComponentbyId(String id){
        Component component = findComponentbyId(id);
        if (component!=null) {
            return component;
        }
        return new Component();
    }
    
    public Vector<Component> populateAllComponents(){
        Vector<Component> components = new Vector<Component>() ;
        populateComponents(this, components);
        return components;
    }

    private  void populateComponents(ComponentGroup component, Vector<Component> components){
        for (int i = 0; i < component.getComponentCount(); i++) {
            if (component.getComponent(i) instanceof ComponentGroup) {
                if (components.contains((ComponentGroup)component.getComponent(i))) {					
                }else{
                    components.add(component.getComponent(i));	
                    populateComponents((ComponentGroup)component.getComponent(i), components);
                }				
            }else{
                components.add(component.getComponent(i));
            }
        }
    }


}
