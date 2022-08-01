/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator.action;

import com.nikita.generator.Component;
import com.nikita.generator.ComponentGroup;
import com.nikita.generator.IAdapterListener;
import com.nikita.generator.NikitaEngine;
import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaService;
import com.nikita.generator.NikitaServlet;
import com.nikita.generator.NikitaViewV3;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.mobile.Generator;
import com.nikita.generator.ui.Button;
import com.nikita.generator.ui.Checkbox;
import com.nikita.generator.ui.Combolist;
import com.nikita.generator.ui.ListView;
import com.nikita.generator.ui.Radiobox;
import com.nikita.generator.ui.Tablegrid;
import com.nikita.generator.ui.layout.NikitaForm;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
 
import java.util.Vector;

/**
 *
 * @author rkrzmail
 */
public class FormAction implements IAction{

    @Override
    public boolean OnAction(final Nset currdata, final NikitaRequest request, final NikitaResponse response, final NikitaLogic logic) {
        String code = response.getVirtualString(currdata.getData("code").toString());  
        String comp = (currdata.getData("args").getData("param1").toString());
        String modal = response.getVirtualString(currdata.getData("args").getData("param2").toString());  
        String reqcode = response.getVirtualString(currdata.getData("args").getData("param4").toString());  
        
        
        
        if (code.equals("showform")||code.equals("showfinder")||code.equals("showwindows")||code.equals("showcontent")||code.equals("calllink")||code.equals("calltask")||code.equals("callfunction")) {
            //2001/2017
            Generator.saveOpenForms(response);
            
            //new 19/08/2016 == 2601/2017 web
            String param5 = response.getVirtualString(currdata.getData("args").getData("param5").toString());  
			
            //add for finder 13/02/2015
            if (code.equals("showfinder")) {
                modal="modal";
                //reqcode="FINDER-"+reqcode;
                request.setParameter("fincomp1", currdata.getData("args").getData("finparam1").toString() );
                request.setParameter("fincomp2", currdata.getData("args").getData("finparam2").toString() );
                request.setParameter("fincomp3", currdata.getData("args").getData("finparam3").toString() );
                request.setParameter("fincomp4", currdata.getData("args").getData("finparam4").toString() );
                
                //add 30102015
                for (int i = 5; i < 32; i++) {
                    if (!currdata.getData("args").getData("finparam"+i).toString().equals("")) {
                        request.setParameter("fincomp"+i, currdata.getData("args").getData("finparam"+i).toString() ); 
                    }                                        
                }
                
                
            }//end
            
            //new 20160401
            comp = response.getVirtualString(currdata.getData("args").getData("param1").toString());
            
            Nset n = Nset.newObject();
            int ilog = logic.getCurrentRow();
            if (comp.startsWith("{")) {
                n = Nset.readJSON(comp);
                /*comp=n.getData("formid").toString();
                if (!n.getData("formname").toString().equals("")) {
                    comp=n.getData("formname").toString();
                }
                */
                /*
                update 16/01/2021 tidak mennggunakan idform
                */
                comp=n.getData("formname").toString();
                
                
                String[] keys = n.getData("args").getObjectKeys();               
                for (int i = 0; i < keys.length; i++) {
                    String data = n.getData("args").getData(keys[i]).toString();
                    if (keys[i].startsWith("[")&& keys[i].endsWith("]")) {
                        //Difinition Result                     
                        request.setParameter(keys[i].substring(1, keys[i].length()-1), data );   
                    }else{
                         
                        request.setParameter(keys[i], response.getVirtualString(  data ) );   
                    }                                                 
                }
            }       
            
            if (NikitaService.isModeCloud()) {
                    //String user = response.getVirtualString("@+SESSION-LOGON-USER");                    
                    //String prefix = NikitaService.getPrefixUserCloud(response.getConnection(NikitaConnection.LOGIC), user);
                    String prefix = String.valueOf(NikitaService.sLinkName.get()); 
                    if (comp.equalsIgnoreCase("bhc")||comp.startsWith("dashboard")) {
                        //native all boleh
                    }else if (prefix.equalsIgnoreCase("")|| Utility.isNumeric(prefix)) {
                        
                    }else if (prefix.startsWith("u")) {
                        if (prefix.contains("_")) {
                            if (comp.startsWith(prefix.substring(0, prefix.indexOf("_")+1)) ) {
                                 //ok
                            }else{
                                comp = prefix + comp;//bypasss with prefik
                            }
                        }else{
                            comp = prefix + comp;//bypasss with prefik
                        }
                    } else{
                        comp = prefix + comp;//bypasss with prefik
                    }
            }
            
            String intance = reqcode;
             //19/08/2016
            if (Utility.isNumeric(param5.trim())) {
                comp  = comp + NikitaEngine.NFID_ARRAY_INDEX + param5.trim();
                intance = reqcode  + NikitaEngine.NFID_ARRAY_INDEX + param5.trim();
            }
            
            if (response.getVirtual(currdata.getData("args").getData("param3").toString()) instanceof Nset) {
                Nset arg = (Nset)response.getVirtual(currdata.getData("args").getData("param3").toString());
                String[] keys = arg.getObjectKeys();
                for (int i = 0; i < keys.length; i++) {
                    request.setParameter(keys[i], arg.getData(keys[i]).toString() );                    
                }
            }
            if (code.equals("calllink")||code.equals("calltask")||code.equals("callfunction")) {
                response.runServletGen(comp, request, response , logic );
            }else if (code.equals("showwindows")||code.equals("showcontent")) {                
                response.showContentGen(comp, reqcode, response.getComponent(currdata.getData("args").getData("param2").toString()).getId() , request, response , response.getNikitaLogic() );
            }else{
                response.showformGen(comp, request, reqcode, modal.equals("true")||modal.equals("modal") , intance );
            }
            
            logic.setCurrentRow(ilog);      
        }else if (code.equals("showbusy")) {
            response.showBusy();
        }else if (code.equals("submit")) {            
            response.submit(response.getComponent(comp), modal);
        }else if (code.equals("checkfile")) {            
            response.checkfilesubmit(response.getComponent(comp), modal);
        }else if (code.equals("snapshot")) {            
            String param1 = response.getVirtualString(currdata.getData("args").getData("param1").toString()); 
            String param2 = response.getVirtualString(currdata.getData("args").getData("param2").toString());  
    
            response.setVirtual(currdata.getData("args").getData("result").toString(), response.captureSnapshot(param1, param2));
        }else if (code.equals("location")) {            
            response.getLocation(response.getVirtualString(currdata.getData("args").getData("param1").toString()));
        }else if (code.equals("localdate")) {            
            response.getLocalDate(response.getVirtualString(currdata.getData("args").getData("param1").toString()));
        }else if (code.equals("datetime")) {            
            response.getDateTimeClient(response.getVirtualString(currdata.getData("args").getData("param1").toString()));        
        }else if (code.equals("validation")) {            
            if (response.getContent()!=null) {
                StringBuffer sb = new StringBuffer();
                Vector<Component>  components = response.getContent().populateAllComponents();
                for (int i = 0; i < components.size(); i++) {
                    Component component = components.get(i);
                    if (component.isVisible() && component.isEnable()&& component.isMandatory() && component.getName().length()>=1  ) {
                        if ((component instanceof Tablegrid)||(component instanceof ListView)) {
                         }else if ((component instanceof Button)) {
                            String va = component.getValidation();
                            if (va.length()>=1){
                                boolean mand = false;
                                Vector<String> cv =Utility.splitVector(va, ",");
                                for (int j = 0; j < cv.size(); j++) {                                     
                                    if ( MemaAction.getTextComponentFromAll(response, cv.elementAt(i)).length()==0 ) {
                                        mand = true;
                                        break;
                                    }
                                }
                                if (mand){
                                    sb.append(sb.toString().length()>=1?",":"").append(component.getLabel());
                                }
                            }else if (component.getText().length()==0){
                                    sb.append(sb.toString().length()>=1?",":"").append(component.getLabel());
                            }
                        }else if ((component instanceof ComponentGroup)) {                            
                        }else if ((component instanceof Combolist)||(component instanceof Radiobox)||(component instanceof Checkbox)) {
                             
                        }else if (component.getText().length()==0){
                            sb.append(sb.toString().length()>=1?",":"").append(component.getLabel());
                        }
                    }
                }
                response.setVirtual(currdata.getData("args").getData("result").toString(), sb.toString());
                
            }
        }else if (code.equals("closeform")) {
                String s = response.getVirtualString(currdata.getData("args").getData("param1").toString());
	    	s = getFromNameStream(s);
                if (s.equals("*")||s.equals("[*]")) {
                    response.closeform("*");	
                    response.clearMobileGenerator();
                    response.clearNikitaFormPublic();
	    	}else if (s.equals("")||s.equals("[]")) {    
                    String instance  = "";
                    if (comp.equals("")) {
                        comp=response.getContent().getId();
                        instance=response.getContent().getInstanceId();
                    }
                    response.closeform(comp, instance);
                    response.getMobileGenerator().setData(comp, Nset.newObject());
                }else{
                    //???
                    
                }
        }else if (code.equals("back")) {
            String s = response.getVirtualString(currdata.getData("args").getData("param1").toString());
	    	s = getFromNameStream(s);
            if (s.equals("*")||s.equals("[*]")) {
                    response.backform(response.getContent().getId(), response.getContent().getInstanceId());	                   
	    	}else if (s.equals("")||s.equals("[]")) {   
                    String instance  = "";
                    if (comp.equals("")) {
                        comp=response.getContent().getId();
                        instance=response.getContent().getInstanceId();
                    }
                    response.backform(comp, instance);                   
                }else{
                    //???
                    response.cleardataform(s, "");  
                }
        }else if (code.equals("cleardataform")) {
               String s = response.getVirtualString(currdata.getData("args").getData("param1").toString());
	     	s = getFromNameStream(s);
                if (s.equals("*")||s.equals("[*]")) {
                    response.cleardataform(response.getContent().getId(), response.getContent().getInstanceId());	                   
	    	}else if (s.equals("")||s.equals("[]")) {   
                    String instance  = "";
                    if (comp.equals("")) {
                        comp=response.getContent().getId();
                        instance=response.getContent().getInstanceId();
                    }
                    response.cleardataform(comp, instance);                   
                }else{
                    response.cleardataform(s, "");     
                    
                }
        }else if (code.equals("calllogic")){
            int i = logic.getCurrentRow();
            int l = logic.getLoopCount();
            boolean b =logic.expression;
            
            if (comp.startsWith("$")) {
                //call route
                NikitaService.loopdetect(response);
                if (response.getComponent(comp).getId().equals("")) {
                    //link
                    /*
                    Nikitaset nikitaset = response.getConnection().QueryPage(1, 1, "SELECT * FROM web_component WHERE formid = ? ORDER BY compindex ASC",logic.getFormId());
                    if (nikitaset.getRows()>=1) {
                        new NikitaServlet().execLogic(request, response, response.getNikitaLogic(), nikitaset.getText(0, "compid"));
                    }  
                    */
                    
                }else{
                    NikitaServlet.execLogicComponent(response.getContent(), request, response, logic, response.getComponent(comp).getId());
                    //new NikitaServlet().execLogic(request, response, response.getNikitaLogic(), response.getComponent(comp).getId());
                }
            }else{
                //call action
                NikitaService.loopdetect(response);
                NikitaService.runActionClass(Nset.newObject().setData("class", comp), request, response, logic);
            }
            
            //refill
            logic.expression=b;
            logic.setCurrentRow(i);
            logic.setLoopCount(l);
            response.setVirtualRegistered("@+LOGICCOUNT", logic.getLoopCount());
            response.setVirtualRegistered("@+LOGICCOUNTB1", logic.getLoopCount()+1);            
        }else if (code.equals("reloadbrowser")) {
            //response.showWindows(comp, request,  logic);
            response.reloadBrowser();
        }else if (code.equals("redirect")) {
            response.sendRedirect(comp);
        }else if (code.equals("openwindows")) {
  
            NikitaResponse nr =  response.newInstance();
            NikitaRequest nq = request.newInstance();            
            String fname = response.getVirtualString(currdata.getData("args").getData("param1").toString()) ;
            
            
            if (fname.startsWith("{")) {
                Nset n = Nset.readJSON(fname);
                fname=n.getData("formid").toString();

                if (!n.getData("formname").toString().equals("")) {
                    fname=n.getData("formname").toString();
                }
                
                String[] keys = n.getData("args").getObjectKeys();               
                for (int i = 0; i < keys.length; i++) {
                    nq.setParameter(keys[i], response.getVirtualString(  n.getData("args").getData(keys[i]).toString() ) );                                
                }
            }            
            if (response.getVirtual(currdata.getData("args").getData("param3").toString()) instanceof Nset) {
                Nset arg = (Nset)response.getVirtual(currdata.getData("args").getData("param3").toString());
                String[] keys = arg.getObjectKeys();
                for (int i = 0; i < keys.length; i++) {
                    nq.setParameter(keys[i], arg.getData(keys[i]).toString() );                    
                }
            }    
            if (reqcode.length()>=1) {
                fname=fname+"["+reqcode+"]";
            }
            response.openWindowsGen(fname,   nq, nr , response.getNikitaLogic() );
        }else if (code.equals("populate")) {
                    
        }else if (code.equals("inflate")) {
            //equ = ComponentAction.settedata
            
            final Component component  = response.getComponent(currdata.getData("args").getData("param1").toString());  
            if (component instanceof Tablegrid||component instanceof ListView) {
                if (response.getVirtual(currdata.getData("args").getData("param2").toString()) instanceof Nikitaset) {

                    ((Tablegrid)component).setData( (Nikitaset)response.getVirtual(currdata.getData("args").getData("param2").toString())  );
                }else{
                    component.setData(new Nset(response.getVirtual(currdata.getData("args").getData("param2").toString())));
                }
                Nset header = Nset.newNull();
                if (currdata.getData("args").getData("param4").toString().trim().length()>=1) {    
                    Nset n  = new Nset(response.getVirtual(currdata.getData("args").getData("param4").toString()));
                    if (response.getVirtual(currdata.getData("args").getData("param4").toString()) instanceof  String) {
                        String s = response.getVirtualString(currdata.getData("args").getData("param4").toString());
                        if((s.startsWith("[")|| s.startsWith("{") ) && !(s.startsWith("[*")|| s.startsWith("[#")|| s.startsWith("[v"))){
                            n = Nset.readJSON(s);
                        } else if(s.contains("|")){
                            n = Nset.readsplitString(s,"|");
                        } else if(s.contains(",")){
                            n = Nset.readsplitString(s,",");
                        }
                    }
                    if (n.getArraySize()>=1) {
                        ((Tablegrid)component).setDataHeader(n);
                    }                    
                     
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
                    
                    header = new Nset(((Vector)n.getInternalObject()).clone());
                    for (int i = 0; i < n.getArraySize(); i++) {
                       ((Tablegrid)component).setColHide(i, n.getData(i).toString().equals(""));
                        if (n.getData(i).toString().contains("[*]")) {
                            ((Tablegrid)component).setRowCounter(true);                       
                        }else if (n.getData(i).toString().equals("[v]")) {
                            ((Tablegrid)component).setColType(i, Tablegrid.TYPE_CHECKBOK);
                        }else if (n.getData(i).toString().startsWith("[")&&n.getData(i).toString().endsWith("]") ) {
                            //((Tablegrid)component).setColType(i, Tablegrid.TYPE_CHECKBOK);
                           // ((Vector)n.getInternalObject()).setElementAt("", i);
                        }
                    }     
                    
                }
                
                 
                final String xxxxxxxxxxx = response.getContent().getJsId();
                if (component instanceof ListView) {
                    ((ListView)component).setAdapterListener(new IAdapterListener() {
                        private NikitaForm nf  ;
                        private Nset header;
                        private String id;
                        public IAdapterListener get(String id, Nset header){
                            this.id=id;
                            this.header=header;
                            return this;
                        }
                        public Component getViewItem(int row, int col, Component parent, Nset data) {
                            NikitaResponse nr = response.newInstance();
                                NikitaRequest nq = request.newInstance();                    

                                nq.setParameter("INDEX", row+"");
                                nq.setParameter("ROWDATA", data.getData(row).toJSON());

                                nr.setVirtualRegistered("@+INDEX", row);
                                nr.setVirtualRegistered("@+ROWDATA", data.getData(row).toJSON());
                                nr.setVirtual("@NEW-INSTANCE",xxxxxxxxxxx+row);

                                String fname = response.getVirtualString(currdata.getData("args").getData("param3").toString()) ;
                                if (fname.startsWith("{")) {
                                    Nset n = Nset.readJSON(fname);
                                    fname=n.getData("formid").toString();
                                    if (!n.getData("formname").toString().equals("")) {
                                        fname=n.getData("formname").toString();
                                    }
                                    String[] keys = n.getData("args").getObjectKeys();               
                                    for (int i = 0; i < keys.length; i++) {
                                        nq.setParameter(keys[i], response.getVirtualString(  n.getData("args").getData(keys[i]).toString() ) );                                
                                    }
                                }            
                                if (response.getVirtual(currdata.getData("args").getData("param3").toString()) instanceof Nset) {
                                    Nset arg = (Nset)response.getVirtual(currdata.getData("args").getData("param3").toString());
                                    String[] keys = arg.getObjectKeys();
                                    for (int i = 0; i < keys.length; i++) {
                                        nq.setParameter(keys[i], arg.getData(keys[i]).toString() );                    
                                    }
                                }            

                                
                                if (response.getVirtualString("@+NIKITAGENERATORTABLEGRIDMASTERDETAIL").equals(fname) && (response.getVirtual("@+NIKITAGENERATORTABLEGRIDMASTERDETAILENGINE") instanceof NikitaEngine) ) {
                                    //System.out.println("NIKITAGENERATORTABLEGRIDMASTERDETAIL1");
                                    response.runServletGen( fname , nq, nr , response.getNikitaLogic(), (NikitaEngine)response.getVirtual("@+NIKITAGENERATORTABLEGRIDMASTERDETAILENGINE") );
                                }else{
                                    //System.out.println("NIKITAGENERATORTABLEGRIDMASTERDETAIL2");
                                    response.runServletGen( fname , nq, nr , response.getNikitaLogic());
                                    response.setVirtualRegistered("@+NIKITAGENERATORTABLEGRIDMASTERDETAIL", fname);
                                    response.setVirtualRegistered("@+NIKITAGENERATORTABLEGRIDMASTERDETAILENGINE", nr.getNikitaEngine());
                                }
                                //response.runServletGen( fname , nq, nr , response.getNikitaLogic());
                                
                                
                                //response.initLoadInclude(nr);

                                nf = nr.getContent();
                                if (nf!=null) {
                                    nf.setInstanceId(xxxxxxxxxxx+row);
                                    nf.setFormCaller(xxxxxxxxxxx);
                                }
                                return nf;
                        }                        
                    }.get(xxxxxxxxxxx, header ));
                    
                    response.refreshComponent(component);
                }else{
                    ((Tablegrid)component).setAdapterListener(new IAdapterListener() {
                        private NikitaForm nf  ;
                        private Nset header;
                        private String id;
                        public IAdapterListener get(String id, Nset header){
                            this.id=id;
                            this.header=header;
                            return this;
                        }
                                                
                        public Component getViewItem(int row, int col, Component parent, Nset data) {                     
                            if (col==0 && response.getVirtualString(currdata.getData("args").getData("param3").toString()).trim().length()>=1 ) { 
                                NikitaResponse nr = response.newInstance();
                                NikitaRequest nq = request.newInstance();                    

                                nq.setParameter("INDEX", row+"");
                                nq.setParameter("ROWDATA", data.getData(row).toJSON());

                                nr.setVirtualRegistered("@+INDEX", row);
                                nr.setVirtualRegistered("@+ROWDATA", data.getData(row).toJSON());
                                nr.setVirtual("@NEW-INSTANCE",xxxxxxxxxxx+row);

                                String fname = response.getVirtualString(currdata.getData("args").getData("param3").toString()) ;
                                if (fname.startsWith("{")) {
                                    Nset n = Nset.readJSON(fname);
                                    fname=n.getData("formid").toString();
                                    if (!n.getData("formname").toString().equals("")) {
                                        fname=n.getData("formname").toString();
                                    }
                                    String[] keys = n.getData("args").getObjectKeys();               
                                    for (int i = 0; i < keys.length; i++) {
                                        nq.setParameter(keys[i], response.getVirtualString(  n.getData("args").getData(keys[i]).toString() ) );                                
                                    }
                                }            
                                if (response.getVirtual(currdata.getData("args").getData("param3").toString()) instanceof Nset) {
                                    Nset arg = (Nset)response.getVirtual(currdata.getData("args").getData("param3").toString());
                                    String[] keys = arg.getObjectKeys();
                                    for (int i = 0; i < keys.length; i++) {
                                        nq.setParameter(keys[i], arg.getData(keys[i]).toString() );                    
                                    }
                                }            

                                response.runServletGen( fname , nq, nr , response.getNikitaLogic());
                                //response.initLoadInclude(nr);

                                nf = nr.getContent();
                                if (nf!=null) {
                                    nf.setInstanceId(xxxxxxxxxxx+row);
                                    nf.setFormCaller(xxxxxxxxxxx);
                                }


                                if (response.getContent()!=null) {
                                    /*
                                    response.getContent().addComponent(new Component(){
                                        String view = "";
                                        public Component get(String view ){
                                            this.view=view;
                                            return this;
                                        }
                                        public String getView() {
                                            return view;
                                        }                                
                                    }.get( nf!=null?nf.getViewTag():""));
                                    */
                                    response.getContent().addComponent(new Component(){
                                        Component view ;
                                        public Component get(Component view ){
                                            this.view=view;
                                            return this;
                                        }

                                        @Override
                                        public String getView(NikitaViewV3 v3) {
                                            return (nf!=null?nf.getViewTag(v3):"");  
                                        }
                                                                 
                                    }.get(nf));
                                    
                                }

                            }  

                            if (nf!=null) {
                                if (col>=nf.getComponentCount()) {
                                    return null;
                                }
                                Component cmp = nf.getComponent(col);     
                                if (cmp.isEnable() == false && cmp.isVisible() == false && cmp.getName().equals("")) {                               
                                    return null;
                                }
                                return cmp;
                            }
                            return null;
                        }
                    }.get(xxxxxxxxxxx, header ));
                }
                
                
                response.refreshComponent(component);
            }else{
                int len = 0;
                if (response.getVirtual(currdata.getData("args").getData("param2").toString()) instanceof Nikitaset) {
                    Nikitaset n =  (Nikitaset)response.getVirtual(currdata.getData("args").getData("param2").toString())  ;
                    len = n.getRows();
                }else{
                    Nset n = new Nset(response.getVirtual(currdata.getData("args").getData("param2").toString())) ;
                    len = n.getArraySize()+ n.getObjectKeys().length;
                }
                
                for (int row = 0; row < len; row++) {
                    NikitaResponse nr = response.newInstance();
                    NikitaRequest nq = request.newInstance();      
                    //response.mergerVirtual(nr);
                    
                    nq.setParameter("INDEX", row+"");
                    nq.setParameter("ROWDATA", "{}");

                    nr.setVirtualRegistered("@+INDEX", row);
                    
                    nr.setVirtual("@NEW-INSTANCE",row);
                    if (response.getVirtual(currdata.getData("args").getData("param2").toString()) instanceof Nikitaset) {
                        Nikitaset n =  (Nikitaset)response.getVirtual(currdata.getData("args").getData("param2").toString())  ;
                        nq.setParameter("ROWDATA", new Nset( n.getDataAllVector().elementAt(row)).toJSON());
                        nr.setVirtualRegistered("@+ROWDATA", new Nset( n.getDataAllVector().elementAt(row)).toJSON());
                    }else{
                        Nset n = new Nset(response.getVirtual(currdata.getData("args").getData("param2").toString())) ;
                        nq.setParameter("ROWDATA", n.getData(row).toJSON());
                        nr.setVirtualRegistered("@+ROWDATA", n.getData(row).toJSON());
                    }

                    String fname = response.getVirtualString(currdata.getData("args").getData("param3").toString()) ;
                    if (fname.startsWith("{")) {
                        Nset n = Nset.readJSON(fname);
                        fname=n.getData("formid").toString();
                        if (!n.getData("formname").toString().equals("")) {
                            fname=n.getData("formname").toString();
                        }
                
                        
                        String[] keys = n.getData("args").getObjectKeys();               
                        for (int i = 0; i < keys.length; i++) {
                            nq.setParameter(keys[i], response.getVirtualString(  n.getData("args").getData(keys[i]).toString() ) );                                
                        }
                    }            
                    if (response.getVirtual(currdata.getData("args").getData("param3").toString()) instanceof Nset) {
                        Nset arg = (Nset)response.getVirtual(currdata.getData("args").getData("param3").toString());
                        String[] keys = arg.getObjectKeys();
                        for (int i = 0; i < keys.length; i++) {
                            nq.setParameter(keys[i], arg.getData(keys[i]).toString() );                    
                        }
                    }           
                   
                    
                    response.runServletGen( fname , nq, nr , response.getNikitaLogic()); 
                    nr.mergerVirtual(response);
                }
            }
            
            
            
            
            
        }
        return true;
    }
    private static String getFromNameStream(String fname){
    	if (fname.startsWith("{")) {
            Nset n = Nset.readJSON(fname);
            fname = n.getData("formid").toString();
            if (!n.getData("formname").toString().equals("")) {
            	fname=n.getData("formname").toString();
            }
    	}
        return fname;
    }
    
}
