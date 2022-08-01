

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator.action;

import com.nikita.generator.Component;
import com.nikita.generator.NikitaEngine;
import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaServlet;
import com.nikita.generator.ui.SmartGrid;
import com.nikita.generator.ui.Tablegrid;
import com.nikita.generator.ui.Textbox;
import com.nikita.generator.ui.layout.NikitaForm;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import java.util.Vector;

/**
 *
 * @author rkrzmail
 */
public class ComponentAction implements IAction{

    @Override
    public boolean OnAction(Nset currdata, NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        String code = response.getVirtualString(currdata.getData("code").toString());  
        String comp = currdata.getData("args").getData("param1").toString();       
                
        if (code.equals("setvisibleall")||code.equals("setenableall")||code.equals("settextall")) {
            Component component  = response.getContent();	
            setComponent(component, code,  (currdata.getData("args").getData("param1").toString() ) ,  currdata, request, response );               
            return true;
        }
        
        if (code.equals("component") && response.isVirtualComponnet(currdata.getData("args").getData("param2").toString())  ) {
        	Object object = response.getVirtual(currdata.getData("args").getData("param2").toString());
        	Nset nset = Nset.newArray();
        	if (object instanceof Nset) {
        		nset = (Nset)object;
			}else if (String.valueOf(object).startsWith("[") && String.valueOf(object).endsWith("]")) {
				nset = Nset.readJSON(String.valueOf(object));
			}else{
				nset.addData(String.valueOf(object));
			}
            for (int i = 0; i < nset.getSize(); i++) {
        		/*
                 * cann repeat
                 */
                String ncode = response.getVirtualString(currdata.getData("args").getData("param1").toString());
                Component component  = response.getComponent( nset.getData(i).toString() );
                setComponent(component, ncode, (currdata.getData("args").getData("param2").toString() ), currdata, request, response);
            }
        }else if (response.isVirtual(comp)) {
            String data = response.getVirtualString(currdata.getData("args").getData("param2").toString());  
            if (code.equals("settext")) {
                response.setVirtual(comp, data);
            }if (code.equals("setdata")) {
                response.setVirtual(comp, response.getVirtual(currdata.getData("args").getData("param2").toString()) );
            }            
        }else if (code.equals("component")) {           
            Component component  = response.getComponent(currdata.getData("args").getData("param2").toString());
            setComponent(component, response.getVirtualString(currdata.getData("args").getData("param1").toString()) ,(currdata.getData("args").getData("param3").toString()), currdata, request, response);
        }else if (response.isComponent(comp)){
            Component component  = response.getComponent(currdata.getData("args").getData("param1").toString());
            setComponent(component, code, (currdata.getData("args").getData("param2").toString() ), currdata, request, response);
        }
        return true;
    }
    
    private void setComponent(Component component, String code, String data, Nset currdata,final  NikitaRequest request,final NikitaResponse response){
        
        if (code.equals("settext")) {
            component.setText(response.getVirtualString(data));
            
            response.refreshComponent(component);
        }else  if (code.equals("gettype")) {
            response.setVirtual(data, component.getType());
        }else  if (code.equals("settextall")) {
             Object virtual = response.getVirtual(data);
            if (data.trim().endsWith("(autoset)") && virtual instanceof Nset ){
                Nset nset = (Nset) virtual;
                Vector<Component> components = response.getContent().populateAllComponents();
                for (int i = 0; i < components.size(); i++) {
                    component  =  components.elementAt(i);
                    if (nset.containsKey(component.getName())){
                        component.setText(nset.getData(component.getName()).toString());
                        response.refreshComponent(component);
                    }
                }
            }else{
                data = response.getVirtualString(data);
                Vector<Component> components = response.getContent().populateAllComponents();
                for (int i = 0; i < components.size(); i++) {//response.getContent().getComponentCount()
                    component  = components.get(i);// response.getContent().getComponent(i);
                    component.setText(response.getVirtualString(data));
                    response.refreshComponent(component);
                }  
            }
        }else  if (code.equals("setlabel")) {
            component.setLabel(response.getVirtualString(data));
            
            response.refreshComponent(component);
        }else if (code.equals("addmapmarker")) { 
            Nset n;
            String s = currdata.getData("args").getData("param2").toString();
            if (s.startsWith("[")||s.startsWith("{")) {
                n = Nset.readJSON(s);
            }else{
                n =  new Nset ( response.getVirtual(currdata.getData("args").getData("param2").toString()) );
            }  
            response.addMapMarker(component.getJsId(), n);
        }else if (code.equals("setmapmarker")) { 
            Nset n;
            String s = currdata.getData("args").getData("param2").toString();
            if (s.startsWith("[")||s.startsWith("{")) {
                n = Nset.readJSON(s);
            }else{
                n =  new Nset ( response.getVirtual(currdata.getData("args").getData("param2").toString()) );
            }  
            response.setMapMarker(component.getJsId(), n);
        }else if (code.equals("setvisible")) {
            data = response.getVirtualString(data);
            component.setVisible(data.equalsIgnoreCase("true")||data.equals("1")?true:false);
            
            response.refreshComponent(component);
        
        }else  if (code.equals("setvisibleall")) {
            data = response.getVirtualString(data);
            
            Vector<Component> components = response.getContent().populateAllComponents();
            for (int i = 0; i < components.size(); i++) {//response.getContent().getComponentCount()
                component  = components.get(i);// response.getContent().getComponent(i);
                component.setVisible(data.equalsIgnoreCase("true")||data.equals("1")?true:false);
                
                response.refreshComponent(component);
            }  
        }else if (code.equals("setcomment")) {
            data = response.getVirtualString(data);
            component.setComment( data );
            
            response.refreshComponent(component);
        }else if (code.equals("setenable")) {
            data = response.getVirtualString(data);
            component.setEnable(data.equalsIgnoreCase("true")||data.equals("1")?true:false);
            
            response.refreshComponent(component);
        }else  if (code.equals("setenableall")) {
            data = response.getVirtualString(data);
            Vector<Component> components = response.getContent().populateAllComponents();
            for (int i = 0; i < components.size(); i++) {//response.getContent().getComponentCount()
                component  = components.get(i);// response.getContent().getComponent(i);
                component.setEnable(data.equalsIgnoreCase("true")||data.equals("1")?true:false);
                
                response.refreshComponent(component);
            }  
        }else if (code.equals("setmandatory")) { 
            data = response.getVirtualString(data);
            component.setMandatory(data.equalsIgnoreCase("true")||data.equals("1")?true:false);
            response.viewMandatory(component, data.equalsIgnoreCase("true")||data.equals("1")?true:false);
            //response.refreshComponent(component);
        }else if (code.equals("settag")) {
            Object obj = response.getVirtual(data);
            if (obj instanceof Nikitaset) {
                component.setTag(((Nikitaset)obj).toNset().toJSON());  
            }else if (obj instanceof Nset) {
                component.setTag(((Nset)obj).toJSON());   
            }else  if (obj != null) {
                component.setTag( response.getVirtualString(data));
            }else{
                component.setTag("");
            }     
            
            response.refreshComponent(component);
        }else if (code.equals("requestfocus")) {
            if (component instanceof Textbox) {
                response.requestfocus((Textbox)component);
            }  
        }else if (code.equals("resultgrid")) {
            //setcomponet, set action
             
            if (component instanceof SmartGrid) {  
                SmartGrid grid = ((SmartGrid)component);
                Nset smartarg = Nset.readJSON(   response.getVirtualString(currdata.getData("args").getData("param9").toString())  );  
                                 
                grid.setCurrentAction(smartarg.getData("action").toString());             
                if (smartarg.getData("showdetailview").toString().equals("false")) {
                    grid.setCurrentDetail("");//hide
                }
            }
            
        }else if (code.equals("smartgrid")) {
            //add 12/12/2021
            if (component instanceof SmartGrid||component instanceof Tablegrid) {
                if (response.getVirtualString("@+EXPORT-MODE").equalsIgnoreCase("export")) {
                    return;//no neet refresh
                }                             
            }
            
            Object  vData = response.getVirtual (currdata.getData("args").getData("param2").toString());
            Nset smartarg = Nset.readJSON(   response.getVirtualString(currdata.getData("args").getData("param9").toString())  );  
            
            if (vData instanceof Nikitaset && component instanceof SmartGrid) {
                ((SmartGrid)component).setData((Nikitaset)vData);
            }else if (vData instanceof Nset) {
                component.setData((Nset)vData);
            }else{
                 
            }
            if (component instanceof SmartGrid) {     
                SmartGrid grid = ((SmartGrid)component);
                grid.showRowNumber(smartarg.getData("showrownum").toString().equals("true"));
                grid.showAction(smartarg.getData("showaction").toString().equals("true"), smartarg.getData("action").toString());
                grid.showRowCheckbox(smartarg.getData("multiselect").toString().equals("true"), smartarg.getData("multiselectfield").toString());
                grid.sortableGrid(smartarg.getData("sortable").toString().equals("true"));
                
                grid.showRowMaster(!smartarg.getData("showrowmaster").toString().equals("false"));
          
                //new request
                Object obj = response.getVirtual(smartarg.getData("select").toString());
                if (obj instanceof Nset) {
                    grid.setSelectedRow( ((Nset)obj).toJSON() );
                }else{
                    grid.setSelectedRow(String.valueOf(obj));
                }
                
                
                grid.setDefaultHeader(smartarg.getData("header").toString());
                grid.setDefaultDataView(smartarg.getData("data").toString());
                grid.setDefaultDetailView(smartarg.getData("detail").toString());
                grid.setHideHeaderEmpty(true);
                
                grid.setSmartGridTextViewListener(new SmartGrid.SmartGridTextViewListener() {
                    public String onTextView(String comp, int row, int col, String text) {
                        comp = (comp.startsWith("$")?"":"$")+comp;
                        response.setVirtualRegistered("@+GRIDROW", row);
                        response.setVirtualRegistered("@+GRIDCOUNT", row);  
                        response.setVirtualRegistered("@+GRIDCOL", col);
                        Component component = response.getComponent(comp);
                        component.setText(text);
                        NikitaServlet.execLogicComponent(response.getContent(), request, response, response.getNikitaLogic(), component.getId());
                        return component.getText();
                    }
                });
                
                final Component comp = response.getComponent(smartarg.getData("looper").toString());
               
                if (!comp.getId().equals("")) {
                     
                    grid.setSmartGridItemViewListener(new SmartGrid.SmartGridItemViewListener() {
                        public Component[] onItemView(SmartGrid parent, int row, Object data) {
                            response.setVirtualRegistered("@+GRIDROW", row);
                            response.setVirtualRegistered("@+GRIDCOUNT", row);
                            response.setVirtualRegistered("@+GRIDDATA", data);
                            if (data instanceof Nikitaset) {
                                response.setVirtualRegistered("@+GRIDDATANSET", ((Nikitaset)data).toNset() );
                            }else if (data instanceof Nset) {                             
                                response.setVirtualRegistered("@+GRIDDATANSET", (Nset)data);
                            }else{
                                response.setVirtualRegistered("@+GRIDDATANSET", Nset.newObject());
                            }
                                    
                            
                             
                            
                            NikitaServlet.execLogicComponent(response.getContent(), request, response, response.getNikitaLogic(), comp.getId());
                            Component[] components= new Component[0];
                            
                             
                            
                            //parent.setCurrentAction("");
                            return components;
                        }
                    });
                }    
                if (!comp.getId().equals("")||!grid.getDefaultDetailView().equals("")) {    
                    grid.setSmartGridDetailViewListener(new SmartGrid.SmartGridDetailViewListener() {
                        public NikitaForm onDetailView(SmartGrid parent, int row, Object data) {                           
                            //String comp = parent.getCurrentDetail()!=null?(parent.getCurrentDetail().equals("")?parent.getDefaultDetailView():parent.getCurrentDetail()):parent.getDefaultDetailView();
                            String comp =  parent.getCurrentDetail()!=null?parent.getCurrentDetail():"";
                            if (comp.equals("")) {
                                return null; //hide
                            }
                            
                            NikitaResponse nr = response.newInstance();
                            NikitaRequest nq = request.newInstance();       
                                                        
                            
                            response.setVirtualRegistered("@+GRIDROW", row);
                            response.setVirtualRegistered("@+GRIDCOUNT", row);
                            response.setVirtualRegistered("@+GRIDDATA", data);
                            if (data instanceof Nikitaset) {
                                response.setVirtualRegistered("@+GRIDDATANSET", ((Nikitaset)data).toNset() );
                            }else if (data instanceof Nset) {                             
                                response.setVirtualRegistered("@+GRIDDATANSET", (Nset)data);
                            }else{
                                response.setVirtualRegistered("@+GRIDDATANSET", Nset.newObject());
                            }
                            
                          
                            Nset n = Nset.newObject();
                            if (comp.startsWith("{")) {
                                n = Nset.readJSON(comp);
                                comp=n.getData("formid").toString();
                                if (!n.getData("formname").toString().equals("")) {
                                    comp=n.getData("formname").toString();
                                }


                                String[] keys = n.getData("args").getObjectKeys();               
                                for (int i = 0; i < keys.length; i++) {
                                    String vdata = n.getData("args").getData(keys[i]).toString();
                                    if (keys[i].startsWith("[")&& keys[i].endsWith("]")) {
                                        //Difinition Result                     
                                        nq.setParameter(keys[i].substring(1, keys[i].length()-1), vdata );   
                                    }else{

                                        nq.setParameter(keys[i], response.getVirtualString(  vdata ) );   
                                    }                                                 
                                }
                            } else if (comp.equals("")) {
                                return null;
                            }
                         
                            
                            if (response.getVirtualString("@+NIKITAGENERATORSMARTGRIDMASTERDETAIL").equals(comp) && (response.getVirtual("@+NIKITAGENERATORSMARTGRIDMASTERDETAILENGINE") instanceof NikitaEngine) ) {
                                response.runServletGen( comp , nq, nr , response.getNikitaLogic(), (NikitaEngine)response.getVirtual("@+NIKITAGENERATORSMARTGRIDMASTERDETAILENGINE") );
                            }else{
                                response.runServletGen( comp , nq, nr , response.getNikitaLogic());
                                response.setVirtualRegistered("@+NIKITAGENERATORSMARTGRIDMASTERDETAIL", comp);
                                response.setVirtualRegistered("@+NIKITAGENERATORSMARTGRIDMASTERDETAILENGINE", nr.getNikitaEngine());
                            }
                            
                            //response.showWindowsGen(comp, "gridmasterdetail", response.getComponent(currdata.getData("args").getData("param2").toString()).getId() , request, response , response.getNikitaLogic() );
                           
                            nr.getContent().setInstanceId( parent.getJsId()+ "-" + row );
                            nr.getContent().setFormCaller( parent.getFormJsId() );//20160219 sebelumnya salah
                            nr.getContent().setInitLoad(nr.getInitLoadAndClear());//addd for init
                            
                            parent.setCurrentDetail("");//reset
                            if (nr.getVirtualString("@SMARTGRIDDETAILVIEWHIDE").equals("true")) {
                               return null; //hide
                            }
                            return nr.getContent();
                        }
                    });
                }
                       
                
                
                
            }
            response.refreshComponent(component);
        }else if (code.equals("setdata")) {
            //add 12/12/2021
            if (component instanceof SmartGrid||component instanceof Tablegrid) {
                if (response.getVirtualString("@+EXPORT-MODE").equalsIgnoreCase("export")) {
                    return;//no neet refresh
                }                          
            }
            
            //equ = FormAction.inflate
            
            if (component instanceof Tablegrid) {
                if (response.getVirtual(data) instanceof Nikitaset) {
                    ((Tablegrid)component).setData( (Nikitaset)response.getVirtual(data)  );
                }else{
                    component.setData(new Nset(response.getVirtual(data)));
                }
                //header
                if (currdata.getData("args").getData("param3").toString().trim().length()>=1) {         
                    Nset  n  = new Nset(response.getVirtual(currdata.getData("args").getData("param3").toString()));
                    if (response.getVirtual(currdata.getData("args").getData("param3").toString()) instanceof  String) {
                        String s = response.getVirtualString(currdata.getData("args").getData("param3").toString());
                        if((s.startsWith("[")|| s.startsWith("{") ) && !(s.startsWith("[*")|| s.startsWith("[#")|| s.startsWith("[v"))){
                            n = Nset.readJSON(s);
                        } else if(s.contains("|")){
                            n = Nset.readsplitString(s,"|");
                        } else if(s.contains(",")){
                            n = Nset.readsplitString(s,",");
                        }
                    }
                    ((Tablegrid)component).setDataHeader(n);
                     
                    
                 
                    if ( n.getData(0).toString().startsWith("[#]") ) {
                        ((Tablegrid)component).showRowIndex(true);
                        try {
                            ((Vector)n.getInternalObject()).setElementAt(n.getData(0).toString().substring(3), 0);
                        } catch (Exception e) { }
                    }else if ( n.getData(0).toString().startsWith("[*]") ) {
                        ((Tablegrid)component).showRowIndex(true,true);
                        try {
                            ((Vector)n.getInternalObject()).setElementAt(n.getData(0).toString().substring(3), 0);
                        } catch (Exception e) { }
                    }else if ( n.getData(0).toString().startsWith("[v]") ) {
                        ((Tablegrid)component).showRowIndex(false,true);
                        try {
                            ((Vector)n.getInternalObject()).setElementAt(n.getData(0).toString().substring(3), 0);
                        } catch (Exception e) { }
                    }
                    
                    for (int i = 0; i < n.getArraySize(); i++) {
                       ((Tablegrid)component).setColHide(i, n.getData(i).toString().equals(""));
                        if (n.getData(i).toString().contains("[*]")) {
                            ((Tablegrid)component).setRowCounter(true);                       
                        }else if (n.getData(i).toString().equals("[v]")) {
                            ((Tablegrid)component).setColType(i, Tablegrid.TYPE_CHECKBOK);                        
                        }else if (n.getData(i).toString().startsWith("[")&&n.getData(i).toString().endsWith("]") ) {
                            //((Tablegrid)component).setColType(i, Tablegrid.TYPE_CHECKBOK);
                        }
                    }
                    
                }
                
            }else if (component instanceof SmartGrid) {
                if (response.getVirtual(data) instanceof Nikitaset) {
                    ((SmartGrid)component).setData( (Nikitaset)response.getVirtual(data)  );
                }else{
                    component.setData(new Nset(response.getVirtual(data)));
                }   
            }else{                
                if (response.getVirtual(data) instanceof Nikitaset) {
                    component.setData(new Nset( ((Nikitaset)response.getVirtual(data)).getDataAllVector() ));
                }else{
                    component.setData(new Nset(response.getVirtual(data)));
                }            
                
            }
            
            response.refreshComponent(component);
        } else if (code.equals("gettext")) {    
            response.setVirtual(data, component.getText());
         } else if (code.equals("getcomment")) {    
            response.setVirtual(data, component.getComment());
        } else if (code.equals("gettag")) {   
            response.setVirtual(data, component.getTag());    
        } else if (code.equals("getenable")) {            
            response.setVirtual(data, component.isEnable()?"1":"0");
        } else if (code.equals("getvisible")) {            
            response.setVirtual(data, component.isVisible()?"1":"0");    
        } else if (code.equals("getmandatory")) {            
            response.setVirtual(data, component.isVisible()?"1":"0");     
        }    
       
    }  
            
}
